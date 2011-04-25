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
 * A border that makes a component look a bit hovering. This effect is used for
 * the main parts of the <code>ConfiguratorGUI</code>
 * 
 * @author Fasheng Qiu
 */
public class ShadowBorder extends AbstractBorder {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5989097059889267368L;

	/**
	 * Creates a new ShadowBorder.
	 */
	public ShadowBorder() {
	}

	/**
	 * Returns the insets used by this border.
	 * <p/>
	 * This border uses one pixel on the top and left side, and 4 pixels on the
	 * bottom and right side.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @return the insets object initialized to (1,1,4,4)
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 4, 4);
	}

	/**
	 * Paints this border with a one pixel wide line on the top and left side,
	 * and a fading shadow on the bottom and right side.
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
		int from = Color.GRAY.getRed();
		int to = c.getBackground().getRed();

		int change = (to - from) / 3;

		Color col1 = new Color(from, from, from);
		Color col2 = new Color(from + change, from + change, from + change);
		Color col3 = new Color(from + 2 * change, from + 2 * change, from + 2
				* change);

		g.setColor(col1);
		g.drawLine(x, y, x + width - 4, y);
		g.drawLine(x, y, x, y + height - 4);
		g.drawLine(x + width - 4, y + 1, x + width - 4, y + height - 3);

		g.setColor(col2);
		g.drawLine(x + width - 3, y + 1, x + width - 3, y + height - 3);
		g.setColor(col3);
		g.drawLine(x + width - 2, y + 2, x + width - 2, y + height - 3);

		g.setColor(col1);
		g.drawLine(x, y + height - 4, x + width - 4, y + height - 4);

		g.setColor(col2);
		g.drawLine(x + 1, y + height - 3, x + width - 4, y + height - 3);

		g.setColor(col3);
		g.drawLine(x + 2, y + height - 2, x + width - 3, y + height - 2);
	}
}
