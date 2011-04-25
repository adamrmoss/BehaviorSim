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

/**
 * Mechanism with no dynamics. It is the initial action selection mechanism for
 * each entity.
 * 
 * @author Fasheng Qiu
 */
public class NoDynamicsMechanism implements IMechanism {

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
	public void execute() {

	}

	/**
	 * Return a copy of this mechanism
	 */
	public IMechanism copy() {
		return this;
	}

	// time
	public int getTime() {
		throw new RuntimeException("NO SUPPORTED");
	}

	public void setTime(int timetick) {
	}
}
