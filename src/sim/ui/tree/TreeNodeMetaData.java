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

import java.util.HashMap;
import java.util.Map;

/**
 * The meta data wrapper of the tree node.
 * 
 * It is used to wrap all meta data information of the related tree node. Some
 * typical meta data would be node type, the actual object of the tree node.
 * 
 * It is the default implementation of the interface, <code>MetaData</code>. A
 * hash map is used to store all meta data.
 * 
 * The default implementation also supports the copy and paste functions.
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 4:40PM
 * @see sim.ui.tree.MetaData
 */
public class TreeNodeMetaData implements MetaData, ICopier {

	/* Meta data hash map */
	private Map metaDatas = null;

	/* Constructor */
	public TreeNodeMetaData() {
		metaDatas = new HashMap();
	}

	/**
	 * Constructor
	 * 
	 * @param tnmd
	 *            The existing TreeNodeMetaData
	 */
	public TreeNodeMetaData(MetaData tnmd) {
		if (tnmd != null)
			metaDatas = tnmd.getAllMetaData();
		if (metaDatas == null)
			metaDatas = new HashMap();
	}

	/**
	 * Setup a meta data for the tree node. The meta data is specified with a
	 * name and related value.
	 * 
	 * @param name
	 *            The name of the meta data
	 * @param value
	 *            The value of the meta data
	 */
	public void addMetaData(String name, Object value) {
		metaDatas.put(name, value);
	}

	/**
	 * Return the value related with the named meta data
	 * 
	 * @param name
	 *            The name of the meta data to be returned
	 * @return The value of the meta data
	 */
	public Object getMetaData(String name) {
		return metaDatas.get(name);
	}

	/**
	 * Return all meta datas of this tree node. The typical implementation would
	 * be returning a copy of all meta datas
	 * 
	 * @return All meta datas of key-value pairs (name with value)
	 */
	public Map getAllMetaData() {
		if (metaDatas == null)
			return null;
		return new HashMap(metaDatas);
	}

	/**
	 * Whether there are meta datas of the related object, such as tree node
	 * 
	 * @return Whether there are some meta data existing
	 */
	public boolean hasMetaData() {
		return !metaDatas.isEmpty();
	}

	/**
	 * The copy function. The object wants to be copied needs to implement this
	 * method.
	 * 
	 * The shallow or deep copy is dependent on the object which is to be
	 * copied.
	 * 
	 * This method is usually invoked in the pop up event of the tree node, but
	 * it can also be used in more general ways.
	 * 
	 * In this method, a new TreeNodeMetaData is returned with all mappings in
	 * this class.
	 * 
	 * @return The copied object
	 */
	public Object copy() {
		return new TreeNodeMetaData(this);
	}

	/**
	 * Paste the specified object to the specified space.
	 * 
	 * The object to be pasted is often the returned object from the copy
	 * function.
	 * 
	 * The object to be pasted should be of type TreeNodeMetaData.
	 * 
	 * @param toPaste
	 *            The object to be pasted
	 * @return Whether the operation is successful. If it succeeds, true should
	 *         be returned. Otherwise, false is returned
	 */
	public boolean paste(Object toPaste) {
		if (toPaste == null || !(toPaste instanceof MetaData)) {
			return false;
		}
		this.metaDatas.clear();
		Map md = ((MetaData) toPaste).getAllMetaData();
		if (md != null)
			this.metaDatas.putAll(md);
		return true;
	}
}
