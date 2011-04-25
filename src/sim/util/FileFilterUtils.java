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

import javax.swing.filechooser.FileFilter;

/**
 * An implementation of a general purpose FileFilter.
 * <p/>
 * This can be used to only show a certain type of files. Note that this
 * implementation only takes file endings into account.
 * 
 * @author Fasheng Qiu
 */
public class FileFilterUtils extends FileFilter {
	private String[] endings;
	private boolean showDirectories;
	private String description;

	/**
	 * Constructs a new FileFilterGeneral.
	 * 
	 * @param endings
	 *            the file endings to allow
	 * @param showDirectories
	 *            whether or not directories are shown
	 * @param description
	 *            the description of the file types
	 */
	public FileFilterUtils(String[] endings, boolean showDirectories,
			String description) {
		this.endings = endings;
		this.showDirectories = showDirectories;
		this.description = description;
	}

	/**
	 * Returns whether or not to accept the passed file in the selection.
	 * 
	 * @param file
	 *            the File to test
	 * @return true if this is a selectable file, false otherwise
	 */
	public boolean accept(File file) {
		if (file.isDirectory() && showDirectories) {
			return true;
		}

		if (file.isDirectory() && !showDirectories) {
			return false;
		}

		if (endings.length == 0) {
			return true;
		}

		for (int i = 0; i < endings.length; i++) {
			String ending = endings[i];
			if (file.getName().toLowerCase().endsWith(ending.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the description used in this FileFilter.
	 * 
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	}
}
