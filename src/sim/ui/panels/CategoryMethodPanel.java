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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SpringLayout;
import javax.swing.text.Document;

import sim.core.AppEngine;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.ui.method.EJEArea;
import sim.ui.tree.TreeUpdateListener;
import sim.util.MessageUtils;
import sim.util.SimException;

/**
 * An editor class of a single class method. It highlights the keyword and check
 * the validity of the method definition. Referenced methods and variables are
 * checked through JVM.
 * 
 * @author Fasheng Qiu
 */
public class CategoryMethodPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1687995295169957319L;

	// Method source code
	private EJEArea codeTextArea;

	// User defined methods
	private List userDefinedMethods;

	// Category
	private Category category = null;

	// Method
	private CMethod cMethod = null;

	// Original code
	private String code = null;

	// Parent panel, to save the method definition
	private CategoryInformationPanel parent = null;

	// The category change listener
	private TreeUpdateListener tul = null;

	/**
	 * Default Constructor
	 */
	public CategoryMethodPanel(CategoryInformationPanel parent) {
		this(null, null, parent);
	}

	/**
	 * Creates a new CategoryMethodPanel to modify existing method or create a
	 * new method based on whether the given cMethod parameter. If cMethod is
	 * not null, modify existing method. Otherwise, it is used to create a new
	 * method.
	 * 
	 * @param category
	 *            The category which the method belongs to
	 * @param cMethod
	 *            The existing method. Null if creating a new method
	 */
	public CategoryMethodPanel(Category category, CMethod cMethod,
			CategoryInformationPanel parent) {

		super();

		this.parent = parent;

		init();

		changeMethod(category, cMethod);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int) (screenSize.width * 0.8),
				(int) (screenSize.height * 0.8));
		Dimension guiSize = this.getSize();
		this.setLocation((screenSize.width - guiSize.width) / 2,
				(screenSize.height - guiSize.height) / 2);
	}

	/** Set up the category change listener */
	public void setTreeUpdateListener(TreeUpdateListener l) {
		tul = l;
	}

	/**
	 * Change the category and method this panel represents.
	 * 
	 * @param category
	 *            The new category to change to
	 * @param cMethod
	 *            The new method to changte to. NULL if creating a new method in
	 *            the given category
	 */
	public void changeMethod(Category category, CMethod cMethod) {

		// Category and method to be updated
		this.category = category;
		this.cMethod = cMethod;

		// Refresh the existing methods of the category
		if (category != null) {
			// Reload category methods to reflect changes if any
			userDefinedMethods = category.getAllMethods();
		} else {
			userDefinedMethods = new ArrayList();
		}

		// Initialize the text area
		codeTextArea.setPredefined(category);
		codeTextArea.setText("");
		codeTextArea.setCaretPosition(0);

		// Set the method to be modified
		Document document = codeTextArea.getDocument();
		if (cMethod != null) {
			code = cMethod.src;
			try {
				int caretPos = codeTextArea.getCaretPosition();
				document.insertString(caretPos, cMethod.src, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			code = null;
		}

	}

	/**
	 * Return the panel to operate category methods
	 * 
	 * @return the panel to operate category methods
	 */
	private void init() {

		JPanel cmPanel = this;

		JScrollPane codePanel = new JScrollPane();
		JButton editButton = new JButton("OK");
		JButton resetButton = new JButton("RESET");
		JButton backButton = new JButton("CANCEL");

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				codeTextArea.setText(code);
			}
		});
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.showCategoryInformationPanel();
			}
		});

		Font font = new Font("Times New Roman", Font.ITALIC, 14);
		JLabel label = new JLabel("Type @ to see available methods and fields.");
		label.setFont(font);
		label.setForeground(Color.BLUE);

		codeTextArea = new EJEArea(null);
		codeTextArea.setEditable(true);
		codeTextArea.setBackground(Color.WHITE);
		JViewport port = codePanel.getViewport();
		port.setOpaque(true);
		port.setBackground(Color.red);
		port.setView(codeTextArea);
		port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		codePanel
				.setPreferredSize(new Dimension((int) (screenSize.width * 0.5),
						(int) (screenSize.height * 0.5)));
		codePanel.setMaximumSize(codePanel.getPreferredSize());

		editButton.setActionCommand("edit");
		editButton.addActionListener(this);

		SpringLayout sl = new SpringLayout();
		cmPanel.setLayout(sl);
		cmPanel.add(label);
		cmPanel.add(codePanel);
		cmPanel.add(editButton);
		cmPanel.add(resetButton);
		cmPanel.add(backButton);

		// Layout label
		sl.putConstraint(SpringLayout.WEST, label, 15, SpringLayout.WEST,
				cmPanel);
		sl.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH,
				cmPanel);
		// Layout methodLabel
		sl.putConstraint(SpringLayout.WEST, codePanel, 15, SpringLayout.WEST,
				cmPanel);
		sl.putConstraint(SpringLayout.NORTH, codePanel, 5, SpringLayout.SOUTH,
				label);
		// Layout buttons
		sl.putConstraint(SpringLayout.WEST, editButton, 15, SpringLayout.WEST,
				cmPanel);
		sl.putConstraint(SpringLayout.NORTH, editButton, 15,
				SpringLayout.SOUTH, codePanel);
		sl.putConstraint(SpringLayout.WEST, resetButton, 15, SpringLayout.EAST,
				editButton);
		sl.putConstraint(SpringLayout.NORTH, resetButton, 15,
				SpringLayout.SOUTH, codePanel);
		sl.putConstraint(SpringLayout.WEST, backButton, 15, SpringLayout.EAST,
				resetButton);
		sl.putConstraint(SpringLayout.NORTH, backButton, 15,
				SpringLayout.SOUTH, codePanel);
	}

	private String[] getMethodsNameList(List cms2) {
		String[] cmss2 = new String[cms2 == null ? 0 : cms2.size()];
		for (int i = 0; cms2 != null && i < cms2.size(); i++) {
			cmss2[i] = ((CMethod) cms2.get(i)).name;
		}
		return cmss2;
	}

	public void actionPerformed(ActionEvent e) {

		// Check method definition
		String methodCode = codeTextArea.getText().trim();
		if (methodCode.equals("")) {
			MessageUtils.displayError("Method definition can not be empty.");
			return;
		}

		// Code not changed
		if (code != null && code.equals(methodCode)) {
			return;
		}

		// Obtain the method name
		String methodName = null;
		try {
			StringTokenizer st = new StringTokenizer(methodCode.substring(0,
					methodCode.indexOf('(')));
			while (st.hasMoreTokens()) {
				methodName = st.nextToken();
			}
		} catch (Exception ex) {
			MessageUtils.displayError("Method definition is not correct.");
			return;
		}

		// Check whether it is an identifier
		if (!sim.util.JavaUtils.isJavaIdentifier(methodName)) {
			MessageUtils.displayError(methodName
					+ " is not a valid java identifier.");
			return;
		}

		// Decide actions
		if (category != null && cMethod != null) {
			// Modified?
			boolean modified = false;
			// Modify the method in the given category
			java.util.List cms2 = userDefinedMethods;// .get(category.getName());
			String[] cmss2 = getMethodsNameList(cms2);
			for (int i = 0; i < cmss2.length; i++) {
				if (cmss2[i].equals(methodName)) {
					int ret = MessageUtils
							.displayConfirm("The method already exists, override?");
					if (ret == JOptionPane.YES_OPTION) {

						try {
							// Verify method definition, to avoid the duplicate
							// method definition exception,
							// The method name is replace only for the
							// verification purpose. The actual method
							// name is same as before.
							StringBuffer sb = new StringBuffer();
							sb.append(methodCode.substring(0, methodCode
									.indexOf(methodName)));
							sb.append("_temp" + System.currentTimeMillis()); // Temporary
							// method
							// name
							sb.append(methodCode.substring(methodCode
									.indexOf(methodName) + 1));
							AppEngine.getInstance().appManager.currentApp.dm
									.verifyMethodDef(category.getEntityType(),
											sb.toString());
						} catch (IndexOutOfBoundsException ex) {
							MessageUtils
									.displayError("Method definition is not correct.");
							return;
						} catch (Exception ex) {
							MessageUtils.displayError(ex);
							return;
						}

						// Override the existing method
						CMethod newMethod = new CMethod(methodName, methodCode);
						try {
							// Update category
							AppEngine.getInstance().replaceMethod(category,
									newMethod);
							// Update the methods in the kept map
							for (int ii = 0; ii < cms2.size(); ii++) {
								if (((CMethod) cms2.get(ii)).name
										.equals(methodName)) {
									cms2.set(ii, newMethod);
									break;
								}
							}
							// Change the tree
							if (tul != null)
								tul.treeChanged();
							modified = true;
							MessageUtils
									.displayNormal("The method is updated sucessfully!");
						} catch (Exception ee) {
							MessageUtils.displayError(ee);
						}
						return;
					}
				}
			}
			if (!modified) {
				MessageUtils
						.displayError("The method is not modified. The method name should not be changed.");
			}
		} else if (category != null && cMethod == null) {
			// Verify the method, only from the syntax aspect
			try {
				AppEngine.getInstance().appManager.currentApp.dm
						.verifyMethodDef(category.getEntityType(), methodCode);
			} catch (SimException ex) {
				MessageUtils.displayError(ex);
				return;
			}
			// Create a new method in the given category
			java.util.List cms2 = userDefinedMethods;
			String[] cmss2 = getMethodsNameList(cms2);
			for (int i = 0; i < cmss2.length; i++) {
				if (cmss2[i].equals(methodName)) {
					MessageUtils
							.displayConfirm("The method already exists, please define a new method.");
					return;
				}
			}
			// Create a new method if not already exists
			try {
				AppEngine.getInstance().createANewMethod(
						category.getEntityType(), true, true, methodName,
						methodCode);
				MessageUtils
						.displayNormal("The method is created sucessfully!");
				// Change the tree
				if (tul != null)
					tul.treeChanged();
			} catch (Exception ee) {
				MessageUtils.displayError(ee);
				return;
			}
		}

	}
}
