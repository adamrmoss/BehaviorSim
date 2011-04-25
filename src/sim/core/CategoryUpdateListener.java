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

/**
 * <p>Title: The Category and Entity Update Listener</p>
 * <p>Description:
 * It is used when the category is changed (added or edited or deleted).
 * It is used when a new entity is added into the system.
 * It is used in the category display interface (SystemEditorPanel).
 * </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Fasheng Qiu
 * @version 1.0
 */
import sim.model.entity.Category;
import sim.model.entity.Entity;

public interface CategoryUpdateListener {
	// newCategory : The category to be added
	/**
	 * A new category is added.
	 * 
	 * @param newCategory
	 *            The newly added category
	 */
	public void categoryAdded(Category newCategory);

	// categoryName: The category to be deleted
	/**
	 * A category is deleted
	 * 
	 * @param categoryName
	 *            The name of the category to be deleted
	 */
	public void categoryDeleted(String categoryName);

	// newCategory: The category after updated
	/**
	 * Update the definition of an existing category
	 * 
	 * @param oldCategoryName
	 *            The name of the existing category
	 * @param newCategory
	 *            The name of the new category
	 */
	public void categoryUpdated(String oldCategoryName, Category newCategory);

	// newEntity: The entity to be added
	/**
	 * An new entity is added
	 * 
	 * @param newEntity
	 *            The instance of that entity
	 */
	public void entityAdded(Entity newEntity);

	/**
	 * Update the simulation world
	 * 
	 */
	public void entityUpdated();

}