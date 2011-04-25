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

import java.awt.Dimension;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToolBar;

import sim.ui.MainApplet;
import sim.ui.MainFrame;

public class MainToolBar extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2998205374766702118L;
	/**
	 * The main window
	 */
	private MainFrame parent;
	private MainApplet main;

	/**
	 * Constructor
	 * 
	 */
	public MainToolBar(MainFrame parent, MainApplet main) {
		this.parent = parent;
		this.main = main;
		this.initToolBar();
	}

	/**
	 * Initializes the tool bar.
	 * <p/>
	 * All actions are available from the tool bar.
	 */
	private void initToolBar() {

		super.add(new NewAction(parent, main));
		super.add(new OpenAction(parent, main));
		super.add(new SaveAction(main));
		super.add(new SaveAsAction(main));

		super.addSeparator(new Dimension(10, 0));

		super.add(new DefineCategoryAction());
		super.add(new DefineBehaviorNetworkAction());
		super.add(new StartSimulationAction(main));

		super.addSeparator(new Dimension(10, 0));

		super.add(new HelpAction(parent));
		JCheckBoxMenuItem jb = new JCheckBoxMenuItem(new ScheduleSaveAction(
				parent));
		jb.setPreferredSize(new Dimension(45, 28));
		jb.setMaximumSize(jb.getPreferredSize());
		jb.setMinimumSize(jb.getPreferredSize());
		super.add(jb);
		super.add(new ExitAction(main));
	}
}
