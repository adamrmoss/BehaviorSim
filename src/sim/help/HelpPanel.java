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

package sim.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import sim.ui.tree.AppEvent;
import sim.ui.tree.BasePanel;
import sim.ui.tree.ClickEvent;
import sim.ui.tree.ClickListener;
import sim.ui.tree.MetaData;
import sim.ui.tree.TreeNode;
import sim.ui.tree.TreeNodeMetaData;
import sim.ui.tree.TreeUpdateListener;

/**
 * The panel to show system help documents.
 * 
 * @author Fasheng Qiu
 * 
 */
public class HelpPanel extends BasePanel implements TreeUpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3818875970880013392L;

	/** Current selected panel */
	private int currentPanelID = DataSourceHelper.OVERVIEW;

	/** Default cell renderer */
	private HighlightCellRenderer highlightRender = new HighlightCellRenderer();

	/** Panels */
	public final static BaseHelpPanel OVERVIEW_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.OVERVIEW)));
	public final static BaseHelpPanel CONCEPTS_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CONCEPTS)));
	public final static BaseHelpPanel APP_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.APP)));
	public final static BaseHelpPanel MODELFILE_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.MODELFILE)));
	public final static BaseHelpPanel STARTED_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.STARTED)));
	public final static BaseHelpPanel STEPS_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.STEPS)));
	public final static BaseHelpPanel CREATEAPP_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CREATEAPP)));
	public final static BaseHelpPanel NEWAPP_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.NEWAPP)));
	public final static BaseHelpPanel OLDAPP_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.OLDAPP)));
	public final static BaseHelpPanel CREATECATEGORY_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CREATECATEGORY)));
	public final static BaseHelpPanel CATEGORYBASIC_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CATEGORYBASIC)));
	public final static BaseHelpPanel CATEGORYMETHOD_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CATEGORYMETHOD)));
	public final static BaseHelpPanel CATEGORYBASICCOPYPASTE_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CATEGORYBASICCOPYPASTE)));
	public final static BaseHelpPanel CREATEENTITY_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CREATEENTITY)));
	public final static BaseHelpPanel NEWENTITY_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.NEWENTITY)));
	public final static BaseHelpPanel UPDATEENTITY_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.UPDATEENTITY)));
	public final static BaseHelpPanel CREATEDYNAMICS_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CREATEDYNAMICS)));
	public final static BaseHelpPanel CREATEGENERAL_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CREATEGENERAL)));
	public final static BaseHelpPanel CREATEBN_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.CREATEBN)));
	public final static BaseHelpPanel NEWBEHAVIOR_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.NEWBEHAVIOR)));
	public final static BaseHelpPanel NEWBEHAVIORCOPY_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.NEWBEHAIVORBYCOPY)));
	public final static BaseHelpPanel BEHAVIORTASKQUEUE_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.BEHAIVORTASKQUEUE)));
	public final static BaseHelpPanel COEFF_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.COEFF)));
	public final static BaseHelpPanel SIMULATION_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.SIMULATION)));
	public final static BaseHelpPanel APIS_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.APIS)));
	public final static BaseHelpPanel APIGUI_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.APIGUI)));
	public final static BaseHelpPanel METHODGUI_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.METHODGUI)));
	public final static BaseHelpPanel PRACTICE_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.PRACTICE)));
	public final static BaseHelpPanel FAQ_PANEL = new BaseHelpPanel(
			(String) DataSourceHelper.dataMap.get(new Integer(
					DataSourceHelper.FAQ)));

	/** Setup id -> panel mapping */
	public final static Map panelMap = new HashMap();
	static {
		panelMap.put(new Integer(DataSourceHelper.NONE), new BaseHelpPanel(""));
		panelMap.put(new Integer(DataSourceHelper.OVERVIEW), OVERVIEW_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CONCEPTS), CONCEPTS_PANEL);
		panelMap.put(new Integer(DataSourceHelper.APP), APP_PANEL);
		panelMap.put(new Integer(DataSourceHelper.MODELFILE), MODELFILE_PANEL);
		panelMap.put(new Integer(DataSourceHelper.STARTED), STARTED_PANEL);
		panelMap.put(new Integer(DataSourceHelper.STEPS), STEPS_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CREATEAPP), CREATEAPP_PANEL);
		panelMap.put(new Integer(DataSourceHelper.NEWAPP), NEWAPP_PANEL);
		panelMap.put(new Integer(DataSourceHelper.OLDAPP), OLDAPP_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CREATECATEGORY),
				CREATECATEGORY_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CATEGORYBASIC),
				CATEGORYBASIC_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CATEGORYMETHOD),
				CATEGORYMETHOD_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CATEGORYBASICCOPYPASTE),
				CATEGORYBASICCOPYPASTE_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CREATEENTITY),
				CREATEENTITY_PANEL);
		panelMap.put(new Integer(DataSourceHelper.NEWENTITY), NEWENTITY_PANEL);
		panelMap.put(new Integer(DataSourceHelper.UPDATEENTITY),
				UPDATEENTITY_PANEL);

		panelMap.put(new Integer(DataSourceHelper.CREATEDYNAMICS),
				CREATEDYNAMICS_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CREATEGENERAL),
				CREATEGENERAL_PANEL);
		panelMap.put(new Integer(DataSourceHelper.CREATEBN), CREATEBN_PANEL);
		panelMap.put(new Integer(DataSourceHelper.NEWBEHAVIOR),
				NEWBEHAVIOR_PANEL);
		panelMap.put(new Integer(DataSourceHelper.NEWBEHAIVORBYCOPY),
				NEWBEHAVIORCOPY_PANEL);
		panelMap.put(new Integer(DataSourceHelper.BEHAIVORTASKQUEUE),
				BEHAVIORTASKQUEUE_PANEL);
		panelMap.put(new Integer(DataSourceHelper.COEFF), COEFF_PANEL);
		panelMap
				.put(new Integer(DataSourceHelper.SIMULATION), SIMULATION_PANEL);
		panelMap.put(new Integer(DataSourceHelper.APIS), APIS_PANEL);
		panelMap.put(new Integer(DataSourceHelper.APIGUI), APIGUI_PANEL);
		panelMap.put(new Integer(DataSourceHelper.METHODGUI), METHODGUI_PANEL);
		panelMap.put(new Integer(DataSourceHelper.PRACTICE), PRACTICE_PANEL);
		panelMap.put(new Integer(DataSourceHelper.FAQ), FAQ_PANEL);
	}

	/** The click event listener */
	private static ClickListener cl = null;

	/** Document listener */
	private static DataSourceHelper listener = null;

	/**
	 * Constructor
	 */
	public HelpPanel() {
		super();

		/** Setup listener */
		listener = new DataSourceHelper(this);

		/** Setup panels */
		GradientSearchPanel search = new GradientSearchPanel();
		search.addTitleComponent(new SearchPanel(this));
		super.add(search, BorderLayout.NORTH);

		/** Set up menu items */
		navigatorPanel.getGradientTitlePanel().setTitleText("");
		navigatorPanel.getGradientTitlePanel().addTitleComponent(
				HelpPanelHepler.getTopicsMenuItem(listener));
		propertyPanel.getGradientTitlePanel().addTitleComponent(
				HelpPanelHepler.getTopicMenuItem(listener));
		// Setup the panel
		propertyPanel.setCenterComponent((JPanel) panelMap.get(new Integer(
				DataSourceHelper.OVERVIEW)));
		// default renderer
		tree.setCellRenderer(highlightRender);
		// display
		displayAtTree(DataSourceHelper.OVERVIEW);
		// tree
		tree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	/**
	 * Search text in the topics
	 * 
	 * @param text
	 *            Text to search
	 */
	public void searchText(String text) {
		listener.searchText(text);
	}

	/**
	 * Maximum/minized the size of left tree panel
	 */
	private boolean lexpanded = true;

	public void expandLeftTree() {
		HelpFrame helpf = HelpFrame.getInstance(null, null);
		int width = helpf.getSize().width;
		if (lexpanded)
			treeSplitPane.setDividerLocation(width - 10);
		else
			treeSplitPane.setDividerLocation(10);
		lexpanded = !lexpanded;
		HelpPanelHepler.switchExpand(true, lexpanded);
	}

	/**
	 * Maximum/minized the size of right tree panel
	 */
	private boolean rexpanded = true;

	public void expandRightTree() {
		HelpFrame helpf = HelpFrame.getInstance(null, null);
		int width = helpf.getSize().width;
		if (rexpanded)
			treeSplitPane.setDividerLocation(10);
		else
			treeSplitPane.setDividerLocation(width - 10);
		rexpanded = !rexpanded;
		HelpPanelHepler.switchExpand(false, rexpanded);
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
			// Save
			currentPanelID = type;
			// Setup the panel
			propertyPanel.setCenterComponent((JPanel) panelMap.get(new Integer(
					type)));

		}
	}

	/**
	 * The tree is needed to rebuilt, since categories are changed
	 */
	public void treeChanged() {
		throw new RuntimeException("Not supported yet.");
	}

	/**
	 * The sub tree is needed to rebuilt, since categories are changed
	 */
	public void subTreeChanged() {
		throw new RuntimeException("Not supported yet.");
	}

	/**
	 * Build the help tree
	 */
	protected void buildTree() {
		/** Tree root and model */
		// Tree root
		TreeNode top = new TreeNode("BehaviorSim v1.0 User's Guide");
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
		cl = new MyClickEventListener();

		// Set up the click event listener for the tree root
		MetaData md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.NONE));
		top.setMetaData(md);
		top.addClickListener(cl);

		// Node index
		int nodeInx = 0;

		// Overview help node
		TreeNode overviewNode = new TreeNode("Overview");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.OVERVIEW));
		overviewNode.setMetaData(md);
		overviewNode.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(overviewNode, top, nodeInx++);

		// Concepts
		TreeNode appNode = new TreeNode("Concepts");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CONCEPTS));
		appNode.setMetaData(md);
		appNode.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(appNode, top, nodeInx++);

		// Concepts
		TreeNode app = new TreeNode("Application");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.APP));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, appNode, 0);

		// Concepts
		app = new TreeNode("Model File");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.MODELFILE));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, appNode, 1);

		// Getting started help node
		TreeNode helpNode = new TreeNode("Getting Started");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.STARTED));
		helpNode.setMetaData(md);
		helpNode.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(helpNode, top, nodeInx++);

		app = new TreeNode("Major steps in application development");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.STEPS));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, helpNode, 0);

		app = new TreeNode("Creating an application");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CREATEAPP));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, helpNode, 1);

		TreeNode sub = new TreeNode("Create a new application");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.NEWAPP));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 0);

		sub = new TreeNode("Open an existing application");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.OLDAPP));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 1);

		app = new TreeNode("Creating a category");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CREATECATEGORY));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, helpNode, 2);

		sub = new TreeNode("Create category basic information");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CATEGORYBASIC));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 0);

		sub = new TreeNode("Create a new method");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CATEGORYMETHOD));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 1);

		sub = new TreeNode("Create category by copy-and-paste");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(
				DataSourceHelper.CATEGORYBASICCOPYPASTE));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 2);

		app = new TreeNode("Creating an entity");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CREATEENTITY));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, helpNode, 3);

		sub = new TreeNode("Creating a new entity");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.NEWENTITY));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 0);

		sub = new TreeNode("Update entity properties");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.UPDATEENTITY));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 1);

		app = new TreeNode("Creating entity dynamics");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CREATEDYNAMICS));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, helpNode, 4);

		sub = new TreeNode("Creating general dynamics");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CREATEGENERAL));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 0);

		sub = new TreeNode("Creating behavior network");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.CREATEBN));
		sub.setMetaData(md);
		sub.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub, app, 1);

		TreeNode sub2 = new TreeNode("Creating a new behavior");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.NEWBEHAVIOR));
		sub2.setMetaData(md);
		sub2.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub2, sub, 0);

		sub2 = new TreeNode("Creating behavior by copy-and-paste");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.NEWBEHAIVORBYCOPY));
		sub2.setMetaData(md);
		sub2.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub2, sub, 1);

		sub2 = new TreeNode("Specifying behavior action through task queue");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.BEHAIVORTASKQUEUE));
		sub2.setMetaData(md);
		sub2.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub2, sub, 2);

		sub2 = new TreeNode("Specify coefficients/weights");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.COEFF));
		sub2.setMetaData(md);
		sub2.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(sub2, sub, 3);

		app = new TreeNode("Starting simulation");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.SIMULATION));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, helpNode, 5);

		// Entity help node
		TreeNode node = new TreeNode("Using system APIs");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.APIS));
		node.setMetaData(md);
		node.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(node, top, nodeInx++);

		app = new TreeNode("APT details");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.APIGUI));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, node, 0);

		app = new TreeNode("Using method editor");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.METHODGUI));
		app.setMetaData(md);
		app.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(app, node, 1);

		// Behavior network help node
		node = new TreeNode("Best Practices");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.PRACTICE));
		node.setMetaData(md);
		node.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(node, top, nodeInx++);

		// Simulation help node
		node = new TreeNode("FAQ");
		// Construct a meta data model (node type) for that node
		md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(DataSourceHelper.FAQ));
		node.setMetaData(md);
		node.addClickListener(cl);
		// Insert the node into the correct position
		defaultModel.insertNodeInto(node, top, nodeInx++);

		// Expand the root tree node
		super.expand(top);
	}

	/**
	 * Display the topic with the specific id in the left tree
	 * 
	 * @param id
	 *            Id of the page to be displayed at the left tree
	 */
	public void displayAtTree(int id) {
		// Locate the selected node
		TreeNode node = visitAllNodes(tree, id);
		if (node != null) {
			// Un-expand all nodes ahead
			expandAll(tree, false);
			// Expand the path to the selected node
			TreePath path = new TreePath(((DefaultTreeModel) tree.getModel())
					.getPathToRoot(node));
			tree.fireTreeExpanded(path);

		}
	}

	/**
	 * If expand is true, expands all nodes in the tree. Otherwise, collapses
	 * all nodes in the tree.
	 * 
	 * @param tree
	 * @param expand
	 */
	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}

	}

	class HighlightCellRenderer extends DefaultTreeCellRenderer implements
			TreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 257211155873959733L;

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			TreeNode node = (TreeNode) value;
			JLabel helper = new JLabel(String.valueOf(value));
			if (node == current) {
				Font font = new Font("Times New Roman", Font.BOLD, 12);
				helper.setFont(font);
				helper.setForeground(Color.BLUE);
			}
			return helper;
		}
	}

	/**
	 * Traverse the tree and return the TreeNode whose type is of the specified
	 * id.
	 * 
	 * @param tree
	 *            Tree to traverse
	 * @param id
	 *            Page id
	 * @return The tree node. It would be null if no page is found.
	 */
	private TreeNode current;

	public TreeNode visitAllNodes(JTree tree, int id) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		visitAllNodes(root, id);
		return current;
	}

	private void visitAllNodes(TreeNode node, int id) {
		// Check the meta data
		MetaData md = node.getMetaData();
		// The node type
		int type = ((Integer) md.getMetaData("type")).intValue();
		if (type == id) {
			current = node;
			return;
		}

		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				visitAllNodes(n, id);
			}
		}
	}

	/**
	 * @return the currentPanelID
	 */
	public int getCurrentPanelID() {
		return currentPanelID;
	}

	/**
	 * @param currentPanelID
	 *            the currentPanelID to set
	 */
	public void setCurrentPanelID(int currentPanelID) {
		this.currentPanelID = currentPanelID;
		this.propertyPanel.setCenterComponent((JPanel) panelMap
				.get(new Integer(currentPanelID)));
	}

}
