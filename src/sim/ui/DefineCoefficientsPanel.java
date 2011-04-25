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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import sim.util.MessageUtils;
import sim.util.SimException;

/**
 * Panel for defining coefficients.
 * 
 * @author Fasheng Qiu
 * 
 */
public class DefineCoefficientsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1749496856009674273L;
	/** Controls */
	private JPanel tablePanel = new JPanel();
	private GridBagLayout tableLayout = new GridBagLayout();
	private BehaviorView dlgParent = null;
	private JTextField[][] coeffFields = null;
	private JCheckBox dynamic = new JCheckBox("Dynamical coefficients");
	private JTextArea dCoefficients = new JTextArea(10, 25);
	private JScrollPane sp2 = new JScrollPane();

	/** Variables */
	private double[][] coefficients = null;
	private int[] ids = null;

	/** Constructor */
	public DefineCoefficientsPanel(BehaviorView parent, double coeff[][],
			int ids[], String[] names) {
		dlgParent = parent;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		if (coeff != null && coeff.length != 0 && coeff[0].length != 0
				&& ids != null && ids.length > 0)
			init(coeff, ids, names);

	}

	/** Initialize controls */
	private void init(double coeff[][], int ids[], String bnames[]) {

		JButton okButton = new JButton();
		JPanel buttonBar = new JPanel();
		FlowLayout buttonBarFlowLayout = new FlowLayout();
		JPanel buttonBarGrid = new JPanel();
		GridLayout buttonBarGridLayout = new GridLayout();

		buttonBar.setLayout(buttonBarFlowLayout);
		buttonBarGrid.setLayout(buttonBarGridLayout);
		buttonBarGridLayout.setColumns(2);
		buttonBarGridLayout.setHgap(6);
		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		coefficients = coeff;
		this.ids = ids;
		coeffFields = new JTextField[coeff.length][];
		tablePanel.setLayout(tableLayout);
		addToCoefficientsTable(new JLabel(), 0, 0);
		for (int j = 0; j < ids.length; j++) {
			addToCoefficientsTable(new JLabel(bnames[j]), j + 1, 0);
		}
		int k = 0, i = 0;
		for (k = 0; k < coefficients.length; k++) {
			addToCoefficientsTable(new JLabel(bnames[k]), 0, k + 1);
			coeffFields[k] = new JTextField[coefficients[k].length];
			for (i = 0; i < coefficients[k].length; i++) {
				coeffFields[k][i] = new JTextField(5);
				if (i == k)
					coeffFields[k][i].setEnabled(false);
				addToCoefficientsTable(coeffFields[k][i], i + 1, k + 1); // x -
				// columns,
				// y
				// -
				// rows
				coeffFields[k][i].setText(String.valueOf(coefficients[k][i]));
				// MessageUtils.debug(this, "init", coeffFields[k][i].getText(
				// ));
			}
		}

		/** Initialize states */
		boolean d = dlgParent.getEntity().getBehaviorNetwork().isDynamic();
		String dynamicStr = dlgParent.getEntity().getBehaviorNetwork()
				.getDynamicStr();
		if (d) {
			dynamic.setSelected(true);
			dCoefficients.setEnabled(true);
			dCoefficients.setText(dynamicStr);
			for (k = 0; k < coefficients.length; k++) {
				for (i = 0; i < coefficients[k].length; i++) {
					coeffFields[k][i].setEnabled(false);
				}
			}
		} else {
			dCoefficients.setEnabled(false);
		}

		JPanel dPanel = new JPanel();
		dPanel.setLayout(new BoxLayout(dPanel, BoxLayout.PAGE_AXIS));
		constructDynamicalPanel(dPanel);
		dPanel.add(okButton);

		tablePanel.add(dPanel, new GridBagConstraints(0, k + 1,
				GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		add(tablePanel);

		if (bnames.length == 1)
			okButton.setEnabled(false);
		else
			okButton.setEnabled(true);

	}

	/** Setup the dynamic panel */
	private void constructDynamicalPanel(JPanel ret) {

		dynamic.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp2.setAlignmentX(Component.LEFT_ALIGNMENT);

		sp2.getViewport().add(dCoefficients);

		ret.add(dynamic);
		ret.add(sp2);

		dynamic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dynamic.isSelected()) {
					for (int k = 0; k < coefficients.length; k++) {
						for (int i = 0; i < coefficients[k].length; i++) {
							coeffFields[k][i].setEnabled(false);
						}
					}
					dCoefficients.setEnabled(true);
				} else {
					for (int k = 0; k < coefficients.length; k++) {
						for (int i = 0; i < coefficients[k].length; i++) {
							if (i != k)
								coeffFields[k][i].setEnabled(true);
						}
					}
					dCoefficients.setEnabled(false);
				}
			}
		});

	}

	/** Setup table cells */
	public void addToCoefficientsTable(Component component, int x, int y) {
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
			dlgParent.updateCoefficients(coefficients, ids);
		} else {
			String dynaCoef = dCoefficients.getText().trim();
			if (dynaCoef.equals("")) {
				MessageUtils
						.displayError("Code for computing dynamic coefficients is required.");
				return;
			}
			try {
				dlgParent.getEntity().getBehaviorNetwork()
						.setDynamicBehaviorNetwork(dlgParent.getEntity(),
								dCoefficients.getText());
			} catch (Exception e) {
				MessageUtils
						.displayError(new SimException(
								"NET-S-DYNA-COEFF",
								"The code for computing dynamic coefficients has syntax errors.",
								e));
				return;
			}
		}
		MessageUtils.displayNormal("The network is updated successfully.");

	}

	/**
	 * Update the coefficients
	 * 
	 * @param coefficients
	 *            The new coefficients to update
	 */
	public void refreshCoeffientsTable(double[][] coefficients) {
		int k = 0, i = 0;
		for (k = 0; k < coefficients.length; k++) {
			for (i = 0; i < coefficients[k].length; i++) {
				if (i == k)
					continue;
				coeffFields[k][i].setText(Double.toString(coefficients[k][i]));
			}
		}

	}

	public boolean update() {
		int k = 0, i = 0;
		for (k = 0; k < coefficients.length; k++) {
			for (i = 0; i < coefficients[k].length; i++) {
				try {
					String cValue = coeffFields[k][i].getText();
					coefficients[k][i] = Double.valueOf(cValue).doubleValue();
					if (coefficients[k][i] < -1.0 || coefficients[k][i] > 1.0) {
						MessageUtils.displayError("In row " + (k + 1)
								+ ", column " + (i + 1) + ", the coefficient '"
								+ cValue
								+ "' is out of the range [-1.0, 1.0]. ");
						return false;
					}
				} catch (NumberFormatException e) {
					MessageUtils.displayError("In row " + (k + 1) + ", column "
							+ (i + 1) + ", the coefficient '"
							+ coeffFields[k][i].getText()
							+ "' is not a real number. ");
					return false;
				}
			}
		}
		return true;

	}

	public double[][] getCoefficients() {
		return coefficients;
	}

}
