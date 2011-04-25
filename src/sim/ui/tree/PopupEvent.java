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
 * Pop up event wrapper
 * 
 * This is the pop up event from tree nodes. The object that published this
 * event is of type <code>TreeNode</code>, if the <code>PopupEvent</code> is
 * configured in the node.
 * 
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 10:26PM
 * 
 */
public class PopupEvent extends AppEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4103987464000832985L;
	/** The location where pop up event occurs */
	private int x = 0;
	private int y = 0;

	/**
	 * Create a new PopupEvent
	 * 
	 * @param source
	 *            The tree node that published the event
	 */
	public PopupEvent(TreeNode source, int x, int y) {
		super(source);
		this.setLocation(x, y);
	}

	/**
	 * Return the tree node that published the event
	 * 
	 * @return The tree node that published the event
	 */
	public TreeNode getTreeNode() {
		return (TreeNode) getSource();
	}

	/**
	 * Return position of the event
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * Set the location of the event
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
