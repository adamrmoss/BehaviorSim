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

package sim.model.action;

/**
 * The wrapper for entity action.
 * 
 * <p>Basically, the action is a hierarchical structure,
 * which may contains child actions (for the behavior action).</p>
 * 
 * <p>Basically, each behavior has a behavior action, which 
 * is a composite action plus some extra functions, such as
 * reseting index. OR the process method just calls a behavior 
 * action (for the general cases).</p>
 * 
 * <p>Each behavior action maintains a children list which contains
 * one-time composite actions. And each composite action
 * can contain one or multiple statement(s).</p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * @see <code>CompositeAction</code>
 * @see <code>BehaviorAction</code>
 */
import sim.core.AppEngine;

public abstract class Action {

	/* Action name */
	protected String actionName = null;

	/* Action type */
	protected int actionType = 0;
	public static final int ATOMIC = 1;
	public static final int COMPOSITE = 2;
	public static final int BEHAVIOR = 3;
	public static final int ONETIMECOMPOSITE = 4;

	/* Application Engine */
	protected static AppEngine engine = AppEngine.getInstance();

	/**
	 * Execute the action specified in the action string (for the one-time
	 * composite action), or execute one of the children actions (for the
	 * behavior actions).
	 * 
	 * <p>
	 * For the behavior actions,
	 * </p>
	 * 
	 * <p>
	 * The general algorithm of the child action to be executed is that: If the
	 * current index is less than the size of the child list, the action
	 * specified in the action string is executed.
	 * </p>
	 * 
	 * <p>
	 * Otherwise if the current index is not less than the size of the child
	 * list, return to the parent and let its parent executes the next action
	 * which follows the above algorithm.
	 * </p>
	 * 
	 * </p>After the execution, the current index is increased by 1.</p>
	 * 
	 * @return The execution result
	 */
	public ActionResult execute() {
		return null;
	}

	/**
	 * Return a copy of this action. It is mainly used in the sequence
	 * construction.
	 * 
	 * <p>
	 * It is used to avoid the action impacts between each other's internal
	 * states. Such as currentIndex, children, initializationString...
	 * </p>
	 * 
	 * @return a copy of this action
	 */
	public abstract Action copy();

	/**
	 * Return the type of this action object. It can be any of the following
	 * options: <code>ATOMIC</code>, <code>BEHAVIOR</code>,
	 * <code>COMPOSITE</code>and <code>ONETIMECOMPOSITE</code>
	 * 
	 * @return the type of this action object.
	 */
	public abstract int getActionType();

	// The setters and getters of object fields
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public String toString() {
		return this.actionName;
	}
}
