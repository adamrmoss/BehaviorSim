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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SpringLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import sim.core.AppEngine;
import sim.model.entity.BNCategory;
import sim.model.mechanism.SystemDynamicMechanism;
import sim.ui.method.EJEArea;
import sim.util.MessageUtils;

public class DefineGeneralDyanmicsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8760126965977699126L;

	/** Controls */
	private EJEArea actionText = new EJEArea(null);

	/** The target category object */
	private BNCategory bnEntity = null;

	/** App engine */
	private AppEngine eng = AppEngine.getInstance();

	/** Original code */
	private String oldCode = null;

	/** Constructor */
	public DefineGeneralDyanmicsPanel(BNCategory c) {

		this.bnEntity = c;

		actionText.setPredefined(bnEntity);
		actionText.setText("");
		actionText.setCaretPosition(0);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init();
		Dimension frameSize = new Dimension(500, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		setLocation(((screenSize.width - frameSize.width) / 2),
				((screenSize.height - frameSize.height) / 2));
		setSize(frameSize);

	}

	/** Initialize the code */
	public void initCode() {
		SystemDynamicMechanism m = (SystemDynamicMechanism) bnEntity
				.getOriginalActionSelectionMechanism();
		String code = m.getCode();
		if (code != null) {
			actionText.setText(code);
			oldCode = code;
		} else {
			actionText.setText("");
			oldCode = "";
		}
	}

	/** Initialize controls */
	private void init() {

		/** Controls */
		JButton okButton = new JButton();
		JButton closeButton = new JButton();
		SpringLayout layout = new SpringLayout();
		JScrollPane actionScrollPanel = new JScrollPane();

		/** Layout */
		setLayout(layout);

		/** Buttons */
		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		/** Initialize the components */
		setBorder(new CompoundBorder(
				new TitledBorder(null, "Specify entity dynamics",
						TitledBorder.LEFT, TitledBorder.TOP), new EmptyBorder(
						5, 5, 5, 5)));

		actionText.setEditable(true);
		actionText.setBackground(Color.WHITE);
		JViewport port = actionScrollPanel.getViewport();
		port.setOpaque(true);
		port.setBackground(Color.red);
		port.setView(actionText);
		port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		actionScrollPanel.setPreferredSize(new Dimension(500, 250));
		actionScrollPanel.setMaximumSize(actionScrollPanel.getPreferredSize());

		Font font = new Font("Times New Roman", Font.ITALIC, 12);
		JLabel helper = new JLabel("Need help?");
		helper.setFont(font);
		helper.setForeground(Color.BLUE);
		helper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		helper.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Dialog
				JOptionPane op = new JOptionPane(new HelpPanel());
				JDialog dialog = op.createDialog(
						DefineGeneralDyanmicsPanel.this,
						("Help of Creating/Updating General Dynamics"));
				// Set the size of the window
				dialog.setSize(600, 320);
				// Center the dialog on the parent window.
				Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				Dimension guiSize = dialog.getSize();
				dialog.setLocation((screenSize.width - guiSize.width) / 2,
						(screenSize.height - guiSize.height) / 2);
				dialog.setVisible(true);
			}
		});

		/** add components */
		JLabel nameL = new JLabel("General Dynamics:");

		add(nameL);
		add(actionScrollPanel);
		add(helper);
		add(okButton); // add(closeButton);

		/** set constraints */
		layout.putConstraint(SpringLayout.WEST, nameL, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, nameL, 5, SpringLayout.NORTH,
				this);

		layout.putConstraint(SpringLayout.WEST, actionScrollPanel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, actionScrollPanel, 5,
				SpringLayout.SOUTH, nameL);

		layout.putConstraint(SpringLayout.WEST, helper, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, helper, 10,
				SpringLayout.SOUTH, actionScrollPanel);

		layout.putConstraint(SpringLayout.WEST, okButton, 150,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, okButton, 10,
				SpringLayout.SOUTH, actionScrollPanel);
		layout.putConstraint(SpringLayout.WEST, closeButton, 50,
				SpringLayout.EAST, okButton);
		layout.putConstraint(SpringLayout.NORTH, closeButton, 10,
				SpringLayout.SOUTH, actionScrollPanel);

		/** Initialize the original code for general dynamics of this entity */
		initCode();
	}

	/** Create or update behavior */
	public void onOk() {
		// Check the definition
		String code = actionText.getText().trim();
		if (code.equals("")) {
			int ret = JOptionPane
					.showConfirmDialog(
							this,
							"No General Dynamics is provided. Do you want to remove General Dynamics from the entity '"
									+ bnEntity.getDisplayName() + "'",
							"Warning", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.NO_OPTION)
				return;
			code = null;
		} else if (code.equals(oldCode)) {
			return;
		}
		// Update the General Dynamics
		try {
			// Update the definition
			bnEntity.registerGeneralDynamics(code);
			// Update the navigation tree
			eng.navPanel.setGeneralDynamics(bnEntity, code == null);
			// Set dirty
			eng.appManager.currentApp.setDirty(true);
			MessageUtils.displayNormal("General Dynamics for entity '"
					+ bnEntity.getDisplayName() + "' is updated successfully.");
		} catch (Exception e) {
			MessageUtils.displayError(e);
			return;
		}
		
	}
	
	/**
	 * The help panel
	 */
	public class HelpPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4309168786932209315L;

		/**
		 * Constructs an HelpPanel object
		 */
		public HelpPanel() {

			// Create the main panel
			setLayout(new GridLayout(16, 1));
			setBorder(new EmptyBorder(0, 5, 5, 5));

			// Create the document contents string
			this.add(new JLabel(".\n"));
			this
					.add(new JLabel(
							"General dynamics of the specified entity can be specified through the text area.\n"));
			this.add(new JLabel(
					"It is same as the definition of method body in Java.\n"));
			this.add(new JLabel("\n"));

			this
					.add(new JLabel(
							"Note that: Task queue can not be used in general dynamics. In other words,\n"));
			this
					.add(new JLabel(
							"TASKQUEUE.add(...) does not work in the definition of general dynamics.\n"));
			this
					.add(new JLabel(
							"However, other methods can be used in general dynamics. \n"));

		}

	}
}
