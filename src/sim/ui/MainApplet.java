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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import sim.core.AppEngine;
import sim.model.entity.Category;
import sim.ui.menus.MainMenuBar;
import sim.ui.menus.MainToolBar;
import sim.util.FileFilterUtils;
import sim.util.MessageUtils;

/**
 * The applet version of BehaviorSim environment
 * 
 * @author Pavel, Fasheng Qiu
 * 
 */
public class MainApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4541927061562357691L;

	/* Content pane */
	JPanel contentPane = null;

	// Tabbed pane for setting up components
	private JTabbedPane tabbedPane = new JTabbedPane();

	// Control panel
	private JPanel controlPanel = new JPanel();

	// Tree-related elements
	private DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
	private JTree appTree = new JTree(top);
	private JScrollPane treeView = new JScrollPane(appTree);
	private DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
	private Font mainFont = new Font("Times New Roman", Font.BOLD, 11);

	// View panels
	private EditorView editView;
	private SimulationView simView;

	// Application engine
	public static AppEngine engine = AppEngine.getInstance();

	// The owner of this applet
	private MainFrame owner = null;

	/** Constructor */
	public MainApplet(MainFrame owner) {
		this.owner = owner;
	}

	// From JApplet
	public void init() {
		java.net.URL url = super.getDocumentBase();
		String externalForm = url.toExternalForm();
		// externalForm = "http://localhost:8080/sim-applet/sim.htm";
		if (externalForm.startsWith("http")) { // For web-applet
			StringBuffer urlStr = new StringBuffer();
			urlStr.append("jar:");
			urlStr.append(externalForm.substring(0, externalForm
					.lastIndexOf('/')));
			urlStr.append('/');
			urlStr.append("lib");
			urlStr.append('/');
			urlStr.append("behaviorsim.jar!/");
			try {
				urlStr.append("jar:");
				urlStr.append(externalForm.substring(0, externalForm
						.lastIndexOf('/')));
				engine.init(urlStr.toString());
			} catch (Exception e) {
				MessageUtils.debugAndDisplay(this, "init", e);
			}
		} else { // For local applet
			String docBase = url.getPath();
			if (docBase.startsWith("/")) {
				docBase = docBase.substring(1);
			}
			docBase = docBase.substring(0, docBase.lastIndexOf('/'));
			try {
				engine.init(docBase);
			} catch (Exception e) {
				MessageUtils.debugAndDisplay(this, "init", e);
			}
		}
	}

	/**
	 * Initialize system components
	 * 
	 * @throws Exception
	 *             If the components are not initialized successfully
	 */
	public void initComponent() throws Exception {

		// Initialize the message utils if necessary
		MessageUtils.initialze();

		// Initialize the application resources
		engine.resources.initialize(this, engine);

		// Initialize the views and components
		try {
			editView = new EditorView( );
			simView = new SimulationView( );
			jbInit();
		} catch (Exception exception) {
			throw new sim.util.SimException(
					"LOAD-SYSTEM-COMPONENT-001A",
					"Can not initialize system components, required resources can not be found",
					exception);
		}

	}

	/**
	 * Component initialization.
	 * 
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception {

		// content panel
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());

		// main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// Add tab
		tabbedPane.addTab(editView.getTitle(), editView.getViewIcon(), editView);
		tabbedPane.addTab(simView.getTitle(), simView.getViewIcon(), simView);
		
		// tabbed panel
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// Component to show
				AppView cur = (AppView) tabbedPane.getSelectedComponent();
				// Notify the current display time to edit view
				if (cur == editView)
				{
					editView.simulationStopped(simView.getDisplayTime());
				}
				// Switched to a different tab
				cur.refresh();
			}
		});
		tabbedPane.setFont(mainFont);
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		mainPanel.add(AppStatusbar.getInstance(), BorderLayout.SOUTH);

		controlPanel.setLayout(new BorderLayout());
		controlPanel.setBackground(Color.WHITE);

		ImageIcon viewIcon = new ImageIcon(engine.jrl
				.getImage("/sim/ui/images/tools.gif".substring(1)));
		treeRenderer.setIcon(viewIcon);
		treeRenderer.setLeafIcon(viewIcon);
		appTree.setCellRenderer(treeRenderer);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Fish");
		top.add(node);

//		FontMetrics fmetrics = getFontMetrics(mainFont);

		JButton blabel = new JButton("Navigation Tree");

		blabel.setFont(mainFont);
		blabel.setFocusable(false);
		controlPanel.add(blabel, BorderLayout.NORTH);
		controlPanel.add(treeView, BorderLayout.CENTER);
		// controlPanel.setSize(new
		// Dimension(fmetrics.stringWidth(blabel.getText()) + 50,
		// screenSize.height*9/10));

		contentPane.add(mainPanel, BorderLayout.CENTER);

		MainMenuBar bar = MainMenuBar.getInstance(owner, this);
		setJMenuBar(bar);
		contentPane.add(new MainToolBar(owner, this), BorderLayout.NORTH);
		// this.setSize(new Dimension(screenSize.width*9/10,
		// screenSize.height*14/15));
	}

	/**
	 * Start the simulation
	 */
	public void startSimulation() {
		tabbedPane.setSelectedIndex(1);
	}

	/**
	 * Save the current configuration into the external file
	 * 
	 * @param path
	 *            The full path of the configuration file
	 */
	public void saveAsFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilterUtils(new String[] { "xml" },
				true, "XML File (*.xml)"));
		int result = fileChooser.showSaveDialog(this);
		// if we selected an image, load the image
		if (result == JFileChooser.APPROVE_OPTION) {
			String path = fileChooser.getSelectedFile().getPath();
			this.saveAsFile0(path);
		}
	}

	/**
	 * Save the current configuration into the external file
	 * 
	 * @param path
	 *            The full path of the configuration file
	 */
	private void saveAsFile0(String path) {
		if (engine.saveAppAsFile(path)) {
			MessageUtils.displayNormal("The application is saved.");
			return;
		}
	}

	/**
	 * Reset states of all system entries
	 */
	public void clearAll() {
		engine.clearAll();
		editView.clearAll();
		simView.clearAll();
	}

	/**
	 * Remove all categories and entities from the system.
	 */
	public void clearAll2() throws Exception {
		engine.clearAll2();
		editView.clearAll();
		simView.clearAll();
	}

	/**
	 * Remove the specified category from the system.
	 * 
	 * @param catName
	 *            Name of the category to remove
	 * @throws Exception
	 *             If exception throws.
	 */
	public void clearAll3(String catName) throws Exception {
		// Remove the dynamic class definition of the category
		engine.removeDynaCategory(catName);
		// Remove all entities of the category from the navigation panel
		java.util.List all = engine.system.getEntityByCategoryName(catName);
		for (int i = 0; i < all.size(); i++) {
			engine.navPanel.removeEntity(((Category) all.get(i)));
		}
		// Remove all entities of the category from the system
		boolean updatePanel = engine.system.removeAllEntities(catName);
		if (updatePanel)
		{
			engine.navPanel.removeAllEntityNodes();
			engine.navPanel.addAllEntities();
		}
		// Remove all entities of the category from the simulation world
		engine.categoryUpdateListener.categoryDeleted(catName);
		// Reset simulation world
		engine.system.resetWorld();
	}
}
