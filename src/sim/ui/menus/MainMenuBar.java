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

package sim.ui.menus;

import java.io.File;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import sim.core.AppEngine;
import sim.core.ConfigParameters;
import sim.ui.DefineAppWindow;
import sim.ui.MainApplet;
import sim.ui.MainFrame;

public class MainMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8859405824330756730L;
	/**
	 * The main window
	 */
	private MainFrame parent;
	private MainApplet main;

	/** Configuration parameters */
	private static Properties properties;

	/** Handler for recent applications */
	private static RecentFilesHandler recentFiles;

	/** The only instance */
	private static MainMenuBar instance;

	/** Recent application menu */
	JMenu recentMenu = new JMenu("Recent Apps");

	/** Return the instance */
	public static MainMenuBar getInstance(MainFrame parent, MainApplet main) {
		if (instance == null) {
			properties = ConfigParameters.properties;
			instance = new MainMenuBar(parent, main);
		}
		return instance;
	}

	/**
	 * Return the recent files handler
	 * 
	 * @return
	 */
	public RecentFilesHandler getRecentFilesHandler() {
		return recentFiles;
	}

	/**
	 * Recent applications handler
	 * 
	 */
	public class RecentFilesHandler {

		// Whether all recent files have been saved
		private boolean dirty;

		// Full-path application configuration files
		private String[] files;

		// Maximum number of recent items
		private int max_number = ConfigParameters.RECENT_FILE_MAXIMUM_NUMBER;

		// Constructor
		public RecentFilesHandler() {

			// Number of recent files

			int number = 10;
			try {
				number = Integer.parseInt(properties
						.getProperty(ConfigParameters.RECENT_FILE_NUMBER));
			} catch (Exception e) {
			} finally {
				if (number > max_number)
					number = max_number;
			}
			List list = new ArrayList();
			// Populate the file list
			for (int i = 0; i < number; i++) {
				String ret = null;
				try {
					ret = properties
							.getProperty(ConfigParameters.RECENT_FILE_PREFIX
									+ (i + 1));
				} catch (Exception e) {
				}
				// Only show valid recent files
				if (ret != null && new File(ret).exists())
					list.add(ret);
			}
			files = new String[list.size()];
			files = (String[]) list.toArray(files);
			dirty = false;

		}

		public void updateProperties() {
			// Remove the original recent files
			Enumeration iter = properties.keys();
			while (iter.hasMoreElements()) {
				String key = (String) iter.nextElement();
				if (key.startsWith(ConfigParameters.RECENT_FILE_PREFIX)) {
					properties.remove(key);
				}
			}
			// Save the list of recent file information
			for (int i = 0; i < files.length; i++) {
				properties.put(ConfigParameters.RECENT_FILE_PREFIX + (i + 1),
						files[i]);
			}
			// Save the number of recent files
			properties.remove(ConfigParameters.RECENT_FILE_NUMBER);
			properties.put(ConfigParameters.RECENT_FILE_NUMBER, files.length
					+ "");
			// Save to external file
			sim.core.AppEngine.getInstance().system.systemParameters
					.saveProperties(properties);
			dirty = false;
		}

		private int _index(String file) {
			for (int i = 0; i < files.length; i++)
				if (files[i].equals(file))
					return i;
			return -1;
		}

		public void add(String file) {
			// Update?
			boolean update = false;
			// Index of the file in the array
			int inx = _index(file);
			// The file is already in the recent list and it is not the first
			// one.
			if (inx != -1) {
				if (inx != 0) {
					shiftFile(file, inx);
					update = true;
				}
			} else {
				update = true;
				shiftFile(file);
			}
			// Update the menu
			if (update) {
				setupRecentFiles();
				dirty = true;
			}

		}

		/**
		 * Put the existing file to the first one and shift other items
		 * 
		 * @param file
		 *            Existing file
		 * @param inx
		 *            The index
		 */
		private void shiftFile(String file, int inx) {
			String[] fs = new String[files.length];
			fs[0] = file;
			for (int i = 0; i < inx; i++)
				fs[i + 1] = files[i];
			for (int i = inx + 1; i < files.length; i++)
				fs[i] = files[i];
			System.arraycopy(fs, 0, files, 0, files.length);
		}

		/**
		 * Put the new file to the first one and shift other items.
		 * 
		 * @param file
		 */
		private void shiftFile(String file) {
			int newCount = files.length + 1;
			if (newCount > max_number)
				newCount = max_number;
			String[] fs = new String[newCount];
			fs[0] = file;
			int i = 0;
			while (i < newCount - 1) {
				fs[i + 1] = files[i];
				i++;
			}
			files = new String[fs.length];
			System.arraycopy(fs, 0, files, 0, fs.length);
		}

		public boolean isDirty() {
			return dirty;
		}

		public List getFiles() {
			return java.util.Arrays.asList(files);
		}
	}

	/** Open recent action implementation */
	class OpenRecentListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// File to open
			String file = e.getActionCommand().substring(3);
			if (file == null) {
				return;
			}
			// If the file exists, then open it.
			if (new java.io.File(file).exists()) {
				boolean isDirty = false;
				try {
					sim.core.AppManager appManager = AppEngine.getInstance().appManager;
					isDirty = appManager.currentApp.isDirty();
				} catch (Throwable ee) {
				}
				String dir = null;
				try {
					dir = AppEngine.getInstance().appManager.currentApp
							.getAppDir();
				} catch (Throwable ee) {
				}
				// Check unchanged
				if (dir != null) {
					if (isDirty) {
						int response = JOptionPane
								.showConfirmDialog(
										parent,
										"Unsaved changes will be lost.\nDo you want to proceed?",
										"Warning", JOptionPane.YES_NO_OPTION);
						if (response == JOptionPane.NO_OPTION) {
							return;
						}
					}
					// Close the current active application
					try {
						AppEngine.getInstance().appManager.destroyApp(AppEngine
								.getInstance().appManager.currentApp);
					} catch (Exception ex) {
					}
				}
				// Clear the current application first
				main.clearAll();
				// Show the application definition dialog to define new
				// application
				DefineAppWindow appWindow = new DefineAppWindow(parent,
						MainApplet.engine);
				appWindow.openApp(file);
			} else {
				JOptionPane.showMessageDialog(parent, "File does not exist.");
				return;
			}
		}
	};

	/** Setup recent application files */
	protected void setupRecentFiles() {
		recentMenu.removeAll();
		List keys = recentFiles.getFiles();
		if (keys.isEmpty()) {
			JMenuItem recentMenuItem = new JMenuItem("<<None>>");
			recentMenu.add(recentMenuItem);
		} else
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				JMenuItem recentMenuItem = new JMenuItem((i + 1) + ". " + key);
				recentMenuItem.addActionListener(new OpenRecentListener());
				recentMenu.add(recentMenuItem);
				super.repaint();
			}
	}

	/**
	 * Constructor
	 * 
	 */
	public MainMenuBar(MainFrame parent, MainApplet main) {
		this.parent = parent;
		this.main = main;
		this.initMenuBar();
		this.setup();
	}

	/**
	 * Setup the recent file handler
	 */
	public void setup() {
		try {
			recentFiles = new RecentFilesHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setupRecentFiles();
	}

	/**
	 * Initializes the menu bar.
	 * <p/>
	 * All actions are available from the menu.
	 */
	private void initMenuBar() {
		// file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);

		JMenuItem fileNewMenuItem = new JMenuItem(new NewAction(parent, main));
		fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				Event.CTRL_MASK));
		fileMenu.add(fileNewMenuItem);
		JMenuItem fileOpenMenuItem = new JMenuItem(new OpenAction(parent, main));
		fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				Event.CTRL_MASK));
		fileMenu.add(fileOpenMenuItem);

		JMenuItem fileSaveMenuItem = new JMenuItem(new SaveAction(main));
		fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Event.CTRL_MASK));
		fileMenu.add(fileSaveMenuItem);
		JMenuItem fileSaveAsMenuItem = new JMenuItem(new SaveAsAction(main));
		fileSaveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				Event.CTRL_MASK));
		fileMenu.add(fileSaveAsMenuItem);

		fileMenu.addSeparator();
		fileMenu.add(recentMenu);
		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem(new ExitAction(main));
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				Event.CTRL_MASK));
		fileMenu.add(exitMenuItem);

		// tools menu
		JMenu toolsMenu = new JMenu("Application");
		toolsMenu.setMnemonic(KeyEvent.VK_A);
		add(toolsMenu);

		JMenuItem defineCategoryMenuItem = new JMenuItem(
				new DefineCategoryAction());
		defineCategoryMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, Event.CTRL_MASK));
		toolsMenu.add(defineCategoryMenuItem);
		JMenuItem defineBehaviorNetworkMenuItem = new JMenuItem(
				new DefineBehaviorNetworkAction());
		defineBehaviorNetworkMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_B, Event.CTRL_MASK));
		toolsMenu.add(defineBehaviorNetworkMenuItem);
		JMenuItem startSimulationMenuItem = new JMenuItem(
				new StartSimulationAction(main));
		startSimulationMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, Event.CTRL_MASK));
		toolsMenu.add(startSimulationMenuItem);

		// help menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);

		JMenuItem helpMenuItem = new JMenuItem(new HelpAction(parent));
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				Event.CTRL_MASK));
		helpMenu.add(helpMenuItem);

		// JCheckBoxMenuItem clock = new JCheckBoxMenuItem(new
		// ScheduleSaveAction(parent));
		// helpMenu.add(clock);
		helpMenu.addSeparator();
		JMenuItem showMemItem = new JMenuItem(new ShowMemAction(parent));
		helpMenu.add(showMemItem);

		JMenuItem aboutMenuItem = new JMenuItem(new AboutAction(parent));
		helpMenu.add(aboutMenuItem);
	}
}
