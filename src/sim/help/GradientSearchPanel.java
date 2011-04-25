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

package sim.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A Panel used to display the search panel.
 * <p/>
 * <p/>
 * It uses a gradient with user defineable colors as background.
 * 
 * @author Fasheng Qiu
 */
public class GradientSearchPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6073138506217811075L;
	private JPanel componentPanel;
	private Color[] gradientColors;

	/**
	 * Creates a new GradientTitlePanel with a gradient from DARK_GREY to
	 * LIGHT_GREY. The default title string is set to a one char width
	 * whitespace. This results in a correctly calculated preferred size of the
	 * title label.
	 */
	public GradientSearchPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);

		gradientColors = new Color[] { Color.LIGHT_GRAY, Color.LIGHT_GRAY };

		componentPanel = new JPanel(new GridLayout(1, 0));

		add(componentPanel, BorderLayout.WEST);
	}

	/**
	 * Adds a component to the title components.
	 * 
	 * @param component
	 *            the component to add
	 */
	public void addTitleComponent(JComponent component) {
		componentPanel.add(component);
	}

	/**
	 * Removes a component from the title components.
	 * 
	 * @param component
	 *            the component to remove
	 */
	public void removeTitleComponent(JComponent component) {
		componentPanel.remove(component);
	}

	/**
	 * Sets the colors used as gradient.
	 * <p/>
	 * <p/>
	 * The first color in the array is used as the leftmost color in the
	 * gradient.
	 * 
	 * @param gradientColors
	 *            the colors to use as gradient
	 */
	public void setGradientColors(Color[] gradientColors) {
		if (gradientColors == null || gradientColors.length == 0) {
			throw new IllegalArgumentException(
					"gradientColors must be array with more than zero elements");
		}
		if (gradientColors.length == 1) {
			this.gradientColors = new Color[] { gradientColors[0],
					gradientColors[0] };
		}
		this.gradientColors = new Color[gradientColors.length];
		System.arraycopy(gradientColors, 0, this.gradientColors, 0,
				gradientColors.length);
	}

	/**
	 * First paints the gradient, and delegates the call to its children.
	 * <p/>
	 * Usually you should use components that do not draw their background on
	 * their own.
	 * 
	 * @param g
	 *            the GraphicsContext to paint on
	 * @see JComponent#setOpaque(boolean)
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g;

		int widthIncrement = getWidth() / (gradientColors.length - 1);

		for (int i = 0; i < gradientColors.length - 1; i++) {
			GradientPaint gradientPaint = new GradientPaint(new Point2D.Double(
					widthIncrement * i, 0), gradientColors[i],
					new Point2D.Double((i + 1) * widthIncrement, 0),
					gradientColors[i + 1]);
			graphics2D.setPaint(gradientPaint);
			graphics2D.fillRect(i * widthIncrement, 0, widthIncrement,
					getHeight());
		}

		super.paintComponent(g);
	}

}
