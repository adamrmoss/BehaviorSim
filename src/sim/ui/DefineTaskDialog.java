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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

public class DefineTaskDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428534934140414948L;

	private Integer[] intervalModel;

	private JComboBox intervalCombo;

	private int scheduledTime = -1;

	/**
	 * @return the scheduledTime
	 */
	public int getScheduledTime() {
		return scheduledTime;
	}

	public DefineTaskDialog(JFrame owner) {
		super(owner);
		super.setTitle("Schedule Periodical Application Saving Task");
		super.setModal(true);
		this.init();

		// Re-position this window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new Dimension(400, 200));
		setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);
	}

	public void init() {

		intervalModel = new Integer[5];
		intervalModel[0] = new Integer(20);
		intervalModel[1] = new Integer(30);
		intervalModel[2] = new Integer(40);
		intervalModel[3] = new Integer(50);
		intervalModel[4] = new Integer(60);

		intervalCombo = new JComboBox(intervalModel);
		intervalCombo.setSelectedIndex(3);

		JButton ok = new JButton("Schedule");
		JButton cancel = new JButton("Close");
		cancel.addActionListener(this);
		ok.addActionListener(this);

		SpringLayout layout = new SpringLayout();
		getContentPane().setLayout(layout);

		JLabel l1, l2;

		getContentPane()
				.add(
						l1 = new JLabel(
								"Specifiy time interval between successive executions:"));
		getContentPane().add(l2 = new JLabel("Time interval (second): "));
		getContentPane().add(intervalCombo);
		getContentPane().add(ok);
		getContentPane().add(cancel);

		layout
				.putConstraint(SpringLayout.WEST, l1, 15, SpringLayout.WEST,
						this);
		layout.putConstraint(SpringLayout.NORTH, l1, 15, SpringLayout.NORTH,
				this);

		layout
				.putConstraint(SpringLayout.WEST, l2, 15, SpringLayout.WEST,
						this);
		layout
				.putConstraint(SpringLayout.NORTH, l2, 20, SpringLayout.SOUTH,
						l1);

		layout.putConstraint(SpringLayout.WEST, intervalCombo, 5,
				SpringLayout.EAST, l2);
		layout.putConstraint(SpringLayout.NORTH, intervalCombo, 20,
				SpringLayout.SOUTH, l1);

		layout
				.putConstraint(SpringLayout.WEST, ok, 15, SpringLayout.WEST,
						this);
		layout.putConstraint(SpringLayout.NORTH, ok, 20, SpringLayout.SOUTH,
				intervalCombo);

		layout.putConstraint(SpringLayout.WEST, cancel, 5, SpringLayout.EAST,
				ok);
		layout.putConstraint(SpringLayout.NORTH, cancel, 20,
				SpringLayout.SOUTH, intervalCombo);

	}

	public void submitTask() {
		Object s = intervalCombo.getSelectedItem();
		scheduledTime = Integer.parseInt(String.valueOf(s));

	}

	public void actionPerformed(ActionEvent ev) {
		String c = ev.getActionCommand();
		if (c == "Schedule") {
			submitTask();
		}
		setVisible(false);
		dispose();

	}

}