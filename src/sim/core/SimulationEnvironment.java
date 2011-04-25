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

import java.awt.Image;
import java.awt.Rectangle;

import sim.core.dclass.ResourceLoader;
import sim.model.entity.Display;
import sim.model.entity.Entity;
import sim.util.Point;

/**
 * Simulation environment. It wraps information of simulation environment.
 * 
 * @author Pavel, Fasheng Qiu
 * 
 */
public class SimulationEnvironment {

	/** Constants of collision */
	private static final int ENV_NO_COLLISION = 0;
	private static final int ENV_COLLISION_NORTH = 1;
	private static final int ENV_COLLISION_SOUTH = 2;
	private static final int ENV_COLLISION_EAST = 3;
	private static final int ENV_COLLISION_WEST = 4;

	/** Constants of environment type */
	public static final int ROUNDED = 0;
	public static final int OPEN = 1;
	public static final int CLOSED = 2;
	private int type = ROUNDED; // Default type is rounded

	/** Display instance */
	private Display display = null;

	/** Constructor, used internally */
	SimulationEnvironment() {
		display = new Display();
	}

	/** Set background image */
	public void setImage(Image image) {
		display.setImage(image);
	}

	/** Get the back ground image */
	public Image getImage() {
		return display.getImage();
	}

	/** Set the path of the background image */
	public void setImagePath(String path) {
		display.setImagePath(path);
	}

	/** Get the background image path */
	public String getImagePath() {
		return display.getImagePath();
	}

	/**
	 * Return the input stream of the background image. It is used in the case
	 * that the image is included in the beahviorsim.jar. A resource loader is
	 * used to get the input stream. Note that, this method only reads the input
	 * stream from BehaviorSim.jar.
	 * 
	 * @see #{ResourceLoader.getEntryAsInputStream}
	 * 
	 * @return The input stream of the background image.
	 */
	public java.io.InputStream getImageInputStream() throws Exception {
		ResourceLoader rl = AppEngine.getInstance().jrl;
		String path = AppResources.seaBitmapPath;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return rl.getEntryAsInputStream(path);
	}

	/**
	 * Reset the simulation environment when an application is created. It is
	 * used to reset the background image path, since the default image path is
	 * relative to BehaviorSim.jar.
	 * 
	 * <p>
	 * This method copies the default blank background image into the
	 * application resource directory and reset the image information of this
	 * environment
	 * </p>
	 * 
	 * 
	 */
	public void resetToApp() {
		try {
			// Get the input stream of the background image
			java.io.InputStream is = getImageInputStream();
			// Upload the image
			String fullPath = AppEngine.getInstance().uploadImage(is,
					getRelativeImagePath());
			// Set the full path of the background image
			setImagePath(fullPath);
			// Set the environment type
			type = ROUNDED;
			// Reset the navigation panel
			AppEngine.getInstance().navPanel.updateSimulationWorld();
		} catch (Exception e) {
			e.printStackTrace();
			sim.util.MessageUtils.debug(this, "resetToApp", e);
		}
	}

	/**
	 * Set the relative path of the background image
	 * 
	 * @param path
	 */
	public void setRelativeImagePath(String path) {
		display.setRelativeImagePath(path);
	}

	/**
	 * Return the relative path of the background image
	 * 
	 * @return
	 */
	public String getRelativeImagePath() {
		return display.getRelativeImagePath();
	}

	/**
	 * Get the image display direction
	 * 
	 * @return
	 */
	public double getDirection() {
		return display.getDirection();
	}

	/**
	 * Set the image display direction
	 * 
	 * @param d
	 */
	public void setDirection(double d) {
		display.setDirection(d);
	}

	
	/**
	 * Set the height of the environment
	 * @param h Height of the simulation environment
	 */
	public void setHeight(int h)
	{
		display.setHeight(h);
	}
	
	/**
	 * Set the width of the environment
	 * @param w Width of the simulation environment
	 */
	public void setWidth(int w)
	{
		display.setWidth(w);
	}

	/**
	 * Return the environment width
	 * 
	 * @return
	 */
	public int getWidth() {
		return display.getWidth();
	}

	/**
	 * Return the environment height
	 * 
	 * @return
	 */
	public int getHeight() {
		return display.getHeight();
	}

	/**
	 * Whether the position collides with the environment boudary.
	 * 
	 * @param p
	 * @return
	 */
	public int determineCollision(Point p) {
		int collision = ENV_NO_COLLISION;

		if (p.y < 0)
			collision = ENV_COLLISION_NORTH;
		else if (p.y > display.getHeight())
			collision = ENV_COLLISION_SOUTH;
		else if (p.x > display.getWidth())
			collision = ENV_COLLISION_EAST;
		else if (p.x < 0)
			collision = ENV_COLLISION_WEST;

		return collision;
	}

	/**
	 * Determine whether the rectangle collides with the environment
	 * 
	 * @param r
	 * @return
	 */
	public int determineCollision(Rectangle r) {
		int collision = ENV_NO_COLLISION;

		if (r.y < 0)
			collision = ENV_COLLISION_NORTH;
		else if (r.y + r.height > display.getHeight())
			collision = ENV_COLLISION_SOUTH;
		else if (r.x + r.width > display.getWidth())
			collision = ENV_COLLISION_EAST;
		else if (r.x < 0)
			collision = ENV_COLLISION_WEST;

		return collision;
	}

	/**
	 * Handle the environment bounds after the computation of the entity
	 * position to ensure that the entity is in the environment.
	 * 
	 */
	public void handleEnvironmentBounds(Entity entity) {
		// position and width
		double x = entity.getPosition().x;
		double y = entity.getPosition().y;
		int width = entity.getWidth();
		int height = entity.getHeight();
		int collision = determineCollision(new Rectangle((int) (x - width / 2),
				(int) (y - height / 2), width, height));
		// environment width and height
		double h = getHeight();
		double w = getWidth();
		if (type == ROUNDED) {
			// adjust position
			switch (collision) {
			case ENV_COLLISION_NORTH:
				entity.setPosition(x, y + h - height);
				break;
			case ENV_COLLISION_SOUTH:
				entity.setPosition(x, y - h + height);
				break;
			case ENV_COLLISION_EAST:
				entity.setPosition(x - w + width, y);
				break;
			case ENV_COLLISION_WEST:
				entity.setPosition(x + w - width, y);
				break;
			}
		} else if (type == OPEN) {
			// Just let the entity move out of the environment and ignore
			// it?????????
		} else if (type == CLOSED) {
			// adjust motion direction
			switch (collision) {
			case ENV_COLLISION_NORTH:
				entity.setDirection(-entity.getDirection());
				entity.setPosition(x, height / 2);
				break;
			case ENV_COLLISION_SOUTH:
				entity.setDirection(-entity.getDirection());
				entity.setPosition(x, h - height / 2);
				break;

			case ENV_COLLISION_EAST:
				entity.setDirection(Math.PI - entity.getDirection());
				entity.setPosition(w - width / 2, y);
				break;
			case ENV_COLLISION_WEST:
				entity.setDirection(Math.PI - entity.getDirection());
				entity.setPosition(width / 2, y);
				break;

			}
		}
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
}
