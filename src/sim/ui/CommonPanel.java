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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * Basic panel based on the <code>SpringLayout</code>.
 * 
 * 
 * @author Fasheng Qiu
 * @since 03/04/2009
 * 
 */
public abstract class CommonPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6602170632074516747L;

	/** The layout used for this panel */
	private SpringLayout sl = new SpringLayout();

	/** The last component that is added into the panel */
	private Component last = this;

	/** The next-to-last component that is added into the panel */
	private Component nextToLast = this;

	/** The next-to-last line component */
	private Component nextToLastLine = this;

	/** Constructor */
	public CommonPanel() {

		/** Setup the layout of this panel */
		setLayout(sl);
		/** Enable windows event */
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		/** Setup location and size */
		Dimension frameSize = getPreferredSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		setLocation(((screenSize.width - frameSize.width) / 5),
				((screenSize.height - frameSize.height) / 5));
	}

	/**
	 * Add the specified component into this panel and setup the layout.
	 * 
	 * @param c
	 *            The component to add
	 */
	protected void addToNewLine(Component c) {
		/** Add component */
		add(c);
		/** Constraints */
		sl.putConstraint(SpringLayout.WEST, c, 15, SpringLayout.WEST, this);
		if (last == this) {
			/** The first new line */
			sl.putConstraint(SpringLayout.NORTH, c, 20, SpringLayout.NORTH,
					last);
		} else {
			/** Second line and further */
			sl.putConstraint(SpringLayout.NORTH, c, 20, SpringLayout.SOUTH,
					last);
		}
		/** Save as the last component */
		nextToLastLine = last;
		nextToLast = last;
		last = c;
	}

	/**
	 * Add the specified component into this panel and setup the layout.
	 * 
	 * <p>
	 * The component will be added to the end of the last already added
	 * component.
	 * </p>
	 * 
	 * @param c
	 *            The component to add
	 */
	protected void addToEnd(Component c) {
		/** Add component */
		add(c);
		/** Constraints */
		sl.putConstraint(SpringLayout.WEST, c, 15, SpringLayout.EAST, last);
		if (nextToLast == this) {
			/** The first new line */
			sl.putConstraint(SpringLayout.NORTH, c, 20, SpringLayout.NORTH,
					nextToLast);
		} else {
			/** Second line and further */
			sl.putConstraint(SpringLayout.NORTH, c, 20, SpringLayout.SOUTH,
					nextToLast);
		}
		/** Save as the last component */
		nextToLast = last;
		last = c;
	}

	/**
	 * Add the specified component into this panel and setup the layout.
	 * 
	 * <p>
	 * The component will be added to the end of the last already added
	 * component.
	 * </p>
	 * 
	 * @param c
	 *            The component to add
	 */
	protected void addToEndMore(Component c) {
		/** Add component */
		add(c);
		/** Constraints */
		sl.putConstraint(SpringLayout.WEST, c, 15, SpringLayout.EAST, last);
		if (nextToLastLine == this) {
			/** The first new line */
			sl.putConstraint(SpringLayout.NORTH, c, 20, SpringLayout.NORTH,
					nextToLastLine);
		} else {
			/** Second line and further */
			sl.putConstraint(SpringLayout.NORTH, c, 20, SpringLayout.SOUTH,
					nextToLastLine);
		}
		/** Save as the last component */
		nextToLast = last;
		last = c;
	}

	/**
	 * Initialize the controls
	 */
	public abstract void init();

	/**
	 * Action to perform
	 */
	public void actionPerformed(ActionEvent e) {
	}

}
