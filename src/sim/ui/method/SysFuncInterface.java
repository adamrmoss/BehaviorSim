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

package sim.ui.method;

import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;
import sim.core.dclass.ResourceLoader;
import sim.model.entity.CMethod;
import sim.model.entity.Category;
import sim.model.entity.Property;
import sim.model.entity.SystemFunction;

/**
 * Define the interface of the system function class with methods. ALSO the
 * defined interface is used to display user-defined methods and variables.
 * 
 * The methods of system function is obtained through reflection, while the
 * user-defined variables and methods are obtained as two parameters.
 */

public class SysFuncInterface extends JavaInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7784866945761838710L;

	private static int rowLength = 0;

	private static final String sysFunctionName = SystemFunction.NAME;

	public SysFuncInterface(Category c) throws ClassNotFoundException {

		// Remove methods from Object
		Class javaObjectClass = Object.class;
		Set objMethods = new HashSet();
		Method[] methods = javaObjectClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			objMethods.add(methods[i].getName());
		}
		
		// Populate methods from SystemFunction.
		Class objectClass = Class
				.forName(sysFunctionName, false, new java.net.URLClassLoader(
						new java.net.URL[] { ResourceLoader.getResource(sysFunctionName,
								SysFuncInterface.class) }));
		methods = objectClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			String name = methods[i].getName();
			if (objMethods.contains(name))
				continue;
			Class[] classParameters = methods[i].getParameterTypes();
			String[] stringClassParameters = new String[classParameters.length];
			for (int j = 0; j < classParameters.length; ++j) {
				stringClassParameters[j] = classParameters[j].toString();
			}
			String methodReturnType = methods[i].getReturnType().toString();
			MemberLine memberLine = new MemberLine(name, stringClassParameters,
					methodReturnType);
			this.addElement(memberLine);
			int memberLength = memberLine.toString().length();
			if (memberLength > rowLength) {
				rowLength = memberLength;
			}
		}

		// Populate additional methods from Entity
		MemberLine memberLine = new MemberLine("getDirection", new String[] {},
				"double");
		this.addElement(memberLine);
		int memberLength = memberLine.toString().length();
		if (memberLength > rowLength) {
			rowLength = memberLength;
		}
		memberLine = new MemberLine("getSpeed", new String[] {}, "double");
		this.addElement(memberLine);
		memberLength = memberLine.toString().length();
		if (memberLength > rowLength) {
			rowLength = memberLength;
		}
		memberLine = new MemberLine("getPositionX", new String[] {}, "double");
		this.addElement(memberLine);
		memberLength = memberLine.toString().length();
		if (memberLength > rowLength) {
			rowLength = memberLength;
		}
		memberLine = new MemberLine("getPositionY", new String[] {}, "double");
		this.addElement(memberLine);
		memberLength = memberLine.toString().length();
		if (memberLength > rowLength) {
			rowLength = memberLength;
		}
		memberLine = new MemberLine("getMyId", new String[] {}, "int");
		this.addElement(memberLine);
		memberLength = memberLine.toString().length();
		if (memberLength > rowLength) {
			rowLength = memberLength;
		}
		memberLine = new MemberLine("getTime", new String[] {}, "int");
		this.addElement(memberLine);
		memberLength = memberLine.toString().length();
		if (memberLength > rowLength) {
			rowLength = memberLength;
		}

		super.sort();

		if (c != null) {
			// Add user-defined functions
			List usrmethods = c.getAllMethods();
			java.util.Collections.sort(usrmethods, new java.util.Comparator() {
				public int compare(Object o1, Object o2) {
					CMethod m1 = (CMethod) o1;
					CMethod m2 = (CMethod) o2;
					return m1.name.compareTo(m2.name);
				}
			});
			for (int i = 0; i < usrmethods.size(); i++) {
				CMethod m = (CMethod) usrmethods.get(i);
				// The method is lack of necessary information, check the
				// category for the definition
				if (m.paraTypes == null || m.returnType == null) {
					CtMethod method = engineRef.appManager.currentApp.dm
							.getMethodByte(c.getEntityType(), m.name);

					try {
						CtClass[] types = method.getParameterTypes();
						String[] typeInfo = new String[types.length];
						for (int j = 0; j < types.length; ++j) {
							typeInfo[j] = types[j].getSimpleName();
						}
						m.paraTypes = typeInfo;
					} catch (Exception e) {
					}

					try {
						CtClass t = method.getReturnType();
						m.returnType = t.getSimpleName();
					} catch (Exception e) {
					}
				}
				// "0" is only used to identify the user-defined methods
				memberLine = new MemberLine("0" + m.name, m.paraTypes,
						m.returnType);
				this.addElement(memberLine);
				memberLength = memberLine.toString().length();
				if (memberLength > rowLength) {
					rowLength = memberLength;
				}
			}

			// Add user-defined variables
			List properties = c.getAllProperties();
			java.util.Collections.sort(properties, new java.util.Comparator() {
				public int compare(Object o1, Object o2) {
					Property m1 = (Property) o1;
					Property m2 = (Property) o2;
					return m1.name.compareTo(m2.name);
				}
			});
			for (int i = 0; i < properties.size(); i++) {
				Property p = (Property) properties.get(i);
				memberLine = new MemberLine(p.name, p.typeName);
				this.addElement(memberLine);
				memberLength = memberLine.toString().length();
				if (memberLength > rowLength) {
					rowLength = memberLength;
				}
			}
		}

		this.addElement("<<no member>>");
	}
}