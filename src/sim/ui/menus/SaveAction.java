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

import sim.ui.MainApplet;
import sim.util.GUIUtils;

/**
 * Saves the currently open application
 * 
 * @author Fasheng Qiu
 */
class SaveAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4110959898031553856L;

	/**
	 * Creates the SaveAsAction.
	 */
	public SaveAction(MainApplet parent) {
		super("Save", GUIUtils.getImageIconForObject(GUIUtils.MENU_SAVE_IMAGE));
		super
				.putValue(Action.SHORT_DESCRIPTION,
						"Save the current application");
	}

	/**
	 * Executes the SaveAsAction.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		new sim.core.AppTask().run();
	}
}
