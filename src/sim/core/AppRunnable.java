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

package sim.core;

/**
 * Basic interface for all threads involved in the application.
 * 
 * This interface mainly used to specify common rutines for all the
 * implementation classes.
 * 
 * 
 * 
 * 
 * @author Fasheng Qiu
 */
public interface AppRunnable extends Runnable {

	/**
	 * Specify whether the thread should be suspended.
	 * 
	 * The implementation should specify how the thread is suspened.
	 * 
	 * Also, the implementation can issue suspend event to other components,
	 * such as other appviews.
	 * 
	 * @param suspended
	 *            Whether the thread needed to suspend
	 */
	public void setSuspended(boolean suspended);

	/**
	 * Set the time interval between two steps
	 * 
	 * @param deltaT
	 *            The interval
	 */
	public void setDeltaT(int deltaT);

}
