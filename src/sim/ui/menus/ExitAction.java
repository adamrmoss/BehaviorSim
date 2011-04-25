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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import sim.core.AppEngine;
import sim.ui.MainApplet;
import sim.util.FileFilterUtils;
import sim.util.GUIUtils;
import sim.util.MessageUtils;

/**
 * Quits the system.
 * 
 */
class ExitAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3871692025107083063L;

	/**
	 * Creates the ExitAction.
	 */
	public ExitAction(MainApplet parent) {
		super("Exit", GUIUtils.getImageIconForObject(GUIUtils.MENU_EXIT_IMAGE));

		super.putValue(Action.SHORT_DESCRIPTION, "Exit BehaviorSim");
	}

	/**
	 * Executes the ExitAction. Ask for saving configuration information if
	 * there is an active application.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		// Check active application
		boolean isDirty = false;
		try {
			isDirty = AppEngine.getInstance().appManager.currentApp.isDirty();
		} catch (Exception ee) {
		}
		String dir = null;
		try {
			dir = AppEngine.getInstance().appManager.currentApp.getAppDir();
		} catch (Exception ee) {
		}
		// Check unchanged
		if (dir != null && isDirty) {
			if (MessageUtils
					.displayConfirm("Do you want to save configurations?") == JOptionPane.OK_OPTION) {
				saveAsFile();
			}
		}
		// Save the recent files
		sim.ui.menus.MainMenuBar.getInstance(null, null)
				.getRecentFilesHandler().updateProperties();
		System.exit(0);
	}

	/**
	 * Save the current configuration into the external file
	 * 
	 * @param path
	 *            The full path of the configuration file
	 */
	private void saveAsFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilterUtils(new String[] { "xml" },
				true, "XML File (*.xml)"));
		int result = fileChooser.showSaveDialog(sim.ui.MainFrame.getInstance());
		// if we selected an image, load the image
		if (result == JFileChooser.APPROVE_OPTION) {
			String path = fileChooser.getSelectedFile().getPath();
			saveAsFile0(path);
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
			return;
		}
	}
}
