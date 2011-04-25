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

package sim.ui;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.border.Border;

import sim.core.AppEngine;
import sim.core.AppResources;
import sim.core.CategoryUpdateListener;
import sim.core.SimulationComputeThread;
import sim.core.SimulationDisplayThread;
import sim.core.SimulationThread;
import sim.model.entity.Category;
import sim.model.entity.Entity;

/**
 * Simulation view. The central place for starting the simulation and checking
 * the result.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class SimulationView extends AppView implements ActionListener,
		CategoryUpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6806612834367656399L;
		
	// The view icon
	private ImageIcon viewIcon = null;

	// Application engine
	public AppEngine engineRef = AppEngine.getInstance();

	// Simulation Pad
	private SimulationPad pad = null;

	// Animation thread (for drawing simulation results)
	private SimulationDisplayThread animationThread = null;

	// Simulation thread for animation
	private SimulationThread thread = null;

	// Computation thread for Simulation data
	private SimulationComputeThread computeThread = null;

	// Simulation thread for computation
	private SimulationThread thread2 = null;

	// The simulation progress slider
	private JSlider slider = new JSlider();

	// Time tick label
	private JLabel tkLabel = new JLabel("Current time step: 0");
	
	// Current display time
	private int displayTime;
	
	// Initial position of all agents
	private Map initialPos = new HashMap();

	// Display ratio label
//	private JLabel dispRatio = new JLabel("Display ratio:");
//	private JLabel ratioLabel = new JLabel("100%");
//	private JSlider ratioSlider = new JSlider();

	/**
	 * Whether the simulation is paused and stopped
	 */
	private boolean isPaused = false;
	private boolean isStopped = false;

	/**
	 * Constructor
	 * 
	 * @param engine
	 */
	public SimulationView( ) {

		engineRef.registerSimulationViewUpdateListener(this);
		viewIcon = new ImageIcon(engineRef.jrl
				.getImage(AppResources.systIconPath));

		JButton playButton = null;
		JButton stopButton = null;
		JButton pauseButton = null;
		JPanel sliderPanel = new JPanel();
		playButton = new JButton(new ImageIcon(engineRef.jrl
				.getImage(AppResources.playImagePath)));
		stopButton = new JButton(new ImageIcon(engineRef.jrl
				.getImage(AppResources.stopImagePath)));
		pauseButton = new JButton(new ImageIcon(engineRef.jrl
				.getImage(AppResources.pauseImagePath)));
		setLayout(new GridBagLayout());
		Border b = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(b);
		pad = new SimulationPad(this);

		// Create the animation thread and reset current simulation time
		animationThread = new SimulationDisplayThread(pad, this,
				slider);

		JPanel buttonBar = new JPanel();
		JPanel buttonBarGrid = new JPanel();
		GridLayout buttonBarGridLayout = new GridLayout();
		buttonBar.setLayout(new FlowLayout());
		buttonBarGrid.setLayout(buttonBarGridLayout);
		buttonBarGridLayout.setColumns(3);
		buttonBarGridLayout.setHgap(10);
		buttonBarGrid.add(playButton);
		buttonBarGrid.add(pauseButton);
		buttonBarGrid.add(stopButton);
		buttonBar.add(buttonBarGrid);

		SpringLayout sl = new SpringLayout();
		sliderPanel.setLayout(sl);
		sliderPanel.add(tkLabel);
		sliderPanel.add(slider);
		sliderPanel.add(buttonBar);
		// sliderPanel.add(dispRatio);
		// sliderPanel.add(ratioLabel);
		// sliderPanel.add(ratioSlider);

		// Layout displayPad at (20, 20)
		// sl.putConstraint(SpringLayout.WEST, displayPad,
		// 20,
		// SpringLayout.WEST, sliderPanel);
		// sl.putConstraint(SpringLayout.NORTH, displayPad,
		// 20,
		// SpringLayout.NORTH, sliderPanel);

		// Layout l at (20, <displayPad's bottom edge> + 5)
		sl.putConstraint(SpringLayout.WEST, this, 20, SpringLayout.WEST,
				sliderPanel);
		sl.putConstraint(SpringLayout.NORTH, this, 5, SpringLayout.NORTH, this);

		sl.putConstraint(SpringLayout.WEST, tkLabel, 20, SpringLayout.WEST,
				sliderPanel);
		sl.putConstraint(SpringLayout.NORTH, tkLabel, 30, SpringLayout.NORTH,
				pad);

		// Layout cb at (20, <displayPad's bottom edge> + 5)
		// sl.putConstraint(SpringLayout.WEST, cb,
		// 20,
		// SpringLayout.WEST, sliderPanel);
		// sl.putConstraint(SpringLayout.NORTH, cb,
		// 5,
		// SpringLayout.SOUTH, displayPad);
		// sl.putConstraint(SpringLayout.WEST, cb,
		// 5,
		// SpringLayout.EAST, l);

		// Layout slider
		// ---Adjust constraints for the slider so it's at
		// (20, <cb's bottom edge> + 30).
		sl.putConstraint(SpringLayout.WEST, slider, 20, SpringLayout.WEST,
				sliderPanel);
		sl.putConstraint(SpringLayout.NORTH, slider, 30, SpringLayout.NORTH,
				tkLabel);

		// Layout buttonBar
		// ---Adjust constraints for the buttonBar so it's at
		// (20, <slider's bottom edge> + 5).
		sl.putConstraint(SpringLayout.WEST, buttonBar, 20, SpringLayout.WEST,
				sliderPanel);
		sl.putConstraint(SpringLayout.NORTH, buttonBar, 5, SpringLayout.SOUTH,
				slider);

		/*
		 * sl.putConstraint(SpringLayout.WEST, dispRatio, 20, SpringLayout.WEST,
		 * sliderPanel); sl.putConstraint(SpringLayout.NORTH, dispRatio, 30,
		 * SpringLayout.SOUTH, buttonBar); sl.putConstraint(SpringLayout.WEST,
		 * ratioLabel, 10, SpringLayout.EAST, dispRatio);
		 * sl.putConstraint(SpringLayout.NORTH, ratioLabel, 30,
		 * SpringLayout.SOUTH, buttonBar); sl.putConstraint(SpringLayout.WEST,
		 * ratioSlider, 20, SpringLayout.WEST, sliderPanel);
		 * sl.putConstraint(SpringLayout.NORTH, ratioSlider, 5,
		 * SpringLayout.SOUTH, dispRatio);
		 */

		pad.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		add(pad, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));

		add(sliderPanel, new GridBagConstraints(GridBagConstraints.RELATIVE, 0,
				GridBagConstraints.REMAINDER, 1, 30, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});

		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pause();
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		slider.setMaximum(engineRef.getTotalTimeticks());
		slider.setValue(0);

//		ratioSlider.setMinimum(0);
//		ratioSlider.setMaximum(500);
//		ratioSlider.setMajorTickSpacing(100);
//		ratioSlider.setMinorTickSpacing(10);
//		ratioSlider.setPaintTicks(true);
//		ratioSlider.setValue(100);
//		ratioSlider.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				Object obj = e.getSource();
//				if (obj == ratioSlider) {
//					ratioLabel.setText(ratioSlider.getValue() + "%");
//				}
//			}
//		});

	}
	
	/**
	 * return the title of this view
	 */
	public String getTitle() {
		return new String("Simulation");
	}

	/**
	 * return the view icon
	 */
	public Icon getViewIcon() {
		return viewIcon;
	}
	
	/**
	 * Return current display time
	 * 
	 * @return
	 */
	public int getDisplayTime() {
		return displayTime;
	}
		

	/**
	 * @param displayTime the displayTime to set
	 */
	public void setDisplayTime(int displayTime) {
		this.displayTime = displayTime;
	}

	/**
	 * Draw the simulation result for current time step
	 * 
	 * @param g
	 *            Graphics
	 */
	public void drawSimulation(Graphics g) {
		engineRef.system.drawAvailableEntities(g, getDisplayTime());
	}

	/**
	 * Draw the simulation result for the specified time step
	 * 
	 * @param g
	 *            Graphics
	 * @param time
	 *            The time step
	 */
	public void drawSimulation(Graphics g, int time) {
		engineRef.system.drawAvailableEntities(g, time);
	}

	/**
	 * Set the current time step
	 */
	public void setTimeStepLabel(int time) {
		tkLabel.setText("Current time step: " + time);
	}
	
	/**
	 * Save initial position of all available entities
	 */
	private void initializePos()
	{
		List agents = engineRef.getAvailableEntities();
		for (int i = 0; i < agents.size(); i++)
		{
			Entity entity = (Entity)agents.get(i);
			initialPos.put(
					new Integer(entity.getMyId()), 
					entity.getPosition()
			);
		}
	}
	
	/**
	 * Show the progress window or not?
	 * 
	 * @param show
	 *            True if the window should be shown
	 */
	public void showProgressWin(boolean show) {
		computeThread.setShow(show);
	}

	/**
	 * Repaint the simulation results
	 */
	public void refreshSimulation() {

		pad.paintSimulation();
		pad.repaint();

	}

	/**
	 * Reset the current simulation time and restart the thread which is to
	 * compute the simulation data.
	 */
	public void refresh() {
		
		// Store initial position of all entities
		initializePos();

		// Reset the slider
		slider.setMaximum(engineRef.getTotalTimeticks());
		slider.setValue(0);

		// Prepare the simulation calculation
		engineRef.resetSimulation(initialPos);

		// Reset the simulation time and display the initial image
		animationThread.setSimulationTime(0, true);

		// To display entity initial position properly,
		// current thread sleeps for 50 milliseconds
		try {
			Thread./*currentThread().*/sleep(50);
		} catch (Exception ee) {
		}

		// Launch compute thread and show a progress window
		if (computeThread != null)
			computeThread.setSuspended(true);
		if (thread2 != null && thread2.isAlive()) {
			thread2.getTarget().setSuspended(true);
		}
		thread2 = new SimulationThread(
				computeThread = new SimulationComputeThread(this),
				"COMPUTATIONTHREAD");
		thread2.start();
		
		// Compute thread is launched, reset the stop variable
		isStopped = false;

	}

	/**
	 * Start the simulation
	 */
	public void play() {

		if (engineRef.getAvailableEntities().isEmpty()) {
			// show warning indicating there are no entities
			JOptionPane.showMessageDialog(this,
					"No entities configured to perform simulation", getTitle(),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// The computation thread is stopped, restart it
		if (isStopped) {

			// Prepare the simulation calculation
			engineRef.resetSimulation(initialPos);

			// Launch compute thread and show a progress window
			if (computeThread != null)
				computeThread.setSuspended(true);
			if (thread2 != null && thread2.isAlive()) {
				thread2.getTarget().setSuspended(true);
			}
			thread2 = new SimulationThread(
					computeThread = new SimulationComputeThread(this),
					"COMPUTATIONTHREAD");
			thread2.start();

			// Reset the stopped flag
			isStopped = false;

		}

		// restart the animation thread
		animationThread.setSuspended(false);
		if (!isPaused) {
			animationThread.setSimulationTime(0, true);
		}
		isPaused = false;

		// stop the former thread
		if (thread != null && thread.isAlive()) {
			thread.getTarget().setSuspended(true);
		}
		thread = new SimulationThread(animationThread, "SIMULATIONTHREAD");
		thread.start();

	}

	/**
	 * Suspend the animation thread
	 */
	public void pause() {

		animationThread.setSuspended(true);
		// displayPad.setSuspended(true);
		isPaused = true;

	}

	/**
	 * Stop and reset the animation thread and also reset the progress slider
	 * and the current simulation time
	 */
	public void stop() {

		// reset the slider and positions of all the entities
		animationThread.setSuspended(true);

		// reset the computation thread
		computeThread.setSuspended(true);

		// displayPad.setSuspended(true);
		// displayPad.reset();
		// displayPad.setSimulationStep(1);
		isPaused = false;

		// Reset the slider
		slider.setMaximum(engineRef.getTotalTimeticks());
		slider.setValue(0);

		// Reset the simulation time - 9/8/2010
//		animationThread.setSimulationTime2(0, true);

		// The computation thread and the animation thread are already stopped
		isStopped = true;
		
		// Save the current position of all agents - 9/8/2010
		engineRef.system.populatePositions(initialPos, displayTime);

	}

	/**
	 * Action event listener
	 */
	public void actionPerformed(ActionEvent e) {
		// if (e.getSource() instanceof JComboBox){
		// String crayfishName =
		// (String)((JComboBox)e.getSource()).getSelectedItem();
		// displayPad.setSuspended(true);
		// displayPad.setCrayfish((Crayfish)engineRef.retrieveEntity(crayfishName));
		// }
	}

	/**
	 * @param viewIcon
	 *            the viewIcon to set
	 */
	public void setViewIcon(ImageIcon viewIcon) {
		this.viewIcon = viewIcon;
	}

	/**
	 * @param engineRef
	 *            the engineRef to set
	 */
	public void setEngineRef(AppEngine engineRef) {
		this.engineRef = engineRef;
	}

	/**
	 * @param pad
	 *            the pad to set
	 */
	public void setPad(SimulationPad pad) {
		this.pad = pad;
	}

	/**
	 * @param animationThread
	 *            the animationThread to set
	 */
	public void setAnimationThread(SimulationDisplayThread animationThread) {
		this.animationThread = animationThread;
	}

	/**
	 * @param thread
	 *            the thread to set
	 */
	public void setThread(SimulationThread thread) {
		this.thread = thread;
	}

	/**
	 * @param computeThread
	 *            the computeThread to set
	 */
	public void setComputeThread(SimulationComputeThread computeThread) {
		this.computeThread = computeThread;
	}

	/**
	 * @param thread2
	 *            the thread2 to set
	 */
	public void setThread2(SimulationThread thread2) {
		this.thread2 = thread2;
	}

	/**
	 * @param slider
	 *            the slider to set
	 */
	public void setSlider(JSlider slider) {
		this.slider = slider;
	}

	/**
	 * @param tkLabel
	 *            the tkLabel to set
	 */
	public void setTkLabel(JLabel tkLabel) {
		this.tkLabel = tkLabel;
	}

	/**
	 * @param isPaused
	 *            the isPaused to set
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * @param isStopped
	 *            the isStopped to set
	 */
	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	/**
	 * A new category is added.
	 * 
	 * @param newCategory
	 *            The newly added category
	 */
	public void categoryAdded(Category newCategory) {

	}

	/**
	 * A category is deleted
	 * 
	 * @param categoryName
	 *            The name of the category to be deleted
	 */
	public void categoryDeleted(String categoryName) {

	}

	/**
	 * Update the definition of an existing category
	 * 
	 * @param oldCategoryName
	 *            The name of the existing category
	 * @param newCategory
	 *            The name of the new category
	 */
	public void categoryUpdated(String oldCategoryName, Category newCategory) {

	}

	/**
	 * An new entity is added
	 * 
	 * @param newEntity
	 *            The instance of that entity
	 */
	public void entityAdded(Entity newEntity) {

	}

	/**
	 * Update the simulation world
	 * 
	 */
	public void entityUpdated() {
		refreshSimulation();
	}

}
