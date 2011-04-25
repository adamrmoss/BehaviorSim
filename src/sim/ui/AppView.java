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

package sim.ui;

import javax.swing.Icon;
import javax.swing.JPanel;

import sim.util.ThreadUtils;

/**
 * Application view. Some sub-classes of the Application view are
 * <code>EditorView</code>, <code>SimulationView</code> and
 * <code>BehaviorView</code> etc.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class AppView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6660243242638179117L;

	/**
	 * 
	 * @return The title of this view
	 */
	public String getTitle() {
		return "";
	}

	/**
	 * 
	 * @return The icon of this view
	 */
	public Icon getViewIcon() {
		return null;
	}

	/**
	 * Add a created behavior into the behavior network.
	 * 
	 * @param id
	 *            The id of the behavior to add
	 */
	public void behaviorDefined(int id) {
	}

	/**
	 * Refresh the simulation. Refresh the simulation panel
	 */
	public void refreshSimulation() {
	}

	/**
	 * Clear the system editor
	 */
	public void clearAll() {
	}

	/**
	 * Remove the specified category
	 */
	public void clear(String categoryName) {
	}

	/**
	 * Load all information for this view
	 */
	public void loadAll() {
	}

	/**
	 * Refresh the view. Stop instantiated threads by default.
	 */
	public void refresh() {
		ThreadUtils.suspendThread(new String[] { "COMPUTATIONTHREAD",
				"SIMULATIONTHREAD", "ANALYSISTHREAD" });
	}
}
