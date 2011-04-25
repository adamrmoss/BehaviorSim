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

import sim.model.entity.Entity;
import sim.util.MessageUtils;

/**
 * The wrapper for composite action.
 * 
 * <p>
 * The action may contain one or more statements to be executed in one time
 * step.
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * @see <code>Action</code>
 */
public class CompositeAction extends Action {

	/* The statements to be executed in this action */
	/*
	 * The byte codes of the statements are inserted into the relative category
	 * class
	 */
	/* as a method, the method name is generated in the creator of this action */
	private String statements = null;

	public String getStatements() {
		return this.statements;
	}

	/**
	 * Constructor
	 * 
	 * @param statements
	 *            The statements to be executed once this action is called
	 */
	public CompositeAction(String statements) {
		this.statements = statements;
	}

	/**
	 * Return a copy of this action. It is mainly used in the sequence
	 * construction.
	 * 
	 * 
	 * @return a copy of this action
	 */
	public Action copy() {
		return new CompositeAction(this.getStatements());
	}

	/**
	 * execute statements
	 * 
	 * @return The execution result
	 */
	public ActionResult execute() {
		ActionResult result = new ActionResult();
		try {
			result.speedAndDirection = engine.evaluateAction(
					getCurrentEntity(), this.statements);
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.error(this, "execute", e);
			result.speedAndDirection = null;
		}
		result.allExecuted = true;
		return result;
	}

	/**
	 * Return the type of this action
	 */
	public int getActionType() {
		return COMPOSITE;
	}

	// /////////////////////////////////////////////////////////////////
	// For dynamic construction of child actions
	// /////////////////////////////////////////////////////////////////
	// ////////////////////////BEGIN////////////////////////////////////

	/**
	 * Return the current active entity.
	 * 
	 * <p>
	 * It is mainly used to retrieve the current active entity and perform some
	 * actions (make method calls by the method "init()") on it. See
	 * </p>
	 * 
	 * {@link #init()}
	 * 
	 * @return The current active entity
	 */
	public Entity getCurrentEntity() {
		return engine.appManager.currentApp.currentEntity;
	}
	// ////////////////////////END//////////////////////////////////////

}
