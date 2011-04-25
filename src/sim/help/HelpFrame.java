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

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A help frame
 * 
 * @author Fasheng Qiu
 * @since 10/20/2007
 * 
 */
public class HelpFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8163800995473122029L;
	/** The only frame instance */
	private static HelpFrame helpCenter = null;

	/** Get instance */
	public static HelpFrame getInstance(sim.ui.MainFrame owner, JPanel panel) {
		if (helpCenter == null) {
			helpCenter = new HelpFrame(panel);
			owner.setHelpCenter(helpCenter);
		}
		return helpCenter;
	}

	/**
	 * Creates a new PanelDialog.
	 */
	private HelpFrame(JPanel panel) {

		// Dialog property
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				HelpFrame.this.dispose();
			}
		});

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int) (screenSize.width * 0.8),
				(int) (screenSize.height * 0.8));
		Dimension guiSize = this.getSize();
		this.setLocation((screenSize.width - guiSize.width) / 2,
				(screenSize.height - guiSize.height) / 2);
		this.setResizable(true);

		this.setIconImage(sim.ui.MainFrame.getInstance().getIconImage());

		// Add the definition panel
		this.getContentPane().add(panel);

	}
}