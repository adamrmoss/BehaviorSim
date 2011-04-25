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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sim.core.AppEngine;
import sim.core.AppSystem;
import sim.util.MessageUtils;
import sim.util.MethodUtils;
import sim.util.Point;
import sim.util.Vect;

/**
 * <p>
 * A utility class for all basic system methods. These methods can be referred in
 * user-defined methods. 
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class SystemFunction extends Object {

	/** Full qualified name of this class. */
	public static final String NAME = "sim.model.entity.SystemFunction";

	/** Generator of random number, which may be needed in the subclass */
	protected final static Random randomer = new Random();

	/** Application Engine */
	protected static final AppEngine engine = AppEngine.getInstance();

	/** Application system object */
	private static final AppSystem system = engine.system;

	/** Move forward, internal use only */
	public static final int MOVEFORWARD = 0;

	/** Move backward, internal use only */
	public static final int MOVEBACKWARD = 1;

	/** Rotate clockwise, internal use only */
	public static final int ROTATECLOCKWISE = 2;

	/** Rotate anti-clockwise, internal use only */
	public static final int ROTATECOUNTERCLOCKWISE = 3;

	// public static final int ACCELERATION = 4;

	// -----------------------------------------------
	// System functions, which can be used by users
	//
	// -----------------------------------------------

	/**
	 * Return the width of the simulation world
	 * 
	 * @return the width of the simulation world
	 */
	public static int getWorldWidth() {
		return system.env.getWidth();
	}

	/**
	 * Return the height of the simulation world
	 * 
	 * @return the height of the simulation world
	 */
	public static int getWorldHeight() {
		return system.env.getHeight();
	}

	/**
	 * Return the width of the specified entity.
	 * 
	 * @param entityID
	 *            ID of the specified entity.
	 * @return the width of the specified entity.
	 */
	public static int getEntityWidth(int entityID) {
		return getEntityById(entityID).getWidth();
	}

	/**
	 * Return the height of the specified entity.
	 * 
	 * @param entityID
	 *            ID of the specified entity.
	 * @return the height of the specified entity.
	 */
	public static int getEntityHeight(int entityID) {
		return getEntityById(entityID).getHeight();
	}

	/**
	 * Set the width of the specified entity.
	 * 
	 * @param entityID
	 *            ID of the specified entity.
	 * @param width
	 *            Width to be set.
	 */
	public static void setEntityWidth(int entityID, int width) {
		getEntityById(entityID).setWidth(width);
	}

	/**
	 * Set the height of the specified entity.
	 * 
	 * @param entityID
	 *            ID of the specified entity.
	 * @param height
	 *            Height to be set.
	 */
	public static void setEntityHeight(int entityID, int height) {
		getEntityById(entityID).setHeight(height);
	}

	/**
	 * Return the category name of the specified entity.
	 * 
	 * @param entityID
	 *            ID of the specified entity
	 * @return The category name of the entity.
	 */
	public static String getCategoryName(int entityID) {
		return getEntityById(entityID).getEntityType();
	}

	/**
	 * Obtain the id of the specified entity with the given display name. If no
	 * entity matches the given display name and if the entity is not active, -1
	 * will be returned. If multiple entities have the same name, the first matched
	 * entity will be returned.
	 * 
	 * <p>
	 * The id of an entity is ranged from 0 to total number of entities - 1, so
	 * the first added entity's id is 0, the second one is 1, ....
	 * </p>
	 * 
	 * @param displayName
	 *            The display name of the entity whose id is returned
	 * @return The entity id if the entity exists. Otherwise, -1 will be
	 *         returned
	 */
	public static int getEntityId(String displayName) {
		Entity entity = system.getEntityByDisplayName(displayName);
		if (entity == null || !entity.isActive())
			return -1;
		return entity.getMyId();
	}

	/**
	 * Obtain the id of the closest entity. The closest entity belongs to the
	 * given category name. If the category name is "ALL" (case insensitive),
	 * the closest entity will be the closest one, no matter what category it
	 * belongs to.
	 * 
	 * @param categoryName
	 *            The category name of the closest entity to be returned
	 * @return The closest entity id. If no closest exists, -1 will be returned.
	 */
	public static int getClosestEntityInCategory(String categoryName) {
		int entityID = engine.getCurrentEntity().getMyId();
		Entity closestOne = getClosestEntity(entityID, categoryName);
		return closestOne == null ? -1 : closestOne.getMyId();
	}

	/**
	 * Return the entity of the specified display name. If no such entity 
	 * can be found or the found entity is inactive, <code>null<code> will be returned. 
	 * If multiple entities have the same name, the first matched entity will be returned.
	 * 
	 * 
	 * @param displayName
	 *            The display name of the entity to return
	 * @return The entity of the specified display name
	 */
	private static Entity getEntity(int entityID) {
		Entity entity = system.getEntityById(entityID);
		if (entity == null || !entity.isActive())
			return null;
		return entity;
	}

	/**
	 * Return the entity who is closest the specified entity, in the current
	 * time slice of the specified entity.
	 * 
	 * 
	 * @param entityID
	 *            The entity id based on which the distance is computed
	 * @param categoryName
	 *            The category name whose entities are computed
	 * @return The name of the entity who is closest the specified entity
	 */
	private static Entity getClosestEntity(int entityID, String categoryName) {
		Entity self = getEntity(entityID);
		if (categoryName.trim().equalsIgnoreCase("all"))
			return system.getClosestEntity(self,
					self.getTime() /*- 1 >= 0 ? self.getTime() - 1 : 0*/,
					Double.MAX_VALUE);
		else
			return system.getClosestEntityByType(self, categoryName, self
					.getTime() /*- 1 >= 0 ? self.getTime() - 1 : 0*/,
					Double.MAX_VALUE);
	}

	/**
	 * Get a list (EntityList) of entities which are within the given distance
	 * from this entity. Each entry of the list will be the entity reference
	 * (instance of Entity).
	 * 
	 * @param distance
	 *            The distance range from this entity
	 * @return The list of entities within the given range
	 */
	public static EntityList getListOfEntitiesWithinDistance(double distance) {
		Entity self = engine.getCurrentEntity();
		return new EntityList(system.getEntitiesWithinDistance(self, self
				.getTime() /*- 1 >= 0 ? self.getTime() - 1 : 0*/, distance));
	}

	/**
	 * Get a list (EntityList) of entities which are within the given distance
	 * from this entity. Each entry of the list will be the entity reference
	 * (instance of Entity).
	 * 
	 * @param distance
	 *            The distance range from this entity
	 * @return The list of entities within the given range
	 */
	public static EntityList getListOfEntitiesWithinDistance(int distance) {
		return getListOfEntitiesWithinDistance(distance * 1.0D);
	}

	/**
	 * Get a list (EntityList) of entities which are within the given distance
	 * from this entity. Each entry of the list will be the entity reference
	 * (instance of Entity).
	 * 
	 * @param distance
	 *            The distance range from this entity
	 * @return The list of entities within the given range
	 */
	public static EntityList getListOfEntitiesWithinDistance(float distance) {
		return getListOfEntitiesWithinDistance(distance * 1.0D);
	}

	/**
	 * Get a list (EntityList) of entities which belong to the given category.
	 * Each entry of the list will be the entity reference (instance of Entity).
	 * 
	 * @param categoryName
	 *            The name of the category
	 * @return The list of entities which belong to the given category name
	 */
	public static EntityList getListOfEntitiesInCategory(String categoryName) {
		return new EntityList(system.getEntityByCategoryName(categoryName));
	}

	/**
	 * Get a list (EntityList) of entities of the given category which are
	 * within the given distance range. Each entry of the list will be the
	 * entity reference (instance of Entity).
	 * 
	 * @param categoryName
	 *            The category name of the returned entities
	 * @param distance
	 *            The distance range of the returned entities and this entity
	 * @return The list of entities
	 */
	public static EntityList getListOfCategoryEntitiesWithinDistance(
			String categoryName, double distance) {
		EntityList list = getListOfEntitiesWithinDistance(distance);
		List ret = new ArrayList(list.size());
		for (int i = 0; i < list.size(); i++) {
			if (((Category) list.getEntity(i)).getEntityType().equals(
					categoryName)) {
				ret.add(list.getEntity(i));
			}
		}
		return new EntityList(ret);
	}

	/**
	 * Get a list (EntityList) of entities of the given category which are
	 * within the given distance range. Each entry of the list will be the
	 * entity reference (instance of Entity).
	 * 
	 * @param categoryName
	 *            The category name of the returned entities
	 * @param distance
	 *            The distance range of the returned entities and this entity
	 * @return The list of entities
	 */
	public static EntityList getListOfCategoryEntitiesWithinDistance(
			String categoryName, int distance) {
		return getListOfCategoryEntitiesWithinDistance(categoryName,
				distance * 1.0D);
	}

	/**
	 * Get a list (EntityList) of entities of the given category which are
	 * within the given distance range. Each entry of the list will be the
	 * entity reference (instance of Entity).
	 * 
	 * @param categoryName
	 *            The category name of the returned entities
	 * @param distance
	 *            The distance range of the returned entities and this entity
	 * @return The list of entities
	 */
	public static EntityList getListOfCategoryEntitiesWithinDistance(
			String categoryName, float distance) {
		return getListOfCategoryEntitiesWithinDistance(categoryName,
				distance * 1.0D);
	}

	/**
	 * Get the entity with the specified id. If no such entity exists, or the
	 * entity is not active, <code>
		 * null</code> will be returned.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The entity with the id.
	 */
	public static Entity getEntityById(int entityID) {
		Entity entity = system.getEntityById(entityID);
		if (entity == null || !entity.isActive())
			return null;
		return entity;
	}

	/**
	 * Remove the specified entity from the system. If no entity with the given
	 * id is found, no actions will be performed and <code>false</code> will be
	 * returned.
	 * 
	 * @param entityID
	 *            The id of the entity to be removed
	 * @return whether the entity is removed successfully.
	 */
	public static boolean removeEntity(int entityID) {
		Entity entity = system.getEntityById(entityID);
		if (entity == null)
			return false;
		return engine.removeEntity(entityID);
	}

	/**
	 * Create a new entity which belongs to the specified category.
	 * 
	 * @param categoryName
	 *            The category from which an entity is created
	 * @return The id of the newly created entity. -1 If creation is failed.
	 */
	public static int createNewEntityInCategory(String categoryName) {
		try {
			return engine.createNewEntityInCategory(categoryName);
		} catch (Exception e) {
			MessageUtils.error(SystemFunction.class,
					"createNewEntityInCategory", e);
			return -1;
		}
	}

	/**
	 * Create a new entity by copying an existing entity. If the entity to copy
	 * from is not in existence, no actions will be performed and -1 will be
	 * returned. Also if the entity can not be created, -1 will be returned.
	 * 
	 * @param originalEntityID
	 *            The id of the entity to copy from
	 * @return The id of the newly copied entity
	 */
	public static int createNewEntityByCopyEntity(int originalEntityID) {
		Entity entity = system.getEntityById(originalEntityID);
		if (entity == null)
			return -1;
		try {
			return engine.createNewEntityByCopyEntity(originalEntityID);
		} catch (Exception ee) {
			ee.printStackTrace();
			return -1;
		}
	}

	/***
	 * Return the number of system entities.
	 * 
	 * @return The number of system entities.
	 */
	public static int getEntityCount() {
		return system.getAvailableEntities().size();
	}

	/**
	 * Get the <code>x</code> position of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The x position of the entity. If no such entity exists,
	 *         <code>Double.MAX_VALUE</code> will be returned.
	 */
	public static double getPositionX(int entityID) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return Double.MAX_VALUE;
		return entity.getPosition().x;
	}

	/**
	 * Get the <code>y</code> position of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The y position of the entity. If no such entity exists,
	 *         <code>Double.MAX_VALUE</code> will be returned.
	 */
	public static double getPositionY(int entityID) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return Double.MAX_VALUE;
		return entity.getPosition().y;
	}

	/**
	 * Return the current position of the specified entity. This function will
	 * return a instance of {@link #sim.util.Vect}. The x position will be saved in dx,
	 * and the y position will be saved in dy.
	 * 
	 * @param entityID
	 *            The id of the entity whose position is probed.
	 * @return The current position of the specified entity.
	 */
	public static Vect getPosition(int entityID) {
		return new Vect(getPositionX(entityID), getPositionY(entityID));
	}

	/**
	 * Return the relative position from this entity to the specified entity.
	 * This function will return a instance of {@link #sim.util.Vect}. The relative x
	 * position will be saved in dx, and the relative y position will be saved
	 * in dy.
	 * 
	 * @param entityID
	 *            The id of the entity whose position is probed.
	 * @return The relative position to the specified entity.
	 */
	public Vect getRelativePosition(int entityID) {
		Entity self = (Entity) this;
		return new Vect(getPositionX(entityID) - self.getPosition().x,
				getPositionY(entityID) - self.getPosition().y);
	}

	/**
	 * Return the relative position from the specified entity to this entity.
	 * This function will return a instance of {@link #sim.util.Vect}. The relative x
	 * position will be saved in dx, and the relative y position will be saved
	 * in dy.
	 * 
	 * @param entityID
	 *            The id of the entity whose position is probed.
	 * @return The relative position from the specified entity .
	 */
	public Vect getReverseRelativePosition(int entityID) {
		Entity self = (Entity) this;
		return new Vect(-getPositionX(entityID) + self.getPosition().x,
				-getPositionY(entityID) + self.getPosition().y);
	}

	/**
	 * Get the <code>currentDirection</code> of the entity which is specified by
	 * the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The heading direction of the entity. If no such entity exists,
	 *         <code>Double.MAX_VALUE</code> will be returned.
	 */
	public static double getHeadingDirection(int entityID) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return Double.MAX_VALUE;
		return entity.getDirection();
	}

	/**
	 * Get the <code>movingSpeed</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The moving speed of the entity. If no such entity exists,
	 *         <code>Double.MAX_VALUE</code> will be returned.
	 */
	public static double getSpeed(int entityID) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return Double.MAX_VALUE;
		return entity.getSpeed();
	}

	/**
	 * Set the <code>movingSpeed</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newSpeed
	 *            The moving speed to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setSpeed(int entityID, double newSpeed) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return;
		entity.setSpeed(newSpeed);
	}

	/**
	 * Set the <code>movingSpeed</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newSpeed
	 *            The moving speed to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setSpeed(int entityID, int newSpeed) {
		setSpeed(entityID, newSpeed * 1.0D);
	}

	/**
	 * Set the <code>movingSpeed</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newSpeed
	 *            The moving speed to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setSpeed(int entityID, float newSpeed) {
		setSpeed(entityID, newSpeed * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, double newX, double newY) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return;
		entity.setPosition(newX, newY);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, double newX, int newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, double newX, float newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, int newX, int newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, int newX, float newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, int newX, double newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, float newX, int newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, float newX, float newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>position</code> of the entity which is specified by the id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newX
	 *            The x coordination of the entity to be set
	 * @param newY
	 *            The y coordination of the entity to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setPosition(int entityID, float newX, double newY) {
		setPosition(entityID, newX * 1.0D, newY * 1.0D);
	}

	/**
	 * Set the <code>direction</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newDirection
	 *            The direction to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setDirection(int entityID, double newDirection) {
		Entity entity = getEntityById(entityID);
		if (entity == null)
			return;
		entity.setDirection(newDirection);
	}

	/**
	 * Set the <code>direction</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newDirection
	 *            The direction to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setDirection(int entityID, int newDirection) {
		setDirection(entityID, newDirection * 1.0D);
	}

	/**
	 * Set the <code>direction</code> of the entity which is specified by the
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @param newDirection
	 *            The direction to be set
	 * @return The moving speed of the entity. If no such entity exists, no
	 *         actions will be performed.
	 */
	public static void setDirection(int entityID, float newDirection) {
		setDirection(entityID, newDirection * 1.0D);
	}

	/**
	 * Get the distance from this entity to the entity which is specified by id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The distance to the entity specified by id
	 */
	public static double getDistanceToEntity(int entityID) {
		Entity self = engine.getCurrentEntity();
		Point pos = getPrevEntityPos(entityID, self.getTime() /*- 1 >= 0 ? self.getTime() - 1 : 0*/);
		if (pos != null)
			return self.getPosition().dist(pos);
		return Double.MAX_VALUE;
	}

	/**
	 * Return the previous position of the entity with the specified display
	 * name.
	 * 
	 * <p>
	 * The entity with the display name is retrieved and the position is
	 * returned
	 * </p>
	 * 
	 * @param entityID
	 *            The id of the target entity
	 * @param timeStick
	 *            The specified time the position is computed
	 * @return The previous entity position. If
	 */
	private static Point getPrevEntityPos(int entityID, int timeStick) {
		Point p = null;
		Entity entity = getEntityById(entityID);
		if (entity != null)
			p = system.getPreviousEntityPosition(entity, timeStick);
		return p;
	}

	/**
	 * Get the distance between two entities, specified by <code>
		 * oneID</code> and
	 * <code>anotherID</code>.
	 * 
	 * @param oneID
	 *            The first one
	 * @param anotherID
	 *            The second one
	 * @return The distance between these two entities
	 */
	public static double getDistanceBetweenTwoEntities(int oneID, int anotherID) {
		Entity one = getEntityById(oneID);
		Point pos = getPrevEntityPos(anotherID, one.getTime() /*- 1 >= 0 ? one.getTime() - 1 : 0*/);
		if (pos != null)
			return one.getPosition().dist(pos);
		return Double.MAX_VALUE;
	}

	/**
	 * Turn to the specified direction. This function will change the heading
	 * direction of current agent into the given direction.
	 * 
	 * @param direction
	 *            The new heading direction of this agent
	 */
	public void turnTo(double direction) {
		Entity self = (Entity) this;
		self.setDirection(direction);
	}

	/**
	 * Turn to the specified direction. This function will change the heading
	 * direction of current agent into the given direction.
	 * 
	 * @param direction
	 *            The new heading direction of this agent
	 */
	public void turnTo(int direction) {
		turnTo(direction * 1.0D);
	}

	/**
	 * Turn to the specified direction. This function will change the heading
	 * direction of current agent into the given direction.
	 * 
	 * @param direction
	 *            The new heading direction of this agent
	 */
	public void turnTo(float direction) {
		turnTo(direction * 1.0D);
	}

	/**
	 * Get the direction from this entity to the entity which is specified by
	 * id.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The direction to the entity specified by id. If the position of
	 *         the specified entity can not be retrieved,
	 *         <code>Double.MAX_VALUE</code> will be returned.
	 */
	public static double getDirectionToEntity(int entityID) {
		Entity self = engine.getCurrentEntity();
		Point currentPosition = self.getPosition();
		Point p = getPrevEntityPos(entityID, self.getTime() /*- 1 <= 0 ? 0 : self.getTime()*/);
		if (p == null)
			return Double.MAX_VALUE;
		// Vector oriented towards the entity specified by entityID
		Vect towardDir = currentPosition.difference(p).unit();
		return towardDir.angle();
	}

	/**
	 * Return the reverse direction from this entity to the another one, based
	 * on the current time slice of the caller.
	 * 
	 * @param myID
	 *            My entity id
	 * @param otherID
	 *            Entity id of another entity
	 * @return The direction
	 */
	public static double getReverseDirectionToEntity(int myID, int otherID) {
		// The self entity and other entity
		Entity self = getEntityById(myID);
		Entity other = getEntityById(otherID);
		if (self == null || other == null)
			return Double.MAX_VALUE;
		// Position of self entity
		Point currentPosition = self.getPosition();
		// Position of another entity
		Point anotherPosition = system.getPreviousEntityPosition(other, self
				.getTime()/*- 1 <= 0 ? 0 : self.getTime()*/);
		// Compute the difference of two positions
		Vect diff = anotherPosition.difference(currentPosition);
		return diff.unit().angle();
	}

	/**
	 * Get the closest distance to the specified category from this entity
	 * 
	 * @param categoryName
	 *            The name of the category
	 * @return The closest distance. If no entities are configured for that
	 *         category, <code>Double.MAX_VALUE</code> will be returned.
	 */
	public static double getClosestDistanceToCategory(String categoryName) {
		int closestEntityID = getClosestEntityInCategory(categoryName);
		Entity self = engine.getCurrentEntity();
		Point pos = getPrevEntityPos(closestEntityID, self.getTime() /*- 1 <= 0 ? 0 : self.getTime()*/);
		if (pos != null)
			return self.getPosition().dist(pos);
		return Double.MAX_VALUE;
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			double posX, double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			double posX, int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			double posX, float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			float posX, double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			float posX, int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			float posX, float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			int posX, double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			int posX, int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, double direction,
			int posX, float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction,
			double posX, double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction,
			double posX, int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction,
			double posX, float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction,
			float posX, double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction,
			float posX, int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction,
			float posX, float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction, int posX,
			double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction, int posX,
			int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, float direction, int posX,
			float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction,
			double posX, double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction,
			double posX, int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction,
			double posX, float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction, float posX,
			double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction, float posX,
			int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction, float posX,
			float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction, int posX,
			double posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction, int posX,
			int posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment.
	 * 
	 * @param entityID
	 *            The id of the source
	 * @param direction
	 *            The source direction
	 * @param posX
	 *            The position x of the source
	 * @param posY
	 *            The position y of the source
	 * @return The closest IR distance
	 */
	public static double getIRDistance(int entityID, int direction, int posX,
			float posY) {
		return system.getIRDistance(entityID, direction, posX, posY);
	}

	/**
	 * Get the closest IR direction from this entity to all other entities,
	 * including the simulation environment.
	 * 
	 * @return The closest IR distance to other entities
	 */
	public double getIRDistanceToOthers() {
		Entity self = (Entity) this;
		return getIRDistance(self.getMyId(), self.getDirection(), self
				.getPosition().x, self.getPosition().y);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, double positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, double positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, double positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */

	public double getIRDistanceToCategory(double direction, float positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, float positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, float positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, int positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, int positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(double direction, int positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */

	public double getIRDistanceToCategory(float direction, double positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, double positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, double positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */

	public double getIRDistanceToCategory(float direction, float positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, float positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, float positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, int positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, int positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(float direction, int positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, double positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, double positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, double positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */

	public double getIRDistanceToCategory(int direction, float positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, float positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, float positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, int positionX,
			double positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, int positionX,
			float positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category.
	 * 
	 * @param direction
	 *            The direction of the source entity
	 * @param positionX
	 *            The position x of the source entity
	 * @param positionY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int direction, int positionX,
			int positionY, String catName) {
		int entityID = ((Entity) this).getMyId();
		return system.getIRDistanceToCategory(entityID, direction, positionX,
				positionY, catName);
	}

	/**
	 * Get the behavior activation of the last time step. Note that, only
	 * the activation of the first behavior with the given name will be 
	 * returned.
	 * 
	 * @param behaviorName
	 *            The name of the behavior
	 * @return The behavior activation
	 * 
	 */
	public static double getBehaviorActivation(String behaviorName) {
		return engine.bnEditor.getBehaviorActivation(behaviorName);
	}

	/**
	 * Update the coefficients of behavior network. It is only used in the
	 * mutual inhibition mechanism. The coefficients are specified through a
	 * two-dimensional array. The first dimension specifies the row and the
	 * second dimension is corresponding to the column.
	 * 
	 * <p>
	 * For instance, suppose the coefficients table is as follows, <br>
	 * behavior i behavior j behavior i 0.0 0.5 behavior j 0.4 0.0 <br>
	 * Then the two dimensional array is specified as follows,<br>
	 * new double[][]{{0.0, 0.5}, {0.4, 0.0}}
	 * </p>
	 * 
	 * <p>
	 * If the array is null or length (for each dimension) is 0, the function
	 * will return without any change to the behavior network. It is required to
	 * contain all coefficients in the array.
	 * </p>
	 * 
	 * @param coefficients
	 *            The coefficients for mutual inhibition mechanism and weights
	 *            for cooperative mechanism
	 */
	public static void setBehaviorNetworkTable(double[][] coefficients) {
		if (coefficients == null || coefficients.length == 0
				|| coefficients[0].length == 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coefficients.length; i++) {
			for (int j = 0; j < coefficients[0].length; j++) {
				sb.append(coefficients[i][j]).append(" ");
			}
		}
		engine.bnEditor.setBehaviorNetworkTable(sb.toString());
	}

	/**
	 * Update the coefficients of behavior network. It is only used in the
	 * mutual inhibition mechanism. The coefficients are specified through a
	 * two-dimensional array. The first dimension specifies the row and the
	 * second dimension is corresponding to the column.
	 * 
	 * <p>
	 * For instance, suppose the coefficients table is as follows, <br>
	 * behavior i behavior j behavior i 0.0 0.5 behavior j 0.4 0.0 <br>
	 * Then the two dimensional array is specified as follows,<br>
	 * new double[][]{{0.0, 0.5}, {0.4, 0.0}}
	 * </p>
	 * 
	 * <p>
	 * If the array is null or length (for each dimension) is 0, the function
	 * will return without any change to the behavior network. It is required to
	 * contain all coefficients in the array.
	 * </p>
	 * 
	 * <p>
	 * Due to the limitation of Javassist, this method does not work!!!
	 * </p>
	 * 
	 * @param coefficients
	 *            The coefficients for mutual inhibition mechanism and weights
	 *            for cooperative mechanism
	 */
	public static void setBehaviorNetworkTable(int[][] coefficients) {
		if (coefficients == null || coefficients.length == 0
				|| coefficients[0].length == 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coefficients.length; i++) {
			for (int j = 0; j < coefficients[0].length; j++) {
				sb.append(coefficients[i][j]).append(" ");
			}
		}
		engine.bnEditor.setBehaviorNetworkTable(sb.toString());
	}

	/**
	 * Update the coefficients of behavior network. It is only used in the
	 * mutual inhibition mechanism. The coefficients are specified through a
	 * two-dimensional array. The first dimension specifies the row and the
	 * second dimension is corresponding to the column.
	 * 
	 * <p>
	 * For instance, suppose the coefficients table is as follows, <br>
	 * behavior i behavior j behavior i 0.0 0.5 behavior j 0.4 0.0 <br>
	 * Then the two dimensional array is specified as follows,<br>
	 * new double[][]{{0.0, 0.5}, {0.4, 0.0}}
	 * </p>
	 * 
	 * <p>
	 * If the array is null or length (for each dimension) is 0, the function
	 * will return without any change to the behavior network. It is required to
	 * contain all coefficients in the array.
	 * </p>
	 * 
	 * <p>
	 * Due to the limitation of Javassist, this method does not work!!!
	 * </p>
	 * 
	 * @param coefficients
	 *            The coefficients for mutual inhibition mechanism and weights
	 *            for cooperative mechanism
	 */
	public static void setBehaviorNetworkTable(float[][] coefficients) {
		if (coefficients == null || coefficients.length == 0
				|| coefficients[0].length == 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coefficients.length; i++) {
			for (int j = 0; j < coefficients[0].length; j++) {
				sb.append(coefficients[i][j]).append(" ");
			}
		}
		engine.bnEditor.setBehaviorNetworkTable(sb.toString());
	}

	/**
	 * Update the coefficients of behavior network. All coefficients or weights
	 * are list in the <code>String</code>, separating by blanks, like
	 * "1.0 0.2 0.8 0.7".
	 * 
	 * 
	 * It is up to the user to ensure the correct order of coefficients. They
	 * should be the same order of the behaviors added to the network. (HINT:
	 * the order shown in the coefficients/weights table, which is under the
	 * behavior network).
	 * 
	 * <p>
	 * For instance, suppose the coefficients table is as follows, <br>
	 * &nbsp;&nbsp;behavior i behavior j <br>
	 * behavior i 0.0 0.5 <br>
	 * behavior j 0.4 0.0 <br>
	 * <br>
	 * Then the coefficients are specified as follows,<br>
	 * "0.5 0.4". The diagonal elements are skipped.
	 * </p>
	 * 
	 * @param coefficients
	 *            The coefficients for mutual inhibition mechanism and weights
	 *            for cooperative mechanism
	 */
	public static void setBehaviorNetworkTable(String coefficients) {
		engine.bnEditor.setBehaviorNetworkTable(coefficients);
	}

	/**
	 * Set behavior weights. It is only used in the cooperative mechanism. The
	 * weights are specified through a double array. The order should be same as
	 * the order of behavior name labels.
	 * 
	 * <p>
	 * For instance, suppose the weights table is as follows, <br>
	 * 
	 * behavior i 0.2 behavior j 0.4 <br>
	 * Then the array is specified as follows,<br>
	 * new double[]{0.2, 0.4}
	 * </p>
	 * 
	 * <p>
	 * If the array is null or length is 0, the function will return without any
	 * change to the behavior network. It is required to contain all weights in
	 * the array.
	 * </p>
	 * 
	 * @param weights
	 *            The new behavior weights to set
	 */
	public static void setBehaviorWeights(double[] weights) {
		if (weights == null || weights.length == 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < weights.length; i++) {
			sb.append(weights[i]).append(" ");
		}
		engine.bnEditor.setBehaviorNetworkTable(sb.toString());
	}

	/**
	 * Set behavior weights. It is only used in the cooperative mechanism. The
	 * weights are specified through a double array. The order should be same as
	 * the order of behavior name labels.
	 * 
	 * <p>
	 * For instance, suppose the weights table is as follows, <br>
	 * 
	 * behavior i 0.2 behavior j 0.4 <br>
	 * Then the array is specified as follows,<br>
	 * new double[]{0.2, 0.4}
	 * </p>
	 * 
	 * <p>
	 * If the array is null or length is 0, the function will return without any
	 * change to the behavior network. It is required to contain all weights in
	 * the array.
	 * </p>
	 * 
	 * @param weights
	 *            The new behavior weights to set
	 */
	public static void setBehaviorWeights(float[] weights) {
		if (weights == null || weights.length == 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < weights.length; i++) {
			sb.append(weights[i]).append(" ");
		}
		engine.bnEditor.setBehaviorNetworkTable(sb.toString());
	}

	/**
	 * Set behavior weights. It is only used in the cooperative mechanism. The
	 * weights are specified through a double array. The order should be same as
	 * the order of behavior name labels.
	 * 
	 * <p>
	 * For instance, suppose the weights table is as follows, <br>
	 * 
	 * behavior i 0.2 behavior j 0.4 <br>
	 * Then the array is specified as follows,<br>
	 * new double[]{0.2, 0.4}
	 * </p>
	 * 
	 * <p>
	 * If the array is null or length is 0, the function will return without any
	 * change to the behavior network. It is required to contain all weights in
	 * the array.
	 * </p>
	 * 
	 * @param weights
	 *            The new behavior weights to set
	 */
	public static void setBehaviorWeights(int[] weights) {
		if (weights == null || weights.length == 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < weights.length; i++) {
			sb.append(weights[i]).append(" ");
		}
		engine.bnEditor.setBehaviorNetworkTable(sb.toString());
	}

	/**
	 * Set the weight of the specified behavior. It is only used in the
	 * cooperative mechanism.
	 * 
	 * @param behaviorName
	 *            The name of the behavior to set
	 * @param weight
	 *            The new weight of the behavior
	 */
	public static void setBehaviorWeight(String behaviorName, double weight) {
		engine.bnEditor.setBehaviorWeight(behaviorName, weight);
	}

	/**
	 * Set the weight of the specified behavior. It is only used in the
	 * cooperative mechanism.
	 * 
	 * @param behaviorName
	 *            The name of the behavior to set
	 * @param weight
	 *            The new weight of the behavior
	 */
	public static void setBehaviorWeight(String behaviorName, float weight) {
		engine.bnEditor.setBehaviorWeight(behaviorName, weight);
	}

	/**
	 * Set the weight of the specified behavior. It is only used in the
	 * cooperative mechanism.
	 * 
	 * @param behaviorName
	 *            The name of the behavior to set
	 * @param weight
	 *            The new weight of the behavior
	 */
	public static void setBehaviorWeight(String behaviorName, int weight) {
		engine.bnEditor.setBehaviorWeight(behaviorName, weight);
	}

	/**
	 * Move forward compute entity at the speed and direction in one time step.
	 * It is mainly used in the behavior-based control (behavior action of
	 * mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(double speed, double direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward this entity at the speed and
	 * direction in the next time step. The actual movement is NOT carried out
	 * in the method call until the call of the corresponding action selection
	 * mechanism is finished. It is mainly used in the behavior-based control
	 * (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(double speed, float direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(double speed, int direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward this entity at the speed and
	 * direction in the next time step. The actual movement is NOT carried out
	 * in the method call until the call of the corresponding action selection
	 * mechanism is finished. It is mainly used in the behavior-based control
	 * (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(float speed, double direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(float speed, float direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward this entity at the speed and
	 * direction in the next time step. The actual movement is NOT carried out
	 * in the method call until the call of the corresponding action selection
	 * mechanism is finished. It is mainly used in the behavior-based control
	 * (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(float speed, int direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */

	public static String move(int speed, double direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward this entity at the speed and
	 * direction in the next time step. The actual movement is NOT carried out
	 * in the method call until the call of the corresponding action selection
	 * mechanism is finished. It is mainly used in the behavior-based control
	 * (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(int speed, float direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String move(int speed, int direction) {
		return system.getSpeedVector(speed, direction, MOVEFORWARD);
	}

	/**
	 * Return a string representing moving forward this entity at the speed and
	 * direction in the next time step. The actual movement is NOT carried out
	 * in the method call until the call of the corresponding action selection
	 * mechanism is finished. It is mainly used in the behavior-based control
	 * (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(double speed, double direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(double speed, float direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward this entity at the speed and
	 * direction in the next time step. The actual movement is NOT carried out
	 * in the method call until the call of the corresponding action selection
	 * mechanism is finished. It is mainly used in the behavior-based control
	 * (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(double speed, int direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(float speed, double direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(float speed, float direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(float speed, int direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(int speed, double direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(int speed, float direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving forward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveForward(int speed, int direction) {
		return move(speed, direction);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(double speed, double direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(double speed, float direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(double speed, int direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(float speed, double direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(float speed, float direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(float speed, int direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(int speed, double direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(int speed, float direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Return a string representing moving backward compute entity at the speed
	 * and direction in the next time step. The actual movement is NOT carried
	 * out in the method call until the call of the corresponding action
	 * selection mechanism is finished. It is mainly used in the behavior-based
	 * control (behavior action of mutual inhibition or vector sum).
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 * @return speed vector, in the format "speed+","+direction"
	 */
	public static String moveBackward(int speed, int direction) {
		return system.getSpeedVector(speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(double speed, double direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(double speed, float direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(double speed, int direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(float speed, double direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(float speed, float direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(float speed, int direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(int speed, double direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(int speed, float direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void move2(int speed, int direction) {
		system.move((Entity) this, speed, direction, MOVEFORWARD);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(double speed, double direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(double speed, float direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(double speed, int direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(float speed, double direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(float speed, float direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(float speed, int direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(int speed, double direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(int speed, float direction) {
		move2(speed, direction);
	}

	/**
	 * Move forward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveForward2(int speed, int direction) {
		move2(speed, direction);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(double x, double y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(double x, float y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(double x, int y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(float x, double y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(float x, float y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(float x, int y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(int x, double y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(int x, float y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move the compute entity to the given position. It is mainly used in
	 * general dynamics.
	 * 
	 * @param x
	 *            The x coordination of the destination
	 * @param y
	 *            The y coordination of the destination
	 */
	public void moveTo(int x, int y) {
		Entity self = (Entity) this;
		self.setPosition(x, y);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(double speed, double direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(double speed, float direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(double speed, int direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(float speed, double direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(float speed, float direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(float speed, int direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(int speed, double direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(int speed, float direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Move backward the compute entity at the speed and direction in one time
	 * step. It is mainly used in the general dynamics.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param direction
	 *            Moving direction
	 */
	public void moveBackward2(int speed, int direction) {
		system.move((Entity) this, speed, direction, MOVEBACKWARD);
	}

	/**
	 * Turn counter clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turn(double angularSpeed) {
		system.turnWithAngularSpeed(angularSpeed, ROTATECOUNTERCLOCKWISE);
	}

	/**
	 * Turn counter clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turn(float angularSpeed) {
		system.turnWithAngularSpeed(angularSpeed, ROTATECOUNTERCLOCKWISE);
	}

	/**
	 * Turn counter clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turn(int angularSpeed) {
		system.turnWithAngularSpeed(angularSpeed, ROTATECOUNTERCLOCKWISE);
	}

	/**
	 * Turn counter clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turnLeft(double angularSpeed) {
		turn(angularSpeed);
	}

	/**
	 * Turn counter clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turnLeft(float angularSpeed) {
		turn(angularSpeed);
	}

	/**
	 * Turn counter clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turnLeft(int angularSpeed) {
		turn(angularSpeed);
	}

	/**
	 * Turn clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turnRight(double angularSpeed) {
		system.turnWithAngularSpeed(angularSpeed, ROTATECLOCKWISE);
	}

	/**
	 * Turn clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turnRight(float angularSpeed) {
		system.turnWithAngularSpeed(angularSpeed, ROTATECLOCKWISE);
	}

	/**
	 * Turn clockwise with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 */
	public static void turnRight(int angularSpeed) {
		system.turnWithAngularSpeed(angularSpeed, ROTATECLOCKWISE);
	}

	/**
	 * Return the value of the specified name of user-defined field of the
	 * compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public Object getValue(String fieldName) throws Exception {
		return MethodUtils.invokeExactField(this, fieldName);
	}

	/**
	 * Return the double value of the specified name of user-defined field of
	 * the compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The double value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public double getDoubleValue(String fieldName) throws Exception {
		Object o = MethodUtils.invokeExactField(this, fieldName);
		if (o != null && o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		throw new RuntimeException("The returned value is not a double!");
	}

	/**
	 * Return the float value of the specified name of user-defined field of
	 * the compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The float value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public float getFloatValue(String fieldName) throws Exception {
		Object o = MethodUtils.invokeExactField(this, fieldName);
		if (o != null && o instanceof Number) {
			return ((Number) o).floatValue();
		}
		throw new RuntimeException("The returned value is not a double!");
	}
	
	/**
	 * Return the String value of the specified name of user-defined field of
	 * the compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The String value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public String getStringValue(String fieldName) throws Exception {
		Object o = MethodUtils.invokeExactField(this, fieldName);
		if (o != null && o instanceof String) {
			return (String) o;
		}
		throw new RuntimeException("The returned value is not a string!");
	}

	/**
	 * Set the value of the specified field of the compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The property name in the entity
	 * @param value
	 *            The value to be set
	 * @throws Exception
	 *             If the property value can not be set properly.
	 */
	public void setValue(String fieldName, Object value) throws Exception {
		((Category) this).updateProperty(fieldName, String.valueOf(value));
	}

	/**
	 * Set the double value of the specified field of the compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The property name in the entity
	 * @param value
	 *            The value to be set
	 * @throws Exception
	 *             If the property value can not be set properly.
	 */
	public void setDoubleValue(String fieldName, double value) throws Exception {
		setValue(fieldName, new Double(value));
	}

	/**
	 * Set the String value of the specified field of the compute entity.
	 * 
	 * 
	 * @param fieldName
	 *            The property name in the entity
	 * @param value
	 *            The value to be set
	 * @throws Exception
	 *             If the property value can not be set properly.
	 */
	public void setStringValue(String fieldName, String value) throws Exception {
		setValue(fieldName, value);
	}

	/**
	 * Return the value of the specified field of the compute entity.
	 * 
	 * 
	 * @param entityID
	 *            The id of the entity whose field is to be probed
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public Object getValue(int entityID, String fieldName) throws Exception {
		return MethodUtils.invokeExactField(getEntityById(entityID), fieldName);
	}

	/**
	 * Return the double value of the specified field of the compute entity.
	 * 
	 * 
	 * @param entityID
	 *            The id of the entity whose field is to be probed
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public double getDoubleValue(int entityID, String fieldName)
			throws Exception {
		Object o = MethodUtils.invokeExactField(getEntityById(entityID),
				fieldName);
		if (o != null && o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		throw new RuntimeException("The returned value is not a double!");
	}

	/**
	 * Return the String value of the specified field of the compute entity.
	 * 
	 * 
	 * @param entityID
	 *            The id of the entity whose field is to be probed
	 * @param fieldName
	 *            The field name whose value is returned
	 * @return The value of the specified field.
	 * @throws If
	 *             the value can not be returned successfully
	 */
	public String getStringValue(int entityID, String fieldName)
			throws Exception {
		Object o = MethodUtils.invokeExactField(getEntityById(entityID),
				fieldName);
		if (o != null && o instanceof String) {
			return (String) o;
		}
		throw new RuntimeException("The returned value is not a string!");
	}

	/**
	 * Set the value of the self-defined field of the compute entity
	 * 
	 * @param entityID
	 *            The id of the entity whose field is to be probed
	 * @param fieldName
	 *            The field name
	 * @param value
	 *            The value to be set
	 * @throws Exception
	 *             If the value can not be set properly.
	 */
	public void setValue(int entityID, String fieldName, Object value)
			throws Exception {
		((Category) getEntityById(entityID)).updateProperty(fieldName, String
				.valueOf(value));
	}

	/**
	 * Set the value of the self-defined field of the compute entity
	 * 
	 * @param entityID
	 *            The id of the entity whose field is to be probed
	 * @param fieldName
	 *            The field name
	 * @param value
	 *            The value to be set
	 * @throws Exception
	 *             If the value can not be set properly.
	 */
	public void setDoubleValue(int entityID, String fieldName, double value)
			throws Exception {
		setValue(entityID, fieldName, new Double(value));
	}

	/**
	 * Set the value of the self-defined field of the compute entity
	 * 
	 * @param entityID
	 *            The id of the entity whose field is to be probed
	 * @param fieldName
	 *            The field name
	 * @param value
	 *            The value to be set
	 * @throws Exception
	 *             If the value can not be set properly.
	 */
	public void setStringValue(int entityID, String fieldName, String value)
			throws Exception {
		setValue(entityID, fieldName, value);
	}
}
