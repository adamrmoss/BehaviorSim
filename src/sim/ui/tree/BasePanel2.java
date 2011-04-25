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

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import sim.util.GUIUtils;
import sim.util.MessageUtils;

/**
 * Main class that handles the display of two trees and displays the correct
 * panel in the middle part of the GUI to adjust the values.
 * 
 * It is used as a base panel of other application related panel to make the
 * tree package easier to use.
 * 
 * @author Fasheng Qiu
 * @since 10/19/2007 10:49PM
 */
public abstract class BasePanel2 extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * User object associated with this panel, it would be the entity to be
	 * edited
	 */
	protected Object userObject;

	/* The left top tree control (main tree) */
	protected JTree tree;

	/* The left bottom tree control (subtree) */
	protected JTree subTree;

	/* The pop up node for main tree */
	protected TreeNode popupNode;

	/* The pop up node for subtree */
	protected TreeNode subPopupNode;

	/* The split pane which contains two controls, 'main tree' and 'sub tree' */
	private JSplitPane treeControlSplitPane;

	/* The split pane which contains two controls, 'Tree' and 'Property editor' */
	private JSplitPane treeSplitPane;

	/* Titles of the two controls */
	private BorderTitlePanel navigatorPanel;
	protected BorderTitlePanel propertyPanel;

	/* Border */
	private static final EmptyBorder SCROLLPANEBORDER = new EmptyBorder(0, 1,
			0, 0);

	/**
	 * Creates a new BasePanel.
	 * 
	 * The top level node is expanded by default.
	 * 
	 */
	public BasePanel2() {
		this(null);
	}

	public BasePanel2(Object userObject) {
		super(new BorderLayout());
		// Save user object
		this.userObject = userObject;
		// build the tree first
		buildTree();
		buildSubTree();
		if (tree == null || subTree == null) {
			MessageUtils.displayError("The tree controls should be setted up.");
			return;
		}

		/* Set the properties of the tree */
		tree.setBorder(new EmptyBorder(0, 0, 0, 0));
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setSelectionModel(selectionModel);

		subTree.setBorder(LineBorder.createBlackLineBorder());
		selectionModel = new DefaultTreeSelectionModel();
		selectionModel
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		subTree.setSelectionModel(selectionModel);

		/* Set the tree renderer */
		DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
		treeRenderer.setOpenIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_NODE_EXPAND_IMAGE));
		treeRenderer.setClosedIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_NODE_UNEXPAND_IMAGE));
		treeRenderer.setLeafIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_LEAF_IMAGE));
		tree.setCellRenderer(treeRenderer);

		treeRenderer = new DefaultTreeCellRenderer();
		treeRenderer.setOpenIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_NODE_EXPAND_IMAGE));
		treeRenderer.setClosedIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_NODE_UNEXPAND_IMAGE));
		treeRenderer.setLeafIcon(GUIUtils
				.getImageIconForObject(GUIUtils.TREE_LEAF_IMAGE));
		subTree.setCellRenderer(treeRenderer);

		/* Show root handle */
		tree.setShowsRootHandles(true);
		subTree.setShowsRootHandles(true);

		/*
		 * The tree pane, including two sub-panes, "Tree Pane" and
		 * "Property Pane"
		 */
		treeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		treeSplitPane.setContinuousLayout(false);

		/* The tree pane, including two sub-panes, "Main tree" and "Sub tree" */
		treeControlSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		treeControlSplitPane.setContinuousLayout(false);

		/* Scroll pane for the tree control */
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		JScrollPane scrollPaneForSubTree = new JScrollPane(subTree);
		scrollPaneForSubTree.setBorder(new EmptyBorder(0, 0, 0, 0));

		/* The title ('A title panel') for the tree control */
		navigatorPanel = new BorderTitlePanel();
		navigatorPanel.getGradientTitlePanel().setGradientColors(
				sim.util.GUIUtils.getGradientTitlePanelColors());
		navigatorPanel.setBorder(new FlatBorder());
		navigatorPanel.getGradientTitlePanel().setTitleText(
				"Click or right-click the root to continue.");
		navigatorPanel.setCenterComponent(treeControlSplitPane);

		/*
		 * The title for the property control, which is used to edit the
		 * properties of the selected node
		 */
		propertyPanel = new BorderTitlePanel();
		propertyPanel.getGradientTitlePanel().setGradientColors(
				sim.util.GUIUtils.getGradientTitlePanelColors());
		propertyPanel.setBorder(new FlatBorder());

		/*
		 * Set the tree as the left top control and the sub tree as the bottom
		 * control
		 */
		treeControlSplitPane.setTopComponent(scrollPane);
		treeControlSplitPane.setBottomComponent(scrollPaneForSubTree);
		treeControlSplitPane.setContinuousLayout(true);
		treeControlSplitPane.setDividerLocation(350); // The initial width of
		// the tree

		/*
		 * Set the tree as the left control and the property panel as the right
		 * control
		 */
		treeSplitPane.setTopComponent(navigatorPanel);
		treeSplitPane.setBottomComponent(propertyPanel);
		treeSplitPane.setDividerLocation(220); // The initial width of the tree

		/* Add the tree selection listener */
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				treeSelectionChanged(tree);
			}
		});
		subTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				treeSelectionChanged(subTree);
			}
		});

		/* Add the mouse listener */
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupForNode(tree, e.getX(), e.getY());
				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupForNode(tree, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupForNode(tree, e.getX(), e.getY());
				}
			}
		});
		subTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupForNode(subTree, e.getX(), e.getY());
				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupForNode(subTree, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupForNode(subTree, e.getX(), e.getY());
				}
			}
		});

		/* Add the panel of the tree and the property editor */
		add(treeSplitPane, BorderLayout.CENTER);

	}

	/**
	 * Expand the specified tree node
	 */
	protected void expand(TreeNode node, JTree tree) {
		javax.swing.tree.TreeNode[] nodes = ((DefaultTreeModel) tree.getModel())
				.getPathToRoot(node);
		tree.expandPath(new TreePath(nodes));
	}

	/**
	 * Build the tree control, including all tree nodes and their relations, the
	 * meta data of each node, setting up click and pop up listeners if
	 * necessary
	 * 
	 */
	protected abstract void buildTree();

	/**
	 * Build the sub tree control, including all tree nodes and their relations,
	 * the meta data of each node, setting up click and pop up listeners if
	 * necessary
	 * 
	 */
	protected abstract void buildSubTree();

	/**
	 * Invoked when the tree has changed the selected node and therefore the
	 * property panel is needed to be adjusted to reflect this change. For
	 * security reasons, it first removes all panels, and then adds the correct
	 * ones to the GUI again.
	 * 
	 * The user should specify how to construct the panel for each type of node,
	 * since the panel for each type of node is different.
	 */
	private void treeSelectionChanged(JTree tree) {

		/* The tree path of the selected node */
		TreePath treePath = tree.getSelectionPath();

		/* Clear the property panel first */
		cleanPropertyPanel();

		/* Reset the selected node */
		TreeNode selectedNode = null;

		/* Position the selected node */
		if (treePath != null) {

			Object lpc = treePath.getLastPathComponent();
			selectedNode = (TreeNode) lpc;

			/* Cast the click event of this tree node */
			if (selectedNode != null)
				selectedNode.multicastClickEvent(new ClickEvent(selectedNode));
		}
	}

	/**
	 * Creates a JScrollPane suited to be displayed in the propertyPanel. It has
	 * no Border, and sets the UnitIncrement of its JScrollBars to 10 pixels.
	 * 
	 * @param contentPanel
	 *            the panel to create a JScrollPane for
	 * @return the created JScrollPane
	 * @see JScrollBar#setUnitIncrement(int)
	 */
	protected JScrollPane createContentScrollPane(JPanel contentPanel) {
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(SCROLLPANEBORDER);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		return scrollPane;
	}

	/**
	 * Ensures to remove all components from the PropertyPanel (the one in the
	 * right of the GUI).
	 */
	protected void cleanPropertyPanel() {
		propertyPanel.setCenterComponent(new JPanel());
		propertyPanel.getGradientTitlePanel().setTitleText(" ");
		propertyPanel.getGradientTitlePanel().setDescriptionText(" ");
		propertyPanel.getGradientTitlePanel().setIcon(null);
	}

	/**
	 * Shows a suitable PopupMenu for the currently selected node, if necessary.
	 * 
	 * @param x
	 *            the x coordinate of the click in tree local coordinates
	 * @param y
	 *            the y coordinate of the click in tree local coordinates
	 */
	private void showPopupForNode(JTree tree, int x, int y) {
		/* Path of the location */
		TreePath treePath = tree.getPathForLocation(x, y);
		if (treePath == null) {
			return;
		}

		/* Position the selected tree node */
		final Object treeNode = treePath.getLastPathComponent();
		TreeNode tn = (TreeNode) treeNode;

		/* Save the pop up node */
		popupNode = tn;

		tn.multicastPopupEvent(new PopupEvent(tn, x, y));

	}
}
