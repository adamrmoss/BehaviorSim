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
 * The type of the category property. Currently, BehaviorSim only supports
 * "Number", "String" and "Object".
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public final class PropertyType {

	/** Type strings */
	public final static String[] types = { "Number", "String", "Object" };

	/** Default values for these types */
	public final static String[] defaultValues = { "0.0", "\" \"", "null" };

	/** Number type, the java type is "double" */
	public final static int NUMBER = 0;

	/** String type, the java type is "String" */
	public final static int STRING = 1;

	/** Object type, the java type is "Object" */
	public final static int OBJECT = 2;

	/** Error type */
	public static int ERROR = 3;

}
