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

import javassist.CtMethod;

/**
 * A wrapper for the user defined methods of the dynamic category class.
 * 
 * <p>
 * Basically, the methods are belonged to the category class.
 * </p>
 * 
 * @author Owner
 * 
 */
public class CMethod {

	// Method name, should be unique for a category
	public String name;

	// Parameter types
	public String[] paraTypes;

	// Return type
	public String returnType;

	// The method code input by the user
	public String src;

	// The method source code after the translation
	public String translatedSrc;

	// The method bytecode
	public CtMethod bytecode;

	// Whether the method source code is translated successfully
	public boolean transSuccess = false;

	public CMethod copy() {
		CMethod method = new CMethod();
		method.name = name;
		method.src = src;
		method.translatedSrc = translatedSrc;
		method.paraTypes = paraTypes;
		method.returnType = returnType;
		method.transSuccess = transSuccess;
		method.bytecode = bytecode;
		return method;
	}

	public CMethod() {
	}

	public CMethod(String name, String input) {
		this.name = name;
		this.src = input;
		this.transSuccess = false;
	}

	public CMethod(String name, String input, String translated,
			boolean transSuccess) {
		this.name = name;
		this.src = input;
		this.translatedSrc = translated;
		this.transSuccess = transSuccess;
	}
}
