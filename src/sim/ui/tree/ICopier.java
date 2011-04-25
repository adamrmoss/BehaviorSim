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

/**
 * This is the copier and paster interface.
 * 
 * It is used to implement the copying and pasting functionality of tree node
 * and its meta data.
 * 
 * If one object is need to support the copying and paste function, it should
 * implement this interface
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 10:51
 * 
 */
public interface ICopier extends Cloneable {
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
	 * @return The copied object
	 */
	Object copy();

	/**
	 * Paste the specified object to the specified space.
	 * 
	 * The object to be pasted is often the returned object from the copy
	 * function.
	 * 
	 * 
	 * @param toPaste
	 *            The object to be pasted
	 * @return Whether the operation is successful. If it succeeds, true should
	 *         be returned. Otherwise, false is returned
	 */
	boolean paste(Object toPaste);
}
