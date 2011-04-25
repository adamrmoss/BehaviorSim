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

import java.util.HashMap;
import java.util.Map;

import sim.model.entity.Entity;
import sim.model.entity.EntityRecord;

/**
 * Repository of simulation data. To make the simulation faster, two arrays are
 * used alternatively and wrap-aroundly to store the simulation data (main array
 * is used first and then the backup array). The computation and display of
 * simulation results follow the same order for
 * <code>SimulationComputeThread</code> and <code>AnimateThread</code>.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class SimulationData {

	/**
	 * Simulation data for each entity
	 */
	private Map data = new HashMap(10);

	/**
	 * Backup simulation data for each entity
	 */
	private Map backup = new HashMap(10);

	/**
	 * Whether the "display" data source is switched. If false, main data will
	 * be used. Otherwise, backup data will be used.
	 */
	private boolean displaySwitched = false;

	/**
	 * Whether the "compute" data source is switched. If false, main data will
	 * be used. Otherwise, backup data will be used.
	 */
	private boolean computeSwitched = false;

	/**
	 * Whether the computation of the main data is completed.
	 */
	private boolean mainCompleted = false;

	/**
	 * Whether the computation of the backup data is completed.
	 */
	private boolean backupCompleted = false;

	/**
	 * Initial size of entity simulation data
	 */
	private int entityTotalRecords = 0;

	/**
	 * Constructor
	 * 
	 * @param totaltimeticks
	 */
	public SimulationData(int totaltimeticks) {
		entityTotalRecords = totaltimeticks;
	}

	/**
	 * Reset this SimulationData. When a new simulation cycle is instantiated,
	 * the SimulationData should be reset to the initial state when entities are
	 * added to the system.
	 * 
	 */
	public void reset() {

		// Reset the flags
		this.setBackupCompleted(false);
		this.setComputeSwitched(false);
		this.setDisplaySwitched(false);
		this.setMainCompleted(false);

	}

	/**
	 * Add a new record for the target entity
	 * 
	 * @param entity
	 *            The entity to setup simulation data
	 */
	public void add(Entity entity) {

		EntityRecord[] recordArray = new EntityRecord[entityTotalRecords];
		for (int i = 0; i < recordArray.length; i++)
			recordArray[i] = new EntityRecord();
		data.put(entity, recordArray);

		recordArray = new EntityRecord[entityTotalRecords];
		for (int i = 0; i < recordArray.length; i++)
			recordArray[i] = new EntityRecord();
		backup.put(entity, recordArray);

	}

	/**
	 * Remove the simulation data for the target entity
	 * 
	 * @param entity
	 *            The target entity
	 */
	public void remove(Entity entity) {
		data.remove(entity);
		backup.remove(entity);
	}

	/**
	 * Store the data of a specified time step of the target entity to the
	 * repository
	 * 
	 * @param entity
	 *            The target entity
	 * @param record
	 *            The simulation data
	 * @param time
	 *            The time to set
	 */
	public void store(Entity entity, EntityRecord record, int time) {
		EntityRecord[] records = null;
		if (!computeSwitched)
			records = (EntityRecord[]) data.get(entity);
		else
			records = (EntityRecord[]) backup.get(entity);
		if (records != null)
			records[time % entityTotalRecords] = record;
	}

	/**
	 * Save the simulation data of a target entity
	 * 
	 * @param entity
	 *            The target entity
	 * @param record
	 *            The simulation data
	 */
	public void put(Entity entity, EntityRecord[] record) {
		if (!computeSwitched)
			data.put(entity, record);
		else
			backup.put(entity, record);
	}

	/**
	 * Return the simulation data of an entity
	 * 
	 * @param entity
	 *            The target entity
	 * @return The simulation data
	 */
	public EntityRecord[] getEntityRecord(Entity entity) {
		if (!displaySwitched)
			return (EntityRecord[]) data.get(entity);
		else
			return (EntityRecord[]) backup.get(entity);
	}

	/**
	 * Return the previous simulation record of a specified time of an entity.
	 * Wrap around is used to retrieve the record.
	 * 
	 * Since two arrays are used alternatively and wrap-aroundly to store the
	 * simulation data (main array is used first and then the backup array). For
	 * the main array, if the previous record of the time step 0 (time %
	 * TOTALNUMBEROFTICKS == 0) is retrieved, the last record of backup array
	 * should be returned.
	 * 
	 * For backup array, if the previous record of the time step 0 (time %
	 * TOTALNUMBEROF TICKS == 0) is retrieved, the last record of the main array
	 * should be returned.
	 * 
	 * In other cases, the record of (time-1) % TOTALNUMBEROFTICKS should be
	 * returned.
	 * 
	 * This method is mainly used in the "computation" stage to get the previous
	 * entity position. See {@link #getClosestEntity(int entityid)} of
	 * <code>SystemFunction</code>.
	 * 
	 * @param entity
	 *            The target entity
	 * @param time
	 *            The current simulation time
	 * @return The simulation data
	 */
	public EntityRecord getPreviousEntityRecord(Entity entity, int time) {

		// Wrap around first
		int currentTime = time % entityTotalRecords;

		// Get the previous record of the special cases
		EntityRecord[] records = null;
		if (!computeSwitched) {
			// The previous record of the first record of main array should be
			// returned
			if (currentTime == 0) {
				return ((EntityRecord[]) backup.get(entity))[entityTotalRecords - 1];
			}
			records = (EntityRecord[]) data.get(entity);
		} else {
			// The previous record of the first record of backup array should be
			// returned
			if (currentTime == 0) {
				return ((EntityRecord[]) data.get(entity))[entityTotalRecords - 1];
			}
			records = (EntityRecord[]) backup.get(entity);
		}

		// Wrap around implementation. Project the index into the approximate
		// position
		return records[currentTime - 1];
	}

	/**
	 * Return the simulation data of a specified time of an entity. Wrap around
	 * is used to retrieve the record.
	 * 
	 * 
	 * @param entity
	 *            The target entity
	 * @param time
	 *            The simulation time
	 * @return The simulation data
	 */
	public EntityRecord getEntityRecord(Entity entity, int time) {
		EntityRecord[] records = null;
		if (!displaySwitched)
			records = (EntityRecord[]) data.get(entity);
		else
			records = (EntityRecord[]) backup.get(entity);

		// Wrap around implementation. Project the index into the approximate
		// position
		return records[time % entityTotalRecords];
	}

	/**
	 * Return the simulation data of a specified time of an entity. Wrap around
	 * is used to retrieve the record. This routine is ONLY used in
	 * <code>SimulationComputeThread</code> and
	 * <code>AppSystem.changeEntityInitialParameters</code>, where the wrap
	 * around is based on the computation status. If the computation is done for
	 * both arrays, then <code>null</code> will be returned. Otherwise, If the
	 * computation is done for main array, the backup array will be retrieved.
	 * Otherwise, the main array will be returned.
	 * 
	 * 
	 * @param entity
	 *            The target entity
	 * @param time
	 *            The simulation time
	 * @return The simulation data
	 */
	public EntityRecord getEntityRecord2(Entity entity, int time) {
		EntityRecord[] records = null;
		if (!computeSwitched) {
			records = (EntityRecord[]) data.get(entity);
		} else {
			records = (EntityRecord[]) backup.get(entity);
		}
		if (records == null) {
			return null;
		}
		return records[time % entityTotalRecords];
	}

	/**
	 * @return the displaySwitched
	 */
	public boolean isDisplaySwitched() {
		return displaySwitched;
	}

	/**
	 * @param displaySwitched
	 *            the displaySwitched to set
	 */
	public void setDisplaySwitched(boolean displaySwitched) {
		this.displaySwitched = displaySwitched;
	}

	/**
	 * @return the computeSwitched
	 */
	public boolean isComputeSwitched() {
		return computeSwitched;
	}

	/**
	 * @param computeSwitched
	 *            the computeSwitched to set
	 */
	public void setComputeSwitched(boolean computeSwitched) {
		this.computeSwitched = computeSwitched;
	}

	/**
	 * @return the mainCompleted
	 */
	public boolean isMainCompleted() {
		return mainCompleted;
	}

	/**
	 * @param mainCompleted
	 *            the mainCompleted to set
	 */
	public void setMainCompleted(boolean mainCompleted) {
		this.mainCompleted = mainCompleted;
	}

	/**
	 * @return the backupCompleted
	 */
	public boolean isBackupCompleted() {
		return backupCompleted;
	}

	/**
	 * @param backupCompleted
	 *            the backupCompleted to set
	 */
	public void setBackupCompleted(boolean backupCompleted) {
		this.backupCompleted = backupCompleted;
	}

}
