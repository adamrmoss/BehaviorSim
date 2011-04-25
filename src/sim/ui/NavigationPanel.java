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

package sim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

//import javax.swing.JSplitPane;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import sim.core.AppEngine;
import sim.core.ConfigParameters;
import sim.core.SimulationEnvironment;
import sim.core.AppResources;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.entity.BNCategory;
import sim.model.entity.Category;
import sim.model.entity.Entity;
import sim.model.entity.Property;
import sim.model.mechanism.CooperativeMechanism;
import sim.model.mechanism.MutualInhibitionMechanism;
import sim.model.mechanism.NoDynamicsMechanism;
import sim.model.mechanism.SystemDynamicMechanism;
import sim.ui.panels.BehaviorNetworkDefinePanel;
import sim.ui.panels.PanelDialog;
import sim.ui.tree.TreeNode;
import sim.ui.tree.TreeNodeMetaData;
import sim.util.GUIUtils;
import sim.util.MessageUtils;
import sim.util.Point;

/**
 * System navigation panel for showing all available entities.
 * 
 * @author Pavel
 * @version 1.0
 */
public class NavigationPanel extends AppView implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8506623798320617192L;

	/** Top element */
	private DefaultMutableTreeNode top = new DefaultMutableTreeNode(
			"Application");

	/** The tree model for system entities */
	private DefaultTreeModel systemTreeModel = new DefaultTreeModel(top);

	/** Entities node */
	private DefaultMutableTreeNode entities = new DefaultMutableTreeNode(
			"Entities");

	/** World node */
	private DefaultMutableTreeNode world = new DefaultMutableTreeNode("World");

	/** Node of no entities */
	private DefaultMutableTreeNode noEntitiesNode = new DefaultMutableTreeNode(
			"No entities");

	/** Entity tree */
	private JTree appTree = new JTree(top);

	/** Tree view */
	private JScrollPane treeView = new JScrollPane(appTree);

	/** Tree renderer */
	private DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();

	/** Font */
	private Font mainFont = new Font("Times New Roman", Font.BOLD, 11);

	/** Label */
	private JLabel bNavigationTree = new JLabel("Navigation Tree");

	/** ID of entity to be copied. It is only used in 'copy-and-paste' entities */
	private int IDofEntityToCopy = -1;

	/**
	 * ID of entity whose 'Entity Dynamics' is to be copied. It is only used in
	 * 'copy-and-paste' entity dynamics
	 */
	private int IDofEntityDynamicsToCopy = -1;

	/** Total count of entities */
	public static int totalCount = 0;

	/** No entity node yet? */
	private boolean noentnode = true;

	/** Application engine */
	private AppEngine engineRef = null;

	/** The parent view of the navigation panel */
	private EditorView parentView = null;

	/** Popup menu for tree nodes and menu items */
	private JPopupMenu popupMenu = new JPopupMenu();
	private JPopupMenu popupMenuSystem = new JPopupMenu();
	private JPopupMenu popupMenuWorld = new JPopupMenu();
	private JPopupMenu popupMenuPasteEntity = new JPopupMenu();

	/** Selected Entity id and corresponding node */
	private int entityId = -1;

	/** The name of selected node */
	private int selectedNodeId = -1;

	/**
	 * Constructor
	 * 
	 * @param eng
	 *            Application engine
	 * @param view
	 *            The parent view of this panel
	 */
	public NavigationPanel(AppEngine eng, EditorView view) {
		engineRef = eng;
		parentView = view;

		Border b = BorderFactory.createEmptyBorder(5, 0, 0, 10);
		Border compound = BorderFactory.createCompoundBorder(b, BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		treeView.setBorder(compound);
		treeView.setBackground(new Color(245, 245, 245));

		setLayout(new BorderLayout());
		setBackground(treeView.getBackground());

		treeRenderer.setOpenIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_NODE_EXPAND_IMAGE));
		treeRenderer.setClosedIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_NODE_UNEXPAND_IMAGE));
		treeRenderer.setLeafIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_LEAF_IMAGE));
		appTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		appTree.setShowsRootHandles(true);
		appTree.setCellRenderer(treeRenderer);
		appTree.setFont(mainFont);

		systemTreeModel.insertNodeInto(noEntitiesNode, entities, 0);
		appTree.setModel(systemTreeModel);
		appTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				treeSelectionChanged(e);
			}
		});

		appTree.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				navPanelmouseClicked(e);
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});

		bNavigationTree.setFont(mainFont);
		bNavigationTree.setFocusable(false);

		add(bNavigationTree, BorderLayout.NORTH);
		add(treeView, BorderLayout.CENTER);
		
//		JSplitPane  splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//                treeView, bNavigationTree);
//		splitPane.setResizeWeight(0.8);
//		splitPane.setOneTouchExpandable(true);
//		splitPane.setContinuousLayout(true);
//		add(splitPane, BorderLayout.CENTER);

		// //////////////////////////////////////
		JMenuItem menuItemPasteEntity = new JMenuItem("Paste Entity");
		menuItemPasteEntity.setToolTipText("Paste the copied entity.");
		menuItemPasteEntity.addActionListener(this);
		popupMenuPasteEntity.add(menuItemPasteEntity);

		JMenuItem menuItemWorld = new JMenuItem("Edit World...");
		menuItemWorld
				.setToolTipText("Edit properties of the simulation world.");
		menuItemWorld.addActionListener(this);
		popupMenuWorld.add(menuItemWorld);

		JMenuItem menuItemPosition = new JMenuItem("Edit Position...");
		menuItemPosition.setToolTipText("Change the position of this entity.");
		menuItemPosition.addActionListener(this);
		popupMenu.add(menuItemPosition);

		JMenuItem menuItemDisplay = new JMenuItem("Edit Display...");
		menuItemDisplay.setToolTipText("Edit the display of this entity.");
		menuItemDisplay.addActionListener(this);
		popupMenu.add(menuItemDisplay);

		JMenuItem menuItemProperties = new JMenuItem("Edit Fields...");
		menuItemProperties
				.setToolTipText("Edit values of fields of this entity.");
		menuItemProperties.addActionListener(this);
		popupMenu.add(menuItemProperties);

		popupMenu.addSeparator();

		JMenuItem menuItemCopy2 = new JMenuItem("Copy Dynamics");
		menuItemCopy2.setToolTipText("Copy entity dynamics of this entity.");
		menuItemCopy2.addActionListener(this);
		popupMenu.add(menuItemCopy2);

		JMenuItem menuItemPaste = new JMenuItem("Paste Dynamics");
		menuItemPaste
				.setToolTipText("Assign the copied entity dynamics to this entity.");
		menuItemPaste.addActionListener(this);
		popupMenu.add(menuItemPaste);

		JMenu menuDynamics = new JMenu("Setup Dyanmics");
		JMenuItem menuMutual = new JMenuItem("Mutual Inhibition");
		menuMutual
				.setToolTipText("Setup mutual inhibition action selection mechanism for this entity.");
		JMenuItem menuCooperative = new JMenuItem("Vector Sum");
		menuCooperative
				.setToolTipText("Setup cooperative action selection mechanism for this entity.");
		JMenuItem menuSysDynamics = new JMenuItem("General Dynamics");
		menuSysDynamics
				.setToolTipText("Setup general dynamics for this entity.");

		menuMutual.addActionListener(this);
		menuCooperative.addActionListener(this);
		menuSysDynamics.addActionListener(this);
		menuDynamics.add(menuMutual);
		menuDynamics.add(menuCooperative);
		menuDynamics.add(menuSysDynamics);
		popupMenu.add(menuDynamics);

		// JMenuItem menuViewDynamics = new JMenuItem("View Dynamics");
		// menuViewDynamics.setToolTipText("View dynamics of this entity.");
		// menuViewDynamics.addActionListener(this);
		// popupMenu.add(menuViewDynamics);

		JMenuItem menuRemoveDynamics = new JMenuItem("Remove Dynamics");
		menuRemoveDynamics.setToolTipText("Remove dynamics from this entity.");
		menuRemoveDynamics.addActionListener(this);
		popupMenu.add(menuRemoveDynamics);

		popupMenu.addSeparator();

		JMenuItem menuItemCopy = new JMenuItem("Copy This Entity");
		menuItemCopy.setToolTipText("Make a copy of this entity.");
		menuItemCopy.addActionListener(this);
		popupMenu.add(menuItemCopy);

		JMenuItem menuItemRemove = new JMenuItem("Remove This Entity");
		menuItemRemove.setToolTipText("Remove this entity from the system.");
		menuItemRemove.addActionListener(this);		
		popupMenu.add(menuItemRemove);

		popupMenuSystem = new JPopupMenu("Application");
		JMenuItem edit = popupMenuSystem.add("Edit Application...");
		edit.setToolTipText("Edit properties of current application.");
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dir = null;
				try {
					dir = engineRef.appManager.currentApp.getAppDir();
				} catch (Throwable ee) {
				}
				if (dir == null) {
					return;
				}
				EditSystemProperty editor = new EditSystemProperty(
						new Property("", engineRef.appManager.currentApp
								.getAppName()));
				editor.show();
				String newAppName = editor.getNewAppName();
				if (newAppName != null) {
					engineRef.appManager.currentApp.setAppName(newAppName);
					new sim.core.AppTask().run();
					MainFrame.getInstance().setTitle(
							newAppName
									+ " - "
									+ new File(engineRef.appManager.currentApp
											.getAppDir()
											+ File.separator
											+ engineRef.appManager.currentApp
													.getAppFileName())
											.getAbsolutePath());
					MessageUtils.displayNormal("Application name is updated.");
				}
			}
		});

		initializeSubroots();
		initSimulationWorld();
		updateSimulationWorld();
		addAllEntities();

	}

	/**
	 * Initialize the simulation world (background image)
	 */
	private void initSimulationWorld() {

		// Application engine
		sim.core.AppEngine engineRef = sim.core.AppEngine.getInstance();
		// Simulation environment
		sim.core.SimulationEnvironment se = engineRef
				.getSimulationEnvironment();

		// The default background
		se.setWidth(engineRef.resources.seaImage
				.getWidth(null));
		se.setHeight(engineRef.resources.seaImage.getHeight(null));

		// Set the image and path
		se.setImage(engineRef.resources.seaImage);
		se.setImagePath(engineRef.jrl.getResourcePath() + File.separator
				+ AppResources.seaBitmapPath);

		// Set the relative background image name
		String ip = se.getImagePath().replace('/', File.separatorChar);
		String backgroundImgName = ip
				.substring(ip.lastIndexOf(File.separator) + 1);
		se.setRelativeImagePath(backgroundImgName);

	}

	/**
	 * Initialize sub-root nodes
	 */
	public void initializeSubroots() {
		// Setup world node
		systemTreeModel.insertNodeInto(world, top, 0);
		// Setup entities node
		systemTreeModel.insertNodeInto(entities, top, 1);
		// Expand sub-nodes
		Object elements[] = new Object[2];
		elements[0] = top;
		elements[1] = world;
		appTree.fireTreeExpanded(new TreePath(elements));

		elements[1] = entities;
		appTree.fireTreeExpanded(new TreePath(elements));

	}

	public void updateSimulationWorld() {
		// First remove the children of the world node if necessary
		try {
			DefaultMutableTreeNode n = null;
			for (int i = 0; i < 3; i++) {
				n = (DefaultMutableTreeNode) systemTreeModel.getChild(world, 0);
				systemTreeModel.removeNodeFromParent(n);
			}
		} catch (Exception e) {
		}
		// Simulation environment
		SimulationEnvironment env = engineRef.getSimulationEnvironment();
		// Setup world properties
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("image = "
				+ env.getImagePath());
		systemTreeModel.insertNodeInto(node, world, 0);
		node = new DefaultMutableTreeNode("size = (" + env.getWidth() + ", "
				+ env.getHeight() + ")");
		systemTreeModel.insertNodeInto(node, world, 1);
		node = new DefaultMutableTreeNode("type = "
				+ (env.getType() == SimulationEnvironment.ROUNDED ? "Rounded"
						: env.getType() == SimulationEnvironment.OPEN ? "Open"
								: "Closed"));
		systemTreeModel.insertNodeInto(node, world, 2);

	}

	/**
	 * Return the preferred size of this panel
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * Add all entities into the navigation panel
	 */
	public void addAllEntities() {
		
		List seaEntities = engineRef.getAvailableEntities();
		for (int m = 0; m < seaEntities.size(); m++) {
			Entity entity = (Entity) seaEntities.get(m);
			addNewNode(entity);
		}
		if (seaEntities.isEmpty()) {
			noentnode = true;
			systemTreeModel.insertNodeInto(noEntitiesNode, entities, 0);
		}
		
	}

	/**
	 * Get the minimum size of the panel
	 */
	public Dimension getMinimumSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension(180, screenSize.height * 3 / 4);
	}

	/**
	 * Add a new node for the specified entity
	 * 
	 * @param entity
	 *            The target entity
	 */
	public void addNewNode(Entity entity) {
		try {
			if (noentnode) {
				systemTreeModel.removeNodeFromParent(noEntitiesNode);
				noentnode = false;
			}
			if (entity != null && entity instanceof BNCategory) {
				BNCategory c = (BNCategory) entity;
				totalCount++;
				int pos = 0;

				String displayName = entity.getDisplayName();
				// Tree node
				TreeNode nodeBNCategory = new TreeNode(displayName);
				// Construct a meta data model for that node
				TreeNodeMetaData md = new TreeNodeMetaData();
				md.addMetaData("id", new Integer(entity.getMyId()));
				nodeBNCategory.setMetaData(md);
				systemTreeModel.insertNodeInto(nodeBNCategory, entities,
						totalCount - 1);

				DefaultMutableTreeNode nodeDisplay = new DefaultMutableTreeNode(
						"Display");
				systemTreeModel.insertNodeInto(nodeDisplay, nodeBNCategory,
						pos++);

				DefaultMutableTreeNode nodeImage = new DefaultMutableTreeNode(
						"image=" + entity.getImagePath());
				systemTreeModel.insertNodeInto(nodeImage, nodeDisplay, 0);

				DefaultMutableTreeNode nodeSize = new DefaultMutableTreeNode(
						"size=(" + entity.getWidth() + ", "
								+ entity.getHeight() + ")");
				systemTreeModel.insertNodeInto(nodeSize, nodeDisplay, 1);

				DefaultMutableTreeNode nodePosition = new DefaultMutableTreeNode(
						"Position");
				systemTreeModel.insertNodeInto(nodePosition, nodeBNCategory,
						pos++);

				DefaultMutableTreeNode nodeOthers = new DefaultMutableTreeNode(
						"Fields");
				systemTreeModel.insertNodeInto(nodeOthers, nodeBNCategory,
						pos++);

				DefaultMutableTreeNode nodeBehaviorNetwork = new DefaultMutableTreeNode(
						"Entity Dynamics");
				systemTreeModel.insertNodeInto(nodeBehaviorNetwork,
						nodeBNCategory, pos++);

				DefaultMutableTreeNode nodeX = new DefaultMutableTreeNode("x: "
						+ (int) entity.getPosition().x);
				systemTreeModel.insertNodeInto(nodeX, nodePosition, 0);

				DefaultMutableTreeNode nodeY = new DefaultMutableTreeNode(
						new String("y: " + (int) entity.getPosition().y));
				systemTreeModel.insertNodeInto(nodeY, nodePosition, 1);

				Object elements[] = new Object[3];
				elements[0] = top;
				elements[1] = entities;
				elements[2] = nodeBNCategory;
				TreePath path = new TreePath(elements);
				appTree.fireTreeExpanded(path);

				if (c.isNoDynamicsMechanism()) {
					addNewBehavior(BehaviorNetwork.NODYNAMICS, "No Dynamics", c);
				} else if (c.isSystemDynamicMechanism()) {
					addNewBehavior(BehaviorNetwork.DYNAMICS,
							"General Dynamics", c);
				} else {
					BehaviorNetwork network = c.getBehaviorNetwork();
					List behaviors = network.getBehaviorList();
					for (int m = 0; m < behaviors.size(); m++) {
						Behavior b = (Behavior) behaviors.get(m);
						addNewBehavior(b.getMyId(), b.getBehaviorName(), c);
					}
					if (behaviors.isEmpty()) {
						addNewBehavior(c.getActionSelectionMechanismIndex(),
								"Behavior Network", c);
					}
				}

				// Entity Other Properties
				List p = c.getOriginalProperties();
				for (int i = 0; i < p.size(); i++) {
					Property property = (Property) p.get(i);
					DefaultMutableTreeNode nodeO = new DefaultMutableTreeNode(
							property.name + " = " + property.value);
					systemTreeModel.insertNodeInto(nodeO, nodeOthers, i);
				}
				if (p.isEmpty()) {
					DefaultMutableTreeNode nodeO = new DefaultMutableTreeNode(
							"No fields");
					systemTreeModel.insertNodeInto(nodeO, nodeOthers, 0);
				}
			} else if (entity != null && entity instanceof Category) { // A
				// dynamically
				// created
				// category
				Category c = (Category) entity; // Category object
				totalCount++;
				String displayName = c.getDisplayName();
				// Tree node
				TreeNode nodeOther = new TreeNode(displayName);
				// Construct a meta data model for that node
				TreeNodeMetaData md = new TreeNodeMetaData();
				md.addMetaData("id", new Integer(entity.getMyId()));
				nodeOther.setMetaData(md);
				systemTreeModel.insertNodeInto(nodeOther, entities,
						totalCount - 1);
				// Display
				int pos = 0;
				DefaultMutableTreeNode nodeDisplay = new DefaultMutableTreeNode(
						"Display");
				systemTreeModel.insertNodeInto(nodeDisplay, nodeOther, pos++);

				DefaultMutableTreeNode nodeImage = new DefaultMutableTreeNode(
						"image=" + entity.getImagePath());
				systemTreeModel.insertNodeInto(nodeImage, nodeDisplay, 0);

				DefaultMutableTreeNode nodeSize = new DefaultMutableTreeNode(
						"size=(" + entity.getWidth() + ", "
								+ entity.getHeight() + ")");
				systemTreeModel.insertNodeInto(nodeSize, nodeDisplay, 1);
				// Entity position
				DefaultMutableTreeNode nodePosition = new DefaultMutableTreeNode(
						"Position");
				systemTreeModel.insertNodeInto(nodePosition, nodeOther, pos++);

				DefaultMutableTreeNode nodeOthers = new DefaultMutableTreeNode(
						"Fields");
				systemTreeModel.insertNodeInto(nodeOthers, nodeOther, pos++);

				DefaultMutableTreeNode nodeX = new DefaultMutableTreeNode("x: "
						+ (int) entity.getPosition().x);
				systemTreeModel.insertNodeInto(nodeX, nodePosition, 0);
				DefaultMutableTreeNode nodeY = new DefaultMutableTreeNode(
						new String("y: " + (int) entity.getPosition().y));
				systemTreeModel.insertNodeInto(nodeY, nodePosition, 1);

				// Entity Other Properties
				List p = c.getOriginalProperties();
				for (int i = 0; i < p.size(); i++) {
					Property property = (Property) p.get(i);
					DefaultMutableTreeNode nodeO = new DefaultMutableTreeNode(
							property.name + " = " + property.value);
					systemTreeModel.insertNodeInto(nodeO, nodeOthers, i);
				}
				// Expand the entity tree node
				Object elements[] = new Object[3];
				elements[0] = top;
				elements[1] = entities;
				elements[2] = nodeOther;
				TreePath path = new TreePath(elements);
				appTree.fireTreeExpanded(path);

			}
		} catch (Exception e) {
			MessageUtils.debug(this, "addNewNode", e);
		}

	}

	/**
	 * Add a named behavior node into the target entity
	 * 
	 * @param behaviorID
	 *            The id of the behavior node
	 * @param behaviorName
	 *            name of the behavior
	 * @param entity
	 *            The target entity
	 */
	public void addNewBehavior(int behaviorID, String behaviorName,
			Entity entity) {

		Object root = systemTreeModel.getRoot(); // Root node
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root); // Entity count
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {

				// Tree node
				TreeNode behaviorNode = new TreeNode(behaviorName);
				// Construct a meta data model for that node
				md = new TreeNodeMetaData();
				md.addMetaData("id", new Integer(behaviorID));
				behaviorNode.setMetaData(md);

				DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 3);
				systemTreeModel.insertNodeInto(behaviorNode,
						behaviorNetworkNode, systemTreeModel
								.getChildCount(behaviorNetworkNode));
				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = entities;
				elements[2] = entityNode;
				elements[3] = behaviorNetworkNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}
	}

	/**
	 * Add a 'General Dynamics' node under the 'Entity Dynamics'.
	 * 
	 * @param entity
	 *            The target entity
	 * @param remove
	 *            Whether the predefined dynamics should be removed
	 */
	public void setGeneralDynamics(Entity entity, boolean remove) {
		Object root = systemTreeModel.getRoot(); // Root node
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root); // Entity count
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {
				// Entity dynamics node
				DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 3);
				// Remove the node and add it again first.
				systemTreeModel.removeNodeFromParent(behaviorNetworkNode);
				behaviorNetworkNode = new DefaultMutableTreeNode(
						"Entity Dynamics");
				systemTreeModel.insertNodeInto(behaviorNetworkNode, entityNode,
						3);
				if (remove) {
					return;
				}
				DefaultMutableTreeNode behaviorNode = new DefaultMutableTreeNode(
						"General Dynamics");
				systemTreeModel.insertNodeInto(behaviorNode,
						behaviorNetworkNode, systemTreeModel
								.getChildCount(behaviorNetworkNode));
				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = entities;
				elements[2] = entityNode;
				elements[3] = behaviorNetworkNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}
	}

	/**
	 * Add a 'General Dynamics' node under the 'Entity Dynamics'.
	 * 
	 * @param entity
	 *            The target entity
	 * @param remove
	 *            Whether the predefined dynamics should be removed
	 */
	public void setNoDynamics(Entity entity, boolean remove) {
		Object root = systemTreeModel.getRoot(); // Root node
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root); // Entity count
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {
				// Entity dynamics node
				DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 3);
				// Remove the node and add it again first.
				systemTreeModel.removeNodeFromParent(behaviorNetworkNode);
				behaviorNetworkNode = new DefaultMutableTreeNode(
						"Entity Dynamics");
				systemTreeModel.insertNodeInto(behaviorNetworkNode, entityNode,
						3);
				if (remove) {
					return;
				}
				TreeNode behaviorNode = new TreeNode("No Dynamics");
				md = new TreeNodeMetaData();
				md.addMetaData("type", new Integer(BehaviorNetwork.NODYNAMICS));
				behaviorNode.setMetaData(md);
				systemTreeModel.insertNodeInto(behaviorNode,
						behaviorNetworkNode, systemTreeModel
								.getChildCount(behaviorNetworkNode));
				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = entities;
				elements[2] = entityNode;
				elements[3] = behaviorNetworkNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}
	}

	/**
	 * Remove the specified node from the navigation tree
	 * 
	 * @param entity
	 *            The entity to be removed
	 */
	public void removeEntity(Entity entity) {
		Object root = systemTreeModel.getRoot(); // Tree root
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			// Locate the entity to be removed
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {
				// Iterate the children of this entity
				int childrenCount = entityNode.getChildCount();
				int j = childrenCount - 1;
				while (j >= 0) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) systemTreeModel
							.getChild(entityNode, j--);
					// Iterate the children of this node
					int chC = child.getChildCount();
					int k = chC - 1;
					while (k >= 0) {
						DefaultMutableTreeNode ch = (DefaultMutableTreeNode) systemTreeModel
								.getChild(child, k--);
						systemTreeModel.removeNodeFromParent(ch);
					}
					systemTreeModel.removeNodeFromParent(child);
				}
				systemTreeModel.removeNodeFromParent(entityNode);
				totalCount--;
				break;
			}
		}
		if (totalCount == 0) {
			systemTreeModel.insertNodeInto(noEntitiesNode, entities, 0);
			Object elements[] = new Object[3];
			elements[0] = top;
			elements[1] = root;
			elements[2] = noEntitiesNode;
			TreePath path = new TreePath(elements);
			appTree.fireTreeExpanded(path);
			noentnode = true;
		}

	}

	/**
	 * Insert no dynamics for behavior network of the specified entity if no
	 * behaviors configured for the behavior network.
	 * 
	 * @param bn
	 *            Entity whose behavior is to remove
	 */
	public void insertNoDynamicsIfNecessary(BNCategory bn) {

		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {

			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer idf = (Integer) md.getMetaData("id");
			if (idf.intValue() != bn.getMyId()) {
				continue;
			}

			DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
					.getChild(entityNode, 3);
			int behaviorsCount = systemTreeModel
					.getChildCount(behaviorNetworkNode);
			if (behaviorsCount == 0) {
				addNewBehavior(BehaviorNetwork.NODYNAMICS, "No Dynamics", bn);
			}

			Object elements[] = new Object[4];
			elements[0] = top;
			elements[1] = entities;
			elements[2] = entityNode;
			elements[3] = behaviorNetworkNode;
			TreePath npath = new TreePath(elements);
			appTree.fireTreeExpanded(npath);

			break;

		}

	}

	/**
	 * Remove the named behavior from the specified entity.
	 * 
	 * @param bn
	 *            Entity whose behavior is to remove
	 * @param id
	 *            The ID of the behavior to remove
	 */
	public void removeNodesOfBehavior(BNCategory bn, int id) {
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {

			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer idf = (Integer) md.getMetaData("id");
			if (idf.intValue() != bn.getMyId()) {
				continue;
			}
			DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
					.getChild(entityNode, 3);
			int behaviorsCount = systemTreeModel
					.getChildCount(behaviorNetworkNode);
			for (int j = 0; j < behaviorsCount; j++) {
				TreeNode behaviorNode = (TreeNode) systemTreeModel.getChild(
						behaviorNetworkNode, j);
				md = (TreeNodeMetaData) behaviorNode.getMetaData();
				idf = (Integer) md.getMetaData("id");
				if (idf.intValue() == id) {
					systemTreeModel.removeNodeFromParent(behaviorNode);
					break;
				}
			}

		}
	}

	/**
	 * Remove the named behavior from the target entity
	 * 
	 * @param behaviorID
	 *            The id of the behavior to remove
	 * @param entity
	 *            The target entity
	 */
	public void removeBehavior(int behaviorID, Entity entity) {
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {
				DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 3);
				int behaviorsCount = systemTreeModel
						.getChildCount(behaviorNetworkNode);
				for (int j = 0; j < behaviorsCount; j++) {
					TreeNode behaviorNode = (TreeNode) systemTreeModel
							.getChild(behaviorNetworkNode, j);
					md = (TreeNodeMetaData) behaviorNode.getMetaData();
					id = (Integer) md.getMetaData("id");
					if (id.intValue() == behaviorID) {
						systemTreeModel.removeNodeFromParent(behaviorNode);
						break;
					}
				}
				break;
			}
		}
	}

	/**
	 * Rebuild entity dynamics because of the change of behavior network
	 * 
	 * @param entity
	 *            Target entity to be updated
	 */
	public void updateEntityDynamics(BNCategory entity) {
		// Remove the dynamics first
		setGeneralDynamics(entity, true);
		// Add the behavior network
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {

				// Entity dynamics node
				DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 3);

				// Add behaviors
				BehaviorNetwork network = entity.getBehaviorNetwork();
				List behaviors = network.getBehaviorList();
				for (int m = 0; m < behaviors.size(); m++) {
					Behavior b = (Behavior) behaviors.get(m);
					// Construct a meta data model for that node
					md = new TreeNodeMetaData();
					md.addMetaData("id", new Integer(b.getMyId()));

					TreeNode behaviorNode = new TreeNode(b.getBehaviorName());
					behaviorNode.setMetaData(md);
					systemTreeModel.insertNodeInto(behaviorNode,
							behaviorNetworkNode, systemTreeModel
									.getChildCount(behaviorNetworkNode));
				}

				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = entities;
				elements[2] = entityNode;
				elements[3] = behaviorNetworkNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);

				break;
			}
		}
	}

	/**
	 * Remove all behavior nodes of the target entity
	 * 
	 * @param entity
	 *            The target entity
	 */
	public void removeBehaviors(Entity entity) {
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {

				DefaultMutableTreeNode behaviorNetworkNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 1);
				systemTreeModel.removeNodeFromParent(behaviorNetworkNode);
				behaviorNetworkNode = new DefaultMutableTreeNode(
						"Entity Dynamics");
				systemTreeModel.insertNodeInto(behaviorNetworkNode, entityNode,
						1);
				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = root;
				elements[2] = entityNode;
				elements[3] = behaviorNetworkNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}

	}

	/**
	 * Remove all entity nodes from the navigation panel
	 */
	public void removeAllEntityNodes() {
		
		Object root = systemTreeModel.getRoot(); // Root node
		root = systemTreeModel.getChild(root, 1); // Entities node
		systemTreeModel.removeNodeFromParent((DefaultMutableTreeNode) root); // Remove
		// it
		// first
		entities = new DefaultMutableTreeNode("Entities"); // Re-initialize it
		systemTreeModel.insertNodeInto(entities, top, 1); // Re-insert
		systemTreeModel.insertNodeInto(noEntitiesNode, entities, 0); // Insert no entities node

		Object elements[] = new Object[3];
		elements[0] = top;
		elements[1] = entities;
		elements[2] = noEntitiesNode;
		TreePath path = new TreePath(elements);
		appTree.fireTreeExpanded(path);
		totalCount = 0;
		noentnode = true;

	}

	/**
	 * Remove the entity node recursively
	 * 
	 * @param entity
	 *            The target entity
	 */
	public void removeNode(Entity entity) {
		this.removeEntity(entity);

	}

	/**
	 * Update the value of the named property of a given entity
	 * 
	 * @param entity
	 *            The target entity
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	public void updateEntityProperty(Entity entity, String name, String value) {
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {
				int eCount = systemTreeModel.getChildCount(entityNode);
				for (int j = 0; j < eCount; j++) {
					DefaultMutableTreeNode eNode = (DefaultMutableTreeNode) systemTreeModel
							.getChild(entityNode, j);
					int index = entityNode.getIndex(eNode);
					if (eNode.getUserObject().toString().indexOf(name) != -1) {
						systemTreeModel.removeNodeFromParent(eNode);
						DefaultMutableTreeNode nodeX = new DefaultMutableTreeNode(
								name + " = " + value);
						systemTreeModel
								.insertNodeInto(nodeX, entityNode, index);
					}
				}
				Object elements[] = new Object[3];
				elements[0] = top;
				elements[1] = root;
				elements[2] = entityNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}
	}

	/**
	 * Update the position of the target entity
	 * 
	 * @param entity
	 *            The target entity
	 */
	public void updateEntityPosition(Entity entity) {
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {

			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");

			if (id.intValue() == entity.getMyId()) {
				DefaultMutableTreeNode positionNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 1); // position node
				DefaultMutableTreeNode xNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(positionNode, 0);
				DefaultMutableTreeNode yNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(positionNode, 1);
				systemTreeModel.removeNodeFromParent(xNode);
				systemTreeModel.removeNodeFromParent(yNode);

				DefaultMutableTreeNode nodeX = new DefaultMutableTreeNode("x: "
						+ (int) entity.getPosition().x);
				systemTreeModel.insertNodeInto(nodeX, positionNode, 0);

				DefaultMutableTreeNode nodeY = new DefaultMutableTreeNode(
						new String("y: " + (int) entity.getPosition().y));
				systemTreeModel.insertNodeInto(nodeY, positionNode, 1);
				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = root;
				elements[2] = entityNode;
				elements[3] = positionNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}
	}

	/**
	 * Update the display node of the entity
	 * 
	 * @param entity
	 *            The target entity
	 */
	public void updateDisplay(Entity entity) {
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == entity.getMyId()) {
				DefaultMutableTreeNode displayNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 0); // display node
				DefaultMutableTreeNode imNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(displayNode, 0);
				DefaultMutableTreeNode sNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(displayNode, 1);

				imNode.setUserObject(entity.getImagePath());
				sNode.setUserObject("size=(" + entity.getWidth() + ", "
						+ entity.getHeight() + ")");
				Object elements[] = new Object[4];
				elements[0] = top;
				elements[1] = root;
				elements[2] = entityNode;
				elements[3] = displayNode;
				TreePath npath = new TreePath(elements);
				appTree.fireTreeExpanded(npath);
				break;
			}
		}
	}

	/**
	 * Update the display name of the entity
	 * 
	 * @param entity
	 *            The target entity
	 */
	private void updateDisplayName(Entity entity) {
		int myId = entity.getMyId();
		Object root = systemTreeModel.getRoot();
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root);
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id.intValue() == myId) {
				entityNode.setUserObject(entity.getDisplayName());
				break;
			}
		}
	}

	public void updateCategoryProperties(Entity category) {
		_updateProperties(category, true);
	}

	public void updateProperties(Entity entity) {
		_updateProperties(entity, false);
	}

	/**
	 * Update all properties node of the entity or category
	 * 
	 * @param name
	 *            Name of the entity or category
	 * @param startWith
	 *            Whether the checking is startWith or equal
	 */
	private void _updateProperties(Entity entity, boolean startWith) {
		String name = entity.getDisplayName();
		if (name == null) 
			return ;
		Object root = systemTreeModel.getRoot(); // Top
		root = systemTreeModel.getChild(root, 1); // Entities node
		int entitiesCount = systemTreeModel.getChildCount(root); // All entities
		for (int i = 0; i < entitiesCount; i++) {
			TreeNode entityNode = (TreeNode) systemTreeModel.getChild(root, i);
			TreeNodeMetaData md = (TreeNodeMetaData) entityNode.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			String displayName = entityNode.getUserObject().toString();
			boolean pass = startWith ? displayName.startsWith(name) : 
									   id.intValue() == entity.getMyId();
			if (pass) {
				// Properties node
				DefaultMutableTreeNode propertiesNode = (DefaultMutableTreeNode) systemTreeModel
						.getChild(entityNode, 2); 
				// Remove the node and add it again first
				systemTreeModel.removeNodeFromParent(propertiesNode);
				propertiesNode = new DefaultMutableTreeNode("Fields");
				systemTreeModel.insertNodeInto(propertiesNode, entityNode, 2);
				// Add the new properties into the properties node
				DefaultMutableTreeNode nodeO = null;
				Entity temp = engineRef.system.getEntityById(id.intValue());
				List plist = ((Category) temp).getOriginalProperties();
				for (int j = 0; j < plist.size(); j++) {
					Property property = (Property) plist.get(j);
					nodeO = new DefaultMutableTreeNode(property.name + " = "
							+ property.value);
					systemTreeModel.insertNodeInto(nodeO, propertiesNode, j);
				}
				// Expand it
				if (nodeO != null) {
					Object elements[] = new Object[5];
					elements[0] = top;
					elements[1] = root;
					elements[2] = entityNode;
					elements[3] = propertiesNode;
					elements[4] = nodeO;
					TreePath npath = new TreePath(elements);
					appTree.fireTreeExpanded(npath);
				} else {
					// No configured properties
					systemTreeModel.insertNodeInto(noEntitiesNode,
							propertiesNode, 0);
				}
			}
		}
	}

	/**
	 * Tree selection changed event. Save the selected node.
	 * 
	 * @param e
	 *            The tree selection event
	 */
	public void treeSelectionChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) appTree
				.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		if (node instanceof TreeNode)
		{
			TreeNode n = (TreeNode)node;
			TreeNodeMetaData md = (TreeNodeMetaData) n.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id != null)
				selectedNodeId = id.intValue();
			else
				selectedNodeId = -1;
		}
		else {
			selectedNodeId = -1;
		}
	}

	/**
	 * Return the id of the selected tree node. It is mainly
	 * used by <code>DefineBehaviorNetworkAction</code>, either as a menu item
	 * or as a tool bar button.
	 * 
	 * @return The selected user object
	 */
	public int getSelectedId() {
		return selectedNodeId;
	}

	/**
	 * Mouse clicking event handler
	 * 
	 * @param e
	 *            The event
	 */
	public void navPanelmouseClicked(MouseEvent e) {
		TreePath path = appTree.getPathForLocation(e.getX(), e.getY());
		DefaultMutableTreeNode nodeClicked = null;
		if (path != null) {
			nodeClicked = (DefaultMutableTreeNode) path.getLastPathComponent();
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) appTree.getLastSelectedPathComponent();
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
			if (node == null)
				return;
			int entityId = -1; String entityName = null;
			try {
				if (nodeClicked.getParent() instanceof TreeNode)
				{
					TreeNode ent = (TreeNode) nodeClicked.getParent();
					entityName = ((String)ent.getUserObject()).trim();
					TreeNodeMetaData md = (TreeNodeMetaData) ent.getMetaData();
					Integer id = (Integer) md.getMetaData("id");
					if (id != null)
						entityId = id.intValue();
				}
			} 
			catch (Exception e2) {
				return;
			}
			Entity entity = null;
			if (entityId != -1)
			{
				entity = engineRef.system.getEntityById(entityId);
			}
			else if (entityName != null && entityName.equals("Entity Dynamics")) {
				try {
					if (nodeClicked.getParent().getParent() instanceof TreeNode)
					{
						TreeNode ent = (TreeNode) nodeClicked.getParent().getParent();
						entityName = ((String)ent.getUserObject()).trim();
						TreeNodeMetaData md = (TreeNodeMetaData) ent.getMetaData();
						Integer id = (Integer) md.getMetaData("id");
						if (id != null)
							entityId = id.intValue();
					}
				} catch (Exception e2) {
					return;
				}
				if (entityId != -1)
				{
					entity = engineRef.system.getEntityById(entityId);
				}
			}
			if (entity != null && node.getUserObject().equals("Entity Dynamics")) {

				if (!(entity instanceof BNCategory)) {
					MessageUtils
							.displayWarning("The entity does not have a behavior network.");
					return;
				}

				BNCategory bn = (BNCategory) entity;
				int inx = bn.getActionSelectionMechanismIndex();
				if (inx == sim.model.behavior.BehaviorNetwork.DYNAMICS) {
					// Show system dynamics editor
					PanelDialog pd = new PanelDialog(MainFrame.getInstance(),
							new sim.ui.panels.GeneralDynamicsDefinePanel((BNCategory)entity));
					pd.setTitle("Define general dynamics for entity '"
							+ entity.getDisplayName() + "'");
					pd.setSize(pd.getSize().width, pd.getSize().height + 100);
					pd.setModal(true);
					pd.show();
					pd.dispose();
				} else if (inx != sim.model.behavior.BehaviorNetwork.NODYNAMICS) {
					// Show the behavior network definition panel
					PanelDialog pd = new PanelDialog(sim.ui.MainFrame
							.getInstance(), new BehaviorNetworkDefinePanel(
							engineRef.navPanel, (BNCategory) entity));
					pd.setTitle("Define behavior network for entity '"
							+ entity.getDisplayName() + "'");
					pd.setSize(pd.getSize().width, pd.getSize().height + 100);
					pd.setModal(true);
					pd.show();
					pd.dispose();
				}
				return;

			}
		} else if (e.isMetaDown()) {
			if (nodeClicked == null) {
				return;
			}
			String nodeName = (String) nodeClicked.getUserObject();
			if (nodeName == "World") {
				popupMenuWorld.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			if (nodeName == "Application") {
				popupMenuSystem.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			if (nodeName == "Entities") {
				popupMenuPasteEntity.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			// Return if not a entity node
			if (!(nodeClicked instanceof TreeNode))
				return;
			// Locate the entity node and display the pop-up menu
			int entityId = -1;
			TreeNode ent = (TreeNode) nodeClicked;
			TreeNodeMetaData md = (TreeNodeMetaData) ent.getMetaData();
			Integer id = (Integer) md.getMetaData("id");
			if (id != null)
				entityId = id.intValue();
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity == null) {
				return;
			}
			this.entityId = entityId;
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Action event handler
	 */
	public void actionPerformed(ActionEvent e) {
		String actionString = e.getActionCommand();
		if (actionString.equals("Copy This Entity")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity != null) {
				IDofEntityToCopy = entity.getMyId();
				MessageUtils
						.displayNormal("The entity is copied successfully.");
			} else {
				MessageUtils
						.displayError("Can not get information of the entity to be copied.");
			}
		} else if (actionString.equals("Paste Entity")) {
			// The application should be defined ahead.
			try {
				if (engineRef.appManager.currentApp == null) {
					return;
				}
			} catch (Exception eee) {
				return;
			}
			Entity entity = engineRef.system.getEntityById(IDofEntityToCopy);
			if (entity != null) {
				// Create a copied entity based on the existing entity
				try {
					engineRef.createNewEntityByCopyEntity(IDofEntityToCopy);
					MessageUtils
							.displayNormal("The entity is pasted successfully.");
				} catch (Exception ex) {
					MessageUtils.displayError(ex);
				} finally {
					// Reset the id of the entity to be copied
					IDofEntityToCopy = -1;
				}
			} else {
				MessageUtils
						.displayError("Can not get information of the entity to be pasted.");
			}
		} else if (actionString.equals("Copy Dynamics")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity != null) {
				IDofEntityDynamicsToCopy = entity.getMyId();
				MessageUtils
						.displayNormal("The entity dynamics is copied successfully.");
			} else {
				MessageUtils
						.displayError("Can not get information of the entity whose dyanmics is to be copied.");
			}
		} else if (actionString.equals("Paste Dynamics")) {
			// Entity to be copied
			BNCategory entity = (BNCategory) engineRef.system
					.getEntityById(IDofEntityDynamicsToCopy);
			if (entity != null) {
				// Get the target entity
				BNCategory target = (BNCategory) engineRef.system.getEntityById(entityId);
				if (target != null) {
					// Same entity checking
					if (entity.getMyId() == target.getMyId()) {
						MessageUtils
								.displayError("Can not paste entity dynamics on the same entity.");
						return;
					}
					// Get the type of entity dynamics of the target entity and
					// the entity to be copied
					int src_inx = entity.getActionSelectionMechanismIndex();
					int dest_inx = target.getActionSelectionMechanismIndex();
					// If the target action mechanism is not general dynamics
					// and the entity dynamics of the two does not match
					// Then the entity dynamics can not be pasted.
					if (dest_inx != BehaviorNetwork.DYNAMICS
							&& src_inx != dest_inx) {
						MessageUtils
								.displayError("The two entity dynamics do not match.");
						return;
					}
					// Otherwise, perform the paste.
					int ret = JOptionPane
							.showConfirmDialog(
									MainFrame.getInstance(),
									"Do you want to replace the entity dynamics with the copy?",
									"Warning", JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.NO_OPTION) {
						return;
					}
					// Save a copy
					String dynamics = dest_inx == BehaviorNetwork.DYNAMICS ? target
							.getGeneralDynamics()
							: null;
					BehaviorNetwork copy = dest_inx != BehaviorNetwork.DYNAMICS ? target
							.getBehaviorNetwork().copy(true)
							: null;
					// Copy the entity dynamics to the target entity
					try {
						if (src_inx == BehaviorNetwork.DYNAMICS) {
							
							// Set the target action selection mechanism
							target
									.setActionSelectionMechanism(new SystemDynamicMechanism());
							// Update the current entity
							engineRef.appManager.currentApp.currentEntity = target;
							// Update the definition
							target.registerGeneralDynamics(entity
									.getGeneralDynamics());
							// Update the navigation tree
							setGeneralDynamics(target, entity
									.getGeneralDynamics() == null);
							// Set dirty
							engineRef.appManager.currentApp.setDirty(true);
							MessageUtils
									.displayNormal("General Dynamics for entity '"
											+ target.getDisplayName()
											+ "' is replaced successfully.");
							
						} else if (src_inx == BehaviorNetwork.COOPERATIVE) {
							// Set the target action selection mechanism
							target
									.setActionSelectionMechanism(new CooperativeMechanism());
							// Paste a behavior network
							BehaviorNetwork copiedBN = entity
									.getBehaviorNetwork();
							if (copiedBN != null) {
								target.getBehaviorNetwork().paste(target,
										copiedBN);
								MessageUtils
										.displayNormal("The behavior network is pasted successfully.");
								// Update behavior network in the navigation
								// tree
								updateEntityDynamics(target);
							}
						} else if (src_inx == BehaviorNetwork.MUTUAL) {
							// Set the target action selection mechanism
							target
									.setActionSelectionMechanism(new MutualInhibitionMechanism());
							// Paste a behavior network
							BehaviorNetwork copiedBN = entity
									.getBehaviorNetwork();
							if (copiedBN != null) {
								target.getBehaviorNetwork().paste(target,
										copiedBN);
								MessageUtils
										.displayNormal("The behavior network is pasted successfully.");
								// Update behavior network in the navigation
								// tree
								updateEntityDynamics(target);
							}

						}
					} catch (Exception ex) {
						// Restore the original entity dynamics?
						try {
							if (dest_inx == BehaviorNetwork.DYNAMICS) {
								// Set the target action selection mechanism
								target
										.setActionSelectionMechanism(new SystemDynamicMechanism());
								// Update the current entity
								engineRef.appManager.currentApp.currentEntity = target;
								// Update the definition
								target.registerGeneralDynamics(dynamics);
								// Update the navigation tree
								setGeneralDynamics(target, dynamics == null);
								// Set dirty
								engineRef.appManager.currentApp.setDirty(true);
							} else {
								// Restore behavior network
								if (dest_inx == BehaviorNetwork.COOPERATIVE)
									target
											.setActionSelectionMechanism(new CooperativeMechanism());
								else
									target
											.setActionSelectionMechanism(new MutualInhibitionMechanism());
								target.getBehaviorNetwork().paste(target, copy);
							}
						} catch (Exception eee) {
						}
						MessageUtils.displayError(ex);
					} finally {
						// Reset the id of the entity to be copied
						IDofEntityDynamicsToCopy = -1;
					}
				} else {
					MessageUtils
							.displayError("Can not get information of the target entity.");
					return;
				}

			} else {
				MessageUtils
						.displayError("Can not get information of the entity whose dyanmics to be pasted.");
			}
		} else if (actionString.equals("Remove Dynamics")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity == null) {
				MessageUtils
						.displayWarning("Can not locate the entity. Select the entity node first.");
				return;
			}
			if (!(entity instanceof BNCategory)) {
				return;
			}
			BNCategory c = (BNCategory) entity;
			int inx = c.getActionSelectionMechanismIndex();
			if (inx != BehaviorNetwork.NODYNAMICS) {
				int ret = JOptionPane.showConfirmDialog(
						MainFrame.getInstance(),
						"Do you want to remove dynamics from this entity?",
						"Warning", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
				if (inx != BehaviorNetwork.DYNAMICS)
				{
					c.resetBehaviorNetwork();
				}
				setNoDynamics(entity, false);
				c.setActionSelectionMechanism(new NoDynamicsMechanism());
				MessageUtils.displayNormal("Entity dynamics was removed sucessfully.");
			}
		} else if (actionString.equals("View Dynamics")) {
			// TODO
		} else if (actionString.equals("Remove This Entity")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity != null) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
						this, "Do you want to remove this entity?")) {
					parentView.getSystemEditView().removeEntity(entity);
					MessageUtils
					.displayNormal("Entity '"+entity.getDisplayName()+"' was removed sucessfully.");
				}
				
			}
		} else if (actionString.equals("Mutual Inhibition")) {
			// Show behavior network definition dialog
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity == null) {
				MessageUtils
						.displayWarning("Can not locate the entity. Select the entity node first.");
				return;
			}
			if (!(entity instanceof BNCategory)) {
				MessageUtils
						.displayWarning("The entity does not have a behavior network.");
				return;
			}
			BNCategory bn = (BNCategory) entity;
			if (entity != null) {
				int inx = bn.getActionSelectionMechanismIndex();
				if (inx == BehaviorNetwork.COOPERATIVE) {
					MessageUtils
							.displayWarning("Can not change from 'Vector Sum' to 'Mutual inhibition'.");
					return;
				}
				if (inx != BehaviorNetwork.MUTUAL) {
					int ret = JOptionPane
							.showConfirmDialog(
									MainFrame.getInstance(),
									"Do you want to change the entity dynamics from 'General Dynamics' to 'Mutual inhibition'?",
									"Warning", JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.NO_OPTION) {
						return;
					}
					setGeneralDynamics(entity, true);
					bn
							.setActionSelectionMechanism(new MutualInhibitionMechanism());
				}
				PanelDialog pd = new PanelDialog(MainFrame.getInstance(),
						new BehaviorNetworkDefinePanel(this,
								(BNCategory) entity));
				pd.setTitle("Define behavior network for entity '"
						+ entity.getDisplayName() + "'");
				pd.setSize(pd.getSize().width, pd.getSize().height + 100);
				pd.setModal(true);
				pd.show();
				pd.dispose();
			}
		} else if (actionString.equals("Vector Sum")) {

			// Show behavior network definition dialog
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity == null) {
				MessageUtils
						.displayWarning("Can not locate the entity. Select the entity node first.");
				return;
			}
			if (!(entity instanceof BNCategory)) {
				MessageUtils
						.displayWarning("Can not setup dyanmics for the entity.");
				return;
			}
			BNCategory bn = (BNCategory) entity;
			if (entity != null) {
				int inx = bn.getActionSelectionMechanismIndex();
				if (inx == BehaviorNetwork.MUTUAL) {
					MessageUtils
							.displayWarning("Can not change from 'Mutual inhibition' to 'Vector Sum'.");
					return;
				}
				if (inx != BehaviorNetwork.COOPERATIVE) {
					int ret = JOptionPane
							.showConfirmDialog(
									MainFrame.getInstance(),
									"Do you want to change the entity dynamics from 'General Dynamics' to 'Vector Sum'?",
									"Warning", JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.NO_OPTION) {
						return;
					}
					setGeneralDynamics(entity, true);
					bn.setActionSelectionMechanism(new CooperativeMechanism());
				}
				PanelDialog pd = new PanelDialog(MainFrame.getInstance(),
						new BehaviorNetworkDefinePanel(this,
								(BNCategory) entity));
				pd.setTitle("Define behavior network for entity '"
						+ entity.getDisplayName() + "'");
				pd.setSize(pd.getSize().width, pd.getSize().height + 100);
				pd.setModal(true);
				pd.show();
				pd.dispose();
			}
		} else if (actionString.equals("General Dynamics")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			if (entity == null) {
				MessageUtils
						.displayWarning("Can not locate the entity. Select the entity node first.");
				return;
			}
			if (!(entity instanceof BNCategory)) {
				MessageUtils
						.displayWarning("Can not setup dyanmics for the entity.");
				return;
			}
			BNCategory c = (BNCategory) entity;
			int inx = c.getActionSelectionMechanismIndex();
			if (inx != BehaviorNetwork.DYNAMICS) {
				int ret = JOptionPane
						.showConfirmDialog(
								MainFrame.getInstance(),
								"Do you want to change the entity dynamics from Behavior Network to 'General Dynamics'?",
								"Warning", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
				setGeneralDynamics(entity, false);
				c.setActionSelectionMechanism(new SystemDynamicMechanism());
			}
			// Show system dynamics editor-
			PanelDialog pd = new PanelDialog(MainFrame.getInstance(),
					new sim.ui.panels.GeneralDynamicsDefinePanel(c));
			pd.setTitle("Define general dynamics for entity '"
					+ entity.getDisplayName() + "'");
			pd.setSize(pd.getSize().width, pd.getSize().height + 100);
			pd.setModal(true);
			pd.show();
			pd.dispose();
		} else if (actionString.equals("Edit Position...")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			Point p = entity.getPosition();
			EditEntityPositionDlg editPositionDlg = new EditEntityPositionDlg(
					this, "Edit " + entity.getDisplayName() + " position",
					(int) p.x, (int) p.y);
			editPositionDlg.setModal(true);
			editPositionDlg.show();
			if (((int) p.x != editPositionDlg.xCoordinate)
					|| ((int) p.y != editPositionDlg.yCoordinate)) {
				/** Update the parent view */
				parentView.updateEntityPosition(entity,
						editPositionDlg.xCoordinate,
						editPositionDlg.yCoordinate);
				/** Mark the application as dirty */
				AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);
				MessageUtils.displayNormal("Entity position was updated sucessfully.");
			}

		} else if (actionString.equals("Edit Fields...")) {
			Category entity = (Category) engineRef.system.getEntityById(entityId);
			List allProps = entity.getAllProperties();
			if (allProps.isEmpty()) {
				MessageUtils.displayWarning("No fields exist.");
				return;
			}
			EditPropertiesDialog dlg = new EditPropertiesDialog(allProps,
					entity.getOriginalProperties());
			dlg.setModal(true);
			dlg.show();
			if (dlg.isChanged()) {
				/** Mark the application as dirty */
				AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);
				List newProps = dlg.getProps();
				for (int i = 0; i < newProps.size(); i++) {
					Property p = (Property) newProps.get(i);
					try {
						// Set the new value for the property
						entity.setValue(p.name, p.value);
						// Set the new initial value for the property
						entity.updatePropertyInitial(p.name, p.value);
					} catch (Exception ee) {
						;
					}
				}
				MessageUtils.displayNormal("The fields were updated sucessfully.");
				// Update the navigation tree
				updateProperties(entity);
				return;
			}
		} else if (actionString.equals("Edit World...")) {
			// The application should be defined ahead.
			try {
				if (engineRef.appManager.currentApp == null) {
					return;
				}
			} catch (Exception eee) {
				return;
			}
			// Edit the world properties
			EditWorldDialog dlg = new EditWorldDialog();
			dlg.setModal(true);
			dlg.show();
			if (dlg.isChanged()) {
				/** Mark the application as dirty */
				AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);
				// Update entities position information
				engineRef.resizeAndRepositionEntities(dlg.getWidthRatio(), dlg
						.getHeightRatio());
				// Update the configuration parameters
				engineRef.system.systemParameters.save(
						ConfigParameters.ENV_TYPE,
						new Integer(dlg.getEnvType()));
				// Update simulation panel
				parentView.updateSystemEditorView();
				// Update tree
				updateSimulationWorld();
				MessageUtils.displayNormal("Simulation world was updated sucessfully.");
			}
		} else if (actionString.equals("Edit Display...")) {
			Entity entity = engineRef.system.getEntityById(entityId);
			EditDisplayDialog dlg = new EditDisplayDialog(entity);
			dlg.setModal(true);
			dlg.show();
			if (dlg.isChanged()) {
				/** Mark the application as dirty */
				AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);
				// Change size of the entity
				// Update the simulation world
				// Update the navigation tree
				// Change picture of the entity
				// Update the simulation world
				// Update the navigation tree
				// Not change the category icon
				// Update the simulation world
				parentView.updateSystemEditorView();
				// Update the navigation tree
				updateDisplay(entity);
				// Update the display name in the navigation tree
				updateDisplayName(entity);
				// Update the display name in the system
				engineRef.system.updateEntityDisplayName(entity.getMyId(),
						entity.getDisplayName());
				// Set message
				MessageUtils.displayNormal("Display of entity '"
						+ entity.getDisplayName() + "' is updated.");

			}
		}
	}
}
