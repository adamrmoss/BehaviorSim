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

package sim.model.mechanism;

import sim.model.behavior.Timed;

/**
 * An interface for the action selection mechanism. A single method is needed to
 * be implemented by each specific mechanism. <code>execute</code> is used to
 * perform one action of the current computing entity.
 * 
 * 
 * 
 * @author Fasheng Qiu
 * @since 10/25/2007
 * 
 */
public interface IMechanism extends Timed {
	/**
	 * Select action(s) to perform, based on specific mechanism.
	 * 
	 * Essentially, the effect of action execution is that, the state of the
	 * current computing entity will be changed.
	 * 
	 * A mechanism can select a behavior of the entity to dominate the entity
	 * and the state of the entity is decided by that behavior.
	 * 
	 * Also, the state of the entity may be the combination of that of several
	 * behaviors, while the weight of the effect caused by each behavior may be
	 * different.
	 */
	void execute();

	/**
	 * Return a copy of this mechanism
	 */
	IMechanism copy();
}
