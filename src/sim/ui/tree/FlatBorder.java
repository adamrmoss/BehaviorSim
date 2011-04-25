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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A border implementation that is used by the FlatButton.
 * 
 * @author Fasheng Qiu
 */
public class FlatBorder extends AbstractBorder {
	/**
	 * 
	 */
	private static final long serialVersionUID = -413599383931025597L;
	private boolean lowered;

	/**
	 * Constructs a new raised border.
	 */
	public FlatBorder() {
	}

	/**
	 * Constructs a new border.
	 * 
	 * @param lowered
	 *            a lowered border if true, a raised otherwise
	 */
	public FlatBorder(boolean lowered) {
		this.lowered = lowered;
	}

	/**
	 * Returns the Insets of this border.
	 * <p/>
	 * This border always returns a one pixel wide Inset.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @return the Insets initilaized to 1
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 1, 1);
	}

	/**
	 * Paints this border with a one pixel wide line on all sides.
	 * 
	 * @param c
	 *            the component for which this border is being painted
	 * @param g
	 *            the paint graphics
	 * @param x
	 *            the x position of the painted border
	 * @param y
	 *            the y position of the painted border
	 * @param width
	 *            the width of the painted border
	 * @param height
	 *            the height of the painted border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		if (lowered) {
			g.setColor(Color.DARK_GRAY);
			g.drawLine(x, y, x + width - 1, y);
			g.drawLine(x, y, x, y + height - 1);
			g.setColor(Color.white);
			g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
			g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
		} else {
			g.setColor(Color.white);
			g.drawLine(x, y, x + width - 1, y);
			g.drawLine(x, y, x, y + height - 1);
			g.setColor(Color.DARK_GRAY);
			g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
			g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
		}
	}
}