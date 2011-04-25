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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sim.core.AppEngine;
import sim.model.entity.Category;
import sim.model.entity.Entity;

/**
 * System editor view
 * 
 * @author Pavel
 * @version 1.0
 */
public class SystemEditView extends AppView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4145349928869796715L;

	/**
	 * Icon path
	 */
	private static final String toolIconPath = "/sim/ui/images/tools.gif";

	/**
	 * View icon
	 */
	private ImageIcon viewIcon = null;

	/**
	 * Navigation panel for system entities
	 */
	private NavigationPanel navPanel = null;

	/**
	 * System editor panel
	 */
	private SystemEditorPanel systemPad = null;

	/**
	 * Application engine
	 */
	public AppEngine engineRef = null;

	public SystemEditView(AppEngine eng, NavigationPanel nPanel) {
		engineRef = eng;
		navPanel = nPanel;
		setLayout(new BorderLayout());
		JPanel systemEditor = new JPanel();
		add(systemEditor, BorderLayout.CENTER);
		viewIcon = new ImageIcon(engineRef.jrl.getImage(toolIconPath
				.substring(1)));
		systemPad = new SystemEditorPanel(this);
		systemEditor.setLayout(new GridBagLayout());
		systemEditor.add(systemPad, new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

	}

	/**
	 * Settings of simulation environment are changed. Repaint the environment.
	 */
	public void updateSystemEditorView() {
		((SystemEditorPanel) systemPad).updateSystemEditorImage();
	}

	/**
	 * Settings of display component are changed. Repaint the editor
	 */
	public void updateCategory(String categoryName, Category category) {
		((SystemEditorPanel) systemPad).categoryUpdated(categoryName, category);
	}

	/**
	 * The title of the system editor
	 */
	public String getTitle() {
		return new String("System Editor");
	}

	/**
	 * The view icon of this editor
	 */
	public Icon getViewIcon() {
		return viewIcon;
	}

	/**
	 * Rebuild the entities' tree due to category changed.
	 */
	public void rebuildTree(Entity entity) {
		navPanel.updateCategoryProperties(entity);
	}

	/**
	 * Add an entity into the system as well as the navigation panel
	 * 
	 * @param refEntity
	 *            The entity to add
	 */
	public void addEntity(Entity refEntity) {
		engineRef.addEntity(refEntity);
		navPanel.addNewNode(refEntity);
	}

	/**
	 * Remove an entity from the system as well as the navigation panel
	 * 
	 * @param entity
	 *            The entity to remove
	 */
	public void removeEntity(Entity entity) {
		navPanel.removeNode(entity);
		engineRef.removeEntity(entity);
		systemPad.updateSystemEditorImage();
	}

	/**
	 * 
	 * @return All available entities
	 */
	public List retrieveEntities() {
		return engineRef.getAvailableEntities();
	}

	/**
	 * Update the entity position from the navigation panel and the system
	 * engine
	 * 
	 * @param entity
	 *            The entity to update
	 */
	public void updateEntityPosition(Entity entity) {
		navPanel.updateEntityPosition(entity);
		engineRef.changeEntityInitialParameters(entity);
	}

	/**
	 * Update the named property of the target entity
	 * 
	 * @param entity
	 *            The entity to update
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	public void updateEntityProperty2(Entity entity, String name, String value) {
		navPanel.updateEntityProperty(entity, name, value);
	}

	/**
	 * Update the entity position
	 * 
	 * @param entity
	 *            The target entity to update
	 * @param x
	 *            The new x position
	 * @param y
	 *            The new y position
	 */
	public void updateEntityPositionFromNavPanel(Entity entity, int x, int y) {
		systemPad.updateEntityPosition(entity, x, y);
	}

	/**
	 * Update the named property of the target entity
	 * 
	 * @param entity
	 *            The entity to update
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	public void updateEntityProperty(Entity entity, String name, String value) {
		systemPad.updateEntityProperty(entity, name, value);
	}

	/**
	 * clear entities from the system pad
	 */
	public void clearAll() {
		systemPad.updateSystemEditorImage();
	}

}
