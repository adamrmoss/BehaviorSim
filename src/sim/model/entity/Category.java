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

package sim.model.entity;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.beanutils.BeanUtils;

import sim.util.MessageUtils;
import sim.util.MethodUtils;

/**
 * The entity category wrappers. It is the supper class of all categories in the
 * system.
 * 
 * <p>
 * Each category has two fixed properties: Category Name and Category Icon
 * </p>
 * 
 * <p>
 * The category name should be unique!!!
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class Category extends Entity {

	// Category name
	private String name;

	// Category properties, used in the navigation tree panel
	// The value of the properties can be changed by the user
	private List properties = null;

	// Category methods
	private List methods = null;

	/**
	 * A deep copy of this category object to the specified category object.
	 * 
	 * <p>
	 * The purpose of this method is to provide a state copy for the cooperative
	 * action selection mechanism, where each action will produce a "new" state
	 * of the category. To avoid the "new" state affects the original state, a
	 * copied category is used to perform the action.
	 * </p>
	 * 
	 * <p>
	 * <strong>FIXME</strong> DO WE NEED TO COPY methods ? DOES THE EXCUTED
	 * ACTION AT EACH TIME STEP AFFECT THESE TWO PROPERTIES?
	 * </p>
	 * 
	 * @param category
	 *            The category who gets the states of this category
	 */
	public void copyTo(Category category) {
		if (category == null)
			return;
		super.copyTo(category);
		category.name = this.name;

		// Default: the methods are not copied, suppose that
		// it will never be changed after the execution of all behaviors.
		category.properties.clear();
		category.properties.addAll(this.getProperties());
		// category.methods.clear();
		// category.methods.addAll(this.properties);
	}

	/**
	 * Initialize the category and register the category name and iconPath
	 * 
	 */
	public Category() {
		super();
		properties = new ArrayList();
		methods = new ArrayList();
	}

	/**
	 * Register a property for further referenced
	 * 
	 * @param p
	 *            The category property
	 */
	public void registerPropertyName(Property p) {
		properties.add(p);
	}

	/**
	 * Register a bunch of properties for further referenced
	 * 
	 * @param p
	 *            The category property list
	 */
	public void registerProperties(List p) {
		if (p != null && p.size() > 0) {
			properties.addAll(p);
		}
	}

	/**
	 * Obtain all properties (not populated with property values)
	 * 
	 * @return All properties
	 */
	public List getProperties() {
		List copy = new ArrayList(properties.size());
		for (int i = 0; i < properties.size(); i++) {
			copy.add(((Property) properties.get(i)).copy());
		}
		return copy;
	}

	/**
	 * <p>
	 * Determine the type of the specified property.
	 * </p>
	 * <p>
	 * First, query the property list and find that property.
	 * </p>
	 * <p>
	 * Then, return the type of the property.
	 * </p>
	 * 
	 * <p>
	 * If no property of the specified name is found, then -1 is returned to
	 * indicate that the specified name does not exist as a category property.
	 * </p>
	 * 
	 * @param name
	 *            The property name
	 * @return The type of the property
	 */
	public int getPropertyType(String propertyName) {
		for (int i = 0; i < properties.size(); i++) {
			Property p = (Property) properties.get(i);
			if (p.name.equals(propertyName)) {
				return p.type;
			}
		}
		return -1;
	}

	/**
	 * Return all properties, including property name, property type and
	 * property value.
	 * 
	 * @return All registered properties
	 */
	public List getAllProperties() {
		List p = new ArrayList(properties.size());
		for (int i = 0; i < properties.size(); i++) {
			Property temp = ((Property) properties.get(i)).copy();
			try {
				temp.value = MethodUtils.invokeExactField(this, temp.name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			p.add(temp);
		}
		return p;
	}

	/**
	 * Return a copy of all properties, values of which are those defined in
	 * CategoryDefinePanel.
	 * 
	 * @return A copy of all properties.
	 */
	public List getOriginalProperties() {
		return getProperties();

	}

	/**
	 * System called initialization.It is called before the simulation
	 * calculation. It is supposed to be only used internally.
	 * 
	 * <p>
	 * Here the values of user-defined properties are reset to the initial
	 * values.
	 * </p>
	 */
	public void _initInternal() {
		for (int i = 0; i < properties.size(); i++) {
			Property property = (Property) properties.get(i);
			try {
				MethodUtils.invokeSetField(this, property.name, property.value);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * <p>
	 * Invoke the category and set the specified property to its new value.
	 * <p>
	 * 
	 * <p>
	 * This method uses java reflect to set the property value. It assumes that
	 * except the properties "name", all the other properties are declared in
	 * the subclass, and thus they can be set by the direct field access.
	 * <p>
	 * 
	 * <p>
	 * This method also set the value by different types, see
	 * {@link #getPropertyType(String propertyName)}.
	 * </p>
	 * 
	 * @param propertyName
	 *            The property name to be set
	 * @param propertyValue
	 *            The property value
	 * @throws Exception
	 *             If the property value can not be set properly.
	 */
	public void updateProperty(String propertyName, String propertyValue)
			throws Exception {
		if (propertyName.equals("name")) {
			this.setEntityType(propertyValue);
		} else {
			Object propertyV = propertyValue;
			int type = this.getPropertyType(propertyName);
			if (type == PropertyType.NUMBER) {
				try {
					propertyV = new Double(propertyValue);
				} catch (Exception e) {
					MessageUtils.debug(this, "updateProperty", e);
					MessageUtils
							.displayError("The property value: "
									+ propertyV
									+ " is incorrect according to the pre-set property type!");
					return;
				}
			}
			MethodUtils.invokeSetField(this, propertyName, propertyV);
		}
	}

	/**
	 * Update the initial value for the specified property. The initial value is
	 * reset before each simulation iteration.
	 * 
	 * @param propertyName
	 *            The name of the property to set the initial value
	 * @param propertyValue
	 *            The new initial value of the property
	 */
	public void updatePropertyInitial(String propertyName, Object propertyValue) {
		for (int i = 0; i < properties.size(); i++) {
			Property property = (Property) properties.get(i);
			if (property.name.equals(propertyName))
				property.value = propertyValue;
		}
	}

	/**
	 * Register a method for further reference.
	 * 
	 * <p>
	 * This method can only be called after the method is compiled successfully.
	 * It is generally called by the <code>DynamicManager</code> {@link #sim.configure.dclass.DynamicManager}.
	 * </p>
	 * 
	 * @param m
	 *            The method to register
	 * @throws IllegalStateException
	 *             When the method to add is not compiled successfully
	 */
	public void registerMethod(CMethod m) {
		if (m != null && m.transSuccess) {
			this.methods.add(m);
			return;
		}
		throw new IllegalStateException("The method '" + m.name
				+ "' is not compiled successfully!");
	}

	/**
	 * Update the method definition. The name of the old method and the new
	 * method is the same.
	 * 
	 * @param newMethod
	 *            The new method instance
	 */
	public void updateMethod(CMethod newMethod) {
		if (newMethod == null)
			return;
		for (int i = 0; i < methods.size(); i++) {
			if (((CMethod) methods.get(i)).name.equals(newMethod.name.trim())) {
				methods.set(i, newMethod);
				break;
			}
		}
	}

	/**
	 * Return all user-defined methods for further reference
	 * 
	 * @return all user-defined methods for further reference
	 */
	public List getAllMethods() {
		return new ArrayList(this.methods);
	}

	/**
	 * Register a bunch of methods for this category
	 * 
	 * @param methods
	 *            The method list to register
	 */
	public void registerMethods(List methods) {
		if (methods == null)
			return;
		for (int i = 0; i < methods.size(); i++) {
			this.registerMethod((CMethod) methods.get(i));
		}
	}

	/**
	 * Remove all property definitions
	 */
	public void removeProperties() {
		this.properties.clear();
	}

	/**
	 * Remove all method definitions
	 */
	public void removeMethods() {
		this.methods.clear();
	}

	/**
	 * The initialization method, should be called before the category can
	 * function
	 * 
	 * @param c
	 *            The component used to load the image, usually it is
	 *            SystemEditorPanel
	 * @param name
	 *            The name of the category
	 * @param iconPath
	 *            The icon image path of the category
	 */
	public void init(JComponent c, String name, String iconPath) {
		/** Set the name and icon path */
		this.name = name;
		/** Initialize internal states if any */
		init();
		/** Prepare entity image */
		prepareEntityImage(c, iconPath);
	}

	/**
	 * Prepare the entity image
	 */
	protected void prepareEntityImage(JComponent c, String path) {
		// Prepare the entity image
		super.prepareEntityImage(c, path);
		// Adjust the image
		ImageFilter filter = new WhiteFilter();
		FilteredImageSource filteredImage = new FilteredImageSource(display
				.getImage().getSource(), filter);
		Image image = Toolkit.getDefaultToolkit().createImage(filteredImage);
		MediaTracker tracker = new MediaTracker(c);
		try {
			tracker.addImage(image, 0);
			tracker.waitForAll();
		} catch (InterruptedException e) {
		}
		display.setImage(image);
		display.setImagePath(path);
	}

	/**
	 * To setup the current action to be executed and/or prepare other
	 * pre-requirements (such as setting variable).
	 * 
	 * <p>
	 * The user should implement this method to implement the choice mechanism
	 * of current action to be executed.
	 * </p>
	 * 
	 * <p>
	 * An example would be: if (some conditions) {
	 * setAction(getAction("categoryName", "actionName")); } else if (...) { ...
	 * ... ... ... } where categoryName is the name of the category when it is
	 * defined and actionName is the name of the action when it is defined.
	 * </p>
	 */
	protected void preprocess() {
	}

	/**
	 * Copy the internal states from this entity to the specified entity.
	 * 
	 * <p>
	 * All fields and user-defined properties are copied. The fields are copied
	 * using <code>org.apache.commons.beanutils.BeanUtils.</code> and the
	 * user-defined properties are copied using <code>MethodUtils</code>
	 * </p>
	 * 
	 * @param toEntity
	 *            The entity which gets the states of this entity
	 * @throws Exception
	 *             if copy is not successfully.
	 */
	public void copyState(Entity toEntity) throws Exception {
		try {
			BeanUtils.copyProperties(toEntity, this);
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.debug(this, "copyState", e);
			throw new RuntimeException(
					"The internal states of the two-version entities can not be copied from one to the other.");
		}
		// The initial values of user-defined properties
		((Category) toEntity).properties.clear();
		((Category) toEntity).registerProperties(getProperties());
		// Set the current value of each property on the target entity
		for (int i = 0; i < properties.size(); i++) {
			Property temp = (Property) properties.get(i);
			try {
				Object value = MethodUtils.invokeExactField(this, temp.name);
				MethodUtils.invokeSetField(toEntity, temp.name, value);
			} catch (NoSuchFieldException e) {
				;// MessageUtils.debug(this, "copyState", e);
			}
		}
	}

	/**
	 * return type of this entity
	 */
	public String getEntityType() {
		return this.name;
	}

	/**
	 * Set the name of this entity
	 * 
	 * @param name
	 *            The name of this entity
	 */
	public void setEntityType(String name) {
		this.name = name;
	}

}