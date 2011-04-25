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
 * The tree update listener
 * 
 * When category/entity/behavior network information is changed in the system,
 * the event source notifies the <code>TreeUpdateListener</code> to reflect the
 * change.
 * 
 * The destination where the tree defined should implement this interface and
 * the source where the information changes should notify the source about the
 * change.
 * 
 * @author Fasheng Qiu
 * @since 10/20/2007
 * 
 */
public interface TreeUpdateListener {
	/**
	 * A simple method used to notify the change
	 */
	public void treeChanged();

	/**
	 * The sub tree is changed
	 */
	public void subTreeChanged();
}
