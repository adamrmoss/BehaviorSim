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

package sim.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sim.core.AppEngine;

public class BaseHelpPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2049221447039005401L;

	/** The help "htm" file for specific topic */
	private String htmlFile = null;

	/** Application engine */
	AppEngine engineRef = AppEngine.getInstance();

	/** Panel */
	JEditorPane editorPane = null;

	public BaseHelpPanel(String html) {

		// Save the file
		this.htmlFile = html;

		// Set layout and border
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setLayout(new BorderLayout());

		// Create an editor pane.
		editorPane = createEditorPane();
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));
		editorScrollPane.setForeground(Color.WHITE);

		// Add component
		super.add(editorScrollPane, BorderLayout.CENTER);

	}

	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		java.net.URL helpURL = DataSourceHelper.loadPage(htmlFile);
		if (htmlFile != null && !htmlFile.equals("") && helpURL != null) {
			try {
				editorPane.setPage(helpURL);
			} catch (IOException e) {
				System.err.println("Attempted to read a bad URL: " + helpURL);
			}
		} else {
			System.err.println("Couldn't find file: " + htmlFile);
		}
		return editorPane;
	}

	public void getHTMLStream(java.io.Writer writer) throws Exception {
		editorPane.write(writer);
	}

}
