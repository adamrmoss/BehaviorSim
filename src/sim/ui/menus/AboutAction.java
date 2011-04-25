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

import sim.ui.AboutDialog;
import sim.ui.MainFrame;

/**
 * Show about dialog
 * 
 * @author Fasheng Qiu
 */
class AboutAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 92613704115375256L;
	/**
	 * The main window
	 */
	private MainFrame parent;

	/**
	 * Creates the AboutAction.
	 * 
	 */
	AboutAction(MainFrame parent) {
		super("About"/*
					 * ,
					 * GUIUtils.getImageIconForObject(GUIUtils.MENU_ABOUT_IMAGE)
					 */);
		this.parent = parent;
		super
				.putValue(
						Action.SHORT_DESCRIPTION,
						"About BehaviorSim v"
								+ sim.core.ConfigParameters.version);
	}

	/**
	 * Executes the action.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		// Show about dialog
		AboutDialog appWindow = new AboutDialog(parent);
		appWindow.setModal(true);
		appWindow.show();
	}
}
