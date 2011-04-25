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

package sim.ui.panels;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import sim.model.entity.BNCategory;
import sim.ui.DefineGeneralDyanmicsPanel;
import sim.ui.tree.AppEvent;
import sim.ui.tree.BasePanel;
import sim.ui.tree.ClickEvent;
import sim.ui.tree.ClickListener;
import sim.ui.tree.MetaData;
import sim.ui.tree.TreeNode;
import sim.ui.tree.TreeNodeMetaData;

/**
 * The general dynamics definition panel. It is used to define general dynamics
 * for the specified agent. And it is invoked when the node of "entity dynamics"
 * is clicked in the navigation tree located in the system editor.
 * 
 * The panel contains two controls: Left-side tree control and right-side
 * property editor. The left-side tree control shows the dynamics in a
 * hierarchical way, where behaviors are listed as the child node of the
 * behavior network node. The property editor is used to specify properties of
 * behavior and behavior network.
 * 
 * @author Fasheng Qiu
 * @since 10/20/2007
 * 
 */
public class GeneralDynamicsDefinePanel extends BasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5962969842457567519L;
	/** The type of different tree nodes */
	private static final int PARENT_NODE = -1;
	private static final int DYNAMICS_NODE = 0;

	/** Panels */
	private DefineGeneralDyanmicsPanel dynamicPanel = null;

	/** Entity to edit */
	private BNCategory entity = null;

	/**
	 * Constructor
	 */
	public GeneralDynamicsDefinePanel(BNCategory e) {

		/** Setup entity */
		entity = e;

		/** Setup panels */
		dynamicPanel = new DefineGeneralDyanmicsPanel(entity);

		/** Show panel */
		StringBuffer title = new StringBuffer();
		title.append("Create dynamics for entity '").append(
				entity.getDisplayName()).append("'");

		showDynamicsEditorPanel(title.toString(), null);

	}

	/**
	 * The click event listener for tree nodes
	 */
	private class MyClickEventListener extends ClickListener {
		/**
		 * Handle the click event of the tree nodes. The source node can be
		 * retrieved from the event.
		 */
		public void onApplicationEvent(AppEvent event) {
			// Check the event type to make sure that it is a click event
			super.onApplicationEvent(event);
			ClickEvent ce = (ClickEvent) event;
			// Check the source node
			TreeNode treeNode = ce.getTreeNode();
			// Check the meta data
			MetaData md = treeNode.getMetaData();
			// The node type
			int type = ((Integer) md.getMetaData("type")).intValue();
			switch (type) {

			case DYNAMICS_NODE: { /* Entity dynamics */

				StringBuffer title = new StringBuffer();
				title.append("Create dynamics for entity '").append(
						entity.getDisplayName()).append("'");
				showDynamicsEditorPanel(title.toString(), treeNode);
				break;
			}

			}
		}

	}

	/**
	 * Show behavior updating panel
	 */
	private void showDynamicsEditorPanel(String title, TreeNode treeNode) {
		dynamicPanel.initCode();
		propertyPanel.setCenterComponent(dynamicPanel);
		propertyPanel.getGradientTitlePanel().setTitleText(title);
		if (treeNode != null)
			propertyPanel.getGradientTitlePanel().setIcon(
					treeNode.getImageIcon());
	}

	protected void buildTree() {
		/** Tree root and model */
		// Tree root
		TreeNode top = new TreeNode("Entity Dynamics");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(top);

		// Build tree root
		tree = new JTree(top);
		// Tree model
		tree.setModel(defaultModel);
		buildTreeModel(defaultModel, top);
		// Expand tree node
		expand(top);
	}

	protected void buildTreeModel(DefaultTreeModel defaultModel, TreeNode top) {

		// Create the listeners
		MyClickEventListener cl = new MyClickEventListener();

		// Set up the pop up event listener for the tree root
		MetaData md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(PARENT_NODE));
		top.setMetaData(md);
		top.addClickListener(cl);

		TreeNode dynamics = new TreeNode("General Dynamics");
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DYNAMICS_NODE));
		dynamics.setMetaData(md);
		dynamics.addClickListener(cl);
		defaultModel.insertNodeInto(dynamics, top, 0);

	}

}
