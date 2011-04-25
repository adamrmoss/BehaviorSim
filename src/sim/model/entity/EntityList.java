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

/**
 * <p>
 * Entity list. This class serves as a container of a list of entities. Each
 * entity is an instance of <code>Entity</code>, which can be referred in the
 * code directly.
 * 
 * One typical usage of this class would be as follows.
 * </p>
 * 
 * <pre>
 * EntityList entityList = getListOfCategoryEntitiesWithinDistance(&quot;categoryName&quot;,
 * 		150.0);
 * int numberOfEntities = entityList.size();
 * for (int i = 0; i &lt; numberOfEntities; i++) {
 * 	Entity entity = entityList.getEntity(i);
 * 	// Processing the entity ...
 * 	// All listed system functions are available for each entity.
 * }
 * </pre>
 * 
 * @author Fasheng Qiu
 * @since 04/10/2009
 * @version 1.0
 */
public class EntityList {

	/** List of entities */
	private List entities = null;

	/**
	 * Constructor.
	 * 
	 * @param entities
	 *            List of contained entities
	 */
	EntityList(List entities) {
		this.entities = new ArrayList();
		if (entities != null)
			this.entities.addAll(entities);
	}

	/**
	 * Return the number of entities
	 * 
	 * @return The number of entities
	 */
	public int size() {
		return entities.size();
	}

	/**
	 * Get the specified entity at the specified location.
	 * 
	 * @param index
	 *            Entity index
	 * @return The entity at the specified location.
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index &lt; 0 || index &gt;=
	 *             size()).
	 */
	public Entity getEntity(int index) {
		return (Entity) entities.get(index);
	}

}
