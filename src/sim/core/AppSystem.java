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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import sim.model.action.BehaviorActionRepository;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.entity.BNCategory;
import sim.model.entity.Category;
import sim.model.entity.Display;
import sim.model.entity.Entity;
import sim.model.entity.EntityRecord;
import sim.model.entity.SystemFunction;
import sim.model.mechanism.IMechanism;
import sim.util.Point;
import sim.util.Vect;

/**
 * Application System Object. It is used to keep the entity list and all
 * relative operations, such as obtaining entities by specified conditions,
 * compute simulation data, etc.
 * 
 * @author Fasheng Qiu
 * @version
 * @since 11/20/2007
 */
public class AppSystem {

	/* Constructor, used internally */
	AppSystem() {
		// system parameters
		systemParameters = new ConfigParameters();
		// define a time interval for each tick - for 0.5 seconds total time is
		// 1000 seconds or 16 min 40 sec
		data = new SimulationData(systemParameters.getTotalTimeticks()); // initial
		// simulation
		// data
		// and
		// subsequent
		// environment type property
		env.setType(systemParameters.getEnvType());
		// action repository
		actionRepository = new BehaviorActionRepository();
	}

	/* All configured entities */
	private List entityList = new ArrayList();

	/* All configured entities which are stored in hash table */
	private Hashtable entityTypesTable = new Hashtable();

	/* Simulation data of all configured entities */
	private SimulationData data = null;

	/* Simulation environment object */
	public final SimulationEnvironment env = new SimulationEnvironment();

	/* System global parameters */
	public ConfigParameters systemParameters = null;

	/* Behavior action repository */
	public BehaviorActionRepository actionRepository = null;

	/**
	 * Reset the simulation and re-initialize all entities to the initial state
	 * 
	 * @param initialPos Initial positions
	 * 
	 */
	public void resetSimulation(Map initialPos) {

		// Reset the simulation data since the initial states of each
		// entity have been changed because of the wrap-around implementation.
		// The initial states should be reset to the states when an entity
		// is added to the system.
		data.reset();

		for (int i = 0; i < entityList.size(); i++) {

			// Initialize the entity by calling the user-override method
			Entity current = (Entity) entityList.get(i);
			current.setTime(0);
			current.init();
			// Initialize the entity by calling the system internal
			// initialization method
			current._initInternal();
			if (current instanceof BNCategory) {
				BNCategory c = (BNCategory) current;
				// Save in engine
				AppEngine.getInstance().appManager.getCurrentApp()
						.setCurrentEntity(c);
				// Debug and show the methods list
				// MethodUtils.probeMethodsList(current);
				List behaviors = c.getBehaviorNetwork().getBehaviorList();
				for (int j = 0; j < behaviors.size(); j++) {
					Behavior b = (Behavior) behaviors.get(j);
					b.resetAction();
					b.resetActionIndex();
				}
			}
			
			// Change the initial parameters first since its state may be
			// changed
			// because of the size change of the world.
			changeEntityInitialParameters(current, initialPos);
		}

	}

	/**
	 * Add a entity into the system
	 * 
	 * @param entity
	 *            The entity to add
	 * @param name
	 *            The entity's name
	 */
	public void addEntity(Entity entity) {
		// Initialize the entity
		entity.init();
		// Initialize id
		entity.setId(entityList.size());
		// Add entity to a list of entities
		entityList.add(entity);
		// Add a record for an entity
		data.add(entity);
		// Add entity to the type
		_addEntityToType(entity);
		// Assign a display name
		_addDisplayName(entity);
		// Assume initial position for an entity
		// what is initial motion direction?
		changeEntityInitialParameters(entity);
	}

	/**
	 * In the case of no display name, an new entity will be assigned a display
	 * name based on an available index of the display names of the category.
	 * 
	 * @param entity
	 *            Entity to add and to check.
	 */
	private void _addDisplayName(Entity entity) {
		if (entity.getDisplayName() == null) {
			entity.setDisplayName(entity.getEntityType()
					+ systemParameters.getDisplayNameIndex(entity
							.getEntityType()));
		}
	}

	/**
	 * Obtain the available entity list
	 * 
	 * @return The available entity list
	 */
	public List getAvailableEntities() {
		return entityList;
	}

	/**
	 * Update the information for the specified entity
	 * 
	 * @param entityID
	 *            The id of the entity to update
	 * @param newEntity
	 *            New info
	 */
	public void updateEntityDisplayName(int entityID, String displayName) {
		for (int i = 0; i < entityList.size(); i++) {
			Entity entity = (Entity) entityList.get(i);
			if (entity.getMyId() == entityID) {
				entity.setDisplayName(displayName);
				break;
			}
		}
	}

	/**
	 * Resize and reposition entities because of the change in properties of
	 * simulation world. Note that the simulation data is not changed.
	 * 
	 * @param widthRatio
	 *            The change in the world width, from 0 to 1
	 * @param heightRatio
	 *            The change in the world height, from 0 to 1
	 */
	public void resizeAndRepositionEntities(double widthRatio,
			double heightRatio) {
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = (Entity) entityList.get(i);
			Display d = e.getOrdiginalDisplay();
			d.setWidth((int) (d.getWidth() * widthRatio));
			d.setHeight((int) (d.getHeight() * heightRatio));
			e.setPosition(e.getPosition().x * widthRatio, e.getPosition().y
					* heightRatio);
		}
	}

	/**
	 * Obtain the entity in the given rectangle
	 * 
	 * @param detectRect
	 *            The detection retangle
	 * @return The entity retrieved
	 */
	public Entity getEntityByDetectRectangle(Rectangle detectRect) {
		Entity entity = null;
		for (int i = 0; i < entityList.size(); i++) {
			Entity current = (Entity) entityList.get(i);
			if (current.getDetectRect().equals(detectRect)) {
				entity = current;
				break;
			}
		}
		return entity;
	}

	/**
	 * Obtain the entity list based on the given prefix.
	 * 
	 * @param prefix
	 *            The prefix of the display name
	 * @return The entity list retrieved
	 */
	public List getEntitiesBeginWithName(String prefix) {
		/** Entity list retrieved */
		List entitiesList = new ArrayList();
		/** Search for the entities which begin with the prefix */
		for (int i = 0; i < entityList.size(); i++) {
			Entity current = (Entity) entityList.get(i);
			if (current.getDisplayName().startsWith(prefix)) {
				entitiesList.add(current);
			}
		}
		return entitiesList;
	}

	/**
	 * Obtain the entity based on the given displayName. If 
	 * multiple entities have same name, the first matched 
	 * will be returned.
	 * 
	 * @param displayName
	 *            The entity's display name
	 * @return The entity retrieved
	 */
	public Entity getEntityByDisplayName(String displayName) {
		Entity entity = null;
		for (int i = 0; i < entityList.size(); i++) {
			Entity current = (Entity) entityList.get(i);
			if (current.getDisplayName().equals(displayName)) {
				entity = current;
				break;
			}
		}
		return entity;
	}

	/**
	 * Obtain the entity list based on the given category name. The entities are
	 * active.
	 * 
	 * 
	 * @param catName
	 *            The entity's category name
	 * @return The entity list retrieved
	 */
	public List getEntityByCategoryName(String categoryName) {
		List categories = new ArrayList(entityList.size());
		for (int i = 0; i < entityList.size(); i++) {
			Category current = (Category) entityList.get(i);
			if (current.getEntityType().equals(categoryName)
					&& current.isActive()) {
				categories.add(current);
			}
		}
		return categories;
	}

	/**
	 * Obtain the closest entity based on the given type
	 * 
	 * @param fromEntity
	 *            The entity from which the closest entity is based on
	 * @param type
	 *            The entity type
	 * @param time
	 *            The time slice
	 * @param searchRadius
	 *            The search radius the closest entity locates in
	 * @return The closest entity from the given entity
	 */
	public Entity getClosestEntityByType(Entity fromEntity, String type,
			int time, double searchRadius) {
		// !! to do - determine the closest entity based on the time instant
		List entitiesOfType = (List) entityTypesTable.get(type);
		Entity resultEntity = null;
		double closestDistance = Double.MAX_VALUE;
		if (entitiesOfType != null) // there exist entities of this type
			for (int i = 0; i < entitiesOfType.size(); i++) {
				Entity current = (Entity) entitiesOfType.get(i);

				if ((current == fromEntity) || !current.isActive())
					continue;
				double distance = fromEntity.getPosition().dist(
						getPreviousEntityPosition(current, time));
				if (distance < closestDistance && distance <= searchRadius) {
					closestDistance = distance;
					resultEntity = current;
				}
			}
		return resultEntity;
	}

	/**
	 * Get the entity with the specified id. If no such entity exists, <code>
	 * null</code>
	 * will be returned.
	 * 
	 * @param entityID
	 *            Entity's id
	 * @return The entity with the id.
	 */
	public Entity getEntityById(int entityID) {
		List availableEntities = getAvailableEntities();
		for (int i = 0; i < availableEntities.size(); i++) {
			if (((Entity) availableEntities.get(i)).getMyId() == entityID)
				return (Entity) availableEntities.get(i);
		}
		return null;
	}

	/**
	 * Obtain the closest entity, no matter which type it belongs to
	 * 
	 * @param fromEntity
	 *            The entity from which the closest entity is based on
	 * @param type
	 *            The entity type
	 * @param time
	 *            The time slice
	 * @param searchRadius
	 *            The search radius the closest entity locates in
	 * @return The closest entity from the given entity
	 */
	public Entity getClosestEntity(Entity fromEntity, int time,
			double searchRadius) {
		Iterator types = entityTypesTable.keySet().iterator();// All types
		double closestDistance = Double.MAX_VALUE; // The closest distance
		Entity resultEntity = null; // The closest entity
		while (types.hasNext()) {
			String type = (String) types.next();
			List entitiesOfType = (List) entityTypesTable.get(type);// Entities
			// of that
			// type
			if (entitiesOfType != null) // There exist entities of this type
				for (int i = 0; i < entitiesOfType.size(); i++) {
					Entity current = (Entity) entitiesOfType.get(i);
					if ((current == fromEntity) || !current.isActive())
						continue;
					double distance = fromEntity.getPosition().dist(
							getPreviousEntityPosition(current, time));
					if (distance < closestDistance && distance <= searchRadius) {
						closestDistance = distance;
						resultEntity = current;
					}
				}
		}
		return resultEntity;
	}

	/**
	 * Obtain the list of entities, which are within the given distance range
	 * from the given entity.
	 * 
	 * @param fromEntity
	 *            The entity from which the closest entity is based on
	 * @param type
	 *            The entity type
	 * @param time
	 *            The time slice
	 * @param searchRadius
	 *            The search radius the closest entity locates in
	 * @return The closest entity from the given entity
	 */
	public List getEntitiesWithinDistance(Entity fromEntity, int time,
			double searchRadius) {
		List ret = new ArrayList(); // The result list
		Iterator types = entityTypesTable.keySet().iterator();// All types
		while (types.hasNext()) {
			String type = (String) types.next();
			List entitiesOfType = (List) entityTypesTable.get(type);// Entities
			// of that
			// type
			if (entitiesOfType != null) // There exist entities of this type
				for (int i = 0; i < entitiesOfType.size(); i++) {
					Entity current = (Entity) entitiesOfType.get(i);
					if ((current == fromEntity) || !current.isActive())
						continue;
					double distance = fromEntity.getPosition().dist(
							getPreviousEntityPosition(current, time));
					if (distance <= searchRadius) {
						ret.add(current);
					}
				}
		}
		return ret;
	}

	/**
	 * Obtain the position of the specified entity in the given time slice.
	 * 
	 * @param entity
	 *            The entity whose position is looked into
	 * @param time
	 *            The time slice of the position
	 * @return The position
	 */
	public Point getEntityPosition(Entity entity, int time) {
		EntityRecord record = data.getEntityRecord(entity, time);
		Point position;
		if (record == null)
			position = new Point(entity.getPosition());
		else
			position = record.position;
		return position;
	}

	/**
	 * Obtain the previous position of the specified entity in the given time
	 * slice.
	 * 
	 * @param entity
	 *            The entity whose position is looked into
	 * @param time
	 *            The time slice of the position
	 * @return The previous position
	 */
	public Point getPreviousEntityPosition(Entity entity, int time) {
		EntityRecord record = data.getPreviousEntityRecord(entity, time);
		Point position;
		if (record == null || record.position == null)
			position = new Point(entity.getPosition());
		else
			position = record.position;
		return position;
	}

	/**
	 * Obtain the state of the specified entity and time slice
	 * 
	 * @param entity
	 *            The entity whose state to be retrieved
	 * @param timestep
	 *            The time slice
	 * @return The entity state
	 */
	public int getEntityState(Entity entity, int timeslice) {
		return data.getEntityRecord(entity, timeslice).state;
	}

	/**
	 * Obtain the simulation data
	 * 
	 * @return The simulation data
	 */
	public SimulationData getSimulationData() {
		return this.data;
	}

	/**
	 * Obtain the simulation environment
	 * 
	 * @return The simulation environment
	 */
	public SimulationEnvironment getSimulationEnvironment() {
		return env;
	}

	/**
	 * Remove the specified entity
	 * 
	 * @param entity
	 *            The entity to remove
	 */
	public void removeEntity(Entity entity) {
		entityList.remove(entity);
		removeEntityFromType(entity);
		data.remove(entity);
	}

	/**
	 * Remove the specified entity
	 * 
	 * @param entity
	 *            The entity to remove
	 * @return the entity name
	 */
	public String removeEntity(int entityID) {
		Entity entity = (getEntityById(entityID));
		String entityName = entity.getDisplayName();
		removeEntity(entity);
		return entityName;
	}

	/**
	 * Remove the specified entity from the entity table.
	 * 
	 * @param entity
	 *            The entity to be removed
	 */
	public void removeEntityFromType(Entity entity) {
		List entitiesOfType = (List) entityTypesTable.get(entity
				.getEntityType());
		entitiesOfType.remove(entity);
	}

	/**
	 * Remove all entities from the system
	 * 
	 */
	public void removeAllEntities() {
		int i = 0;
		while (entityList.size() > 0) {
			Entity current = (Entity) entityList.get(i);
			removeEntityFromType(current);
			data.remove(current);
			entityList.remove(i);
		}

	}

	/**
	 * Remove all entities of the specified category from the system.
	 * 
	 * @param categoryName
	 *            Name of the category to remove
	 * @return 
	 * 			  Whether the navigation tree should be rebuilt because of the change of id
	 */
	public boolean removeAllEntities(String categoryName) {
		boolean removed = false;
		int i = 0;
		while ( i < entityList.size() ) {
			Entity current = (Entity) entityList.get(i);
			if (current.getEntityType().equals(categoryName)) {
				removeEntityFromType(current);
				data.remove(current);
				entityList.set(i, null);
				removed = true;
			}
			i++;
		}
		if (removed)
		{
			List newList = new ArrayList();
			for (i = 0; i < entityList.size(); i++)
			{
				Object entity = entityList.get(i);
				if (entity != null) {
					((Entity)entity).setId(i);
					newList.add(entity);
				}
			}
			entityList.clear();
			entityList.addAll(newList);
		}
		return removed;
	}

	/**
	 * Reset the state of the simulation world to the original state
	 */
	public void resetWorld() {
		// env.
	}

	/**
	 * Replace the pre-defined entities because of the change of class
	 * definition.
	 * 
	 * <p>
	 * Iterator the entity list, if an entity is created from the specified
	 * category. Then, the entity object is re-created from the new category
	 * (The category after change). Finally, the re-created entity is populated
	 * with the internal states of the pre-defined entity.
	 * </p>
	 * 
	 * @param oldName
	 *            The name of the category whose entities are to be redefined
	 * @param newName
	 *            The name of the category which is the new class of the
	 *            entities to be redefined.
	 */
	public void updateEntities(String oldName, String newName) throws Exception {
		AppEngine engine = AppEngine.getInstance();
		Category old = null;
		Category newE = null;
		for (int i = 0; i < entityList.size(); i++) {
			if (((Category) entityList.get(i)).getEntityType().equals(oldName)) {
				// Previous version
				old = (Category) entityList.get(i);
				// New version
				newE = (Category) engine.appManager.currentApp.dm
						.getCategory(newName);
				// Initialize it
				int id = old.getMyId();
				String newDisplayName = newE.getEntityType()
						+ old.getDisplayName().substring(oldName.length());
				String newIconPath = newE.getImagePath();
				Display display = old.getDisplay();
				IMechanism m = null;
				if (old instanceof BNCategory) {
					m = ((BNCategory) old).getActionSelectionMechanism();
				}
				// Copy general states
				old.copyState(newE);
				// Re-initialize with new special states
				newE.init((JComponent) engine.getCategoryUpdateListener(),
						newName, newIconPath);
				newE.setDisplayName(newDisplayName);
				newE.setId(id);
				newE.setDisplay(display);
				if (newE instanceof BNCategory) {
					((BNCategory) newE).setActionSelectionMechanism(m);
				}
				// Probe methods if necessary
				// MethodUtils.probeMethodsList(newE);
				// If successfully, replace the old one with the new one
				entityList.set(i, newE);
				// Initialize simulation data
				data.put(newE, data.getEntityRecord(old));
				// Handle type of entities
				if (entityTypesTable.containsKey(oldName)) {
					List entitiesOfType = (List) entityTypesTable.get(oldName);
					for (int j = 0; j < entitiesOfType.size(); j++) {
						if (((Entity) entitiesOfType.get(j)).getMyId()
								==(old.getMyId())) {
							entitiesOfType.set(j, newE);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Update the specified entity of the given category because of the change
	 * of class definition.
	 * 
	 * <p>
	 * Iterator the entity list, if the entity is created from the specified
	 * category. Then, the entity object is re-created from the new category
	 * (The category after change). Finally, the re-created entity is populated
	 * with the internal states of the pre-defined entity.
	 * </p>
	 * 
	 * <p>
	 * NOTE THAT, The updated entity adopts the action selection mechanism of
	 * the specified entity to be updated, not that of the entity created
	 * through system editor.
	 * </p>
	 * 
	 * @param categoryName
	 *            The name of the category from which the entity is created.
	 * @param newName
	 *            The name of the category which is the new class of the
	 *            entities to be redefined.
	 */
	public Category updateEntity(String categoryName, Category toUpdate)
			throws Exception {
		AppEngine engine = AppEngine.getInstance();
		Category old = null;
		Category newE = null;
		for (int i = 0; i < entityList.size(); i++) {
			old = (Category) entityList.get(i);
			if (old.getMyId() == toUpdate.getMyId()
					&& old.getEntityType().equals(categoryName)) {
				// New version
				newE = (Category) engine.appManager.currentApp.dm
						.getCategory(categoryName);
				// sim.util.MethodUtils.probeMethodsList(newE);
				// Initialize it
				int id = old.getMyId();
				String newDisplayName = /* categoryName + */
				old.getDisplayName()/* .substring(categoryName.length()) */;
				String newIconPath = newE.getImagePath();
				Display display = old.getDisplay();
				IMechanism m = null;
				BehaviorNetwork net = null;
				if (newE instanceof BNCategory) {
					m = ((BNCategory) toUpdate)
							.getOriginalActionSelectionMechanism();
					net = ((BNCategory) toUpdate).getBehaviorNetwork();
				}
				// Copy general states
				old.copyState(newE);
				// Re-initialize with new special states
				newE.init((JComponent) engine.getCategoryUpdateListener(),
						categoryName, newIconPath);
				newE.setDisplayName(newDisplayName);
				newE.setId(id);
				newE.setDisplay(display);
				if (newE instanceof BNCategory) {
					((BNCategory) newE).setActionSelectionMechanism(m);
					((BNCategory) newE).setBehaviorNetwork(net);
				}
				// Probe methods if necessary
				// sim.util.MethodUtils.probeMethodsList(newE);
				// If successfully, replace the old one with the new one
				entityList.set(i, newE);
				// Initialize simulation data
				data.put(newE, data.getEntityRecord(old));
				// Handle type of entities
				if (entityTypesTable.containsKey(categoryName)) {
					List entitiesOfType = (List) entityTypesTable
							.get(categoryName);
					for (int j = 0; j < entitiesOfType.size(); j++) {
						if (((Entity) entitiesOfType.get(j)).getMyId()
								== (old.getMyId())) {
							entitiesOfType.set(j, newE);
							break;
						}
					}
				}
			}
		}
		return newE;
	}

	/**
	 * Obtain the total time slices from the system configuration parameter
	 * object
	 * 
	 * @return The total time slices
	 */
	public int getTotalTimeticks() {
		return systemParameters.getTotalTimeticks();
	}

	/**
	 * Add an entity to the entity table
	 * 
	 * @param entity
	 *            The entity to add
	 */
	private void _addEntityToType(Entity entity) {
		List entitiesOfType;
		if (entityTypesTable.containsKey(entity.getEntityType())) {
			entitiesOfType = (List) entityTypesTable
					.get(entity.getEntityType());
			entitiesOfType.add(entity);
		} else {
			entitiesOfType = new ArrayList();
			entitiesOfType.add(entity);
			entityTypesTable.put(entity.getEntityType(), entitiesOfType);
		}
	}
	
	/**
	 * Save the initial parameters for the given entity.
	 * 
	 * @param e
	 *            The entity whose initial parameters are saved
	 * @param initialPos
	 * 			  Initial position to set
	 */
	public void changeEntityInitialParameters(Entity e, Map initialPos) {

		EntityRecord record = data.getEntityRecord2(e, 0);
		
		if (initialPos != null) {
			Point pos = (Point)initialPos.get(new Integer(e.getMyId()));
			if (pos != null) {		
				e.getOriginalPosition().x = pos.x;
				e.getOriginalPosition().y = pos.y;
			}
		}

		record.position = e.getPosition();		
		record.direction = systemParameters.getInitialMotionDirection();
		record.state = e.getState();
		record.display = e.getDisplay();
		data.store(e, record, 0);

	}

	/**
	 * Save the initial parameters for the given entity.
	 * 
	 * @param e
	 *            The entity whose initial parameters are saved
	 */
	public void changeEntityInitialParameters(Entity e) {

		changeEntityInitialParameters(e, null);

	}

	// ------------------------------------------------------
	// Move and turn the specified entity
	// ------------------------------------------------------
	/**
	 * Turn with a given angle.
	 * 
	 * @param angularSpeed
	 *            The turning speed
	 * @param type
	 *            The type of the turn (CLOCKWISE or COUNTERCLOCKWISE)
	 */
	public void turnWithAngularSpeed(double angularSpeed, int type) {
		Entity e = AppEngine.getInstance().appManager.currentApp.currentEntity;
		if (type == SystemFunction.ROTATECOUNTERCLOCKWISE)
			e.setDirection(e.getDirection() + angularSpeed);
		else if (type == SystemFunction.ROTATECLOCKWISE)
			e.setDirection(e.getDirection() - angularSpeed);
		// MessageUtils.debugNormal(angularSpeed +","+e.getDirection());
	}

	/**
	 * Return a speed vector which reflects the moving speed and angle. It is
	 * mainly used in the behavior-network based simulation.
	 * 
	 * @param speed
	 *            Moving speed
	 * @param angle
	 *            Moving direction
	 * @param type
	 *            The type of move (<code>MOVEFORWARD, MOVEBACKWARD</code>)
	 * @return speed vector
	 */
	public String getSpeedVector(double speed, double angle, int type) {
		return speed + "," + angle + "," + type;
	}

	/**
	 * Move at the specified speed and angle(direction). It is mainly used in
	 * the general simulation, since the move (position change) will be
	 * fulfilled in specific action selection mechanisms in the case of behavior
	 * network based simulation.
	 * 
	 * @param entity
	 *            The entity to move
	 * @param speed
	 *            The moving speed of the entity
	 * @param angle
	 *            The moving direction of the entity
	 * @param type
	 *            The type of moving, <code>MOVEFORWARD</code> or
	 *            <code>MOVEBACKWARD</code>
	 */
	public void move(Entity entity, double speed, double angle, int type) {
		if (entity == null)
			return;
		if (type == SystemFunction.MOVEFORWARD)
			entity.setPosition(
					entity.getPosition().x + Math.cos(angle) * speed, entity
							.getPosition().y
							+ Math.sin(angle) * speed);
		else if (type == SystemFunction.MOVEBACKWARD)
			entity.setPosition(
					entity.getPosition().x - Math.cos(angle) * speed, entity
							.getPosition().y
							- Math.sin(angle) * speed);
		entity.setDirection(angle);
		// if (MessageUtils.isDebug())
		// MessageUtils.debugNormal(entity.getDisplayName() + " " +
		// entity.getPosition().x + " " +
		// entity.getPosition().y + "," +
		// angle);
		env.handleEnvironmentBounds(entity);
	}

	/**
	 * Move forward at the specified increment on X and Y coordinations
	 * 
	 * @param entity
	 *            The entity to move
	 * @param deltaX
	 *            The movement on x coordination
	 * @param deltaY
	 *            The movement on y coordination
	 */
	public void move(Entity entity, double deltaX, double deltaY) {
		if (entity == null)
			return;
		entity.setPosition(entity.getPosition().x + deltaX, entity
				.getPosition().y
				+ deltaY);
		entity.setDirection(new Vect(deltaX, deltaY).unit().angle());
		// if (MessageUtils.isDebug())
		// MessageUtils.debugNormal(entity.getDisplayName() + " " +
		// entity.getPosition().x + " " +
		// entity.getPosition().y);
		env.handleEnvironmentBounds(entity);
	}

	// ------------------------------------------------------
	// IR distances
	// ------------------------------------------------------
	/**
	 * Get the closest IR direction from the given source to all other entities,
	 * including the simulation environment, see
	 * <code>SimulationEnvironment</code>. It delegates the call to the
	 * <code>AppSystem</code>.
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
	public double getIRDistance(int entityID, double direction, double posX,
			double posY) {
		// parameters
		double Sx = posX, Sy = posY, Sa = direction;

		// distance
		double Fdistance = Double.POSITIVE_INFINITY, temp;

		// check the distances to the surrounding walls
		temp = _getIRDistanceToEnvironment(Sa, Sx, Sy);
		if (temp < Fdistance)
			Fdistance = temp;

		// check the distances to other entities
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = (Entity) entityList.get(i);
			if (e.getMyId() != entityID && e.isActive()) {
				temp = _getIRDistanceToEntity(e, Sa, Sx, Sy);
				if (temp < Fdistance)
					Fdistance = temp;
			}
		}
		return Fdistance;
	}

	/**
	 * Get the closest IR direction from the specified entity to all other
	 * entities belonging to the same category, see
	 * <code>SimulationEnvironment</code>. It delegates the call to the
	 * <code>AppSystem</code>.
	 * 
	 * @param entityID
	 *            The id of the source entity which will not be included in the
	 *            final result.
	 * @param direction
	 *            The direction of the source entity
	 * @param posX
	 *            The position x of the source entity
	 * @param posY
	 *            The position y of the source entity
	 * @param catName
	 *            The category name of destination entities
	 * @return The closest IR distance to other entities of the same category
	 */
	public double getIRDistanceToCategory(int entityID, double direction,
			double positionX, double positionY, String catName) {
		double Fdistance = Double.MAX_VALUE;
		// check the distances to other entities
		for (int i = 0; i < entityList.size(); i++) {
			Category e = (Category) entityList.get(i);
			if (e.getEntityType().equals(catName) && e.getMyId() != entityID
					&& e.isActive()) {
				double temp = _getIRDistanceToEntity(e, direction, positionX,
						positionY);
				if (temp < Fdistance)
					Fdistance = temp;
			}
		}
		return Fdistance;
	}

	/**
	 * Return the IR distance from the source to the simulation environment.
	 * 
	 * @param direction
	 *            The source direction
	 * @param pX
	 *            The source coordination x
	 * @param pY
	 *            The source coordination y
	 * @return IR distance
	 */
	private double _getIRDistanceToEnvironment(double direction, double pX,
			double pY) {
		return _getIRDistanceOfRectangleShape(direction, pX, pY, env
				.getHeight() / 2, env.getWidth() / 2, 0.0D,
				env.getHeight() / 2, env.getWidth() / 2);
	}

	/**
	 * Return the IR distance from the source to the given entity.
	 * 
	 * @param entity
	 *            The destination (entity)
	 * @param direction
	 *            The source direction
	 * @param pX
	 *            The source coordination x
	 * @param pY
	 *            The source coordination y
	 * @return IR distance
	 */
	private double _getIRDistanceToEntity(Entity entity, double direction,
			double pX, double pY) {
		return _getIRDistanceOfRectangleShape(direction, pX, pY, entity
				.getHeight() / 2, entity.getWidth() / 2, entity.getDirection(),
				entity.getPosition().x, entity.getPosition().y);
	}

	/**
	 * Get the distance from (Fx, Fy, Fa) to the boundary of the given object
	 * (entity or simulation environment). It is similar to the IR distance used
	 * by robots to detect obstacles.
	 * 
	 * In the case of the shape of the object is rectangle.
	 * 
	 * @param pX
	 *            The coordination Fx of the source
	 * @param pY
	 *            The coordination Fy of the source
	 * @param direction
	 *            The angle of source
	 * @param LhalfSize
	 *            Half of the destination object height
	 * @param WhalfSize
	 *            Half of the destination object width
	 * @param Angle
	 *            Moving direction of the dest. object
	 * @param posX
	 *            The coordination x of the dest.
	 * @param posY
	 *            The coordination y of the dest.
	 * 
	 * @return the distance
	 */
	private double _getIRDistanceOfRectangleShape(double direction, double pX,
			double pY, double LhalfSize, double WhalfSize, double Angle,
			double posX, double posY) {
		double Fx = pX, Fy = pY, Fa = direction;
		double Fdistance = Double.POSITIVE_INFINITY, temp;
		double a1, b1, c1, a2, b2, c2; // each line is described by a*x+b*y+c=0
		double XBs, XBl, YBs, YBl; // boundary of a segment of a line

		a1 = Math.sin(Fa);
		b1 = -1 * Math.cos(Fa);
		c1 = Fy * Math.cos(Fa) - Fx * Math.sin(Fa);

		// check the distance to this worldEntity's front edge
		a2 = Math.cos(Angle);
		b2 = Math.sin(Angle);
		c2 = -1 * LhalfSize - posX * Math.cos(Angle) - posY * Math.sin(Angle);
		XBs = posX + LhalfSize * Math.cos(Angle) - WhalfSize
				* Math.abs(Math.sin(Angle));
		XBl = posX + LhalfSize * Math.cos(Angle) + WhalfSize
				* Math.abs(Math.sin(Angle));
		YBs = posY + LhalfSize * Math.sin(Angle) - WhalfSize
				* Math.abs(Math.cos(Angle));
		YBl = posY + LhalfSize * Math.sin(Angle) + WhalfSize
				* Math.abs(Math.cos(Angle));
		temp = _calcuDist(a1, b1, c1, a2, b2, c2, XBs, XBl, YBs, YBl, Fx, Fy,
				Fa);
		if (temp < Fdistance)
			Fdistance = temp;
		// check the distance to this worldEntity's back edge
		a2 = Math.cos(Angle);
		b2 = Math.sin(Angle);
		c2 = LhalfSize - posX * Math.cos(Angle) - posY * Math.sin(Angle);
		XBs = posX - LhalfSize * Math.cos(Angle) - WhalfSize
				* Math.abs(Math.sin(Angle));
		XBl = posX - LhalfSize * Math.cos(Angle) + WhalfSize
				* Math.abs(Math.sin(Angle));
		YBs = posY - LhalfSize * Math.sin(Angle) - WhalfSize
				* Math.abs(Math.cos(Angle));
		YBl = posY - LhalfSize * Math.sin(Angle) + WhalfSize
				* Math.abs(Math.cos(Angle));
		temp = _calcuDist(a1, b1, c1, a2, b2, c2, XBs, XBl, YBs, YBl, Fx, Fy,
				Fa);
		if (temp < Fdistance)
			Fdistance = temp;
		// check the distance to this worldEntity's right edge
		a2 = Math.sin(Angle);
		b2 = -1 * Math.cos(Angle);
		c2 = -1 * WhalfSize - posX * Math.sin(Angle) + posY * Math.cos(Angle);
		XBs = posX + WhalfSize * Math.sin(Angle) - LhalfSize
				* Math.abs(Math.cos(Angle));
		XBl = posX + WhalfSize * Math.sin(Angle) + LhalfSize
				* Math.abs(Math.cos(Angle));
		YBs = posY - WhalfSize * Math.cos(Angle) - LhalfSize
				* Math.abs(Math.sin(Angle));
		YBl = posY - WhalfSize * Math.cos(Angle) + LhalfSize
				* Math.abs(Math.sin(Angle));
		temp = _calcuDist(a1, b1, c1, a2, b2, c2, XBs, XBl, YBs, YBl, Fx, Fy,
				Fa);
		if (temp < Fdistance)
			Fdistance = temp;
		// check the distance to this worldEntity's left edge
		a2 = Math.sin(Angle);
		b2 = -1 * Math.cos(Angle);
		c2 = WhalfSize - posX * Math.sin(Angle) + posY * Math.cos(Angle);
		XBs = posX - WhalfSize * Math.sin(Angle) - LhalfSize
				* Math.abs(Math.cos(Angle));
		XBl = posX - WhalfSize * Math.sin(Angle) + LhalfSize
				* Math.abs(Math.cos(Angle));
		YBs = posY + WhalfSize * Math.cos(Angle) - LhalfSize
				* Math.abs(Math.sin(Angle));
		YBl = posY + WhalfSize * Math.cos(Angle) + LhalfSize
				* Math.abs(Math.sin(Angle));
		temp = _calcuDist(a1, b1, c1, a2, b2, c2, XBs, XBl, YBs, YBl, Fx, Fy,
				Fa);
		if (temp < Fdistance)
			Fdistance = temp;

		return Fdistance;
	}

	private double _calcuDist(double a1, double b1, double c1, double a2,
			double b2, double c2, double XBs, double XBl, double YBs,
			double YBl, double Sx, double Sy, double Sa) {
		double intectX, intectY, D;
		if ((a1 * b2 - a2 * b1) == 0)
			return Double.POSITIVE_INFINITY; // two lines are parallel
		else {
			intectX = (b1 * c2 - c1 * b2) / (a1 * b2 - a2 * b1);
			intectY = (c1 * a2 - a1 * c2) / (a1 * b2 - a2 * b1);
			D = Math.sqrt((intectX - Sx) * (intectX - Sx) + (intectY - Sy)
					* (intectY - Sy));
			// check the intersection point is at "front" or "back"
			if ((intectX - Sx) * Math.cos(Sa) < 0
					|| (intectY - Sy) * Math.sin(Sa) < 0)
				return Double.POSITIVE_INFINITY;
			else { // check if the intersection point is inside the boundary
				if ((int) (intectX) < (int) XBs || (int) (intectX) > (int) XBl
						|| (int) (intectY) < (int) YBs
						|| (int) (intectY) > (int) YBl)
					return Double.POSITIVE_INFINITY;
				else
					return D;
			}
		}
	}

	// ------------------------------------------------------
	// Entity drawing routines
	// ------------------------------------------------------
	
	/**
	 * Store the position of all agents at the given time slice.
	 * 
	 * @param positions The updated position of all agents
	 * @param timeslice The updated time step
	 */
	public void populatePositions(Map positions, int timeslice) {
		for (int m = 0; m < entityList.size(); m++) {
			Entity entity = (Entity) entityList.get(m);
			EntityRecord record = data.getEntityRecord(entity, timeslice);
			if (record == null)
				return;
			positions.put(new Integer(entity.getMyId()), record.position);
		}
	}
	
	/**
	 * Draw all available entities on the given graphics and time slice
	 * 
	 * @param g
	 *            The graphics the entities drawn to
	 * @param timeslice
	 *            The time slice of the entity record
	 */
	public void drawAvailableEntities(Graphics g, int timeslice) {
		Graphics2D g2 = (Graphics2D) g;
		for (int m = 0; m < entityList.size(); m++) {
			Entity entity = (Entity) entityList.get(m);
			EntityRecord record = data.getEntityRecord(entity, timeslice);
			if (record == null)
				return;
			Point currentPosition = record.position;
			double direction = record.direction;
			Display display = record.display;
			if (currentPosition == null || display == null)
				return;
			int state = this.getEntityState(entity, timeslice);
			if (entity.isVisible()) {
				if (timeslice == 0)
					direction = display.getDirection();
				_drawEntity(display, g2, entity.getMyId(), direction,
						currentPosition, state);
			}
		}

	}
	
	/**
	 * Update position of all available entities on the given display time slice
	 * 
	 * @param timeslice
	 *            The time slice of the entity record
	 */
	public void updatePositionOfEntities( int timeslice ) {
		
		for (int m = 0; m < entityList.size(); m++) {
			Entity entity = (Entity) entityList.get(m);
			EntityRecord record = data.getEntityRecord(entity, timeslice);
			if (record == null)
				continue;
			entity.setDirection(record.direction);
			entity.setPosition(record.position);
			entity.setDisplay(record.display);
			entity.setState(record.state);
		}

	}

	/**
	 * Draw the entity. Used in the simulation view
	 * 
	 * @param display
	 *            The display component of the entity to draw
	 * @param g2
	 *            The graphics on which the entity is drawn on
	 * @param id
	 *            The id of the entity
	 * @param direction
	 *            The entity direction
	 * @param position
	 *            The entity position
	 * @param state
	 *            The entity state
	 */
	private void _drawEntity(Display display, Graphics2D g2, int id,
			double direction, Point position, int state) {
		// In order to be drawn each entity should have an image, width and
		// height
		if ((state == Entity.ACTIVE) && (display.getImage() != null)) {
			// set transform, first toCenter then rotate
			AffineTransform at = new AffineTransform();
			at.rotate(direction);
			AffineTransform toCenterAt = new AffineTransform();
			toCenterAt.translate(position.x, position.y); // assume the the
			// (0,0) is at left
			// bottom corner
			toCenterAt.concatenate(at);
			// save old transform
			AffineTransform saveXform = g2.getTransform();
			// transform
			g2.transform(toCenterAt);
			// draw
			g2.drawImage(display.getImage(),
					(int) (-1 * display.getWidth() / 2), (int) (-1
							* display.getHeight() / 2), (int) display
							.getWidth(), (int) display.getHeight(), null);
			// g2.drawString(String.valueOf(id), -2, -5);
			// restore old transform
			g2.setTransform(saveXform);
			// g2.drawOval((int)position.x, (int)position.y, 4, 4);
			// g2.drawOval((int)position.x, (int)position.y -
			// display.getHeight()/2, 4, 4);
			// g2.drawOval((int)position.x, (int)position.y +
			// display.getHeight()/2, 4, 4);
		}
	}

	/**
	 * Draw the given entity. Used only in the system editor
	 * 
	 * @param entity
	 *            The entity to display
	 * @param g
	 *            The graphics on which the entity is drawn on
	 * @param direction
	 *            The entity direction
	 * @param position
	 *            The entity position
	 * @param state
	 *            The entity state
	 */
	public void drawEntity(Entity entity, Graphics g, double direction,
			Point position, int state) {
		// In order to be drawn each entity should have an image, width and
		// height
		if (entity.isVisible() && (state == Entity.ACTIVE)
				&& (entity.getImage() != null)) {
			int width = entity.getImage().getWidth(null);
			int height = entity.getImage().getHeight(null);
			if (width > height) {
				height = (int) (1.0 * entity.getHeight() * height / width);
				width = entity.getWidth();
			} else {
				width = (int) (1.0 * entity.getWidth() * width / height);
				height = entity.getHeight();
			}
			g.drawImage(entity.getImage(), (int) (position.x - width / 2),
					(int) (position.y - height / 2), width, height, null);
		}
	}

	/**
	 * Draw the given entity. Used only in the system editor
	 * 
	 * @param entity
	 *            The entity to display
	 * @param g
	 *            The graphics on which the entity is drawn on
	 * @param direction
	 *            The entity direction
	 * @param position
	 *            The entity position
	 * @param state
	 *            The entity state
	 */
	public void drawEntity2(Entity entity, Graphics g, double direction,
			Point position, int state) {
		// In order to be drawn each entity should have an image, width and
		// height
		if (entity.isVisible() && (state == Entity.ACTIVE)
				&& (entity.getImage() != null)) {
			g.drawImage(entity.getImage(), (int) (position.x - entity
					.getWidth() / 2),
					(int) (position.y - entity.getHeight() / 2), entity
							.getWidth(), entity.getHeight(), null);
		}
	}

}
