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

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sim.core.dclass.JarResourceLoader;
import sim.model.entity.Entity;
import sim.model.entity.WhiteFilter;
import sim.util.FileFilterUtils;
import sim.util.MessageUtils;

/**
 * Editor for the display component of each entity
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class EditDisplayDialog extends CommonDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 384307059921408155L;
	/** Controls */
	private JTextField displayName = new JTextField(15);
	private JTextField picFileName = new JTextField(15);
	private JTextField ww = new JTextField(8);
	private JTextField hh = new JTextField(8);

	/** States */
	private int width, height;
	private int originalWidth, originalHeight;
	private String imagePath;
	private Entity entity;

	/** World is changed */
	private boolean changed = false;

	/**
	 * Constructor
	 */
	public EditDisplayDialog(Entity entity) {
		/** Save parameter */
		this.entity = entity;
		/** Initialize components */
		init();
		/** Initialize states */
		_initStates(entity);
		/** Initialize the components */
		super.setTitle("Display options");
		/** Set size */
		super.setSize(400, 300);
	}

	/**
	 * Initialize control values
	 */
	private void _initStates(Entity se) {

		originalWidth = width = se.getWidth();
		originalHeight = height = se.getHeight();

		ww.setText(String.valueOf(width));
		hh.setText(String.valueOf(height));

		imagePath = se.getImagePath();
		picFileName.setEditable(false);
		picFileName.setText(imagePath);

		displayName.setText(se.getDisplayName());
	}

	/**
	 * Initialize the components used in this window.
	 */
	public void init() {

		/** Image */
		JLabel pictureLabel = new JLabel("Image:");
		JButton pictureChooser = new JButton("Choose");

		super.addToNewLine(new JLabel("Name:"));
		super.addToEnd(displayName);

		super.addToNewLine(pictureLabel);
		super.addToEnd(picFileName);
		super.addToEndMore(pictureChooser);
		pictureChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFromFile();
			}
		});

		/** Reset the range */
		final JCheckBox cb = new JCheckBox(
				"Let the size same as the image size.");
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
						ww.setText(String.valueOf(image.getWidth(cb)));
						hh.setText(String.valueOf(image.getHeight(cb)));
					}
				}
			}
		});

		/** Reset the range */

		super.addToNewLine(new JLabel("Size:"));
		super.addToEnd(ww);
		super.addToEndMore(hh);

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
	 * Action to perform
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			// Check the validity of the width and height
			if (!checkWidthHeight())
				return;
			// Check the display name
			String disName = displayName.getText().trim();
			if (disName.equals("")) {
				MessageUtils.displayError("Name can not be left blank.");
				return;
			}
			// Original display name
			String odisName = entity.getDisplayName().trim();
			// Target entity
			Entity se = entity;
			// Set display name
			se.setDisplayName(disName);
			// Entity display width and height
			se.setEntityWidthHeight(width, height);
			// Upload the image into the resource directory
			String imPath = picFileName.getText().trim();
			try {
				imPath = sim.core.AppEngine.getInstance().uploadImage(imPath);
			} catch (Exception ex) {
				MessageUtils.displayError("The image can not be saved.");
				return;
			}
			// Set image path
			se.setImagePath(imPath);
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
				// Set the new image
				se.setImage(image);
				// Set the new image relative path
				se.setRelativeImagePath(imPath.substring(imPath
						.lastIndexOf(File.separator) + 1));
			}
			if (!odisName.equals(disName)
					|| !se.getImagePath().trim().equals(imagePath)
					|| width != originalWidth || height != originalHeight)
				changed = true;
		}
		setVisible(false);
		dispose();
	}

	/**
	 * @return the changed
	 */
	public boolean isChanged() {
		return changed;
	}
}
