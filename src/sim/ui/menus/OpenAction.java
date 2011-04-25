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

package sim.ui.menus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import sim.core.AppEngine;
import sim.ui.DefineAppWindow;
import sim.ui.MainApplet;
import sim.ui.MainFrame;
import sim.util.GUIUtils;

/**
 * Open an existing application.
 * 
 * @author Fasheng Qiu
 */
class OpenAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9087775591708010689L;
	/**
	 * The main window
	 */
	private MainFrame parent;
	private MainApplet main;

	/**
	 * Constructor.
	 * 
	 */
	OpenAction(MainFrame parent, MainApplet main) {
		super("Open", GUIUtils.getImageIconForObject(GUIUtils.MENU_OPEN_IMAGE));
		this.parent = parent;
		this.main = main;
		super.putValue(Action.SHORT_DESCRIPTION,
				"Open an existing simulation application");
	}

	/**
	 * Executes the action.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		boolean isDirty = false;
		try {
			sim.core.AppManager appManager = AppEngine.getInstance().appManager;
			isDirty = appManager.currentApp.isDirty();
		} catch (Throwable ee) {
		}
		String dir = null;
		try {
			dir = AppEngine.getInstance().appManager.currentApp.getAppDir();
		} catch (Throwable ee) {
		}
		// Check unchanged
		if (dir != null) {
			if (isDirty) {
				int response = JOptionPane
						.showConfirmDialog(
								parent,
								"Unsaved changes will be lost.\nDo you want to proceed?",
								"Warning", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.NO_OPTION) {
					return;
				}
			}
			int response = JOptionPane
					.showConfirmDialog(
							parent,
							"Current application will be closed.\nDo you want to proceed?",
							"Warning", JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION) {
				return;
			}
			// Close the current active application
			try {
				AppEngine.getInstance().appManager.destroyApp(AppEngine
						.getInstance().appManager.currentApp);
			} catch (Exception ex) {
			}
		}
		// Clear the current application first
		main.clearAll();
		// Show the application definition dialog to define new application
		DefineAppWindow appWindow = new DefineAppWindow(parent,
				MainApplet.engine);
		appWindow.disableTab(0);
		appWindow.show();
	}
}
