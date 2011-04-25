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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import sim.util.MessageUtils;
import sim.util.SimException;

/**
 * Panel for defining weights.
 * 
 * @author Fasheng Qiu
 * 
 */
public class DefineWeightsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7588737260848848959L;
	/** Controls */
	private JPanel tablePanel = new JPanel();
	private GridBagLayout tableLayout = new GridBagLayout();
	private BehaviorView dlgParent = null;
	private double[] weights = null;
	private int[] ids = null;
	private TextField[] coeffFields = null;
	private JCheckBox dynamic = new JCheckBox("Dynamical weights");
	private JTextArea dCoefficients = new JTextArea();

	public DefineWeightsPanel(BehaviorView parent, double coeff[], int[] ids,
			String bnames[]) {

		dlgParent = parent;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		if (coeff != null && coeff.length != 0 && bnames != null
				&& bnames.length > 0)
			init(coeff, ids, bnames);

	}

	/** Initialize controls */
	private void init(double weights[], int ids[], String bnames[]) {
		JPanel contentPane = tablePanel;
		JButton okButton = new JButton();
		JScrollPane sp2 = new JScrollPane();

		tablePanel.setLayout(tableLayout);

		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		this.weights = weights;
		this.ids = ids;
		coeffFields = new TextField[weights.length];
		JLabel[] bnL = new JLabel[weights.length];
		int k = 0;
		for (k = 0; k < weights.length; k++) {
			bnL[k] = new JLabel(bnames[k]);
			coeffFields[k] = new TextField(Double.toString(weights[k]), 10);

			addToWeightsTable(bnL[k], 0, k + 1);
			addToWeightsTable(coeffFields[k], 1, k + 1);
		}

		dCoefficients.setRows(10);
		dCoefficients.setColumns(38);

		JPanel dPanel = new JPanel();
		dPanel.setLayout(new BoxLayout(dPanel, BoxLayout.PAGE_AXIS));
		dynamic.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp2.setAlignmentX(Component.LEFT_ALIGNMENT);
		okButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		dPanel.add(dynamic);
		dPanel.add(sp2);
		dPanel.add(okButton);

		contentPane.add(dPanel, new GridBagConstraints(0, k + 1,
				GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		dCoefficients.setEnabled(true);
		dynamic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dynamic.isSelected()) {
					for (int k = 0; k < coeffFields.length; k++) {
						coeffFields[k].setEnabled(false);
					}
					dCoefficients.setEnabled(true);
				} else {
					for (int k = 0; k < coeffFields.length; k++) {
						coeffFields[k].setEnabled(true);
					}
					dCoefficients.setEnabled(false);
				}
			}
		});
		sp2.getViewport().add(dCoefficients, null);

		add(contentPane);

		/** Initialize states */
		boolean d = dlgParent.getEntity().getBehaviorNetwork().isDynamic();
		String dynamicStr = dlgParent.getEntity().getBehaviorNetwork()
				.getDynamicStr();
		if (d) {
			dynamic.setSelected(true);
			dCoefficients.setEnabled(true);
			dCoefficients.setText(dynamicStr);
			for (k = 0; k < weights.length; k++) {
				coeffFields[k].setEnabled(false);
			}
		} else {
			dCoefficients.setEnabled(false);
		}
	}

	/** Setup table cells */
	public void addToWeightsTable(Component component, int x, int y) {

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0;
		c.weighty = 0;
		tablePanel.add(component);
		tableLayout.setConstraints(component, c);

	}

	public void onOk() {
		if (!dynamic.isSelected()) {
			if (!update())
				return;
			dlgParent.updateWeights(weights, ids);
		} else {
			String dynaCoef = dCoefficients.getText().trim();
			if (dynaCoef.equals("")) {
				MessageUtils
						.displayError("Code for computing dynamic weights is required.");
				return;
			}
			try {
				dlgParent.getEntity().getBehaviorNetwork()
						.setDynamicBehaviorNetwork(dlgParent.getEntity(),
								dCoefficients.getText());
			} catch (Exception e) {
				MessageUtils
						.displayError(new SimException(
								"NET-S-DYNA-WEIGHTS",
								"The code for computing dynamic weights has syntax errors.",
								e));
				return;
			}
		}
		MessageUtils.displayNormal("The network is updated successfully.");

	}

	public boolean update() {

		for (int k = 0; k < weights.length; k++) {
			String cValue = coeffFields[k].getText();
			try {
				weights[k] = Double.valueOf(cValue).doubleValue();
				if (weights[k] < 0.0 || weights[k] > 1.0) {
					MessageUtils.displayError("In row " + (k + 1)
							+ " the weight '" + cValue
							+ "' is out of range [0.0, 1.0]. ");
					return false;
				}
			} catch (Exception e) {
				MessageUtils.displayError("In row " + (k + 1) + " the weight '"
						+ cValue + "' is not a real number. ");
				return false;
			}
		}

		return true;

	}
}
