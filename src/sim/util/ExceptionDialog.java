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

package sim.util;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;

/**
 * Dialog for showing exception information
 * 
 * @author Fasheng Qiu
 * 
 */
public class ExceptionDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6906771413535108537L;
	/** Panel to display exception information */
	private ExceptionPanel ep = null;

	/** Constructor */
	public ExceptionDialog() {
		this(null);

	}

	/** Constructor */
	public ExceptionDialog(Exception e) {
		super(sim.ui.MainFrame.getInstance());
		super.setTitle("Exception information");
		ep = new ExceptionPanel(this, e);
		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			this.getContentPane().add(ep);
			pack();
			Dimension frameSize = new Dimension(550, 400);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (frameSize.height > screenSize.height)
				frameSize.height = screenSize.height;
			if (frameSize.width > screenSize.width)
				frameSize.width = screenSize.width;
			setLocation(((screenSize.width - frameSize.width) / 2),
					((screenSize.height - frameSize.height) / 2));
			setSize(frameSize);
		} catch (Exception ee) {
		}
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				dispose();
			}
		});
	}

	/** Set the exception to be displayed */
	public void setException(Exception e) {
		ep.setException(e);
	}

	/** Dispose window */
	public void onDispose() {
		dispose();
	}
}
