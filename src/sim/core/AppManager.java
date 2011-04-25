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

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for all user-defined applications. It maintains the whole life-cycle
 * of an application.
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * @since 03/07/2009
 */
public class AppManager {

	/** Current active application */
	public App currentApp;

	/** All instantiated applications */
	private Map apps;

	/** The only instance */
	private static AppManager instance;

	/**
	 * Private constructor
	 */
	private AppManager() {
		apps = new HashMap();
	}

	/**
	 * Return the only instance
	 * 
	 * @return The only instance
	 */
	public static AppManager getInstance() {
		if (instance == null)
			instance = new AppManager();
		return instance;
	}

	/**
	 * Create a new application instance. The fields of the new application are
	 * populated with default values.
	 * 
	 * @return The new application
	 */
	public App newApp() {
		App newapp = new App();
		apps.put(new Long(newapp.getAppId()), newapp);
		return newapp;
	}

	/**
	 * Initialize the specified application with population of application
	 * various information.
	 * 
	 * @param app
	 *            Application to initialize
	 * @throws Exception
	 */
	public void initApp(App app) throws Exception {
		app.init();
	}

	/**
	 * Destroy the specified application
	 * 
	 * @param app
	 *            The application to be destroyed.
	 */
	public void destroyApp(App app) {
		app.destroy();
		apps.remove(new Long(app.getAppId()));
	}

	/**
	 * Return the current active application
	 * 
	 * @return the currentApp The current active application
	 */
	public App getCurrentApp() {
		return currentApp;
	}

	/**
	 * Set the current active application
	 * 
	 * @param currentApp
	 *            the currentApp to set
	 */
	public void setCurrentApp(App currentApp) {
		this.currentApp = currentApp;
	}

}
