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

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;

import sim.core.AppEngine;
import sim.core.dclass.ResourceLoader;
import sim.model.behavior.Timed;
import sim.util.Point;

/**
 * <p>
 * The entity class. It is the key object used in entity modeling. Each
 * user-defined category is an subclass of this class.
 * </p>
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public abstract class Entity extends SystemFunction implements Timed {

	// The display component
	protected Display display = null;

	// Active state. An active entity involves in simulation. 
	public static final int ACTIVE = 0;
	
	// Inactive state
	public static final int NOT_ACTIVE = 1;
	
	// Default state is active
	protected int state = ACTIVE;

	// The display name of this entity, it serves as the name of this entity.
	private String displayName = null;

	// The integer id of this entity
	private int id = -1;

	// The center of entity
	protected Point position = null;
	
	// Current moving direction
	protected double direction = 0.0D;
	
	// Current moving speed
	protected double speed = 0.0D;

	// X position of the upper left 
	private int detectRefX = 0;
	
	// Y position of the upper left
	private int detectRefY = 0;

	// Current simulation time tick
	protected int timetick = 0;

	// Constructor
	public Entity() {
		display = new Display();
		position = new Point();
	}

	/**
	 * Obtain the current time slice
	 */
	public int getTime() {
		return timetick;
	}

	/**
	 * Set the time slice of this entity
	 * 
	 * @param timetick
	 *            The time slice to be set
	 */
	public void setTime(int timetick) {
		this.timetick = timetick;
	}

	/**
	 * Increase the time slice to be the next time slice.
	 * 
	 */
	private void increaseTimeInstance() {
		this.timetick++;
	}

	/**
	 * Set the display name of this entity.
	 * 
	 * <p>
	 * This name is shown in the navigation panel and used as the unique
	 * identity of this entity.
	 * </p>
	 * 
	 * <p>
	 * This method is called when an entity is created in the system. And it is
	 * mainly used internally.
	 * </p>
	 * 
	 * @param name
	 *            The display name of this entity
	 */
	public void setDisplayName(String name) {
		displayName = name;
	}

	/**
	 * Return the display name of the entity
	 * 
	 * @return the display name of the entity
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Return the entity image to be drawn in the simulation environment.
	 * 
	 * @return The entity image
	 */
	public Image getImage() {
		return display.getImage();
	}

	/**
	 * Return the width of the entity image
	 * 
	 * @return the width of the entity image
	 */
	public int getWidth() {
		return display.getWidth();
	}

	/**
	 * Return the height of the entity image
	 * 
	 * @return the height of the entity image
	 */
	public int getHeight() {
		return display.getHeight();
	}

	/**
	 * Set the detect point of this entity
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            The y position
	 */
	public void setDetectPoint(int x, int y) {
		detectRefX = x;
		detectRefY = y;
	}

	/**
	 * Return the detect position of this entity
	 * 
	 * @return the detect position of this entity
	 */
	public Rectangle getDetectRect() {
		// set detect reference point
		return new Rectangle(detectRefX, detectRefY, getWidth() + 4,
				getHeight() + 4);
	}

	/**
	 * Return the rectangle representation of this entity
	 * 
	 * @param position
	 *            The entity position
	 * @return the rectangle representation of this entity
	 */
	public Rectangle getDrawRect(Point position) {
		return new Rectangle((int) (position.x - getWidth() / 2),
				(int) (position.y - getHeight() / 2), getWidth(), getHeight());
	}

	/**
	 * Prepare the entity image by using specified component tracker.
	 * 
	 * @param c
	 *            The media tracker
	 * @param path
	 *            The image path
	 */
	protected void prepareEntityImage(JComponent c, String path) {
		try {
			// Is it the only way to read the local images?
			Image image = Toolkit.getDefaultToolkit().createImage(
					ResourceLoader.getResource(path, getClass()));
			MediaTracker tracker = new MediaTracker(c);
			try {
				tracker.addImage(image, 0);
				tracker.waitForAll();
			} catch (InterruptedException e) {
			}
			display.setImage(image);
			display.setImagePath(path);
		} catch (Exception e) {
		}
	}

	/**
	 * Set the width and height of the entity image
	 * 
	 * @param w
	 *            The image width
	 * @param h
	 *            The image height
	 */
	public void setEntityWidthHeight(int w, int h) {
		display.setWidth(w);
		display.setHeight(h);
	}

	/**
	 * Set the position of this entity
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            The y position
	 */
	public void setPosition(double x, double y) {

		position.x = x;
		position.y = y;
		setDetectPoint((int) (x - getWidth() / 2 - 4), (int) (y - getHeight()
				/ 2 - 4));

	}

	/**
	 * Set the position
	 * 
	 * @param p
	 *            The position to be set
	 */
	public void setPosition(Point p) {
		position = p;
		setDetectPoint((int) (p.x - getWidth() / 2 - 4), (int) (p.y
				- getHeight() / 2 - 4));
	}

	/** Get the initial display direction for the entity */
	public double getInitialDisplayDirection() {
		return display.getDirection();
	}

	/**
	 * Get the current motion direction
	 * 
	 * @return the current motion direction
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * Return the state of this entity
	 * 
	 * @return the state of this entity
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * Whether this entity is till active or not
	 * 
	 * @return The entity state, active or not
	 */
	public boolean isActive() {
		return this.state == ACTIVE;
	}

	/**
	 * Set the state of the entity.
	 * 
	 * When the parameter is not in the correct range <code>
     * EntityState.ACTIVE</code>
	 * or <code>EntityState.NOT_ACTIVE</code>, an exception
	 * <code>IllegalArgumentException</code> will be thrown.
	 * 
	 * Also, whether the state is not active, the visibility will be false.
	 * 
	 * @param state
	 *            The state of the entity
	 */
	public void setState(int state) {
		if (state != ACTIVE && state != NOT_ACTIVE) {
			throw new IllegalArgumentException(
					"The parameter in 'setState' is not correct.");
		}
		this.state = state;
		if (state == NOT_ACTIVE)
			this.setVisible(false);
		else
			this.setVisible(true);
	}

	/**
	 * Get the current position
	 * 
	 * @return the current position
	 */
	public Point getPosition() {
		return new Point(position);
	}

	/**
	 * Get the current position
	 * 
	 * @return the current position
	 */
	public Point getOriginalPosition() {
		return position;
	}

	/**
	 * Return the x position of this agent.
	 * 
	 * @return The x position of this agent
	 */
	public double getPositionX() {
		return position.x;
	}

	/**
	 * Return the y position of this agent.
	 * 
	 * @return The y position of this agent
	 */
	public double getPositionY() {
		return position.y;
	}

	/**
	 * Set the visibility of the entity
	 * 
	 * @param v
	 *            the visibility to be set
	 */
	public void setVisible(boolean v) {
		display.setVisible(v);
	}

	/**
	 * Get the visibility of the entity
	 * 
	 * @return the visibility of the entity
	 */
	public boolean isVisible() {
		return display.isVisible();
	}

	/**
	 * Return the string representation of this entity. Just return the entity
	 * display name
	 */
	public String toString() {
		return this.displayName;
	}

	/**
	 * The actual processing logic.
	 */
	public void act() {
		AppEngine.getInstance().appManager.currentApp.currentEntity = ((Category) this);
		this.increaseTimeInstance();
		this.preprocess();
		this.process();
		this.postprocess();
	}

	/**
	 * The method call before the actual action processed
	 * 
	 * <p>
	 * It is used to prepare some pre-requirements before the actual action
	 * processed, such as setup the choice of the action to execute, or update
	 * the variable values, etc.
	 * </p>
	 */
	protected void preprocess() {
	}

	/**
	 * The method call after the actual action processed
	 */
	protected void postprocess() {
	}

	/**
	 * The actual action processing
	 * 
	 * <p>
	 * The user needs to setup the behavior action first to let the action
	 * executes
	 * </p>
	 */
	protected void process() {
	}

	/**
	 * General case initialization. It is called before the simulation
	 * calculation. The user can override this method to do some initialization
	 * work
	 */
	public void init() {

	}

	/**
	 * System called initialization.It is called before the simulation
	 * calculation. It is supposed to be only used internally.
	 */
	public void _initInternal() {

	}

	/**
	 * Return the entity type - the category name
	 * 
	 * @return the entity type
	 */
	public abstract String getEntityType();

	/**
	 * A deep copy of this entity object to the specified entity.
	 * 
	 * <p>
	 * The purpose of this method is to provide a state copy for the cooperative
	 * action selection mechanism, where each action will produce a "new" state
	 * of the entity. To avoid the "new" state affects the original state, a
	 * copied entity is used to perform the action.
	 * </p>
	 * 
	 * <p>
	 * <strong>FIXME</strong> DO WE NEED TO COPY behavior action? DOES THE
	 * EXCUTED ACTION AT EACH TIME STEP EFFECT THESE THIS behavior action?
	 * </p>
	 * 
	 * @param entity
	 *            The entity who gets the states of this entity
	 */
	public void copyTo(Entity entity) {
		if (entity == null)
			return;
		entity.position.x = this.position.x;
		entity.position.y = this.position.y;
		entity.state = this.state;
		entity.displayName = this.displayName;
		entity.detectRefX = this.detectRefX;
		entity.detectRefY = this.detectRefY;
		entity.display = display.copy();
		entity.direction = this.direction;
		entity.timetick = this.timetick;
		entity.id = this.id;

		// Default: the behavior action is not copied, suppose that
		// it will never be changed after the execution of all behaviors.
		// entity.action = (BehaviorAction)this.action.copy();

	}

	// Getters and Setters
	/**
	 * Return the full path of the entity image
	 */
	public String getImagePath() {
		return display.getImagePath();
	}

	/**
	 * Set the full path of the entity image
	 * 
	 * @param imagePath
	 */
	public void setImagePath(String imagePath) {
		display.setImagePath(imagePath);
	}

	/**
	 * Return relative path of the entity image
	 * 
	 * @return relative path of the entity image
	 */
	public String getRelativeImagePath() {
		return display.getRelativeImagePath();
	}

	/**
	 * Set the relative path of the entity image
	 * 
	 * @param imagePath
	 */
	public void setRelativeImagePath(String imagePath) {
		display.setRelativeImagePath(imagePath);
	}

	/**
	 * Set the entity image, used in simulation
	 * 
	 * @param image
	 */
	public void setImage(Image image) {
		display.setImage(image);
	}

	/**
	 * Set the width of the image displayed during simulation
	 * 
	 * @param imageWidth
	 */
	public void setWidth(int imageWidth) {
		display.setWidth(imageWidth);
	}

	/**
	 * Set the height of the image displayed during simulation
	 * 
	 * @param imageHeight
	 */
	public void setHeight(int imageHeight) {
		display.setHeight(imageHeight);
	}

	/**
	 * Set the moving direction
	 * 
	 * @param currentMotionDirection
	 */
	public void setDirection(double currentMotionDirection) {
		// if (currentMotionDirection > Math.PI)
		// currentMotionDirection = 2 * Math.PI - currentMotionDirection;
		// else if (direction < -Math.PI)
		// currentMotionDirection = 2 * Math.PI + currentMotionDirection;
		this.direction = currentMotionDirection;
	}

	/**
	 * Return entity id
	 * 
	 * @return the id
	 */
	public int getMyId() {
		return id;
	}

	/**
	 * Return the current moving speed
	 * 
	 * @return the movingSpeed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set the moving speed
	 * 
	 * @param movingSpeed
	 *            the movingSpeed to set
	 */
	public void setSpeed(double movingSpeed) {
		this.speed = movingSpeed;
	}

	/**
	 * Set entity id
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Return the original display component, rather than a copy
	 * 
	 * @return the original display component
	 */
	public Display getOrdiginalDisplay() {
		return display;
	}

	/**
	 * Return the display component
	 * 
	 * @return the display
	 */
	public Display getDisplay() {
		return display.copy();
	}

	/**
	 * Set the display component
	 * 
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(Display display) {
		this.display = display.copy();
	}

	/**
	 * Method from <code>java.lang.Object</code>
	 */
	public int hashCode() {
		return id;
	}

	/**
	 * Method from <code>java.lang.Object</code>
	 */

	public boolean equals(Object obj) {
		if (!(obj instanceof Entity))
			return false;
		return ((Entity) obj).id == id;
	}

}