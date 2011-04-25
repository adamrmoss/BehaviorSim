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
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

/**
 * The AboutDialog is an information window that is called from the "About" item
 * in the <code>MainMenuBar</code>. It provides information about the project,
 * credit to contributors and the GPL license.
 * 
 * @author Fasheng Qiu
 */
public class AboutDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1037792743579661193L;
	// Data members
	private JButton closeButton; // The close button
	private JViewport viewPort; // The view port for the text pane

	/**
	 * Constructs an AboutDialog object
	 * 
	 * @param mainWindow
	 *            the main window
	 */
	public AboutDialog(MainFrame mainWindow) {

		// Use JDialog constructor
		super(
				mainWindow,
				"About BehaviorSim v"
						+ sim.core.ConfigParameters.version);

		// Finalize the window
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create the main panel
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPane);

		// Create the text panel
		// I'd like to replace this with an HTML parser text pane sometime
		// <Scott>
		JTextPane textPane = new JTextPane();
		DefaultStyledDocument document = new DefaultStyledDocument();
		textPane.setStyledDocument(document);
		textPane.setBackground(new Color(245, 245, 245));
		textPane.setEditable(false);

		Border b = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		Border compound = BorderFactory.createCompoundBorder(b, BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		textPane.setBorder(b);

		// Create the document contents string
		StringBuffer buf = new StringBuffer();
		buf
				.append("BehaviorSim v"
						+ sim.core.ConfigParameters.version
						+ "\n\n");

		buf
				.append("BehaivorSim is a learning environment for behavior-based agent. It supports learning of behavior-based control by defining simulated agents in an intuitive manner corresponding to the behavior-based paradigm. \n\n");
		buf
				.append("Developed and maintained by a research group led by Dr. Xiaolin Hu.\n");
		buf
				.append("Visit http://cs.gsu.edu/~cscxlh/BehaviorSim.htm for more information. \n");
		buf
				.append("Questions, suggestions and bugs report to xhu@cs.gsu.edu.\n");
		buf.append("\n");

		/*
		 * buf.append("This program is free software; you can redistribute it ");
		 * buf.append("and/or modify it under the terms of the GNU General ");
		 * buf.append("Public License as published by the Free Software ");
		 * buf.append
		 * ("Foundation; either version 2 of the License, or (at your ");
		 * buf.append("option) any later version.\n\n");
		 */

		buf
				.append("This program is free software; It is distributed in the hope that it will be ");
		buf
				.append("useful, but WITHOUT ANY WARRANTY; without even the implied ");
		buf
				.append("warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
		// buf.append("PURPOSE.  See the GNU General Public License for more details.");

		// Create the document
		try {
			document.insertString(0, buf.toString(), null);
		} catch (BadLocationException e) {
			System.err.println(e.toString());
		}

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setBorder(compound);
		viewPort = scrollPane.getViewport();
		viewPort.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		mainPane.add(scrollPane);

		// Create close button panel
		JPanel closeButtonPane = new JPanel(new FlowLayout());
		closeButtonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPane.add(closeButtonPane, "South");

		// Create close button
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		closeButtonPane.add(closeButton);

		// Set the size of the window
		setSize(500, 400);

		// Center the window on the parent window.
		Point parentLocation = mainWindow.getLocation();
		int Xloc = (int) parentLocation.getX()
				+ ((mainWindow.getWidth() - 350) / 2);
		int Yloc = (int) parentLocation.getY()
				+ ((mainWindow.getHeight() - 400) / 2);
		setLocation(Xloc, Yloc);

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