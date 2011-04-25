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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sim.model.entity.Property;

/**
 * Dialog for editing system property
 * 
 * @author Fasheng Qiu
 * 
 */
public class EditSystemProperty extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 536030853592026670L;

	// controls
	private JTextField appnameTF = new JTextField(20);

	// app name
	private String appName = null;

	// application engine
	private Property para = null;

	public EditSystemProperty(Property p) {

		super(MainFrame.getInstance(), true);

		para = p;

		try {
			init();
			pack();
			Dimension frameSize = getPreferredSize();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (frameSize.height > screenSize.height)
				frameSize.height = screenSize.height;
			if (frameSize.width > screenSize.width)
				frameSize.width = screenSize.width;
			setLocation(((screenSize.width - frameSize.width) / 2),
					((screenSize.height - frameSize.height) / 2));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void init() {

		// panels
		JPanel contentPane = new JPanel();
		JPanel buttonBarGrid = new JPanel();
		JPanel controlsPanel = new JPanel();
		JPanel buttonBar = new JPanel();

		// controls: buttons, text boxes, Labels
		JButton cancelButton = new JButton();
		JButton okButton = new JButton();
		JLabel nameLabel = new JLabel("App Name: ");

		BorderLayout borderLayout = new BorderLayout();
		GridLayout buttonBarGridLayout = new GridLayout();
		GridBagLayout controlsGridBagLayout = new GridBagLayout();
		FlowLayout buttonBarFlowLayout = new FlowLayout();

		setTitle("Edit application properties...");
		setContentPane(contentPane);
		contentPane.setPreferredSize(new Dimension(300, 150));
		contentPane.setLayout(borderLayout);
		buttonBar.setLayout(buttonBarFlowLayout);
		buttonBarGrid.setLayout(buttonBarGridLayout);
		buttonBarGridLayout.setColumns(2);
		buttonBarGridLayout.setHgap(5);

		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});

		cancelButton.setText("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonBarGrid.add(okButton);
		buttonBarGrid.add(cancelButton);
		buttonBar.add(buttonBarGrid);

		appnameTF.setText((String.valueOf(para.value)));

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(nameLabel);
		p.add(appnameTF);

		controlsPanel.setLayout(controlsGridBagLayout);

		controlsPanel.add(p, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

		contentPane.add(controlsPanel, BorderLayout.NORTH);
		contentPane.add(buttonBar, BorderLayout.SOUTH);

	}

	public void onOk() {

		String name = appnameTF.getText().trim();
		if (name.equals("")) {
			sim.util.MessageUtils
					.displayWarning("Application name is required.");
			return;
		}
		appName = name;
		dispose();
	}

	public String getNewAppName() {
		return appName;
	}

	public void onCancel() {
		dispose();
	}

}
