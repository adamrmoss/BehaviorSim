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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import sim.core.AppEngine;
import sim.core.ConfigParameters;
import sim.model.action.BehaviorAction;
import sim.model.entity.BNCategory;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Entity;
import sim.util.MessageUtils;
import sim.util.MethodUtils;

/**
 * Behavior network editor. It contains functionalities, such as define a
 * behavior, define behavior network, setup weights, etc.
 * 
 * 
 * @author Fasheng Qiu
 * @since 11/20/1007
 * 
 */
public class BehaviorNetworkEditor {

	// For behavior based simulation
	public BehaviorRepository repository = new BehaviorRepository();

	/**
	 * Create a new behavior with the given name, equation and actionString. And
	 * put the behavior into the behaviors list.
	 * 
	 * @param bn
	 *            Owner of the behavior
	 * @param name
	 *            The behavior name
	 * @param equation
	 *            The behavior equation
	 * @param resumable
	 *            The task queue is resumable or not
	 */
	public Behavior createNewBehavior(BNCategory bn, String name,
			String equationStr, boolean resumable) {

		/** Create the behavior */
		Behavior behavior = new Behavior(bn, name, equationStr, resumable);
		addBehaviorToRepository(behavior);
		return behavior;

	}

	/**
	 * Update the behavior equation and actions
	 * 
	 * @param owner
	 *            Owner of the behavior
	 * @param id
	 *            The id of the behavior to update
	 * @param name
	 *            The name of the behavior to update
	 * @param equation
	 *            The behavior equation
	 * @param actionString
	 *            The behavior actions
	 * @param resumable
	 *            The task queue is resumable or not
	 * @return Whether the behavior is updated successfully
	 */
	public Behavior updateBehavior(BNCategory owner, int id, String name,
			String equationStr, boolean resumable) {
		Behavior behavior = repository.getBehaviorById(owner, id);
		behavior.setBehaviorName(name);
		behavior.setBehaviorEquation(equationStr);
		behavior.setResumable(resumable);
		return behavior;
	}

	/**
	 * Get the behavior activation
	 * 
	 * @param behaviorName
	 *            The name of the behavior
	 * @return The behavior activation
	 */
	public double getBehaviorActivation(String behaviorName) {
		AppEngine engine = AppEngine.getInstance();
		/** Get the behavior */
		BehaviorNetwork bn = ((BNCategory) engine.appManager.currentApp.currentEntity)
				.getBehaviorNetwork();
		Behavior behavior = bn.getBehavior(behaviorName);
		/** Get the current time instance */
		int c = ((BNCategory) engine.appManager.currentApp.currentEntity)
				.getTime();
		/** Get the strength */
		return behavior.getBehaviorStrength(c - 1 >= 0 ? c - 1 : 0);
	}

	/**
	 * Update the coefficients of behavior network
	 * 
	 * @param args
	 *            The coefficients for mutual inhibition mechanism and weights
	 *            for cooperative mechanism
	 */
	public void setBehaviorNetworkTable(String args) {
		/** Translate the string format coefficients to a list */
		List coefficients = new ArrayList();
		StringTokenizer st = new StringTokenizer(args);
		while (st.hasMoreElements()) {
			coefficients.add(new Double(Double.parseDouble((String) st
					.nextElement())));
		}
		/** Update the coefficients */
		AppEngine engine = AppEngine.getInstance();
		boolean bn = ((BNCategory) engine.appManager.currentApp.currentEntity)
				.isMutualInhibitionMechanism();
		((BNCategory) engine.appManager.currentApp.currentEntity)
				.getBehaviorNetwork().updateEdgesFromCoefficientsList(bn,
						coefficients);
	}

	/**
	 * Set the weight of the specified behavior.
	 * 
	 * @param behaviorName
	 *            The name of the behavior to set
	 * @param weight
	 *            The new weight of the behavior
	 */
	public void setBehaviorWeight(String behaviorName, double weight) {
		/** Update the weight */
		AppEngine engine = AppEngine.getInstance();
		((BNCategory) engine.appManager.currentApp.currentEntity)
				.getBehaviorNetwork().updateWeight(behaviorName, weight);
	}

	/**
	 * Compute the behavior excitation by make a method call on the current
	 * computing entity.
	 * 
	 * <p>
	 * The method should return a double value as the computing result, that is,
	 * excitation value.
	 * </p>
	 * 
	 * @param The
	 *            behavior whose excitation to be computed
	 * @return The behavior excitation
	 * @throws RuntimeException
	 *             If any exception happens during the call
	 */
	public double computeBehaviorExcitation(Behavior behavior) {
		/** Prepare objects */
		String methodName = behavior.getBehavEquationMethodName();
		Entity current = AppEngine.getInstance().appManager.currentApp.currentEntity;
		/** Make method call */
		Object result = null;
		try {
			result = MethodUtils.invokeExactMethod(current, methodName, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (result == null || !(result instanceof Number)) {
			throw new RuntimeException("The returned result should be double.");
		}
		return ((Number) result).doubleValue();
	}

	/**
	 * Update the coefficients dynamically if necessary
	 */
	public void updateCoefficients() {
		/** Current behavior network */
		AppEngine engine = AppEngine.getInstance();
		BehaviorNetwork bn = ((BNCategory) engine.appManager.currentApp.currentEntity)
				.getBehaviorNetwork();
		if (!bn.isDynamic()) {
			return;
		}
		/** Prepare objects */
		String methodName = bn.getDynamicStrMethodName();
		Entity current = AppEngine.getInstance().appManager.currentApp.currentEntity;
		/** Make method call */
		try {
			MethodUtils.invokeExactMethod(current, methodName, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Add a behavior into the behaviors list
	 * 
	 * @param b
	 *            The behavior to add
	 */
	public void addBehaviorToRepository(Behavior b) {
		repository.addBehaviorToRepository(b);
	}

	/**
	 * Return the given behavior based on the given name. If the behavior can
	 * not be found, an empty behavior is returned
	 * 
	 * @param owner
	 *            Owner of the behavior
	 * @param name
	 *            The name of the behavior to return
	 * @return The behavior with the given name
	 */
	public Behavior getBehavior(BNCategory owner, String name) {
		return repository.getBehaviorByName(owner, name);
	}

	/**
	 * Verify the definition of behavior excitation and action. It is used when
	 * a behavior from repository is added to the behavior network of the
	 * specified entity.
	 * 
	 * @param behaviorID
	 *            ID of the behavior
	 * @return Whether the definition is correct.
	 */
	public boolean verifyExcitationActionMethod(BNCategory e, int behaviorID)
			throws sim.util.SimException {
		/** Get behavior and its location */
		Behavior behavior = repository.getBehaviorById(e, behaviorID);
		if (behavior == Behavior.NO_BEHAVIOR)
			throw new RuntimeException(
					"The behavior with the specified name does not exist.");
		/** Just use a copy of the behavior */
		behavior = behavior.copy(true);
		/** Get engine */
		AppEngine engine = AppEngine.getInstance();
		/** Initialize the behavior excitation computation code */
		String methodNameOfExcitationComputationCode = "_proxy"
				+ (ConfigParameters.methodIndex++);
		StringBuffer code = new StringBuffer();
		code.append("public double ").append(
				methodNameOfExcitationComputationCode).append("(){").append(
				behavior.getBehaviorEquation()).append("}");
		/** Check the validity of behavior excitation */
		engine.appManager.currentApp.dm.verifyMethodDef(e.getEntityType(), code
				.toString());

		/** Get behavior action */
		BehaviorAction ba = (BehaviorAction) engine.getAction(behaviorID,
				behavior.getBehaviorActionName());
		/** Create a method to setup the task queue setting up helper */
		String body = ba.getActionString();
		code = new StringBuffer();
		if (body.indexOf("TASKQUEUE.add") == -1) {
			String methodNameOfSingleCompositeAction = "_proxy"
					+ (ConfigParameters.methodIndex++);
			code.append("public String ").append(
					methodNameOfSingleCompositeAction).append("(){");
			code.append(body);
			code.append("}");
		} else {
			String methodNameOfTaskQueueAdder = "_proxy"
					+ (ConfigParameters.methodIndex++);
			// a single code block, construct the task queue automatically
			code.append("public void ").append(methodNameOfTaskQueueAdder)
					.append("(){").append(body).append("}");
		}
		engine.appManager.currentApp.dm.verifyMethodDef(e.getEntityType(), code
				.toString());

		/** All verification is passed */
		return true;
	}

	/**
	 * Add the specified behavior to the current network. And attach the
	 * behavior to an unoccupied position in the network.
	 * 
	 * <p>
	 * Initialize the behavior internal states, currently, the excitation
	 * computation code is wrapped in a method which is created in the current
	 * entity. The method name is saved in the behavior which can be invoked
	 * later.
	 * </p>
	 * 
	 * <p>
	 * Also, the behavior action is initialized for that behavior. If the action
	 * contains no explicit task queue implementation (no "TASKQUEUE.add" is
	 * found), the code block will be treated as the body of a single composite
	 * action, which will be added to the task queue automatically. Otherwise,
	 * normal task queue implementation is used, the above step is not involved.
	 * </p>
	 * 
	 * @param e
	 *            The target entity
	 * @param id
	 *            The behavior's id
	 */
	public void addBehaviorToNetwork(BNCategory e, int id) throws Exception {
		/** Get behavior and its location */
		Behavior behavior = repository.getBehaviorById(e, id);
		if (behavior == Behavior.NO_BEHAVIOR)
			throw new RuntimeException(
					"The behavior with the specified name does not exist.");
		BehaviorPosition position = e.getBehaviorNetwork()
				.getFirstUnoccupiedPosition();
		if (position == null)
			throw new RuntimeException(
					"Can not find a location for that behavior.");
		/**
		 * NOTE THAT BEHAVIOR IN BEHAVIOR NETWORK IS SAME AS THAT IN BEHAVIOR
		 * REPOSITORY.
		 */
		/** Get engine */
		AppEngine engine = AppEngine.getInstance();
		/** Initialize the behavior excitation computation code */
		String methodNameOfExcitationComputationCode = "_proxy"
				+ (ConfigParameters.methodIndex++);
		StringBuffer code = new StringBuffer();
		code.append("public double ").append(
				methodNameOfExcitationComputationCode).append("(){").append(
				behavior.getBehaviorEquation()).append("}");
		engine.createANewMethod(e.getEntityType(), false, false,
				methodNameOfExcitationComputationCode, code.toString());

		/** Get behavior action */
		BehaviorAction ba = (BehaviorAction) engine.getAction(id, behavior
				.getBehaviorActionName());
		/** Create a method to setup the task queue setting up helper */
		// If no calls of "TASKQUEUE.add", then all code blocks should be
		// treated as single composite action
		String body = ba.getActionString();
		String methodNameOfSingleCompositeAction = null;
		if (body.indexOf("TASKQUEUE.add") == -1) {
			// create a method which represents the composite action
			methodNameOfSingleCompositeAction = "_proxy"
					+ (ConfigParameters.methodIndex++);
			code = new StringBuffer();
			code.append("public String ").append(
					methodNameOfSingleCompositeAction).append("(){");
			code.append(body);
			// See if anything returned. If not, add a return state
			// Temporary solution
			if (body.indexOf("return") == -1) {
				code.append("return null;");
			}
			code.append("}");
			engine.createANewMethod(e.getEntityType(), false, false,
					methodNameOfSingleCompositeAction, code.toString());
		}
		String methodNameOfTaskQueueAdder = "_proxy"
				+ (ConfigParameters.methodIndex++);
		code = new StringBuffer();
		if (methodNameOfSingleCompositeAction == null) {
			// normal task queue fashion
			code.append("public void ").append(methodNameOfTaskQueueAdder)
					.append("(){").append(body).append("}");
		} else {
			// a single code block, construct the task queue automatically
			code.append("public void ").append(methodNameOfTaskQueueAdder)
					.append("(){").append("TASKQUEUE.add(\"").append(
							methodNameOfSingleCompositeAction).append("()\");")
					.append("}");
		}
		engine.createANewMethod(e.getEntityType(), false, false,
				methodNameOfTaskQueueAdder, code.toString());
		if (true) {
			/** Set method names */
			behavior
					.setBehavEquationMethodName(methodNameOfExcitationComputationCode);
			ba.setMethodNameToSetupTaskQueue(methodNameOfTaskQueueAdder);
			/**
			 * Update the entities' definition - Not necessary, since it is done
			 * in creating new method calls
			 */
			try {
				engine.system.updateEntity(e.getEntityType(), e);
			} catch (Exception ex) {
				MessageUtils.debug(this, "addBehaviorToNetwork", ex);
				throw new RuntimeException(ex);
			}
			/** Setup behavior action */
			behavior.setBehaviorAction(ba);
			/** Add to the current behavior network */
			e.getBehaviorNetwork().addBehavior(behavior, position);
		}
	}

	/**
	 * Update the behavior network of the specified entity because of the change
	 * of individual behaviors.
	 * 
	 * <p>
	 * 1. Update the method for computing excitation.<br/>
	 * The excitation computation code is wrapped in a method which is created
	 * in the current entity. The method name is saved in the behavior which can
	 * be invoked later.
	 * </p>
	 * 
	 * <p>
	 * 2. Update the method of behavior actions. <br>
	 * If the action contains no explicit task queue implementation (no
	 * "TASKQUEUE.add" is found), the code block will be treated as the body of
	 * a single composite action, which will be added to the task queue
	 * automatically. Otherwise, normal task queue implementation is used, the
	 * above step is not involved.
	 * </p>
	 * 
	 * @param e
	 *            The target entity
	 * @param id
	 *            The behavior's id
	 * @param updateExcitM
	 *            Update the excitation method
	 * @param updateActM
	 *            Update the action method
	 */
	public void updateBehaviorNetwork(BNCategory e, int id,
			boolean updateExcitM, boolean updateActM) throws Exception {
		/** No need to update */
		if (!updateExcitM && !updateActM) {
			return;
		}
		/** Get behavior and its location */
		Behavior behavior = repository.getBehaviorById(e, id);
		if (behavior == Behavior.NO_BEHAVIOR)
			throw new RuntimeException(
					"The behavior with the specified name does not exist.");
		/**
		 * NOTE THAT BEHAVIOR IN BEHAVIOR NETWORK IS SAME AS THAT IN BEHAVIOR
		 * REPOSITORY.
		 */
		/** Get engine */
		AppEngine engine = AppEngine.getInstance();
		String methodNameOfExcitationComputationCode = null;
		StringBuffer code = null;
		if (updateExcitM) {
			/** Initialize the behavior excitation computation code */
			methodNameOfExcitationComputationCode = "_proxy"
					+ (ConfigParameters.methodIndex++);
			code = new StringBuffer();
			code.append("public double ").append(
					methodNameOfExcitationComputationCode).append("(){")
					.append(behavior.getBehaviorEquation()).append("}");
			engine.createANewMethod(e.getEntityType(), false, false,
					methodNameOfExcitationComputationCode, code.toString());
		}

		BehaviorAction ba = null;
		String methodNameOfTaskQueueAdder = null;
		if (updateActM) {
			/** Get behavior action */
			ba = (BehaviorAction) engine.getAction(id, behavior
					.getBehaviorActionName());
			/** Create a method to setup the task queue setting up helper */
			// If no calls of "TASKQUEUE.add", then all code blocks should be
			// treated as single composite action
			String body = ba.getActionString();
			String methodNameOfSingleCompositeAction = null;
			if (body.indexOf("TASKQUEUE.add") == -1) {
				// create a method which represents the composite action
				methodNameOfSingleCompositeAction = "_proxy"
						+ (ConfigParameters.methodIndex++);
				code = new StringBuffer();
				code.append("public String ").append(
						methodNameOfSingleCompositeAction).append("(){");
				code.append(body);
				// See if anything returned. If not, add a return state
				// Temporary solution
				if (body.indexOf("return") == -1) {
					code.append("return null;");
				}
				code.append("}");
				engine.createANewMethod(e.getEntityType(), false, false,
						methodNameOfSingleCompositeAction, code.toString());
			}
			methodNameOfTaskQueueAdder = "_proxy"
					+ (ConfigParameters.methodIndex++);
			code = new StringBuffer();
			if (methodNameOfSingleCompositeAction == null) {
				// normal task queue fashion
				code.append("public void ").append(methodNameOfTaskQueueAdder)
						.append("(){").append(body).append("}");
			} else {
				// a single code block, construct the task queue automatically
				code.append("public void ").append(methodNameOfTaskQueueAdder)
						.append("(){").append("TASKQUEUE.add(\"").append(
								methodNameOfSingleCompositeAction).append(
								"()\");").append("}");
			}
			engine.createANewMethod(e.getEntityType(), false, false,
					methodNameOfTaskQueueAdder, code.toString());
		}

		if (updateExcitM || updateActM) {
			/**
			 * Update the entities' definition - Not necessary, since it is done
			 * in creating new method calls
			 */
			try {
				engine.system.updateEntity(e.getEntityType(), e);
			} catch (Exception ex) {
				MessageUtils.debug(this, "addBehaviorToNetwork", ex);
				throw new RuntimeException(ex);
			}
		}

		if (updateExcitM) {
			/** Set method names */
			behavior
					.setBehavEquationMethodName(methodNameOfExcitationComputationCode);
		}

		if (updateActM) {
			/** Set method names */
			ba.setMethodNameToSetupTaskQueue(methodNameOfTaskQueueAdder);
			/** Setup behavior action */
			behavior.setBehaviorAction(ba);
		}

	}

	/**
	 * Add the specified behavior into the entity.
	 * 
	 * 
	 * @param e
	 *            The entity the behavior added to
	 * @param behaviorName
	 *            The behavior to add to the entity
	 * @param row
	 *            The behavior's position. When row = -1, the behavior's
	 *            position is determined by the entity's behavior network.
	 * @param column
	 *            The behavior's position When column = -1, the behavior's
	 *            position is determined by the entity's behavior network.
	 * 
	 */
	public void addBehaviorToEntity(Entity e, String behaviorName, int row,
			int column) {
		Behavior behavior = repository.getBehaviorByName((BNCategory) e,
				behaviorName);
		if (behavior == Behavior.NO_BEHAVIOR) {
			return;
		}
		BehaviorNetwork network = ((BNCategory) e).getBehaviorNetwork();
		BehaviorPosition position = null;
		if (row >= BehaviorPosition.NETWORK_SQUARES || row < 0
				|| column >= BehaviorPosition.NETWORK_SQUARES || column < 0) {
			position = network.getFirstUnoccupiedPosition();
		} else {
			position = BehaviorPosition.getPosition(row, column);
		}
		network.addBehavior(behavior, position);
	}

	/**
	 * Check the behavior network of each entity of the specified category. In
	 * the behavior network, each behavior is checked to see whether it refers
	 * to the deleted (dangling) methods.
	 * 
	 * @param catObject
	 *            The category object with the method definition
	 * @param method
	 *            The method to remove.
	 * @return Whether the method(s) are removed successfully
	 * @throws Exception
	 *             Exception throws during the steps.
	 */
	public boolean verifyBehaviorNetwork(BNCategory catObject, CMethod method)
			throws Exception {
		AppEngine engine = AppEngine.getInstance();
		List entityList = engine.system.getAvailableEntities();
		String catName = catObject.getEntityType();
		Category category = null;
		for (int i = 0; i < entityList.size(); i++) {
			if ((category = (Category) entityList.get(i)).getEntityType()
					.equals(catName)) {
				if (category instanceof BNCategory) {
					BNCategory bnc = (BNCategory) category;
					BehaviorNetwork bn = bnc.getBehaviorNetwork();
					List behaviors = bn.getBehaviorList();
					if (!behaviors.isEmpty()) {
						Behavior[] bs = new Behavior[behaviors.size()];
						bs = (Behavior[]) behaviors.toArray(bs);
						verifyBehaviorNetwork(bnc, bs);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Check whether the behavior network of the specified entity is properly
	 * defined. The behaviors of behavior network are often related with
	 * user-defined methods and properties. In the case of updating user-defined
	 * methods and/or properties, the behavior network should be checked.
	 * 
	 * Once a behavior does not pass the verification, it will be removed from
	 * the behavior network.
	 * 
	 * @param entity
	 *            Entity to check
	 * @param behaviors
	 *            List of behaviors
	 * @return Whether the behavior network is legally defined.
	 * @throws Exception
	 *             occur
	 */
	private boolean verifyBehaviorNetwork(BNCategory entity,
			Behavior behaviors[]) throws Exception {

		AppEngine engine = AppEngine.getInstance();
		boolean finalRet = true;
		if (behaviors != null) {
			for (int k = 0; k < behaviors.length; k++) {
				// Verify the validity of behavior definition ahead
				boolean ret = false;
				try {
					ret = engine.bnEditor.verifyExcitationActionMethod(entity,
							behaviors[k].getMyId());
				} catch (Exception e) {
					// Errors
					ret = false;
				} finally {
					if (!ret) {
						// Remove behavior from the behavior network and
						entity.getBehaviorNetwork().removeBehavior(
								entity.getBehaviorNetwork().getPosition(
										behaviors[k]));
						// Remove behavior from navigation panel
						engine.navPanel.removeBehavior(behaviors[k].getMyId(),
								entity);
					}
					finalRet &= ret;
				}
			}
		}

		return finalRet;
	}

	/**
	 * Update the edges in the current behavior network
	 * 
	 * @param edges
	 *            The new edges to be set
	 */
	public void updateEdges(Edge edges[]) {

		((BNCategory) (AppEngine.getInstance().appManager.currentApp.currentEntity))
				.getBehaviorNetwork().updateEdges(edges);
	}

	/**
	 * Remove all defined behaviors.
	 * 
	 * <p>
	 * This method is used when the application is shutdown.
	 * </p>
	 * 
	 */
	public void removeAllBehaviors() {
		repository.removeAllBehaviors();
	}

	/**
	 * Remove the behavior from behavior repository
	 * 
	 * @param owner
	 *            Owner of the behavior
	 * @param id
	 *            The ID of the behavior to remove
	 */
	public void removeBehavior(BNCategory bn, int id) {
		repository.removeBehavior(bn, id);
	}

}
