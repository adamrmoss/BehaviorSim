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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 158671876626293866L;

	/** Parent panel */
	private HelpPanel parent;

	/** Text field */
	private JTextField text;

	/**
	 * Constructor
	 * 
	 * @param p
	 *            Parent panel
	 */
	public SearchPanel(HelpPanel p) {
		parent = p;
		init();
	}

	/**
	 * Initialize the components
	 */
	public void init() {
		super.add(new JLabel("Text to search: "));

		text = new JTextField(10);
		super.add(text);

		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(this);
		super.add(searchButton);

		Dimension frameSize = parent.getSize();
		setSize(new Dimension((int) frameSize.getWidth(), 20));

	}

	/**
	 * Action to perform
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Search")) {
			String t = text.getText().trim();
			if (t.equals("")) {
				JOptionPane.showMessageDialog(parent,
						"Please type in the text to search.", "Search text",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			parent.searchText(t);
		}
	}
}
