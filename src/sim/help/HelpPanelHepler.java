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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import sim.core.AppEngine;

/**
 * Helper class for making help menus.
 * 
 * @author Fasheng Qiu
 * 
 */
public class HelpPanelHepler {

	// Left and right expand buttons
	static JButton lexpand = null, rexpand = null;

	// Max and min icons
	static ImageIcon maxicon = null, minicon = null;

	// Load icons
	static {
		maxicon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(
				AppEngine.getInstance().jrl.getImage("/sim/ui/images/max.png")));
		minicon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(
				AppEngine.getInstance().jrl.getImage("/sim/ui/images/min.png")));
	}

	/**
	 * Change the expanding icon based on the status of left or right panels.
	 * 
	 * @param isLeftPanel
	 *            Whether the panel to update is the left tree panel
	 * @param maximized
	 *            Whether the panel has been maximized.
	 */
	public static void switchExpand(boolean isLeftPanel, boolean maximized) {
		if (isLeftPanel) {
			if (maximized) {
				lexpand.setIcon(minicon);
				lexpand.setToolTipText("Unexpand window");
			} else {
				lexpand.setIcon(maxicon);
				lexpand.setToolTipText("Expand window");
			}
		} else {
			if (maximized) {
				rexpand.setIcon(minicon);
				rexpand.setToolTipText("Unexpand window");
			} else {
				rexpand.setIcon(maxicon);
				rexpand.setToolTipText("Expand window");
			}
		}
	}

	public static JPanel getTopicsMenuItem(ActionListener l) {
		JPanel panel = new JPanel();

		lexpand = new JButton(maxicon);
		lexpand.setActionCommand("left-expand");
		lexpand.setToolTipText("Expand window");
		lexpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lexpand.setPreferredSize(new Dimension(16, 16));
		lexpand.addActionListener(l);

		panel.add(lexpand);

		return panel;
	}

	public static JPanel getTopicMenuItem(ActionListener l) {
		JPanel panel = new JPanel();

		JButton prev = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(
						AppEngine.getInstance().jrl
								.getImage("/sim/ui/images/back.gif"))));
		prev.setActionCommand("prev");
		prev.setToolTipText("Previous page");
		prev.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton next = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(
						AppEngine.getInstance().jrl
								.getImage("/sim/ui/images/next.gif"))));
		next.setActionCommand("next");
		next.setToolTipText("Next page");
		next.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton home = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(
						AppEngine.getInstance().jrl
								.getImage("/sim/ui/images/home.gif"))));
		home.setActionCommand("home");
		home.setToolTipText("Home page");
		home.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton display = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(
						AppEngine.getInstance().jrl
								.getImage("/sim/ui/images/displayattree.gif"))));
		display.setActionCommand("display");
		display.setToolTipText("Display in left tree");
		display.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton print = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(
						AppEngine.getInstance().jrl
								.getImage("/sim/ui/images/print.png"))));
		print.setActionCommand("print");
		print.setToolTipText("Print page");
		print.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		rexpand = new JButton(maxicon);
		rexpand.setActionCommand("right-expand");
		rexpand.setToolTipText("Expand window");
		rexpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		rexpand.setPreferredSize(new Dimension(16, 16));
		rexpand.addActionListener(l);

		prev.addActionListener(l);
		next.addActionListener(l);
		home.addActionListener(l);
		display.addActionListener(l);
		print.addActionListener(l);
		rexpand.addActionListener(l);

		prev.setPreferredSize(new Dimension(16, 16));
		next.setPreferredSize(new Dimension(16, 16));
		home.setPreferredSize(new Dimension(16, 16));
		display.setPreferredSize(new Dimension(16, 16));
		print.setPreferredSize(new Dimension(16, 16));
		rexpand.setPreferredSize(new Dimension(16, 16));

		prev.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		next.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		home.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		display.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		print.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		rexpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		panel.add(prev);
		panel.add(next);
		panel.add(home);
		panel.add(display);
		panel.add(print);
		panel.add(rexpand);

		return panel;
	}

}
