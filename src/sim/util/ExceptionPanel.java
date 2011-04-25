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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Panel to display exception information. For the particular exception
 * <code>sim.configure.dclass.DynaException</code>, also displays exception
 * code, and root exception if necessary.
 * 
 * @author Fasheng Qiu
 * 
 */
class ExceptionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 843686122954705697L;
	/** Controls */
	private JButton okButton = new JButton();
	private JTextField codeTF = new JTextField(25);
	private JTextField briefInfoTF = new JTextField(40);
	private JTextArea detailInfo = new JTextArea(4, 40);
	private JLabel email = new JLabel("Your email address is:");
	private JLabel sending = new JLabel();
	private JTextField emailTF = new JTextField(25);

	/** Detailed bugs */
	private Exception e;
	private String detailedBugs;

	ExceptionDialog parent;

	/** Detailed info */
	public String toString() {
		StringBuffer sb = new StringBuffer(codeTF.getText());
		sb.append("\n").append(briefInfoTF.getText()).append("\n");
		sb.append("\n").append(detailedBugs).append("\n");
		return sb.toString();
	}

	/** Constructor */
	public ExceptionPanel(ExceptionDialog parent) {
		this(parent, null);
	}

	public ExceptionPanel(ExceptionDialog parent, Exception e) {
		try {
			this.parent = parent;
			init(e);
			Dimension frameSize = new Dimension(500, 600);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (frameSize.height > screenSize.height)
				frameSize.height = screenSize.height;
			if (frameSize.width > screenSize.width)
				frameSize.width = screenSize.width;
			setLocation(((screenSize.width - frameSize.width) / 2),
					((screenSize.height - frameSize.height) / 2));
			setSize(frameSize);
		} catch (Exception ee) {
			;
		}

	}

	/** Change to show the given exception */
	public void setException(Exception e) {
		this.e = e;
		sending.setText("");
		if (e == null) {

			codeTF.setText("");
			briefInfoTF.setText("");
			detailInfo.setText("");
			detailedBugs = "";
		} else {
			if (!(e instanceof sim.util.SimException)) {

				codeTF.setText("An exception is thrown.");
				briefInfoTF.setText(e.getMessage());

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				detailedBugs = sw.toString();
				detailInfo.setText(e.getMessage());

			} else {

				sim.util.SimException ee = (sim.util.SimException) e;
				if (ee.getStack() != null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ee.getStack().printStackTrace(pw);
					detailedBugs = sw.toString();
					detailInfo
							.setText(ee.getStack().getMessage()/* sw.toString() */);
				}

				codeTF.setText(ee.getCode());
				briefInfoTF.setText(ee.getBriefInfo());

			}
		}
	}

	/** Initialize controls */
	private void init(Exception e) {

		/** Controls */
		codeTF.setEditable(false);
		briefInfoTF.setEditable(false);
		detailInfo.setEditable(false);
		codeTF.setForeground(Color.RED);
		briefInfoTF.setForeground(Color.RED);
		detailInfo.setForeground(Color.RED);
		detailInfo.setBackground(briefInfoTF.getBackground());

		JButton closeButton = new JButton();
		SpringLayout layout = new SpringLayout();
		JLabel codeLabel = new JLabel("Code:");
		JLabel briefLabel = new JLabel("Exception:");
		JLabel detailsLabel = new JLabel("Details:");

		JScrollPane detailsSP = new JScrollPane();

		detailsSP.getViewport().add(detailInfo);

		/** Layout */
		setLayout(layout);

		/** Buttons */
		okButton.setText("Report Bugs");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		closeButton.setText("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});

		/** Initialize the components */
		setBorder(new CompoundBorder(new TitledBorder(null,
				"An exception arised", TitledBorder.LEFT, TitledBorder.TOP),
				new EmptyBorder(5, 5, 5, 5)));

		// setBorder(BorderFactory.createMatteBorder(
		// 0, 30, 0, 0, GUIUtils.getImageIconForObject(GUIUtils.ERROR_ICON)));

		setException(e);

		/** add components */
		Font font = new Font("Times New Roman", Font.ITALIC, 11);
		JLabel helper = new JLabel("Why am I asked for the email address?");
		helper.setFont(font);
		helper.setForeground(Color.BLUE);
		helper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		helper
				.setToolTipText("It is used to email the solution back to you, if you click the 'Report Bugs' button. \n No reply will be made without an valid email address.");

		sending.setForeground(Color.BLUE);

		codeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		briefLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		detailsLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		add(codeLabel);
		add(codeTF);
		add(briefLabel);
		add(briefInfoTF);
		add(detailsLabel);
		add(detailsSP);
		add(email);
		add(emailTF);
		add(helper);
		add(okButton);
		add(closeButton);
		add(sending);

		/** set constraints */
		layout.putConstraint(SpringLayout.WEST, codeLabel, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, codeLabel, 5,
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, codeTF, 25, SpringLayout.EAST,
				codeLabel);
		layout.putConstraint(SpringLayout.NORTH, codeTF, 5, SpringLayout.NORTH,
				this);

		layout.putConstraint(SpringLayout.WEST, briefLabel, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, briefLabel, 20,
				SpringLayout.SOUTH, codeLabel);
		layout.putConstraint(SpringLayout.WEST, briefInfoTF, 1,
				SpringLayout.EAST, briefLabel);
		layout.putConstraint(SpringLayout.NORTH, briefInfoTF, 20,
				SpringLayout.SOUTH, codeLabel);

		layout.putConstraint(SpringLayout.WEST, detailsLabel, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, detailsLabel, 20,
				SpringLayout.SOUTH, briefLabel);

		layout.putConstraint(SpringLayout.WEST, detailsSP, 15,
				SpringLayout.EAST, detailsLabel);
		layout.putConstraint(SpringLayout.NORTH, detailsSP, 20,
				SpringLayout.SOUTH, briefLabel);

		layout.putConstraint(SpringLayout.WEST, email, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, email, 20, SpringLayout.SOUTH,
				detailsSP);

		layout.putConstraint(SpringLayout.WEST, emailTF, 1, SpringLayout.EAST,
				email);
		layout.putConstraint(SpringLayout.NORTH, emailTF, 20,
				SpringLayout.SOUTH, detailsSP);

		layout.putConstraint(SpringLayout.WEST, helper, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, helper, 20,
				SpringLayout.SOUTH, emailTF);

		layout.putConstraint(SpringLayout.WEST, okButton, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, okButton, 20,
				SpringLayout.SOUTH, helper);

		layout.putConstraint(SpringLayout.WEST, closeButton, 5,
				SpringLayout.EAST, okButton);
		layout.putConstraint(SpringLayout.NORTH, closeButton, 20,
				SpringLayout.SOUTH, helper);

		layout.putConstraint(SpringLayout.WEST, sending, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, sending, 10,
				SpringLayout.SOUTH, closeButton);

	}

	/** Create or update behavior */
	public void onOk() {
		if (e != null) {
			if (!emailTF.isVisible()) {
				emailTF.setVisible(true);
			}
			// Email address
			final String emailadd = emailTF.getText().trim();
			if (emailadd.length() != 0 && !isValidEmailAddress(emailadd)) {
				MessageUtils.displayWarning("Your email address is not valid.");
				emailTF.requestFocus();
				return;
			}

			// Send email
			new Thread(new sim.core.AppWorker() {
				public void setUp() {
					okButton.setEnabled(false);
					sending.setText("Bugs are being sent... Please wait!");
					MessageUtils.displayNormal(sending.getText());
				}

				public void execute() {
					try {
						if (new EmailSender().send(emailadd, toString())) {
							MessageUtils.displayNormal(ExceptionPanel.this,
									"Thanks for reporting bugs to us!");
							onClose();
						}
					} catch (Exception e) {
						sending
								.setText("Report Bugs is failed. Make sure there is internet connection for your computer.");
						MessageUtils.displayError(sending.getText());
					}
				}

				public void tearDown() {
					okButton.setEnabled(true);
				}
			}).start();
		}

	}

	public void onClose() {
		parent.setVisible(false);
	}

	/**
	 * Validate the form of an email address.
	 * 
	 * <P>
	 * Return <tt>true</tt> only if
	 *<ul>
	 * <li> <tt>aEmailAddress</tt> can successfully construct an
	 * {@link javax.mail.internet.InternetAddress}
	 * <li>when parsed with "@" as delimiter, <tt>aEmailAddress</tt> contains
	 * two tokens which satisfy
	 * {@link hirondelle.web4j.util.Util#textHasContent}.
	 *</ul>
	 * 
	 *<P>
	 * The second condition arises since local email addresses, simply of the
	 * form "<tt>albert</tt>", for example, are valid for
	 * {@link javax.mail.internet.InternetAddress}, but almost always undesired.
	 */
	public boolean isValidEmailAddress(String aEmailAddress) {
		if (aEmailAddress == null)
			return false;
		boolean result = true;
		try {
			new javax.mail.internet.InternetAddress(aEmailAddress);
			if (!hasNameAndDomain(aEmailAddress)) {
				result = false;
			}
		} catch (javax.mail.internet.AddressException ex) {
			result = false;
		}
		return result;
	}

	private boolean hasNameAndDomain(String aEmailAddress) {
		String[] tokens = aEmailAddress.split("@");
		return tokens.length == 2 && tokens[0] != null
				&& !tokens[0].trim().equals("") && tokens[1] != null
				&& !tokens[1].trim().equals("");
	}

}
