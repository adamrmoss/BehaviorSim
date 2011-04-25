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

package sim.model.entity;

import sim.util.Point;

/**
 * Simulation data for each time step and each entity.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class EntityRecord {

	/**
	 * Position
	 */
	public Point position; // The current position

	/**
	 * Motion direction
	 */
	public double direction; // The current motion direction

	/**
	 * State
	 */
	public int state; // The current entity state, active or not-active

	/**
	 * The display component
	 */
	public Display display = null;// The display component

	/**
	 * Default constructor
	 */
	public EntityRecord() {

	}

	/**
	 * Constructor
	 * 
	 * @param p
	 *            The current position of the entity
	 * @param d
	 *            The current motion direction
	 * @param s
	 *            The current entity state
	 * @param display
	 *            The display component of the entity
	 */
	public EntityRecord(Point p, double d, int s, Display display) {
		this.position = p;
		this.direction = d;
		this.state = s;
		this.display = display;
	}
}
