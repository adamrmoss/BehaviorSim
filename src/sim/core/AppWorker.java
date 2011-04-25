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

package sim.core;

import java.awt.EventQueue;

/**
 * A Worker Thread class used in Swing Programs to execute long during Threads
 * in the background and update the GUI in the correct Swing Thread.
 * 
 * @author Fasheng Qiu
 */
public class AppWorker extends Thread {
	private Object setupObject;
	private Object executeObject;
	private Object tearDownObject;

	/**
	 * Constructs a new Worker Thread instance. Use the start method to start
	 * its execution.
	 */
	protected AppWorker() {
	}

	/**
	 * This is executed directly after startup and runs in the Swing Thread.
	 */
	public void setUp() {
	}

	/**
	 * This method is executed in its own Thread.
	 */
	public void execute() {
	}

	/**
	 * This method is executed after the execute method has finished its
	 * execution.
	 */
	public void tearDown() {
	}

	/**
	 * Sets the return value of the setup method.
	 * 
	 * @param setupObject
	 *            the Object that is created in the setUp method
	 */
	public void setSetupObject(Object setupObject) {
		this.setupObject = setupObject;
	}

	/**
	 * Sets the return value of the execute method.
	 * 
	 * @param executeObject
	 *            the Object that is created in the execute method
	 */
	public void setExecuteObject(Object executeObject) {
		this.executeObject = executeObject;
	}

	/**
	 * Sets the return value of the tearDown method.
	 * 
	 * @param tearDownObject
	 *            the Object that is created in the setUp method
	 */
	public void setTearDownObject(Object tearDownObject) {
		this.tearDownObject = tearDownObject;
	}

	/**
	 * Returns the setupObject.
	 * 
	 * @return the setupObject
	 */
	public Object getSetupObject() {
		return setupObject;
	}

	/**
	 * Returns the executeObject.
	 * 
	 * @return the executeObject
	 */
	public Object getExecuteObject() {
		return executeObject;
	}

	/**
	 * Returns the tearDownObject.
	 * 
	 * @return the tearDownObject
	 */
	public Object getTearDownObject() {
		return tearDownObject;
	}

	/**
	 * Executes this Workerthreads methods.
	 * <p/>
	 * You should usually not call the start method from the EventQueue Thread,
	 * since this results in an ordinary sequential execution of the methods.
	 */
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setUp();
			}
		});

		execute();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				tearDown();
			}
		});

	}
}
