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

package sim.util;

import java.io.PrintWriter;

/**
 * Exception wrapper for exceptions from <code>DynamicManager</code>,
 * <code>AppEngine</code>, etc.
 * 
 * @author Fasheng Qiu
 * 
 */
public class SimException extends Exception {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -1167059946186510214L;

	/** The exception code for maintain and debug */
	private String code;

	/** Brief information for this exception */
	private String briefInfo;

	/** The exception stack for the detailed stack */
	private Exception stack;

	/** Constructor */
	public SimException(String code, String briefInfo, Exception exception) {
		this.code = code;
		this.briefInfo = briefInfo;
		this.stack = exception;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the briefInfo
	 */
	public String getBriefInfo() {
		return briefInfo;
	}

	/**
	 * @param briefInfo
	 *            the briefInfo to set
	 */
	public void setBriefInfo(String briefInfo) {
		this.briefInfo = briefInfo;
	}

	/**
	 * @return the stack
	 */
	public Exception getStack() {
		return stack;
	}

	/**
	 * @param stack
	 *            the stack to set
	 */
	public void setStack(Exception stack) {
		this.stack = stack;
	}

	/**
	 * Print the exception stack
	 */
	public void printStackTrace() {
		this.stack.printStackTrace();
	}

	/**
	 * Print to the specified destination
	 */
	public void printStackTrace(PrintWriter s) {
		this.stack.printStackTrace(s);
	}

}
