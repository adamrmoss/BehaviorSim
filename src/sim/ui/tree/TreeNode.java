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

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A generic tree node.
 * 
 * It is a simple DeafultMutableTreeNode plus some additional methods that
 * return the text to display. In addition it provides an icon. And a meta data
 * model.
 * 
 * Also, this class supports <code>ClickListener</code>, which can feed back the
 * click event of this tree node; supports <code>PopupListener</code>, which can
 * feed back the pop up event of this tree node.
 * 
 * All these two listeners can be optionally setup by the client.
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 11:13AM
 * @see sim.ui.tree.MetaData
 * @see sim.ui.tree.ClickListener
 * @see sim.ui.tree.PopupListener
 */
public class TreeNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4727756990939576415L;

	/* The display text of the tree node */
	private String displayText;

	/* The image icon of this tree node */
	private ImageIcon imageIcon;

	/* The click listeners setuped by the client */
	private List clickListeners = null;

	/* The pop up listeners setuped by the client */
	private List popupListeners = null;

	/* The meta data related with this tree node */
	private MetaData metaData = null;

	/**
	 * Creates a TreeNode with the given userObject.
	 * 
	 * @param userObject
	 *            the Object to use as content
	 */
	public TreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * Creates a new TreeNode with the userObject set to null.
	 */
	public TreeNode() {
	}

	/**
	 * MultiCast the click event of this node to all click listeners
	 * 
	 * @param event
	 *            The click event to be multicasted
	 */
	public void multicastClickEvent(ClickEvent event) {
		if (clickListeners == null)
			return;
		for (int i = 0; i < clickListeners.size(); i++) {
			AppListener l = (AppListener) clickListeners.get(i);
			l.onApplicationEvent(event);
		}
	}

	/**
	 * MultiCast the pop up event of this node to all pop up listeners
	 * 
	 * @param event
	 *            The pop up event to be multicasted
	 */
	public void multicastPopupEvent(PopupEvent event) {
		if (popupListeners == null)
			return;
		for (int i = 0; i < popupListeners.size(); i++) {
			AppListener l = (AppListener) popupListeners.get(i);
			l.onApplicationEvent(event);
		}
	}

	/**
	 * Set the meta data model of this tree node
	 * 
	 * @param md
	 *            The meta data to set
	 */
	public void setMetaData(MetaData md) {
		this.metaData = md;
	}

	/**
	 * Return the meta data related with this node
	 * 
	 * @return The meta data
	 */
	public MetaData getMetaData() {
		return new TreeNodeMetaData(this.metaData);
	}

	/**
	 * Add a click listener to this node. If the list of click listeners is
	 * null, initializing it first.
	 * 
	 * @param cl
	 *            The click listener to be added
	 */
	public void addClickListener(ClickListener cl) {
		if (clickListeners == null)
			clickListeners = new ArrayList(1);
		if (cl != null)
			clickListeners.add(cl);
	}

	/**
	 * Add a pop up listener to this node. If the list of pop up listeners is
	 * null, initializing it first.
	 * 
	 * @param pl
	 *            The pop up listener to be added
	 */
	public void addPopupListener(PopupListener pl) {
		if (popupListeners == null)
			popupListeners = new ArrayList(1);
		if (pl != null)
			popupListeners.add(pl);
	}

	/**
	 * Reset the click listeners
	 */
	public void resetClickListener() {
		clickListeners = new ArrayList(1);
	}

	/**
	 * Reset the popup listeners
	 */
	public void resetPopupListener() {
		popupListeners = new ArrayList(1);
	}

	/**
	 * Add a list of click listeners
	 * 
	 * @param cl
	 *            The list of click listeners to add
	 */
	public void addClickListener(List cl) {
		if (clickListeners == null)
			clickListeners = new ArrayList(1);
		if (cl != null)
			clickListeners.addAll(cl);
	}

	/**
	 * Add a list of pop up listeners
	 * 
	 * @param cl
	 *            The list of pop up listeners to add
	 */
	public void addPopupListener(List pl) {
		if (popupListeners == null)
			popupListeners = new ArrayList(1);
		if (pl != null)
			popupListeners.addAll(pl);
	}

	/**
	 * Return all click listeners. Null will be returned if no click listeners
	 * are configured.
	 * 
	 * @return All click listeners
	 */
	public List getClickListener() {
		if (clickListeners == null)
			return null;
		return new ArrayList(clickListeners);
	}

	/**
	 * Return all pop up listeners. Null will be returned if no pop up listeners
	 * are configured.
	 * 
	 * @return All pop up listeners
	 */
	public List getPopupListener() {
		if (popupListeners == null)
			return null;
		return new ArrayList(popupListeners);
	}

	/**
	 * Returns the Icon used to display this node.
	 * 
	 * @return the ImageIcon
	 */
	public ImageIcon getImageIcon() {
		return this.imageIcon;
	}

	/**
	 * Set the Icon used to display this node
	 * 
	 * @param imageIcon
	 *            The image icon to be setted.
	 */
	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	/**
	 * Set the display text of this node
	 * 
	 * @param displayText
	 *            The display text to set
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * Returns the main display text use to present this node to the user.
	 * 
	 * @return the display text
	 */
	public String getDisplayText() {
		return this.displayText;
	}

}
