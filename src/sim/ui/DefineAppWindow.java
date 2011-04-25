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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import sim.core.App;
import sim.core.AppEngine;
import sim.util.FileFilterUtils;
import sim.util.MessageUtils;

/**
 * Define the type of the application and relative attributes, such as whether
 * the app is behavior- based or it is an arbitary app, and the name of the
 * application.
 * 
 * <p>
 * It is shown when the app started up, see
 * </p>
 * <p>
 * {@link #sim.ui.MainViewer.handleAppWindow()}
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class DefineAppWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3927885661030366827L;

	// Owner
	private static MainFrame owner = null;

	// Panel
	private AppPanel appPanel = null;

	/**
	 * Constructor
	 * 
	 * @param owner
	 *            The owner of this window
	 */
	public DefineAppWindow(MainFrame o, AppEngine engine) {
		super(o, "Application Definition");
		super.setResizable(false);
		super.setModal(true);
		appPanel = new AppPanel(engine);
		init(engine);
		owner = o;
		pack();

	}

	/**
	 * Initialize the components used in this window.
	 */
	private void init(AppEngine engine) {

		// Add controls panel
		getContentPane().add(appPanel);

		// Re-position this window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);

	}

	/**
	 * Create a new application.
	 * 
	 * @param appName
	 *            Application name
	 * @param appDir
	 *            Application directory
	 * @param imgDir
	 *            Image directory
	 */
	public void createNewApp(String appName, String appDir, String imgDir) {
		// Application
		App newApp = null;
		// Application configuration file
		String appFileName = null;
		// Application resource directory name
		String appResourceDir = null;
		// Application engine
		AppEngine engine = AppEngine.getInstance();

		// Application name
		if (appName == null || (appName.trim()).equals("")) {
			MessageUtils.displayError(this,
					"The application name should be set!");
			return;
		}

		// Application directory
		if (appDir == null || (appDir.trim()).equals("")) {
			MessageUtils.displayError(this,
					"The application directory should be set!");
			return;
		}
		// Make the appName conforms to the OS
		String title = appName;
		title = title.replace('?', '_');
		title = title.replace(':', '_');
		title = title.replace('\\', '_');
		title = title.replace('/', '_');
		title = title.replace('*', '_');
		title = title.replace('\"', '_');
		title = title.replace('<', '_');
		title = title.replace('>', '_');
		title = title.replace('|', '_');
		title = title.replace(' ', '_');
		title = title.replace('\t', '_');
		// Try the name "appName.xml" first
		StringBuffer full = new StringBuffer();
		full.append(appDir);
		full.append(File.separator);
		full.append(title);
		appFileName = full.toString() + ".xml";
		File configFile = new File(appFileName);
		if (configFile.exists()) {
			// Get a unique name
			SimpleDateFormat formatter = new SimpleDateFormat(
					"_MM_dd_yy_hh_mm_ss");
			String formatted = formatter.format(new Date());
			full.append(formatted);
			// The default name of the configuration file, can be changed
			// manually
			appFileName = full.toString() + ".xml";
			// New configuration file
			configFile = new File(appFileName);
		}
		try {
			boolean created = configFile.createNewFile();
			if (!created) {
				throw new RuntimeException("The file name '" + appFileName
						+ "' already exists.");
			}
		} catch (Exception ioe) {
			MessageUtils
					.displayError(
							this,
							"Can not create the configuration file. You can empty the selected application directory to continue.");
			return;
		}

		// Get the app file name
		appFileName = appFileName.substring(appFileName
				.lastIndexOf(File.separator) + 1);

		// Application resource directory
		if (imgDir != null && !(imgDir.trim()).equals("")) {
			// Resource directory name
			appResourceDir = imgDir;
			// Create the resource directory.
			File resourceFile = new File(appDir + File.separator + imgDir);
			try {
				resourceFile.mkdir();
			} catch (Exception ioe) {
				MessageUtils
						.displayError(
								this,
								"Can not create the resource directory. The directory name should be valid accoding to the target operating system.");
				return;
			}
		}

		// Create an new application to represent it
		newApp = engine.appManager.newApp();
		newApp.setAppDir(appDir);
		newApp.setAppFileName(appFileName);
		newApp.setAppName(appName);
		newApp.setDirty(true);
		if (appResourceDir != null) {
			newApp.setAppResourceDir(appResourceDir);
		}
		try {
			engine.appManager.initApp(newApp);
		} catch (Exception e) {
			MessageUtils.error(this, "onOK", new sim.util.SimException(
					"LOAD-APPLICATION-INIT-001A",
					"Can not initialize application.", e));
			return;
		}
		engine.appManager.setCurrentApp(newApp);

		// Prepare the background image and relative path for the simulation
		// environment
		engine.system.env.resetToApp();

		AppStatusbar.getInstance().changeMessage(
				"A new application is created successfully.");
		AppStatusbar.getInstance().changeAppStatus("App created");

		// Save the application information to external file --- Added on
		// 04092009
		new sim.core.AppTask().run();

		// Update title
		owner.setTitle(appName + " - " + configFile.getAbsolutePath());

		// Setup recent files
		sim.ui.menus.MainMenuBar.getInstance(null, null)
				.getRecentFilesHandler().add(configFile.getAbsolutePath());
		sim.ui.menus.MainMenuBar.getInstance(null, null)
				.getRecentFilesHandler().updateProperties();

		try {
			// Dispose this window
			DefineAppWindow.this.setVisible(false);
			DefineAppWindow.this.dispose();
		} catch (Exception e) {
		}
	}

	/**
	 * Open the application of a full-path configuration
	 * 
	 * @param path
	 *            The full-path configuration file
	 */
	public void openApp(String path) {

		// Application
		App newApp = null;
		// Application name
		String appName = null;
		// Application configuration file
		String appFileName = null;
		// Application directory full-path
		String appDir = null;
		// Application engine
		AppEngine engine = AppEngine.getInstance();

		// Get the configuration file full path
		if (path.equals("")) {
			MessageUtils.displayError("Please select an configuration file.");
			return;
		}

		// Load application name
		try {
			appName = engine.preloadAppDef(path);
		} catch (Exception e) {
			AppStatusbar
					.getInstance()
					.changeMessage(
							"The application is not loaded. Make sure the configuration file is correct.");
			return;
		}

		// Load application other information
		try {
			appFileName = path.substring(path.lastIndexOf(File.separator) + 1);
			appDir = path.substring(0, path.lastIndexOf(File.separator));
		} catch (Exception e) {
			AppStatusbar.getInstance().changeMessage(
					"Can not obtain configuration file and directory.");
			return;
		}

		// Create an new application to represent it
		newApp = engine.appManager.newApp();
		newApp.setAppDir(appDir);
		newApp.setAppFileName(appFileName);
		newApp.setAppName(appName);
		newApp.setDirty(false);
		try {
			engine.appManager.initApp(newApp);
		} catch (Exception e) {
			MessageUtils.error(DefineAppWindow.class, "onOK",
					new sim.util.SimException("LOAD-APPLICATION-INIT-001A",
							"Can not initialize application.", e));
			return;
		}
		engine.appManager.setCurrentApp(newApp);

		// Load the application configuration
		sim.core.AppLoader.loadAppFromFile(path, true);

		// Setup recent files
		sim.ui.menus.MainMenuBar.getInstance(null, null)
				.getRecentFilesHandler().add(path);
		sim.ui.menus.MainMenuBar.getInstance(null, null)
				.getRecentFilesHandler().updateProperties();

		try {

			// Update title
			owner.setTitle(appName + " - " + path);
			// Dispose this window
			DefineAppWindow.this.setVisible(false);
			DefineAppWindow.this.dispose();
		} catch (Exception e) {
		}
	}

	/**
	 * Disable the specified tab
	 * 
	 * @param tabIndex
	 *            The index of the tab to disabled
	 */
	public void disableTab(int tabIndex) {
		appPanel.disableTab(tabIndex);
	}

	/**
	 * The controls panel
	 */
	private class AppPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4945154753773563202L;
		// Panels
		private JPanel contentPane = new JPanel();
		private JPanel buttonBarGrid = new JPanel();
		private JPanel controlsPanel = new JPanel();
		private JPanel buttonBar = new JPanel();

		// Controls
		private JTextField nameTF = new JTextField();
		private JTextField appDirTF = new JTextField();
		private JTextField imgDirTF = new JTextField();

		private JTextField pictureFileName = new JTextField();
		private JTabbedPane tabbedPane = new JTabbedPane();

		// Application engine
		private AppEngine engine = null;

		// Constructor
		public AppPanel(AppEngine eng) {
			engine = eng;
			try {
				enableEvents(AWTEvent.WINDOW_EVENT_MASK);
				init();
				pack();

				// pictureFileName.setText("C:\\Users\\qfs\\Documents\\QQ\\1236305629168.xml");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void disableTab(int tabIndex) {
			if (tabIndex != 0 && tabIndex != 1)
				throw new IllegalArgumentException(
						"The tab index is not correct.");
			tabbedPane.setEnabledAt(tabIndex, false);
			tabbedPane.setSelectedIndex(tabIndex == 0 ? 1 : 0);
		}

		private void init() {

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			contentPane.setPreferredSize(new Dimension(screenSize.width / 2,
					screenSize.height / 3));

			setContentPane(contentPane);
			contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
			buttonBar.setLayout(new FlowLayout());
			buttonBarGrid.setLayout(new FlowLayout());

			JButton cancelButton = new JButton();
			JButton okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onOk();
				}
			});
			cancelButton.setText("CANCEL");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			});
			buttonBarGrid.add(okButton);
			buttonBarGrid.add(cancelButton);
			buttonBar.add(buttonBarGrid);

			controlsPanel.setLayout(new BoxLayout(controlsPanel,
					BoxLayout.X_AXIS));
			controlsPanel.add(createTab());
			contentPane.add(controlsPanel, BorderLayout.NORTH);
			contentPane.add(buttonBar, BorderLayout.SOUTH);
		}

		/**
		 * The help dialog
		 */
		public class HelpDialog extends JDialog implements ActionListener {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7983564263411505453L;


			/**
			 * Constructs an AboutDialog object
			 * 
			 * @param mainWindow
			 *            the main window
			 */
			public HelpDialog() {

				// Use JDialog constructor
				super(DefineAppWindow.this, "Help of Defining Application");

				// Finalize the window
				this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

				// Create the main panel
				JPanel mainPane = new JPanel(new GridLayout(10, 1));
				mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				setContentPane(mainPane);

				// Create the document contents string
				mainPane
						.add(new JLabel(
								"Application Directory: The directory where the new application is stored.\n"));
				mainPane
						.add(new JLabel(
								"Resource Directory: The name of the directory where all resources are stored.\n"));
				mainPane
						.add(new JLabel(
								"Application Name: The meaningful name of the new application.\n"));
				mainPane.add(new JLabel("\n"));

				mainPane
						.add(new JLabel(
								"Note that: Resource Directory is a directory under the specified Application Directory. \n"));
				mainPane
						.add(new JLabel(
								"The name of Resource Directory should meet the naming rules of folders of your OS.\n\n"));
				mainPane
						.add(new JLabel(
								"Application Name should meet the naming rules of files of your OS.\n\n"));

				// Set the size of the window
				setSize(500, 200);

				// Center the window on the parent window.
				Point ploc = DefineAppWindow.this.getLocation();
				setLocation(ploc.x, ploc.y);

				// Prevent the window from being resized by the user.
				setResizable(false);

				// Show the window
				setVisible(true);
			}

			// Implementing ActionListener method
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
				dispose();
			}
		}

		private class NewAppPanel extends CommonPanel {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2239175285140910905L;

			/**
			 * Constructor
			 * 
			 */
			public NewAppPanel() {
				/** Initialize components */
				init();
			}

			/**
			 * Initialize the components used in this window.
			 */
			public void init() {

				appdirChooser = new JButton("Choose");
				appdirChooser.addActionListener(this);

				Font font = new Font("Times New Roman", Font.ITALIC, 12);
				helper = new JLabel("Need help?");
				helper.setFont(font);
				helper.setForeground(Color.BLUE);
				helper
						.setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
				helper.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						new HelpDialog().setVisible(true);
					}
				});

				super.addToNewLine(new JLabel("Application Directory:"));
				super.addToEnd(appDirTF);
				super.addToEndMore(appdirChooser);
				appdirChooser.setPreferredSize(new Dimension(appdirChooser
						.getPreferredSize().width, 20));
				appDirTF.setEditable(false);
				appDirTF.setPreferredSize(new Dimension(contentPane
						.getPreferredSize().width / 2, 20));

				super.addToNewLine(new JLabel("Image Directory:      "));
				super.addToEnd(imgDirTF);
				super.addToEndMore(new JLabel("(optional)"));
				imgDirTF.setPreferredSize(new Dimension(contentPane
						.getPreferredSize().width / 3, 20));

				super.addToNewLine(new JLabel("Application Name:   "));
				super.addToEnd(nameTF);
				nameTF.setPreferredSize(new Dimension(contentPane
						.getPreferredSize().width / 3, 20));

				super.addToNewLine(helper);

			}

			/**
			 * Action to perform
			 */
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == appdirChooser) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser
							.setDialogTitle("Select an Application Directory");
					fileChooser.setApproveButtonText("OK");
					if (!appDirTF.getText().equals(""))
						fileChooser.setCurrentDirectory(new File(appDirTF
								.getText()));
					fileChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = fileChooser.showOpenDialog(this);
					// if we selected an image, load the image
					if (result == JFileChooser.APPROVE_OPTION) {
						String path = fileChooser.getSelectedFile().getPath();
						appDirTF.setText(path);
					}
				}
			}

			/** Application dir chooser */
			private JButton appdirChooser;

			/** Helper */
			private JLabel helper;

		}

		private JTabbedPane createTab() {
			Font mainFont = new Font("Times New Roman", Font.BOLD, 11);
			tabbedPane.setFont(mainFont);
			tabbedPane.setBorder(new BevelBorder(BevelBorder.LOWERED));

			tabbedPane.add("New application", new NewAppPanel());

			JPanel existingApp = new JPanel();
			existingApp.setLayout(new BoxLayout(existingApp, BoxLayout.X_AXIS));
			JLabel pictureLabel = new JLabel("Configuration:");
			JButton pictureChooser = new JButton("Choose");

			pictureFileName.setEditable(false);
			pictureFileName.setMaximumSize(new Dimension(contentPane
					.getPreferredSize().width, 25));
			existingApp.add(Box.createHorizontalStrut(20));
			existingApp.add(pictureLabel);
			existingApp.add(pictureFileName);
			existingApp.add(Box.createHorizontalStrut(5));
			existingApp.add(pictureChooser);
			existingApp.add(Box.createHorizontalStrut(20));
			pictureChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadFromFile();
				}
			});
			tabbedPane.add("Existing application", existingApp);
			tabbedPane.setPreferredSize(contentPane.getPreferredSize());

			return tabbedPane;
		}

		/**
		 * Load the app configuration from the external file
		 * 
		 * @param path
		 *            The full path of the configuration file
		 */
		private void loadFromFile() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Select a Model File");
			fileChooser.setApproveButtonText("OK");
			if (!pictureFileName.getText().equals(""))
				fileChooser.setCurrentDirectory(new File(pictureFileName
						.getText()));
			else {
				try {
					if (engine.appManager.currentApp.getAppDir() != null)
						fileChooser.setCurrentDirectory(new File(
								engine.appManager.currentApp.getAppDir()));
				} catch (Exception ex) {
				}
			}
			fileChooser.setFileFilter(new FileFilterUtils(
					new String[] { "xml" }, true, "XML File (*.xml)"));
			int result = fileChooser.showOpenDialog(this);
			// if we selected an image, load the image
			if (result == JFileChooser.APPROVE_OPTION) {
				String path = fileChooser.getSelectedFile().getPath();
				pictureFileName.setText(path.trim());
			}
		}

		private void onOk() {

			// Existing application
			if (tabbedPane.getSelectedIndex() == 1) {

				openApp(pictureFileName.getText().trim());

			}
			// Create a new application
			else {

				createNewApp(nameTF.getText().trim(),
						appDirTF.getText().trim(), imgDirTF.getText().trim());
			}

		}


		private void onCancel() {
			discard();
		}

		private void discard() {
			DefineAppWindow.this.dispose();
		}
	}
}
