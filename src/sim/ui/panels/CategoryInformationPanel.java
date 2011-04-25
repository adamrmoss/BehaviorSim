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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import sim.core.AppEngine;
import sim.core.PropertyListener;
import sim.model.action.CompositeAction;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Property;
import sim.ui.EditPropertyDialog;
import sim.ui.tree.TreeUpdateListener;
import sim.util.FileFilterUtils;
import sim.util.MessageUtils;

/**
 * Dialog used to define the entity categories, where category name, picture,
 * and other properties are defined.
 * 
 * When a property is defined, the property name and type should be defined.
 * Also we can give a default value to the property.
 * 
 * @author Owner
 * 
 */
public class CategoryInformationPanel extends JPanel implements ActionListener,
		ItemListener, PropertyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8536052382082506143L;
	// Controls
	private JButton methodButton = new JButton("Define Method");
	// private JCheckBox cb = new JCheckBox("Need to setup behavior network?");
	// private JComboBox mechanisms = null;
	private JTextField variableNameTextField = new JTextField(15);
	private JTextField pictureFileName = new JTextField(20);
	private JFileChooser pictureChoser = new JFileChooser();
	private JTable propertyTable = new JTable();
	private CategoryPropertyTableModel propertyTableModel = new CategoryPropertyTableModel();

	// application engine
	private int selectedRow;
	private AppEngine engine = AppEngine.getInstance();

	// Category to be edited
	private Category category = null;

	// The category change listener
	private TreeUpdateListener tul = null;

	// Category define panel
	private CategoryDefinePanel parent = null;

	// Method defitions
	private List methods = null;

	// Action definitions
	private Map actions = null;

	public CategoryInformationPanel(CategoryDefinePanel p) {
		parent = p;
		engine.setPropertyListener(this);

		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			init();
			Dimension frameSize = getPreferredSize();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (frameSize.height > screenSize.height)
				frameSize.height = screenSize.height;
			if (frameSize.width > screenSize.width)
				frameSize.width = screenSize.width;
			setLocation(((screenSize.width - frameSize.width) / 2),
					((screenSize.height - frameSize.height) / 2));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** Set up the category change listener */
	public void setTreeUpdateListener(TreeUpdateListener l) {
		tul = l;
	}

	/**
	 * Set the category to be edited, including refreshing the value of controls
	 * 
	 * @param category
	 *            The category to be edited
	 */
	public void setCategory(Category category) {
		this.category = category;
		// Remove properties first
		propertyTableModel.removeAll();
		if (category == null) {
			variableNameTextField.setText("");
			pictureFileName.setText("");
			// cb.setSelected(false);
			// mechanisms.setVisible(false);
			methodButton.setEnabled(false);
			return;
		}
		methodButton.setEnabled(true);
		variableNameTextField.setText(category.getEntityType());
		pictureFileName.setText(category.getImagePath());
		// cb.setSelected(category instanceof BNCategory ? true: false);
		// if (!cb.isSelected()) {
		// mechanisms.setVisible(false);
		// } else {
		// mechanisms.setVisible(true);
		// mechanisms.setSelectedIndex(((BNCategory)category).getActionSelectionMechanism()
		// instanceof MutualInhibitionMechanism ? 0 : 1);
		// }
		List properties = category.getOriginalProperties();
		if (properties != null && !properties.isEmpty()) {
			for (int i = 0; i < properties.size(); i++) {
				Property p = (Property) properties.get(i);
				if (p.name.equals("name") || p.name.equals("iconPath")) // Internal
					// properties
					// of
					// category,
					// just
					// ignore
					continue;
				propertyTableModel.addRow(p.name, p.type, p.value);
			}
		}

	}

	public void init() {
		// layouts
		GridLayout buttonBarGridLayout = new GridLayout();
		FlowLayout buttonBarFlowLayout = new FlowLayout();

		// panels
		JPanel buttonBarGrid = new JPanel();
		JPanel buttonBar = new JPanel();
		JScrollPane propertyScrollPane = new JScrollPane();

		// controls: buttons, text boxes, Labels
		JButton okButton = new JButton();
		JButton addButton = new JButton();
		JButton editButton = new JButton();
		JButton deleteButton = new JButton();
		JLabel pictureLabel = new JLabel("Category Picture:");
		JLabel propertyLabel = new JLabel("Category Fields:");
		JButton pictureChooser = new JButton("Choose");
		JLabel variableNameLabel = new JLabel("Category Name :");
		pictureChooser.addActionListener(this);

		buttonBar.setLayout(buttonBarFlowLayout);
		buttonBarGrid.setLayout(buttonBarGridLayout);
		buttonBarGridLayout.setColumns(2);
		buttonBarGridLayout.setHgap(5);

		methodButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defineMethod(category);
			}
		});

		okButton.setText("Apply");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		addButton.setText("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAdd();
			}
		});

		editButton.setText("Edit");
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEdit();
			}
		});

		deleteButton.setText("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		buttonBarGrid.add(addButton);
		buttonBarGrid.add(editButton);
		buttonBarGrid.add(deleteButton);
		buttonBar.add(buttonBarGrid);

		propertyTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							TableModel tm = propertyTable.getModel();
							int[] selRows = propertyTable.getSelectedRows();
							if (selRows.length > 0) {
								for (int i = 0; i < selRows.length; i++) {
									selectedRow = selRows[i];
									tm.setValueAt((new Boolean((String) tm
											.getValueAt(selRows[i], 0)))
											.booleanValue() ? "FALSE" : "TRUE",
											selRows[i], 0);
								}
							}
						}
					}
				});
		propertyTable.setAutoCreateColumnsFromModel(false);
		propertyTable.setColumnModel(createColumnModel());
		propertyTable.setRowSelectionAllowed(true);
		propertyTable.setColumnSelectionAllowed(false);
		propertyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		propertyTable.setModel(propertyTableModel);
		propertyTable.getTableHeader().setPreferredSize(new Dimension(400, 20));
		propertyScrollPane.setPreferredSize(new Dimension(400, 200));
		propertyScrollPane.getViewport().add(propertyTable, null);

		// Setup layout
		SpringLayout sl = new SpringLayout();
		setLayout(sl);
		add(variableNameLabel);
		add(variableNameTextField);
		add(pictureLabel);
		add(pictureFileName);
		add(pictureChooser);
		add(propertyLabel);
		add(propertyScrollPane);
		add(buttonBar);
		add(methodButton);
		add(okButton);

		// Layout variableNameLabel at (15, 5)
		sl.putConstraint(SpringLayout.WEST, variableNameLabel, 15,
				SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, variableNameLabel, 5,
				SpringLayout.NORTH, this);

		sl.putConstraint(SpringLayout.WEST, variableNameTextField, 15,
				SpringLayout.EAST, variableNameLabel);
		sl.putConstraint(SpringLayout.NORTH, variableNameTextField, 5,
				SpringLayout.NORTH, this);

		sl.putConstraint(SpringLayout.WEST, pictureLabel, 15,
				SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, pictureLabel, 15,
				SpringLayout.SOUTH, variableNameLabel);

		sl.putConstraint(SpringLayout.WEST, pictureFileName, 15,
				SpringLayout.EAST, pictureLabel);
		sl.putConstraint(SpringLayout.NORTH, pictureFileName, 15,
				SpringLayout.SOUTH, variableNameLabel);
		sl.putConstraint(SpringLayout.WEST, pictureChooser, 15,
				SpringLayout.EAST, pictureFileName);
		sl.putConstraint(SpringLayout.NORTH, pictureChooser, 15,
				SpringLayout.SOUTH, variableNameLabel);

		// sl.putConstraint(SpringLayout.WEST, cb,
		// 15,
		// SpringLayout.WEST, this);
		// sl.putConstraint(SpringLayout.NORTH, cb,
		// 15,
		// SpringLayout.SOUTH, pictureLabel);
		//		
		// sl.putConstraint(SpringLayout.WEST, mechanisms,
		// 15,
		// SpringLayout.EAST, cb);
		// sl.putConstraint(SpringLayout.NORTH, mechanisms,
		// 15,
		// SpringLayout.SOUTH, pictureLabel);

		sl.putConstraint(SpringLayout.WEST, propertyLabel, 15,
				SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, propertyLabel, 15,
				SpringLayout.SOUTH, pictureLabel);

		sl.putConstraint(SpringLayout.WEST, propertyScrollPane, 15,
				SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, propertyScrollPane, 15,
				SpringLayout.SOUTH, propertyLabel);

		sl.putConstraint(SpringLayout.WEST, buttonBar, 10, SpringLayout.WEST,
				this);
		sl.putConstraint(SpringLayout.NORTH, buttonBar, 15, SpringLayout.SOUTH,
				propertyScrollPane);

		sl.putConstraint(SpringLayout.WEST, methodButton, 15,
				SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, methodButton, 30,
				SpringLayout.SOUTH, buttonBar);

		sl.putConstraint(SpringLayout.WEST, okButton, 15, SpringLayout.WEST,
				this);
		sl.putConstraint(SpringLayout.NORTH, okButton, 40, SpringLayout.SOUTH,
				methodButton);

		pictureFileName.setEditable(false);
		pictureChoser.setFileFilter(new FileFilterUtils(new String[] { "jpg",
				"tif", "gif", "png" }, true,
				"Image File (*.jpg, *.gif, *.tif, *.png)"));

		pictureChoser.setCurrentDirectory(new File(
				AppEngine.getInstance().appManager.currentApp.getAppDir()));

	}

	// GUI to define a new method of the new category
	private void defineMethod(Category category) {
		parent.defineMethod(category);
	}

	// Save the method definition for the new category
	// Return false if the method name already exists
	public boolean saveMethod(CMethod c) {
		if (methods == null)
			methods = new ArrayList();
		for (int i = 0; i < methods.size(); i++) {
			if (((CMethod) methods.get(i)).name.equals(c.name.trim())) {
				int a = MessageUtils
						.displayConfirm("The method with the name '" + c.name
								+ "' alread exists override it?");
				if (a == JOptionPane.YES_OPTION) {
					methods.set(i, c);
					return true;
				}
				return false;
			}
		}
		methods.add(c);
		showCategoryInformationPanel();
		return true;
	}

	// Obtain the defined methods used for action definition
	public List getMethods() {
		if (methods == null || methods.isEmpty())
			return new ArrayList();
		return new ArrayList(methods);
	}

	// Save the action definition for the new category
	// Return false if the action name already exists
	public boolean saveAction(CompositeAction c) {
		if (actions == null)
			actions = new HashMap();
		if (actions.get(c.getActionName()) != null) {
			int a = MessageUtils.displayConfirm("The action with the name '"
					+ c.getActionName() + "' alread exists override it?");
			if (a == JOptionPane.YES_OPTION) {
				actions.put(c.getActionName(), c);
				return true;
			}
			return false;
		}
		actions.put(c.getActionName(), c);
		showCategoryInformationPanel();
		return true;
	}

	// Show the category information panel
	public void showCategoryInformationPanel() {
		parent.showCategoryInformationPanel();
	}

	public void actionPerformed(ActionEvent e) {
		int result = pictureChoser.showOpenDialog(this);
		// if we selected an image, load the image
		if (result == JFileChooser.APPROVE_OPTION) {
			this.pictureFileName.setText(pictureChoser.getSelectedFile()
					.getPath());
		}
	}

	public void itemStateChanged(ItemEvent e) {

	}

	class CRenderer extends JCheckBox implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8678822664170962156L;

		public CRenderer() {
			setOpaque(false);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			setSelected(isSelected);
			setToolTipText("Edit " + table.getValueAt(row, 1));

			return this;
		}
	}

	class CEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7210702701291670842L;
		protected final JCheckBox checkBox;

		public CEditor(JCheckBox ch) {
			super(ch);
			checkBox = new JCheckBox();
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			checkBox.setSelected(isSelected);
			checkBox.setToolTipText("Edit " + table.getValueAt(row, 1));
			return checkBox;
		}

		public Object getCellEditorValue() {
			return checkBox.isSelected() ? "TRUE" : "FALSE";
		}

		public boolean stopCellEditing() {
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}

	public DefaultTableColumnModel createColumnModel() {

		TableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();

		TableColumn columnZero = new TableColumn(0, 0, new CRenderer(),
				new CEditor(new JCheckBox()));
		TableColumn columnOne = new TableColumn(1, 0, defaultRenderer, null);
		TableColumn columnTwo = new TableColumn(2, 0, defaultRenderer, null);
		TableColumn columnThree = new TableColumn(3, 0, defaultRenderer, null);

		columnZero.setHeaderValue("");
		columnOne.setHeaderValue("Name");
		columnTwo.setHeaderValue("Type");
		columnThree.setHeaderValue("Value");

		columnModel.addColumn(columnZero);
		columnModel.addColumn(columnOne);
		columnModel.addColumn(columnTwo);
		columnModel.addColumn(columnThree);

		return columnModel;
	}

	// Add a new category property
	public void onAdd() {
		propertyTableModel.addRow("", 0, "");

		propertyTable.setRowSelectionInterval(
				propertyTableModel.getRowCount() - 1, propertyTableModel
						.getRowCount() - 1);
		propertyTable.setColumnSelectionInterval(0, 0);
		TableModel tm = propertyTable.getModel();
		tm.setValueAt("TRUE", propertyTableModel.getRowCount() - 1, 0);
		super.repaint();

		java.util.List properties = new ArrayList();
		properties.add(propertyTableModel.getProperty(propertyTableModel
				.getRowCount() - 1));
		JDialog e = new EditPropertyDialog(engine, properties,
				propertyTableModel.getRowCount() - 1);
		e.setModal(true);
		e.show();
	}

	// Delete the chosen property
	public void onDelete() {
		propertyDelete(selectedRow/* propertyTable.getSelectedRow() */);
	}

	// Edit the chosen property
	public void onEdit() {
		java.util.List properties = new ArrayList();
		properties.add(propertyTableModel.getProperty(selectedRow));

		JDialog e = new EditPropertyDialog(engine, properties, selectedRow);
		e.setModal(true);
		e.show();
	}

	public void onOk() {
		try {
			// Check whether the property name is chosen
			// Just Ignore

			// Check whether the default value matches its type
			// Just Ignore

			// If checks are passed, create the category if it is actually to
			// create a new category
			// rather than modifying an existing one
			String categoryName = variableNameTextField.getText();
			String pictureName = pictureFileName.getText();

			// Check whether the category name and picture is provided
			if (categoryName.length() == 0 || pictureName.length() == 0) {
				MessageUtils
						.displayWarning("Category name and picture are required.");
				return;
			}

			// Check whether it is an identifier
			if (!sim.util.JavaUtils.isJavaIdentifier(categoryName)) {
				MessageUtils.displayError(categoryName
						+ " is not a valid java identifier.");
				return;
			}

			// Check properties
			List rows = propertyTableModel.getRows();
			boolean nbn = true;// cb.isSelected();

			// Check whether the property is properly set
			for (int i = 0; i < rows.size(); i++) {
				Property property = (Property) rows.get(i);
				if (property.getName() == null
						|| property.getName().trim().length() == 0) {
					MessageUtils.displayError("Field name can not be empty.");
					return;
				}
			}

			// Create a new category
			if (this.category == null) {
				// Check the uniqueness of the category name
				if (engine.appManager.currentApp.dm.categoryExist(categoryName)) {
					MessageUtils.displayError("The category '" + categoryName
							+ "' is already in the system.");
					return;
				}
				// If methods are defined,
				try {
					if (methods != null && !methods.isEmpty())
						engine.createANewCategory(categoryName, pictureName,
								nbn, rows, methods);
					else
						engine.createANewCategory(categoryName, pictureName,
								nbn, rows);
				} catch (Exception e) {
					MessageUtils.displayError(e.getMessage());
					return;
				}
				MessageUtils
						.displayNormal("The category is created successfully.");
				// Reset the controls to use for further use
				parent.resetCategory();
			} else { // Modify an existing category
				String oldCategoryName = category.getEntityType();
				try {
					engine.updateCategory(oldCategoryName, categoryName,
							pictureName, nbn, rows);
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.debug(this, "onOK", e);
					MessageUtils.displayError(e.getMessage());
					return;
				}
				MessageUtils
						.displayNormal("The category is updated successfully.");
			}

			// Notify the tree update listener
			if (tul != null)
				tul.treeChanged();
		} catch (Exception e) {
			e.printStackTrace();
			return;

		}
	}

	public static class PictureFilterUtils {

		public final static String jpeg = "jpeg";
		public final static String jpg = "jpg";
		public final static String gif = "gif";
		public final static String tiff = "tiff";
		public final static String tif = "tif";
		public final static String png = "png";

		/*
		 * Get the extension of a file.
		 */
		public static String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}
	}

	public void propertyAdded(String name, int type, String value) {
		propertyTableModel.addRow(name, type, value);
	}

	public void propertyEdit(int selectedRow, Property newParameter) {
		propertyTableModel.setValueAt(newParameter.name, selectedRow, 1);
		propertyTableModel.setValueAt("" + newParameter.type, selectedRow, 2);
		propertyTableModel.setValueAt("" + newParameter.value, selectedRow, 3);
		super.repaint();
	}

	public void propertyDelete(int selectedRow) {
		propertyTableModel.removeRow(selectedRow);
	}

}
