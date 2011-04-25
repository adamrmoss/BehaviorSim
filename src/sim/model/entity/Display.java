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

package sim.model.entity;

import java.awt.Image;

/**
 * The display component of entities and simulation environment. It is used to
 * set up the image, display width, display height and display direction.
 * 
 * @author Fasheng Qiu
 * 
 */
public class Display {

	/** The display direction of the image */
	private double direction = 0.0;

	/**
	 * The display width of the image, only suppose to set through the
	 * navigation tree
	 */
	private int width = 40;

	/**
	 * The display height of the image, only suppose to set through the
	 * navigation tree
	 */
	private int height = 40;

	/** The image to be displayed */
	private Image image = null;

	/**
	 * The shape of the display image
	 */
	private String shape = null;

	/**
	 * The absolute path of the display image
	 */
	private String imagePath = null;

	/**
	 * The relative path of the display image. It is the path that will be
	 * stored in the application configuration file.
	 * 
	 */
	private String relativeImagePath = null;

	/***
	 * Whether the entity is visible or not
	 */
	private boolean visible = true;

	/**
	 * Constructor
	 */
	public Display() {
	}

	/***
	 * Constructor
	 * 
	 * @param image
	 *            The display image
	 */
	public Display(Image image, String imagePath) {
		this.image = image;
		this.imagePath = imagePath;
	}

	/**
	 * Return a copy of this display component
	 * 
	 * @return
	 */
	public Display copy() {
		Display d = new Display(image, imagePath);
		d.setWidth(width);
		d.setHeight(height);
		d.setDirection(direction);
		d.setVisible(visible);
		d.setRelativeImagePath(relativeImagePath);
		return d;
	}

	/**
	 * @return the direction
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(double direction) {
		this.direction = direction;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the shape
	 */
	public String getShape() {
		return shape;
	}

	/**
	 * @param shape
	 *            the shape to set
	 */
	public void setShape(String shape) {
		this.shape = shape;
	}

	/**
	 * @return the relativeImagePath
	 */
	public String getRelativeImagePath() {
		return relativeImagePath;
	}

	/**
	 * @param relativeImagePath
	 *            the relativeImagePath to set
	 */
	public void setRelativeImagePath(String relativeImagePath) {
		this.relativeImagePath = relativeImagePath;
	}

}
