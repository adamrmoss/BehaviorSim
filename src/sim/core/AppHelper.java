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

package sim.core;

import sim.util.MessageUtils;

public class AppHelper {

	/** Constructor, used internally */
	AppHelper() {
	}

	/**
	 * Save the whole application into the chosen external file.
	 * 
	 * <p>
	 * In this method, the whole information will be saved.
	 * </p>
	 * 
	 * <p>
	 * The actual save process is finished by the method call in
	 * <code>XMLUtils</code>
	 * </p>
	 * 
	 * @param path
	 *            The external file the app will be saved to
	 * @return Whether it is successful
	 */
	public boolean saveAppAsFile(String path) {
		try {
			return AppLoader.appToXML(path);
		} catch (Exception e) {
			MessageUtils.debug(this, "saveAppAsFile", e);
			MessageUtils.displayError("Can not save the application.");
		}
		return false;
	}

	/**
	 * Preload some application definition, including application name and
	 * whether the app is based on behavior network.
	 * 
	 * @param externalFile
	 *            The external file the definition loaded from
	 * @return Application name
	 * @throws Exception
	 *             Any exception happens
	 */
	public String preloadAppDef(String externalFile) {
		try {
			return AppLoader.preloadAppDef(externalFile);
		} catch (Exception e) {
			MessageUtils.debug(this, "preloadAppDef", e);
			MessageUtils.displayError("Can not preload the app definition.");
		}
		return null;
	}

	/**
	 * Load the whole application from the chosen external file.
	 * 
	 * <p>
	 * In this method, the whole information will be loaded.
	 * </p>
	 * 
	 * <p>
	 * The actual load process is finished by the method call in
	 * <code>XMLUtils</code>
	 * </p>
	 * 
	 * @param path
	 *            The external file the app will be loaded from
	 * @return Whether it is successful
	 */
	public void loadAppFromFile(String path) throws Exception {
		AppLoader.loadAppFromFile(path);
	}
}
