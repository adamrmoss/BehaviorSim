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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import sim.core.AppEngine;
import sim.ui.MainApplet;
import sim.util.FileFilterUtils;
import sim.util.GUIUtils;
import sim.util.MessageUtils;

/**
 * Saves the currently open application to another file
 * 
 * @author Fasheng Qiu
 */
class SaveAsAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6222294352122330932L;

	/**
	 * Creates the SaveAsAction.
	 */
	public SaveAsAction(MainApplet parent) {
		super("Save As", GUIUtils
				.getImageIconForObject(GUIUtils.MENU_SAVEAS_IMAGE));
		super.putValue(Action.SHORT_DESCRIPTION,
				"Save the current application to an external file");
	}

	/**
	 * Executes the SaveAsAction.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		saveAsFile();
	}

	/**
	 * Save the current configuration into the external file
	 * 
	 * @param path
	 *            The full path of the configuration file
	 */
	private void saveAsFile() {

		String dir = null;
		try {
			dir = AppEngine.getInstance().appManager.currentApp.getAppDir();
		} catch (Exception ee) {
		}
		if (dir == null)
			return;
		JFileChooser fileChooser = new JFileChooser();
		if (dir != null)
			fileChooser.setCurrentDirectory(new File(dir));
		fileChooser.setFileFilter(new FileFilterUtils(new String[] { "xml" },
				true, "XML File (*.xml)"));
		fileChooser.setDialogTitle("Save As");
		int result = fileChooser.showSaveDialog(sim.ui.MainFrame.getInstance());
		if (result == JFileChooser.APPROVE_OPTION) {
			String path = fileChooser.getSelectedFile().getPath();
			// Add a suffix if necessary
			if (!path.trim().toLowerCase().endsWith(".xml")) {
				path = path + ".xml";
			}
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
					.displayNormal("The application is saved. You are still working on the original version.");
			// Set the application file full path
			// AppEngine.getInstance().getConfigParameters().setAppFile(path);
			return;
		}

	}
}
