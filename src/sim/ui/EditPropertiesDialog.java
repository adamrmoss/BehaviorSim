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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import sim.model.entity.Property;
import sim.model.entity.PropertyType;
import sim.util.MessageUtils;

/**
 * Editor for entity properties
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class EditPropertiesDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6452380245913286212L;
	/** Controls */
	private GridBagLayout layout = new GridBagLayout();
	private JTextField[] valuesTF = null;

	/** List of properties to edit */
	private List curProps = null;
	private List origProps = null;

	/** Property is changed */
	private boolean changed = false;

	/** Content Panel */
	private JPanel contentPanel = new JPanel();

	/**
	 * Constructor
	 */
	public EditPropertiesDialog(List properties, List origProps) {
		super(MainFrame.getInstance(), true);

		/** Initialize states */
		this.curProps = properties;
		this.origProps = origProps;
		this.valuesTF = new JTextField[properties.size()];

		/** Initialize components */
		init();

		/** Initialize the components */
		super.setTitle("Entity fields");

		/** Set size */
		if (properties.size() > 1)
			setSize(400, (properties.size() + 3) * 40);
		else
			setSize(400, 150);

		/** Set location */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getPreferredSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		setLocation(((screenSize.width - frameSize.width) / 2),
				((screenSize.height - frameSize.height) / 2));

	}

	/**
	 * Initialize the components used in this window.
	 */
	public void init() {

		/** Layout */
		contentPanel.setLayout(layout);

		/** Controls */
		NumberFormat formatter = new DecimalFormat("#0.00");
		addToPanel(new JLabel("Name"), 0, 0);
		addToPanel(new JLabel("Initial Value"), 1, 0);
		addToPanel(new JLabel("    "), 2, 0);
		addToPanel(new JLabel("Last Value"), 3, 0);

		int i = 0;
		for (; i < origProps.size(); i++) {

			Property p = (Property) origProps.get(i);

			JLabel temp = new JLabel(p.name);
			temp.setAlignmentX(Component.RIGHT_ALIGNMENT);
			temp.setFont(new Font("Times New Roman", Font.ITALIC, 12));

			String curVal = "";
			try {
				curVal = formatter
						.format(((Number) ((Property) curProps.get(i)).value)
								.doubleValue());
			} catch (Exception e) {
			}
			JLabel cur = new JLabel(curVal);
			cur.setFont(new Font("Times New Roman", Font.BOLD, 12));
			cur.setForeground(Color.BLUE);

			valuesTF[i] = new JTextField(15);
			valuesTF[i].setText(String.valueOf(p.value));

			addToPanel(temp, 0, i + 1);
			addToPanel(valuesTF[i], 1, i + 1);
			addToPanel(new JLabel("    "), 2, i + 1);
			addToPanel(cur, 3, i + 1);

		}

		/** Add space */
		addToPanel(Box.createVerticalStrut(10), 0, i + 1);
		addToPanel(Box.createVerticalStrut(10), 1, i + 1);

		/** Setup listeners for the save button */
		JButton saveButton = new JButton("OK");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		addToPanel(saveButton, 0, i + 2);

		/** Setup listeners for the cancel button */
		JButton cancelButton = new JButton("CANCEL");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		addToPanel(cancelButton, 1, i + 2);

		/** Add the content panel into the dialog */
		JScrollPane sp = new JScrollPane(contentPanel);
		getContentPane().add(sp);

	}

	/** Setup table cells */
	public void addToPanel(Component component, int x, int y) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 0;
		c.ipady = 5;
		c.weightx = 0;
		c.weighty = 0;
		contentPanel.add(component);
		layout.setConstraints(component, c);

	}

	/**
	 * Action to perform
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			/** States */
			for (int i = 0; i < origProps.size(); i++) {

				Property p = (Property) origProps.get(i);

				if (p.type == PropertyType.NUMBER) {
					try {
						Double.parseDouble(valuesTF[i].getText());
					} catch (Exception ee) {
						MessageUtils.displayError("The value '"
								+ valuesTF[i].getText() + "' is "
								+ "incompatible with the property type.");
						return;
					}
				}

				p.value = valuesTF[i].getText();

				changed = true;

			}
		}
		setVisible(false);
		dispose();
	}

	/**
	 * @return the props
	 */
	public List getProps() {
		return origProps;
	}

	/**
	 * @return the changed
	 */
	public boolean isChanged() {
		return changed;
	}
}
