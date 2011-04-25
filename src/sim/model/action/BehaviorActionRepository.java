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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Repository for storing all actions of defined behaviors.
 * 
 * 
 * @author Fasheng Qiu
 * 
 */
public class BehaviorActionRepository {

	// Behavior action mapping, "behaviorID" -> "HashMap",
	// where "HashMap" is another mapping, "actionName" -> "action object".
	private Map behaviorActions = null;

	/** Constructor */
	public BehaviorActionRepository() {
		// behavior actions
		behaviorActions = new HashMap();
	}

	// ----------------------------------------------
	// Internal functions, originally located at
	// AppEngine
	// ----------------------------------------------
	/**
	 * Reset the behavior actions map. Let it be empty
	 */
	public void removeAllBehaviorActions() {
		this.behaviorActions.clear();
	}

	/**
	 * Remove all actions related to the specified behavior
	 * 
	 * @param id
	 *            Behavior Id
	 */
	public void removeBehaviorAction(int id) {
		this.behaviorActions.remove(new Integer(id));
	}

	/**
	 * Add an action for the specified behavior.
	 * 
	 * </p>Before calling this method, the caller should verify that the action
	 * is valid for the given behavior, meaning that the action string is a
	 * valid method call in the given category (for the atomic action) and the
	 * child-actions are valid (for the composite and behavior action).</p>
	 * 
	 * <p>
	 * If the action already exists, the new one will replace the old one
	 * without any promotes.
	 * </p>
	 * 
	 * <p>
	 * The action name and the action string and the child actions , if any,
	 * should be populated well, before calling this method.
	 * </p>
	 * 
	 * @param id
	 *            ID of the behavior where the action is associated to
	 * @param action
	 *            The action to be associated to
	 */
	public void addAction(int id, Action action) {
		Map actions = (Map) this.behaviorActions.get(new Integer(id));
		if (actions == null) {
			actions = new HashMap();
			this.behaviorActions.put(new Integer(id), actions);
		}
		actions.put(action.getActionName(), action);
	}

	/**
	 * Create a behavior action based on the given action string, which can
	 * contain simple method call, such as "move(1.3, 1.4)", also can contain
	 * composite method call, such as "moveForDistance(1.3, 1.4, 10.0)".
	 * 
	 * <p>
	 * Typically, the user can type in the action string in the normal way of
	 * method call. An example would be:
	 * </p>
	 * 
	 * <code>
	 * ......
	 * double speed = 1.3, direction = 1.4, distance = 10.0;
	 * double turnAngle = 0.1 * 2 * Math.PI;
	 * double totalAngle = 2 * Math.PI;
	 * if (speed > turnAngle) {
	 *   TASKQUEUE.add("move("+speed+", "+direction+")");
	 * } else {
	 *   TASKQUEUE.add("turnWithAngularSpeed("+turnAngle+")");
	 *   TASKQUEUE.add("moveForDistance(speed, direction, distance)");
	 *   TASKQUEUE.add("turnForAngle(turnAngle, totalAngle)");
	 * }
	 * ......
     * </code>
	 * <p>
	 * Each parameter of <code>TASKQUEUE.add</code> will be a single method
	 * call, which can contain any variable, such as local variables, category
	 * properties, constants, etc.
	 * </p>
	 * 
	 * @param behaviorId
	 *            The id of the behavior where the action locates at
	 * @param actionString
	 *            The action string which is used to define the task queue
	 * @return The name of the created behavior action
	 */
	public String addBehaviorAction(int behaviorId, String actionString) {
		if (actionString == null || actionString.length() == 0)
			actionString = "";
		/** Create the behavior action */
		BehaviorAction composite = new BehaviorAction();
		composite.setActionName(behaviorId + "Action");
		composite.setActionString(actionString);
		composite.setBehaviorID(behaviorId);
		addAction(behaviorId, composite);
		return composite.getActionName();
	}

	/**
	 * Return all behavior actions
	 * 
	 * @return all behavior actions
	 */
	public Map getAllBehaviorActions() {
		return this.behaviorActions;
	}

	/**
	 * Set the behavior actions map to be a new one
	 * 
	 * @param behaviorActions
	 *            The map to be set
	 */
	public void setAllBehaviorActions(Map behaviorActions) {
		this.behaviorActions.clear();
		this.behaviorActions.putAll(behaviorActions);
	}

	/**
	 * Return the given behavior's action map
	 * 
	 * @param id
	 *            The behavior id where the action map is returned
	 * @return the given behavior's action map
	 */
	public Map getBehaviorActionMap(int id) {
		return (Map) this.behaviorActions.get(new Integer(id));
	}

	/**
	 * Return a copy of the action with the specified action name and behavior
	 * id to avoid the action impacts each other's internal states such as
	 * <code>currentIndex</code> and <code>children</code>.
	 * 
	 * 
	 * @param behavioirID
	 *            The behavior the action locates at
	 * @param actionName
	 *            The action name of the action to be returned
	 * @return the action with the specified action name and category.
	 */
	public Action getAction(int id, String actionName) {
		Map actions = (Map) this.behaviorActions.get(new Integer(id));
		if (actions == null) {
			return null;
		}
		Action action = ((Action) actions.get(actionName));
		return action.copy();
	}

	/**
	 * Return all action names of the specified behavior.
	 * 
	 * @param behaviorName
	 *            The behavior where actions locates at
	 * @return all action names of the specified behavior.
	 */
	public List getAllBehaviorActionName(int id) {
		Map actions = (Map) this.behaviorActions.get(new Integer(id));
		if (actions == null)
			return new ArrayList();
		List ret = new ArrayList();
		Iterator keys = actions.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (((Action) actions.get(key)).getActionType() != Action.BEHAVIOR) {
				ret.add(key);
			}
		}
		return ret;
	}

	// ------------------------------------------------------
	// Getters/Setters
	// ------------------------------------------------------

	/**
	 * Return the behavior actions
	 */
	public Map getBehaviorActions() {
		return this.behaviorActions;
	}

}
