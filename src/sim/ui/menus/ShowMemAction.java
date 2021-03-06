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
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import sim.ui.MainFrame;

/**
 * Show memory usage dialog
 * 
 * @author Fasheng Qiu
 */
class ShowMemAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8340898382317809340L;
	/**
	 * The main window
	 */
	private MainFrame parent;

	/**
	 * Creates the ShowMemAction.
	 * 
	 */
	ShowMemAction(MainFrame parent) {
		super("Show Memory Usage");
		this.parent = parent;
		super.putValue(Action.SHORT_DESCRIPTION, "Show Memory Usage");
	}

	public void actionPerformed(ActionEvent e) {
		Runtime rt = Runtime.getRuntime();
		long used = rt.freeMemory();
		long allocated = rt.totalMemory();
		String msg = (allocated / 1024) + "K allocated, " + (used / 1024)
				+ "K used.";
		JOptionPane pane = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE);
		JDialog dialog = pane.createDialog(parent, "Memory Usage Information");
		dialog.show();
	}
};