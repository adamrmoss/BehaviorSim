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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import sim.xml.jdom.Document;
import sim.xml.jdom.Element;
import sim.xml.jdom.input.SAXBuilder;
import sim.xml.jdom.output.XMLOutputter;

/**
 * Loader of properties defined in external XML files.
 * 
 * @author Fasheng Qiu
 * 
 */
public class XMLProperties {

	private File file;
	private Document doc;
	private Map propertyCache;

	public XMLProperties(String fileName) throws IOException {
		propertyCache = new HashMap();
		file = new File(fileName);
		if (!file.exists()) {
			File tempFile = new File(file.getParentFile(), file.getName()
					+ ".tmp");
			if (tempFile.exists()) {
				System.err
						.println("WARNING: "
								+ fileName
								+ " was not found, but temp file from "
								+ "previous write operation was. Attempting automatic recovery. Please "
								+ "check file for data consistency.");
				tempFile.renameTo(file);
			} else {
				throw new FileNotFoundException(
						"XML properties file does not exist: " + fileName);
			}
		}
		if (!file.canRead())
			throw new IOException("XML properties file must be readable: "
					+ fileName);
		if (!file.canWrite())
			throw new IOException("XML properties file must be writable: "
					+ fileName);
		try {
			SAXBuilder builder = new SAXBuilder();
			// DataUnformatFilter format = new DataUnformatFilter();
			// builder.setXMLFilter(format);
			doc = builder.build(file);
		} catch (Exception e) {
			System.err.println("Error creating XML properties file: ");
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public synchronized String getProperty(String name) {
		String value = (String) propertyCache.get(name);
		if (value != null)
			return value;
		String propName[] = parsePropertyName(name);
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			element = element.getChild(propName[i]);
			if (element == null)
				return null;
		}

		value = element.getText();
		if ("".equals(value)) {
			return null;
		} else {
			value = value.trim();
			propertyCache.put(name, value);
			return value;
		}
	}

	public String[] getChildrenProperties(String parent) {
		String propName[] = parsePropertyName(parent);
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			element = element.getChild(propName[i]);
			if (element == null)
				return new String[0];
		}

		List children = element.getChildren();
		int childCount = children.size();
		String childrenNames[] = new String[childCount];
		for (int i = 0; i < childCount; i++)
			childrenNames[i] = ((Element) children.get(i)).getName();

		return childrenNames;
	}

	public synchronized void setProperty(String name, String value) {
		propertyCache.put(name, value);
		String propName[] = parsePropertyName(name);
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			if (element.getChild(propName[i]) == null)
				element.addContent(new Element(propName[i]));
			element = element.getChild(propName[i]);
		}

		element.setText(value);
		saveProperties();
	}

	public synchronized void deleteProperty(String name) {
		propertyCache.remove(name);
		String propName[] = parsePropertyName(name);
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			element = element.getChild(propName[i]);
			if (element == null)
				return;
		}

		element.removeChild(propName[propName.length - 1]);
		saveProperties();
	}

	private synchronized void saveProperties() {
		OutputStream out = null;
		boolean error = false;
		File tempFile = null;
		try {
			tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
			XMLOutputter outputter = new XMLOutputter("    ", true);
			out = new BufferedOutputStream(new FileOutputStream(tempFile));
			outputter.output(doc, out);
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				error = true;
			}
		}
		if (!error) {
			if (!file.delete()) {
				System.err.println("Error deleting property file: "
						+ file.getAbsolutePath());
				return;
			}
			for (int count = 0; !tempFile.renameTo(file) && count++ < 3; System.err
					.println("Error renaming temp file from "
							+ tempFile.getAbsolutePath() + " to "
							+ file.getAbsolutePath() + ", Attempt #" + count))
				try {
					Thread.sleep(50L);
				} catch (InterruptedException interruptedexception) {
				}

		}
	}

	private String[] parsePropertyName(String name) {
		int size = 1;
		for (int i = 0; i < name.length(); i++)
			if (name.charAt(i) == '.')
				size++;

		String propName[] = new String[size];
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		for (int i = 0; tokenizer.hasMoreTokens(); i++)
			propName[i] = tokenizer.nextToken();

		return propName;
	}
}
