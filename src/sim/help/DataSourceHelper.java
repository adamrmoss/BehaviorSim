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

package sim.help;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JOptionPane;


import sim.core.dclass.ResourceLoader;

public class DataSourceHelper implements ActionListener {


	/** Help panel */
	private HelpPanel parent = null;

	/** Root node id */
	public final static int NONE = 0;

	/** Maximum number of pages */
	public final static int COUNT = 26;

	/** Document ids */
	public final static int OVERVIEW = 1;
	public final static int CONCEPTS = 2;
	public final static int APP = 3;
	public final static int MODELFILE = 4;
	public final static int STARTED = 5;
	public final static int STEPS = 6;
	public final static int CREATEAPP = 7;
	public final static int NEWAPP = 8;
	public final static int OLDAPP = 9;
	public final static int CREATECATEGORY = 10;
	public final static int CATEGORYBASIC = 11;
	public final static int CATEGORYMETHOD = 12;
	public final static int CATEGORYBASICCOPYPASTE = 13;
	public final static int CREATEENTITY = 14;
	public final static int NEWENTITY = 15;
	public final static int UPDATEENTITY = 16;
	public final static int CREATEDYNAMICS = 17;
	public final static int CREATEGENERAL = 18;
	public final static int CREATEBN = 19;
	public final static int NEWBEHAVIOR = 20;
	public final static int NEWBEHAIVORBYCOPY = 21;
	public final static int BEHAIVORTASKQUEUE = 22;
	public final static int COEFF = 23;
	public final static int SIMULATION = 24;
	public final static int APIS = 25;
	public final static int APIGUI = 26;
	public final static int METHODGUI = 27;
	public final static int PRACTICE = 28;
	public final static int FAQ = 29;

	/** Initialize the data */
	public final static Map dataMap = new HashMap();
	static {
		dataMap.put(new Integer(OVERVIEW), "overview.htm");
		dataMap.put(new Integer(CONCEPTS), "concepts.htm");
		dataMap.put(new Integer(APP), "concepts_app.htm");
		dataMap.put(new Integer(MODELFILE), "concepts_modelfile.htm");
		dataMap.put(new Integer(STARTED), "started.htm");
		dataMap.put(new Integer(STEPS), "started_steps.htm");
		dataMap.put(new Integer(CREATEAPP), "started_createapp.htm");
		dataMap.put(new Integer(NEWAPP), "started_newapp.htm");
		dataMap.put(new Integer(OLDAPP), "started_existingapp.htm");
		dataMap.put(new Integer(CREATECATEGORY), "started_createcategory.htm");
		dataMap.put(new Integer(CATEGORYBASIC), "started_categorybasic.htm");
		dataMap.put(new Integer(CATEGORYMETHOD), "started_categorymethod.htm");
		dataMap.put(new Integer(CATEGORYBASICCOPYPASTE),
				"started_categorybasiccopypaste.htm");
		dataMap.put(new Integer(CREATEENTITY), "started_createentity.htm");
		dataMap.put(new Integer(NEWENTITY), "started_newentity.htm");
		dataMap.put(new Integer(UPDATEENTITY), "started_updateentity.htm");
		dataMap.put(new Integer(CREATEDYNAMICS), "started_dynamics.htm");
		dataMap.put(new Integer(CREATEGENERAL), "started_generaldynamics.htm");
		dataMap.put(new Integer(CREATEBN), "started_createbn.htm");
		dataMap.put(new Integer(NEWBEHAVIOR), "started_newbehavior.htm");
		dataMap.put(new Integer(NEWBEHAIVORBYCOPY),
				"started_newbehaviorcopy.htm");
		dataMap.put(new Integer(BEHAIVORTASKQUEUE),
				"started_newbehaviorbytaskqueue.htm");
		dataMap.put(new Integer(COEFF), "started_coeff.htm");
		dataMap.put(new Integer(SIMULATION), "started_simulation.htm");
		dataMap.put(new Integer(APIS), "api.htm");
		dataMap.put(new Integer(APIGUI), "api_gui.htm");
		dataMap.put(new Integer(METHODGUI), "api_method.htm");
		dataMap.put(new Integer(PRACTICE), "practice.htm");
		dataMap.put(new Integer(FAQ), "faq.htm");
	}

	/**
	 * Load a page with the specified id name
	 * 
	 * @param file
	 *            File to load
	 * @return The loaded page
	 */
	public static java.net.URL loadPage(String file) {
		java.net.URL helpURL = ResourceLoader.getResource(file,
				DataSourceHelper.class);
		if (helpURL != null) {
			return helpURL;
		} else {
			// System.err.println("Couldn't find file: " + file);
		}
		return null;
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Help panel
	 */
	public DataSourceHelper(HelpPanel parent) {
		this.parent = parent;
	}

	/**
	 * Click event
	 */
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("prev")) {
			int curID = parent.getCurrentPanelID();
			if (curID > NONE) {
				parent.setCurrentPanelID(curID - 1);
				parent.displayAtTree(curID - 1);
			}
			return;
		}
		if (action.equals("next")) {
			int curID = parent.getCurrentPanelID();
			if (curID < FAQ) {
				parent.setCurrentPanelID(curID + 1);
				parent.displayAtTree(curID + 1);
			}
			return;
		}
		if (action.equals("home")) {
			int curID = parent.getCurrentPanelID();
			if (curID != OVERVIEW) {
				parent.setCurrentPanelID(OVERVIEW);
				parent.displayAtTree(OVERVIEW);
			}
			return;
		}
		if (action.equals("display")) {
			int curID = parent.getCurrentPanelID();
			parent.displayAtTree(curID);
			return;
		}
		if (action.equals("print")) {
			// sim.core.AppWorker worker = new sim.core.AppWorker(){
			// public void execute() {
			// try{
			// final PipedReader in = new PipedReader();
			// final PipedWriter out = new PipedWriter(in);
			// OutputStreamWriter outW = new OutputStreamWriter(
			// System.out
			// );
			// int curID = parent.getCurrentPanelID();
			// BaseHelpPanel bhp = (BaseHelpPanel)parent.panelMap.get(new
			// Integer(curID));
			// bhp.getHTMLStream(outW);
			// printHTML(parent, in);
			// } catch(Exception ex){ex.printStackTrace();}
			// }
			// };
			// worker.start();
			showFutureWork();
			return;

		}
		if (action.equals("left-expand")) {
			parent.expandLeftTree();
			return;
		}
		if (action.equals("right-expand")) {
			parent.expandRightTree();
			return;
		}
	}

	/**
	 * Search text in the topics
	 * 
	 * @param text
	 *            Text to search
	 */
	public void searchText(String text) {
		showFutureWork();
	}

	private void showFutureWork() {
		JOptionPane.showMessageDialog(parent, "Print will be supported later.");
		sim.util.MessageUtils.info(parent, "print action",
				"Print will be supported later.");
	}

	public static boolean printHTML(Component c, Reader reader) {
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		PrintService printService[] = PrintServiceLookup.lookupPrintServices(
				flavor, pras);
		PrintService defaultService = PrintServiceLookup
				.lookupDefaultPrintService();
		PrintService service = null;
		try {
			service = ServiceUI.printDialog(null, 200, 200, printService,
					defaultService, flavor, pras);
		} catch (IllegalArgumentException exception) {
			JOptionPane.showMessageDialog(c, exception);
			return false;
		}
		if (service != null) {
			try {
				DocPrintJob job = service.createPrintJob();
				DocAttributeSet das = new HashDocAttributeSet();
				Doc doc = new SimpleDoc(reader, DocFlavor.READER.TEXT_HTML, das);
				job.print(doc, pras);
			} catch (Exception pe) {
				pe.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
