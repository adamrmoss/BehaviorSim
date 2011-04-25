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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The loading dialog of user-specified application configuration file (XML
 * model file).
 * 
 * 
 * @author Owner QFS
 * 
 */
public class AppLoadDialog extends JDialog implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6180059238343709005L;

	public AppLoadDialog(JFrame parentPanel) {

		super(parentPanel, "Loading configuration file...", true);

		JLabel notice = new JLabel("Application is loading, please wait!");
		getContentPane().add(notice);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				hideIt();
			}
		});

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - 260) / 2, (d.height - 80) / 2);
		setSize(new Dimension(300, 100));

	}

	public void hideIt() {
		setVisible(false);
		dispose();
	}

	public void run() {
		setVisible(true);
	}

}