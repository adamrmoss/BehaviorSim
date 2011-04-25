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

import java.awt.Graphics;

import javax.swing.JSlider;

import sim.ui.SimulationPad;
import sim.ui.SimulationView;
import sim.util.MessageUtils;
import sim.util.SimException;

/**
 * Thread for drawing simulation results of each time step according to the
 * specified time interval.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class SimulationDisplayThread implements AppRunnable {

	// Current simulation time
	private int simulationTime = 0;

	// Current time interval between two time steps
	private int delta_t = 50;

	// Application engine
	private AppEngine engineRef = AppEngine.getInstance();
	
	// Simulation data
	private SimulationData data = engineRef.getSimulationData();

	/**
	 * Simulation pad
	 */
	private SimulationPad pad = null;

	/**
	 * Whether the thread should be suspended
	 */
	private boolean isSuspended = true;

	/**
	 * Progress bar
	 */
	private JSlider progressSlider = null;

	/**
	 * The simulation view
	 */
	private SimulationView t = null;

	/**
	 * Constructor
	 * 
	 * @param engine
	 * @param p
	 * @param slider
	 */
	public SimulationDisplayThread(SimulationPad p,
			SimulationView sv, JSlider slider) {
		super();

		pad = p;
		t = sv;
		progressSlider = slider;
		init();
	}

	/**
	 * Set the time interval
	 */
	public void setDeltaT(int dt) {
		delta_t = dt;
	}

	/**
	 * Initialize the time interval
	 * 
	 * @param params
	 *            Configuration parameter object
	 */
	public void init() {
		delta_t = engineRef.system.systemParameters.getTimeStepInterval();
	}

	/**
	 * Run
	 */
	public void run() {

		// Total time ticks
		int totaltime = engineRef.getTotalTimeticks();

		// Number of runs
		int runs = -1;

		while (!isSuspended) {
			if (!data.isDisplaySwitched()) { // For main data
				if (!data.isMainCompleted()) { // The main data is not already
					// computed
					t.showProgressWin(true); // Show the progress window
					continue; // Wait for the computation
				}
				t.showProgressWin(false);
			} else { // For backup data
				if (!data.isBackupCompleted()) { // The backup data is not
					// already computed
					t.showProgressWin(true); // Show the progress window
					continue; // Wait for the computation
				}
				t.showProgressWin(false);
			}

			// Increment the number of runs
			runs++;

			// Draw the simulation results
			try {
				for (; simulationTime <= totaltime - 1 && !isSuspended; simulationTime++) {

					// Set the label of current time step
					t.setTimeStepLabel(runs * totaltime + simulationTime);

					// Draw the simulation result
					drawSimulation(simulationTime, simulationTime + 1);

					// Wait for a while
					try {
						Thread.sleep(delta_t);
					} catch (InterruptedException e) {
					}

					// Check for simulation suspension
					if (isSuspended) {
						MessageUtils.debug(this, "run",
								"Animation Tread Stopped.");
						return;
					}

					// Update the progress slider
					if (progressSlider != null)
						progressSlider.setValue(simulationTime);					
					t.setDisplayTime(simulationTime);

				}
			} catch (Exception e) {
				MessageUtils.debugAndDisplay(this, "run", new SimException(
						"SIM-S-TF001A",
						"An exception arised during simulation", e));
				return;
			}

			// Reset the data to be un-computed
			if (!data.isDisplaySwitched()) { // For main data
				data.setMainCompleted(false);
			} else { // For backup data
				data.setBackupCompleted(false);
			}

			// Draw the other simulation results
			data.setDisplaySwitched(!data.isDisplaySwitched());

			// Reset the simulation time
			simulationTime = 0;
			t.setDisplayTime(simulationTime);

			// Reset the slider to 0
			progressSlider.setValue(0);

		}

	}

	/**
	 * Set the suspension flag
	 */
	public void setSuspended(boolean suspended) {
		isSuspended = suspended;
	}

	/**
	 * Draw the simulation result of the next time step
	 * 
	 * @param tprev
	 *            Previous time step
	 * @param tnext
	 *            Next time step
	 */
	private void drawSimulation(int tprev, int tnext) {
		Graphics g = pad.getSimulationImage().getGraphics();
		// List entityList = engineRef.getAvailableEntities();
		// for(int m = 0; m < entityList.size(); m++){
		// Entity entity = (Entity)entityList.get(m);
		// Point prevPosition = engineRef.getEntityPosition(entity, tprev),
		// nextPosition = engineRef.getEntityPosition(entity, tnext);
		// if(prevPosition == null || nextPosition == null) {
		// continue;
		// }
		// Rectangle rP = entity.getDrawRect(prevPosition),
		// rN = entity.getDrawRect(nextPosition);
		// Rectangle clipRect = rP.union(rN);
		// clipRect.grow(50, 50); // FIX IT!!!!!!!!!!!!!!!!!!!!, Previous:
		// grow(1, 1)
		// if(!(tnext == 0 && 0 == tprev))
		// g.setClip(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		pad.paintSimulation(g, tnext/* tprev */);
		// }
		g.dispose();
		pad.repaint();
	}

	/**
	 * Set the current simulation time and draw it
	 * 
	 * @param time
	 *            The current simulation time
	 * @param updateDispTime 
	 * 			  Whether the original display time should be reset
	 */
	public void setSimulationTime(int time, boolean updateDispTime) {
		if (time >= 0 && time < engineRef.getTotalTimeticks()) {
			int prevTime = simulationTime;
			simulationTime = time;
			if (updateDispTime)
				t.setDisplayTime(time);
			drawSimulation(prevTime, simulationTime);
		}
	}
	
	/**
	 * Set the current simulation time
	 * 
	 * @param time
	 *            The current simulation time
	 * @param updateDispTime 
	 * 			  Whether the original display time should be reset
	 */
	public void setSimulationTime2(int time, boolean updateDispTime) {
		if (time >= 0 && time < engineRef.getTotalTimeticks()) {
			simulationTime = time;
			if (updateDispTime)
				t.setDisplayTime(time);
		}
	}
}
