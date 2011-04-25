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

/**
 * Used to output messages / errors to the standard output platform,
 * or to the external files. The logging function is fulfilled through
 * the apache logging library.
 * 
 * The configuration file by default is in the logging directory under
 * the SIM_HOME, which is a system property set when the system starts up.
 *
 * Also this class can be used to display messages / errors to the user.
 * The messages include the error code and detailed message.
 *
 *
 * @author Fasheng Qiu
 *
 */
import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public final class MessageUtils {

	// Dialog to show exception information
	private static ExceptionDialog dlg = new ExceptionDialog();

	/**
	 * Initialize the logging setting in the system.
	 */
	public static void initialze() {
		StringBuffer file = new StringBuffer(System.getProperty("SIM_HOME"));
		file.append(File.separator).append("config").append(File.separator);
		file.append(File.separator);
		file.append("log4j.xml");
		DOMConfigurator.configure(file.toString());
	}

	/**
	 * Debug information
	 */
	public static void debug(Object src, String method, String msg) {
		Logger.getLogger(src.getClass()).debug(
				src.getClass().getName() + "." + method + " ---" + msg);
	}

	/**
	 * Debug information
	 */
	public static void debug(Object src, String method, Exception msg) {
		Logger.getLogger(src.getClass()).debug(
				src.getClass().getName() + "." + method + " ---", msg);
		// Logger.getLogger(src.getClass()).debug(msg);
	}

	/**
	 * Info information
	 */
	public static void info(Object src, String method, String msg) {
		Logger.getLogger(src.getClass()).info(
				src.getClass().getName() + "." + method + " ---" + msg);
	}

	/**
	 * Info information
	 */
	public static void info(Object src, String method, Exception msg) {
		Logger.getLogger(src.getClass()).info(
				src.getClass().getName() + "." + method + " ---", msg);
		// Logger.getLogger(src.getClass()).info(msg);
	}

	/**
	 * Info information
	 */
	public static void error(Object src, String method, String msg) {
		Logger.getLogger(src.getClass()).error(
				src.getClass().getName() + "." + method + " ---" + msg);
	}

	/**
	 * Info information
	 */
	public static void error(Object src, String method, Exception msg) {
		Logger.getLogger(src.getClass()).error(
				src.getClass().getName() + "." + method + " ---", msg);
		// Logger.getLogger(src.getClass()).error(msg);
	}

	/**
	 * Record the debug informaiton and display it to the user
	 * 
	 * @param e
	 */
	public static void debugAndDisplay(Object src, String method, Exception e) {

		// Log the exception
		debug(src, method, e);

		// Display the exception
		displayError(e);
	}

	/**
	 * The following routines are used to display messages to the user.
	 * Different icons will be used for different types of messages -
	 * INFORMATION_MESSAGE, ERROR_MESSAGE, etc.
	 * 
	 * 
	 * 
	 * @param message
	 */
	public static void displayNormal(String message) {
		display(null, message, "Message", JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static void displayNormal(Component parent, String message) {
		display(parent, message, "Message", JOptionPane.INFORMATION_MESSAGE,
				null);
	}

	public static void displayNormal(Component parent, String message,
			String title) {
		display(parent, message, title, JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static void displayNormal(Component parent, String message,
			String title, Icon icon) {
		display(parent, message, title, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	public static void displayError(Exception e) {
		
		dlg.setException(e);
		dlg.setModal(true);
		dlg.show();
	}

	public static void displayError(String message) {
		display(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
	}

	public static void displayError(Component parent, String message) {
		display(parent, message, "Error", JOptionPane.ERROR_MESSAGE, null);
	}

	public static void displayError(Component parent, String message,
			String title) {
		display(parent, message, title, JOptionPane.ERROR_MESSAGE, null);
	}

	public static void displayError(Component parent, String message,
			String title, Icon icon) {
		display(parent, message, title, JOptionPane.ERROR_MESSAGE, icon);
	}

	public static void displayWarning(String message) {
		display(null, message, "Warning", JOptionPane.WARNING_MESSAGE, null);
	}

	public static int displayConfirm(String message) {
		return display2(null, message, "Confirm", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static int displayConfirm(Component parent, String message) {
		return display2(parent, message, "Confirm",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null);
	}

	public static int displayConfirm(Component parent, String message,
			String title) {
		return display2(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static int displayConfirm(Component parent, String message,
			String title, int optionType) {
		return display2(parent, message, title, optionType,
				JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static int displayConfirm(Component parent, String message,
			String title, int optionType, int messageType) {
		return display2(parent, message, title, optionType, messageType, null);
	}

	public static int displayConfirm(Component parent, String message,
			String title, int optionType, int messageType, Icon icon) {
		return display2(parent, message, title, optionType, messageType, icon);
	}

	private static int display2(Component parent, String message, String title,
			int optionType, int messageType, Icon icon) {
		return JOptionPane.showConfirmDialog(parent, message, title,
				optionType, messageType, icon);
	}

	private static void display(Component parent, String message, String title,
			int messageType, Icon icon) {
		sim.ui.AppStatusbar.getInstance().changeMessage(message);
		// JOptionPane.showMessageDialog(parent, message, title, messageType,
		// icon);
	}
}
