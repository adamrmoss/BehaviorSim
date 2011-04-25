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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * <p>
 * Global configuration parameters for all applications.
 * </p>
 * The parameters service as the default setting for all user-defined
 * applications.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class ConfigParameters {

	/** Environment version */
	public static final String version = "1.0";

	/** Global method index, used as part of method signatures. */
	public static int methodIndex = 0;

	/** Global behavior index, used in behavior network */
	public static int behaviorIndex = 0;

	/** Global entity index, used for the management of display names */
	private static Hashtable entityIndices = new Hashtable();

	/**
	 * Constant names for time steps, time interval, environment type and
	 * initial direction
	 */
	public static final String TOTALTIMESTEPS = "timesteps";
	public static final String TIMESTEPINTERVAL = "timestepinterval";
	public static final String ENV_TYPE = "environmenttype";
	public static final String INITIAL_MOTION_DIRECTION = "initialdirection";
	public static final String RESOURCEPATH = "resourcepath";

	/** Constant names for recent files */
	public static final String RECENT_FILE_NUMBER = "recent.file.number";
	public static final int RECENT_FILE_MAXIMUM_NUMBER = 5;
	/** Should be less than 10 */
	public static final String RECENT_FILE_PREFIX = "recent.file.";

	/** Repository for named parameters */
	private Hashtable paramsDepository;

	/** Plain configuration loader */
	public static Properties properties = new Properties();

	/** Constructor, used internally */
	ConfigParameters() {

		// Initial configuration
		paramsDepository = new Hashtable(15);
		paramsDepository.put(TOTALTIMESTEPS, new Integer(500));
		paramsDepository.put(ENV_TYPE, new Integer(
				SimulationEnvironment.ROUNDED));
		paramsDepository.put(TIMESTEPINTERVAL, new Integer(50));
		paramsDepository.put(INITIAL_MOTION_DIRECTION, new Double(0.0D));
		paramsDepository.put(RESOURCEPATH, "/");

		// The path to the property file
		init();

	}

	/**
	 * Load all system configuration properties
	 */
	public void init() {
		InputStream is = null;
		try {
			StringBuffer file = new StringBuffer(System.getProperty("SIM_HOME"));
			file.append(File.separator).append("config").append(File.separator);
			file.append(File.separator);
			file.append("behaviorsim.properties");
			is = new FileInputStream(file.toString());
			properties.load(is);
		} catch (FileNotFoundException e) {
			sim.util.MessageUtils
					.displayError("File behaviorsim.properties is not found.");
		} catch (IOException e) {
			sim.util.MessageUtils
					.displayError("Errors occured in reading behaviorsim.properties.");
		} finally {
			try {
				is.close();
			} catch (IOException e1) {
			}
		}
	}

	/**
	 * Save the modified properties
	 */
	public void saveProperties(Properties properties) {
		OutputStream os = null;
		try {
			StringBuffer file = new StringBuffer(System.getProperty("SIM_HOME"));
			file.append(File.separator).append("config").append(File.separator);
			file.append(File.separator);
			file.append("behaviorsim.properties");
			os = new FileOutputStream(file.toString());
			properties.store(os, "SYSTEM CONFIGURATION");
		} catch (FileNotFoundException e) {
			sim.util.MessageUtils
					.displayError("File behaviorsim.properties is not found.");
		} catch (IOException e) {
			sim.util.MessageUtils
					.displayError("Errors occured in writing behaviorsim.properties.");
		} finally {
			try {
				os.close();
			} catch (IOException e1) {
			}
		}
	}

	/**
	 * Return the system properties
	 * 
	 * @return The system properties
	 */
	public Properties getSystemProperties() {
		return properties;
	}

	/**
	 * 
	 * @return Resource path which is relative to path of application
	 *         configuration
	 */
	public String getResourcePath() {
		return (String) paramsDepository.get(RESOURCEPATH);
	}

	/**
	 * Set the new relative path of the application resources.
	 * 
	 * @param resourcePath
	 */
	public void setResourcePath(String resourcePath) {
		paramsDepository.put(RESOURCEPATH, resourcePath);
	}

	/**
	 * @return Total simulation ticks
	 */
	public int getTotalTimeticks() {
		return ((Integer) paramsDepository.get(TOTALTIMESTEPS)).intValue();
	}

	/**
	 * @return The interval between time steps
	 */
	public int getTimeStepInterval() {
		return ((Integer) paramsDepository.get(TIMESTEPINTERVAL)).intValue();
	}

	/**
	 * @return The environment type
	 */
	public int getEnvType() {
		return ((Integer) paramsDepository.get(ENV_TYPE)).intValue();
	}

	/**
	 * @return the initial motion direction of mobile entities
	 */
	public double getInitialMotionDirection() {
		return ((Double) paramsDepository.get(INITIAL_MOTION_DIRECTION))
				.doubleValue();
	}

	/**
	 * Return next available index for constructing the display name of the
	 * entity of the specified category.
	 * 
	 * @param categoryName
	 *            Name of entity category
	 * @return The next available index
	 */
	public int getDisplayNameIndex(String categoryName) {
		int inx = -1;
		Integer i = (Integer) entityIndices.get(categoryName);
		if (i == null) {
			inx = 1;
		} else {
			inx = i.intValue();
		}
		entityIndices.put(categoryName, new Integer(inx + 1));
		return inx;
	}

	/**
	 * Set value of the parameter
	 */
	public void save(String parameterName, Object value) {
		paramsDepository.put(parameterName, value);
	}

}
