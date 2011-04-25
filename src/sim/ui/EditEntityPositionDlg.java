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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Update entity position
 * 
 * @author Pavel
 * @version 1.0
 */
public class EditEntityPositionDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1405138704838945630L;

	/**
	 * The x position of the entity
	 */
	private JTextField xText = new JTextField();

	/**
	 * The y position of the entity
	 */
	private JTextField yText = new JTextField();

	/**
	 * Navigation panel
	 */
	private NavigationPanel navPanel;

	/**
	 * X position
	 */
	public int xCoordinate = 0;

	/**
	 * y position
	 */
	public int yCoordinate = 0;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Navigation panel
	 * @param title
	 *            The title of the dialog
	 * @param xPos
	 *            The x position
	 * @param yPos
	 *            The y position
	 */
	public EditEntityPositionDlg(NavigationPanel parent, String title,
			int xPos, int yPos) {
		super(MainFrame.getInstance(), true);
		setTitle(title);
		navPanel = parent;
		xCoordinate = xPos;
		yCoordinate = yPos;

		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			init();
			Point x = new Point(0, 0);// .getLocation();
			Dimension screenSize = navPanel.getSize();
			pack();
			Dimension frameSize = getPreferredSize();
			if (x.x <= 0) {
				x = new Point(0, 0);
				screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			}
			if (frameSize.height > screenSize.height)
				frameSize.height = screenSize.height;
			if (frameSize.width > screenSize.width)
				frameSize.width = screenSize.width;
			setLocation(((screenSize.width - frameSize.width) / 2) + x.x,
					((screenSize.height - frameSize.height) / 2) + x.y);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initialize GUI components
	 */
	private void init() {

		JPanel contentPane = new JPanel();
		JButton cancelButton = new JButton();
		JButton okButton = new JButton();
		JPanel controls = new JPanel();
		JPanel buttonBar = new JPanel();
		FlowLayout buttonBarFlowLayout = new FlowLayout();
		JPanel buttonBarGrid = new JPanel();
		GridLayout buttonBarGridLayout = new GridLayout();
		BorderLayout borderLayout = new BorderLayout();
		GridBagLayout clientGridBagLayout = new GridBagLayout();
		JLabel chooseLabel = new JLabel();
		JLabel xLabel = new JLabel();
		JLabel yLabel = new JLabel();

		setContentPane(contentPane);
		contentPane.setLayout(borderLayout);
		buttonBar.setLayout(buttonBarFlowLayout);
		buttonBarGrid.setLayout(buttonBarGridLayout);
		buttonBarGridLayout.setColumns(2);
		buttonBarGridLayout.setHgap(6);
		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		// okButton.addKeyListener(this);
		cancelButton.setText("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// cancelButton.addKeyListener(this);
		controls.setLayout(clientGridBagLayout);
		chooseLabel.setHorizontalAlignment(SwingConstants.CENTER);
		chooseLabel.setText("Specify the position:");
		xLabel.setText("x:");
		yLabel.setText("y:");
		xText.setText(Integer.toString(xCoordinate));
		yText.setText(Integer.toString(yCoordinate));

		buttonBarGrid.add(okButton);
		buttonBarGrid.add(cancelButton);
		buttonBar.add(buttonBarGrid);

		controls.add(chooseLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));
		controls.add(xLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						4, 4, 4), 0, 0));
		controls.add(xText, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));
		controls.add(yLabel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));
		controls.add(yText, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

		contentPane.add(controls, BorderLayout.CENTER);
		contentPane.add(buttonBar, BorderLayout.SOUTH);
	}

	/**
	 * Update the entity position
	 */
	public void onOk() {
		int x, y;
		try {
			x = Integer.valueOf(xText.getText()).intValue();
		} catch (NumberFormatException e) {
			sim.util.MessageUtils.displayError("x should be an integer!");
			return;
		}
		try {
			y = Integer.valueOf(yText.getText()).intValue();
		} catch (NumberFormatException e) {
			sim.util.MessageUtils.displayError("y should be an integer!");
			return;
		}
		xCoordinate = x;
		yCoordinate = y;

		dispose();
	}

	/**
	 * Dispose the dialog
	 */
	public void onCancel() {
		dispose();
	}

}
