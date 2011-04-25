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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JComponent;

import sim.core.dclass.DirResourceLoader;
import sim.core.dclass.JarResourceLoader;
import sim.core.dclass.ResourceLoader;
import sim.model.action.Action;
import sim.model.action.BehaviorAction;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.behavior.BehaviorNetworkEditor;
import sim.model.behavior.Edge;
import sim.model.entity.BNCategory;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Display;
import sim.model.entity.Entity;
import sim.model.entity.Property;
import sim.ui.NavigationPanel;
import sim.util.FileUtils;
import sim.util.MethodUtils;
import sim.util.Point;
import sim.util.SimException;

/**
 * <p>
 * Title: Simulation application engine
 * </p>
 * <p>
 * Description: Control object for all logics issued from GUI components or from
 * internal other components.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class AppEngine {

	// listener for the property change in category definition
	private PropertyListener proListener = null;

	// resource loader
	public ResourceLoader jrl = null;

	// application manager
	public AppManager appManager = null;

	// application task scheduler
	private AppTask appTask = null;

	// application system object
	public AppSystem system = null;

	// application resources
	public AppResources resources = null;

	// application helper
	public AppHelper helper = null;

	// single application engine
	private static AppEngine engine = null;

	// behavior network editor
	public BehaviorNetworkEditor bnEditor = null;

	// category update handler, it should be a JComponent instance
	public CategoryUpdateListener categoryUpdateListener = null;

	// category update handler, for <code>SimulationView</code>
	public CategoryUpdateListener simulationViewUpdateListener = null;

	// navigation panel in the system editor
	public NavigationPanel navPanel = null;

	/**
	 * Constructor.
	 */
	private AppEngine() {

		// Application task
		appTask = new AppTask();

		// Initialize the application system object
		system = new AppSystem();

		// Application helper
		helper = new AppHelper();

		// Behavior network editor
		bnEditor = new BehaviorNetworkEditor();

		// Application resources
		resources = new AppResources();

		// Application manager
		appManager = AppManager.getInstance();

	}

	/**
	 * Return the single application engine
	 * 
	 * @return the single application engine
	 */
	public static AppEngine getInstance() {
		if (engine == null)
			engine = new AppEngine();
		return engine;
	}

	/**
	 * Return an uncanceled task
	 * 
	 * @return An uncancled task
	 */
	public AppTask getAppTask() {
		if (appTask.isCanceled()) {
			appTask = new AppTask();
		}
		return appTask;
	}

	/**
	 * Set the application status
	 * 
	 * @param status
	 *            Application status
	 */
	public void setAppStatus(int status) {
		if (status == App.CLEAN) {
			engine.appManager.currentApp.setDirty(false);
			sim.ui.AppStatusbar.getInstance().changeAppStatus("App Saved");
		} else if (status == App.DIRTY) {
			engine.appManager.currentApp.setDirty(true);
			sim.ui.AppStatusbar.getInstance().changeAppStatus("App Not Saved");
		}
	}

	/**
	 * Return the current active entity
	 * 
	 * @return The current active entity
	 */
	public Entity getCurrentEntity() {
		return appManager.currentApp.currentEntity;
	}

	/**
	 * Reset the dynamic managers by reset the manager object
	 */
	public void removeAllCategories() throws Exception {

		// Remove all categories from the dynamic manager
		Map categories = appManager.currentApp.dm.getCategories();
		Iterator keys = categories.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			appManager.currentApp.dm.detachCategory(key);
			this.categoryUpdateListener.categoryDeleted(key);
		}
		appManager.currentApp.dm.getCategories().clear();
		appManager.currentApp.dm.getCategoryVersions().clear();
		appManager.currentApp.dm.getCategoryMethods().clear();

	}

	/**
	 * Remove the specified category from <code>DynamicManager</code> of the
	 * current application.
	 * 
	 * @param categoryName
	 *            Name of the category to remove
	 */
	public void removeDynaCategory(String categoryName) throws Exception {

		Map categories = appManager.currentApp.dm.getCategories();
		Iterator keys = categories.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.equals(categoryName)) {
				appManager.currentApp.dm.detachCategory(key);
				categories.remove(key);
				break;
			}
		}

		categories = appManager.currentApp.dm.getCategoryVersions();
		keys = categories.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.equals(categoryName)) {
				categories.remove(key);
				break;
			}
		}

		categories = appManager.currentApp.dm.getCategoryMethods();
		keys = categories.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.equals(categoryName)) {
				categories.remove(key);
				break;
			}
		}

	}

	/**
	 * Initialize the dynamic class manager and jar resource loader. The jar
	 * resources include .class files which will be used in the dynamics class
	 * manager, as well as icons that will be used to render buttons, etc.
	 * 
	 * 
	 * <p>
	 * A typical directory structure of BehaviorSim would be:
	 * </p>
	 * <p>
	 * 
	 * <SIM_HOME> |---------------------------- | | lib (or bin/classes) config
	 * | |------------------------ | | | | behaviorsim.jar (or .classes)
	 * log4j.xml debug.log behaviorsim.log
	 * 
	 * </p>
	 * <p>
	 * Where SIM_HOME is the full path for the behaviorsim environment.
	 * Typically, it is the user's current directory.
	 * </p>
	 * 
	 * The order of probing for the class files is as follows:<br>
	 * 0) The basePath will be checked whether it is a jar file 1) Then The
	 * behaviorsim.jar (under the lib directory) will be checked. 2) If the jar
	 * file does not exist, the bin directory will be checked. 3) If the bin
	 * directory does not exist, the classes directory will be checked.
	 * 
	 * If all the options are not available, a <code>SimException</code> will be
	 * thrown.
	 * 
	 * @param basePath
	 *            The base class path
	 * @throws If
	 *             any exception occurs.
	 */
	public void init(String basePath) throws Exception {

		// Jar resource loader
		try {
			this.jrl = new JarResourceLoader(basePath);
		} catch (Exception e) {
			this.jrl = null;
		}

		StringBuffer sb = null;
		if (jrl == null) {
			sb = new StringBuffer();
			sb.append(basePath);
			sb.append(File.separator);
			sb.append("lib");
			sb.append(File.separator);
			sb.append("behaviorsim_v");
			sb.append(ConfigParameters.version);
			sb.append(".jar");
			try {
				this.jrl = new JarResourceLoader(sb.toString());
			} catch (Exception e) {
				this.jrl = null;
			}
		}

		// Resource loader for the directory "bin"
		if (jrl == null) {
			sb = new StringBuffer();
			sb.append(basePath);
			sb.append(File.separator);
			sb.append("bin");
			try {
				this.jrl = new DirResourceLoader(sb.toString());
			} catch (Exception e) {
				this.jrl = null;
			}
		}

		// Resource loader for the directory "classes"
		if (jrl == null) {
			sb = new StringBuffer();
			sb.append(basePath);
			sb.append(File.separator);
			sb.append("classes");
			try {
				this.jrl = new DirResourceLoader(sb.toString());
			} catch (Exception e) {
				this.jrl = null;
			}
		}

		// Can not initialize
		if (jrl == null) {
			throw new SimException("CLASSPATH-NOT-FOUND",
					"The path for loading necessary classes is not found", null);
		}

	}

	/**
	 * Register the navigation panel to the system.
	 * 
	 * @param nav
	 *            The navigation panel to register
	 */
	public void registerNavigationPanel(NavigationPanel nav) {
		this.navPanel = nav;
	}

	/**
	 * Register a category update listener to the system
	 * 
	 * @param l
	 *            The category update listener to register
	 */
	public void registerCategoryUpdateListener(CategoryUpdateListener l) {
		this.categoryUpdateListener = l;
	}

	/**
	 * Return the registered category update listener
	 * 
	 * @return the registered category update listener
	 */
	public CategoryUpdateListener getCategoryUpdateListener() {
		return this.categoryUpdateListener;
	}

	/**
	 * Return the registered category update listener
	 * 
	 * @return the registered category update listener
	 */
	public void registerSimulationViewUpdateListener(CategoryUpdateListener l) {
		this.simulationViewUpdateListener = l;
	}

	/**
	 * Return the registered category update listener for simulation view
	 * 
	 * @return the registered category update listener for simulation view
	 */
	public CategoryUpdateListener getSimulationViewUpdateListener() {
		return this.simulationViewUpdateListener;
	}

	/**
	 * Evaluate the given action to check whether it is a valid method call in
	 * the given entity.
	 * 
	 * <p>
	 * The action (represented in an action string) is actually a
	 * <strong>SINGLE</strong> method defined in the specified category.
	 * </p>
	 * 
	 * <p>
	 * This method checks whether the given action string is a valid method
	 * defined in the category specified by the category name. The check is
	 * finished by calling the method in the <code>DynamicManager</code>, see
	 * </p>
	 * 
	 * {@link #DynamicManager.checkMethod(String categoryName, String method)}
	 * 
	 * <p>
	 * If the action string is a valid method, the action string is evaluated as
	 * a method call in the given entity.
	 * </p>
	 * 
	 * {@link #BNCategory.process()} and {@link #ActionDlg.onOk()}
	 * 
	 * @param entity
	 *            The entity where the action string is defined If it is not
	 *            specified, an entity created from the dynamic manager is used.
	 * @param actionString
	 *            The action to execute. It is a SINGLE method including method
	 *            name and parameter values, maybe also including a character
	 *            ";"
	 * @return The evaluation result
	 * @throws Exception
	 *             if any exception occurs
	 */
	public Object evaluateAction(Entity entity, String actionString)
			throws Exception {
		
		// The target entity should be specified
		if (entity == null) {
			throw new RuntimeException("Entity is not specified.");
		}

		// Check the format of method call
		if (actionString == null || actionString.trim().equals("")) {
			throw new RuntimeException("The action can not be empty.");
		}
		if (actionString.indexOf('(') == -1 || actionString.indexOf(')') == -1) {
			throw new RuntimeException("The action should be a method call.");
		}

		// Strip from the character ';' to the end of the actionString
		// Just keep the actual single method call
		if (actionString.indexOf(';') != -1) {
			actionString = actionString.substring(0, actionString.indexOf(';'));
		}
		
		// Check whether the method exists in the category
		String methodName = actionString
				.substring(0, actionString.indexOf('('));
		try {
			boolean methodExist = appManager.currentApp.dm.checkMethod(entity
					.getEntityType(), methodName);
			if (!methodExist) {
				throw new RuntimeException("Method '"+methodName+"' does not exist in entity '"+entity.getDisplayName()+"'");
			}
		} catch (Exception e) {
			throw new RuntimeException("The action (method call) '"
					+ actionString + "' is not valid for the category '"
					+ ((Category) entity).getEntityType() + "'.");
		}

		// Call the method. 
		try {
			return invokeAction(entity, methodName, actionString);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * <p>
	 * Invoke the specified method (include name and parameter values) in the target 
	 * entity. In the case of multiple signatures (different parameter types for ex.),
	 * each signature will be checked until one is found. In the case of no signature
	 * is found, no method will be invoked.
	 * </p>
	 *  
	 * 
	 * @param entity 
	 * 			  Target entity 
	 * @param methodName
	 *            The method name to look in
	 * @return Result of method execution
	 */
	private Object invokeAction(Entity entity, String methodName, String methodCall) 
		throws Exception {
		Class clss = entity.getClass();
		Method[] methods = clss.getMethods();
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(methodName)) {
					// Parameter types
					Class[] types = methods[i].getParameterTypes();
					// Values for parameters
					Object[] values = null;
					try {
						values = _getParameterValues(entity, types, methodCall);
					} catch(Exception e){
						continue;
					}
					// Invoke the method
					return MethodUtils.invokeExactMethod(entity, methodName, values, types);
				}
			}
		}
		return null;
	}

	/**
	 * Obtain the parameters of method. It is mainly used in the method call
	 * invoked in the <code>Action</code>.
	 * 
	 * <p>
	 * Each parameter can only be either a constant literal or property of the
	 * given entity.
	 * </p>
	 * 
	 * <p>
	 * For different parameter types, first check the constant literal. If it is
	 * not successful, then property of the current computing is checked.
	 * Currently, only types <code>double(Double)</code>,
	 * <code>int(Integer)</code>, and <code>Object</code> are handled, according
	 * to the method signatures in the class
	 * <code>sim.model.engine.SystemFunction</code>.
	 * </p>
	 * 
	 * <p>
	 * Any exceptions will be thrown out as the <code>RuntimeException</code>.
	 * </p>
	 * 
	 * @param entity
	 *            Entity to probe
	 * @param paramTypes
	 *            Parameter types
	 * @param methodString
	 *            The method string, including method name and parameter values
	 *            such as "compute("1", 2, 3)"
	 * @return parameter values
	 */
	private Object[] _getParameterValues(Entity entity, Class[] paramTypes, String methodString) {
		String parameters = methodString.substring(
				methodString.indexOf('(') + 1, methodString.indexOf(')'));
		if (parameters == null || (parameters = parameters.trim()).equals("")) {
			return null;
		}
		/** Get parameter values */
		List params = new ArrayList();
		while (parameters.indexOf(',') != -1) {
			params.add(parameters.substring(0, parameters.indexOf(',')));
			parameters = parameters.substring(parameters.indexOf(',') + 1);
		}
		if (parameters != null && !parameters.trim().equals("")) {
			params.add(parameters);
		}
		RuntimeException runEx = new RuntimeException();
		/**
		 * Translate the parameter values into the actual value based on the
		 * current state of the entity
		 */
		if (params.size() != paramTypes.length) {
			throw runEx;
		}
		for (int i = 0; i < paramTypes.length; i++) {
			String param = ((String) params.get(i)).trim();
			if (paramTypes[i] == double.class || paramTypes[i] == Double.class) {
				Double actualParam;
				try {
					/** A constant double value ? */
					actualParam = Double.valueOf(param);
				} catch (Exception e) {
					/** Property of the current entity ? */
					try {
						actualParam = new Double(entity.getDoubleValue(param));
					} catch (Exception ee) {
						try {
							actualParam = new Double(((Number) MethodUtils
									.invokeExactMethod(entity, "get"
											+ param.substring(0, 1)
													.toUpperCase()
											+ param.substring(1), null))
									.doubleValue());
						} catch (Exception eee) {
							throw runEx;
						}
					}
				}
				params.set(i, actualParam);
			} 
			else if (paramTypes[i] == int.class
					|| paramTypes[i] == Integer.class) {
				Integer actualParam;
				try {
					/** A constant integer value ? */
					actualParam = Integer.valueOf(param);
				} catch (Exception e) {
					/** Property of the current entity ? */
					try {
						actualParam = new Integer((int) entity
								.getDoubleValue(param));
					} catch (Exception ee) {
						try {
							actualParam = new Integer(((Number) MethodUtils
									.invokeExactMethod(entity, "get"
											+ param.substring(0, 1)
													.toUpperCase()
											+ param.substring(1), null))
									.intValue());
						} catch (Exception eee) {
							throw runEx;
						}
					}
				}
				params.set(i, actualParam);
			} 
			else if (paramTypes[i] == float.class
					|| paramTypes[i] == Float.class) {
				Float actualParam;
				try {
					/** A constant float value ? */
					actualParam = Float.valueOf(param);
				} catch (Exception e) {
					/** Property of the current entity ? */
					try {
						actualParam = new Float(entity.getFloatValue(param));
					} catch (Exception ee) {
						try {
							actualParam = new Float(((Number) MethodUtils
									.invokeExactMethod(entity, "get"
											+ param.substring(0, 1)
													.toUpperCase()
											+ param.substring(1), null))
									.floatValue());
						} catch (Exception eee) {
							throw runEx;
						}
					}
				}
				params.set(i, actualParam);
			} 
			else if (paramTypes[i] == Object.class) {
				Object actualParam;
				/** Property of the current entity */
				try {
					actualParam = entity.getValue(param);
				} catch (Exception ee) {
					try {
						actualParam = MethodUtils.invokeExactMethod(entity,
								"get" + param.substring(0, 1).toUpperCase()
										+ param.substring(1), null);
					} catch (Exception eee) {
						throw runEx;
					}
				}
				params.set(i, actualParam);
			}
			/** FIXME: How about the string and other parameters ? */
		}
		return params.toArray();
	}

	/**
	 * Set the category property change listener
	 * 
	 * @listener The lister to set
	 */
	public void setPropertyListener(PropertyListener listener) {
		this.proListener = listener;
	}

	/**
	 * Update the category property in the category creation view
	 * 
	 * @param selectedRow
	 *            The specified row
	 * @param name
	 *            The property name
	 * @param type
	 *            The property type
	 * @param value
	 *            The property value
	 */
	public void updateProperty(int selectedRow, String name, int type,
			String value) {
		this.proListener.propertyEdit(selectedRow, new Property("", type, name,
				value));
	}

	/**
	 * Upload the specified agent image into the application resource directory.
	 * The file name is kept unchanged. A copy of the image file is put into the
	 * resource directory. If there is an image in the directory with the same
	 * name, the image will be overwritten. If the image is exactly the same as
	 * that in the resource directory, no copy will happen.
	 * 
	 * @param agentImageInFullPath
	 *            Full Path Agent Image.
	 * @return The full path agent image which is stored in the resource
	 *         directory
	 * @throws Exception
	 *             If the agent image can not be copied successfully
	 */
	public String uploadImage(String agentImageInFullPath) throws Exception {
		// Fetch the resource directory.
		File resourceDir = new File(appManager.currentApp.getAppDir()
				+ File.separator + appManager.currentApp.getAppResourceDir());
		// Output file
		File outputFile = null;
		String imageName = agentImageInFullPath.substring(agentImageInFullPath
				.lastIndexOf(File.separator) + 1);
		// Create the file
		try {
			outputFile = new File(resourceDir, imageName);
			// Exactly the same file, then no copy happens
			if (agentImageInFullPath.trim().equals(
					outputFile.getAbsolutePath().trim()))
				return agentImageInFullPath;
			if (!outputFile.exists())
				outputFile.createNewFile();
		} catch (Exception ioe) {
			throw new SimException("CAT-IMAGE-COPY-FAILED",
					"The category image can not be copied.", ioe);
		}
		// Save the image into the resource directory
		FileUtils.copyFile(agentImageInFullPath, outputFile.getAbsolutePath());
		return outputFile.getAbsolutePath();
	}

	/**
	 * Upload the specified image into the application resource directory. The
	 * image is an <code>InputStream</code>.
	 * 
	 * @param relativeImageName
	 *            Relative name of the image (no directory information
	 *            included).
	 * @throws Exception
	 *             If the agent image can not be copied successfully
	 */
	public String uploadImage(java.io.InputStream input,
			String relativeImageName) throws Exception {
		// Fetch the resource directory.
		File resourceDir = new File(appManager.currentApp.getAppDir()
				+ File.separator + appManager.currentApp.getAppResourceDir());
		// Output file
		File outputFile = null;
		// Create the file
		try {
			outputFile = new File(resourceDir, relativeImageName);
			if (!outputFile.exists())
				outputFile.createNewFile();
		} catch (Exception ioe) {
			throw new SimException("CAT-IMAGE-COPY-FAILED",
					"The category image can not be copied.", ioe);
		}
		// Save the image into the resource directory
		FileUtils.copyFile(input, outputFile.getAbsolutePath(), null);
		return outputFile.getAbsolutePath();
	}

	/**
	 * Create a new entity category. Here suppose that the properties in the
	 * propties list are different from each other.
	 * 
	 * <p>
	 * <ul>
	 * There are several steps involved: <ui>1) Create the new category class -
	 * Dynamic class</ui> <ui>2) Create the category icon in the system
	 * editor</ui>
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            The name of the category to be created
	 * @param pictureFileName
	 *            The icon picture of the category
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup the behavior network
	 * @param properties
	 *            The properties list of the category
	 * @throws Exception
	 *             If an exception happens
	 */
	public void createANewCategory(String name, String pictureFileName,
			boolean needBehaviorNetwork, List properties) throws Exception {
		Category category = null;
		String path = uploadImage(pictureFileName);
		category = (Category) appManager.currentApp.dm.createCategory(name,
				path, needBehaviorNetwork, properties);
		categoryUpdateListener.categoryAdded(category);
	}

	/**
	 * Update the definition of an existing category. The basic steps include:
	 * 
	 * <ul>
	 * <ui>1. Remove the predefined category specified by the oldName.</ui>
	 * <ui>2. Define a new category with the new name, picture, nbn and
	 * propertiesList.</ui> <ui>3. Update the entities' definition of the
	 * existing category to the new one.</ui> <ui>4. Define method calls for the
	 * actions</ui>
	 * </ul>
	 * 
	 * <p>
	 * Note that the method definition of the new category is the same as the
	 * existing category.
	 * </p>
	 * 
	 * @param oldName
	 *            The name of previous category
	 * @param name
	 *            The name of the new category
	 * @param pictureFileName
	 *            The picture of the new category
	 * @param nbn
	 *            Whether the new category is behavior network based
	 * @param properties
	 *            The properties of the new categories
	 * @throws Exception
	 *             if any exception occurs
	 */
	public void updateCategory(String oldName, String name,
			String pictureFileName, boolean nbn, List properties)
			throws Exception {

		// Upload file
		String path = uploadImage(pictureFileName);

		// Update first
		Category newC = (Category) this.appManager.currentApp.dm
				.updateCategory(oldName, name, path, nbn, properties);

		// Update the entities' definition
		system.updateEntities(oldName, name);

		// Update the category
		this.categoryUpdateListener.categoryUpdated(oldName, newC);

	}

	/**
	 * Create a new entity category. Here suppose that the properties in the
	 * properties list are different from each other.
	 * 
	 * <p>
	 * <ul>
	 * There are several steps involved: <ui>1) Create the new category class -
	 * Dynamic class</ui> <ui>2) Populate the new category class with
	 * properties</ui> <ui>3) Populate the new category class with methods</ui>
	 * <ui>4) Create the category icon in the system editor</ui>
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            The name of the category to be created
	 * @param pictureFileName
	 *            The icon picture of the category
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup the behavior network
	 * @param properties
	 *            The properties list of the category
	 * @param methods
	 *            The method list of the category
	 * @throws Exception
	 *             if any exception occurs
	 */
	public void createANewCategory(String name, String pictureFileName,
			boolean needBehaviorNetwork, List properties, List methods)
			throws Exception {

		// Upload the image
		String path = uploadImage(pictureFileName);

		// Create the new category
		Category category = appManager.currentApp.dm.createCategory(name, path,
				needBehaviorNetwork, properties, methods);

		// Add the category into the category list
		categoryUpdateListener.categoryAdded(category);

	}

	/**
	 * Remove the specified category from the application. If no category is
	 * specified, all categories will be removed from the application. USE THIS
	 * METHOD WITH CAUTION.
	 * 
	 * <p>
	 * The following steps are involved in the removal,
	 * <ul>
	 * <ui>1. If the specified category name is null, then all categories will
	 * be removed.</ui> <ui>2. Otherwise, remove the specific category from the
	 * system.</ui>
	 * </ul>
	 * </p>
	 * 
	 * @param categoryName
	 *            Name of the category to remove.
	 * @return Whether the operation is successful or not.
	 * @throws If
	 *             exception throws.
	 */
	public boolean removeCategory(String categoryName) throws Exception {

		if (categoryName == null) {
			sim.ui.MainFrame.getInstance().applet.clearAll2();
		} else {
			sim.ui.MainFrame.getInstance().applet.clearAll3(categoryName);
		}

		return true;
	}

	/**
	 * Create a new entity category. Here suppose that the properties in the
	 * properties list are different from each other.
	 * 
	 * <p>
	 * <ul>
	 * There are several steps involved: <ui>1) Create the new category class -
	 * Dynamic class</ui> <ui>2) Populate the new category class with
	 * properties</ui> <ui>3) Populate the new category class with methods</ui>
	 * <ui>4) Create the category icon in the system editor</ui>
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            The name of the category to be created
	 * @param display
	 *            The display properties of the category
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup the behavior network
	 * @param properties
	 *            The properties list of the category
	 * @param methods
	 *            The method list of the category
	 * @exception If
	 *                any exception occurs
	 */
	public void createANewCategory(String name, Display display,
			boolean needBehaviorNetwork, List properties, List methods)
			throws Exception {

		// Upload the image
		// uploadImage( pictureFileName );

		// Create the new category
		Category category = appManager.currentApp.dm.createCategory(name,
				display, needBehaviorNetwork, properties, methods);

		// Add the category into the category list
		categoryUpdateListener.categoryAdded(category);

	}

	/**
	 * Create a new category method. Update entities of the specified category.
	 * 
	 * <p>
	 * This method simply delegates the call to the DynamicManager,
	 * </p>
	 * {@link #sim.configure.dclass.DynamicManager.createMethod(String categoryName, String methodName, String inputCode)}
	 * 
	 * @param categoryName
	 *            The name of the category
	 * @param saved
	 *            Whether the method definition should be saved It is a general
	 *            case that the internal methods are not saved
	 * @param methodName
	 *            The name of the method to be created
	 * @param updateAll
	 *            Update all entities of the category
	 * @param inputCode
	 *            The source code input by the user
	 */
	public void createANewMethod(String categoryName, boolean saved,
			boolean updateAll, String methodName, String inputCode)
			throws Exception {
		appManager.currentApp.dm.createMethod(categoryName, saved, methodName,
				inputCode);
		if (updateAll)
			system.updateEntities(categoryName, categoryName);
	}

	/**
	 * Replace the old method definition of the specified category.
	 * 
	 * <p>
	 * First, the latest version of that category is retrieved. Second, the old
	 * method definition is removed and lastly, the new method is inserted into
	 * the category and the methods list is updated.
	 * </p>
	 * 
	 * <p>
	 * This method simply delgates the call to the DynamicManager,
	 * </p>
	 * {@link #sim.configure.dclass.DynamicManager.replaceMethod(String categoryName, CMethod newMethod)}
	 * 
	 * @param categoryName
	 *            The name of the category where the method is replaced
	 * @param newMethod
	 *            The new method instance to be inserted
	 */
	public void replaceMethod(Category category, CMethod newMethod)
			throws Exception {
		appManager.currentApp.dm.replaceMethod(category.getEntityType(),
				newMethod);
		system.updateEntities(category.getEntityType(), category
				.getEntityType());
		if (category instanceof BNCategory) {
			bnEditor.verifyBehaviorNetwork((BNCategory) category, newMethod);
		}
	}

	/**
	 * Remove the specified method from the specified category. If no method is
	 * specified, all methods of the category will be removed.
	 * 
	 * <p>
	 * This method contains the following actions:
	 * 
	 * <ul>
	 * <ui>1. Remove the method from the category definition. <br/>
	 * This step simply delegates to <code>sim.core.dclass.DynamicManager</code>
	 * of the current active application, see
	 * {@link #sim.core.dclass.DynamicManager.removeMethod(String categoryName, CMethod newMethod)}
	 * for more details. </ui> <ui>2. Remove the methods repository from the
	 * specified category. <br>
	 * This step also delegates to <code>sim.core.dclass.DynamicManager</code>
	 * of the current active application, see
	 * {@link #sim.core.dclass.DynamicManager.removeMethod(String categoryName, CMethod newMethod)}
	 * for more details. </ui> <ui>3. Update the entities of the category with
	 * the updated information. <br>
	 * In this step, the validity of agent behaviors will be checked, since the
	 * removed methods may be referred in the definition of behavior excitation
	 * and action. The behavior which refers to the dangling method will be
	 * removed from corresponding agent. </ui>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * TODO: Is it necessary to remove the invalid behaviors from the behavior
	 * repository and the entities which are configured with those behaviors?<br>
	 * In the current implementation, the invalid behaviors are not removed from
	 * the behavior repository. It is the user's responsibility to ensure
	 * validity of the affected behaviors.
	 * </p>
	 * 
	 * 
	 * 
	 * @param catObject
	 *            The category object with the method definition
	 * @param method
	 *            The method to remove.
	 * @return Whether the method(s) are removed successfully
	 * @throws Exception
	 *             Exception throws during the steps.
	 */
	public boolean removeMethod(Category catObject, CMethod method)
			throws Exception {
		// Step 1 and 2
		appManager.currentApp.dm
				.removeMethod(catObject.getEntityType(), method);

		// Step 3
		system.updateEntities(catObject.getEntityType(), catObject
				.getEntityType());
		if (catObject instanceof BNCategory) {
			bnEditor.verifyBehaviorNetwork((BNCategory) catObject, method);
		}
		return true;

	}

	/**
	 * Replace the pre-defined entities because of the change of method/property
	 * definition.
	 * 
	 * <p>
	 * Iterator the entity list, if an entity is created from the specified
	 * category. Then, the entity object is re-created from that category (The
	 * category after change). Finally, the re-created entity is populated with
	 * the internal states of the pre-defined entity.
	 * </p>
	 * 
	 * @param categoryName
	 *            The category whose entities should be re-created
	 */
	public void replaceEntities(String categoryName) throws Exception {
		system.updateEntities(categoryName, categoryName);
	}

	/**
	 * Return all user-defined categories and their methods.
	 * 
	 * <p>
	 * This method is used to retrieve the category methods which are used in
	 * the GUI to edit.
	 * </p>
	 * 
	 * <p>
	 * The key of the returned map is the category name and the value is an
	 * array list containing user defined methods.
	 * </p>
	 * 
	 * 
	 * @return All categories and their methods
	 */
	public Map getAllCategoryMethods() {
		return appManager.currentApp.dm.getAllCategoryMethods();
	}

	// --------------------------------------------------------------
	// Used in the GUI
	// --------------------------------------------------------------
	/** Add the action into the specified category */
	public void addAction(int behaviorID, Action action) {
		this.system.actionRepository.addAction(behaviorID, action);
	}

	/**
	 * Save the whole application into the chosen external file.
	 */
	public boolean saveAppAsFile(String path) {
		return helper.saveAppAsFile(path);
	}

	/**
	 * Pre-load some application definition, including application name and
	 * whether the app is based on behavior network.
	 */
	public String preloadAppDef(String externalFile) {
		return helper.preloadAppDef(externalFile);
	}

	/**
	 * Load the whole application from the chosen external file.
	 */
	public void loadAppFromFile(String path) throws Exception {
		helper.loadAppFromFile(path);
	}

	/**
	 * Refresh the world node from the navigation tree. Since the simulation
	 * environment is changed.
	 */
	public void refreshWorldNode() {
		navPanel.updateSimulationWorld();
	}

	/**
	 * Obtain the total time slices from the system configuration parameter
	 * object.
	 */
	public int getTotalTimeticks() {
		return this.system.getTotalTimeticks();
	}

	/**
	 * Obtain the available entity list.
	 */
	public List getAvailableEntities() {
		return this.system.getAvailableEntities();
	}

	/**
	 * Obtain the position of the specified entity in the given time slice.
	 */
	public Point getEntityPosition(Entity entity, int time) {
		return this.system.getEntityPosition(entity, time);
	}

	/**
	 * Add the specified behavior to the current network. And attach the
	 * behavior to an unoccupied position in the network.
	 * 
	 * @param c
	 *            Owner of the behavior
	 * @param
	 */
	public void addBehaviorToNetwork(BNCategory c, int id) throws Exception {
		this.bnEditor.addBehaviorToNetwork(c, id);
	}

	/**
	 * Return the given behavior based on the given name. If the behavior can
	 * not be found, an empty behavior is returned.
	 */
	public Behavior getBehavior(BNCategory c, int id) {
		return this.bnEditor.repository.getBehaviorById(c, id);
	}

	/** Reset the simulation data */
	public void resetSimulation(Map initialPos) {
		this.system.resetSimulation(initialPos);
	}

	/**
	 * Obtain the simulation data.
	 */
	public SimulationData getSimulationData() {
		return this.system.getSimulationData();
	}

	/**
	 * Remove the specified entity.
	 */
	public void removeEntity(Entity entity) {
		this.system.removeEntity(entity);
	}

	/**
	 * Obtain the simulation environment.
	 */
	public SimulationEnvironment getSimulationEnvironment() {
		return this.system.getSimulationEnvironment();
	}

	/**
	 * Add a entity into the system.
	 */
	public void addEntity(Entity entity) {
		this.system.addEntity(entity);
	}

	/**
	 * Save the initial parameters for the given entity.
	 */
	public void changeEntityInitialParameters(Entity e) {
		this.system.changeEntityInitialParameters(e);
	}

	/**
	 * Add the specified behaviors into the network, including: 1) Update the
	 * behavior network tree (in behavior network definition panel) 2) Update
	 * the system navigation tree
	 * 
	 * @param enity
	 *            Owner of the behavior
	 * @param ids
	 *            Id of behaviors to add
	 */
	public void behaviorAddedToNetwork(BNCategory entity, int ids[])
			throws Exception {
		if (ids != null && ids.length > 0) {
			// Get the behavior network for testing the unique of the added
			// behavior
			BehaviorNetwork bn = entity.getBehaviorNetwork();
			List behaviors = bn.getBehaviorList();
			Set behaviorIds = new HashSet();
			for (int i = 0; i < behaviors.size(); i++) {
				behaviorIds.add(new Integer(((Behavior) behaviors.get(i))
						.getMyId()));
			}
			String names[] = new String[ids.length];
			// Check the unique of the behaviors to add
			for (int k = 0; k < ids.length; k++) {
				names[k] = bnEditor.repository.getBehaviorById(entity, ids[k])
						.getBehaviorName();
				if (behaviorIds.contains(new Integer(ids[k]))) {
					sim.util.MessageUtils.displayError("The behavior '"
							+ names[k]
							+ "' is already added into the behavior network.");
					return;
				}
			}
			for (int k = 0; k < ids.length; k++) {
				// Verify the validity of behavior definition ahead
				if (bnEditor.verifyExcitationActionMethod(entity, ids[k])) {
					addBehaviorToNetwork(entity, ids[k]);
					/*
					 * try { navPanel.addNewBehavior(ids[k], names[k], entity );
					 * } catch(Exception ex) { sim.util.MessageUtils.debug(this,
					 * "behaviorAddedToNetwork", ex); ex.printStackTrace();
					 * sim.util.MessageUtils.displayError(
					 * "Behavior is added. but navigation panel/tree can not be updated."
					 * ); return; }
					 */
				}
			}
		}
	}

	/**
	 * Update the weights of each behavior.
	 * 
	 * @param weights
	 *            Behavior weights
	 * @param names
	 *            The behavior name array
	 */
	public void updateWeights(BehaviorNetwork bn, double weights[],
			int ids[]) {

		// Not a dynamic behavior network
		bn.setDynamic(false);

		// Get all behaviors and update its weight
		List bs = bn.getBehaviorList();
		for (int i = 0; i < bs.size(); i++) {
			for (int k = 0; k < ids.length; k++) {
				if (((Behavior) bs.get(i)).getMyId() == ids[k]) {
					((Behavior) bs.get(i)).setWeight(weights[k]);
					break;
				}
			}
		}

	}

	/**
	 * Update the coefficients in the behavior network
	 * 
	 * @param coefficients
	 *            The new coefficients to update
	 * @param names
	 *            The behavior names
	 */
	public void updateCoefficients(BehaviorNetwork bn, double coefficients[][],
			int ids[]) {

		// Not a dynamic behavior network
		bn.setDynamic(false);

		// Get edges of coefficients
		Edge edges[] = bn.getEdgesGivenBehaviors(ids);
		for (int m = 0; m < edges.length; m++) {
			Behavior fB = edges[m].fromB();
			Behavior tB = edges[m].toB();
			int f, t;
			for (f = 0; f < ids.length; f++) {
				if (fB.getMyId() == (ids[f])) {
					for (t = 0; t < ids.length; t++)
						if (tB.getMyId() == (ids[t])) {
							edges[m].setInhibitionFT(coefficients[f][t]);
							edges[m].setInhibitionTF(coefficients[t][f]);
							break;
						}
					break;
				}
			}
		}

		// Update edges
		updateEdges(bn, edges);

	}

	/**
	 * Create a new behavior with the given name, equation and actionString.
	 * Also put the behavior into the behaviors list.
	 * 
	 * <p>
	 * Also, the task queue of this behavior is setup based on the given action
	 * string. And the type of the task queue can be set as resumable.
	 * </p>
	 * 
	 * @param bn
	 *            Owner of the behavior
	 * @param name
	 *            Behavior name
	 * @param equationString
	 *            Behavior equation
	 * @param resumable
	 *            The task queue is resumable
	 * @param actionString
	 *            Behavior action specification
	 * @return Created behavior
	 */
	public Behavior createNewBehavior(BNCategory bn, String name,
			String equationString, boolean resumable, String actionString)
			throws Exception {

		/** Create behavior */
		Behavior behavior = bnEditor.createNewBehavior(bn, name,
				equationString, resumable);

		/** Create behavior action */
		String bActionName = system.actionRepository.addBehaviorAction(behavior
				.getMyId(), actionString);

		/** Save behavior action name */
		behavior.setBehaviorActionName(bActionName);

		return behavior;

	}

	/**
	 * Update the behavior equation and actions. This update is only for
	 * behavior repository.
	 * 
	 * @param bnE
	 *            Owner of the behavior
	 * @param id
	 *            ID of the behavior
	 * @param name
	 *            New name of the behavior
	 * @param equationString
	 *            New excitation equation
	 * @param resumable
	 *            Resumable setting
	 * @param actionString
	 *            New action code
	 * @throws Exception
	 *             Exception happens
	 */
	public void updateBehavior(BNCategory bnE, int id, String name,
			String equationString, boolean resumable, String actionString)
			throws Exception {

		/** Original behavior */
		Behavior old = this.getBehavior(bnE, id);

		/** Update excitation, action? */
		boolean updateExcitM = !equationString
				.equals(old.getBehaviorEquation());
		boolean updateActM = !actionString.equals(getBehaviorActionString(id));

		/** Update behavior */
		Behavior behavior = this.bnEditor.updateBehavior(bnE, id, name,
				equationString, resumable);

		/** Create behavior action */
		String bActionName = this.system.actionRepository.addBehaviorAction(id,
				actionString);

		/** Save behavior action name */
		behavior.setBehaviorActionName(bActionName);

		/** Update the behavior for the target entity */
		List entities = this.system.getAvailableEntities();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			// Locate target entity
			if (!(entity instanceof BNCategory)
					|| entity.getMyId() != bnE.getMyId())
				continue;
			// Update behavior network
			BehaviorNetwork bn = bnE.getBehaviorNetwork();
			if (bn.getBehavior(behavior.getMyId()) != null) {
				bnEditor.updateBehaviorNetwork(bnE, id, updateExcitM,
						updateActM);
			}

		}

	}

	/**
	 * Obtain the entity in the given rectangle.
	 */
	public Entity getEntityByDetectRectangle(Rectangle detectRect) {
		return this.system.getEntityByDetectRectangle(detectRect);
	}

	/** System reset method */
	public void clearAll() {
		try {
			removeAllCategories();
		} catch (Exception e) {
			;
		}
		system.actionRepository.removeAllBehaviorActions();
		system.removeAllEntities();
		system.resetWorld();
		bnEditor.removeAllBehaviors();
		sim.ui.MainFrame.getInstance().setTitle(
				"BehaviorSim v" + ConfigParameters.version);
		sim.ui.AppStatusbar.getInstance().reset();
	}

	/**
	 * Remove all categories and entities from the system. Note that behavior
	 * repository and behavior action information will not be affected.
	 * 
	 * @see clearAll
	 */
	public void clearAll2() throws Exception {
		removeAllCategories();
		system.removeAllEntities();
		system.resetWorld();
	}

	/**
	 * Remove the behavior from behavior repository; Remove the behavior from
	 * behavior action repository; Remove the behavior from owner's network;
	 * Remove the behavior from the navigation tree.
	 * 
	 * @param owner
	 *            Owner of the behavior
	 * @param b
	 *            Behavior to remove
	 * @param removeFromPanel
	 *            remove the behavior from panel?
	 */
	public void removeBehavior(BNCategory owner, Behavior b,
			boolean removeFromPanel) {

		bnEditor.removeBehavior(owner, b.getMyId());
		BehaviorNetwork bn = owner.getBehaviorNetwork();
		bn.removeBehavior(bn.getPosition(b));
		system.actionRepository.removeBehaviorAction(b.getMyId());

		if (removeFromPanel) {
			try {
				navPanel.removeNodesOfBehavior(owner, b.getMyId());
			} catch (Exception e) {
			}
		}

	}

	/**
	 * Remove the specified entity from the system. Actions include 1) Remove
	 * entity from <code>AppSystem</code>; 2) Remove entity from the system
	 * <code>NavigationPanel</code>; 3) Remove entity record from simulation
	 * data. 4) Remove entity from <code>SystemEditor</code>
	 * 
	 * @param entityID
	 *            The id of the entity to be removed.
	 * @return true if the entity is removed successfully.
	 */
	public boolean removeEntity(int entityID) {

		// 1)
		Entity entity = system.getEntityById(entityID);
		// 2)
		navPanel.removeEntity(entity);
		// 3)
		system.removeEntity(entityID);
		// 4)
		categoryUpdateListener.entityUpdated();
		// 5)
		simulationViewUpdateListener.entityUpdated();
		return true;
	}

	/**
	 * Create a new entity which belongs to the specified category.
	 * 
	 * @param categoryName
	 *            The category from which an entity is created
	 * @return The id of the newly created entity. -1 If creation is failed.
	 * @throws Exception
	 *             if the new entity can not be created successfully
	 */
	public int createNewEntityInCategory(String categoryName) throws Exception {

		// Create a new instance of the specified category
		Category newEntity = (Category) appManager.currentApp.dm
				.getCategory(categoryName);

		// Initialize the category image
		newEntity.init((JComponent) categoryUpdateListener, newEntity
				.getEntityType(), newEntity.getImagePath());

		// Set the initial position, default is (20, 20)
		newEntity.setPosition(20, 20);

		// Add the new created entity into the system
		addEntity(newEntity);

		// Add a new node into the navigation tree
		navPanel.addNewNode(newEntity);

		// Refresh the world
		categoryUpdateListener.entityUpdated();

		// Return the id of the created entity
		return newEntity.getMyId();

	}

	/**
	 * Create a new entity by copying an existing entity.
	 * 
	 * @param originalEntityID
	 *            The id of the entity to copy from
	 * @return The id of the newly copied entity
	 * @throws Exception
	 *             If the new entity can not be created from another entity
	 */
	public int createNewEntityByCopyEntity(int originalEntityID)
			throws Exception {

		// Get the original entity
		Category original = (Category) system.getEntityById(originalEntityID);

		// Create an entity from the category of the original entity
		Category newEntity = (Category) appManager.currentApp.dm
				.getCategory(original.getEntityType());

		// Copy state from the original entity
		original.copyTo(newEntity);

		// Change the simulation time to 0, why do we do this?
		newEntity.setTime(0);

		// Set the new position of the copied entity.
		Point pos = newEntity.getPosition();
		Random ran = new Random();
		double incrX = 30 * ran.nextDouble();
		double incrY = 30 * ran.nextDouble();
		newEntity.setPosition(new Point(pos.x + incrX, pos.y + incrY));
		system.env.handleEnvironmentBounds(newEntity);
		
		// Setup a new display name
		newEntity.setDisplayName(newEntity.getDisplayName() + "-Copy");

		// Add the new created entity into the system
		addEntity(newEntity);
		
		// If the entity is an instance of BNCategory, copy
		// behavior network, action selection mechanism and
		// task queue helper then.
		try {
			BNCategory bn = (BNCategory) original;
			if (newEntity instanceof BNCategory) {
				
				BNCategory newBn = (BNCategory)newEntity;
				if (bn.getActionSelectionMechanism() != null) {
					newBn.setActionSelectionMechanism(bn.getActionSelectionMechanism().copy());
				}				
				
				if (!bn.isNoDynamicsMechanism())
				{
					int inx = bn.getActionSelectionMechanismIndex();
					if (inx != BehaviorNetwork.DYNAMICS) {
						
						// Paste behavior network
						BehaviorNetwork bnn = bn.getBehaviorNetwork();
						newBn.getBehaviorNetwork().paste(newBn, bnn);	
						
					} 
					else
					{
						// Paste general dynamics
						newBn.registerGeneralDynamics(bn.getGeneralDynamics());
					}
				}							
				
			}
		} catch(Exception e)
		{
			removeEntity(newEntity);
			throw e;
		}

		// Add a new node into the navigation tree
		navPanel.addNewNode(newEntity);

		// Refresh the world
		categoryUpdateListener.entityUpdated();

		// Refresh the simulation view
		simulationViewUpdateListener.entityUpdated();
		
		// Updates needed
		appManager.currentApp.setDirty(true);

		// Return the id of the new entity
		return newEntity.getMyId();

	}

	/**
	 * Update the coefficients edge
	 * 
	 * @param edges
	 */
	public void updateEdges(BehaviorNetwork bn, Edge edges[]) {
		bn.updateEdges(edges);
	}

	/** Get action of the specified name and behavior */
	public Action getAction(int behaviorID, String actionName) {
		return this.system.actionRepository.getAction(behaviorID, actionName);
	}

	/** Get the action string of the specified behavior */
	public String getBehaviorActionString(int behaviorID) {
		BehaviorAction ba = (BehaviorAction) getAction(behaviorID, behaviorID
				+ "Action");
		if (ba == null)
			return "";
		return ba.getActionString();
	}

	/** Draw the given entity, used in system editor */
	public void drawEntity(Entity entity, Graphics g, double direction,
			Point position, int state) {
		system.drawEntity(entity, g, direction, position, state);
	}

	/** Draw the given entity, used in system editor */
	public void drawEntity2(Entity entity, Graphics g, double direction,
			Point position, int state) {
		system.drawEntity2(entity, g, direction, position, state);
	}

	/** Resize and reposition all entities */
	public void resizeAndRepositionEntities(double widthRatio,
			double heightRatio) {
		system.resizeAndRepositionEntities(widthRatio, heightRatio);
	}
	
	/** Refresh entity positions in navigation tree */
	public void refreshPositionsInNavigationTree(){
		
		List entities = system.getAvailableEntities();
		for (int i = 0; i < entities.size(); i++)
		{
			Entity entity = (Entity)entities.get(i);
			navPanel.updateEntityPosition(entity);
		}

	}

	/**
	 * Return the entity which stands for the user object (a string) of the
	 * selected tree node. It is mainly used by
	 * <code>DefineBehaviorNetworkAction</code>, either as a menu item or as a
	 * tool bar button.
	 * 
	 * @return The selected entity
	 */
	public Entity getSelectedEntityInNavigationPanel() {
		return system.getEntityById(navPanel.getSelectedId());
	}
}