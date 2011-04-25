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

import sim.core.AppEngine;
import sim.core.ConfigParameters;
import sim.model.entity.BNCategory;
import sim.model.entity.CMethod;

public class SystemDynamicMechanism implements IMechanism {

	/** Get engine */
	AppEngine engine = AppEngine.getInstance();


	/**
	 * The associated method for the dynamics
	 * 
	 */
	private CMethod code;

	/**
	 * For copy function only
	 */
	public SystemDynamicMechanism() {
		code = new CMethod();
	}

	/**
	 * Register the method on the current computing entity. The actual
	 * implementation is that, the method is registered on the category template
	 * object and then the current computing entity is updated according to this
	 * template object. Note that, other entities of the same category are not
	 * updated.
	 * 
	 * If there is any error in the method definition, an exception will be
	 * thrown.
	 * 
	 */
	public void set(BNCategory e, String src) throws Exception {
		/** Get the current computing entity */
		if (e == null)
			throw new RuntimeException(
					"Current computing entity can not be figured out.");

		/** Verify the method first */
		verify(e, src);

		/** No code is provided */
		if (src == null || src.trim().equals("")) {
			this.code.src = null;
			this.code.name = null;
			return;
		}

		/**
		 * Register the method representing system dynamics on the category
		 * object
		 */
		String methodNameOfSystemDyanmics = "_proxy"
				+ (ConfigParameters.methodIndex++);
		StringBuffer code = new StringBuffer();
		code.append("public void ").append(methodNameOfSystemDyanmics).append(
				"(){").append(src).append("}");
		engine.appManager.currentApp.dm.createMethod(e.getEntityType(), false,
				methodNameOfSystemDyanmics, code.toString());

		/** Save the method name */
		this.code.src = src;
		this.code.name = methodNameOfSystemDyanmics;

		/** Update the current computing entity */
		engine.system.updateEntity(e.getEntityType(), e);

	}

	/**
	 * Verify whether the method can be defined on the current computing entity.
	 * 
	 * If there is any error in the method definition, an exception will be
	 * thrown.
	 * 
	 * @return True if the method definition for dynamics is correct.
	 */
	private boolean verify(BNCategory e, String src) throws Exception {

		/** No code is provided */
		if (src == null || src.trim().equals("")) {
			return true;
		}

		/**
		 * Verify the method representing system dynamics on the category object
		 */
		String methodNameOfSystemDyanmics = "_proxy"
				+ (ConfigParameters.methodIndex++);
		StringBuffer code = new StringBuffer();
		code.append("public void ").append(methodNameOfSystemDyanmics).append(
				"(){").append(src).append("}");

		/** Verify the method through dynamic manager */
		engine.appManager.currentApp.dm.verifyMethodDef(e.getEntityType(), code
				.toString());

		return true;

	}

	/**
	 * Return the method definition of the general dynamics
	 * 
	 * @return the method definition of the general dynamics
	 */
	public String getCode() {
		return code.src;
	}

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
//		this.timeTick = timeStep % AppEngine.getInstance().getTotalTimeticks();
	}

	/**
	 * Cooperative action selection mechanism
	 */
	public void execute() {
		if (code.name == null)
			return;
		try {
			engine.evaluateAction(engine.appManager.currentApp.currentEntity,
					code.name + "()");
			engine.system.env
					.handleEnvironmentBounds(engine.appManager.currentApp.currentEntity);
		} catch (Exception e) {

			e.printStackTrace();
			sim.util.MessageUtils.debug(this, "execute", e);

		}

	}

	/**
	 * Return a copy of this mechanism
	 */
	public IMechanism copy() {
		SystemDynamicMechanism m = new SystemDynamicMechanism();
		if (code != null)
			m.code = code.copy();
		return m;
	}
}
