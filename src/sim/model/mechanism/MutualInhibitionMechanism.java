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

import java.util.List;

import sim.core.AppEngine;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.entity.Entity;
import sim.util.MessageUtils;

/**
 * Mutual inhibition action selection mechanism. Each time step, the behavior
 * with the highest activation level will dominate the current computing entity.
 * The state of that entity is determined by the selected behavior through the
 * execution of the behavior action, which is configured in the task queue.
 * 
 * @author Fasheng Qiu
 * @since 10/25/2007
 */
public class MutualInhibitionMechanism implements IMechanism {

	/**
	 * The current time step
	 */
	private int timeTick;

	/**
	 * The winner of last time step
	 */
	private int previousWinner = -1;

	/**
	 * Return the current time step of the current computing entity. Should not
	 * be called because the time step is wrapped around. It is not the global
	 * time step. Use {@link #getTime()()} of
	 * <code>sim.configure.model.Entity</code> instead.
	 */
	public int getTime() {
		throw new RuntimeException(
				"Can not probe the time step in the action mechanism.");
	}

	/**
	 * Set the current time step of the current computing entity. For the
	 * mechanism, the time step is wrapped around because of the thread for
	 * computing simulation data. see {@link #run()} of
	 * <code>sim.ui.SimulationDataThread</code>.
	 */
	public void setTime(int timeStep) {
		this.timeTick = timeStep % AppEngine.getInstance().getTotalTimeticks();
	}

	/**
	 * Mutual inhibition action selection mechanism. The method
	 * <code>setTimeInstance</code> should be called each time step to setup the
	 * correct time step of the current computing entity.
	 * 
	 * @see sim.model.mechanism.MechanismContext.setTimeInstance(int)
	 * 
	 */
	public void execute() {
		// Application engine
		AppEngine engine = AppEngine.getInstance();
		// Obtain the behavior network of the current computing entity
		BehaviorNetwork network = ((sim.model.entity.BNCategory) engine.appManager.currentApp.currentEntity)
				.getBehaviorNetwork();
		// Compute behavior excitation and strength
		List behaviors = network.getBehaviorList();
		for (int i = 0; i < behaviors.size(); i++) {
			Behavior behavior = (Behavior) behaviors.get(i);
			try {
				double excitation = engine.bnEditor
						.computeBehaviorExcitation(behavior);
				// if (behavior.getBehaviorName().equals("offender_passball"))
				// System.out.println(engine.getCurrentEntity().getMyId()+","+excitation);
				behavior.updateExcitation(excitation, timeTick);
			} catch (Exception e) {
				e.printStackTrace();
				MessageUtils.debug(this, "execute", e);
				return;
			}
		}
		network.updateBehaviorStrengths(timeTick);

		// Select a behavior, if no behavior is selected, return
		Behavior selectedBehavior = network.selectBehavior(timeTick);
		if (selectedBehavior == Behavior.NO_BEHAVIOR) {
			previousWinner = -1;
			return;
		}
		// System.out.println(engine.getCurrentEntity().getDisplayName() + ", "
		// + selectedBehavior.getBehaviorName());
		// Check whether the winner behavior is resumable. If so, rebuild
		// its task queue by resetting the child list without resetting the
		// index of the current execution child.
		if (previousWinner != -1
				&& previousWinner != selectedBehavior.getMyId()
				&& selectedBehavior.isResumable()) {
			selectedBehavior.resetAction();
			selectedBehavior.resetActionIndex(); // FIXME: Is it reasonable not
			// to reset the index?
		}
		previousWinner = selectedBehavior.getMyId();
		// Execute the next action
		Object speedAndDirection = selectedBehavior.performAction();
		if (speedAndDirection == null)
			return;
		// Move the entity in the specified speed and direction
		if (speedAndDirection instanceof String) {
			String sd = (String) speedAndDirection;
			double speed = Double.parseDouble(sd.substring(0, sd.indexOf(',')));
			double direction = Double.parseDouble(sd.substring(
					sd.indexOf(',') + 1, sd.lastIndexOf(',')));
			int type = sim.model.entity.SystemFunction.MOVEFORWARD;
			try {
				type = Integer.parseInt(sd.substring(sd.lastIndexOf(',') + 1));
			} catch (Exception e) {
			}
			Entity current = engine.getCurrentEntity();
			engine.system.move(current, speed, direction, type);
		}
	}

	/**
	 * Return a copy of this mechanism
	 */
	public IMechanism copy() {
		return new MutualInhibitionMechanism();
	}

}
