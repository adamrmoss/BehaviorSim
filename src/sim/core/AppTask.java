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

import java.io.File;
import java.util.TimerTask;

import sim.util.MessageUtils;

/**
 * Application periodic saving task.
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * @since 03/07/2009
 */
public class AppTask extends TimerTask {

	/**
	 * Is the task canceled?
	 */
	private boolean canceled = false;

	/**
	 * Constructor
	 */
	public AppTask() {

	}

	/**
	 * Task to run
	 */
	public synchronized void run() {
		// Execute the application saving task
		saveFile();
		// Execute the recent files saving
		saveRecentFiles();
	}

	/**
	 * Save the changed recent file lists
	 */
	protected void saveRecentFiles() {
		sim.ui.menus.MainMenuBar.RecentFilesHandler handler = sim.ui.menus.MainMenuBar
				.getInstance(null, null).getRecentFilesHandler();
		if (handler.isDirty())
			// Save the recent files
			handler.updateProperties();
	}

	/**
	 * Save the current configuration
	 */
	protected void saveFile() {
		boolean isDirty = false;
		try {
			sim.core.AppManager appManager = AppEngine.getInstance().appManager;
			isDirty = appManager.currentApp.isDirty();
		} catch (Throwable ee) {
		}
		// Current application directory
		String dir = null;
		try {
			dir = AppEngine.getInstance().appManager.currentApp.getAppDir();
		} catch (Exception ee) {
		}
		// The application does not exist, nothing happens
		if (dir == null || !isDirty) {
			return;
		} else {
			saveAsFile0(dir
					+ File.separator
					+ AppEngine.getInstance().appManager.currentApp
							.getAppFileName());
		}
	}

	/**
	 * Save the current configuration into the external file
	 * 
	 * @param path
	 *            The full path of the configuration file
	 */
	private void saveAsFile0(String path) {
		if (AppEngine.getInstance().saveAppAsFile(path)) {
			MessageUtils
					.displayNormal("The application is saved successfully.");
			AppEngine.getInstance().setAppStatus(sim.core.App.CLEAN);
			return;
		}
	}

	/**
	 * @return the canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * @param canceled
	 *            the canceled to set
	 */
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

};