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

import sim.help.HelpPanel;
import sim.ui.MainFrame;
import sim.util.GUIUtils;

/**
 * Show help dialog
 * 
 * @author Fasheng Qiu
 */
class HelpAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7001114000567923824L;


	/**
	 * Creates the AboutAction.
	 * 
	 */
	HelpAction(MainFrame parent) {
		super("Help Center", GUIUtils
				.getImageIconForObject(GUIUtils.MENU_HELP_IMAGE));

		super.putValue(Action.SHORT_DESCRIPTION,
				"Get system help documentation");
	}

	/**
	 * Executes the action.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		// Show the help panel
		sim.help.HelpFrame pd = sim.help.HelpFrame.getInstance(sim.ui.MainFrame
				.getInstance(), new HelpPanel());
		pd
				.setTitle("Welcome to BehaviorSim v"
						+ sim.core.ConfigParameters.version
						+ " help center");
		pd.setSize(pd.getSize().width + 150, pd.getSize().height);
		pd.setResizable(true);
		pd.show();
	}
}
