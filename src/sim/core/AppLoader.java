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

/**
 * <p>Title: XML operation tools</p>
 * <p>Description: Used to read from and write external xml files</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Fasheng Qiu
 * @version 1.0
 */
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JComponent;

import sim.core.dclass.JarResourceLoader;
import sim.model.action.BehaviorAction;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.entity.BNCategory;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Display;
import sim.model.entity.Property;
import sim.model.entity.PropertyType;
import sim.model.entity.WhiteFilter;
import sim.model.mechanism.CooperativeMechanism;
import sim.model.mechanism.MutualInhibitionMechanism;
import sim.ui.AppStatusbar;
import sim.util.MessageUtils;
import sim.xml.jdom.Document;
import sim.xml.jdom.Element;
import sim.xml.jdom.input.SAXBuilder;
import sim.xml.jdom.output.XMLOutputter;

public final class AppLoader {

	/* The application engine where data come from */
	private static AppEngine engineRef = AppEngine.getInstance();

	/**
	 * Load the specified application. Corresponding information will be shown
	 * in the status bar.
	 * 
	 * @param xml
	 *            The application configuration file
	 * @param showDialog
	 *            Need to show dialog?
	 */
	public static void loadAppFromFile(final String xml,
			final boolean showDialog) {
		if (xml == null) {
			MessageUtils.error(AppLoader.class, "loadAppFromXML",
					"The xml file is required!");
			return;
		}
		sim.ui.AppLoadDialog dlg = null;
		if (showDialog) {
			dlg = new sim.ui.AppLoadDialog(sim.ui.MainFrame.getInstance());
		}
		final sim.ui.AppLoadDialog dlg2 = dlg;
		new Thread() {
			public void run() {
				try {
					AppStatusbar.getInstance().changeAppStatus(
							"Application is loading, please wait!");
					loadAppFromFile(xml);
					AppEngine.getInstance().refreshWorldNode();
				} catch (Exception e) {
					e.printStackTrace();
					if (showDialog)
						dlg2.hideIt();
					MessageUtils.displayError(e);
					return;
				}
				if (showDialog) {
					dlg2.hideIt();
				}
				MessageUtils
						.displayNormal("The application is loaded successfully.");
				AppStatusbar.getInstance().changeAppStatus("App loaded");
			}
		}.start();
		if (showDialog) {
			dlg2.show();
		}
	}

	/**
	 * Store the whole application in the specified map into the external file
	 * 
	 * @param externalFile
	 *            The file the app stored
	 * @return Whether the operation is successful
	 * @throws java.lang.Exception
	 *             When exceptions happened
	 */
	public static boolean appToXML(String externalFile) throws Exception {

		// Check the external file
		if (externalFile == null)
			throw new IllegalArgumentException(
					"The configuration file can not be NULL.");

		Element root = createAppRootElement();
		createGlobalElement(root);
		createWorldElement(root, externalFile);
		createCategoriesElement(root, externalFile);
		createEntitiesElement(root, externalFile);
		Document doc = new Document(root);

		FileWriter writer = new FileWriter(externalFile);
		XMLOutputter outputter = new XMLOutputter();
		outputter.setEncoding("GB2312");
		outputter.setExpandEmptyElements(true);
		outputter.setIndent(true);
		outputter.setNewlines(true);
		outputter.output(doc, writer);
		writer.close();

		return true;

	}

	/*
	 * Write the app root element 'application' which has attributes "name" and
	 * "basedOnBehaviorNetwork"
	 */
	private static Element createAppRootElement() throws Exception {
		Element root = new Element("application");
		root.addAttribute("name", engineRef.appManager.currentApp.getAppName());
		root.addAttribute("version", ConfigParameters.version);
		return root;
	}

	/* Write the global config info */
	private static void createGlobalElement(Element root) throws Exception {

		// Settings
		Element global = new Element("shared-parameters");

		Element resourcePath = new Element("resource-path");
		resourcePath.addContent(engineRef.appManager.currentApp
				.getAppResourceDir());
		global.addContent(resourcePath);

		root.addContent(global);
	}

	/* Write the world info */
	private static void createWorldElement(Element root, String exF)
			throws Exception {

		// World
		SimulationEnvironment se = AppEngine.getInstance()
				.getSimulationEnvironment();
		// Display
		Element world = new Element("world");

		// The background
		Element display = new Element("background-image");
		display.addContent(se.getRelativeImagePath());
		world.addContent(display);

		// Check if the background image is already copied to the resource
		// directory
		// e.g., the application is created for the first time when the
		// background
		// is not copied to the resource directory yet.
		StringBuffer resourceFullPath = new StringBuffer().append(
				new File(exF).getParent()).append(File.separator).append(
				AppEngine.getInstance().appManager.currentApp
						.getAppResourceDir()).append(File.separator);
		String rp = resourceFullPath.toString();
		if (!new File(rp).exists()) {
			// Create the directory first
			new File(rp).mkdir();
		}

		resourceFullPath.append(se.getRelativeImagePath());
		rp = resourceFullPath.toString();
		if (!new File(rp).exists()) {
			// Create the file first
			new File(rp).createNewFile();
			// Copy the background image --- PROBLEMATIC!!!
			sim.util.FileUtils.copyFile(se.getImagePath(), rp);
		}

		// Size of the background
		display = new Element("width");
		display.addContent(se.getWidth() + "");
		world.addContent(display);
		
		display = new Element("height");
		display.addContent(se.getHeight() + "");
		world.addContent(display);

		Element envType = new Element("type");
		int t = AppEngine.getInstance().system.systemParameters.getEnvType();
		if (t == SimulationEnvironment.ROUNDED)
			envType.addContent("ROUNDED");
		else if (t == SimulationEnvironment.OPEN)
			envType.addContent("OPEN");
		else if (t == SimulationEnvironment.CLOSED)
			envType.addContent("CLOSED");
		else
			throw new RuntimeException("The environment type is not legal.");
		world.addContent(envType);

		// Save the configuration
		root.addContent(world);

	}

	/* Write the categories element 'categories' and each category */
	private static void createCategoriesElement(Element root, String exF)
			throws Exception {
		// Categories
		Element categories = new Element("categories");
		root.addContent(categories);

		// All category
		Map allCategories = engineRef.appManager.currentApp.dm.getCategories();
		Iterator iter = allCategories.keySet().iterator();
		while (iter.hasNext()) {
			Category c = (Category) allCategories.get(iter.next());
			createCategoryElement(categories, c, exF);
		}
	}

	/* Write each category element 'category' */
	private static void createCategoryElement(Element parent, Category c,
			String exF) throws Exception {

		// Category
		Element category = new Element("category");
		category.addAttribute("name", c.getEntityType());
		parent.addContent(category);

		// Display
		Element displays = new Element("display");
		Element display = new Element("image");
		display.addContent(c.getRelativeImagePath());
		displays.addContent(display);
		
		display = new Element("width");
		display.addContent(c.getWidth() + "");
		displays.addContent(display);
		
		display = new Element("height");
		display.addContent(c.getHeight() + "");
		displays.addContent(display);
		
		display = new Element("direction");
		display.addContent(String.valueOf(c.getInitialDisplayDirection()));
		displays.addContent(display);
		category.addContent(displays);

		// Check if the image is already copied to the resource directory
		// e.g., the application is created for the first time when the image
		// is not copied to the resource directory yet.
		StringBuffer resourceFullPath = new StringBuffer().append(
				new File(exF).getParent()).append(File.separator).append(
				AppEngine.getInstance().appManager.currentApp
						.getAppResourceDir()).append(File.separator).append(
				c.getRelativeImagePath());
		String rp = resourceFullPath.toString();
		if (!new File(rp).exists()) {
			// Create the file first
			new File(rp).createNewFile();
			// Copy the background image
			sim.util.FileUtils.copyFile(c.getImagePath(), rp);
		}

		// Fields
		List properties = c.getOriginalProperties();
		Element fields = new Element("fields");
		for (int i = 0; i < properties.size(); i++) {
			Property p = (Property) properties.get(i);
			if (p.name.equals("name") || p.name.equals("iconPath")) {
				continue;
			}
			Element field = new Element("field");
			field.addAttribute("name", p.name);
			field.addAttribute("type", PropertyType.types[p.type]);
			field.addContent(String.valueOf(p.value));
			fields.addContent(field);
		}
		category.addContent(fields);

		// Methods
		List methods = c.getAllMethods();
		Element methds = new Element("methods");
		for (int i = 0; i < methods.size(); i++) {
			CMethod p = (CMethod) methods.get(i);
			Element method = new Element("method");
			method.addAttribute("name", p.name);
			method.addContent(p.src); // TO DO: ENCODE?????
			methds.addContent(method);
		}
		category.addContent(methds);
	}

	/* Write behaviors element */
	private static void createBehaviorElement(Element parent, Behavior b)
			throws Exception {
		// Behavior
		Element be = new Element("behavior");
		parent.addContent(be);
		// Behavior action
		Map all = engineRef.system.actionRepository.getAllBehaviorActions();
		/** Create behavior attributes */
		be.addAttribute("name", b.getBehaviorName());
		be.addAttribute("resumable", String.valueOf(b.isResumable()));

		/** Add the equation string and action string as the content */
		Element eqstr = new Element("excitation");
		eqstr.addContent(b.getBehaviorEquation());
		be.addContent(eqstr);

		Element action = new Element("action");
		Map actionsInBehavior = (Map) all.get(new Integer(b.getMyId()));
		action.addContent(((BehaviorAction) actionsInBehavior.get(b.getMyId()
				+ "Action")).getActionString());
		be.addContent(action);
	}

	/* Write entities element */
	private static void createEntitiesElement(Element root, String exF)
			throws Exception {
		// Entities
		Element entities = new Element("entities");
		root.addContent(entities);
		// Each entity
		List aEntities = engineRef.getAvailableEntities();
		for (int i = 0; i < aEntities.size(); i++) {
			Category categoryObject = (Category) aEntities.get(i);
			createEntityElement(entities, categoryObject, exF);
		}
	}

	/* Write entity element */
	private static void createEntityElement(Element parent,
			Category categoryObject, String exF) throws Exception {
		int inx = ((BNCategory) categoryObject)
				.getActionSelectionMechanismIndex();
		String dynamics = "NODYNAMICS";
		if (inx == BehaviorNetwork.DYNAMICS)
			dynamics = "DYNAMICS";
		else if (inx == BehaviorNetwork.MUTUAL)
			dynamics = "MUTUAL";
		else if (inx == BehaviorNetwork.COOPERATIVE)
			dynamics = "COOPERATIVE";
		Element entity = new Element("entity");
		entity.addAttribute("displayName", categoryObject.getDisplayName());
		entity.addAttribute("categoryName", categoryObject.getEntityType());
		entity.addAttribute("dynamics", dynamics);
		entity.addAttribute("direction", String.valueOf(categoryObject
				.getDirection()));
		entity.addAttribute("xposition", ""
				+ (int)categoryObject.getPosition().x);
		entity.addAttribute("yposition", ""
				+ (int)categoryObject.getPosition().y);

		// Display
		Element displays = new Element("display");
		
		Element display = new Element("image");
		display.addContent(categoryObject.getRelativeImagePath());
		displays.addContent(display);
		
		display = new Element("width");
		display.addContent(categoryObject.getWidth() + "");
		displays.addContent(display);
		
		display = new Element("height");
		display.addContent(categoryObject.getHeight() + "");
		displays.addContent(display);
		
		display = new Element("direction");
		display.addContent(String.valueOf(categoryObject
				.getInitialDisplayDirection()));
		displays.addContent(display);
		entity.addContent(displays);

		// Check if the image is already copied to the resource directory
		// e.g., the application is created for the first time when the image
		// is not copied to the resource directory yet.
		StringBuffer resourceFullPath = new StringBuffer().append(
				new File(exF).getParent()).append(File.separator).append(
				AppEngine.getInstance().appManager.currentApp
						.getAppResourceDir()).append(File.separator).append(
				categoryObject.getRelativeImagePath());
		String rp = resourceFullPath.toString();
		if (!new File(rp).exists()) {
			// Create the file first
			new File(rp).createNewFile();
			// Copy the background image
			sim.util.FileUtils.copyFile(categoryObject.getImagePath(), rp);
		}

		// Fields
		Element fields = new Element("fields");
		entity.addContent(fields);
		List filds = categoryObject.getOriginalProperties();
		for (int i = 0; i < filds.size(); i++) {
			Property p = (Property) filds.get(i);
			if (p.name.equals("name") || p.name.equals("iconPath")) {
				continue;
			}
			Element field = new Element("field");
			field.addAttribute("name", p.name);
			field.addContent(String.valueOf(p.value));
			fields.addContent(field);
		}

		// Methods - TO DO: ADD METHODS SECTION

		// Behavior network
		if (categoryObject instanceof BNCategory) {
			Element bn = new Element("entity-dynamics");
			entity.addContent(bn);

			BNCategory bnc = (BNCategory) categoryObject;
			// General dynamics???
			if (bnc.isSystemDynamicMechanism()) {
				bn.addContent(bnc.getGeneralDynamics() == null ? "" : bnc
						.getGeneralDynamics());
			} else {
				BehaviorNetwork bnw = bnc.getBehaviorNetwork();
				List behaviorList = bnw.getBehaviorList();
				for (int i = 0; i < behaviorList.size(); i++) {
					createBehaviorElement(bn, (Behavior) behaviorList.get(i));
				}

				Element coef = null;
				if (!(bnc.getActionSelectionMechanism() instanceof MutualInhibitionMechanism)) {
					coef = new Element("weights");
					coef.addAttribute("dynamic", String
							.valueOf(bnw.isDynamic()));
					if (bnw.isDynamic()) {
						coef.addContent(bnw.getDynamicStr());
					} else {
						coef.addContent(bnw.getWeightsString());
					}
				} else {
					coef = new Element("coefs");
					coef.addAttribute("dynamic", String
							.valueOf(bnw.isDynamic()));
					if (bnw.isDynamic()) {
						coef.addContent(bnw.getDynamicStr());
					} else {
						coef.addContent(bnw.getCoefficientsString());
					}
				}
				bn.addContent(coef);

			}
		}
		parent.addContent(entity);
	}

	/**
	 * Preload some application definition, including application name and
	 * whether the app is based on behavior network.
	 * 
	 * @param externalFile
	 *            The external file the definition loaded from
	 * @return Name of the application
	 * @throws Exception
	 *             Any exception happens
	 */
	public static String preloadAppDef(String externalFile) throws Exception {
		File file = new File(externalFile);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(file);
		Element root = doc.getRootElement();
		String appName = root.getAttributeValue("name");
		return appName;
	}

	/**
	 * Load the whole application from the external file
	 * 
	 * @param externalFile
	 *            The file the app loaded from
	 * @return Whether the operation is successful
	 * @throws java.lang.Exception
	 *             When exceptions happened
	 */
	public static void loadAppFromFile(String externalFile) throws Exception {

		// External application file
		File file = new File(externalFile);

		// Get the directory of the file
		File fileDir = null;

		// Load all information
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(file);
		List elements = doc.getRootElement().getChildren();
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			Element element = (Element) elements.get(i);
			String elementName = element.getName().trim();
			if (elementName.equals("shared-parameters")) {
				loadGlobalElement(/* fileDir, */element);
				fileDir = new File(file.getParent(), File.separator
						+ AppEngine.getInstance().appManager.currentApp
								.getAppResourceDir() + File.separator);
			} else if (elementName.equals("world")) {
				loadWorldElement(fileDir, element);
			} else if (elementName.equals("categories")) {
				loadCategoriesElement(fileDir, element);
			} else if (elementName.equals("entities")) {
				loadEntitiesElement(fileDir, element);
			}
		}

	}

	/* Load the global element */
	private static void loadGlobalElement(/* File parentDir, */Element element)
			throws Exception {
		// Settings
		List children = element.getChildren();
		for (int i = 0; i < children.size(); i++) {
			Element child = (Element) children.get(i);
			String name = child.getName().trim();
			String content = child.getText().trim();
			if (name.equals("resource-path"))
				AppEngine.getInstance().appManager.currentApp
						.setAppResourceDir(content);
		}
	}

	/* Load the world element */
	private static void loadWorldElement(File parentDir, Element element)
			throws Exception {
		// World
		SimulationEnvironment se = AppEngine.getInstance()
				.getSimulationEnvironment();
		// Display
		List children = element.getChildren();
		for (int i = 0; i < children.size(); i++) {
			Element child = (Element) children.get(i);
			String name = child.getName().trim();
			String content = child.getText().trim();
			if (name.equals("background-image")) {
				// FIXME: Use a more elegant way to setup the image tracker
				JComponent c = (JComponent) engineRef
						.getCategoryUpdateListener();
				File imageP = new File(parentDir, content);
				se.setImagePath(imageP.getAbsolutePath()); // Full path
				se.setRelativeImagePath(content);
				Image image = Toolkit.getDefaultToolkit().createImage(
						JarResourceLoader.getResource(se.getImagePath(),
								AppLoader.class));
				MediaTracker tracker = new MediaTracker(c);
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
				tracker = new MediaTracker(c);
				try {
					tracker.addImage(image, 0);
					tracker.waitForAll();
				} catch (InterruptedException ee) {
					ee.printStackTrace();
				} finally {
					// The image failed to load. Use the blank image instead.
					if (image.getWidth(c) <= 0 || image.getHeight(c) <= 0) {
						image = AppEngine.getInstance().resources.seaImage;
					}
				}
				se.setImage(image);
			} 
			else if (name.equals("width"))
				se.setWidth(Integer.parseInt(content));
			else if (name.equals("height"))
				se.setHeight(Integer.parseInt(content));
			else if (name.equals("type")) {
				if (content.toLowerCase().equals("rounded"))
					se.setType(SimulationEnvironment.ROUNDED);
				else if (content.toLowerCase().equals("open"))
					se.setType(SimulationEnvironment.OPEN);
				else if (content.toLowerCase().equals("closed"))
					se.setType(SimulationEnvironment.CLOSED);
			}
		}
	}

	/* Load the categories element 'categories' and each category */
	/* parentDir is the directory path of the external app file */
	private static void loadCategoriesElement(File parentDir, Element element)
			throws Exception {
		List categories = element.getChildren();
		for (int i = 0; i < categories.size(); i++) {
			Element category = (Element) categories.get(i);
			loadCategoryElement(parentDir, category);
		}
	}

	/* Load each category */
	private static void loadCategoryElement(File parentDir, Element element)
			throws Exception {
		String name = element.getAttributeValue("name");
		Display display = null;
		List categories = element.getChildren();
		List properties = new ArrayList();
		List methods = new ArrayList();
		for (int i = 0; i < categories.size(); i++) {
			Element category = (Element) categories.get(i);
			String elementName = category.getName().trim();
			if (elementName.equals("display")) {
				display = new Display();
				List displays = category.getChildren();
				for (int k = 0; k < displays.size(); k++) {
					Element field = (Element) displays.get(k);
					String fname = field.getName().trim();
					String fcontent = field.getText().trim();
					if (fname.equalsIgnoreCase("image")) {
						File image = new File(parentDir, fcontent);
						display.setImagePath(image.getAbsolutePath());
						display.setRelativeImagePath(fcontent);
					} 
					else if (name.equalsIgnoreCase("width"))
						display.setWidth(Integer.parseInt(fcontent));
					else if (name.equalsIgnoreCase("height"))
						display.setHeight(Integer.parseInt(fcontent));
					else if (fname.equalsIgnoreCase("direction"))
						display.setDirection(Double.parseDouble(fcontent));
				}
			} else if (elementName.equals("fields")) {
				List fields = category.getChildren();
				for (int k = 0; k < fields.size(); k++) {
					Element field = (Element) fields.get(k);
					String type = field.getAttributeValue("type").trim();
					int t = 0;
					if (type.equalsIgnoreCase(PropertyType.types[0]))
						t = PropertyType.NUMBER;
					else if (type.equalsIgnoreCase(PropertyType.types[1]))
						t = PropertyType.STRING;
					else if (type.equalsIgnoreCase(PropertyType.types[2]))
						t = PropertyType.OBJECT;
					properties.add(new Property("", t, field
							.getAttributeValue("name"), field.getText()));
				}
			} else if (elementName.equals("methods")) {
				List mthods = category.getChildren();
				for (int k = 0; k < mthods.size(); k++) {
					Element method = (Element) mthods.get(k);
					methods.add(new CMethod(method.getAttributeValue("name"),
							method.getText()));
				}
			}
		}
		/** Check the display component */
		if (display == null)
			throw new Exception("The display component of the category '"
					+ name + "' is missing.");
		/** Check the uniqueness of the category */
		if (engineRef.appManager.currentApp.dm.categoryExist(name)) {
			throw new Exception("The category '" + name
					+ "' is already in the system.");
		}
		/** Create the category */
		engineRef.createANewCategory(name, display, true, properties, methods);
	}

	/* Load the behaviors element 'behaviors' and each behavior */
	private static Behavior loadBehaviorElement(BNCategory bn, Element behavior)
			throws Exception {

		String behaviorName = behavior.getAttributeValue("name");
		boolean resumeCB = new Boolean(behavior.getAttributeValue("resumable"))
				.booleanValue();

		String equationStr = "", actionString = "";
		List children = behavior.getChildren();
		for (int j = 0; j < children.size(); j++) {
			Element ea = (Element) children.get(j);
			if (ea.getName().trim().equals("excitation")) {
				equationStr = ea.getText();
			} else if (ea.getName().trim().equals("action")) {
				actionString = ea.getText();
			}
		}
		return engineRef.createNewBehavior(bn, behaviorName, equationStr,
				resumeCB, actionString);

	}

	/* Load the entities element 'entities' and each entity */
	private static void loadEntitiesElement(File parentDir, Element element)
			throws Exception {
		// Obtain the image tracker - JComponent
		// FIXME: Use a more elegant way to setup the image tracker
		JComponent c = (JComponent) engineRef.getCategoryUpdateListener();
		List entities = element.getChildren();
		Display display = null;
		for (int i = 0; i < entities.size(); i++) {

			/** Whether entities of the category need to update */
			boolean replace = false;

			/** No dynamics */
			boolean nodynamics = false;

			/** General dynamics */
			boolean dyna = false;
			String code = null;

			/** Mutual inhibition mechanism */
			boolean mutual = false, cooperative = false;

			Element entity = (Element) entities.get(i);
			String categoryName = entity.getAttributeValue("categoryName");
			String dynamics = entity.getAttributeValue("dynamics").trim()
					.toUpperCase();
			dyna = dynamics.equals("DYNAMICS");
			mutual = dynamics.equals("MUTUAL");
			cooperative = dynamics.equals("COOPERATIVE");
			nodynamics = dynamics.equals("NODYNAMICS");

			Category categoryObject = (Category) engineRef.appManager.currentApp.dm
					.getCategory(categoryName);
			categoryObject.init(c, categoryName, categoryObject.getImagePath());
			categoryObject.setDirection(Double.parseDouble(entity
					.getAttributeValue("direction")));
			categoryObject.setVisible(true);// TO DO:SAVE VISIBILITY
			String position = entity.getAttributeValue("xposition");
			double x = Double.parseDouble(position);
			position = entity.getAttributeValue("yposition");
			double y = Double.parseDouble(position);
			categoryObject.setPosition(x, y);
			categoryObject.setDisplayName(entity
					.getAttributeValue("displayName"));
			engineRef.appManager.getCurrentApp().setCurrentEntity(
					categoryObject);

			// Initialize fields
			List children = entity.getChildren();
			for (int j = 0; j < children.size(); j++) {
				Element child = (Element) children.get(j);
				if (child.getName().trim().equals("display")) {
					display = new Display();
					List displays = child.getChildren();
					for (int k = 0; k < displays.size(); k++) {
						Element field = (Element) displays.get(k);
						String fname = field.getName().trim();
						String fcontent = field.getText().trim();
						if (fname.equalsIgnoreCase("image")) {
							File imageP = new File(parentDir, fcontent);
							display.setImagePath(imageP.getAbsolutePath());
							display.setRelativeImagePath(fcontent);
							Image image = Toolkit.getDefaultToolkit()
									.createImage(
											JarResourceLoader.getResource(
													display.getImagePath(),
													AppLoader.class));
							MediaTracker tracker = new MediaTracker(c);
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
							tracker = new MediaTracker(c);
							try {
								tracker.addImage(image, 0);
								tracker.waitForAll();
							} catch (InterruptedException ee) {
							}
							display.setImage(image);
						} 
						else if (fname.equalsIgnoreCase("width"))
							display.setWidth(Integer.parseInt(fcontent));
						else if (fname.equalsIgnoreCase("height"))
							display.setHeight(Integer.parseInt(fcontent));
						else if (fname.equalsIgnoreCase("direction"))
							display.setDirection(Double.parseDouble(fcontent));
					}
				} else if (child.getName().trim().equalsIgnoreCase("fields")) {
					List fields = child.getChildren();
					for (int k = 0; k < fields.size(); k++) {
						Element field = (Element) fields.get(k);
						int type = categoryObject.getPropertyType(field
								.getAttributeValue("name"));
						if (type == PropertyType.NUMBER) {
							categoryObject.setValue(field
									.getAttributeValue("name"), new Double(
									Double.parseDouble(field.getText())));
							categoryObject.updatePropertyInitial(field
									.getAttributeValue("name"), new Double(
									Double.parseDouble(field.getText())));
						} else if (type == PropertyType.STRING) {
							categoryObject
									.setValue(field.getAttributeValue("name"),
											field.getText());
							categoryObject
									.updatePropertyInitial(field
											.getAttributeValue("name"), field
											.getText());
						} else if (type == PropertyType.OBJECT) { // FIXME: HOW
							// TO HANDLE
							// THIS?
							categoryObject
									.setValue(field.getAttributeValue("name"),
											field.getText());
							categoryObject
									.updatePropertyInitial(field
											.getAttributeValue("name"), field
											.getText());
						}
					}
				} else if (child.getName().trim().equalsIgnoreCase(
						"entity-dynamics")) {
					BNCategory bn = (BNCategory) categoryObject;
					if (dyna) {
						code = child.getText().trim();
						if (code.equals(""))
							code = null;
					} else if (!nodynamics) {

						List behaviors = child.getChildren();
						for (int k = 0; k < behaviors.size(); k++) {
							Element behavior = (Element) behaviors.get(k);
							if (behavior.getName().trim().equalsIgnoreCase(
									"behavior")) {
								// Load the behavior into behavior repository
								Behavior b = null;
								try {
									b = loadBehaviorElement(bn, behavior);
								} catch (Exception e) {
								}
								// Add the loaded behavior into behavior network
								engineRef.addBehaviorToNetwork(bn, b.getMyId());
								// Replace?
								replace = true;
							} else if (mutual
									&& behavior.getName().trim()
											.equalsIgnoreCase("coefs")) {
								boolean dynamic = Boolean.valueOf(
										behavior.getAttributeValue("dynamic"))
										.booleanValue();
								String coefStr = behavior.getText();
								if (dynamic) {
									bn.getBehaviorNetwork()
											.setDynamicBehaviorNetwork(coefStr);
									replace = true;
									continue;
								}
								List coefficients = new ArrayList();
								StringTokenizer st = new StringTokenizer(
										coefStr);
								while (st.hasMoreElements()) {
									coefficients.add(new Double(Double
											.parseDouble((String) st
													.nextElement())));
								}
								bn.getBehaviorNetwork()
										.updateEdgesFromCoefficientsList(true,
												coefficients);
							} else if (cooperative
									&& behavior.getName().trim()
											.equalsIgnoreCase("weights")) {
								boolean dynamic = Boolean.valueOf(
										behavior.getAttributeValue("dynamic"))
										.booleanValue();
								String coefStr = behavior.getText();
								if (dynamic) {
									bn.getBehaviorNetwork()
											.setDynamicBehaviorNetwork(coefStr);
									replace = true;
									continue;
								}
								List coefficients = new ArrayList();
								StringTokenizer st = new StringTokenizer(
										coefStr);
								while (st.hasMoreElements()) {
									coefficients.add(new Double(Double
											.parseDouble((String) st
													.nextElement())));
								}
								bn.getBehaviorNetwork()
										.updateEdgesFromCoefficientsList(false,
												coefficients);
							}
						}
					}
				}
			}
			if (display != null) {
				categoryObject.setDisplay(display);
			}
			if (!nodynamics && !dyna) {
				if (mutual)
					((BNCategory) categoryObject)
							.setActionSelectionMechanism(new MutualInhibitionMechanism());
				else if (cooperative)
					((BNCategory) categoryObject)
							.setActionSelectionMechanism(new CooperativeMechanism());
			}
			engineRef.getCategoryUpdateListener().entityAdded(categoryObject);
			if (!nodynamics && dyna) {
				((BNCategory) categoryObject).registerGeneralDynamics(code);
			}
			if (replace) {
				engineRef.system.updateEntity(categoryName, categoryObject);
			}
		}
	}

}