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
import sim.ui.panels.CategoryDefinePanel;
import sim.ui.panels.PanelDialog;
import sim.util.GUIUtils;
import sim.util.MessageUtils;

/**
 * Define a new category in the system.
 * 
 * @author Fasheng Qiu
 * 
 */
class DefineCategoryAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2471681943070831864L;

	/**
	 * Creates the ExitAction.
	 */
	public DefineCategoryAction() {
		super("Define Category", GUIUtils
				.getImageIconForObject(GUIUtils.MENU_CATEGORY_IMAGE));
		super.putValue(Action.SHORT_DESCRIPTION, "Create or modify categories");
	}

	/**
	 * Executes the action.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		defineNewCategory();
	}

	/**
	 * Show the window where the new category is created.
	 */
	private void defineNewCategory() {
		String dir = null;
		try {
			dir = AppEngine.getInstance().appManager.currentApp.getAppDir();
		} catch (Exception ee) {
		}
		if (dir == null) {
			MessageUtils
					.displayWarning("Please create or open an application ahead.");
			return;
		}
		PanelDialog dialog = new PanelDialog(sim.ui.MainFrame.getInstance(),
				new CategoryDefinePanel());
		dialog.setTitle("Category Definition");
		dialog.setModal(true);
		dialog.show();
	}
}
