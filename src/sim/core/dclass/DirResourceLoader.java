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
import java.io.FileInputStream;
import java.io.InputStream;

import sim.util.SimException;

/**
 * A loader for the entries in the given directory.
 * 
 * <p>
 * The entries are returned as an <code>InputStream</code>, which can be used to
 * construct images and/or classes.
 * </p>
 * 
 * <p>
 * This class is intentionally used in the developed applet. And also only one
 * directory is support, which contains all classes used in the project.
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class DirResourceLoader extends ResourceLoader {

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The full path from which resources are loaded
	 * @throws Exception
	 *             When the given path is not correct
	 */
	public DirResourceLoader(String path) throws Exception {

		// Call parent constructor
		super(path);

		// If the path does not exist, an exception occurs
		if (!new File(path).exists())
			throw new SimException("DIR-NOT-FOUND", "The path '" + path
					+ "' does not exist", null);

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

		// Get the entry full path
		File entry = new File(this.resourcePath, entryName);

		// Throw a exception if the entry does not exist
		if (!entry.exists())
			throw new SimException("ENTRY-NOT-FOUND", "The entry '" + entry
					+ "' does not exist.", null);

		// Return the entry as an input stream
		return new FileInputStream(entry);

	}

}
