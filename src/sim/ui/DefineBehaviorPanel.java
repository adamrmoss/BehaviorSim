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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SpringLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import sim.core.AppEngine;
import sim.model.behavior.Behavior;
import sim.model.entity.BNCategory;
import sim.ui.method.EJEArea;
import sim.ui.panels.BehaviorNetworkDefinePanel;
import sim.util.MessageUtils;

/**
 * Panel for creating/editing a behavior. The panel is triggered in the behavior
 * network definition panel. It is only used to create or modify a selected
 * behavior for the specified agent.
 * 
 * @author Fasheng Qiu
 * 
 */
public class DefineBehaviorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7358699042117527613L;
	/** Controls */
	private JTextField nameText = new JTextField(15);
	private EJEArea equationText = new EJEArea(null);
	private EJEArea actionText = new EJEArea(null);
	private JCheckBox resumeCB = new JCheckBox("Setup non-resumable task queue");

	/** The parent dialog of this panel */
	private BehaviorView dlgParent = null;
	private BehaviorNetworkDefinePanel owner = null;

	/** The target category object */
	private BNCategory bnEntity = null;

	/** To modify existing behavior ? */
	private boolean modify = false;
	private int behaviorID;
	private String behaviorName;

	/** Application engine */
	private AppEngine eng = AppEngine.getInstance();

	/** Constructor */
	public DefineBehaviorPanel(BehaviorView parent,
			BehaviorNetworkDefinePanel owner, BNCategory c) {
		this(parent, owner, c, Behavior.NO_BEHAVIOR);
	}

	public DefineBehaviorPanel(BehaviorView parent,
			BehaviorNetworkDefinePanel owner, BNCategory c, Behavior behavior) {
		this.bnEntity = c;
		this.dlgParent = parent;
		this.owner = owner;

		equationText.setPredefined(c);
		actionText.setPredefined(c);
		equationText.setText("");
		equationText.setCaretPosition(0);
		actionText.setText("");
		actionText.setCaretPosition(0);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init(behavior);
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

	/** Change to edit another behavior */
	public void setBehavior(Behavior behavior) {
		if (behavior == null || behavior == Behavior.NO_BEHAVIOR) {
			nameText.setText("B_");
			equationText.setText("");
			actionText.setText("");
			resumeCB.setSelected(false);
			modify = false;
		} else {
			nameText.setText(behavior.getBehaviorName());
			resumeCB.setSelected(behavior.isResumable());
			setBehaviorEquation(behavior.getBehaviorEquation());
			actionText.setText(eng.getBehaviorActionString(behavior.getMyId()));
			modify = true;
			behaviorID = behavior.getMyId();
			behaviorName = behavior.getBehaviorName();
		}
	}

	/** Set behavior equation, separating it in several rows */
	private void setBehaviorEquation(String eq) {
		eq = eq.replace('\n', ' ');
		equationText.setText(eq.replaceAll(";", ";\n"));
	}

	/** Initialize controls */
	private void init(Behavior behavior) {

		/** Controls */
		JButton cancelButton = new JButton();
		JButton okButton = new JButton();
		SpringLayout layout = new SpringLayout();
		JLabel equationLabel = new JLabel();
		JScrollPane equationScrollPane1 = new JScrollPane();
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
		cancelButton.setText("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// parent.onDispose();
			}
		});

		/** Initialize the components */
		setBorder(new CompoundBorder(new TitledBorder(null, "Behavior Details",
				TitledBorder.LEFT, TitledBorder.TOP), new EmptyBorder(5, 5, 5,
				5)));
		setBehavior(behavior);
		nameText.selectAll();
		equationLabel.setText("Behavior excitation:");

		equationText.setEditable(true);
		equationText.setBackground(Color.WHITE);
		JViewport port = equationScrollPane1.getViewport();
		port.setOpaque(true);
		port.setBackground(Color.red);
		port.setView(equationText);
		port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		equationScrollPane1.setPreferredSize(new Dimension(500, 150));
		equationScrollPane1.setMaximumSize(equationScrollPane1
				.getPreferredSize());

		actionText.setEditable(true);
		actionText.setBackground(Color.WHITE);
		port = actionScrollPanel.getViewport();
		port.setOpaque(true);
		port.setBackground(Color.red);
		port.setView(actionText);
		port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		actionScrollPanel.setPreferredSize(new Dimension(500, 250));
		actionScrollPanel
				.setMaximumSize(equationScrollPane1.getPreferredSize());

		Font font = new Font("Times New Roman", Font.ITALIC, 12);
		JLabel helper = new JLabel("Need help?");
		helper.setFont(font);
		helper.setForeground(Color.BLUE);
		helper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		helper.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Dialog
				JOptionPane op = new JOptionPane(new HelpPanel());
				JDialog dialog = op.createDialog(DefineBehaviorPanel.this,
						("Help of Creating/Updating Behaivor"));
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

		/** Advanced settings */
		resumeCB.setVisible(false);
		final JCheckBox advanced = new JCheckBox("Advanced settings");
		advanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (advanced.isSelected()) {
					resumeCB.setVisible(true);
				} else {
					resumeCB.setVisible(false);
				}
			}
		});

		/** add components */
		JLabel nameL = new JLabel("Behavior name:");
		JLabel actionL = new JLabel("Behavior action:");
		add(nameL);
		add(nameText);
		add(equationLabel);
		add(equationScrollPane1);

		add(actionL);
		add(actionScrollPanel);
		add(helper);
		add(advanced);
		add(resumeCB);
		add(okButton); // add(cancelButton);

		/** set constraints */
		layout.putConstraint(SpringLayout.WEST, nameL, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, nameL, 5, SpringLayout.NORTH,
				this);

		layout.putConstraint(SpringLayout.WEST, nameText, 10,
				SpringLayout.EAST, nameL);
		layout.putConstraint(SpringLayout.NORTH, nameText, 5,
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, equationLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, equationLabel, 10,
				SpringLayout.SOUTH, nameL);
		layout.putConstraint(SpringLayout.WEST, equationScrollPane1, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, equationScrollPane1, 5,
				SpringLayout.SOUTH, equationLabel);

		layout.putConstraint(SpringLayout.WEST, actionL, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, actionL, 5,
				SpringLayout.SOUTH, equationScrollPane1);
		layout.putConstraint(SpringLayout.WEST, actionScrollPanel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, actionScrollPanel, 5,
				SpringLayout.SOUTH, actionL);

		layout.putConstraint(SpringLayout.WEST, helper, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, helper, 10,
				SpringLayout.SOUTH, actionScrollPanel);

		layout.putConstraint(SpringLayout.WEST, advanced, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, advanced, 10,
				SpringLayout.SOUTH, helper);

		layout.putConstraint(SpringLayout.WEST, resumeCB, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, resumeCB, 5,
				SpringLayout.SOUTH, advanced);

		layout.putConstraint(SpringLayout.WEST, okButton, 200,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, okButton, 10,
				SpringLayout.SOUTH, resumeCB);
	}

	/** Create or update behavior */
	public void onOk() {

		// Check input
		if (nameText.getText().trim().equals("")
				|| equationText.getText().trim().equals("")) {
			MessageUtils
					.displayError("Behavior name and excitation are required.");
			return;
		}

		// Create or update the behavior
		String bev = nameText.getText().trim();
		if (!modify) {

			/**
			 * Check whether the behavior is already existing in the behavior
			 * network
			 */
			Behavior b = null;
			if ((b = eng.bnEditor.getBehavior(bnEntity, bev)) != Behavior.NO_BEHAVIOR) {

				String[] options = { "Yes", "No" };
				int ret = JOptionPane
						.showOptionDialog(
								this,
								"Behavior with the name '"
										+ bev
										+ "' already exists. \nDo you want to create a behavior with same name?",
								"Confirm", JOptionPane.YES_NO_OPTION,
								JOptionPane.INFORMATION_MESSAGE, null, options,
								options[0]);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}

			}

			try {
				b = eng.createNewBehavior(bnEntity, bev, equationText.getText()
						.trim(), resumeCB.isSelected(), actionText.getText()
						.trim());
				dlgParent.behaviorAddedToNetwork(new int[] { b.getMyId() });
			} catch (Exception e) {
				eng.removeBehavior(bnEntity, b, true);
				MessageUtils.displayError(e);
				return;
			}
			try {
				/** Behavior image painted */
				dlgParent.behaviorDefined(bnEntity, b.getMyId());
				/** Network panel and Navigation tree updated */
				owner.behaviorNetworkChanged();
				MessageUtils
						.displayNormal("The behavior is created successfully.");
			} catch (Exception e) {
				MessageUtils.displayError(e);
				return;
			}
		} else {
			try {
				/** Update behavior and behavior network */
				eng.updateBehavior(bnEntity, behaviorID, bev, equationText
						.getText().trim(), resumeCB.isSelected(), actionText
						.getText().trim());
				if (!bev.equals(behaviorName)) {
					/** Update navigation tree */
					/** Update behavior network tree */
					/** Update names in behavior network */
					try {
						/** Behavior image painted */
						dlgParent.behaviorDefined(bnEntity, behaviorID);
						/** Network panel and Navigation tree updated */
						owner.behaviorNetworkChanged();
						MessageUtils
								.displayNormal("The behavior is created successfully.");
					} catch (Exception e) {
						MessageUtils.displayError(e);
						return;
					}
				}
				MessageUtils
						.displayNormal("The behavior is updated successfully.");
			} catch (Exception e) {
				MessageUtils.displayError(e);
				return;
			}
		}
	}

	/**
	 * The help panel
	 */
	public class HelpPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5526326609549240931L;

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
							"Behavior name: Name of the behavior. It is uneditable for the existing behavior.\n"));
			this
					.add(new JLabel(
							"Behavior excitation: Java code for calculating behavior excitation.\n"));
			this
					.add(new JLabel(
							"Behavior action: Java code for deciding behavior action.\n"));
			this
					.add(new JLabel(
							"Non-resumable task queue: Selected if the task queue can not be reset if the behavior is switched.\n"));
			this.add(new JLabel("\n"));

			this
					.add(new JLabel(
							"Note that: Both behavior excitation and action should be valid Java code. \n"));
			this
					.add(new JLabel(
							"	1) For behavior excitation, a double value should be returned. e.g. return 1.0.\n"));
			this.add(new JLabel(
					"	2) There are two ways to define behavior action.\n"));
			this
					.add(new JLabel(
							"		a, Task-queue approach. The java code only consists of a series of statement TASKQUEUE.add(JAVA_METHOD_CALL). \n"));
			this
					.add(new JLabel(
							"          JAVA_METHOD_CALL is the call of a java method which returns a String object \"SPEED, DIRECTION\" used to guide the movement.\n"));
			this
					.add(new JLabel(
							"          SPEED is moving speed and DIRECTION is moving direction. Either is a double number.\n"));
			this
					.add(new JLabel(
							"          e.g. TASKQUEUE.add(move(1.0, 0.0)); TASKQUEUE.add(move(2.0, 1.57));"));
			this
					.add(new JLabel(
							"		b, A simpler way. The java code is the body of a valid method which returns a String object \"SPEED, DIRECTION\". \n"
									+ "					       e.g. return move(1.0, 0.0);\n"));
			this
					.add(new JLabel(
							"In both behavior excitation and action, type @ to show all predefined methods. \n"));

		}

	}
}
