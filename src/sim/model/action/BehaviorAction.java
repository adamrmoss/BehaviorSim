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
import java.util.List;

import sim.util.MessageUtils;
import sim.util.MethodUtils;

/**
 * The wrapper for behavior action.
 * 
 * <p>
 * The action is a hierarchical structure, which may contains child actions,
 * which in turn can be composite action.
 * </p>
 * 
 * <p>
 * Basically, each behavior has a behavior action, which is a composite action
 * plus some extra functions, such as reseting index. OR the process method just
 * calls a behavior action (for the general cases).
 * </p>
 * 
 * <p>
 * Each behavior action maintains a children list which contains composite
 * actions or atomic actions. And each composite action should contain two or
 * more atomic actions.
 * </p>
 * 
 * <p>
 * Also, the children can be constructed dynamically with the code specified by
 * the user.
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * @see <code>Action</code>
 */
public class BehaviorAction extends Action {

	/* The id of the behavior this action belongs to */
	protected int behaviorID;

	/*
	 * The index of current action to be executed in the children list
	 */
	protected int currentIndex = 0;

	/* The child action list */
	protected List children = null;

	/* Helper for establishing task queue (children list) */
	protected TaskQueueHelper helper = null;

	/* The action string which specifies how to construct the task queue */
	protected String actionString = null;

	/* Method for setting up the children list of this action */
	protected String methodNameToSetupTaskQueue = null;

	/** Constructor */
	public BehaviorAction() {
		this.children = new ArrayList();
		this.helper = new TaskQueueHelper(this);
	}

	/** Set the method name for setting up the task queue */
	public void setMethodNameToSetupTaskQueue(String m) {
		this.methodNameToSetupTaskQueue = m;
	}

	/**
	 * Invoke the method of setting up the children list (task queue) of this
	 * action
	 */
	public void setupTaskQueue() {
		if (this.methodNameToSetupTaskQueue == null
				|| (methodNameToSetupTaskQueue = this.methodNameToSetupTaskQueue
						.trim()).length() == 0)
			return;
		try {
			// MethodUtils.probeMethodsList(engine.appManager.currentApp.currentEntity);
			MethodUtils.invokeExactMethod(
					engine.appManager.currentApp.currentEntity,
					methodNameToSetupTaskQueue, 
					null
			);
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.debug(this, "setupTaskQueue", e);
			throw new RuntimeException(
					"Can not invoke task queue setting up routine.");
		}
	}

	/**
	 * Add a child action into this action
	 * 
	 * <p>
	 * It is generally used in the composite action.
	 * </p>
	 * 
	 * @param child
	 *            The child action to be added
	 */
	public void addChildAction(Action child) {
		this.children.add(child);
	}

	/**
	 * Add the action with the specified action name as the last child action.
	 * 
	 * @param childActionName
	 *            The name of the action to be added
	 */
	public void addChild(String actionName) {
		this.addChildAction(engine.getAction(behaviorID, actionName));
	}

	/**
	 * Set all child actions to be a new one
	 * 
	 * @param actions
	 *            The new child actions list
	 */
	public void setChildActions(List actions) {
		this.children = new ArrayList(actions);
	}

	/**
	 * Return a copied list of all child actions
	 */
	public List getChildActions() {
		if (this.children == null)
			return new ArrayList();
		return new ArrayList(this.children);
	}

	/**
	 * Add the action with the specified action name as the last child action.
	 * 
	 * <p>
	 * This method just adds the action name into the child actions list. The
	 * actual child action look-up will be finished later.
	 * </p>
	 * 
	 * {@link #sim.utils.XMLUtils.loadActionsElement(Element e)}
	 * 
	 * @param actionName
	 *            The action name to add
	 */
	public void addChildLazy(String actionName) {
		this.children.add(actionName);
	}

	/**
	 * Reset the current index of this action and its children.
	 * 
	 * <p>
	 * It is generally used in the behavior action to provide the recursive
	 * execution of the action.
	 * </p>
	 * 
	 */
	public void resetIndex() {
		this.currentIndex = 0;
	}

	/**
	 * execute one of the children actions (for the composite and behavior
	 * actions).
	 * 
	 * <p>
	 * The algorithm of the child action to be executed is that:
	 * </p>
	 * <p>
	 * If no children generated yet (for example, for the dynamic behavior
	 * action), simply return. Otherwise,
	 * </p>
	 * 
	 * <p>
	 * Check the type of the current action to be executed by the current index.
	 * If the current index is less than the size of the child list, then if it
	 * is an atomic/one time composite action, the action is executed and
	 * <code>currentIndex</code> is increased by 1. Otherwise, if it is a
	 * composite action, one of its children will be executed and return a flag
	 * which indicates whether the action is finished. If so, the
	 * <code>currentIndex</code> is increased by 1 which let next action to
	 * execute.
	 * </p>
	 * 
	 * <p>
	 * Otherwise if the current index is not less than the size of the child
	 * list, <code>currentIndex</code>is reset and the first children is
	 * executed.
	 * </p>
	 * 
	 * @return The execution result
	 */
	public ActionResult execute() {
		// No children, just return status "finished"
		ActionResult result = new ActionResult();
		if (this.children.isEmpty()) {
			return result;
		}
		if (this.currentIndex < this.children.size()) {
			Action toExecute = (Action) this.children.get(this.currentIndex);
			result = toExecute.execute();
			// If atomic or one time composite, just execute it and increase the
			// index by 1
			/*
			 * if (toExecute.getActionType() == Action.ATOMIC ||
			 * toExecute.getActionType() == Action.ONETIMECOMPOSITE) {
			 * this.currentIndex++; } // If composite, execute it and check
			 * whether it is finished, if so, // increase the index by 1 else
			 */if (toExecute.getActionType() == Action.COMPOSITE) {
				if (result.allExecuted) {
					this.currentIndex++;
				}
			}
		} else {
			this.resetIndex();
			return this.execute();
		}
		return result;
	}

	/**
	 * Return a copy of this action. It is mainly used in the sequence
	 * construction.
	 * 
	 * <p>
	 * It is used to avoid the action impacts between each other's internal
	 * states, such as <code>currentIndex</code>, <code>children</code>,
	 * <code>actionString</code> and so on.
	 * </p>
	 * 
	 * <p>
	 * However, for the dynamically configured composite/behavior actions, this
	 * method should return the original version since it contains the compiled
	 * children construction method.
	 * </p>
	 * 
	 * @return a copy of this action
	 */
	public Action copy() {
		BehaviorAction copy = new BehaviorAction();
		copy.setActionName(this.getActionName());
		copy.setBehaviorID(behaviorID);
		copy.setActionString(this.actionString);
		List copiedChildren = new ArrayList(this.children.size());
		for (int i = 0; i < this.children.size(); i++) {
			Action a = (Action) this.children.get(i);
			copiedChildren.add(a.copy());
		}
		copy.setChildActions(copiedChildren);
		return copy;
	}

	/**
	 * Return whether the child actions are finished
	 * 
	 * @return return whether the child actions are finished
	 */
	public boolean childrenCompleted() {
		return this.currentIndex >= this.children.size();
	}

	/**
	 * Reset all children
	 */
	public void resetChildren() {
		this.children.clear();
	}

	/**
	 * Return the type of this action
	 */
	public int getActionType() {
		return BEHAVIOR;
	}

	/**
	 * @return the behaviorID
	 */
	public int getBehaviorID() {
		return behaviorID;
	}

	/**
	 * @param behaviorID
	 *            the behaviorID to set
	 */
	public void setBehaviorID(int behaviorID) {
		this.behaviorID = behaviorID;
	}

	/**
	 * Return the action string
	 * 
	 * @return the actionString
	 */
	public String getActionString() {
		return actionString;
	}

	/**
	 * Set the action string
	 * 
	 * @param actionString
	 *            the actionString to set
	 */
	public void setActionString(String actionString) {
		this.actionString = actionString;
	}

	/**
	 * Return the task queue helper
	 * 
	 * @return the helper
	 */
	public TaskQueueHelper getHelper() {
		return helper;
	}
}
