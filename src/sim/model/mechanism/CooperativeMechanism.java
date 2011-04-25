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
import sim.model.entity.BNCategory;
import sim.model.entity.SystemFunction;
import sim.util.MessageUtils;

/**
 * Cooperative action selection mechanism. The state of the current computing
 * entity is determined by the execution of all behaviors.
 * 
 * <p>
 * The action of one behavior will cause the entity to be in a new state, Si,
 * after its execution. And The final state of the entity is the vector
 * summation of all produced states, S = S1 + S2 + ... + Sn, for n behaviors.
 * </p>
 * 
 * <p>
 * Currently, the position of the entity is considered as the only element of
 * Si. And the final position is the vector summation of all position vector.
 * </p>
 * 
 * <p>
 * The basic steps involving are as follows:
 * </p>
 * 
 * 
 * @author Fasheng Qiu
 * @since 10/25/2007
 * 
 */
public class CooperativeMechanism implements IMechanism {

	/**
	 * The current time step
	 */
	private int timeTick;

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
	 * Cooperative action selection mechanism
	 */
	public void execute() {
		// Application engine
		AppEngine engine = AppEngine.getInstance();
		// Current computing entity
		BNCategory current = (BNCategory) engine.getCurrentEntity();
		// A copy entity of the current computing entity
		BNCategory copy = null;
		try {
			copy = (BNCategory) engine.appManager.currentApp.dm
					.getCategory(current.getEntityType());
		} catch (Exception e) {
			MessageUtils.debug(this, "execute", e);
			MessageUtils.displayError(e);
			return;
		}
		// Obtain the behavior network of the current computing entity
		BehaviorNetwork network = ((BNCategory) engine.appManager.currentApp.currentEntity)
				.getBehaviorNetwork();
		// Save the increment of speed and direction corresponding to each
		// behavior
		double deltaX = 0.0D, deltaY = 0.0D;
		// Compute behavior excitation and strength
		List behaviors = network.getBehaviorList();
		// System.out.println(((Behavior)behaviors.get(0)).getWeight());
		for (int i = 0; i < behaviors.size(); i++) {
			// Perform each behavior
			Behavior behavior = (Behavior) behaviors.get(i);
			// Change the current computing entity to the copy temporally
			current.copyTo(copy);
			engine.appManager.currentApp.currentEntity = copy;
			// Compute the behavior activation
			try {
				double excitation = engine.bnEditor
						.computeBehaviorExcitation(behavior);
				behavior.setBehaviorStrength(excitation, timeTick);
			} catch (Exception e) {
				MessageUtils.debug(this, "execute", e);
			}
			// Perform the behavior
			Object speedANDDirection = behavior.performAction();
			// Save the new result
			if (speedANDDirection != null) {
				if (speedANDDirection instanceof String) {
					String sd = (String) speedANDDirection;
					double speed = Double.parseDouble(sd.substring(0, sd
							.indexOf(',')));
					double direction = Double.parseDouble(sd.substring(sd
							.indexOf(',') + 1, sd.lastIndexOf(',')));
					int type = sim.model.entity.SystemFunction.MOVEFORWARD;
					try {
						type = Integer.parseInt(sd.substring(sd
								.lastIndexOf(',') + 1));
					} catch (Exception e) {
					}
					if (type == SystemFunction.MOVEFORWARD) {
						deltaX += behavior.getWeight() * speed
								* Math.cos(direction);
						deltaY += behavior.getWeight() * speed
								* Math.sin(direction);
					} else if (type == SystemFunction.MOVEBACKWARD) {
						deltaX -= behavior.getWeight() * speed
								* Math.cos(direction);
						deltaY -= behavior.getWeight() * speed
								* Math.sin(direction);
					}
				}
			}
		}
		// Just vector sum the direction, the speed is not changed!!!!!!

		// Restore the current computing entity
		engine.appManager.currentApp.currentEntity = (current);
		// Obtain the vector summation of all speed and directions
		if (deltaX != 0.0D || deltaY != 0.0D) {
			// double sqrt = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			// if (sqrt > 5.0) {
			// deltaX = 5 * deltaX / sqrt;
			// deltaY = 5 * deltaY / sqrt;
			// } else if (sqrt < 3.0 ){
			// deltaX = 3 * deltaX / sqrt;
			// deltaY = 3 * deltaY / sqrt;
			// }
			// System.out.println(deltaX + "," + deltaY);
			engine.system.move(current, deltaX, deltaY);
		}
	}

	/**
	 * Return a copy of this mechanism
	 */
	public IMechanism copy() {
		return new CooperativeMechanism();
	}
}
