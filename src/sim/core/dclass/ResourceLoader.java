/*
 * BehaviorSim - version 1.0 
 * 
 * Copyright (C) 2010 The BehaviorSim Development Team, fasheng@cs.gsu.edu.
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 * Info, Questions, Suggestions & Bugs Report to fasheng@cs.gsu.edu.
 *  
 */

package sim.core.dclass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sim.util.MessageUtils;

/**
 * Resource loader.
 * 
 * <br>
 * A generic resource loader which is used to load either classes or other
 * resources through different class loaders.
 * 
 * @author Fasheng Qiu
 * 
 */
public abstract class ResourceLoader {

	// The path of the resource (a full-path jar file or a full-path directory)
	protected String resourcePath = null;

	/**
	 * @return the resourcePath
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * Constructor
	 * 
	 * @param rp
	 *            The resource path
	 */
	public ResourceLoader(String rp) {
		resourcePath = rp;
	}

	/**
	 * Load a given resource.
	 * <p>
	 * This method will try to load the resource using the following methods (in
	 * order):
	 * <ul>
	 * <li>From {@link Thread#getContextClassLoader()
	 * Thread.currentThread().getContextClassLoader()}
	 * <li>From {@link Class#getClassLoader()
	 * ClassLoaderUtil.class.getClassLoader()}
	 * <li>From the {@link Class#getClassLoader() callingClass.getClassLoader()
	 * * }
	 * </ul>
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 */
	public static URL getResource(String resourceName, Class callingClass) {
		if (resourceName == null)
			return null;

		URL url = null;

		url = Thread.currentThread().getContextClassLoader().getResource(
				resourceName);

		if (url == null) {
			url = ResourceLoader.class.getClassLoader().getResource(
					resourceName);
		}
		if (url == null) {
			url = ResourceLoader.class.getResource(resourceName);
		}
		if (url == null) {
			url = callingClass.getClassLoader().getResource(resourceName);
		}
		if (url == null) {
			url = callingClass.getResource(resourceName);
		}
		if (url == null) {
			try {
				url = new File(resourceName).toURI().toURL();
			} catch (Exception e) {
				url = null;
			}
		}
		return url;
	}

	/**
	 * This is a convenience method to load a resource as a stream.
	 * 
	 * The algorithm used to find the resource is given in getResource()
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 */
	public static InputStream getResourceAsStream(String resourceName,
			Class callingClass) {
		URL url = getResource(resourceName, callingClass);
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Prints the current classloader hierarchy - useful for debugging.
	 */
	public static void printClassLoader() {
		System.out.println("ClassLoaderUtils.printClassLoader");
		printClassLoader(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Prints the classloader hierarchy from a given classloader - useful for
	 * debugging.
	 */
	public static void printClassLoader(ClassLoader cl) {
		System.out
				.println("ClassLoaderUtils.printClassLoader(cl = " + cl + ")");
		if (cl != null) {
			printClassLoader(cl.getParent());
		}
	}

	/**
	 * Read the input stream and get bytes from it
	 * 
	 * @param in
	 * @param size
	 * @return
	 * @throws IOException
	 */
	protected static byte[] readStream(InputStream in, int size)
			throws IOException {
		if (in == null)
			return null;
		if (size == 0)
			return new byte[0];
		int currentTotal = 0;
		int bytesRead;
		byte[] data = new byte[size];
		while (currentTotal < data.length
				&& (bytesRead = in.read(data, currentTotal, data.length
						- currentTotal)) >= 0)
			currentTotal += bytesRead;
		in.close();
		return data;
	}

	/**
	 * Return the image from this jar file
	 * 
	 * @param imageName
	 *            The image name. ex, 'sim/ui/image/a.gif'
	 * @return The bytes of the image
	 */
	public byte[] getImage(String imageName) {

		try {
			if (imageName.startsWith("/"))
				imageName = imageName.substring(1);
			return getBytes(getEntryAsInputStream(imageName));
		} catch (Exception e) {
			MessageUtils.debug(this, "getImage", e);
			return null;
		}

	}

	/**
	 * Read the bytes array from the given input stream.
	 * 
	 * <p>
	 * When the array can not be read correctly, <code>
  	 * null</code>will be returned.
	 * </p>
	 * 
	 * <p>
	 * Please note that THE GIVEN INPUT STREAM WILL BE CLOSED BEFORE THE METHOD
	 * RETURNS.
	 * </p>
	 * 
	 * @param is
	 *            The input stream the bytes read from
	 * @return The read bytes array
	 * @throws if exception occurs
	 */
	protected static byte[] getBytes(InputStream fis) throws Exception {

		// Output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Temp array
		byte[] bytes = new byte[10000];
		int ln = 0;
		while ((ln = fis.read(bytes)) > 0) {
			baos.write(bytes, 0, ln);
		}
		baos.flush();

		// Close streams
		if (baos != null) {
			baos.close();
		}
		if (fis != null) {
			fis.close();
		}

		return baos.toByteArray();

	}

	/**
	 * Return the given entry as an input stream.
	 * 
	 * @param entryName
	 *            The name of the entry to return
	 * @return The input stream of the entry
	 * @throws Exception
	 *             When the entry can not be read correctly
	 */
	public abstract InputStream getEntryAsInputStream(String entryName)
			throws Exception;

}