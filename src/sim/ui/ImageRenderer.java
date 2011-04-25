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
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.util.HashMap;

import javax.swing.UIManager;

import sim.model.behavior.Behavior;

/**
 * <p>
 * Title: Crayfish simulation application
 * </p>
 * 
 * <p>
 * Description: Simulation of the crayfish behavior
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Pavel
 * @version 1.0
 */
public class ImageRenderer {

	/** Images */
	Image imageF = null;
	Image imageWithBackground = null;
	Image imageOrig = null;
	Image imageBackground = null;

	/** Media tracker */
	Component component = null;

	/** Distance */
	int stdDistance = 61;

	/** Index */
	int cX = 0;
	int cY = 0;

	/** Images map */
	HashMap imagesMap = new HashMap();
	HashMap imagesBgrMap = new HashMap();

	/**
	 * Transparent filter
	 * 
	 */
	class OpaqueFilter extends RGBImageFilter {
		int replacePixel = 0xFF00FF00;

		public OpaqueFilter() {
			canFilterIndexColorModel = false;
		}

		public int filterRGB(int x, int y, int rgb) {
			// System.out.println(x + "   " + y + "   " + rgb);
			// find rgb of the upper left corner pixel
			if (x == 0 && y == 0)
				replacePixel = rgb;
			// if rgb of the pixel matches rgb of the upper left corner pixel
			// make it opaque
			/* if (replacePixel == rgb) */
			if (((cX - x) * (cX - x) + (cY - y) * (cY - y)) > 18.5 * 18.5)
				rgb = rgb & 0x00FFFFFF; // absence of alpha implies
			// opaque pixel
			return rgb;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param c
	 *            The media tracker
	 */
	public ImageRenderer(Component c) {
		component = c;
	}

	/**
	 * Prepare images based on the given image
	 * 
	 * @param im
	 *            The original image
	 */
	public void prepareImage(Image im) {
		imageOrig = im;
		MediaTracker tracker = new MediaTracker(component);
		try {
			tracker.addImage(im, 0);
			tracker.waitForAll();
		} catch (InterruptedException e) {
		}

		stdDistance = im.getWidth(component);
		cX = cY = stdDistance / 2;
		ImageFilter filter = new OpaqueFilter();
		FilteredImageSource filteredImage = new FilteredImageSource(im
				.getSource(), filter);
		im = Toolkit.getDefaultToolkit().createImage(filteredImage);
		try {
			tracker.addImage(im, 0);
			tracker.waitForAll();
		} catch (InterruptedException e) {
		}

		imageF = new BufferedImage(stdDistance, stdDistance,
				BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = imageF.getGraphics();
		graphics.drawImage(im, 0, 0, component);
		graphics.dispose();
		imageBackground = new BufferedImage(stdDistance, stdDistance,
				BufferedImage.TYPE_INT_ARGB);// component.createImage(stdDistance,
		// stdDistance);
		Graphics gBrg = imageBackground.getGraphics();
		gBrg.setColor(UIManager.getColor("Label.background"));
		gBrg.fillRect(0, 0, stdDistance, stdDistance);
		gBrg.dispose();

	}

	/**
	 * Load the image background
	 * 
	 * @param bgrImage
	 *            The original image
	 */
	public void setImageWithBackground(Image bgrImage) {
		MediaTracker tracker = new MediaTracker(component);
		try {
			tracker.addImage(bgrImage, 0);
			tracker.waitForAll();
		} catch (InterruptedException e) {
		}
		imageBackground = bgrImage;
		Image im = new BufferedImage(stdDistance, stdDistance,
				BufferedImage.TYPE_INT_ARGB);// (new
		// JLabel()).createImage(stdDistance,
		// stdDistance);
		Graphics g = im.getGraphics();
		g.drawImage(bgrImage, 0, 0, component);
		g.drawImage(imageF, 0, 0, component);
		g.dispose();
		imageWithBackground = im;
	}

	/**
	 * 
	 * @return filtered image
	 */
	public Image getFilteredImage() {
		return imageF;
	}

	/**
	 * Draw a string in the image
	 * 
	 * @param im
	 *            The image
	 * @param s
	 *            The string to render
	 */
	public void setImageString(Image im, String s) {
		Graphics graphics = im.getGraphics();
		Font f = new Font("Times New Roman", Font.BOLD, 10);
		graphics.setFont(f);
		graphics.setColor(Color.BLACK);
		FontMetrics metrics = graphics.getFontMetrics();
		int strWidth = metrics.stringWidth(s);
		while (strWidth > 35) {
			s = s.substring(0, s.length() - 1);
			strWidth = metrics.stringWidth(s);
		}
		graphics.drawString(s, (stdDistance - strWidth) / 2, (stdDistance) - 2);
		graphics.dispose();
	}

	/**
	 * 
	 * @return The image with background
	 */
	public Image getImageWithBackground() {
		return imageWithBackground;
	}

	/**
	 * 
	 * @return The background image
	 */
	public Image getBackgroundImage() {
		return imageBackground;
	}

	/**
	 * 
	 * @return The std distance
	 */
	public int stdDistance() {
		return stdDistance;
	}

	/**
	 * Prepare the behavior image
	 * 
	 * @param behavior
	 *            The target behavior
	 */
	public void addBehaviorImage(Behavior behavior) {

		Image im = new BufferedImage(stdDistance, stdDistance,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = im.getGraphics();
		g.drawImage(imageF, 0, 0, component);
		g.dispose();
		setImageString(im, behavior.getBehaviorName());

		imagesMap.put(new Integer(behavior.getMyId()), im);
		im = new BufferedImage(stdDistance, stdDistance,
				BufferedImage.TYPE_INT_ARGB);// component.createImage(stdDistance,
		// stdDistance);
		g = im.getGraphics();
		g.drawImage(imageBackground, 0, 0, component);
		g.drawImage(imageF, 0, 0, component);
		g.dispose();
		setImageString(im, behavior.getBehaviorName());
		imagesBgrMap.put(new Integer(behavior.getMyId()), im);

	}

	/**
	 * Get the filtered behavior image
	 * 
	 * @param behavior
	 *            The target behavior
	 * @return The image
	 */
	public Image getBehaviorFilteredImage(Behavior behavior) {
		Image im = (Image) imagesMap.get(new Integer(behavior.getMyId()));
		if (im == null)
			im = imageBackground;
		return im;
	}

	/**
	 * Return the image with background
	 * 
	 * @param behavior
	 *            The target behavior
	 * @return The image with background
	 */
	public Image getBehaviorWithBackgroundImage(Behavior behavior) {
		Image im = (Image) imagesBgrMap.get(new Integer(behavior.getMyId()));
		if (im == null)
			im = imageBackground;
		return im;
	}

	/**
	 * Whether the image with background image exists.
	 * 
	 * @param behavior
	 *            The target behavior
	 * @return Whether has background image
	 */
	public boolean behaviorWithBackgroundImageExists(Behavior behavior) {
		boolean exists = false;
		Image im = (Image) imagesBgrMap.get(new Integer(behavior.getMyId()));
		if (im != null)
			exists = true;
		return exists;
	}

}
