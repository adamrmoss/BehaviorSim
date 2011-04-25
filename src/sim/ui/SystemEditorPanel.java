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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import sim.core.AppEngine;
import sim.core.CategoryUpdateListener;
import sim.core.SimulationEnvironment;
import sim.model.entity.Category;
import sim.model.entity.Entity;
import sim.util.MessageUtils;
import sim.util.Point;

/**
 * System editor for managing system entities. When a category is defined, an
 * icon will show at the right side of this component. TO add an entity into the
 * system, simply drag and drop the corresponding icon into the simulation world
 * which is in the left side of this component.
 * 
 * @author Pavel, Fasheng Qiu
 */
public class SystemEditorPanel extends JComponent implements MouseListener,
		MouseMotionListener, ActionListener, CategoryUpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5880304992443325142L;

	/** Sea image */
	private Image seaImage = null;

	/** System editor image */
	private Image systemEditorImage = null;

	/** Image background */
	private Image imageBackground = null;

	/** Width and height margin of this component */
	private int widthMargin = 100;
	private int heightMargin = 40;

	/** Select string */
	private String selectString = "Select an object";

	/** Whether the mouse is active */
	private boolean mouseActive = false;

	/** The previous x and y position */
	private int prevX = 0;
	private int prevY = 0;

	/** Viewer of system editor */
	private SystemEditView parent = null;

	/** Category entries */
	private Vector toolEntities = new Vector(); // Entity categories

	/** Bounds of the component */
	private Rectangle seaBounds = new Rectangle(0, 0, 0, 0); // height and width
	// will be
	// adjusted

	/** Bounding rectangle of the moving entity */
	private Rectangle movingEntRect = null;

	/** The moved entity */
	private Entity movingEntity = null;

	/** To create a new entity? */
	private boolean creatingNew = false;

	/** Popup menu and menu item */
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem menuItemDelete = new JMenuItem("Remove This Entity");
	private JMenuItem menuItemID = new JMenuItem("");

	/** The entity to delete */
	private Entity entityToDelete = null;

	/**
	 * Constructor
	 * 
	 * @param view
	 *            The viewer container
	 */
	public SystemEditorPanel(SystemEditView view) {
		parent = view;
		parent.engineRef.registerCategoryUpdateListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		_getSeaImage();
		menuItemDelete.addActionListener(this);
		popupMenu.add(menuItemDelete);
		popupMenu.add(menuItemID);
	}

	/** Get the sea image and its bounds */
	private void _getSeaImage() {
		SimulationEnvironment se = AppEngine.getInstance()
				.getSimulationEnvironment();
		seaImage = se.getImage();
		seaBounds.width = se.getWidth();
		seaBounds.height = se.getHeight();
	}

	/**
	 * Return the preferred size of this panel
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * Return the minimum size of this panel
	 */
	public Dimension getMinimumSize() {
		return new Dimension(widthMargin + seaImage.getWidth(this),
				heightMargin + seaImage.getHeight(this));
	}

	/**
	 * Set up image
	 */
	public void addNotify() {
		super.addNotify();
		if (systemEditorImage == null) {
			systemEditorImage = createImage(widthMargin
					+ seaImage.getWidth(this), heightMargin
					+ seaImage.getHeight(this));
			paintSystemEditorImage();
			repaint();
		}
	}

	/**
	 * Paint this panel
	 */
	protected void paintComponent(Graphics g) {
		if (systemEditorImage != null) {
			g.drawImage(systemEditorImage, 0, 0, this);
		}
	}

	/**
	 * Repaint this panel
	 */
	public void updateSystemEditorImage() {
		paintSystemEditorImage();
		repaint();
	}

	/**
	 * Paint the entities
	 */
	private void paintSystemEditorImage() {
		// Simulation environment display component
		_getSeaImage();
		int width = seaBounds.width;
		int height = seaBounds.height;
		Graphics g = systemEditorImage.getGraphics();
		g.clearRect(0, 0, systemEditorImage.getWidth(this), systemEditorImage
				.getHeight(this));
		Color original = g.getColor();
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, 624, 580);
		g.setColor(original);
		g.drawImage(seaImage, 0, 0, width, height, this);
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, width, height);
		g.setColor(original);
		// g.setFont(AppResources.font);
		g.drawString(selectString, width + 8, 25); // locations of the string
		// and line are hard-coded
		g.drawLine(width + 7, 26, widthMargin + width + 30, 26);
		// Update the category positions first
		updatePositions();
		int offset = 0;
		for (int k = 0; k < toolEntities.size(); k++) {
			Entity entity = (Entity) toolEntities.get(k);
			Rectangle entRect = entity.getDetectRect();

			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(entRect.x, entRect.y, entRect.width, entRect.height);
			drawToolBounds(g, entRect, Color.WHITE, Color.DARK_GRAY);
			sim.core.AppEngine.getInstance().drawEntity(
					entity,
					g,
					entity.getInitialDisplayDirection(),
					new Point(entRect.x + entRect.width / 2, entRect.y
							+ entRect.height / 2), entity.getState());
			offset += entRect.height;
		}

		List seaEntities = parent.retrieveEntities();
		for (int m = 0; m < seaEntities.size(); m++) {
			Entity entity = (Entity) seaEntities.get(m);
			Rectangle entRect = entity.getDetectRect();
			if (entity.isVisible()) {
				sim.core.AppEngine.getInstance().drawEntity2(
						entity,
						g,
						entity.getInitialDisplayDirection(),
						new Point(entRect.x + entRect.width / 2, entRect.y + entRect.height / 2), 
						entity.getState());
			} else
				MessageUtils.debug(this, "paintSystemEditorImage",
						"Not drawing " + entity.getEntityType());
		}
		g.dispose();
	}

	/**
	 * Mouse pressed event handler
	 */
	public void mousePressed(MouseEvent e) {
		// handle right mouse button and left mouse button differently
		// e.getButton()
		if (!isEnabled())
			return;
		int x = e.getX();
		int y = e.getY();
		if (x > getMinimumSize().getWidth() || y > getMinimumSize().getHeight())
			return;

		synchronized (this) {

			// To create a new entity?
			mouseActive = false;
			for (int k = 0; k < toolEntities.size(); k++) {
				movingEntity = (Entity) toolEntities.get(k);
				movingEntRect = movingEntity.getDetectRect();
				if (movingEntRect.contains(x, y)) {
					mouseActive = true;
					creatingNew = true;
					break;
				}
			}

			// Not create a new entity, just move entities in the world
			if (creatingNew == false) {
				List seaEntities = parent.retrieveEntities();
				for (int m = 0; m < seaEntities.size(); m++) {
					Entity ent = (Entity) seaEntities.get(m);
					movingEntRect = ent.getDetectRect();
					if (movingEntRect.contains(x, y)) {
						movingEntity = ent;
						// make entity invisible while it is being moved
						ent.setVisible(false);
						paintSystemEditorImage();
						mouseActive = true;
						break;
					}
				}
			}

			// Selected an entity
			if (mouseActive) {
				x = e.getX() - movingEntity.getWidth() / 2;
				y = e.getY() - movingEntity.getHeight() / 2;
				prevX = x;
				prevY = y;
				Graphics g = systemEditorImage.getGraphics();
				if (creatingNew)
					drawToolBounds(g, movingEntRect, Color.DARK_GRAY,
							Color.WHITE);

				g.dispose();
				imageBackground = createImage(movingEntity.getWidth(),
						movingEntity.getHeight());
				g = imageBackground.getGraphics();
				g.drawImage(systemEditorImage, -x, -y, this);
				g.dispose();

			}

		}

	}

	/**
	 * Mouse released event handler
	 */
	public void mouseReleased(MouseEvent e) {
		if (movingEntity == null)
			return;
		int x = e.getX() - movingEntity.getWidth() / 2;
		int y = e.getY() - movingEntity.getHeight() / 2;
		if (mouseActive) {
			boolean dropOccured = false;
			mouseActive = false;
			Graphics g = systemEditorImage.getGraphics();

			g.translate(prevX, prevY);
			g.drawImage(imageBackground, 0, 0, this);
			g.translate(x - prevX, y - prevY);
			// check sea bounds
			if (seaBounds.contains(x, y)
					&& seaBounds.contains(x + movingEntity.getWidth(), y
							+ movingEntity.getHeight())) {
				g.drawImage(movingEntity.getImage(), 0, 0, movingEntity
						.getWidth(), movingEntity.getHeight(), this);
				dropOccured = true;
			}
			g.translate(-x, -y);
			if (creatingNew)
				drawToolBounds(g, movingEntRect, Color.WHITE, Color.DARK_GRAY);
			g.dispose();

			if (dropOccured) {
				if (creatingNew) {
					// position of the entity is its center
					Entity newEntity = null;
					int ep = determineEntityType(movingEntity);
					switch (ep) {
					case 4:// Dynamic entity
						try {
							newEntity = (Category) parent.engineRef.appManager.currentApp.dm
									.getCategory(((Category) movingEntity)
											.getEntityType());
						} catch (Exception ee) {
							MessageUtils.displayError(ee);
							return;
						}
						((Category) newEntity).init(this,
								((Category) movingEntity).getEntityType(),
								((Category) movingEntity).getImagePath());
						for (int i = 0; i < toolEntities.size(); i++) {
							if (toolEntities.get(i) instanceof Category) {
								if (((Category) toolEntities.get(i))
										.getEntityType().equals(
												((Category) movingEntity)
														.getEntityType())) {
									ep = i;
									break;
								}
							}
						}
						break;
					default:
						System.exit(-1);
						break;
					}
					newEntity.setPosition(x + movingEntity.getWidth() / 2, y
							+ movingEntity.getHeight() / 2);
					/** Mark the application as dirty */
					AppEngine.getInstance().setAppStatus(sim.core.App.DIRTY);
					parent.addEntity(newEntity);
					creatingNew = false;
				} else { // update position of the existing entity movingEntRect
					// - is the previous detect
					// rectangle of the moving entity
					Entity updateEntity = parent.engineRef
							.getEntityByDetectRectangle(movingEntRect);
					if (updateEntity != null) {
						// check sea bounds
						if (seaBounds.contains(x, y)
								&& seaBounds.contains(x
										+ movingEntity.getWidth(), y
										+ movingEntity.getHeight())) {
							// if(!parent.engineRef.entitiesOverlap(updateEntity,
							// expectedRect)){
							updateEntity.setPosition(x
									+ movingEntity.getWidth() / 2, y
									+ movingEntity.getHeight() / 2);
							
							
							System.out.println(updateEntity.getMyId() + "," +
									updateEntity.getPosition());
							
							
							parent.updateEntityPosition(updateEntity);
							/** Mark the application as dirty */
							AppEngine.getInstance().setAppStatus(
									sim.core.App.DIRTY);
						}
					}
					updateEntity.setVisible(true);
					paintSystemEditorImage();
				}
			} else { // no drop when moving existing entity
				if (!creatingNew) {
					Entity updateEntity = parent.engineRef
							.getEntityByDetectRectangle(movingEntRect);
					updateEntity.setVisible(true);
					paintSystemEditorImage();
				}
			}

		}

		repaint();
	}

	/**
	 * Mouse dragged event handler
	 */
	public void mouseDragged(MouseEvent e) {

		if (!isEnabled())
			return;
		if (mouseActive) {

			// center the image on the cursor
			int x = e.getX() - movingEntity.getWidth() / 2;
			int y = e.getY() - movingEntity.getHeight() / 2;

			synchronized (this) {

				Graphics gSystem = systemEditorImage.getGraphics();
				// gSystem.drawImage(imageToDrag, detectRefX, detectRefY, this);
				// gSystem.drawImage(fishImage, detectRefX + 6, heightMargin/2 +
				// 20, this);
				// Put the old background back
				gSystem.translate(prevX, prevY);

				gSystem.drawImage(imageBackground, 0, 0, this);

				// Move to the new position
				gSystem.translate(x - prevX, y - prevY);
				prevX = x;
				prevY = y;
				// Remember the new background
				Graphics gBackground = imageBackground.getGraphics();
				gBackground.drawImage(systemEditorImage, -prevX, -prevY, this);
				// Draw moving image

				gSystem.drawImage(movingEntity.getImage(), 0, 0, movingEntity
						.getWidth(), movingEntity.getHeight(), this);

				gSystem.dispose();
				gBackground.dispose();
				repaint();
			}
		}

	}

	/**
	 * Draw the bounds of a given rectangle. The moving entity (dragged by
	 * mouse, for example) can be drawn using this routine.
	 * 
	 * @param g
	 *            The graphics object
	 * @param rect
	 *            The rectangle to draw
	 * @param upper
	 *            The upper color
	 * @param lower
	 *            The lower color
	 */
	private void drawToolBounds(Graphics g, Rectangle rect, Color upper,
			Color lower) {
		g.setColor(upper);
		g.drawLine(rect.x - 1, rect.y - 1, rect.x + rect.width, rect.y - 1);
		g.drawLine(rect.x - 1, rect.y - 1, rect.x - 1, rect.y + rect.height);

		g.setColor(lower);
		g.drawLine(rect.x - 1, rect.y + rect.height + 1, rect.x + rect.width
				+ 1, rect.y + rect.height + 1);
		g.drawLine(rect.x + rect.width + 1, rect.y + rect.height + 1, rect.x
				+ rect.width + 1, rect.y - 1);

	}

	/**
	 * Return the type of the entity
	 * 
	 * @param entity
	 *            The entity
	 * @return The type
	 */
	private int determineEntityType(Entity entity) {
		int ep = 4; // Dynamic entity
		return ep;

	}

	/**
	 * Update the entity position
	 * 
	 * @param entity
	 *            The target entity
	 * @param x
	 *            The new x position
	 * @param y
	 *            The new y position
	 */
	public void updateEntityPosition(Entity entity, int x, int y) {
		if (entity == null)
			return;
		if (seaBounds.contains(x - entity.getWidth() / 2, y
				- entity.getHeight() / 2)
				&& seaBounds.contains(x + entity.getWidth() / 2, y
						+ entity.getHeight() / 2)) {
			entity.setPosition(x, y);
			parent.updateEntityPosition(entity);
			updateSystemEditorImage();
		}

	}

	/**
	 * <p>
	 * Update the property value of the specified dynamically created
	 * categories.
	 * </p>
	 * 
	 * <p>
	 * This includes updating the navigation panel and the instance of the
	 * category. Note that this method does not update the properties of the
	 * category itself.
	 * </p>
	 * 
	 * 
	 * @param entity
	 *            The entity whose property to be updated
	 * @param name
	 *            The property name
	 * @param value
	 *            The new value of the property
	 */
	public void updateEntityProperty(Entity entity, String name, String value) {
		if (entity == null)
			return;
		parent.updateEntityProperty2(entity, name, value);
		if (name.equals("iconPath")) {
			Category c = (Category) entity;
			c.init(this, c.getEntityType(), value);
			paintSystemEditorImage();
		}
	}

	/**
	 * Mouse clicking event handler
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			return;
		}
		int x = e.getX();
		int y = e.getY();
		if (x > getMinimumSize().getWidth() || y > getMinimumSize().getHeight()) {
			return;
		}

		List seaEntities = parent.retrieveEntities();
		for (int m = 0; m < seaEntities.size(); m++) {
			Entity ent = (Entity) seaEntities.get(m);
			Rectangle rect = ent.getDetectRect();
			if (rect.contains(x, y)) {
				entityToDelete = ent;
				
				StringBuffer sb = new StringBuffer();
				sb.append("ID: ").append(ent.getMyId());
				sb.append(", Position: (").append((int)ent.getPositionX());
				sb.append(", ").append((int)ent.getPositionY()).append(")");
				menuItemID.setText(sb.toString());

				popupMenu.show(e.getComponent(), e.getX(), e.getY());
				break;
			}
		}

	}

	/** Mouse entered event handler */
	public void mouseEntered(MouseEvent e) {
	}

	/** Mouse exited event handler */
	public void mouseExited(MouseEvent e) {
	}

	/** Mouse moved event handler */
	public void mouseMoved(MouseEvent e) {

		// Mouse position
		double x = e.getX(), y = e.getY();
		// Check the mouse move over which entity (would be in the world
		// or in the tool box)
		List seaEntities = parent.retrieveEntities();
		for (int m = 0; m < seaEntities.size(); m++) {
			Entity ent = (Entity) seaEntities.get(m);
			movingEntRect = ent.getDetectRect();
			if (movingEntRect.contains(x, y)) {
				tooltip(x, y, "Entity: " + ent.getDisplayName());
				return;
			}
		}

		for (int k = 0; k < toolEntities.size(); k++) {
			movingEntity = (Entity) toolEntities.get(k);
			movingEntRect = movingEntity.getDetectRect();
			if (movingEntRect.contains(x, y)) {
				tooltip(x, y, "Category: " + movingEntity.getEntityType());
				return;
			}
		}

		tooltip(x, y, null);

	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param tooltip
	 */
	public void tooltip(double x, double y, String tooltip) {
		if (tooltip == null)
			AppStatusbar.getInstance().changeFocusableInfo("");
		else
			AppStatusbar.getInstance().changeFocusableInfo(tooltip);
	}

	/** Action event handler */
	public void actionPerformed(ActionEvent e) {
		if (entityToDelete != null) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
					"Do you want to remove this entity?")) {
				parent.removeEntity(entityToDelete);
			}
		}
		entityToDelete = null;
	}

	/**
	 * Add a new category into the system editor panel. Before added, the new
	 * category is prepared its image which should be finished first
	 * 
	 * After that, add the new category into the category list and compute the
	 * detection point of these categories.
	 * 
	 * Lastly, draw the categories' image in the editor where the user can init
	 * category instances
	 * 
	 * @param newCategory
	 *            The category to be added
	 */
	public void categoryAdded(Category newCategory) {
		// Prepare the category image
		newCategory.init(this, newCategory.getEntityType(), newCategory
				.getImagePath());
		// Add the category to the category list
		this.toolEntities.add(newCategory);
		// Update category positions
		updatePositions();
		// Paint the categories
		this.updateSystemEditorImage();
	}

	/**
	 * Update the display position of each category rectangle
	 */
	private void updatePositions() {
		// Size of the simulation world
		int width = seaBounds.width;
		// Update the positions
		int yoffset = 0;
		for (int k = 0; k < toolEntities.size(); k++) {
			Entity entity = (Entity) toolEntities.get(k);
			int h = (k > 0) ? (((Entity) toolEntities.get(k - 1))
					.getDetectRect().height + 4) : 0;
			yoffset += h;
			entity.setDetectPoint(40 + width, 50 + yoffset); // locations of the
			// tool images
			// are hard-coded
		}
	}

	/**
	 * Delete the specified category, from the category entries. Also, the panel
	 * is redrawn according to this change.
	 * 
	 * @param categoryName
	 *            name of the category to delete
	 */
	public void categoryDeleted(String categoryName) {
		for (int k = 0; k < toolEntities.size(); k++) {
			Category entity = (Category) toolEntities.get(k);
			if (entity.getEntityType().trim().equalsIgnoreCase(categoryName)) {
				toolEntities.remove(k);
			}
		}
		this.updateSystemEditorImage();
	}

	// newCategory: The category after updated
	/**
	 * Update the definition of an existing category
	 * 
	 * @param oldCategoryName
	 *            The name of the existing category
	 * @param newCategory
	 *            The name of the new category
	 */
	public void categoryUpdated(String oldCategoryName, Category newCategory) {
		for (int k = 0; k < toolEntities.size(); k++) {
			Category entity = (Category) toolEntities.get(k);
			if (entity.getEntityType().trim().equalsIgnoreCase(oldCategoryName)) {
				toolEntities.remove(k);
			}
		}
		this.categoryAdded(newCategory);
		parent.rebuildTree(newCategory);
	}

	/**
	 * A new entity is added. The navigation panel is updated.
	 */
	public void entityAdded(Entity newEntity) {
		parent.addEntity(newEntity);
		this.updateSystemEditorImage();
	}

	/**
	 * Update the simulation world
	 */
	public void entityUpdated() {
		this.updateSystemEditorImage();
	}

}