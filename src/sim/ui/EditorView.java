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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

import sim.core.AppEngine;
import sim.core.SimulationListener;
import sim.model.entity.Category;
import sim.model.entity.Entity;

/**
 * View of System editor. It includes a left-side navigation panel and a
 * right-side app views
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class EditorView extends AppView implements SimulationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9106097929807432550L;

	/** Icon of the editor view */
	private static final String iconPath = "/sim/ui/images/editor.gif";

	/** Image icon object */
	private ImageIcon viewIcon = null;

	/** Navigation panel in the left side of the view */
	private NavigationPanel navPanel = null;

	/** Application engine */
	private AppEngine engineRef = AppEngine.getInstance();

	/** Tabs of application views */
	private AppView views[] = new AppView[2];

	/**
	 * Constructor
	 * 
	 * @param eng
	 *            Application engine
	 * @throws Exception
	 *             If any exception occurs
	 */
	public EditorView( ) throws Exception {

		// Icons
		viewIcon = new ImageIcon(engineRef.jrl.getImage(iconPath));

		// Left navigation panel
		navPanel = new NavigationPanel(engineRef, this);
		engineRef.registerNavigationPanel(navPanel);

		Border b = BorderFactory.createEmptyBorder(0, 5, 5, 5);
		setBorder(b);
		setLayout(new BorderLayout());

		add(navPanel, BorderLayout.WEST);

		// Tab of system editor and behavior network
		views[0] = new SystemEditView(engineRef, navPanel);
		add(views[0], BorderLayout.CENTER);

	}

	/**
	 * Settings of simulation environment are changed. Repaint the environment.
	 */
	public void updateSystemEditorView() {
		((SystemEditView) views[0]).updateSystemEditorView();
	}

	/**
	 * Settings of display component are changed. Repaint the editor
	 */
	public void updateCategory(String categoryName, Category category) {
		((SystemEditView) views[0]).updateCategory(categoryName, category);
	}

	/** Return the title of this editor view */
	public String getTitle() {
		return new String("Editor");
	}

	/** Return the icon of this view */
	public Icon getViewIcon() {
		return viewIcon;
	}

	/** Return the behavior view */
	public BehaviorView getBehaviorView() {
		return (BehaviorView) views[1];
	}

	/** Return the system editor view */
	public SystemEditView getSystemEditView() {
		return (SystemEditView) views[0];
	}

	/**
	 * Update the entity position
	 * 
	 * @param entity
	 *            The entity to update
	 * @param x
	 *            The new x position
	 * @param y
	 *            The new y position
	 */
	public void updateEntityPosition(Entity entity, int x, int y) {
		((SystemEditView) views[0]).updateEntityPositionFromNavPanel(entity, x,
				y);
	}

	/**
	 * <p>
	 * Update the dynamically created category's property
	 * </p>
	 * 
	 * <p>
	 * If the property is "iconPath", the picture of the entity is changed.
	 * Hence, the entity should be re-init.
	 * </p>
	 * 
	 * @param entity
	 *            The entity whose property value is to be set
	 * @param name
	 *            The property name
	 * @param value
	 *            The new value
	 */
	public void updateEntityProperty(Entity entity, String name, String value) {
		((SystemEditView) views[0]).updateEntityProperty(entity, name, value);
	}
	
	/**
	 * Simulation stopped event. 
	 * @param time Current time when the simulation stopped
	 */
	public void simulationStopped(int time)
	{
		
		// Simulation stopped, update position of all entities in repository
		engineRef.system.updatePositionOfEntities(time);
		
		// Update position in navigation tree
		engineRef.refreshPositionsInNavigationTree();
		
	}

	/**
	 * Refresh the view
	 */
	public void refresh() {

		// Refresh parents
		super.refresh();

		// Reflect the new entity positions
		updateSystemEditorView();

	}

	/**
	 * Clear the view
	 */
	public void clearAll() {
		navPanel.removeAllEntityNodes();
		views[0].clearAll();
	}

	/**
	 * Clear the view of the specified category
	 * 
	 * @param catName
	 *            Name of the category entity to remove
	 */
	public void clear(String catName) {
		// Remove all entities of the category
		java.util.List all = engineRef.system.getEntityByCategoryName(catName);
		for (int i = 0; i < all.size(); i++) {
			navPanel.removeEntity(((Category) all.get(i)));
		}
		// Repaint the system editor view
		views[0].clearAll();
	}

	/**
	 * Reload all information of this view
	 */
	public void loadAll() {
		navPanel.removeAllEntityNodes();
		navPanel.addAllEntities();
		views[0].clearAll();
	}
}
