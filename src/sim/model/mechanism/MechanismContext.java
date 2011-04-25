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
 * The context which wraps a specific action selection mechanism. Each entity,
 * if it is behavior network based, will have a context of specific action
 * mechanism.
 * 
 * <p>
 * In this context, the specific action selection mechanism can be changed in
 * the runtime.
 * </p>
 * 
 * @author Fasheng Qiu
 * @since 10/25/1007
 * 
 */
public class MechanismContext implements Timed {

	/**
	 * Concrete action mechanism used in current runtime
	 */
	private IMechanism concreteMechanism = null;

	/**
	 * Constructor
	 */
	public MechanismContext() {
		this(null);
	}

	/**
	 * Constructor
	 * 
	 * @param cm
	 *            The current mechanism
	 */
	public MechanismContext(IMechanism cm) {
		this.concreteMechanism = cm;
	}

	/**
	 * Execute the mechanism if it is configured
	 */
	public void performAction() {
		if (concreteMechanism != null)
			concreteMechanism.execute();
	}

	/**
	 * Return the configured mechanism
	 * 
	 * @return
	 */
	public IMechanism getConcreteMechanism() {
		return concreteMechanism;
	}

	/**
	 * Change to anther mechanism
	 * 
	 * @param concreteMechanism
	 *            Another mechanism to change to
	 */
	public void setConcreteMechanism(IMechanism concreteMechanism) {
		this.concreteMechanism = concreteMechanism;
	}

	/**
	 * Return the current time step of the current computing entity. Should not
	 * be called because the time step is wrapped around. It is not the global
	 * time step. Use {@link #getTime()()} of
	 * <code>sim.configure.model.Entity</code> instead.
	 */
	public int getTime() {
		// if (this.concreteMechanism != null)
		// return this.concreteMechanism.getTime();
		throw new RuntimeException(
				"Can not probe the time step in the action mechanism.");
	}

	/**
	 * Set the current time step of the current computing entity
	 */
	public void setTime(int timetick) {
		if (this.concreteMechanism != null)
			this.concreteMechanism.setTime(timetick);
	}

}
