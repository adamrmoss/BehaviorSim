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

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A loader for the entries in the given jar file.
 * 
 * <p>
 * The entries are returned as an <code>InputStream</code>, which can be used to
 * construct images and/or classes.
 * </p>
 * 
 * <p>
 * This class is intentionally used in the developed applet. And also only one
 * jar file is support, which contains all classes used in the project.
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class JarResourceLoader extends ResourceLoader {

	/* The only jar file used in the project */
	private JarFile jarFile = null;

	/**
	 * Constructor
	 * 
	 * @param jarFilePath
	 *            The full path of this jar file
	 * @throws Exception
	 *             When the given jar path is not correct
	 */
	public JarResourceLoader(String jarFilePath) throws Exception {
		super(jarFilePath);
		jarFile = new JarFile(new File(jarFilePath));
	}

	/**
	 * Return the wrapped jar file
	 * 
	 * @return the wrapped jar file
	 */
	public JarFile getJarFile() {
		return this.jarFile;
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
	public InputStream getEntryAsInputStream(String entryName) throws Exception {
		JarEntry centry = jarFile.getJarEntry(entryName.replace('\\', '/'));
		return jarFile.getInputStream(centry);
	}
}
