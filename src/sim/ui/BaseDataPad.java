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

import javax.swing.JPanel;

import sim.core.AppRunnable;

/**
 * <p>
 * Title: Simulation Data Viewer
 * </p>
 * 
 * <p>
 * Description: Parent class of the viewers of relative simulation data. To
 * provide common settings of these viewers.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author QFS
 * @version 1.0
 */
public abstract class BaseDataPad extends JPanel implements AppRunnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 310507335163293016L;

	public BaseDataPad(Dimension ps) {
		if (ps == null) {
			throw new RuntimeException(
					"The prefered size of the data pad can not be null!");
		}
		super.setPreferredSize(ps);
	}

	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}

	public void addNotify() {
		super.addNotify();
		this.paintXAndYCoordinates(this.getGraphics());
	}

	protected void paintComponent(Graphics g) {
		this.drawXAndYCoordinates(g);
		// this.drawSimulationData(g);
	}

	protected void paintXAndYCoordinates(Graphics g) {
		this.drawXAndYCoordinates(g);
		g.dispose();
	}

	/*
	 * Reset the display of the domain object. When the domain object is
	 * changed, or the simulation data is changed, then this method should be
	 * called.
	 */
	public void reset() {
		Graphics g = this.getGraphics();
		// Get dimension
		Dimension d = super.getPreferredSize();
		g.clearRect(0, 0, d.width, d.height);
		this.drawXAndYCoordinates(g);
		g.dispose();
	}

	/**
	 * Draw the x and y coordinates based on the range of the simulation data,
	 * the prefered size of this component.
	 * 
	 * 
	 *@param g
	 * 
	 */
	protected abstract void drawXAndYCoordinates(Graphics g);

	/**
	 * Draw the domain simulation data, such as the behavior strength and
	 * excitation. The child class should provide the implementation to provide
	 * actual rendering manner of the simulation data.
	 * 
	 * 
	 * @param g
	 */
	protected abstract void drawSimulationData(Graphics g);

	/**
	 * The viewers are of thread, which tends not to block the main graphics
	 * thread. The draw rutines are handled in this method.
	 */
	public abstract void run();

}
