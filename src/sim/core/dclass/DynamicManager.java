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

package sim.core.dclass;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import sim.core.AppClassLoader;
import sim.core.AppEngine;
import sim.model.entity.BNCategory;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Display;
import sim.model.entity.Property;
import sim.model.entity.PropertyType;
import sim.model.mechanism.CooperativeMechanism;
import sim.model.mechanism.IMechanism;
import sim.model.mechanism.MutualInhibitionMechanism;
import sim.util.MethodUtils;
import sim.util.SimException;

/**
 * Dynamic class manager.
 * 
 * <p>
 * This class is used in the project where dynamic class is created or queried.
 * The system property SIM_HOME should be set before this class can be used.
 * </p>
 * 
 * <p>
 * Several operations are available in this class: 1) Creation of a new class;
 * 2) Query the created class; 3) Update - Class method or fields
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * 
 */
public class DynamicManager {
	/**
	 * The newest version of the dynamic class. The field is used to record the
	 * latest modified version of the dynamically created class.
	 * 
	 * <p>
	 * Since the dynamically created class is changed frequently and also since
	 * each can only be loaded once by a particular class loader, the original
	 * class name and the latest version number (from 0 to Infinity) are
	 * combined to represent the latest dynamic class.
	 * </p>
	 * 
	 * <p>
	 * The version number 0 presents the original version ( the newly created
	 * version).
	 * </p>
	 * 
	 * <p>
	 * NOTE: This field is used by this dynamic manager only.
	 * </p>
	 */
	protected Map latestClassVersions = new HashMap();

	// The object map used to store object state and methods
	protected Map objects = new HashMap();

	// The user-defined methods for each category
	// The mapping would be "categoryName -> <methodmap>" and
	// <methodmap> is "methodName -> CtMethod object"
	protected Map categoryMethods = new HashMap();

	// The class pool
	protected ClassPool pool = null;

	// The class loader of this dynamic manager
	protected ClassLoader cl = null;

	// The application engine
	protected AppEngine engine = null;

	/**
	 * Customized class pool with specified class loader
	 */
	class MyClassPool extends ClassPool {
		public ClassLoader getClassLoader() {
			return cl;
		}
	}

	/**
	 * Constructor. Construct a new dynamic manager
	 * 
	 * @param e
	 *            The application engine
	 * @throws Exception
	 *             If the dynamic manager can not be constructed successfully.
	 */
	public DynamicManager(ClassLoader cl) throws Exception {

		// Class loader
		this.cl = cl;

		// Application engine
		engine = AppEngine.getInstance();

		// Initialize the class pool
		init();

	}

	/**
	 * Initialize the classpool and preload the necessary parent class.
	 * 
	 * <p>
	 * The classpool can not be pruned, because the classes are changed
	 * frequently. The user can change the field or method arbitrarily.
	 * </p>
	 * 
	 * <p>
	 * An additional classpath is specified in the classpool. It is necessary
	 * for the pool to find the packages by searching the classpath.
	 * </p>
	 * 
	 * <p>
	 * Also, some packages should be specified, since the classes or the methods
	 * would reference to them. Through this, the user does not need to wrong
	 * about the classpath and packages.
	 * </p>
	 * 
	 * <p>
	 * The specialized class loader is initialized and used as the class loader
	 * in the system
	 * </p>
	 * 
	 * <p>
	 * The class <code>Category</code> {@link #sim.model.entity.Category} is pre-imported since all
	 * the configurable entites derive from this class.
	 * </p>
	 * 
	 *@see javassist.ClassPool {@link #javassist.ClassPool}
	 */
	private void init() throws Exception {

		// Initialize the class pool and class loader
		initPool();

		// Initialize the class path
		initClassPath();

		// initialize the class pool
		initClassPool();

		// register parent classes for the new created classes
		registerParentClasses();

	}

	/**
	 * Initialize the class pool
	 * 
	 * @throws Exception
	 */
	void initPool() throws Exception {
		pool = new MyClassPool();
		pool.appendSystemPath();
	}

	/**
	 * Initialize the class path
	 * 
	 * @throws Exception
	 */
	void initClassPath() throws Exception {

		// Add the jars (sim.jar) in the lib directory or the classes
		// in the classes/bin directory.
		pool.appendClassPath(engine.jrl.getResourcePath());

		// Add the jars in the system class path into the pool
		pool.appendSystemPath();

	}

	/**
	 * Initialize the class pool
	 */
	private void initClassPool() {

		ClassPool.doPruning = false;
		pool.importPackage("java.util");
		pool.importPackage("java.io");
		pool.importPackage("java.awt");
		pool.importPackage("sim.model.entity");
		pool.importPackage("sim.util");

	}

	/**
	 * Register parent classes for all new created classes
	 * 
	 * @throws Exception
	 */
	protected void registerParentClasses() throws Exception {

		// Category class
		StringBuffer category = new StringBuffer();
		category.append("sim").append(File.separator);
		category.append("model").append(File.separator);
		category.append("entity").append(File.separator);
		category.append("Category.class");

		// BehaviorNetwork Category class
		StringBuffer bncategory = new StringBuffer();
		bncategory.append("sim").append(File.separator);
		bncategory.append("model").append(File.separator);
		bncategory.append("entity").append(File.separator);
		bncategory.append("BNCategory.class");
		try {

			// Make the parent class1
			InputStream is = engine.jrl.getEntryAsInputStream(category
					.toString());
			pool.makeClass(is);

			// Make the parent class2
			is = engine.jrl.getEntryAsInputStream(bncategory.toString());
			pool.makeClass(is);

		} catch (Exception e) {
			throw (new SimException(
					"CAT-NF-001A",
					"The key classes 'Category' or 'BNCategory' can not be found.",
					e));
		}
	}

	/**
	 * Return all defined categories
	 * 
	 * @return all defined categories
	 */
	public Map getCategories() {
		return this.objects;
	}

	/**
	 * Is there any category in the application?
	 * 
	 * @return True if any category has been created.
	 */
	public boolean categoryExist() {
		return !this.objects.isEmpty();
	}

	/**
	 * IS there any user-defined method of the specified category
	 * 
	 * @param catName
	 *            Category name
	 * @return
	 */
	public boolean categoryMethodExist(String catName) {
		Map methods = (Map) categoryMethods.get(catName);
		if (methods == null || methods.isEmpty())
			return false;
		return true;
	}

	/**
	 * Return all defined categories versions
	 * 
	 * @return all defined categories versions
	 */
	public Map getCategoryVersions() {
		return this.latestClassVersions;
	}

	/**
	 * Return all defined categories methods
	 * 
	 * @return
	 */
	public Map getCategoryMethods() {
		return this.categoryMethods;
	}

	/**
	 * Test program
	 * 
	 * @param arg
	 * @throws Exception
	 */
	public static void main(String[] arg) throws Exception {
		//
		 List p = new java.util.ArrayList();
		 p.add(new Property("", 0, "amount", "2.0"));
		 p.add(new Property("", 0, "direction", "0.0"));
		 System.setProperty("SIM_HOME", System.getProperty("user.dir"));
		 DynamicManager m = new DynamicManager(new AppClassLoader());		 
		 Category o = (Category)m.createCategory("NewC", "G:/d.jpg", false, p);
		// List properties = o.getAllProperties();
		//
		//
		// // createMethod("NewC", "proc",
		// "public double proc() { amount++; return amount / direction; }");
		// // createMethod("NewC", "handle",
		// "public void handle(){List properties = this.getAllProperties(); System.out.println(properties.size()); Property p = (Property)properties.get(0); System.out.println(p.name);}");
		// // createMethod("NewC", "printName",
		// "public void printName(Entity entity){System.out.println(entity.getDisplayName());}");
		//
		// // CtClass cc = pool.get("sim.model.entity.NewC");
		// // cc.defrost();
		// //
		// cc.addMethod(CtMethod.make("public double proc() { amount++; return amount / direction; }",
		// cc));
		// //
		// cc.addMethod(CtMethod.make("public void handle(){List properties = this.getAllProperties(); System.out.println(properties.size()); Property p = (Property)properties.get(0); System.out.println(p.name);}",
		// cc));
		// //
		// cc.addMethod(CtMethod.make("public void printName(Entity entity){System.out.println(entity.getDisplayName());}",
		// cc));
		//
		// o = (Category)getCategory("NewC");
		// try {
		// MethodUtils.invokeExactMethod(o, "proc", null);
		// MethodUtils.invokeExactMethod(o, "handle", null);
		// }catch(Exception e){
		// e.printStackTrace();
		// }
	}

	/**
	 * Get the specified category object which is the template of the further
	 * entities of the class the specified object represents.
	 * 
	 * <p>
	 * Basically, the further entities of the category object share some common
	 * information, for example, the category name (the instance name is the
	 * category name plus a unique number), and the category image used in the
	 * simulation environment. While these shared information can be changed for
	 * each individual entity.
	 * </p>
	 * <ul>
	 * <ui> Firstly, the category class is retrieved and then construct an
	 * instance from it; </ui> <ui> Secondly, the instance is populated with the
	 * shared info stored in the template category objects; </ui> <ui> Lastly,
	 * the instance is returned. </ui>
	 * </ul>
	 * 
	 * @param key
	 *            The key of the object
	 * @return The object
	 */
	public Object getCategory(String key) throws Exception {
		// Save the key
		String k = key;
		// Get the class object template
		Object template = objects.get(key);
		// Get the latest class version
		Object version = latestClassVersions.get(key);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			key += ((Integer) version).intValue();
		}
		// Get the original class definition
		Object o = null;
		try {
			o = cl.loadClass("simulation.newmodel." + key).newInstance();
		} catch (Exception e1) {
			try {
				o = Thread.currentThread().getContextClassLoader().loadClass(
						"simulation.newmodel." + key).newInstance();
			} catch (Exception e11) {
				try {
					Class clz = pool.get("simulation.newmodel." + key)
							.toClass();
					o = clz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					throw (new SimException(
							"CAT-S-NF-001A",
							"The category class '" + k + "' can not be loaded.",
							e));
				}
			}
		}
		try {
			MethodUtils.invokeExactMethod(o, "setEntityType", MethodUtils
					.invokeExactMethod(template, "getEntityType", null));

			/** Set the image path and relative image path */
			String ip = (String) MethodUtils.invokeExactMethod(template,
					"getImagePath", null);
			String rip = (String) MethodUtils.invokeExactMethod(template,
					"getRelativeImagePath", null);
			MethodUtils.invokeExactMethod(o, "setImagePath", ip);
			// If the relative image path is not set yet, then extract it
			// From the absolute image path. The relative image path is
			// the current directory of the XML file.
			MethodUtils.invokeExactMethod(o, "setRelativeImagePath",
					rip == null ? ip
							.substring(ip.lastIndexOf(File.separator) + 1)
							: rip);

			MethodUtils.invokeExactMethod(o, "setDisplay", MethodUtils
					.invokeExactMethod(template, "getDisplay", null));
			MethodUtils.invokeExactMethod(o, "registerProperties",
					new Object[] { MethodUtils.invokeExactMethod(template,
							"getProperties", null) },
					new Class[] { List.class });
			MethodUtils.invokeExactMethod(o, "registerMethods",
					new Object[] { MethodUtils.invokeExactMethod(template,
							"getAllMethods", null) },
					new Class[] { List.class });
			// Behavior network based category
			if (o instanceof BNCategory) {
				MethodUtils.invokeExactMethod(o, "setActionSelectionMechanism",
						new Object[] { MethodUtils.invokeExactMethod(template,
								"getActionSelectionMechanism", null) },
						new Class[] { IMechanism.class });
			}
		} catch (Exception e) {
			throw (new SimException("CAT-S-IF-001A", "The category class '" + k
					+ "' can not be initialized.", e));
		}
		return o;
	}

	/**
	 * Get the specified category object which is the template of the further
	 * entities of the class the specified object represents.
	 * 
	 * <p>
	 * Basically, the further entities of the category object share some common
	 * information, for example, the category name (the instance name is the
	 * category name plus a unique number), and the category image used in the
	 * simulation environment. While these shared information can be changed for
	 * each individual entity.
	 * </p>
	 * <ul>
	 * <ui> Firstly, the category class is retrieved and then construct an
	 * instance from it; </ui> <ui> Then, the instance is returned. </ui>
	 * </ul>
	 * 
	 * @param key
	 *            The key of the object
	 * @return The object
	 */
	public Object getCategoryWithoutPopulation(String key) throws Exception {
		// Save the key
		String k = key;
		// Get the latest class version
		Object version = latestClassVersions.get(key);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			key += ((Integer) version).intValue();
		}
		// Get the original class definition
		Object o = null;
		try {
			Class clz = pool.get("simulation.newmodel." + key).toClass();
			o = clz.newInstance();
		} catch (Exception e) {
			try {
				o = Thread.currentThread().getContextClassLoader().loadClass(
						"simulation.newmodel." + key).newInstance();
			} catch (Exception e1) {
				throw (new SimException("CAT-S-NF-001A", "The category class '"
						+ k + "' can not be loaded.", e));
			}
		}
		return o;
	}

	/**
	 * Detach the category and remove it from the class pool.
	 * 
	 * @param categoryName
	 *            The name of the category to be detached.
	 * @throws Exception
	 *             if the category can not be detached successfully.
	 */
	public void detachCategory(String categoryName) throws Exception {
		// Get latest version
		Object version = latestClassVersions.get(categoryName);
		int v = 0;
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			v = ((Integer) version).intValue();
		}
		do {
			String key = categoryName;
			// Get the key
			if (v > 0)
				key += v;
			try {
				// Get the class
				CtClass cc = pool.get("simulation.newmodel." + key);
				// detach it
				cc.detach();
			} catch (Exception e) {
				// MessageUtils.debug(new SimException("CAT-S-DF-101A",
				// "The category '"+categoryName+"' can not be detached.",
				// e));
			}
			// Decrease the version
			--v;
		} while (v >= 0);
	}

	/**
	 * Update the specified dynamic class definition and return an instance of
	 * the updated category class.
	 * 
	 * <p>
	 * This method first removes the old properties definition and then add the
	 * new properties in the category class by invoking the methods present in
	 * the javassist package.
	 * </p>
	 * 
	 * <P>
	 * If it is a behavior-based simulation, the parent class is set to
	 * <code>BNCategory</code>, which contains specific logics than the general
	 * parent class <code>Category</code>
	 * </P>
	 * 
	 * <p>
	 * The new category class is a subclass of the core model,
	 * <code>Category</code> {@link #sim.model.entity.Category}.
	 * </p>
	 * 
	 * <p>
	 * The new category class can have its own fields, which is configured in
	 * the GUI by the user.
	 * </p>
	 * <p>
	 * After setting up the fields, a new category instance is made and keep as
	 * the parent of that category, meaning that all the other instances made
	 * from this category are created from the category class (retrieved from
	 * the classpool and) and then populated with all the field values with this
	 * parent object. The purpose of doing this is to aviod the effect between
	 * different instances (one may affect other's field values) and also to
	 * keep the default common field values (such as name and iconPath). The
	 * default values can be changed independently in individual instances. see
	 * 
	 * {@link #public Object getCategory(String key)}
	 * 
	 * </p>
	 * 
	 * <p>
	 * Also, the method updates the version number for further reference.
	 * </p>
	 * 
	 * <p>
	 * The method definition of the new category is the same as that of the
	 * existing category to be updated.
	 * </p>
	 * 
	 * @param oldClassName
	 *            The class name of the existing category to be updated
	 * @param className
	 *            The class name, currently use the name entered in GUI
	 * @param iconPath
	 *            The image file path, which will be drawn in the GUI
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup behavior network
	 * @param properties
	 *            The class properties, which contains the field information
	 * @return The category object created If no exception is thrown
	 * @throws Exception
	 *             If the category can not be created successfully.
	 */
	public Object updateCategory(String oldClassName, String className,
			String iconPath, boolean needBehaviorNetwork, List properties)
			throws Exception {
		// Category template
		Category old = (Category) objects.get(oldClassName);
		// Retrieve the previous definition
		Object version = latestClassVersions.get(oldClassName);
		int v = 0;
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			v = ((Integer) version).intValue();
		}
		String key = oldClassName;
		// Get the key
		if (v > 0)
			key += v;
		// Get the class
		CtClass cc = pool.get("simulation.newmodel." + key);
		cc.defrost();
		// Change the super class if necessary
		if (needBehaviorNetwork && !(old instanceof BNCategory))
			cc.setSuperclass(pool.get("sim.model.entity.BNCategory"));
		// Rename it
		cc.setName("simulation.newmodel." + (className + (v + 1)));
		// Remove the old definition of properties
		try {
			// Remove old fields
			CtConstructor[] ctc = cc.getConstructors();
			for (int i = 0; ctc != null && i < ctc.length; i++) {
				// System.out.println(ctc[i].toString());
				ctc[i].setBody(null);
			}
			List oldProps = (List) old.getProperties();
			for (int i = 0; i < oldProps.size(); i++) {
				try {
					cc
							.removeField((CtField) ((Property) oldProps.get(i)).byteCode);
				} catch (Exception ee) {
				}
			}
			// Insert new definitions
			if (properties != null) {
				for (int i = 0; i < properties.size(); i++) {
					Property p = (Property) properties.get(i);
					StringBuffer sb = new StringBuffer("public ");
					if (p.type == PropertyType.NUMBER) {
						sb.append("double ");
					} else if (p.type == PropertyType.STRING) {
						sb.append("String ");
					} else {
						sb.append("Object ");
					}
					sb.append(p.name);
					if (p.value != null) {
						if (p.type == PropertyType.NUMBER) {
							sb.append("=")
									.append(
											Double.parseDouble(String
													.valueOf(p.value)));
						} else {
							sb.append("=").append(p.value);
						}
					}
					sb.append(";");
					p.byteCode = CtField.make(sb.toString(), cc);
					cc.addField((CtField) p.byteCode);
				}
			}
		} catch (Exception e) {
			throw (new SimException("CAT-S-FR-011A",
					"Properties of the category '" + oldClassName
							+ "' can not be found.", e));

		}
		// Remove old version and class name
		latestClassVersions.remove(oldClassName);
		objects.remove(oldClassName);
		// Save new version
		latestClassVersions.put(className, new Integer(v + 1));
		// Regenerate new template class
		Category template = (Category) getCategoryWithoutPopulation(className);
		// Update the template class
		template.registerProperties(properties);
		template.registerMethods(old.getAllMethods());
		template.setEntityType(className);
		template.setImagePath(iconPath);
		template.setRelativeImagePath(iconPath.substring(iconPath
				.lastIndexOf(File.separator) + 1));
		// // Behavior network based category
		// if (template instanceof BNCategory && mechanismIndex == 1) {
		// ((BNCategory)template).setActionSelectionMechanism(new
		// CooperativeMechanism());
		// }
		// Save the template
		objects.put(className, template);
		// Adjust category methods
		if (!className.equals(oldClassName)) {
			java.util.Iterator iter = categoryMethods.keySet().iterator();
			while (iter.hasNext()) {
				Object k = iter.next();
				key = (String) k;
				if (key.equals(oldClassName)) {
					Object value = categoryMethods.get(key);
					categoryMethods.remove(oldClassName);
					categoryMethods.put(className, value);
					break;
				}
			}
		}
		return getCategory(className);
	}

	/**
	 * Check whether the category is already defined
	 * 
	 * @param catName
	 *            Name of the category to check
	 * @return Whether the category is already defined.
	 */
	public boolean categoryExist(String catName) {
		return objects.get(catName) != null;
	}

	/**
	 * Create a new category based on an existing category. The created category
	 * has the name "CATEGORYNAME_Copy_CurrentMilliSeconds", which can be
	 * changed later.
	 * 
	 * 
	 * <p>
	 * This method creates the new category class by invoking the methods
	 * present in the javassist package.
	 * </p>
	 * 
	 * <P>
	 * If it is a behavior-based simulation, the parent class is set to
	 * <code>BNCategory</code>, which contains specific logics than the general
	 * parent class <code>Category</code>
	 * </P>
	 * 
	 * <p>
	 * The new category class is a subclass of the core model,
	 * <code>Category</code> {@link #sim.model.entity.Category}.
	 * </p>
	 * 
	 * <p>
	 * The new category class can have its own fields, which are copied from the
	 * specified category.
	 * </p>
	 * <p>
	 * After setting up the fields, a new category instance is made and keep as
	 * the parent of that category, meaning that all the other instances made
	 * from this category are created from the category class (retrieved from
	 * the classpool and) and then populated with all the field values with this
	 * parent object. The purpose of doing this is to aviod the effect between
	 * different instances (one may affect other's field values) and also to
	 * keep the default common field values (such as name and iconPath). The
	 * default values can be changed independently in individual instances. see
	 * 
	 * {@link #public Object getCategory(String key)}
	 * 
	 * </p>
	 * 
	 * <p>
	 * This method is similar to <code>createCategory</code>. The difference is
	 * that, the created category is not stored as a template util the user
	 * specifies to do this.
	 * </p>
	 * 
	 * @param category
	 *            The original category to copy
	 * @return Another copied category
	 * @throws Exception
	 *             If exception throws
	 */
	public Category createANewCategoryByCopy(Category category)
			throws Exception {
		// A unique class name
		String className = category.getEntityType() + "_Copy_"
				+ System.currentTimeMillis();
		// Make a new class
		CtClass cc = pool.makeClass("simulation.newmodel." + className);
		// Set its super class
		CtClass sup = null;
		int mechanismIndex = 0;
		if (category instanceof BNCategory) {
			sup = pool.get("sim.model.entity.BNCategory");

			BNCategory c = (BNCategory) category;
			mechanismIndex = c.getActionSelectionMechanism() instanceof CooperativeMechanism ? 1
					: 0;

		} else {
			sup = pool.get("sim.model.entity.Category");
		}
		cc.setSuperclass(sup);

		// Make a copy of properties
		List properties = category.getOriginalProperties();
		// Set the properties of the created class
		if (properties != null) {
			for (int i = 0; i < properties.size(); i++) {
				Property p = (Property) properties.get(i);
				Property copy = p.copy();
				StringBuffer sb = new StringBuffer("public ");
				if (copy.type == PropertyType.NUMBER) {
					sb.append("double ");
				} else if (copy.type == PropertyType.STRING) {
					sb.append("String ");
				} else {
					sb.append("Object ");
				}
				sb.append(copy.name);
				if (copy.value != null) {
					if (copy.type == PropertyType.NUMBER) {
						sb.append("=").append(
								Double.parseDouble(String.valueOf(copy.value)));
					} else {
						sb.append("=").append(copy.value);
					}
				}
				sb.append(";");
				copy.byteCode = CtField.make(sb.toString(), cc);
				cc.addField((CtField) copy.byteCode);
				properties.set(i, copy);
			}
		}
		// Make a copy of methods
		List methods = category.getAllMethods();
		if (methods != null) {
			for (int i = 0; i < methods.size(); i++) {
				CMethod m = (CMethod) methods.get(i);
				CMethod method = m.copy();
				String translatedCode = CodeHelper.translate(method.src);
				method.transSuccess = true;
				CtMethod methodByte = null;
				try {
					methodByte = CtMethod.make(translatedCode, cc);
					cc.addMethod(methodByte);
					method.bytecode = methodByte;
				} catch (Exception e) {
					throw (new SimException("CAT-S-MF-011B", "Method '"
							+ method.name
							+ "' can not be registered in the category '"
							+ cc.getName() + "'.", e));
				}
				methods.set(i, method);
				// Map methodMap = (Map)categoryMethods.get(className);
				// if (methodMap == null) methodMap = new HashMap();
				// methodMap.put(method.name, methodByte);
				// categoryMethods.put(className, methodMap);
			}
		}
		// Construct a category object to store important info which is used
		// when
		// the instance is created
		Category o = null;
		try {
			o = (Category) pool.get("simulation.newmodel." + className)
					.toClass().newInstance();
			o.setEntityType(className);
			o.setDisplay(category.getDisplay());
			o.setImagePath(category.getImagePath());
			o.setRelativeImagePath(category.getRelativeImagePath());
			if ((o instanceof BNCategory)) {
				if (mechanismIndex == 1)
					((BNCategory) o)
							.setActionSelectionMechanism(new CooperativeMechanism());
				else
					((BNCategory) o)
							.setActionSelectionMechanism(new MutualInhibitionMechanism());
			}
			if (properties != null) {
				for (int i = 0; i < properties.size(); i++) {
					Property p = (Property) properties.get(i);
					o.registerPropertyName(p);
				}
			}
			if (methods != null) {
				for (int i = 0; i < methods.size(); i++) {
					CMethod method = (CMethod) methods.get(i);
					o.registerMethod(method);
				}
			}
		} catch (Exception e) {
			throw (new SimException("CAT-S-IF-021B",
					"Properties of the category '" + className
							+ "' can not be initialized.", e));
		}
		// // Store the states of this category object
		// objects.put(className, o);
		// // Sore the class version
		// latestClassVersions.put(className, new Integer(0));
		return o;
	}

	/**
	 * The action for the paste operation in
	 * <code>sim.ui.panels.CategoryDefinePanel</code>. The copied category is
	 * saved as a template.
	 * 
	 * <p>
	 * Category map, Category version map and method map are setup accodingly.
	 * </p>
	 * 
	 * @see #createANewCategoryByCopy(Category)
	 * @param category
	 *            Category to save
	 * @return Whether the category is saved successfully
	 */
	public boolean saveCopiedCategory(Category category) {
		// Class name
		String className = category.getEntityType();
		// Store the states of this category object
		objects.put(className, category);
		// Sore the class version
		latestClassVersions.put(className, new Integer(0));
		// Method map
		Map methodMap = (Map) categoryMethods.get(className);
		if (methodMap == null) {
			methodMap = new HashMap();
			categoryMethods.put(className, methodMap);
		}
		List methods = category.getAllMethods();
		if (methods != null) {
			for (int i = 0; i < methods.size(); i++) {
				CMethod m = (CMethod) methods.get(i);
				methodMap.put(m.name, m.bytecode);
			}
		}
		return true;
	}

	/**
	 * Create a dynamic class represent a category and return an instance of the
	 * created category class.
	 * 
	 * <p>
	 * This method creates the new category class by invoking the methods
	 * present in the javassist package.
	 * </p>
	 * 
	 * <P>
	 * If it is a behavior-based simulation, the parent class is set to
	 * <code>BNCategory</code>, which contains specific logics than the general
	 * parent class <code>Category</code>
	 * </P>
	 * 
	 * <p>
	 * The new category class is a subclass of the core model,
	 * <code>Category</code> {@link #sim.model.entity.Category}.
	 * </p>
	 * 
	 * <p>
	 * The new category class can have its own fields, which is configured in
	 * the GUI by the user.
	 * </p>
	 * <p>
	 * After setting up the fields, a new category instance is made and keep as
	 * the parent of that category, meaning that all the other instances made
	 * from this category are created from the category class (retrieved from
	 * the classpool and) and then populated with all the field values with this
	 * parent object. The purpose of doing this is to aviod the effect between
	 * different instances (one may affect other's field values) and also to
	 * keep the default common field values (such as name and iconPath). The
	 * default values can be changed independently in individual instances. see
	 * 
	 * {@link #public Object getCategory(String key)}
	 * 
	 * </p>
	 * 
	 * <p>
	 * Also, the method records the version number 0 (the original version )
	 * with the class number for further reference.
	 * </p>
	 * 
	 * @param className
	 *            The class name, currently use the name entered in GUI
	 * @param iconPath
	 *            The image file path, which will be drawn in the GUI
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup behavior network
	 * @param properties
	 *            The class properties, which contains the field information
	 * @return The category object created If no exception is thrown
	 * @throws Exception
	 *             If the category can not be created successfully.
	 */
	public Object createCategory(String className, String iconPath,
			boolean needBehaviorNetwork, List properties) throws Exception {
		// Make a new class
		CtClass cc = pool.makeClass("simulation.newmodel." + className);
		// Set its super class
		CtClass sup = null;
		if (needBehaviorNetwork) {
			sup = pool.get("sim.model.entity.BNCategory");
		} else {
			sup = pool.get("sim.model.entity.Category");
		}
		cc.setSuperclass(sup);
		// Set the properties of the created class
		if (properties != null) {
			for (int i = 0; i < properties.size(); i++) {
				Property p = (Property) properties.get(i);
				StringBuffer sb = new StringBuffer("public ");
				if (p.type == PropertyType.NUMBER) {
					sb.append("double ");
				} else if (p.type == PropertyType.STRING) {
					sb.append("String ");
				} else {
					sb.append("Object ");
				}
				sb.append(p.name);
				if (p.value != null) {
					if (p.type == PropertyType.NUMBER) {
						sb.append("=").append(
								Double.parseDouble((String) (p.value)));
					} else {
						sb.append("=").append(p.value);
					}
				}
				sb.append(";");
				p.byteCode = CtField.make(sb.toString(), cc);
				cc.addField((CtField) p.byteCode);
			}
		}
		// Construct a category object to store important info which is used
		// when
		// the instance is created
		Object o = null;
		try {
			o = pool.get("simulation.newmodel." + className).toClass()
					.newInstance();
			MethodUtils.invokeExactMethod(o, "setEntityType", className);
			MethodUtils.invokeExactMethod(o, "setImagePath", iconPath);
			MethodUtils.invokeExactMethod(o, "setRelativeImagePath", iconPath
					.substring(iconPath.lastIndexOf(File.separator) + 1));

			// if ((o instanceof BNCategory) && mechanismIndex == 1) {
			// ((BNCategory)o).setActionSelectionMechanism(new
			// CooperativeMechanism());
			// }
			if (properties != null) {
				for (int i = 0; i < properties.size(); i++) {
					Property p = (Property) properties.get(i);
					MethodUtils.invokeExactMethod(o, "registerPropertyName", p);
				}
			}
		} catch (Exception e) {
			throw (new SimException("CAT-S-IF-021B",
					"Properties of the category '" + className
							+ "' can not be initialized.", e));
		}
		// Store the states of this category object
		objects.put(className, o);
		// Sore the class version
		latestClassVersions.put(className, new Integer(0));
		return getCategory(className);
	}

	/**
	 * Create a dynamic class represent a category and return an instance of the
	 * created category class.
	 * 
	 * <p>
	 * This method creates the new category class by invoking the methods
	 * present in the javassist package.
	 * </p>
	 * 
	 * <P>
	 * If it is a behavior-based simulation, the parent class is set to
	 * <code>BNCategory</code>, which contains specific logics than the general
	 * parent class <code>Category</code>
	 * </P>
	 * 
	 * <p>
	 * The new category class is a subclass of the core model,
	 * <code>Category</code> {@link #sim.model.entity.Category}.
	 * </p>
	 * 
	 * <p>
	 * The new category class can have its own fields, which is configured in
	 * the GUI by the user.
	 * </p>
	 * <p>
	 * After setting up the fields, a new category instance is made and keep as
	 * the parent of that category, meaning that all the other instances made
	 * from this category are created from the category class (retrieved from
	 * the classpool and) and then populated with all the field values with this
	 * parent object. The purpose of doing this is to aviod the effect between
	 * different instances (one may affect other's field values) and also to
	 * keep the default common field values (such as name and iconPath). The
	 * default values can be changed independently in individual instances. see
	 * 
	 * {@link #public Object getCategory(String key)}
	 * 
	 * </p>
	 * 
	 * <p>
	 * Also, the created category is populated with methods, which are saved in
	 * a method map which may be used further.
	 * </p>
	 * 
	 * <p>
	 * Also, the method records the version number 0 (the original version )
	 * with the class number for further reference.
	 * </p>
	 * 
	 * @param className
	 *            The class name, currently use the name entered in GUI
	 * @param iconPath
	 *            The image file path, which will be drawn in the GUI
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup behavior network
	 * @param properties
	 *            The class properties, which contains the field information
	 * @param methods
	 *            The method list to be added to the created category class
	 * @return The category object created If no exception is thrown
	 * @throws Exception
	 *             If the category can not be created successfully
	 */
	public Category createCategory(String className, String iconPath,
			boolean needBehaviorNetwork, List properties, List methods)
			throws Exception {
		// Make a new class
		CtClass cc = pool.makeClass("simulation.newmodel." + className);
		// Set its super class
		CtClass sup = null;
		if (needBehaviorNetwork) {
			sup = pool.get("sim.model.entity.BNCategory");
		} else {
			sup = pool.get("sim.model.entity.Category");
		}
		cc.setSuperclass(sup);
		// Set the properties of the created class
		if (properties != null) {
			for (int i = 0; i < properties.size(); i++) {
				Property p = (Property) properties.get(i);
				StringBuffer sb = new StringBuffer("public ");
				if (p.type == PropertyType.NUMBER) {
					sb.append("double ");
				} else if (p.type == PropertyType.STRING) {
					sb.append("String ");
				} else {
					sb.append("Object ");
				}
				sb.append(p.name);
				if (p.value != null) {
					sb.append("=").append(p.value);
				}
				sb.append(";");
				p.byteCode = CtField.make(sb.toString(), cc);
				cc.addField((CtField) p.byteCode);
			}
		}
		// Set the methods of the created class
		if (methods != null) {
			for (int i = 0; i < methods.size(); i++) {
				CMethod method = (CMethod) methods.get(i);
				String translatedCode = CodeHelper.translate(method.src);
				method.transSuccess = true;
				CtMethod methodByte = null;
				try {
					methodByte = CtMethod.make(translatedCode, cc);
					cc.addMethod(methodByte);
				} catch (Exception e) {
					throw (new SimException("CAT-S-MF-011B", "Method '"
							+ method.name
							+ "' can not be registered in the category '"
							+ cc.getName() + "'.", e));
				}
				Map methodMap = (Map) categoryMethods.get(className);
				if (methodMap == null)
					methodMap = new HashMap();
				methodMap.put(method.name, methodByte);
				categoryMethods.put(className, methodMap);
			}
		}
		// Construct a category object to store important info which is used
		// when
		// the instance is created
		Object o = null;
		try {
			o = pool.get("simulation.newmodel." + className).toClass()
					.newInstance();
			MethodUtils.invokeExactMethod(o, "setEntityType", className);
			MethodUtils.invokeExactMethod(o, "setImagePath", iconPath);
			MethodUtils.invokeExactMethod(o, "setRelativeImagePath", iconPath
					.substring(iconPath.lastIndexOf(File.separator) + 1));
			// if ((o instanceof BNCategory) && mechanismIndex == 1) {
			// ((BNCategory)o).setActionSelectionMechanism(new
			// CooperativeMechanism());
			// }
			if (properties != null) {
				for (int i = 0; i < properties.size(); i++) {
					Property p = (Property) properties.get(i);
					MethodUtils.invokeExactMethod(o, "registerPropertyName", p);
				}
			}
			if (methods != null) {
				for (int i = 0; i < methods.size(); i++) {
					try {
						MethodUtils.invokeExactMethod(o, "registerMethod",
								new Object[] { methods.get(i) },
								new Class[] { CMethod.class });
					} catch (Exception e) {
						throw (new SimException("CAT-S-MF-011B",
								"Methods of the category '" + className
										+ "' can not be registered.", e));
					}
				}
			}
		} catch (Exception e) {
			throw (new SimException("CAT-S-MPF-011B",
					"Methods/Properties of the category '" + className
							+ "' can not be registered.", e));
		}
		// Store the states of this category object
		objects.put(className, o);
		// Sore the class version
		latestClassVersions.put(className, new Integer(0));
		return (Category) getCategory(className);
	}

	/**
	 * Create a dynamic class represent a category and return an instance of the
	 * created category class.
	 * 
	 * <p>
	 * This method creates the new category class by invoking the methods
	 * present in the javassist package.
	 * </p>
	 * 
	 * <P>
	 * If it is a behavior-based simulation, the parent class is set to
	 * <code>BNCategory</code>, which contains specific logics than the general
	 * parent class <code>Category</code>
	 * </P>
	 * 
	 * <p>
	 * The new category class is a subclass of the core model,
	 * <code>Category</code> {@link #sim.model.entity.Category}.
	 * </p>
	 * 
	 * <p>
	 * The new category class can have its own fields, which is configured in
	 * the GUI by the user.
	 * </p>
	 * <p>
	 * After setting up the fields, a new category instance is made and keep as
	 * the parent of that category, meaning that all the other instances made
	 * from this category are created from the category class (retrieved from
	 * the classpool and) and then populated with all the field values with this
	 * parent object. The purpose of doing this is to aviod the effect between
	 * different instances (one may affect other's field values) and also to
	 * keep the default common field values (such as name and iconPath). The
	 * default values can be changed independently in individual instances. see
	 * 
	 * {@link #public Object getCategory(String key)}
	 * 
	 * </p>
	 * 
	 * <p>
	 * Also, the created category is populated with methods, which are saved in
	 * a method map which may be used further.
	 * </p>
	 * 
	 * <p>
	 * Also, the method records the version number 0 (the original version )
	 * with the class number for further reference. And initializes the display
	 * component.
	 * </p>
	 * 
	 * @param className
	 *            The class name, currently use the name entered in GUI
	 * @param display
	 *            The display component of the category
	 * @param needBehaviorNetwork
	 *            Whether the category needs to setup behavior network
	 * @param properties
	 *            The class properties, which contains the field information
	 * @param methods
	 *            The method list to be added to the created category class
	 * @return The category object created If no exception is thrown
	 * @throws Exception
	 *             If the category can not be created successfully
	 */
	public Category createCategory(String className, Display display,
			boolean needBehaviorNetwork, List properties, List methods)
			throws Exception {
		// Make a new class
		CtClass cc = pool.makeClass("simulation.newmodel." + className);
		// Set its super class
		CtClass sup = null;
		if (needBehaviorNetwork) {
			sup = pool.get("sim.model.entity.BNCategory");
		} else {
			sup = pool.get("sim.model.entity.Category");
		}
		cc.setSuperclass(sup);
		// Set the properties of the created class
		if (properties != null) {
			for (int i = 0; i < properties.size(); i++) {
				Property p = (Property) properties.get(i);
				StringBuffer sb = new StringBuffer("public ");
				if (p.type == PropertyType.NUMBER) {
					sb.append("double ");
				} else if (p.type == PropertyType.STRING) {
					sb.append("String ");
				} else {
					sb.append("Object ");
				}
				sb.append(p.name);
				if (p.value != null) {
					sb.append("=").append(p.value);
				}
				sb.append(";");
				p.byteCode = CtField.make(sb.toString(), cc);
				cc.addField((CtField) p.byteCode);
			}
		}
		// Set the methods of the created class
		if (methods != null) {
			for (int i = 0; i < methods.size(); i++) {
				CMethod method = (CMethod) methods.get(i);
				String translatedCode = CodeHelper.translate(method.src);
				method.transSuccess = true;
				CtMethod methodByte = null;
				try {
					methodByte = CtMethod.make(translatedCode, cc);
					cc.addMethod(methodByte);
				} catch (Exception e) {
					throw (new SimException("CAT-S-MF-011B", "Method '"
							+ method.name
							+ "' can not be registered in the category '"
							+ cc.getName() + "'.", e));
				}
				Map methodMap = (Map) categoryMethods.get(className);
				if (methodMap == null)
					methodMap = new HashMap();
				methodMap.put(method.name, methodByte);
				categoryMethods.put(className, methodMap);
			}
		}
		// Construct a category object to store important info which is used
		// when
		// the instance is created
		Object o = null;
		try {
			o = pool.get("simulation.newmodel." + className).toClass()
					.newInstance();
			MethodUtils.invokeExactMethod(o, "setEntityType", className);
			MethodUtils.invokeExactMethod(o, "setImagePath", display
					.getImagePath());
			MethodUtils.invokeExactMethod(o, "setRelativeImagePath", display
					.getRelativeImagePath());
			MethodUtils.invokeExactMethod(o, "setDisplay", display);
			if (properties != null) {
				for (int i = 0; i < properties.size(); i++) {
					Property p = (Property) properties.get(i);
					MethodUtils.invokeExactMethod(o, "registerPropertyName", p);
				}
			}
			if (methods != null) {
				for (int i = 0; i < methods.size(); i++) {
					try {
						MethodUtils.invokeExactMethod(o, "registerMethod",
								new Object[] { methods.get(i) },
								new Class[] { CMethod.class });
					} catch (Exception e) {
						throw (new SimException("CAT-S-MF-011B",
								"Methods of the category '" + className
										+ "' can not be registered.", e));
					}
				}
			}
		} catch (Exception e) {
			throw (new SimException("CAT-S-MPF-011B",
					"Methods/Properties of the category '" + className
							+ "' can not be registered.", e));
		}
		// Store the states of this category object
		objects.put(className, o);
		// Sore the class version
		latestClassVersions.put(className, new Integer(0));
		return (Category) getCategory(className);
	}

	/**
	 * Create a new method in the specified category.
	 * 
	 * <p>
	 * This method creates the method by invoking relative methods in javassist
	 * package, see.
	 * </p>
	 * 
	 * {@link #javassist.CtClass.addMethod(CtMethod method)} and
	 * {@link #javassist.CtMethod.make(String src, CtClass declaring)}
	 * 
	 * <p>
	 * First, the category class object is retrieved from the map by the
	 * category name.
	 * </p>
	 * 
	 * <p>
	 * Second, the method is integrated into the class by trying to compile it.
	 * If the compilation process is failed, the method will not be created. If
	 * successfully, the method bytes will be saved
	 * </p>
	 * 
	 * <p>
	 * Only the method is compiled successfully, the new version is saved while
	 * the old version is removed and the new version number is recorded for
	 * further reference.
	 * </p>
	 * 
	 * @param categoryName
	 *            The category name where the created method exists
	 * @param saved
	 *            Whether this method definition should be saved
	 * @param methodName
	 *            The name of the method to be created
	 * @param inputCode
	 *            The sourceCode inputed by the user from the GUI
	 * @throws RuntimeException
	 *             When the category class is not found or When the method can
	 *             not be compiled successfully or When the method can not be
	 *             registered in the category class successfully
	 */
	public void createMethod(String categoryName, boolean saved,
			String methodName, String inputCode) throws Exception {
		// Get the latest class version
		String cName = categoryName;
		Object version = latestClassVersions.get(categoryName);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			cName += ((Integer) version).intValue();
		}
		CtClass cc = null;
		try {
			cc = pool.get("simulation.newmodel." + cName);
		} catch (NotFoundException e) {
			throw (new SimException("CAT-S-CATF-001A", "The category '"
					+ categoryName + "' is not found.", e));
		}
		cc.defrost();
		String translatedCode = CodeHelper.translate(inputCode);
		try {
			CtMethod methodByte = CtMethod.make(translatedCode, cc);
			cc.addMethod(methodByte);
			// Save the method bytes for further use
			Map methodMap = (Map) categoryMethods.get(categoryName);
			if (methodMap == null)
				methodMap = new HashMap();
			methodMap.put(methodName, methodByte);
			categoryMethods.put(categoryName, methodMap);
		} catch (Exception e) {
			throw (new SimException("CAT-S-MF-011B", "Method '" + methodName
					+ "' can not be registered in the category '"
					+ categoryName + "'.", e));
		}
		if (saved) {
			Object category = objects.get(categoryName);
			CMethod method = new CMethod(methodName, inputCode, translatedCode,
					true);

			try {
				MethodUtils.invokeExactMethod(category, "registerMethod",
						new Object[] { method }, new Class[] { CMethod.class });
			} catch (Exception e) {
				throw (new SimException("CAT-S-MF-011B",
						"Method of the category '" + categoryName
								+ "' can not be invoked.", e));
			}
		}
		// Save a new version of this dynamic class
		int v = ((Integer) version).intValue() + 1;
		latestClassVersions.put(categoryName, new Integer(v));
		// Put the new dynamic class into the class pool (change the class name)
		// And remove the older version of the dynamic class
		cc.setName("simulation.newmodel." + categoryName + v);

		getCategory(categoryName);
		// sim.util.MethodUtils.probeMethodsList(c);

	}

	/**
	 * Get the bytecode of the specified method.
	 * 
	 * @param categoryName
	 *            Name of the category object
	 * @param methodName
	 *            Method name
	 * @return Byte code of the method
	 */
	public CtMethod getMethodByte(String categoryName, String methodName) {
		Map methodMap = (Map) categoryMethods.get(categoryName);
		return (CtMethod) methodMap.get(methodName);
	}

	/**
	 * Verify the validation of the specified method definition.
	 * 
	 * @param categoryName
	 *            The category where the method is defined
	 * @param inputCode
	 *            The source code of the method
	 * @throws SimException
	 *             If the method definition is not correct.
	 */
	public void verifyMethodDef(String categoryName, String inputCode)
			throws SimException {

		// Get the latest class version
		String cName = categoryName;
		Object version = latestClassVersions.get(categoryName);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			cName += ((Integer) version).intValue();
		}
		CtClass cc = null;
		try {
			cc = pool.get("simulation.newmodel." + cName);
		} catch (NotFoundException e) {
			throw (new SimException("CAT-S-CATF-001A", "The category '"
					+ categoryName + "' is not found.", e));
		}
		cc.defrost();
		// Try to compile the method
		String translatedCode = CodeHelper.translate(inputCode);
		try {
			CtMethod.make(translatedCode, cc);
		} catch (Exception e) {
			throw (new SimException("CAT-S-MF-011C",
					"The method definition is not legal.", e));
		}

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
	 * @param categoryName
	 *            The name of the category where the method is replaced
	 * @param newMethod
	 *            The new method instance to be inserted
	 */
	public void replaceMethod(String categoryName, CMethod newMethod)
			throws Exception {
		// Get the latest class version
		String cName = categoryName;
		Object version = latestClassVersions.get(categoryName);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			cName += ((Integer) version).intValue();
		}
		CtClass cc = null;
		try {
			cc = pool.get("simulation.newmodel." + cName);
		} catch (NotFoundException e) {
			throw (new SimException("CAT-S-CATF-001A", "The category '"
					+ categoryName + "' is not found.", e));
		}
		cc.defrost();
		String translatedCode = CodeHelper.translate(newMethod.src);
		try {
			// System.out.println();
			// Remove and re-insert
			CtMethod methodByte = (CtMethod) ((Map) categoryMethods
					.get(categoryName)).get(newMethod.name);
			if (methodByte != null)
				cc.removeMethod(methodByte);
			methodByte = CtMethod.make(translatedCode, cc);
			cc.addMethod(methodByte);
			// Save the new method bytes, may override the old method bytes
			Map methodMap = (Map) categoryMethods.get(categoryName);
			if (methodMap == null)
				methodMap = new HashMap();
			methodMap.put(newMethod.name, methodByte);
			categoryMethods.put(categoryName, methodMap);
		} catch (Exception e) {
			throw (new SimException("CAT-S-MF-011B",
					"Methods can not be registered in the category '"
							+ categoryName + "'.", e));
		}
		Object category = objects.get(categoryName);
		newMethod.translatedSrc = translatedCode;
		newMethod.transSuccess = true;
		try {
			MethodUtils.invokeExactMethod(category, "updateMethod",
					new Object[] { newMethod }, new Class[] { CMethod.class });
		} catch (Exception e) {
			throw (new SimException("CAT-S-MF-011B", "Method of the category '"
					+ categoryName + "' can not be invoked.", e));
		}
		// Save a new version of this dynamic class
		int v = ((Integer) version).intValue() + 1;
		latestClassVersions.put(categoryName, new Integer(v));
		// Put the new dynamic class into the class pool (change the class name)
		// And remove the older version of the dynamic class
		cc.setName("simulation.newmodel." + categoryName + v);
	}

	/**
	 * Remove the specified method from the specified category. If no method is
	 * specified, all methods of the category will be removed..
	 * 
	 * <p>
	 * First, the latest version of that category is retrieved. Second, the
	 * method definition is removed and lastly, the method is removed from the
	 * respository of the category object (template).
	 * </p>
	 * 
	 * @param categoryName
	 *            The category name with the method definition
	 * @param method
	 *            The method to remove.
	 * @return Whether the method(s) are removed successfully
	 * @throws Exception
	 *             Exception throws during the steps.
	 */
	public boolean removeMethod(String categoryName, CMethod method)
			throws Exception {

		// Get the latest class version
		String cName = categoryName;
		Object version = latestClassVersions.get(categoryName);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			cName += ((Integer) version).intValue();
		}
		CtClass cc = null;
		try {
			cc = pool.get("simulation.newmodel." + cName);
		} catch (NotFoundException e) {
			throw (new SimException("CAT-S-CATF-001A", "The category '"
					+ categoryName + "' is not found.", e));
		}
		cc.defrost();

		// Get a copy of methods
		Category category = (Category) objects.get(categoryName);
		List methods = category.getAllMethods();

		// Remove method(s)
		try {

			// Remove a single method
			if (method != null) {

				// Remove it from the dynamic class
				CtMethod methodByte = (CtMethod) ((Map) categoryMethods
						.get(categoryName)).get(method.name);
				if (methodByte != null)
					cc.removeMethod(methodByte);

				// Remove it from the method map
				Map methodMap = (Map) categoryMethods.get(categoryName);
				if (methodMap != null)
					methodMap.remove(method.name);

				// Remove it from the methods list
				for (int i = 0; i < methods.size(); i++) {
					CMethod ms = (CMethod) methods.get(i);
					if (ms.name.equals(method.name)) {
						methods.remove(i);
					}
				}

			}
			// Remove all user-defined methods
			else {
				Set userdefinedmethods = new HashSet();
				for (int i = 0; i < methods.size(); i++) {
					userdefinedmethods.add(((CMethod) methods.get(i)).name);
				}

				Map methodMap = (Map) categoryMethods.get(categoryName);
				if (methodMap == null)
					methodMap = new HashMap(0);
				Iterator iter = methodMap.keySet().iterator();
				while (iter.hasNext()) {
					String methodName = (String) iter.next();
					// Only the user-defined methods are considered
					if (userdefinedmethods.contains(methodName)) {
						CtMethod ms = (CtMethod) methodMap.get(methodName);
						// Remove the method from the dynamic class
						if (ms != null)
							cc.removeMethod(ms);
						methodMap.remove(methodName);
					}

				}
				methods.clear();
			}

		} catch (Exception e) {
			throw (new SimException("CAT-S-MF-011B",
					"Methods can not be registered in the category '"
							+ categoryName + "'.", e));
		}

		// Update the category template
		category.removeMethods();
		category.registerMethods(methods);

		// Save a new version of this dynamic class
		int v = ((Integer) version).intValue() + 1;
		latestClassVersions.put(categoryName, new Integer(v));

		// Put the new dynamic class into the class pool (change the class name)
		// And remove the older version of the dynamic class
		cc.setName("simulation.newmodel." + categoryName + v);

		return true;

	}

	/**
	 * Return all categories and their user defined methods.
	 * 
	 * <p>
	 * <ul>
	 * There are several steps involved: <ui>1) Obtain the category name and the
	 * category object</ui> <ui>2) Obtain the method list and put them into the
	 * map</ui>
	 * </ul>
	 * </p>
	 * 
	 * @return all categories and their user defined methods.
	 */
	public Map getAllCategoryMethods() {
		Map ret = new HashMap();
		Iterator iter = objects.keySet().iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			Category category = (Category) objects.get(name);
			ret.put(name, category.getAllMethods());
		}
		return ret;
	}

	/**
	 * Check whether the given method exists in the specified category.
	 * 
	 * <p>
	 * Firstly, the dynamic category object is retrieved and then,
	 * </p>
	 * 
	 * <p>
	 * The method is checked in the category object by using the
	 * <code>MethodUtils</code>
	 * </p>
	 * 
	 * <p>
	 * Note that this method does not check whether the actual method call is
	 * correct, that is, this method does not check whether the parameters are
	 * correct.
	 * </p>
	 * 
	 * <p>
	 * Also note that the method name is case sensitiv.
	 * </p>
	 * 
	 * @param categoryName
	 *            The dynamic category
	 * @param methodName
	 *            The method name to be checked
	 * @return Whether the given method exists in the specified category true if
	 *         existing. false otherwise.
	 */
	public boolean checkMethod(String categoryName, String methodName)
			throws Exception {
		// Get the latest class version
		String cName = categoryName;
		Object version = latestClassVersions.get(categoryName);
		if (version != null
				&& (version instanceof Integer && ((Integer) version)
						.intValue() != 0)) {
			cName += ((Integer) version).intValue();
		}
		CtClass cc = null;
		try {
			cc = pool.get("simulation.newmodel." + cName);
		} catch (NotFoundException e) {
			throw (new SimException("CAT-S-CATF-001A", "The category '"
					+ categoryName + "' is not found.", e));
		}
		Class o = null;
		try {
			o = cl.loadClass("simulation.newmodel." + cName);
			if (o == null) {
				throw new RuntimeException();
			}
		} catch (Exception e1) {
			try {
				o = cc.toClass();
			} catch (Exception e) {
				throw (new SimException("CAT-S-NF-001A", "The category '"
						+ categoryName + "' can not be loaded.", e));
			}

		}

		// Check method
		return MethodUtils.checkIfMethodExist(o, methodName);
	}
}