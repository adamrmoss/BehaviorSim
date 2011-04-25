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
 * The tree node which supports the copy and paste functionalities.
 * 
 * @author Fasheng Qiu
 * 
 */
public class CopyTreeNode extends TreeNode implements ICopier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8001745913695415964L;

	/**
	 * Creates a CopyTreeNode with the given userObject.
	 * 
	 * @param userObject
	 *            the Object to use as content
	 */
	public CopyTreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * Creates a new CopyTreeNode with the userObject set to null.
	 */
	public CopyTreeNode() {
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
	 * @return The copied object
	 */
	public Object copy() {
		TreeNode newNode = new CopyTreeNode();
		newNode.setUserObject(super.getUserObject());
		newNode.setDisplayText(super.getDisplayText());
		newNode.setImageIcon(super.getImageIcon());
		newNode.addClickListener(super.getClickListener());
		newNode.addPopupListener(super.getPopupListener());
		newNode.setMetaData(super.getMetaData());
		return newNode;
	}

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
	public boolean paste(Object toPaste) {
		if (toPaste == null || !(toPaste instanceof CopyTreeNode)) {
			return false;
		}
		CopyTreeNode ctn = (CopyTreeNode) toPaste;
		this.setUserObject(ctn.getUserObject());
		this.setDisplayText(ctn.getDisplayText());
		this.setImageIcon(ctn.getImageIcon());
		this.resetClickListener();
		this.resetPopupListener();
		this.addClickListener(ctn.getClickListener());
		this.addPopupListener(ctn.getPopupListener());
		this.setMetaData(ctn.getMetaData());
		return true;
	}
}
