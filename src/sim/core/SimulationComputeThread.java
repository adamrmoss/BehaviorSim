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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import sim.model.entity.Entity;
import sim.model.entity.EntityRecord;
import sim.ui.SimulationView;
import sim.util.MessageUtils;
import sim.util.SimException;

/**
 * Computation thread of the simulation data of all the available entities
 * 
 * 
 * @author Owner QFS
 * 
 */
public class SimulationComputeThread extends Object implements AppRunnable {

	// Application engine
	private AppEngine engineRef = AppEngine.getInstance();
	
	// Simulation data
	private SimulationData data = engineRef.getSimulationData();

	/**
	 * Dialog to show computation progress
	 */
	private JDialog noticeDialog = null;

	/**
	 * Current progress
	 */
	private JLabel progress = new JLabel("0");

	/**
	 * Notification for progress
	 */
	private JLabel notice = new JLabel(
			" percent(s) completed..., please wait a moment!");

	/**
	 * Whether the computation is stopped
	 */
	private boolean stop = false;

	/**
	 * Simulation view
	 */
	private SimulationView view;

	/**
	 * Whether the computation is for the first run
	 */
	private boolean initialRun = true;

	/**
	 * Constructor
	 * 
	 * @param parentPanel
	 *            The simulation view that instantiates this thread
	 * @param engineRef
	 *            The application engine
	 */
	public SimulationComputeThread(final SimulationView parentPanel) {

		this.view = parentPanel;
		Container c = parentPanel;
		while (c != null && !(c instanceof JFrame)) {
			c = c.getParent();
		}

		noticeDialog = new JDialog((JFrame) c, "Computing simulation data...");
		noticeDialog.setSize(new Dimension(300, 80));
		noticeDialog.setModal(true);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		noticeDialog.setLocation((d.width - 260) / 2, (d.height - 80) / 2);

		SpringLayout layout = new SpringLayout();
		noticeDialog.getContentPane().setLayout(layout);
		noticeDialog.getContentPane().add(progress);
		noticeDialog.getContentPane().add(notice);
		noticeDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				new Thread() {
					public void run() {

						setSuspended(true);
						setShow(false);
						parentPanel.setStopped(true);

					}
				}.start();

			}
		});

		layout.putConstraint(SpringLayout.WEST, progress, 10,
				SpringLayout.WEST, noticeDialog);
		layout.putConstraint(SpringLayout.NORTH, progress, 10,
				SpringLayout.NORTH, noticeDialog);

		layout.putConstraint(SpringLayout.NORTH, notice, 10,
				SpringLayout.NORTH, noticeDialog);
		layout.putConstraint(SpringLayout.WEST, notice, 5, SpringLayout.EAST,
				progress);

	}

	/**
	 * Run to compute the simulation data
	 */
	public void run() {

		// Show Progress dialog
		new Thread() {
			public void run() {
				noticeDialog.show();
			}
		}.start();

		// Get available entities
		java.util.List entityList = engineRef.system.getAvailableEntities();
		int prevS = entityList.size(), curS = prevS;

		// Total ticks
		int totalTimeTicks = engineRef.getTotalTimeticks();

		// Percentage
		int percent = totalTimeTicks / 100;

		// Begin the computation
		while (!stop) {
			if (!data.isComputeSwitched()) { // For main data
				if (data.isMainCompleted()) { // The main data is already
					// computed
					data.setComputeSwitched(!data.isComputeSwitched()); // Switch
					// to
					// compute
					// backup
					// data
					continue;
				}
				data.setMainCompleted(false); // The main data is not computed.
				// Compute the main data
			} else { // For backup data
				if (data.isBackupCompleted()) { // The backup data is already
					// computed
					data.setComputeSwitched(!data.isComputeSwitched());
					continue;
				}
				data.setBackupCompleted(false);
			}

			try {

				for (int simulationStep = initialRun ? 1 : 0; simulationStep < totalTimeTicks; simulationStep++) {

					// Stop?
					if (stop) {
						MessageUtils.debug(this, "run",
								"Computation Tread Stopped.");
						return;
					}

					// Compute the data
					prevS = curS;
					entityList = engineRef.system.getAvailableEntities();
					curS = entityList.size();
					for (int i = 0; i < curS; i++) {

						// Stop?
						if (stop) {
							MessageUtils.debug(this, "run",
									"Computation Tread Stopped.");
							return;
						}

						// Let the entity do action
						Entity current = null;
						try {
							current = (Entity) entityList.get(i);
						} catch (Exception e) {
							continue;
						}
						if (current.isActive()) {
							current.act();
						}

						// Store the current state
						EntityRecord record = data.getEntityRecord2(current,
								simulationStep);
						if (record == null) // The entity has been removed
						{
							continue;
						}
						if (record.position != null) {
							record.position.x = current.getOriginalPosition().x;
							record.position.y = current.getOriginalPosition().y;
						} else {
							record.position = current.getPosition();
						}
						record.direction = current.getDirection();
						record.state = current.getState();
						record.display = current.getOrdiginalDisplay();
						/**
						 * Basic assumption here: The display will not be
						 * changed during the simulation.
						 **/

						data.store(current, record, simulationStep);

					}

					// Update the progress
					if (percent != 0)
						progress.setText(String.valueOf(simulationStep
								/ percent));
					else
						progress.setText(String.valueOf(simulationStep));

				}

			} catch (Exception e) {
				e.printStackTrace();
				MessageUtils.debugAndDisplay(this, "run", new SimException(
						"SIM-COMPUTE-EXCEPTION",
						"Exceptions occur in the computation.", e));
				return;
			} finally {
				// Since the system support entity removal/creation, so it is
				// necessary
				// to check whether any entity has been removed.
				// FIXME: is it a reasonable way to check through the size of
				// the
				// entity list?
				if (curS != prevS) {
					view.refreshSimulation();
				}
				noticeDialog.dispose();
			}

			// Set the completion
			if (!data.isComputeSwitched()) // For main data
				data.setMainCompleted(true);
			else
				// For backup data
				data.setBackupCompleted(true);

			// Set the initial run flag
			initialRun = false;

			// Switch to compute the other data
			data.setComputeSwitched(!data.isComputeSwitched());

		}

	}

	/**
	 * @param show
	 *            the show to set
	 */
	public void setShow(boolean show) {
		noticeDialog.setVisible(show);
	}

	/**
	 * Specify whether the thread should be suspended.
	 * 
	 * The implementation should specify how the thread is suspended.
	 * 
	 * Also, the implementation can issue suspend event to other components,
	 * such as other appviews.
	 * 
	 * @param suspended
	 *            Whether the thread needed to suspend
	 */
	public void setSuspended(boolean suspended) {
		this.stop = suspended;
	}

	/**
	 * Set the time interval between two steps
	 * 
	 * @param deltaT
	 *            The interval
	 */
	public void setDeltaT(int deltaT) {
		throw new RuntimeException(" NOT SUPPORTED ");
	}

	/**
	 * Finalize method from Object. It disposes the notice dialog
	 */
	protected void finalize() throws Throwable {
		try {
			noticeDialog.dispose();
		} catch (Exception e) {
		}
	}

}