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

import sim.core.AppEngine;

import javax.swing.*;
import java.io.File;

/**
 * Create a main frame which is used in MainViewer
 *
 * @author Fasheng Qiu
 * @version 1.0
 */
public class MainFrame extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = -4837846996899350672L;

	/** Help center */
	private sim.help.HelpFrame helpCenter = null;

	/* Main window */
	public MainApplet applet = null;

	/** The only instance */
	private static MainFrame owner;

	public static MainFrame getInstance() {
		if (owner == null)
			owner = new MainFrame();
		return owner;
	}

	// Main entry point when running in console mode
	private MainFrame() {

		// Finalize the message tools before close the window
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				boolean isDirty = false;
				try {
					isDirty = AppEngine.getInstance().appManager.currentApp
							.isDirty();
				} catch (Exception ee) {
				}
				String dir = null;
				try {
					dir = AppEngine.getInstance().appManager.currentApp
							.getAppDir();
				} catch (Throwable ee) {
				}
				if (isDirty) {
					String[] options = { "Save&Exit", "Exit", "Cancel" };
					int type = JOptionPane.showOptionDialog(MainFrame.this,
							"Select one button to continue.", "Confirmation",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options,
							options[0]);
					if (type == JOptionPane.CANCEL_OPTION) {
						setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
						return;
					} else if (type == JOptionPane.NO_OPTION) {
						setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					} else {
						AppEngine
								.getInstance()
								.saveAppAsFile(
										dir
												+ File.separator
												+ AppEngine.getInstance().appManager.currentApp
														.getAppFileName());
					}
				} else if (dir != null) {
					int response = JOptionPane
							.showConfirmDialog(
									MainFrame.this,
									"Current application will be closed.\nDo you want to continue?",
									"Warning", JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.NO_OPTION) {
						setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
						return;
					}
				}
				// Save the recent files
				sim.ui.menus.MainMenuBar.getInstance(null, null)
						.getRecentFilesHandler().updateProperties();
				// Close the current active application
				try {
					AppEngine.getInstance().appManager.destroyApp(AppEngine
							.getInstance().appManager.currentApp);
				} catch (Exception ex) {
				}
				// Close the help frame
				if (helpCenter != null) {
					helpCenter.dispose();
				}
				MainFrame.this.dispose();
				System.exit(0);
			}
		});

		// New applet running not as an applet
//		applet = new MainApplet(this);

		// Set up frame
		getContentPane().add(applet);
	}

	/**
	 * @param helpCenter
	 *            the helpCenter to set
	 */
	public void setHelpCenter(sim.help.HelpFrame helpCenter) {
		this.helpCenter = helpCenter;
	}

	/**
	 * Initialize the main components (Dynamic Class Library and Logging).
	 *
	 * @throws Exception
	 *             Throw if any exception occurs
	 */
	public void init() throws Exception {

		// Initialize the dynamic manager
		MainApplet.engine.init(System.getProperty("SIM_HOME"));

		// Initialize the components
		applet.initComponent();

	}

}
