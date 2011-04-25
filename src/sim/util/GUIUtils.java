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

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import sim.core.AppEngine;

/**
 * Utility class used for GUI specific tasks.
 */
public class GUIUtils {
	/** First color of the default gradient in the gradient panel. */
	public static final Color TITLEPANELCOLOR1 = Color.LIGHT_GRAY;

	/** Second color of the default gradient in the gradient panel. */
	public static final Color TITLEPANELCOLOR2 = new Color(170, 170, 170);

	/** Third color of the default gradient in the gradient panel. */
	public static final Color TITLEPANELCOLOR3 = Color.GRAY;

	/** The constants for the image icons. Types of image icon. */
	public static final int TREE_NODE_EXPAND_IMAGE = 0;
	public static final int TREE_NODE_UNEXPAND_IMAGE = 1;
	public static final int TREE_LEAF_IMAGE = 2;
	public static final int MENU_NEW_IMAGE = 3;
	public static final int MENU_EXIT_IMAGE = 4;
	public static final int MENU_OPEN_IMAGE = 5;
	public static final int MENU_SAVE_IMAGE = 6;
	public static final int MENU_SAVEAS_IMAGE = 7;
	public static final int MENU_CATEGORY_IMAGE = 8;
	public static final int MENU_BEHAVIORNETWORK_IMAGE = 9;
	public static final int MENU_SIMULATION_IMAGE = 10;
	public static final int MENU_ABOUT_IMAGE = 11;
	public static final int MENU_HELP_IMAGE = 12;
	public static final int ERROR_ICON = 13;
	public static final int MENU_AUTO_SAVE = 14;

	// /**
	// * Helper main-method to print the list of all set UIManager defaults.
	// *
	// * @param args are not used
	// */
	// public static void main(String[] args)
	// {
	// UIDefaults uiDefaults = UIManager.getDefaults ();
	// Enumeration enum = uiDefaults.keys();
	// while (enum.hasMoreElements())
	// {
	// Object key = enum.nextElement();
	// Object value = uiDefaults.get(key);
	// System.out.println(key + " = " + value);
	// }
	// }

	/**
	 * This is a Utility Class with static methods, prevent Instantiations.
	 */
	private GUIUtils() {
	}

	/**
	 * Returns an ImageIcon for each type of object used in the GUI. Returns an
	 * empty image if no viable choice can be made or no image is available.
	 * 
	 * @param type
	 *            The type of the image icon
	 * @return the correct Icon or an empty Icon for unknown objects
	 */
	public static ImageIcon getImageIconForObject(int type) {
		switch (type) {
		case TREE_NODE_EXPAND_IMAGE:
			return getImageIcon("/sim/ui/images/node_expand.gif");
		case TREE_NODE_UNEXPAND_IMAGE:
			return getImageIcon("/sim/ui/images/node_unexpand.gif");
		case TREE_LEAF_IMAGE:
			return getImageIcon("/sim/ui/images/leaf.gif");
		case MENU_NEW_IMAGE:
			return getImageIcon("/sim/ui/images/new.gif");
		case MENU_EXIT_IMAGE:
			return getImageIcon("/sim/ui/images/exit.gif");
		case MENU_OPEN_IMAGE:
			return getImageIcon("/sim/ui/images/open.gif");
		case MENU_SAVE_IMAGE:
			return getImageIcon("/sim/ui/images/save.gif");
		case MENU_SAVEAS_IMAGE:
			return getImageIcon("/sim/ui/images/saveAs.gif");
		case MENU_CATEGORY_IMAGE:
			return getImageIcon("/sim/ui/images/category.jpg");
		case MENU_BEHAVIORNETWORK_IMAGE:
			return getImageIcon("/sim/ui/images/bn.gif");
		case MENU_SIMULATION_IMAGE:
			return getImageIcon("/sim/ui/images/execute.gif");
		case MENU_ABOUT_IMAGE:
			return getImageIcon("/sim/ui/images/about.jpg");
		case MENU_HELP_IMAGE:
			return getImageIcon("/sim/ui/images/help.gif");
		case MENU_AUTO_SAVE:
			return getImageIcon("/sim/ui/images/periodic.jpg");
		case ERROR_ICON:
			return getImageIcon("/sim/ui/images/error-icon.jpg");
		default:
			return null;
		}
	}

	/**
	 * Return the image icon with the specific image path
	 * 
	 * @param path
	 *            The path of the image icon to return
	 * @return The image icon
	 */
	private static ImageIcon getImageIcon(String path) {
		ImageIcon viewIcon = new ImageIcon(AppEngine.getInstance().jrl
				.getImage(path.substring(1)));
		return viewIcon;
	}

	/**
	 * Returns a border used in the TitlePanels.
	 * 
	 * @return the titleLabelBorder
	 * @see csf.gui.components.BorderTitlePanel
	 */
	public static Border getTitleLabelBorder() {
		Border b1 = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE);
		Border b2 = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY);
		Border b3 = new EmptyBorder(3, 5, 3, 5);
		Border b = BorderFactory.createCompoundBorder(BorderFactory
				.createCompoundBorder(b2, b1), b3);

		return b;
	}

	/**
	 * Returns a border suited for Menus.
	 * 
	 * @param innerInset
	 * @param outerInset
	 * @return the etched Border
	 */
	public static Border getEtchedBottomBorder(int innerInset, int outerInset) {
		Border b0 = BorderFactory.createEmptyBorder(0, 0, innerInset, 0);
		Border b1 = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY);
		Border b2 = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE);
		Border b3 = BorderFactory.createEmptyBorder(0, 0, outerInset, 0);

		Border b = BorderFactory.createCompoundBorder(BorderFactory
				.createCompoundBorder(BorderFactory
						.createCompoundBorder(b3, b2), b1), b0);

		return b;
	}

	/**
	 * Returns an array of the default gradient colors.
	 * 
	 * @return the default gradient colors
	 */
	public static Color[] getGradientTitlePanelColors() {
		return new Color[] { TITLEPANELCOLOR1, TITLEPANELCOLOR2,
				TITLEPANELCOLOR3 };
	}

	/**
	 * Changes the UIDefaults to some optically optimzed default.
	 * <p/>
	 * This removes some ugly bold fonts on labels and some double defined
	 * borders.
	 * 
	 * @param splashScreen
	 *            the SplashScreen to give feedback of the optimzation state
	 */
	public static void deuglyizeUIDefaults() {
		makeFontsPlain();
		makeSplitpaneNice();
		makeScrollPaneNice();
		makeTreeNice();
		makeTabbedPaneNice();
		makeScrollBarNice();

		makeToolbarNice();
		makeMenubarNice();

		makePopupMenuNice();
	}

	/**
	 * Makes all UIDefaults with Bold fonts to Plain fonts.
	 */
	private static void makeFontsPlain() {
		UIDefaults uiDefaults = UIManager.getDefaults();
		Enumeration enum1 = uiDefaults.keys();
		while (enum1.hasMoreElements()) {
			Object key = enum1.nextElement();
			Object value = uiDefaults.get(key);
			if (value instanceof FontUIResource) {
				Font font = ((FontUIResource) value).deriveFont(Font.PLAIN);
				uiDefaults.put(key, new FontUIResource(font));
			}
		}
	}

	/**
	 * Optimizes the appeareance of the JSplitPane.
	 */
	private static void makeSplitpaneNice() {
		UIManager.put("SplitPane.dividerSize", new Integer(1));
		UIManager.put("SplitPane.border ", new EmptyBorder(0, 0, 0, 0));
		UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
	}

	/**
	 * Optimizes the appeareance of the JScrollPane.
	 */
	private static void makeScrollPaneNice() {
	}

	/**
	 * Optimizes the appeareance of the JScrollBar.
	 */
	private static void makeScrollBarNice() {
	}

	/**
	 * Optimizes the appeareance of the JTree.
	 */
	private static void makeTreeNice() {
		UIManager.put("Tree.editorBorder", new EmptyBorder(0, 0, 0, 0));
		UIManager.put("Tree.collapsedIcon ",
				getImageIcon("/sim/ui/images/TreeCollapsed.PNG"));
		UIManager.put("Tree.expandedIcon",
				getImageIcon("/sim/ui/images/TreeExpanded.PNG"));
	}

	/**
	 * Optimizes the appeareance of the JMenubar.
	 */
	private static void makeMenubarNice() {
		UIManager.put("MenuBar.border", getEtchedBottomBorder(0, 2));
	}

	/**
	 * Optimizes the appeareance of the JToolbar.
	 */
	private static void makeToolbarNice() {
		UIManager.put("ToolBar.border", getEtchedBottomBorder(2, 5));
	}

	/**
	 * Optimizes the appeareance of the JPopupMenu.
	 */
	private static void makePopupMenuNice() {
		UIManager.put("PopupMenu.border", new sim.ui.tree.FlatBorder());
	}

	/**
	 * Optimizes the appeareance of the JTabbedPane.
	 */
	private static void makeTabbedPaneNice() {
	}

}
