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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import sim.core.AppEngine;
import sim.model.entity.BNCategory;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Property;
import sim.ui.tree.AppEvent;
import sim.ui.tree.BasePanel;
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
 * The category definition panel.
 * 
 * The panel contains two controls: Left-side tree control and right-side
 * property editor.
 * 
 * @author Fasheng Qiu
 * @since 10/20/2007
 * 
 */
public class CategoryDefinePanel extends BasePanel implements
		TreeUpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6345742239659858515L;

	/* Application engine */
	protected static AppEngine engine = AppEngine.getInstance();

	/** The type of different tree nodes */
	private static final int CATEGORIES_NODE = -1;
	private static final int CATEGORY_NODE = 0;
	private static final int PROPERTIES_NODE = 1;
	private static final int METHODS_NODE = 2;
	private static final int PROPERTY_NODE = 4;
	private static final int METHOD_NODE = 5;

	/** The empty node */
	private static final TreeNode noCategoriesNode = new TreeNode(
			"No categories.");
	private static final TreeNode noPropertiesNode = new TreeNode("No fields.");
	private static final TreeNode noMethodsNode = new TreeNode("No methods.");
	
	/** Panels */
	private CategoryInformationPanel categoryInformationPanel = new CategoryInformationPanel(
			this);
	private JScrollPane categoryInformationPane = createContentScrollPane(categoryInformationPanel);
	private CategoryMethodPanel categoryMethodPanel = new CategoryMethodPanel(
			categoryInformationPanel);

	/** Pop up menus */
	private JPopupMenu popupMenu1 = null;
	private JPopupMenu popupMenu2 = null;
	private JPopupMenu popupMenu3 = null;
	private JPopupMenu popupMenu4 = null;
	private JPopupMenu popupMenu5 = null;
	private JPopupMenu popupMenu6 = null;

	/** The click event listener */
	private static ClickListener cl = null;

	/** The pop up event listener */
	private static PopupListener pl = null;

	/**
	 * Constructor
	 */
	public CategoryDefinePanel() {
		super();

		/** Setup tree update listener */
		categoryInformationPanel.setTreeUpdateListener(this);
		categoryMethodPanel.setTreeUpdateListener(this);

		/** Setup pop up menus */
		popupMenu1 = new JPopupMenu("Categories");
		JMenuItem pastItem = popupMenu1.add("Paste");
		pastItem
				.setToolTipText("Create a category based on the copied category.");
		JMenuItem newItem = popupMenu1.add("Create New Category");
		newItem.setToolTipText("Create a new category");
		JMenuItem deleteAllItem = popupMenu1.add("Remove All Categories");
		deleteAllItem
				.setToolTipText("Delete all categories from the application.");
		/** Add actions to each item - Delete all created category */
		deleteAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Ask for confirmation
				int ret = JOptionPane.showConfirmDialog(
						CategoryDefinePanel.this,
						"Do you want to remove all categories?",
						"Confirmation", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					// Remove this category
					if (deleteCategory(null)) {
						MessageUtils
								.displayNormal("All categories are removed from the application.");
					} else {
						MessageUtils
								.displayNormal("Categories are not removed from the application.");
					}
				}
			}
		});
		/** Add actions to each item */
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propertyPanel.setCenterComponent(categoryInformationPane);
				propertyPanel.getGradientTitlePanel().setTitleText(
						"Create a new category");
				propertyPanel.getGradientTitlePanel().setIcon(
						popupNode.getImageIcon());
				// Clear the panel
				categoryInformationPanel.setCategory(null);
			}
		});
		pastItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Check the copy
				if (engine.appManager.currentApp.copiedCategory == null) {
					MessageUtils.displayError("No category has been copied.");
					return;
				}
				// Copied category
				Category copy = engine.appManager.currentApp.copiedCategory;
				// Save the class definition
				try {
					AppEngine.getInstance().appManager.currentApp.dm
							.saveCopiedCategory(copy);
					treeChanged();
				} catch (Exception ex) {
					MessageUtils.displayError(ex);
					return;
				}
				try {
					// Update the category to the system editor
					engine.categoryUpdateListener.categoryAdded(copy);
				} catch (Exception exxx) {
				}

			}
		});

		popupMenu5 = new JPopupMenu("Categories");
		JMenuItem newItem2 = popupMenu5.add("Create New Category");
		newItem2.setToolTipText("Create a new category");
		newItem2.addActionListener(newItem.getActionListeners()[0]);

		popupMenu2 = new JPopupMenu("Others");
		JMenuItem copyItem = popupMenu2.add("Copy");
		copyItem.setToolTipText("Make a copy of this entity.");
		JMenuItem deleteItem = popupMenu2.add("Remove This Category");
		deleteItem.setToolTipText("Delete this category from the application.");
		/** Add actions to each item - Delete the specified category */
		deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Category name
				String catName = (String) md.getMetaData("catName");
				// Check category name
				if (catName == null) {
					MessageUtils
							.displayError("Can not locate the category information.");
					return;
				}
				// Ask for confirmation
				int ret = JOptionPane
						.showConfirmDialog(CategoryDefinePanel.this,
								"Do you want to remove the category '"
										+ catName + "'?", "Confirmation",
								JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					// Remove this category
					if (deleteCategory(catName)) {
						MessageUtils.displayNormal("The category '" + catName
								+ "' is removed from the application.");
					} else {
						MessageUtils.displayNormal("The category '" + catName
								+ "' is not removed from the application.");
					}
				}
			}
		});
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Category name
				String catName = (String) md.getMetaData("catName");
				// Get the category object
				Category category = getCategory(catName);
				// Check the category to copy
				if (category == null) {
					MessageUtils
							.displayError("Can not obtain the category object.");
					return;
				}

				// Create a temporary copied category and save in the current
				// active application
				try {
					Category copy = AppEngine.getInstance().appManager.currentApp.dm
							.createANewCategoryByCopy(category);
					AppEngine.getInstance().appManager.currentApp.copiedCategory = copy;
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageUtils.displayError(ex);
					return;
				}
				MessageUtils.displayNormal("A copy of the category '" + catName
						+ "' has been made.");

			}
		});

		/** For methods node */
		popupMenu3 = new JPopupMenu("Methods");
		JMenuItem createMethodItem = popupMenu3.add("Create New Method");
		createMethodItem.setToolTipText("Create a new method for this entity.");
		JMenuItem deleteAllMethodItem = popupMenu3.add("Remove All Methods");
		deleteAllMethodItem
				.setToolTipText("Delete all methods of this entity.");
		createMethodItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Category name
				String catName = (String) md.getMetaData("catName");
				// Get the category object
				Category category = getCategory(catName);
				// Define a new method on the category
				if (category != null)
					defineMethod(category);
				else
					MessageUtils
							.displayError("Can not obtain the category object.");
			}
		});
		deleteAllMethodItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Category name
				String catName = (String) md.getMetaData("catName");
				// Get the category object
				Category category = getCategory(catName);
				// Remove all methods from the category
				if (category != null) {
					int ret = JOptionPane.showConfirmDialog(
							CategoryDefinePanel.this,
							"Do you want to remove all methods from the category '"
									+ catName + "'?", "Confirmation",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						// Remove all methods
						if (deleteMethod(category, null)) {
							MessageUtils
									.displayNormal("All user-defined methods are removed from the category '"
											+ catName + "'.");
						} else {
							MessageUtils
									.displayNormal("Methods are not removed from the category '"
											+ catName + "'.");
						}
					}
				} else
					MessageUtils
							.displayError("Can not obtain the category object.");
			}
		});
		popupMenu6 = new JPopupMenu("Methods");
		JMenuItem createMethodItem2 = popupMenu6.add("Create New Method");
		createMethodItem2
				.setToolTipText("Create a new method for this entity.");
		createMethodItem2.addActionListener(createMethodItem
				.getActionListeners()[0]);

		/** For a single method node */
		popupMenu4 = new JPopupMenu("Method");
		JMenuItem deleteMethodItem = popupMenu4.add("Remove This Method");
		deleteMethodItem
				.setToolTipText("Delete current method from this entity.");
		deleteMethodItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// MetaData of the pop up node
				MetaData md = popupNode.getMetaData();
				// Category name
				String catName = (String) md.getMetaData("catName");
				// Get the category object
				Category category = getCategory(catName);
				// Get the method object
				CMethod method = (CMethod) md.getMetaData("method");
				// Remove all methods from the category
				if (category != null && method != null) {
					int ret = JOptionPane.showConfirmDialog(
							CategoryDefinePanel.this,
							"Do you want to remove the method '" + method.name
									+ "' from the category '" + catName + "'?",
							"Confirmation", JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						// Remove the specified method
						if (deleteMethod(category, method)) {
							MessageUtils.displayNormal("The method '"
									+ method.name
									+ "' is removed from the category '"
									+ catName + "'.");
						} else {
							MessageUtils.displayNormal("The method '"
									+ method.name
									+ "' is not removed from the category '"
									+ catName + "'.");
						}
					}
				} else
					MessageUtils
							.displayError("Can not obtain the category/method object.");
			}
		});

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
			case CATEGORIES_NODE:
				if (engine.appManager.currentApp.dm.categoryExist())
					popupMenu1.show(tree, pe.getX(), pe.getY());
				else
					popupMenu5.show(tree, pe.getX(), pe.getY());
				break;
			case CATEGORY_NODE:
				popupMenu2.show(tree, pe.getX(), pe.getY());
				break;
			case METHODS_NODE:
				String catName = (String) md.getMetaData("catName");
				if (engine.appManager.currentApp.dm
						.categoryMethodExist(catName))
					popupMenu3.show(tree, pe.getX(), pe.getY());
				else
					popupMenu6.show(tree, pe.getX(), pe.getY());
				break;
			case METHOD_NODE:
				popupMenu4.show(tree, pe.getX(), pe.getY());
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
			case CATEGORY_NODE: {
				propertyPanel.setCenterComponent(categoryInformationPane);
				propertyPanel.getGradientTitlePanel().setTitleText(
						"Category: " + (String) treeNode.getUserObject());
				propertyPanel.getGradientTitlePanel().setIcon(
						treeNode.getImageIcon());

				// Setup panel information
				Category category = (Category) AppEngine.getInstance().appManager.currentApp.dm
						.getCategories().get(treeNode.getUserObject());
				categoryInformationPanel.setCategory(category);
				break;
			}
			case METHOD_NODE: {

				// Get category and method
				String categoryName = (String) md.getMetaData("catName");
				CMethod method = (CMethod) md.getMetaData("method");

				// Setup title
				StringBuffer title = new StringBuffer();
				if (method != null)
					title.append("Modifying Method '").append(method.name)
							.append("' of Category '").append(categoryName)
							.append("'");
				else
					title.append("Create a new method of Category '").append(
							categoryName).append("'");

				// Change the display panel
				propertyPanel.setCenterComponent(categoryMethodPanel);
				propertyPanel.getGradientTitlePanel().setTitleText(
						title.toString());
				propertyPanel.getGradientTitlePanel().setIcon(
						treeNode.getImageIcon());

				// Setup panel information
				Category category = (Category) AppEngine.getInstance().appManager.currentApp.dm
						.getCategories().get(categoryName);
				categoryMethodPanel.changeMethod(category, method);
				break;
			}
			}
		}
	}

	/**
	 * Get the category object with the specified name
	 * 
	 * @param catName
	 *            Category name
	 * @return The category object
	 */
	private Category getCategory(String catName) {
		if (catName == null)
			return null;
		return (Category) engine.appManager.currentApp.dm.getCategories().get(
				catName);
	}

	/**
	 * Remove the specified method of the category. If no method is specified,
	 * all methods of the category will be removed. After the method(s) is
	 * removed, the tree will be updated.
	 * 
	 * @param cat
	 *            Category object
	 * @param method
	 *            Method to remove
	 * @return Whether the removal is successful
	 */
	private boolean deleteMethod(Category cat, CMethod method) {
		try {
			boolean ret = engine.removeMethod(cat, method);
			this.treeChanged();
			return ret;
		} catch (Exception e) {
			MessageUtils.displayError(e);
			return false;
		}
	}

	/**
	 * Delete the specified category from the system. If no category is
	 * specified, all categories will be removed from the system.
	 * 
	 * @param cat
	 *            Category to delete
	 * @return Whether the removal is successful
	 */
	private boolean deleteCategory(String cat) {
		try {
			boolean ret = engine.removeCategory(cat);
			this.treeChanged();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.displayError(e);
			return false;
		}
	}

	// Show the method definition panel to define a new method
	public void defineMethod(Category category) {

		// Change the property panel
		propertyPanel.setCenterComponent(categoryMethodPanel);
		propertyPanel.getGradientTitlePanel().setTitleText(
				"Define a new category method");

		// Setup panel information
		categoryMethodPanel.changeMethod(category, null);

	}

	// Show the category definition panel
	public void showCategoryInformationPanel() {
		propertyPanel.setCenterComponent(categoryInformationPane);
	}

	// Reset the defined category
	// It is supposed to be used after the new category is created
	public void resetCategory() {
		showCategoryInformationPanel();
		categoryInformationPanel.setCategory(null);
	}

	/**
	 * The tree is needed to rebuilt, since categories are changed
	 */
	public void treeChanged() {

		/** Mark the application as dirty */
		AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);

		/** Reset the tree model */
		// Tree root
		TreeNode top = new TreeNode("Categories");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(top);
		tree.setModel(defaultModel);
		// Rebuild the model
		buildTreeModel(defaultModel, top);

	}

	/**
	 * The sub tree never exists
	 */
	public void subTreeChanged() {
		throw new RuntimeException("No subtree yet.");
	}

	protected void buildTree() {
		/** Tree root and model */
		// Tree root
		TreeNode top = new TreeNode("Categories");
		// Tree model
		DefaultTreeModel defaultModel = new DefaultTreeModel(top);

		// Build tree root
		tree = new JTree(top);
		// Tree model
		tree.setModel(defaultModel);
		buildTreeModel(defaultModel, top);
	}

	protected void buildTreeModel(DefaultTreeModel defaultModel, TreeNode top) {
		// Obtain all configured categories
		Map categories = AppEngine.getInstance().appManager.currentApp.dm
				.getCategories();
		Iterator iter = categories.keySet().iterator();
		List allNames = new ArrayList();
		while (iter.hasNext()) {

			// Category name
			String catName = (String) iter.next();
			allNames.add(catName);

		}
		Collections.sort(allNames, new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				return s1.compareTo(s2);
			}
		});

		// Create the listeners
		cl = new MyClickEventListener();
		pl = new MyPopupEventListener();

		// Set up the pop up event listener for the tree rooot
		MetaData md = new TreeNodeMetaData();
		md.addMetaData("type", new Integer(CATEGORIES_NODE));
		top.setMetaData(md);
		top.addPopupListener(pl);

		// No categories configured, then a node of 'No configured categories'
		// is constructed.
		if (categories.isEmpty()) {
			defaultModel.insertNodeInto(noCategoriesNode, top, 0);
			return;
		}

		// Construct each category
		int catCount = 0;
		for (int inx = 0; inx < allNames.size(); inx++) {

			// Category name
			String catName = (String) allNames.get(inx);
			// Category object
			Category cat = (Category) categories.get(catName);
			// Construct a node to represent this category
			TreeNode catNode = new TreeNode(catName);
			// Construct a meta data model (node type and whether behavior
			// network based) for that node
			md = new TreeNodeMetaData();
			md.addMetaData("type", new Integer(CATEGORY_NODE));
			md.addMetaData("catName", catName);
			md.addMetaData("bnbased", new Boolean(
					cat instanceof BNCategory ? true : false));
			catNode.setMetaData(md);
			catNode.addClickListener(cl);
			catNode.addPopupListener(pl);
			// Insert the node into the correct position
			defaultModel.insertNodeInto(catNode, top, catCount++);

			// Construct the properties child node
			TreeNode propertiesNode = new TreeNode("fields");
			md = new TreeNodeMetaData();
			md.addMetaData("type", new Integer(PROPERTIES_NODE));
			md.addMetaData("catName", catName);
			propertiesNode.setMetaData(md);
			propertiesNode.addClickListener(cl);
			propertiesNode.addPopupListener(pl);
			defaultModel.insertNodeInto(propertiesNode, catNode, 0);

			List props = cat.getOriginalProperties();
			if (props == null || props.isEmpty()) {
				defaultModel
						.insertNodeInto(noPropertiesNode, propertiesNode, 0);
			} else {
				int propCount = 0;
				for (int i = 0; i < props.size(); i++) {
					// For each property, construct a tree node for it. However,
					// no meta data is
					// configured for the node, meaning that that node can not
					// be edited independently.
					Property prop = (Property) props.get(i);
					TreeNode tn = new TreeNode(prop.name + "=" + prop.value);
					md = new TreeNodeMetaData();
					md.addMetaData("type", new Integer(PROPERTY_NODE));
					md.addMetaData("catName", catName);
					tn.setMetaData(md);
					tn.addPopupListener(pl);
					defaultModel
							.insertNodeInto(tn, propertiesNode, propCount++);
				}
			}

			// Construct the methods child node
			TreeNode methodsNode = new TreeNode("methods");
			md = new TreeNodeMetaData();
			md.addMetaData("type", new Integer(METHODS_NODE));
			md.addMetaData("catName", catName);
			methodsNode.setMetaData(md);
			methodsNode.addPopupListener(pl);
			defaultModel.insertNodeInto(methodsNode, catNode, 1);

			List methods = cat.getAllMethods();
			if (methods == null || methods.isEmpty()) {
				defaultModel.insertNodeInto(noMethodsNode, methodsNode, 0);
			} else {
				int methodCount = 0;
				for (int i = 0; i < methods.size(); i++) {
					CMethod method = (CMethod) methods.get(i);
					TreeNode tn = new TreeNode(method.name);
					md = new TreeNodeMetaData();
					md.addMetaData("type", new Integer(METHOD_NODE));
					md.addMetaData("catName", catName);
					md.addMetaData("method", method);
					tn.setMetaData(md);
					tn.addClickListener(cl);
					tn.addPopupListener(pl);
					defaultModel.insertNodeInto(tn, methodsNode, methodCount++);
				}
			}
		}
		// Expand the root tree node
		super.expand(top);
	}

}
