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

package sim.model.behavior;

import sim.core.AppEngine;
import sim.model.action.BehaviorAction;
import sim.model.entity.BNCategory;

/**
 * Behavior abstraction. It is the component of the behavior network.
 * 
 * <p>
 * It contains a name, the excitation computation code, and corresponding
 * action. Note that, in current implementation, a Behavior is private to an
 * agent, which means it is not shared with others. However, a Behavior can be
 * copied from and pasted to another agent.
 * </p>
 * 
 * @author Pavel, Fasheng QIU
 * 
 */
public class Behavior {

	/** Owner of this behavior */
	private BNCategory owner = null;

	/** Identification of this behavior */
	private int myId;

	/** Behavior strength of each time step */
	private double[] behaviorStrength;

	/** Behavior activation level of each time step */
	private double[] behaviorExcitation;

	/** Behavior name */
	private String behavName = null;

	/** Behavior equation ( the excitation computation code ) */
	private String behavEquation = null;

	/**
	 * The method call which wraps the computation code. It is defined in the
	 * entity which contains this behavior and evaluated in that entity. The
	 * method call is setup when this behavior is added into a particular
	 * entity.
	 */
	private String behavEquationMethodName = null;

	/** The action of this behavior once it is chosen */
	private String behaviorActionName = null;
	private boolean resumable = false;
	private BehaviorAction behaviorAction = null;

	/**
	 * The weight of this behavior, used in cooperative action selection
	 * mechanism
	 */
	private double weight = 1.0;

	/** Total time steps */
	private int totalTimeSteps;

	/** Constant of NO Behavior */
	public static final Behavior NO_BEHAVIOR = new Behavior(null, null, null);

	/** Constructor */
	public Behavior(BNCategory owner, String name, String equation) {
		this(owner, name, equation, false);
	}

	public Behavior(BNCategory owner, String name, String equation, boolean r) {
		this(owner, name, equation, r, null);
	}

	public Behavior(BNCategory owner, String name, String equation, boolean r,
			BehaviorAction action) {
		this.owner = owner;
		myId = sim.core.ConfigParameters.behaviorIndex++;
		behavName = name;
		behavEquation = equation;
		behaviorAction = action;
		resumable = r;
		totalTimeSteps = (AppEngine.getInstance().getTotalTimeticks());
		configure(totalTimeSteps);
	}

	/**
	 * Make a copy of this behavior and its action. This method is mainly used
	 * in the case that more than one entity have the same behavior. It is safe
	 * and necessary to make a copy of that behavior since it may have different
	 * action sequence for different entities.
	 * 
	 * <p>
	 * It is up to the client side to decide whether a copy of behavior or the
	 * original behavior is needed.
	 * </p>
	 * 
	 * @param newId
	 *            Whether a different id should be populated.
	 * @return A copy of this behavior and its action.
	 */
	public Behavior copy(boolean newId) {
		Behavior behavior = new Behavior(owner, behavName, behavEquation,
				resumable, (BehaviorAction) (behaviorAction == null ? null
						: behaviorAction.copy()));
		behavior.setBehaviorActionName(behaviorActionName);
		if (!newId) {
			behavior.myId = myId;
		}
		return behavior;
	}

	/** Setup arrays */
	public void configure(int totalticks) {
		behaviorStrength = new double[totalticks];
		behaviorExcitation = new double[totalticks];
	}

	/** Get the name of the behavior action */
	public String getActionName() {
		if (behaviorAction != null)
			return behaviorAction.getActionName();
		return null;
	}

	/** Get the equation computation code */
	public String getBehaviorEquation() {
		return behavEquation;
	}

	/** Set the equation computation code */
	public void setBehaviorEquation(String bEquation) {
		behavEquation = bEquation;
	}

	/** Return the behavior name */
	public String getBehaviorName() {
		return behavName;
	}

	/** Get the behavior information */
	public String toString() {
		if (behavName == null || behavEquation == null)
			return "";
		return "Name: " + behavName + " Equation: " + behavEquation.toString();
	}

	/** Update the excitation at the specified time instance */
	public void updateExcitation(double value, int timetick) {
		behaviorExcitation[timetick] = ((value < 20) ? value : 20);
	}

	/** Get the excitation of the specified time instance */
	public double getExcitation(int timetick) {
		return behaviorExcitation[timetick];
	}

	/** Update the behavior strength of the specified time instance */
	public void setBehaviorStrength(double value, int timetick) {
		behaviorStrength[timetick] = ((value > 0) ? value : 0);
	}

	/** Get the behavior strength at the specified time instance */
	public double getBehaviorStrength(int timetick) {
		return behaviorStrength[timetick];
	}

	/** Reset the behavior action */
	public void resetAction() {
		if (behaviorAction != null) {
			behaviorAction.resetChildren();
			AppEngine engine = AppEngine.getInstance();
			if (engine.appManager.currentApp.currentEntity instanceof BNCategory) {
				((BNCategory) engine.appManager.currentApp.currentEntity)
						.setTASKQUEUE(behaviorAction.getHelper());
				behaviorAction.setupTaskQueue();
			}
		}
	}

	/** Reset the index of current execution child action */
	public void resetActionIndex() {
		if (behaviorAction != null)
			behaviorAction.resetIndex();
	}

	/**
	 * Execute the action of this behavior. The execution result is the speed
	 * and direction of the move the entity will be performed soon.
	 * 
	 * @return The execution result.
	 */
	public Object performAction() {
		if (behaviorAction != null) {
			if (behaviorAction.childrenCompleted()) {
				resetAction();
				resetActionIndex();
			}
			return behaviorAction.execute().speedAndDirection;
		}
		return null;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getBehaviorActionName() {
		return behaviorActionName;
	}

	public void setBehaviorActionName(String behaviorActionName) {
		this.behaviorActionName = behaviorActionName;
	}

	public String getBehavEquationMethodName() {
		return behavEquationMethodName;
	}

	public void setBehavEquationMethodName(String behavEquationMethodName) {
		this.behavEquationMethodName = behavEquationMethodName;
	}

	public void setBehaviorAction(BehaviorAction behaviorAction) {
		this.behaviorAction = behaviorAction;
	}

	/**
	 * @return the resumable
	 */
	public boolean isResumable() {
		return resumable;
	}

	/**
	 * @param resumable
	 *            the resumable to set
	 */
	public void setResumable(boolean resumable) {
		this.resumable = resumable;
	}

	/**
	 * @return the totalTimeSteps
	 */
	public int getTotalTimeSteps() {
		return totalTimeSteps;
	}

	/**
	 * @param behavName
	 *            the behavName to set
	 */
	public void setBehaviorName(String behavName) {
		this.behavName = behavName;
	}

	/**
	 * Return the owner of this behavior
	 * 
	 * @return
	 */
	public BNCategory getOwner() {
		return owner;
	}

	/**
	 * @return Identification of this behavior
	 */
	public int getMyId() {
		return myId;
	}

}
