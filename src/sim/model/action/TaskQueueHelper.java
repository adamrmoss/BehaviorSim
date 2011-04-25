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

import sim.core.AppEngine;
import sim.core.ConfigParameters;
import sim.model.entity.Entity;
import sim.util.MethodUtils;

/**
 * Task queue helper for the action of each behavior, which is based on the
 * behavior network theory.
 * 
 * <p>
 * This class contains specific logics of establishing task queue of the
 * behavior action, see <code>BehaviorAction</code>
 * </p>
 * .
 * 
 * <p>
 * It is only supposed to be used internally.
 * </p>
 * 
 * @author Fasheng Qiu
 * 
 */
public class TaskQueueHelper {

	/** The behavior action object which this queue belongs to */
	private BehaviorAction bAction = null;

	/** Constructor */
	TaskQueueHelper(BehaviorAction bAction) {
		this.bAction = bAction;
	}

	/**
	 * Add an sub-action into the behavior action object. The sub-action is
	 * specified in the string format, which contains a single method call with
	 * actual parameter values.
	 * 
	 * <p>
	 * This method treat the sub-action as an instance of
	 * <code>CompositeAction</code>, and put the action into the children list (
	 * <code>BehaviorAction</code>), which will be executed step-by-step, each
	 * step one action is executed.
	 * </p>
	 * 
	 * <p>
	 * The action string will be treated in two ways: No.1, a simple method
	 * call, either user-defined or system-built-in such as "move",
	 * "turnWithAngularSpeed", will be added to the task queue directly by
	 * creating a composite action to wrap it. No.2, for composite method call,
	 * such as "moveForDistance" and "turnForAngle", several simple method calls
	 * will be created and added into the task queue by creating corresponding
	 * composite action.
	 * </p>
	 * 
	 * <p>
	 * For the user-defined method call, it is up to the user to decide whether
	 * a speed vector will be returned. If no vector is returned, the method
	 * call will be executed, but will not join the vector summation of the
	 * result speed vector.
	 * </p>
	 * 
	 * @param actionString
	 *            The actual method call of the sub-action, which will be
	 *            executed in the simulation, see <code>CompositeAction</code>
	 */
	public void add(String methodCall) {
		if (methodCall == null
				|| (methodCall = methodCall.trim()).length() == 0)
			return;
		/** Composite method calls */
		if (methodCall.startsWith("moveForDistance")
				|| methodCall.startsWith("moveForwardForDistance")) {
			String parameterValues = methodCall.substring(methodCall
					.indexOf('(') + 1, methodCall.indexOf(')'));
			_addCompositeActionForMoveForDistance(parameterValues);
		} else if (methodCall.startsWith("moveBackwardForDistance")) {
			String parameterValues = methodCall.substring(methodCall
					.indexOf('(') + 1, methodCall.indexOf(')'));
			_addCompositeActionForMoveBackwardForDistance(parameterValues);
		} else if (methodCall.startsWith("turnForAngle")
				|| methodCall.startsWith("turnLeftForAngle")) {
			String parameterValues = methodCall.substring(methodCall
					.indexOf('(') + 1, methodCall.indexOf(')'));
			_addCompositeActionForTurnForAngle(parameterValues);
		} else if (methodCall.startsWith("turnRightForAngle")) {
			String parameterValues = methodCall.substring(methodCall
					.indexOf('(') + 1, methodCall.indexOf(')'));
			_addCompositeActionForTurnRightForAngle(parameterValues);
		} else {
			/** Simple method calls */
			_addCompositeAction(methodCall);
		}
	}

	/**
	 * Create several composite actions to wrap the method call
	 * "moveForDistance".
	 * 
	 * @param actionString
	 *            The single call to be wrapped
	 */
	private void _addCompositeActionForMoveForDistance(String actionString) {
		/** Get parameters */
		AppEngine ae = AppEngine.getInstance();
		Entity entity = ae.appManager.currentApp.currentEntity;
		String speedStr = actionString.substring(0, actionString.indexOf(','))
				.trim();
		double speed = 0.0D;
		try {
			/** Constant values ? */
			speed = Double.parseDouble(speedStr);
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				speed = entity.getDoubleValue(speedStr);
			} catch (Exception ee) {
				try {
					String param = speedStr;
					speed = ((Number) MethodUtils.invokeExactMethod(entity,
							"get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '" + speedStr
							+ "' can not be resovled");
				}
			}
		}
		// String directionStr =
		// actionString.substring(actionString.indexOf(',')+1,
		// actionString.lastIndexOf(',')).trim();
		// double value = 0.0;
		// try { /** Constant values ? */
		// value = Double.parseDouble(directionStr);
		// } catch(Exception e) {
		// /** Property of current entity ?*/
		// try {
		// value = entity.getDoubleValue(directionStr);
		// } catch(Exception ee){
		// try {
		// String param = directionStr;
		// value = ((Number)MethodUtils.invokeExactMethod(entity,
		// "get"+param.substring(0,1).toUpperCase()+param.substring(1),
		// null)).doubleValue();
		// } catch(Exception eee) {
		// throw new
		// RuntimeException("The parameter '"+directionStr+"' can not be resovled");
		// }
		// }
		// }
		double distance = 0.0D;
		try {
			/** Constant values ? */
			distance = Double.parseDouble(actionString.substring(
					actionString.lastIndexOf(',') + 1).trim());
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				distance = entity.getDoubleValue(actionString.substring(
						actionString.lastIndexOf(',') + 1).trim());
			} catch (Exception ee) {
				try {
					String param = actionString.substring(
							actionString.lastIndexOf(',') + 1).trim();
					distance = ((Number) MethodUtils.invokeExactMethod(entity,
							"get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '"
							+ actionString.substring(actionString
									.lastIndexOf(',') + 1)
							+ "' can not be resovled");
				}
			}
		}

		/** Steps */
		int steps = (int) Math.abs(distance / speed);

		/** Setup composite actions */
		for (int i = 0; i < steps; i++) {

			/** The action name */
			String name = this.bAction.getActionName()
					+ (ConfigParameters.methodIndex++);

			/** Create a composite action */
			CompositeAction composite = new CompositeAction("move(" + speed
					+ ", direction)");
			composite.setActionName(name);

			/** Add the action into the system */
			this.bAction.addChildAction(composite);
		}

	}

	/**
	 * Create several composite actions to wrap the method call "turnForAngle".
	 * 
	 * @param actionString
	 *            The single call to be wrapped
	 */
	private void _addCompositeActionForTurnForAngle(String actionString) {
		/** Get parameters */
		AppEngine ae = AppEngine.getInstance();
		Entity entity = ae.appManager.currentApp.currentEntity;
		String angleStr = actionString.substring(0, actionString.indexOf(','))
				.trim();
		double angleSpeed = 0.0D;
		try {
			/** Constant values ? */
			angleSpeed = Double.parseDouble(angleStr);
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				angleSpeed = entity.getDoubleValue(angleStr);
			} catch (Exception ee) {
				try {
					String param = angleStr;
					angleSpeed = ((Number) MethodUtils.invokeExactMethod(
							entity, "get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '" + angleStr
							+ "' can not be resovled");
				}
			}
		}
		double totalAngle = 0.0D;
		try {
			/** Constant values ? */
			totalAngle = Double.parseDouble(actionString.substring(
					actionString.lastIndexOf(',') + 1).trim());
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				totalAngle = entity.getDoubleValue(actionString.substring(
						actionString.lastIndexOf(',') + 1).trim());
			} catch (Exception ee) {
				try {
					String param = actionString.substring(
							actionString.lastIndexOf(',') + 1).trim();
					totalAngle = ((Number) MethodUtils.invokeExactMethod(
							entity, "get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '"
							+ actionString.substring(actionString
									.lastIndexOf(',') + 1)
							+ "' can be resovled");
				}
			}
		}

		/** Steps */
		int steps = (int) Math.abs(totalAngle / angleSpeed);

		/** Setup composite actions */
		for (int i = 0; i < steps; i++) {

			/** The action name */
			String name = this.bAction.getActionName()
					+ (ConfigParameters.methodIndex++);

			/** Create a composite action */
			CompositeAction composite = new CompositeAction("turn("
					+ angleSpeed + ")");
			composite.setActionName(name);

			/** Add the action into the system */
			this.bAction.addChildAction(composite);

		}
	}

	/**
	 * Create several composite actions to wrap the method call
	 * "moveBackwardForDistance".
	 * 
	 * @param actionString
	 *            The single call to be wrapped
	 */
	private void _addCompositeActionForMoveBackwardForDistance(
			String actionString) {
		/** Get parameters */
		AppEngine ae = AppEngine.getInstance();
		Entity entity = ae.appManager.currentApp.currentEntity;
		String speedStr = actionString.substring(0, actionString.indexOf(','))
				.trim();
		double speed = 0.0D;
		try {
			/** Constant values ? */
			speed = Double.parseDouble(speedStr);
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				speed = entity.getDoubleValue(speedStr);
			} catch (Exception ee) {
				try {
					String param = speedStr;
					speed = ((Number) MethodUtils.invokeExactMethod(entity,
							"get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '" + speedStr
							+ "' can not be resovled");
				}
			}
		}
		// String directionStr =
		// actionString.substring(actionString.indexOf(',')+1,
		// actionString.lastIndexOf(',')).trim();
		// double value = 0.0;
		// try { /** Constant values ? */
		// value =Double.parseDouble(directionStr);
		// } catch(Exception e) {
		// /** Property of current entity ?*/
		// try {
		// value =entity.getDoubleValue(directionStr);
		// } catch(Exception ee){
		// try {
		// String param = directionStr;
		// value =((Number)MethodUtils.invokeExactMethod(entity,
		// "get"+param.substring(0,1).toUpperCase()+param.substring(1),
		// null)).doubleValue();
		// } catch(Exception eee) {
		// throw new
		// RuntimeException("The parameter '"+directionStr+"' can not be resovled");
		// }
		// }
		// }
		double distance = 0.0D;
		try {
			/** Constant values ? */
			distance = Double.parseDouble(actionString.substring(
					actionString.lastIndexOf(',') + 1).trim());
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				distance = entity.getDoubleValue(actionString.substring(
						actionString.lastIndexOf(',') + 1).trim());
			} catch (Exception ee) {
				try {
					String param = actionString.substring(
							actionString.lastIndexOf(',') + 1).trim();
					distance = ((Number) MethodUtils.invokeExactMethod(entity,
							"get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '"
							+ actionString.substring(actionString
									.lastIndexOf(',') + 1)
							+ "' can not be resovled");
				}
			}
		}

		/** Steps */
		int steps = (int) Math.abs(distance / speed);

		/** Setup composite actions */
		for (int i = 0; i < steps; i++) {

			/** The action name */
			String name = this.bAction.getActionName()
					+ (ConfigParameters.methodIndex++);

			/** Create a composite action */
			CompositeAction composite = new CompositeAction("moveBackward("
					+ speed + ", direction)");
			composite.setActionName(name);

			/** Add the action into the system */
			this.bAction.addChildAction(composite);
		}

	}

	/**
	 * Create several composite actions to wrap the method call
	 * "turnRightForAngle".
	 * 
	 * @param actionString
	 *            The single call to be wrapped
	 */
	private void _addCompositeActionForTurnRightForAngle(String actionString) {
		/** Get parameters */
		AppEngine ae = AppEngine.getInstance();
		Entity entity = ae.appManager.currentApp.currentEntity;
		String angleStr = actionString.substring(0, actionString.indexOf(','))
				.trim();
		double angleSpeed = 0.0D;
		try {
			/** Constant values ? */
			angleSpeed = Double.parseDouble(angleStr);
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				angleSpeed = entity.getDoubleValue(angleStr);
			} catch (Exception ee) {
				try {
					String param = angleStr;
					angleSpeed = ((Number) MethodUtils.invokeExactMethod(
							entity, "get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '" + angleStr
							+ "' can not be resovled");
				}
			}
		}
		double totalAngle = 0.0D;
		try {
			/** Constant values ? */
			totalAngle = Double.parseDouble(actionString.substring(
					actionString.lastIndexOf(',') + 1).trim());
		} catch (Exception e) {
			/** Property of current entity ? */
			try {
				totalAngle = entity.getDoubleValue(actionString.substring(
						actionString.lastIndexOf(',') + 1).trim());
			} catch (Exception ee) {
				try {
					String param = actionString.substring(
							actionString.lastIndexOf(',') + 1).trim();
					totalAngle = ((Number) MethodUtils.invokeExactMethod(
							entity, "get" + param.substring(0, 1).toUpperCase()
									+ param.substring(1), null)).doubleValue();
				} catch (Exception eee) {
					throw new RuntimeException("The parameter '"
							+ actionString.substring(actionString
									.lastIndexOf(',') + 1)
							+ "' can be resovled");
				}
			}
		}

		/** Steps */
		int steps = (int) Math.abs(totalAngle / angleSpeed);

		/** Setup composite actions */
		for (int i = 0; i < steps; i++) {

			/** The action name */
			String name = this.bAction.getActionName()
					+ (ConfigParameters.methodIndex++);

			/** Create a composite action */
			CompositeAction composite = new CompositeAction("turnRight("
					+ angleSpeed + ")");
			composite.setActionName(name);

			/** Add the action into the system */
			this.bAction.addChildAction(composite);

		}
	}

	/**
	 * Create a composite action to wrap the single method call.
	 * 
	 * @param actionString
	 *            The single call to be wrapped
	 * @return The name of the created composite action
	 */
	private void _addCompositeAction(String actionString) {

		/** The action name */
		String name = this.bAction.getActionName()
				+ (ConfigParameters.methodIndex++);

		/** Create a composite action */
		CompositeAction composite = new CompositeAction(actionString);
		composite.setActionName(name);

		/** Add the action into the system */
		this.bAction.addChildAction(composite);

	}
}
