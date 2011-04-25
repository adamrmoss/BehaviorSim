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

package sim.ui.tree;

import java.util.Map;

/**
 * The metadata stored in the tree node. Some typical meta data would be the
 * node name and node type (for example, root node, category node, method node
 * ,), which is interpreted by the client which uses this interface.
 * 
 * A typical implementation of this interface is that, all metadatas are stored
 * in a hash map which can be used in the client.
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 11:04AM
 */
public interface MetaData {
	/**
	 * Setup a meta data for the tree node. The meta data is specified with a
	 * name and related value.
	 * 
	 * @param name
	 *            The name of the meta data
	 * @param value
	 *            The value of the meta data
	 */
	void addMetaData(String name, Object value);

	/**
	 * Return the value related with the named meta data
	 * 
	 * @param name
	 *            The name of the meta data to be returned
	 * @return The value of the meta data
	 */
	Object getMetaData(String name);

	/**
	 * Return all meta datas of this tree node. The typical implementation would
	 * be returning a copy of all meta datas
	 * 
	 * @return All meta datas of key-value pairs (name with value)
	 */
	Map getAllMetaData();

	/**
	 * Whether there are meta datas of the related object, such as tree node
	 * 
	 * @return Whether there are some meta data existing
	 */
	boolean hasMetaData();
}
