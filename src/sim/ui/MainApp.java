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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import sim.core.AppEngine;
import sim.core.ConfigParameters;

/**
 * Application mode of BehaviorSim environment.
 * 
 * 
 * @author Fasheng Qiu
 * 
 */
public class MainApp {

	/** Whether pack frame is needed or not */
	boolean packFrame = false;

	/** The main frame of this viewer */
	private MainFrame frame = null;

	/**
	 * System initialization. The main frame will be created and the SIM_HOME
	 * property will be set.
	 */
	public void init() throws Exception {

		// Set up the SIM_HOME property, which will be used in the
		// initialization of dynamic class library (The base classes
		// will be in the directory SIM_HOME/lib/behaviorsim_version.jar OR
		// will be in the directory SIM_HOME/bin/ or SIM_HOME/classes
		// if the jar file can not be found).
		//
		// For WEB-BASED context, SIM_HOME should be set as the directory
		// of the context root. And the library structure should be same
		// as before.
		//
		// Also, SIM_HOME property will be used in the locating of the logging
		// configuration file and logging file, SIM_HOME/config/log.xml and
		// SIM_HOME/log/behaivorsim.log and SIM_HOME/log/debug.log.
		// 
		// For system resources, such as the icon images, are also stored in
		// the behaviorsim_version.jar.
		// For release version, SIM_HOME is subject to change.
		System.setProperty("SIM_HOME", System.getProperty("user.dir"));

		// Create frame
		frame = MainFrame.getInstance();

		// Validate frames that have preset sizes
		// Pack frames that have useful preferred size info,
		// e.g. from their layout
		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		// Initialize components
		frame.init();

		// Screen size and location
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(new Dimension((int) (screenSize.width),
				(int) (screenSize.height * 0.95)));
		frame.setResizable(true);

		frame.setLocation(0, 0);

		// Set up default title
		frame
				.setTitle("BehaviorSim v"
						+ ConfigParameters.version);
		// Set the logo image
		frame.setIconImage(Toolkit.getDefaultToolkit()
				.createImage(
						AppEngine.getInstance().jrl
								.getImage("/sim/ui/images/logo.gif")));

	}

	/** Return the base path */
	public static String getBasePath() {
		return System.getProperty("SIM_HOME");
	}

	/** Show application definition window */
	public void start() {
		// Show main window
		AppClock clock = new AppClock(frame);
		clock.start();
	}

	/**
	 * Construct and show the application.
	 */
	public MainApp() throws Exception {
		this.init();
		this.start();
	}

	/**
	 * Set the look and feel of this application as the system look and feel
	 */
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Application entry point.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLookAndFeel();
				try {
					new MainApp();
					sim.util.GUIUtils.deuglyizeUIDefaults();
				} catch (Exception e) {
					sim.util.MessageUtils.debug(this, "main", e);
					sim.util.MessageUtils.displayError(e);
					System.exit(1);
				}
			}
		});

	}

}
