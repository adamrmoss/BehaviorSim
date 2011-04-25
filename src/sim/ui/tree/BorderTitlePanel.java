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
import java.awt.Color;

/**
 * A JPanel that is displayed with a JLabel as title and a gradient as a
 * background of the title. This is used as main grouping component in the GUI.
 * <p/>
 * The palette, tree, preview and the configuration panel are displayed in this
 * type of panel.
 * <p/>
 * It also supports easy removal of components inside the panel.
 * 
 * @author Fasheng Qiu
 */
public class BorderTitlePanel extends BorderPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 591959407540496289L;
	private GradientTitlePanel gradientTitlePanel;

	/**
	 * Creates a new BorderTitlePanel with default settings applied.
	 * <p/>
	 * The title String is set to an empty label and added as the panels north
	 * component.
	 */
	public BorderTitlePanel() {
		setLayout(new BorderLayout());

		gradientTitlePanel = new GradientTitlePanel();
		gradientTitlePanel.setForeground(Color.WHITE);
		gradientTitlePanel.setBorder(sim.util.GUIUtils.getTitleLabelBorder());

		setNorthComponent(gradientTitlePanel);
	}

	/**
	 * Returns the GradientTitlePanel used to paint the title.
	 * 
	 * @return the gradientTitlePanel
	 */
	public GradientTitlePanel getGradientTitlePanel() {
		return gradientTitlePanel;
	}

}
