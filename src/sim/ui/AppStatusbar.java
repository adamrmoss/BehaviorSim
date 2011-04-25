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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import sim.core.ConfigParameters;

public class AppStatusbar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3383028167633177015L;

	protected JLabel messageLabel;

	protected JPanel centerPanel;

	protected JLabel textArea;

	protected JLabel appStatus;

	protected JLabel systemEditor;

	protected String title;

	private static AppStatusbar instance;

	public static AppStatusbar getInstance() {
		if (instance == null)
			instance = new AppStatusbar(
					"BehaviorSim v"
							+ ConfigParameters.version);
		return instance;
	}

	private AppStatusbar(String title) {

		this.title = "     " + title + "     ";
		centerPanel = new JPanel(new BorderLayout());
		messageLabel = new JLabel(this.title);
		textArea = new JLabel("");
		appStatus = new JLabel("");
		systemEditor = new JLabel("");
		setup();
		reset();
	}

	public void reset() {
		changeMessage("Define a new application or open an existing one.");
		changeAppStatus("App Not Defined");
	}

	public void setup() {
		setLayouts();

		add(messageLabel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);
		add(appStatus, BorderLayout.EAST);
		centerPanel.add(textArea, BorderLayout.CENTER);
		centerPanel.add(systemEditor, BorderLayout.EAST);
		addDetails();
	}

	public void addDetails() {
		messageLabel.setFont(new Font("dialog", Font.BOLD, 11));
		appStatus.setFont(new Font("dialog", Font.PLAIN, 12));
		appStatus.setForeground(Color.RED);
		appStatus.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
				Color.gray));

		textArea.setFont(new Font("dialog", Font.PLAIN, 12));
		textArea.setForeground(Color.BLUE);
		textArea.setBackground(messageLabel.getBackground());
		textArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		textArea.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
		textArea.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
				Color.gray));
		systemEditor.setFont(textArea.getFont());
		systemEditor.setForeground(textArea.getForeground());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screenSize.width / 2, 25));
	}

	public void setLayouts() {
		setLayout(new BorderLayout());
		centerPanel.setLayout(new BorderLayout());
	}

	public void registerListeners() {

	}

	public void changeMessage(String message) {
		textArea.setText(message);
	}

	public void changeFocusableInfo(String message) {
		systemEditor.setText(message);
	}

	public void changeAppStatus(String message) {
		appStatus.setText(message);
	}
}
