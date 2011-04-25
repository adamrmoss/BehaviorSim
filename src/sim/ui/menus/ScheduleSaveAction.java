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

import sim.core.AppEngine;
import sim.ui.AppStatusbar;
import sim.ui.DefineTaskDialog;
import sim.ui.MainFrame;
import sim.util.GUIUtils;

/**
 * Periodically save the application
 * 
 * @author Fasheng Qiu
 */
class ScheduleSaveAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9031043738863246112L;

	/**
	 * The main window
	 */
	private MainFrame parent;

	/**
	 * Is the automatic periodic saving scheduled ?
	 */
	private boolean scheduled = false;

	/**
	 * Creates the ScheduleSaveAction.
	 * 
	 */
	ScheduleSaveAction(MainFrame parent) {
		super("", GUIUtils.getImageIconForObject(GUIUtils.MENU_AUTO_SAVE));
		this.parent = parent;
		super.putValue(Action.SHORT_DESCRIPTION,
				"Schedule Periodical Application Saving");
	}

	public void actionPerformed(ActionEvent e) {

		boolean isSelected = false;
		Object source = e.getSource();
		javax.swing.JCheckBoxMenuItem toolbarMenuItem = (javax.swing.JCheckBoxMenuItem) source;
		if (toolbarMenuItem.isSelected())
			isSelected = true;
		if (isSelected) {
			String dir = null;
			try {
				dir = AppEngine.getInstance().appManager.currentApp.getAppDir();
			} catch (Throwable ee) {
			}
			if (dir == null) {
				sim.util.MessageUtils.displayError("No application exists.");
				return;
			}
			DefineTaskDialog dlg = new DefineTaskDialog(parent);
			dlg.show();
			if (dlg.getScheduledTime() != -1) {
				String time = "Periodical Application Saving is scheduled in every "
						+ dlg.getScheduledTime() + " seconds.";
				long milliseconds = dlg.getScheduledTime() * 1000;
				new java.util.Timer()
						.scheduleAtFixedRate(sim.core.AppEngine.getInstance()
								.getAppTask(), milliseconds, milliseconds);
				AppStatusbar.getInstance().changeMessage(time);
				scheduled = true;
			}
		} else if (scheduled) {
			sim.core.AppEngine.getInstance().getAppTask().cancel();
			sim.core.AppEngine.getInstance().getAppTask().setCanceled(true);
			AppStatusbar.getInstance().changeMessage(
					"Application saving task is canceled.");
			scheduled = false;
		}

	}
};