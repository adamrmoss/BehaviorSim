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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sim.core.AppEngine;
import sim.model.entity.Property;
import sim.model.entity.PropertyType;

/**
 * <p>
 * Title: The dialog used to edit the chosen property
 * </p>
 * 
 * <p>
 * Description: Interface for editing category property
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Fasheng
 * @version 1.0
 */
public class EditPropertyDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4432798144422753826L;
	// panels
	private JPanel contentPane = new JPanel();
	private JPanel buttonBarGrid = new JPanel();
	private JPanel controlsPanel = new JPanel();
	private JPanel buttonBar = new JPanel();

	// controls: buttons, text boxes, Labels
	private JButton cancelButton = new JButton();
	private JButton okButton = new JButton();
	private JLabel nameLabel = new JLabel("Field Name: ");
	private JLabel typeLabel = new JLabel("Field Type:");
	private JLabel valueLabel = new JLabel("Value:");
	private JTextField nameTF = new JTextField();
	private JTextField valueTF = new JTextField();
	private JComboBox typeTF = new JComboBox(
			new String[] { PropertyType.types[PropertyType.NUMBER] }/*
																	 * PropertyType.
																	 * types
																	 */);

	// layouts
	private BorderLayout borderLayout = new BorderLayout();
	private GridLayout buttonBarGridLayout = new GridLayout();
	private GridBagLayout controlsGridBagLayout = new GridBagLayout();
	private FlowLayout buttonBarFlowLayout = new FlowLayout();

	// application engine
	private AppEngine engine = null;
	private List properties = null;
	private Property para = null;
	private int selectedRow;

	public EditPropertyDialog(AppEngine eng, List p, int s) {
		engine = eng;
		properties = p;
		para = (Property) properties.get(0);
		selectedRow = s;
		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			init();
			pack();
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

	public void init() {

		setTitle("Edit the chosen category field...");
		setContentPane(contentPane);
		contentPane.setPreferredSize(new Dimension(300, 200));
		contentPane.setLayout(borderLayout);
		buttonBar.setLayout(buttonBarFlowLayout);
		buttonBarGrid.setLayout(buttonBarGridLayout);
		buttonBarGridLayout.setColumns(2);
		buttonBarGridLayout.setHgap(5);

		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		cancelButton.setText("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonBarGrid.add(okButton);
		buttonBarGrid.add(cancelButton);
		buttonBar.add(buttonBarGrid);

		nameTF.setEditable(true);
		valueTF.setEditable(true);
		typeTF.setEditable(false);

		nameTF.setText(para.name);

		if (para.type == PropertyType.NUMBER) {
			typeTF.setSelectedIndex(0);
		} else if (para.type == PropertyType.STRING) {
			typeTF.setSelectedIndex(1);
		} else {
			typeTF.setSelectedIndex(2);
		}

		typeTF.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// Set the default value for the selected type
				JComboBox source = (JComboBox) e.getSource();
				valueTF.setText(PropertyType.defaultValues[source
						.getSelectedIndex()]);
			}
		});

		String value = String.valueOf(para.value);
		if (value == null || value.trim().equalsIgnoreCase(""))
			valueTF.setText(PropertyType.defaultValues[typeTF
					.getSelectedIndex()]);
		else
			valueTF.setText(value);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(nameLabel);
		p.add(nameTF);

		controlsPanel.setLayout(controlsGridBagLayout);

		controlsPanel.add(p, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

		controlsPanel.add(typeLabel, new GridBagConstraints(0, 1, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

		controlsPanel.add(typeTF, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

		controlsPanel.add(valueLabel, new GridBagConstraints(0, 3, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

		controlsPanel.add(valueTF, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

		contentPane.add(controlsPanel, BorderLayout.NORTH);
		contentPane.add(buttonBar, BorderLayout.SOUTH);
	}

	public void onOk() {
		// Check whether the default value and the type is matched
		String name = nameTF.getText().trim();
		int type = typeTF.getSelectedIndex();
		String value = valueTF.getText().trim();
		if (name.equals("") || value.equals("")) {
			sim.util.MessageUtils
					.displayWarning("Field name and default value are required.");
			return;
		}
		try {
			switch (type) {
			case PropertyType.NUMBER:
				valueTF.setText(String.valueOf(Double.parseDouble(value)));
				value = valueTF.getText();
				break;
			case PropertyType.STRING:
			case PropertyType.OBJECT:
				;
			}
		} catch (Exception e) {
			sim.util.MessageUtils
					.displayWarning("Field type and default value are not matched.");
			return;
		}
		engine.updateProperty(selectedRow, name, type, value);
		dispose();
	}

	public void onCancel() {
		dispose();
	}

}
