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

/**
 * The category property wrapper.
 * 
 * Each category has two fixed properties: Category Name and Category Icon
 * 
 * @author Owner
 * 
 */
public class Property {

	public String buttonText; // Use in table
	public int type; // Property type
	public String typeName = "double"; // Name of property type, "Number",
	// "String", or "Object"
	public String name; // Property name
	public Object value; // Property value
	public Object byteCode; // The compiled property object, used in dynamic

	// class manager

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param value
	 */
	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructor
	 * 
	 * @param buttonText
	 * @param type
	 * @param name
	 * @param value
	 */
	public Property(String buttonText, int type, String name, Object value) {
		this.buttonText = buttonText;
		this.type = type;
		this.name = name;
		this.value = value;
	}

	/**
	 * Return a copy of this property object
	 * 
	 * @return a copy of this property
	 */
	public Property copy() {
		Property copy = new Property(buttonText, type, name, value);
		copy.byteCode = byteCode;
		copy.typeName = typeName;
		return copy;
	}

	/**
	 * Return property name and value
	 * 
	 * @return property name and value
	 */
	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
