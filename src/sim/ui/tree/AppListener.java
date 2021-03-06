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

import java.util.EventListener;

/**
 * Interface to be implemented by application event listeners. Based on standard
 * java.util base interface.
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 10:36PM
 * 
 */
public interface AppListener extends EventListener {
	/**
	 * Handle an application event
	 * 
	 * @param event
	 *            The event to respond to
	 */
	void onApplicationEvent(AppEvent event);
}
