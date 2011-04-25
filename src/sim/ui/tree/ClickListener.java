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

package sim.ui.tree;

import sim.util.MessageUtils;

/**
 * This class is used to capture the click event of tree nodes in different
 * panels, for example, category definition panel, behavior network definition
 * panel, etc.
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 10:38AM
 * 
 */
public class ClickListener implements AppListener {
	/**
	 * Handle the click event of the tree node. It is the responsibility of the
	 * user to implement the specific handling logics.
	 * 
	 * One of the specific purposes is to construct the property panel to
	 * display the properties of the selected tree node
	 * 
	 */
	public void onApplicationEvent(AppEvent event) {
		if (event == null || !(event instanceof ClickEvent))
			MessageUtils.error(this, "onApplicationEvent",
					"TypeError: the event is not a click event.");
	}

}
