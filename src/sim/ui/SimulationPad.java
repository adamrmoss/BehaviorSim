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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

import sim.core.AppEngine;
import sim.core.SimulationEnvironment;

/**
 * Pad for showing the simulation results
 * 
 * @author Pavel
 * @version 1.0
 */
public class SimulationPad extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4702546698829515128L;
	
	/**
	 * Simulation environment instance 
	 */
	private SimulationEnvironment se = AppEngine.getInstance()
	.getSimulationEnvironment();

	/**
	 * Width and height of this pad
	 */
	private int width, height;

	/**
	 * Background image
	 */
	private Image seaImage = null;

	/**
	 * Simulation image
	 */
	private Image simulationImage = null;

	/**
	 * Simulation view
	 */
	private SimulationView view = null;

	public SimulationPad(SimulationView v) {
		view = v;
		loadEnv();
	}

	/** Get the sea image and its bounds */
	public void loadEnv() {
		
		seaImage = se.getImage();
		width = se.getWidth();
		height = se.getHeight();
		
	}

	/**
	 * return the preferred size
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * Return the minimum size of this pad
	 */
	public Dimension getMinimumSize() {
		return new Dimension(width, height);
	}

	/**
	 * set up simulation image and paint the simulation results
	 */
	public void addNotify() {
		super.addNotify();
		if (simulationImage == null) {
			simulationImage = createImage(width, height);
			paintSimulation();
			repaint();
		}
	}

	/**
	 * Paint this pad
	 */
	protected void paintComponent(Graphics g) {
		if (simulationImage != null) {
			g.drawImage(simulationImage, 0, 0, this);
		}
	}

	/**
	 * Paint the simulation results
	 */
	public void paintSimulation() {
		
		loadEnv();
		Graphics g = simulationImage.getGraphics();
		g.clearRect(0, 0, width, height);
		g.drawImage(seaImage, 0, 0, width, height, this);
		if (view != null)
			view.drawSimulation(g);
		g.dispose();
	
	}

	/**
	 * Paint the simulation results of the specified time
	 */
	public void paintSimulation(Graphics g, int time) {

		loadEnv();
		g.clearRect(0, 0, width, height);
		g.drawImage(seaImage, 0, 0, width, height, this);
		if (view != null)
			view.drawSimulation(g, time);
	
	}

	/**
	 * 
	 * @return Simulation image
	 */
	public Image getSimulationImage() {
		return simulationImage;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
}
