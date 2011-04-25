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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sim.core.AppEngine;
import sim.core.SimulationEnvironment;
import sim.core.dclass.JarResourceLoader;
import sim.model.entity.WhiteFilter;
import sim.util.FileFilterUtils;
import sim.util.MessageUtils;

/**
 * Editor for simulation environment
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class EditWorldDialog extends CommonDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4915097007041360757L;
	/** Controls */
	private JTextField ww = new JTextField(8);
	private JTextField hh = new JTextField(8);
	private JTextField picFileName = new JTextField(15);
	private JComboBox typeCB = new JComboBox(new Object[] { "Rounded", "Open",
			"Closed" });
	/** States */
	private int width, height, type;
	private int originalWidth, originalHeight;
	private String imagePath;
	private double widthRatio, heightRatio;

	/** World is changed */
	private boolean changed = false;

	/**
	 * @return the widthRatio
	 */
	public double getWidthRatio() {
		return widthRatio;
	}

	/**
	 * @return the heightRatio
	 */
	public double getHeightRatio() {
		return heightRatio;
	}

	/**
	 * Constructor
	 */
	public EditWorldDialog() {
		/** Initialize components */
		init();
		/** Initialize states */
		_initStates();
		/** Initialize the components */
		super.setTitle("World properties");
		/** Set size */
		super.setSize(420, 300);
	}

	/**
	 * Initialize control values
	 */
	private void _initStates() {
		SimulationEnvironment se = AppEngine.getInstance()
				.getSimulationEnvironment();
		originalWidth = width = se.getWidth();
		originalHeight = height = se.getHeight();
		ww.setText(String.valueOf(width));
		hh.setText(String.valueOf(height));

		type = se.getType();
		typeCB.setSelectedIndex(type);

		imagePath = se.getImagePath();
		picFileName.setEditable(false);
		picFileName.setText(imagePath);
	}

	/**
	 * Initialize the components used in this window.
	 */
	public void init() {


		/** Image */
		JLabel pictureLabel = new JLabel("Image:");
		JButton pictureChooser = new JButton("Choose");

		// picFileName.setEnabled(false);
		super.addToNewLine(pictureLabel);
		super.addToEnd(picFileName);
		super.addToEndMore(pictureChooser);
		pictureChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFromFile();
			}
		});

		/** Reset the range */
		final JCheckBox cb = new JCheckBox("Set the size as the image size.");
		super.addToNewLine(cb);
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cb.isSelected()) {
					if (!picFileName.getText().trim().equals("")) {
						// Is it the only way to read the local images?
						Image image = Toolkit.getDefaultToolkit().createImage(
								JarResourceLoader.getResource(picFileName
										.getText().trim(), getClass()));
						MediaTracker tracker = new MediaTracker(cb);
						try {
							tracker.addImage(image, 0);
							tracker.waitForAll();
						} catch (InterruptedException ee) {
						}
						// Adjust the image
						ImageFilter filter = new WhiteFilter();
						FilteredImageSource filteredImage = new FilteredImageSource(
								image.getSource(), filter);
						image = Toolkit.getDefaultToolkit().createImage(
								filteredImage);
						tracker = new MediaTracker(cb);
						try {
							tracker.addImage(image, 0);
							tracker.waitForAll();
						} catch (InterruptedException ee) {
						}
						originalWidth = width = image.getWidth(cb);
						originalHeight = height = image.getHeight(cb);
						ww.setText(String.valueOf(width));
						hh.setText(String.valueOf(height));

					}
				}
			}
		});

		/** Reset the range */
		super.addToNewLine(new JLabel("Size:"));
		super.addToEnd(ww);
		super.addToEndMore(hh);
		ww.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				checkWidthHeight();

			}
		});
		hh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				checkWidthHeight();

			}
		});
		Font font = new Font("Times New Roman", Font.ITALIC, 12);
		JLabel helper = new JLabel("Set As Default Size");
		helper
				.setToolTipText("Set the size of background as the default value.");
		helper.setFont(font);
		helper.setForeground(Color.BLUE);
		helper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		helper.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				ww.setText("624");
				hh.setText("540");
			}
		});
		super.addToEndMore(helper);

		/** Type */
		super.addToNewLine(new JLabel("Type:"));
		super.addToEnd(typeCB);

		/** Setup listeners for the save button */
		JButton saveButton = new JButton("OK");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		super.addToNewLine(saveButton);

		/** Setup listeners for the cancel button */
		JButton cancelButton = new JButton("CANCEL");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		super.addToEnd(cancelButton);

	}

	/**
	 * Check whether the width and height are valid
	 * 
	 * @return true if valid. false if not valid
	 */
	private boolean checkWidthHeight() {

		try {
			width = Integer.parseInt(ww.getText());
		} catch (Exception ee) {
			MessageUtils
					.displayError("The width is not valid. Please enter again.");
			return false;
		}

		try {
			height = Integer.parseInt(hh.getText());
		} catch (Exception ee) {
			MessageUtils
					.displayError("The height is not valid. Please enter again.");
			return false;
		}

		return true;
	}

	/**
	 * Load the app configuration from the external file
	 * 
	 * @param path
	 *            The full path of the configuration file
	 */
	private void loadFromFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (!picFileName.getText().equals(""))
			fileChooser.setCurrentDirectory(new File(picFileName.getText()));
		fileChooser.setFileFilter(new FileFilterUtils(new String[] { "jpg",
				"tif", "gif", "png" }, true,
				"Image File (*.jpg, *.gif, *.tif, *.png)"));
		int result = fileChooser.showOpenDialog(this);
		// if we selected an image, load the image
		if (result == JFileChooser.APPROVE_OPTION) {
			String path = fileChooser.getSelectedFile().getPath();
			picFileName.setText(path.trim());
		}
	}

	/**
	 * Action to perform
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			// Check the input
			if (!checkWidthHeight())
				return;
			// Upload the image
			String imPath = picFileName.getText().trim();
			try {
				imPath = sim.core.AppEngine.getInstance().uploadImage(imPath);
			} catch (Exception ex) {
				MessageUtils.displayError("The image can not be saved.");
				return;
			}
			// Update simulation environment
			SimulationEnvironment se = AppEngine.getInstance()
					.getSimulationEnvironment();
			se.setWidth(width);
			se.setHeight(height);
			se.setImagePath(imPath);
			se.setType(typeCB.getSelectedIndex());
			// Size change ratio
			widthRatio = 1.0 * width / originalWidth;
			heightRatio = 1.0 * height / originalHeight;
			// prepare images
			if (!se.getImagePath().trim().equals(imagePath)) {
				// Is it the only way to read the local images?
				Image image = Toolkit.getDefaultToolkit().createImage(
						JarResourceLoader.getResource(se.getImagePath(),
								getClass()));
				MediaTracker tracker = new MediaTracker(this);
				try {
					tracker.addImage(image, 0);
					tracker.waitForAll();
				} catch (InterruptedException ee) {
				}
				// Adjust the image
				ImageFilter filter = new WhiteFilter();
				FilteredImageSource filteredImage = new FilteredImageSource(
						image.getSource(), filter);
				image = Toolkit.getDefaultToolkit().createImage(filteredImage);
				tracker = new MediaTracker(this);
				try {
					tracker.addImage(image, 0);
					tracker.waitForAll();
				} catch (InterruptedException ee) {
				}
				se.setImage(image);

				// Delete the original image if possible
				AppEngine ae = AppEngine.getInstance();
				String configFile = ae.appManager.currentApp.getAppDir();
				if (configFile != null) {
					StringBuffer sb = new StringBuffer();
					sb.append(configFile);
					sb.append(File.separator);
					sb.append(ae.appManager.currentApp.getAppResourceDir());
					sb.append(File.separator);
					sb.append(se.getRelativeImagePath());
					try {
						// System.out.println(sb.toString());
						new File(sb.toString()).delete();
					} catch (Exception e2) {
						;
					}
				}

				// Set the new background image
				se.setRelativeImagePath(imPath.substring(imPath
						.lastIndexOf(File.separator) + 1));

			}
			if (!se.getImagePath().trim().equals(imagePath)
					|| typeCB.getSelectedIndex() != type
					|| width != originalWidth || height != originalWidth)
				changed = true;
		}
		setVisible(false);
		dispose();
	}

	/**
	 * Get env type
	 */
	public int getEnvType() {
		return typeCB.getSelectedIndex();
	}

	/**
	 * @return the changed
	 */
	public boolean isChanged() {
		return changed;
	}
}
