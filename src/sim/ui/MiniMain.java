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

//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Toolkit;
//
//import javax.swing.BoxLayout;
import javax.swing.JApplet;
//import javax.swing.JPanel;
//import javax.swing.border.EtchedBorder;
//
//import sim.core.AppEngine;

/**
 * An applet which contains a subset functionality of the environment. The only
 * functionality supported is starting simulation.
 * 
 * @author Fasheng Qiu
 * 
 */
public class MiniMain extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4900752496529037375L;

	/** Simulation view */
	private SimulationView sv = null;

//	/** The path of the application */
//	private String appFile = null;
//
//	/** Simulation engine */
//	private AppEngine engine = AppEngine.getInstance();

	// // From JApplet
	// public void init() {
	// java.net.URL url = super.getDocumentBase();
	// String externalForm = url.toExternalForm();
	// if (externalForm.startsWith("http")) { // For web-applet
	// StringBuffer urlStr = new StringBuffer();
	// urlStr.append("jar:");
	// urlStr.append(externalForm.substring(0, externalForm.lastIndexOf('/')));
	// urlStr.append('/');
	// urlStr.append("lib");
	// urlStr.append('/');
	// urlStr.append("behaviorsim.jar!/");
	// try {
	// url = new URL(urlStr.toString());
	// JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
	// engine.init(ApplicationDef.APPLET, false, jarConnection.getJarFile(),
	// urlStr.toString());
	// } catch(Exception e) {
	// MessageUtils.debug(e);
	// }
	// } else { // For local applet
	// String docBase = url.getPath();
	// if (docBase.startsWith("/")) {
	// docBase = docBase.substring(1);
	// }
	// docBase = docBase.substring(0, docBase.lastIndexOf('/'));
	// try {
	// engine.init(ApplicationDef.APPLET, false, docBase);
	// } catch(Exception e) {
	// MessageUtils.displayError(e);
	// System.exit(1);
	// }
	// }
	// this.initAppPath();
	// this.initComponent();
	// }
	//
	// /**
	// * Initialize components
	// */
	// private void initComponent() {
	// // Initialize the message utils if necessary
	// MessageUtils.initialze();
	// // Initialize the application resources
	// AppResources.initialize(this, engine);
	// // Initialize the views and components
	// try {
	// sv = new SimulationView(engine);
	// jbInit();
	// } catch (Exception exception) {
	// exception.printStackTrace();
	// }
	// }
	//
	// /**
	// * Get the application file full path
	// */
	// private void initAppPath() {
	// appFile = this.getParameter("appfile");
	// if (appFile == null) {
	// if (MessageUtils.isDebug()) {
	// appFile = "C:\\Users\\qfs\\Desktop\\demo\\crayfish2\\crayfish.xml";
	// }
	// else {
	// MessageUtils.displayError("The application configuration file is not provided.");
	// throw new RuntimeException();
	// }
	// }
	// }

//	/**
//	 * Component initialization.
//	 * 
//	 * @throws java.lang.Exception
//	 */
//	private void jbInit() throws Exception {
//
//		// screen size
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//
//		// content panel
//		JPanel contentPane = (JPanel) getContentPane();
//		contentPane.setLayout(new BorderLayout());
//		// main panel
//		JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
//		mainPanel.setBorder(new EtchedBorder());
//		mainPanel.setBackground(Color.WHITE);
//
//		// Add component
//		mainPanel.add(sv);
//		contentPane.add(mainPanel, BorderLayout.CENTER);
//		this.setSize(new Dimension(screenSize.width * 9 / 10,
//				screenSize.height * 14 / 15));
//
//	}

	/**
	 * Start the simulation
	 */
	public void start() {
		sv.refresh();
	}

}
