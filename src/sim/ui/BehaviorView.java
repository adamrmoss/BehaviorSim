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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sim.core.AppEngine;
import sim.model.behavior.Behavior;
import sim.model.behavior.Edge;
import sim.model.entity.BNCategory;
import sim.ui.panels.BehaviorNetworkListener;

/**
 * Behavior network viewer. It is used to display behavior network and
 * coefficients/weights definition panel.
 * 
 * @author Fasheng Qiu
 * 
 */
public class BehaviorView extends AppView /* implements ActionListener */{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1026428325340749694L;

	// Behavior network panel
	private BehaviorNetPad pad = null;

	// Coefficient dialog
	private JPanel dCoeffDlg = null;

	// Application engine
	private AppEngine engineRef = null;

	// View icon
	private ImageIcon viewIcon = null;

	// The entity
	private BNCategory entity = null;

	public BehaviorView(AppEngine eng, BNCategory e) throws Exception {
		entity = e;
		engineRef = eng;
		constructBehaviorNetworkAndCoefficientsPanel();
	}

	/** Set behavior network listener */
	public void setBehaviorNetworkListener(BehaviorNetworkListener l) {
		pad.setListener(l);
	}

	/** A new behavior is defined, add it to the network */
	public void behaviorDefined(BNCategory bn, int id) {
		pad.getImageRenderer().addBehaviorImage(engineRef.getBehavior(bn, id));
		/*
		 * if (tul != null) tul.subTreeChanged();
		 */
	}

	/**
	 * Construct the behavior network panel which contains the network and
	 * coefficients panel
	 */
	public void constructBehaviorNetworkAndCoefficientsPanel() throws Exception {

		/** Get parameters */
		List behaviorList = entity.getBehaviorNetwork().getBehaviorList();
		int ids[] = new int[behaviorList.size()];
		for (int k = 0; k < behaviorList.size(); k++)
			ids[k] = ((Behavior) behaviorList.get(k)).getMyId();
		String names[] = new String[behaviorList.size()];
		for (int k = 0; k < behaviorList.size(); k++)
			names[k] = ((Behavior) behaviorList.get(k)).getBehaviorName();
		
		boolean mutual = entity.isMutualInhibitionMechanism();
		if (!mutual) {
			double[] weights = new double[ids.length];
			for (int k = 0; k < behaviorList.size(); k++)
				weights[k] = ((Behavior) behaviorList.get(k)).getWeight();
			dCoeffDlg = new DefineWeightsPanel(this, weights, ids, names);

			pad = new BehaviorNetPad2(entity.getBehaviorNetwork());
		} else {
			pad = new BehaviorNetPad(entity.getBehaviorNetwork());
			Edge edges[] = pad.currentNetwork().getEdgesGivenBehaviors(ids);
			double coefficients[][] = new double[ids.length][ids.length];
			for (int m = 0; m < edges.length; m++) {
				Behavior fB = edges[m].fromB();
				Behavior tB = edges[m].toB();
				int f, t;
				for (f = 0; f < ids.length; f++) {
					if (fB.getMyId() == (ids[f])) {
						for (t = 0; t < ids.length; t++)
							if (tB.getMyId() == (ids[t])) {
								coefficients[f][t] = edges[m].inhibitionFT();
								coefficients[t][f] = edges[m].inhibitionTF();
								break;
							}
						break;
					}
				}

			}
			dCoeffDlg = new DefineCoefficientsPanel(this, coefficients, ids, names);

		}
		pad.setApplicationEngine(engineRef);

		/** Construct the panel */
		JPanel interactPanel = this;
		interactPanel.setLayout(new GridBagLayout());

		JPanel padPanel = new JPanel();
		padPanel.setPreferredSize(pad.getSize());
		pad.setAlignmentX(Component.LEFT_ALIGNMENT);
		pad.setAlignmentY(Component.TOP_ALIGNMENT);
		padPanel.add(pad);

		padPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		dCoeffDlg.setAlignmentX(Component.LEFT_ALIGNMENT);

		interactPanel.add(padPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
				100.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		interactPanel.add(dCoeffDlg, new GridBagConstraints(0, 1, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 0, 0, 0), 0, 0));
	}

	/**
	 * Add the specified behaviors into the network, including: 1) Update the
	 * behavior network tree (in behavior network definition panel) -- Removed
	 * 2) Update the system navigation tree -- Removed 3) Update the behavior
	 * network (in behavior network definition panel)
	 * 
	 * @param ids
	 *            ID of behaviors to add
	 */
	public void behaviorAddedToNetwork(int ids[]) throws Exception {
		engineRef.behaviorAddedToNetwork(entity, ids);
		/* if (tul != null) tul.treeChanged(); */
	}

	/**
	 * Update the weights of each behavior.
	 * 
	 * @param weights
	 *            Behavior weights
	 * @param ids
	 *            The behavior id array
	 */
	public void updateWeights(double weights[], int ids[]) {

		// Update weights
		engineRef.updateWeights(entity.getBehaviorNetwork(), weights, ids);

	}

	/**
	 * Update the coefficients in the behavior network
	 * 
	 * @param coefficients
	 *            The new coefficients to update
	 * @param ids
	 *            The behavior IDs
	 */
	public void updateCoefficients(double coefficients[][], int ids[]) {

		// Update coefficients
		engineRef.updateCoefficients(entity.getBehaviorNetwork(), coefficients,
				ids);

		// Update the behavior network
		pad.updateBehaviorNetwork(entity.getBehaviorNetwork());

	}

	/** The title of this panel */
	public String getTitle() {
		return new String("Behavior Network Editor");
	}

	/** The image icon used */
	public Icon getViewIcon() {
		return viewIcon;
	}

	/** Refresh the behavior network */
	public void refreshBehaviorNetwork(boolean show) {
		if (show) {
			pad.addNotify();
			pad.updateBehaviorNetwork(entity.getBehaviorNetwork());
			boolean mutual = entity.isMutualInhibitionMechanism();
			if (mutual) {
				List behaviorList = entity.getBehaviorNetwork()
						.getBehaviorList();
				int ids[] = new int[behaviorList.size()];
				for (int k = 0; k < behaviorList.size(); k++)
					ids[k] = ((Behavior) behaviorList.get(k))
							.getMyId();
				Edge edges[] = pad.currentNetwork().getEdgesGivenBehaviors(
						ids);
				double coefficients[][] = new double[ids.length][ids.length];
				for (int m = 0; m < edges.length; m++) {
					Behavior fB = edges[m].fromB();
					Behavior tB = edges[m].toB();
					int f, t;
					for (f = 0; f < ids.length; f++) {
						if (fB.getMyId() == (ids[f])) {
							for (t = 0; t < ids.length; t++)
								if (tB.getMyId() == (ids[t])) {
									coefficients[f][t] = edges[m]
											.inhibitionFT();
									coefficients[t][f] = edges[m]
											.inhibitionTF();
									break;
								}
							break;
						}
					}

				}
				((DefineCoefficientsPanel) dCoeffDlg)
						.refreshCoeffientsTable(coefficients);
			}

		} else {
			pad.updateBehaviorNetwork(null);
		}
	}

	/**
	 * 
	 * @return The target entity
	 */
	public BNCategory getEntity() {
		return entity;
	}

	/**
	 * Clear the behavior network
	 */
	public void clearAll() {
		refreshBehaviorNetwork(false);
	}
}
