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

package sim.ui.panels;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * A generic dialog used to display the definition panel, such as the category
 * definition panel.
 * 
 * @author Fasheng Qiu
 * @since 10/20/2007
 * 
 */
public class PanelDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3447055440453007009L;

	/**
	 * Creates a new PanelDialog.
	 */
	public PanelDialog(java.awt.Frame owner, JPanel panel) {

		super(owner);

		// Dialog property
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				PanelDialog.this.dispose();
			}
		});

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int) (screenSize.width * 0.8),
				(int) (screenSize.height * 0.8));
		Dimension guiSize = this.getSize();
		this.setLocation((screenSize.width - guiSize.width) / 2, 0);
		this.setResizable(true);

		// Add the definition panel
		this.getContentPane().add(panel);

	}
}