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
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * This component is a replacement for a JPanel with BorderLayout and supports
 * utility methods to remove parts of the panel.
 * 
 * @author Fasheng Qiu
 */
public class BorderPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5594396464836230843L;
	private JComponent northComponent;
	private JComponent southComponent;
	private JComponent centerComponent;
	private JComponent eastComponent;
	private JComponent westComponent;

	/**
	 * Constructs a new empty BorderPanel.
	 */
	public BorderPanel() {
		setLayout(new BorderLayout());

		northComponent = new JPanel();
		northComponent.setPreferredSize(new Dimension(0, 0));

		southComponent = new JPanel();
		southComponent.setPreferredSize(new Dimension(0, 0));

		centerComponent = new JPanel();
		centerComponent.setPreferredSize(new Dimension(0, 0));

		eastComponent = new JPanel();
		eastComponent.setPreferredSize(new Dimension(0, 0));

		westComponent = new JPanel();
		westComponent.setPreferredSize(new Dimension(0, 0));

		super.add(northComponent, BorderLayout.NORTH);
		super.add(southComponent, BorderLayout.SOUTH);
		super.add(centerComponent, BorderLayout.CENTER);
		super.add(eastComponent, BorderLayout.EAST);
		super.add(westComponent, BorderLayout.WEST);
	}

	/**
	 * Not supported operation. Use the setters instead.
	 * 
	 * @param comp
	 *            the component to be added
	 * @return the component argument
	 */
	public Component add(Component comp) {
		throw new RuntimeException("use the setter instead");
	}

	/**
	 * Not supported operation. Use the setters instead.
	 * 
	 * @param comp
	 *            the component to be added
	 * @param index
	 *            the position at which to insert the component, or
	 *            <code>-1</code> to append the component to the end
	 * @return the component <code>comp</code>
	 */
	public Component add(Component comp, int index) {
		throw new RuntimeException("use the setter instead");
	}

	/**
	 * Not supported operation. Use the setters instead.
	 * 
	 * @param comp
	 *            the component to be added
	 * @param constraints
	 *            an object expressing layout contraints for this component
	 */
	public void add(Component comp, Object constraints) {
		throw new RuntimeException("use the setter instead");
	}

	/**
	 * Not supported operation. Use the setters instead.
	 * 
	 * @param comp
	 *            the component to be added
	 * @param constraints
	 *            an object expressing layout contraints for this
	 * @param index
	 *            the position in the container's list at which to insert the
	 *            component; <code>-1</code> means insert at the end component
	 */
	public void add(Component comp, Object constraints, int index) {
		throw new RuntimeException("use the setter instead");
	}

	/**
	 * Not supported operation. Use the setters instead.
	 * 
	 * @param name
	 *            the name of the component
	 * @param comp
	 *            the component to be added
	 * @return comp
	 */
	public Component add(String name, Component comp) {
		throw new RuntimeException("use the setter instead");
	}

	/**
	 * Sets the north component of this container and correctly removes any
	 * previous component.
	 * 
	 * @param component
	 *            the component to be added
	 */
	public void setNorthComponent(JComponent component) {
		remove(northComponent);
		super.add(component, BorderLayout.NORTH);
		northComponent = component;
		revalidate();
		repaint();
	}

	/**
	 * Sets the south component of this container and correctly removes any
	 * previous component.
	 * 
	 * @param component
	 *            the component to be added
	 */
	public void setSouthComponent(JComponent component) {
		remove(southComponent);
		super.add(component, BorderLayout.SOUTH);
		southComponent = component;
		revalidate();
		repaint();
	}

	/**
	 * Sets the center component of this container and correctly removes any
	 * previous component.
	 * 
	 * @param component
	 *            the component to be added
	 */

	public void setCenterComponent(JComponent component) {
		remove(centerComponent);
		super.add(component, BorderLayout.CENTER);
		centerComponent = component;
		revalidate();
		repaint();
	}

	/**
	 * Sets the east component of this container and correctly removes any
	 * previous component.
	 * 
	 * @param component
	 *            the component to be added
	 */
	public void setEastComponent(JComponent component) {
		remove(eastComponent);
		super.add(component, BorderLayout.EAST);
		eastComponent = component;
		revalidate();
		repaint();
	}

	/**
	 * Sets the west component of this container and correctly removes any
	 * previous component.
	 * 
	 * @param component
	 *            the component to be added
	 */
	public void setWestComponent(JComponent component) {
		remove(westComponent);
		super.add(component, BorderLayout.WEST);
		westComponent = component;
		revalidate();
		repaint();
	}

}
