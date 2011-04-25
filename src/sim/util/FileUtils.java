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

package sim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Copy bytes from the source file to the destination file.
	 * 
	 * @param src
	 *            The source file to copy
	 * @param dest
	 *            The destination file
	 */
	public static void copyFile(String src, String dest) {
		copyFile(src, dest, null);
	}

	/**
	 * Copy the source file to the destination file.
	 * 
	 * @param src
	 *            The source file to copy
	 * @param dest
	 *            The destination file
	 * @param header
	 *            The header information
	 */
	public static void copyFile(String src, String dest, String header) {
		try {

			copyFile(new FileInputStream(src), dest, header);

		} catch (IOException ex) {
			MessageUtils.debug(FileUtils.class, "copyFile", ex);
		}

	}

	/**
	 * Copy the source input stream to the destination file. The input stream
	 * will be closed after the method call.
	 * 
	 * @param src
	 *            The source stream to copy
	 * @param dest
	 *            The destination file
	 * @param header
	 *            The header information
	 */
	public static void copyFile(InputStream src, String dest, String header) {

		try {
			// Use unbuffered streams, because we're going to use a large buffer
			// for this sequential io.
			InputStream input = src;
			FileOutputStream output = new FileOutputStream(dest);

			if (header != null) {
				int headerLength = header.length();
				byte[] headerBytes = new byte[headerLength];
				headerBytes = header.getBytes();
				// header.getBytes(0, headerLength, headerBytes, 0);
				output.write(headerBytes, 0, headerLength);
			}

			int bytesRead;
			byte[] buffer = new byte[32 * 1024];
			while ((bytesRead = input.read(buffer, 0, buffer.length)) > 0)
				output.write(buffer, 0, bytesRead);

			input.close();
			output.close();

		} catch (IOException ex) {
			MessageUtils.debug(FileUtils.class, "copyFile", ex);
		}

	}

	/**
	 * Copy from writer to reader
	 * 
	 * @param w
	 * @param r
	 */
	public static void copy(OutputStreamWriter w, InputStreamReader r) {

	}

}
