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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import sim.core.AppEngine;
import sim.model.action.BehaviorAction;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.entity.BNCategory;
import sim.model.mechanism.CooperativeMechanism;
import sim.model.mechanism.MutualInhibitionMechanism;
import sim.ui.BehaviorView;
import sim.ui.DefineBehaviorPanel;
import sim.ui.NavigationPanel;
import sim.ui.tree.AppEvent;
import sim.ui.tree.BasePanel2;
import sim.ui.tree.ClickEvent;
import sim.ui.tree.ClickListener;
import sim.ui.tree.MetaData;
import sim.ui.tree.PopupEvent;
import sim.ui.tree.PopupListener;
import sim.ui.tree.TreeNode;
import sim.ui.tree.TreeNodeMetaData;
import sim.ui.tree.TreeUpdateListener;
import sim.util.MessageUtils;

/**
 * The behavior network definition panel. It is used to define behavior network
 * for the specified agent. And it is invoked when the node of
 * "behavior network" is clicked in the navigation tree located in the system
 * editor.
 * 
 * The panel contains two controls: Left-side tree control and right-side
 * property editor. The left-side tree control shows the behavior network in a
 * hierarchical way, where behaviors are listed as the child node of the
 * behavior network node. The property editor is used to specify properties of
 * behavior and behavior network.
 * 
 * @author Fasheng Qiu
 * @since 10/20/2007
 * 
 */
public class BehaviorNetworkDefinePanel extends BasePanel2 implements
		TreeUpdateListener, BehaviorNetworkListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3931855366562460400L;
	/** The type of different tree nodes */
	private static final int BEHAVIORNETWORK_NODE = -1;
	private static final int BEHAVIOR_NODE = 0;

	/** Types for sub tree nodes */
	private static final int BEHAVIORNETWORKSUB_NODE = -3;
	private static final int BEHAVIORSUB_NODE = 1;

	/** The empty node */
	private static final TreeNode noBehaviorsNode = new TreeNode(
			"No behaviors.");
	private static final TreeNode noBehaviorsNode2 = new TreeNode(
			"No behavior networks.");

	/** Panels */
	private BehaviorView behaviorNetworkPanel = null;
	private JScrollPane behaviorNetworkPanelScrollPane = null;
	private DefineBehaviorPanel behaviorPanel = null;
	private static NavigationPanel parent = null;

	/** Entity to edit */
	private static AppEngine engineRef = AppEngine.getInstance();
	private BNCategory entity = null;

	/** Pop up menus */
	private JPopupMenu popupMenu1 = null;
	private JPopupMenu popupMenu2 = null;

	private JPopupMenu popupMenu3 = null;
	private JPopupMenu popupMenu4 = null;

	/** Copied behavior network and behavior */
	private BehaviorNetwork copiedBN = null;
	private Behavior copiedB = null;

	/**
	 * Constructor
	 */
	public BehaviorNetworkDefinePanel(final NavigationPanel p, BNCategory e) {
		super(e);

		/** Initialize states */
		parent = p;

		/** Setup entity */
		entity = (BNCategory) userObject;

		/** Setup panels */
		this.behaviorNetworkChanged();
		behaviorPanel = new DefineBehaviorPanel(behaviorNetworkPanel, this,
				entity);

		/** Setup pop up menus */
		popupMenu1 = new JPopupMenu("Behavior Network");
		JMenuItem newItem = popupMenu1.add("Create New Behavior");
		newItem.setToolTipText("Create a new behavior for this entity.");
		popupMenu1.addSeparator();
		JMenuItem pastItem = popupMenu1.add("Paste Behavior");
		pastItem.setToolTipText("Assign a copied behavior to this entity.");
		JMenuItem pastBNItem = popupMenu1.add("Paste Behavior Network");
		pastBNItem
				.setToolTipText("Assign a copied behavior network including behaviors to this entity.");
		popupMenu1.addSeparator();
		JMenuItem removeAllItem = popupMenu1.add("Remove All Behaviors");
		removeAllItem.setToolTipText("Remove all behaviors of this entity.");

		popupMenu2 = new JPopupMenu("Behavior");
		JMenuItem removeItem = popupMenu2.add("Remove This Behavior");
		removeItem
				.setToolTipText("Remove the selected behavior from this entity.");

		popupMenu3 = new JPopupMenu("Existing Behavior Networks");
		JMenuItem copyBNItem = popupMenu3.add("Copy This Behavior Network");
		copyBNItem
				.setToolTipText("Make a copy of the selected behavior network.");

		popupMenu4 = new JPopupMenu("Behavior");
		JMenuItem copyItem = popupMenu4.add("Copy This Behavior");
		copyItem.setToolTipText("Make a copy of the selected behavior.");

		/** Add actions to each item */
		newItem.addActionListener(new ActionListener() {
			// Create a new behavior
			public void actionPerformed(ActionEvent e) {
				behaviorPanel.setBehavior(null);
				propertyPanel.setCenterComponent(behaviorPanel);
				propertyPanel.getGradientTitlePanel().setTitleText(
						"Create a new behavior");
				propertyPanel.getGradientTitlePanel().setIcon(
						popupNode.getImageIcon());
			}
		});
		pastItem.addActionListener(new ActionListener() {
			// Paste behavior
			public void actionPerformed(ActionEvent e) {
				// Paste a behavior
				if (copiedB != null) {
					Behavior b = null;
					try {
						b = engineRef
								.createNewBehavior(
										entity,
										copiedB.getBehaviorName(),
										copiedB.getBehaviorEquation(),
										copiedB.isResumable(),
										((BehaviorAction) engineRef.system.actionRepository
												.getAction(copiedB.getMyId(),
														copiedB.getMyId()
																+ "Action"))
												.getActionString());
						behaviorNetworkPanel
								.behaviorAddedToNetwork(new int[] { b.getMyId() });
						behaviorNetworkPanel.behaviorDefined(b.getMyId());
						behaviorNetworkChanged();
						MessageUtils.displayNormal("The behavior '"
								+ b.getBehaviorName()
								+ "' is pasted successfully.");
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageUtils.displayError(ex);
					} finally {
						MessageUtils.displayNormal("Behavior '"+copiedB.getBehaviorName()+"' is pasted successfully.");
						copiedB = null;
					}
					
				}
			}
		});

		pastBNItem.addActionListener(new ActionListener() {
			// Paste behavior network
			public void actionPerformed(ActionEvent e) {
				// Paste a behavior network
				if (copiedBN != null) {
					BehaviorNetwork copy = entity.getBehaviorNetwork().copy(
							true);
					try {
						entity.getBehaviorNetwork().paste(entity, copiedBN);
						behaviorNetworkChanged();
						MessageUtils
								.displayNormal("The behavior network is pasted successfully.");
					} catch (Exception ex) {
						// Exception occurs, paste back the original one.
						try {
							entity.getBehaviorNetwork().paste(entity, copy);
						} catch (Exception ex2) {
						}
						ex.printStackTrace();
						MessageUtils.displayError(ex);
					}
					copiedBN = null;
					MessageUtils.displayNormal("Behavior network is pasted successfully.");
				}
			}
		});

		copyItem.addActionListener(new ActionListener() {
			// Past behavior
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Type of the node
				int type = ((Integer) md.getMetaData("type")).intValue();
				if (type != BEHAVIORSUB_NODE)
					return;
				// Entity of the node
				BNCategory c = (BNCategory) md.getMetaData("entity");
				if (c == null)
					return;
				// Behavior to copy
				Behavior behavior = (Behavior) md.getMetaData("behavior");
				if (behavior == null)
					return;
				// Copy the behavior
				copiedB = behavior;
				MessageUtils.displayNormal("Behavior '"+behavior.getBehaviorName()+"' is copied successfully.");
			}
		});
		copyBNItem.addActionListener(new ActionListener() {
			// Past behavior network
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Type of the node
				int type = ((Integer) md.getMetaData("type")).intValue();
				if (type != BEHAVIORNETWORKSUB_NODE)
					return;
				// Entity of the node
				BNCategory c = (BNCategory) md.getMetaData("entity");
				if (c == null)
					return;
				// Behavior network
				copiedBN = c.getBehaviorNetwork();
				MessageUtils.displayNormal("Behavior network of entity '"+c.getDisplayName()+"' is copied successfully.");
			}
		});

		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Type of the node
				int type = ((Integer) md.getMetaData("type")).intValue();
				if (type != BEHAVIOR_NODE)
					return;
				// Behavior name
				String behaviorName = (String) popupNode.getUserObject();
				// Ask for confirm
				int ret = MessageUtils
						.displayConfirm("Do you want to remove the behavior '"
								+ behaviorName + "'");
				if (ret != JOptionPane.OK_OPTION) {
					return;
				}
				// Remove the behavior from the behavior network of this entity
				if (entity == null) {
					MessageUtils
							.displayNormal(BehaviorNetworkDefinePanel.this,
									"Can not identify the target entity, please close the window and try again.");
					return;
				}
				Behavior behavior = (Behavior) md.getMetaData("behavior");
				// Remove the behavior from the behavior network and the trees
				removeFromAll(behavior);
				// Confirm
				MessageUtils.displayNormal("Behavior '" + behaviorName
						+ "' is removed successfully");
			}
		});
		removeAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Type of the node
				int type = ((Integer) md.getMetaData("type")).intValue();
				if (type != BEHAVIORNETWORK_NODE)
					return;
				// Ask for confirm
				int ret = MessageUtils
						.displayConfirm("Do you want to remove all behaviors?");
				if (ret != JOptionPane.OK_OPTION) {
					return;
				}
				// Remove the behavior from the behavior network of this entity
				if (entity == null) {
					MessageUtils
							.displayNormal(BehaviorNetworkDefinePanel.this,
									"Can not identify the target entity, please close the window and try again.");
					return;
				}
				BehaviorNetwork bn = entity.getBehaviorNetwork();
				List behaviors = bn.getBehaviorList();
				for (int i = 0; i < behaviors.size(); i++) {
					// Get each behavior
					Behavior b = (Behavior) behaviors.get(i);
					try {
						// Remove the behavior from the behavior networks and
						// the trees
						removeFromAll(b);
					} catch (Exception ee) {
						MessageUtils.debug(this, "removeAllSubItem", ee);
					}
				}
				// Confirm
				MessageUtils
						.displayNormal("All behaviors are removed successfully");
			}
		});
	}

	/**
	 * Behavior network is changed. Update relative GUIs
	 */
	public void behaviorNetworkChanged() {

		/** Mark the application as dirty */
		AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);
		/** Reset panels */
		try {
			behaviorNetworkPanel = new BehaviorView(engineRef, entity);
		} catch (Exception e) {
			MessageUtils.error(this, "behaviorNetworkChanged", e);
			return;
		}
		behaviorNetworkPanelScrollPane = new JScrollPane();
		behaviorNetworkPanelScrollPane.getViewport().add(behaviorNetworkPanel);
		propertyPanel.setCenterComponent(behaviorNetworkPanelScrollPane);
		propertyPanel
				.getGradientTitlePanel()
				.setTitleText(
						"Behavior network - Action Selection Mechanism: "
								+ (entity.getActionSelectionMechanism() instanceof sim.model.mechanism.CooperativeMechanism ? "Cooperative"
										: "Mutual inhibition"));
		// Refresh behavior network
		behaviorNetworkPanel.setBehaviorNetworkListener(this);
		behaviorNetworkPanel.refreshBehaviorNetwork(true);
		// Refresh the main tree
		treeChanged();
		// Update behavior network
		parent.updateEntityDynamics(entity);
		// Insert no dynamics if no behaviors configured for the behavior
		// network
		parent.insertNoDynamicsIfNecessary(entity);

	}

	/**
	 * Remove the given behavior and refresh left-side tree control and the
	 * navigation tree.
	 * 
	 * @param behavior
	 *            The behavior to delete
	 */
	private void removeFromAll(Behavior behavior) {

		// Remove the behavior from the system
		engineRef.removeBehavior(entity, behavior, false);

		// Remove the behavior from the behavior network and the trees
		// --- Refresh behavior network of this entity
		this.behaviorNetworkChanged();

	}

	private class MyPopupEventListener extends PopupListener {
		/**
		 * Handle the pop up event of the tree nodes. The source node can be
		 * retrieved from the event.
		 */
		public void onApplicationEvent(AppEvent event) {
			super.onApplicationEvent(event);
			PopupEvent pe = (PopupEvent) event;
			// The tree node that published this event
			TreeNode treeNode = pe.getTreeNode();
			// Check the meta data
			MetaData md = treeNode.getMetaData();
			// The node type
			int type = ((Integer) md.getMetaData("type")).intValue();
			switch (type) {
			case BEHAVIORNETWORK_NODE:
				popupMenu1.show(tree, pe.getX(), pe.getY());
				break;
			case BEHAVIOR_NODE:
				popupMenu2.show(tree, pe.getX(), pe.getY());
				break;
			case BEHAVIORNETWORKSUB_NODE:
				popupMenu3.show(subTree, pe.getX(), pe.getY());
				break;
			case BEHAVIORSUB_NODE:
				popupMenu4.show(subTree, pe.getX(), pe.getY());
				break;
			}
		}
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
			case BEHAVIORNETWORK_NODE: {
				propertyPanel
						.setCenterComponent(behaviorNetworkPanelScrollPane);
				propertyPanel.getGradientTitlePanel().setTitleText(
						"Behavior network");
				propertyPanel.getGradientTitlePanel().setIcon(
						treeNode.getImageIcon());
				break;
			}
			case BEHAVIORSUB_NODE: /* Available behaviors */

			case BEHAVIOR_NODE: { /* Entity Behaviors */

				Behavior behavior = (Behavior) md.getMetaData("behavior");
				StringBuffer title = new StringBuffer();
				if (behavior != null)
					title.append("Modifying behavior '").append(
							behavior.getBehaviorName()).append("'");
				else
					title.append("Create a new behavior");
				showBehaviorEditorPanel(behavior, title.toString(), treeNode);
				break;
			}
			}
		}

		/**
		 * Show behavior updating panel
		 */
		private void showBehaviorEditorPanel(Behavior behavior, String title,
				TreeNode treeNode) {
			behaviorPanel.setBehavior(behavior);
			propertyPanel.setCenterComponent(behaviorPanel);
			propertyPanel.getGradientTitlePanel()
					.setTitleText(title.toString());
			propertyPanel.getGradientTitlePanel().setIcon(
					treeNode.getImageIcon());
		}
	}

	/**
	 * The tree is needed to rebuilt, since categories are changed
	 */
	public void treeChanged() {
		/** Mark the application as dirty */
		AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);

		/** Reset the tree model */
		// Tree root
		TreeNode top = new TreeNode("Behavior Network");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(top);
		tree.setModel(defaultModel);
		// Rebuild the model
		buildTreeModel(defaultModel, top);
	}

	/**
	 * The sub tree is needed to rebuilt, since categories are changed
	 */
	public void subTreeChanged() {
		/** Mark the application as dirty */
		AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);

		/** Reset the tree model */
		// Tree root
		TreeNode top = new TreeNode("Other Behavior Networks");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(top);
		subTree.setModel(defaultModel);
		// Rebuild the model
		buildSubTreeModel(defaultModel, top);
	}

	protected void buildSubTree() {
		/** Tree root and model */
		// Tree root
		TreeNode subTop = new TreeNode("Other Behavior Networks");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(subTop);

		// Build tree root
		subTree = new JTree(subTop);
		// Tree model
		subTree.setModel(defaultModel);
		buildSubTreeModel(defaultModel, subTop);
	}

	protected void buildTree() {
		/** Tree root and model */
		// Tree root
		TreeNode top = new TreeNode("Behavior Network");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(top);

		// Build tree root
		tree = new JTree(top);
		// Tree model
		tree.setModel(defaultModel);
		buildTreeModel(defaultModel, top);
	}

	protected void buildTreeModel(DefaultTreeModel defaultModel, TreeNode top) {

		// Create the listeners
		MyClickEventListener cl = new MyClickEventListener();
		MyPopupEventListener pl = new MyPopupEventListener();

		// Set up the pop up event listener for the tree root
		MetaData md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(BEHAVIORNETWORK_NODE));
		top.setMetaData(md);
		top.addPopupListener(pl);
		top.addClickListener(cl);

		// Obtain all configured behaviors in the behavior network
		if (entity == null) {
			return;
		}
		BehaviorNetwork network = entity.getBehaviorNetwork();
		List behaviors = network.getBehaviorList();
		if (behaviors.isEmpty()) {
			defaultModel.insertNodeInto(noBehaviorsNode, top, 0);
			// Expand the root tree node
			super.expand(top, tree);
			return;
		}

		for (int m = 0; m < behaviors.size(); m++) {
			// Behavior to handle
			Behavior b = (Behavior) behaviors.get(m);
			// Construct a node to represent this category
			TreeNode catNode = new TreeNode(b.getBehaviorName());
			// Construct a meta data model (node type) for that node
			md = new TreeNodeMetaData();
			md.addMetaData("type", new Integer(BEHAVIOR_NODE));
			md.addMetaData("behavior", b);
			catNode.setMetaData(md);
			catNode.addClickListener(cl);
			catNode.addPopupListener(pl);
			// Insert the node into the correct position
			defaultModel.insertNodeInto(catNode, top, m);
		}

		// Expand the root tree node
		super.expand(top, tree);
	}

	protected void buildSubTreeModel(DefaultTreeModel defaultModel, TreeNode top) {

		// Create the listeners
		MyPopupEventListener pl = new MyPopupEventListener();

		// Set up the pop up event listener for the tree root
		MetaData md = null;

		// Obtain all existing behavior networks in the system
		List entities = engineRef.getAvailableEntities();
		if (entities.isEmpty()) {
			defaultModel.insertNodeInto(noBehaviorsNode2, top, 0);
			// Expand the root tree node
			super.expand(top, subTree);
			return;
		}
		int catIndex = 0;
		boolean hasBN = false;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i) == userObject) {
				continue;
			}
			if (!(entities.get(i) instanceof BNCategory)) {
				continue;
			}
			BNCategory entity = (BNCategory) entities.get(i);
			BNCategory thisEntity = (BNCategory) userObject;
			if (entity.getDisplayName().equals(thisEntity.getDisplayName())) {
				continue;
			}
			// Skip different action mechanisms
			if (entity.getActionSelectionMechanismIndex() != thisEntity
					.getActionSelectionMechanismIndex()) {
				continue;
			}
			if (entity.getActionSelectionMechanism() instanceof MutualInhibitionMechanism
					|| entity.getActionSelectionMechanism() instanceof CooperativeMechanism) {
			} else {
				continue;
			}
			List behaviors = entity.getBehaviorNetwork().getBehaviorList();
			if (behaviors.isEmpty()) {
				continue;
			}
			hasBN = true;
			// Construct a node to represent this entity
			TreeNode catNode = new TreeNode(entity.getDisplayName());
			// Insert the node into the correct position
			defaultModel.insertNodeInto(catNode, top, catIndex++);
			// Construct a node to represent the behavior network
			TreeNode netNode = new TreeNode("Behavior Network");
			// Insert the node into the correct position
			defaultModel.insertNodeInto(netNode, catNode, 0);
			// Construct a meta data model (node type) for that node
			md = new TreeNodeMetaData();
			md.addMetaData("type", new Integer(BEHAVIORNETWORKSUB_NODE));
			md.addMetaData("entity", entity);
			netNode.setMetaData(md);
			netNode.addPopupListener(pl);
			for (int m = 0; m < behaviors.size(); m++) {
				// Behavior to handle
				Behavior b = (Behavior) behaviors.get(m);
				// Construct a node to represent this category
				TreeNode behavNode = new TreeNode(b.getBehaviorName());
				// Construct a meta data model (node type) for that node
				md = new TreeNodeMetaData();
				md.addMetaData("type", new Integer(BEHAVIORSUB_NODE));
				md.addMetaData("entity", entity);
				md.addMetaData("behavior", b);
				behavNode.setMetaData(md);
				behavNode.addPopupListener(pl);
				// Insert the node into the correct position
				defaultModel.insertNodeInto(behavNode, netNode, m);
			}
		}

		if (!hasBN) {
			defaultModel.insertNodeInto(noBehaviorsNode2, top, 0);
			// Expand the root tree node
			super.expand(top, subTree);
			return;
		}

		// Expand the root tree node
		super.expand(top, subTree);
	}

}
