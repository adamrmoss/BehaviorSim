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

package sim.ui.menus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import sim.core.AppEngine;
import sim.model.entity.BNCategory;
import sim.model.entity.Entity;
import sim.ui.panels.BehaviorNetworkDefinePanel;
import sim.ui.panels.PanelDialog;
import sim.util.GUIUtils;
import sim.util.MessageUtils;

/**
 * Define a new category in the system.
 * 
 * @author Fasheng Qiu
 * 
 */
class DefineBehaviorNetworkAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -226705877909713582L;

	/**
	 * Creates the DefineBehaviorNetworkAction.
	 */
	public DefineBehaviorNetworkAction() {
		super("Define Entity Dynamics", GUIUtils
				.getImageIconForObject(GUIUtils.MENU_BEHAVIORNETWORK_IMAGE));
		super.putValue(Action.SHORT_DESCRIPTION,
				"Create or modify entity dynamics");
	}

	/**
	 * Executes the action.
	 * 
	 * @param e
	 *            the passed event
	 */
	public void actionPerformed(ActionEvent e) {
		defineBehaviorNetwork();
	}

	/**
	 * Show the window where the behavior network is defined for the selected
	 * entity in the navigation panel.
	 */
	private void defineBehaviorNetwork() {
		// Get the selected entity first
		AppEngine engineRef = AppEngine.getInstance();
		Entity entity = engineRef.getSelectedEntityInNavigationPanel();
		if (entity == null) {
			MessageUtils
					.displayWarning("Please select an entity from the navigation tree first. \n Or configure entities in the system editor.");
			return;
		}
		if (!(entity instanceof BNCategory)) {
			MessageUtils
					.displayWarning("The entity does not have a behavior network.");
			return;
		}

		BNCategory bn = (BNCategory) entity;
		int inx = bn.getActionSelectionMechanismIndex();
		if (inx == sim.model.behavior.BehaviorNetwork.DYNAMICS) {
			// Show system dynamics editor
			sim.ui.DefineGeneralDyanmicsPanel p = new sim.ui.DefineGeneralDyanmicsPanel(
					(BNCategory) entity);
			PanelDialog pd = new PanelDialog(sim.ui.MainFrame.getInstance(), p);
			pd.setTitle("Define general dyanmics for entity '"
					+ entity.getDisplayName() + "'");
			pd.setSize(pd.getSize().width - 200, pd.getSize().height - 200);
			pd.setModal(true);
			pd.show();
			pd.dispose();
		} else {
			// Show the behavior network definition panel
			PanelDialog pd = new PanelDialog(sim.ui.MainFrame.getInstance(),
					new BehaviorNetworkDefinePanel(engineRef.navPanel,
							(BNCategory) entity));
			pd.setTitle("Define behavior network for entity '"
					+ entity.getDisplayName() + "'");
			pd.setSize(pd.getSize().width, pd.getSize().height + 100);
			pd.setModal(true);
			pd.show();
			pd.dispose();
		}
	}
}
