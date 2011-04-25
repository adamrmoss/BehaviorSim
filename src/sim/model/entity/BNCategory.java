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

package sim.model.entity;

import sim.core.AppEngine;
import sim.model.action.TaskQueueHelper;
import sim.model.behavior.BehaviorNetwork;
import sim.model.mechanism.CooperativeMechanism;
import sim.model.mechanism.IMechanism;
import sim.model.mechanism.MechanismContext;
import sim.model.mechanism.MutualInhibitionMechanism;
import sim.model.mechanism.NoDynamicsMechanism;
import sim.model.mechanism.SystemDynamicMechanism;

/**
 * The category whose action is based on behavior network theory.
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class BNCategory extends Category {

	// The entity's behavior network
	private BehaviorNetwork network = new BehaviorNetwork();

	// The entity's action selection mechanism (default)
	private MechanismContext asm = new MechanismContext(
			new NoDynamicsMechanism());

	// The entity's task queue setting up helper of a specific behavior action
	protected TaskQueueHelper TASKQUEUE = null;

	/**
	 * Constructor
	 * 
	 * <p>
	 * Reset time slice
	 * </p>
	 * 
	 */
	public BNCategory() {
		super();
		timetick = 0;
	}

	/**
	 * A deep copy of this category object to the specified category object.
	 * 
	 * <p>
	 * The purpose of this method is to provide a state copy for the cooperative
	 * action selection mechanism, where each action will produce a "new" state
	 * of the category. To avoid the "new" state affects the original state, a
	 * copied category is used to perform the action.
	 * </p>
	 * 
	 * <p>
	 * <strong>FIXME</strong> DO WE NEED TO COPY behavior network and the
	 * mechanism? DOES THE ACTION EFFECT THESE TWO PROPERTIES?
	 * </p>
	 * 
	 * @param category
	 *            The category who gets the states of this category
	 */
	public void copyTo(BNCategory category) {
		if (category == null)
			return;
		super.copyTo(category);
		// Do we need to copy behavior network and the mechanism?
		// Default: not copy, suppose that they will never be changed after
		// the execution of all behaviors
	}

	/**
	 * Central process. Update the states by executing pre-defined actions.
	 */
	public void process() {
		// Setup behavior network
		AppEngine.getInstance().bnEditor.updateCoefficients();
		// Set the current time step of this entity
		asm.setTime(timetick);
		// Actual action selection and execution
		asm.performAction();
	}

	/**
	 * Obtain the behavior network
	 * 
	 * @return The behavior network
	 */
	public BehaviorNetwork getBehaviorNetwork() {
		// if (isSystemDynamicMechanism())
		// throw new
		// RuntimeException("No behavior network setup for this entity.");
		return network;
	}

	/**
	 * Change the behavior network
	 * 
	 * @param bNet
	 *            The new behavior network
	 */
	public void setBehaviorNetwork(BehaviorNetwork bNet) {
		// if (isSystemDynamicMechanism())
		// throw new
		// RuntimeException("No behavior network can be setup for entity with General Dynamics.");
		network = bNet;
	}

	/**
	 * Reset the behavior network
	 */
	public void resetBehaviorNetwork() {
		network.clearAll();
		network = new BehaviorNetwork();
	}

	/**
	 * Whether the behavior network of the entity is defined.
	 * 
	 * 
	 * @return Whether the behavior network of the entity is defined.
	 */
	public boolean isBehaviorNetowrkDefined() {
		// if (isSystemDynamicMechanism())
		// throw new
		// RuntimeException("No behavior network setup for this entity.");
		return network.isBehaviorNetworkDefined();
	}

	/**
	 * Mutual inhibition mechanism
	 * 
	 * @return Whether this category uses mutual inhibition mechanism
	 */
	public boolean isMutualInhibitionMechanism() {
		return asm.getConcreteMechanism() instanceof MutualInhibitionMechanism;
	}

	/**
	 * Cooperative mechanism
	 * 
	 * @return Whether this category uses cooperative mechanism
	 */
	public boolean isCooperativeMechanism() {
		return asm.getConcreteMechanism() instanceof CooperativeMechanism;
	}

	/**
	 * System dynamic mechanism
	 * 
	 * @return Whether this category uses dynamic mechanism
	 */
	public boolean isSystemDynamicMechanism() {
		return asm.getConcreteMechanism() instanceof SystemDynamicMechanism;
	}

	/**
	 * No dynamics mechanism
	 * 
	 * @return Whether this category has no dynamics
	 */
	public boolean isNoDynamicsMechanism() {
		return asm.getConcreteMechanism() instanceof NoDynamicsMechanism;
	}

	/**
	 * Return index of action selection mechanism
	 * 
	 * @return
	 */
	public int getActionSelectionMechanismIndex() {
		if (isMutualInhibitionMechanism())
			return BehaviorNetwork.MUTUAL;
		if (isCooperativeMechanism())
			return BehaviorNetwork.COOPERATIVE;
		if (isSystemDynamicMechanism())
			return BehaviorNetwork.DYNAMICS;
		return BehaviorNetwork.NODYNAMICS;
	}

	/**
	 * Return a copy of the current action selection mechanism
	 * 
	 * @return A copy of the current action selection mechanism of this entity
	 */
	public IMechanism getActionSelectionMechanism() {
		return asm.getConcreteMechanism().copy();
	}

	/**
	 * Get the general dynamics.
	 */
	public String getGeneralDynamics() {
		if (isNoDynamicsMechanism())
			throw new RuntimeException("This entity has no dynamics.");
		if (isMutualInhibitionMechanism())
			throw new RuntimeException(
					"Can not get general dynamics for entity with behavior network.");
		if (isCooperativeMechanism())
			throw new RuntimeException(
					"Can not get general dynamics for entity with behavior network.");
		SystemDynamicMechanism ism = (SystemDynamicMechanism) getOriginalActionSelectionMechanism();
		return ism.getCode();
	}

	/**
	 * Register the general dynamics.
	 * 
	 * @param code
	 *            Definition of the general dynamics
	 * @throws Exception
	 *             if the code has errors
	 */
	public void registerGeneralDynamics(String code) throws Exception {
		if (isMutualInhibitionMechanism())
			throw new RuntimeException(
					"Can not define general dynamics for entity with behavior network.");
		if (isCooperativeMechanism())
			throw new RuntimeException(
					"Can not define general dynamics for entity with behavior network.");
		SystemDynamicMechanism ism = (SystemDynamicMechanism) getOriginalActionSelectionMechanism();
		ism.set(this, code);
	}

	/**
	 * Return the current action selection mechanism
	 * 
	 * @return the current action selection mechanism of this entity
	 */
	public IMechanism getOriginalActionSelectionMechanism() {
		return asm.getConcreteMechanism();
	}

	/**
	 * Set the current action selection mechanism
	 * 
	 * @param asm
	 *            the asm to set
	 */
	public void setActionSelectionMechanism(IMechanism asm) {
		this.asm.setConcreteMechanism(asm);
	}

	/**
	 * Set the task queue setting up helper for the specific behavior action
	 * 
	 * @param taskqueue
	 *            the TASKQUEUE to set
	 */
	public void setTASKQUEUE(TaskQueueHelper taskqueue) {
		// if (isSystemDynamicMechanism())
		// throw new
		// RuntimeException("Task queue can not be used in General Dynamics.");
		TASKQUEUE = taskqueue;
	}

	/**
	 * Return the task queue helper
	 * 
	 * @return the task queue helper
	 */
	public TaskQueueHelper getTASKQUEUE() {
		// if (isSystemDynamicMechanism())
		// throw new
		// RuntimeException("Task queue can not be used in General Dynamics.");
		return TASKQUEUE;
	}
}
