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

package sim.core;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

/**
 * The application resources
 * 
 * <p>
 * Used to provide resources for BehaviorSim GUI components. The resources
 * should be contained in the jar file SIM_HOME/lib/behaviorsim.jar OR
 * SIM_HOME/bin/ OR SIM_HOME/classes/.
 * </p>
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class AppResources {

	/* Image for play button used in the simulation panel */
	public static final String playImagePath = "/sim/ui/images/play.jpg";

	/* Image for stop button used in the simulation panel */
	public static final String stopImagePath = "/sim/ui/images/stop.jpg";

	/* Image for pause button used in the simulation panel */
	public static final String pauseImagePath = "/sim/ui/images/pause.jpg";

	/* Image for execution button used in the tool bar */
	public static final String systIconPath = "/sim/ui/images/execute.gif";

	/* Image for simulation background */
	public static final String seaBitmapPath = "/sim/ui/images/blank.jpg";

	/* Image for simulation background */
	public Image seaImage = null;

	/* Media tracker for image loading */
	private Component component = null;

	/* Application engine */
	private AppEngine e;

	/**
	 * Initialize the application resources. Get the sea image from the class
	 * library.
	 * 
	 * @param c
	 *            Media tracker for image loading
	 * @param engine
	 *            The application engine
	 */
	public void initialize(Component c, AppEngine engine) throws Exception {

		// Media tracker
		component = c;

		// Initialize engine
		e = engine;

		// Load the sea image
		seaImage = getImage(seaBitmapPath);

	}

	/**
	 * Return the image according to the given image path.
	 * 
	 * @param imagePath
	 *            The image path
	 * @return The image
	 * @throws Exception
	 *             If the image can not be loaded
	 */
	public Image getImage(String imagePath) throws Exception {

		Image image = Toolkit.getDefaultToolkit().createImage(
				e.jrl.getImage(imagePath));

		MediaTracker tracker = new MediaTracker(component);
		tracker.addImage(image, 0);
		tracker.waitForAll();

		return image;
	}
}
