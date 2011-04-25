package sim.ui.method;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import sim.core.AppEngine;

/*
 * EJE - version 2.7 - "Everyone's Java Editor"
 * 
 * Copyright (C) 2003 Claudio De Sio Cesari
 * 
 * Require JDK 1.4
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
 * Info, Questions, Suggestions & Bugs Report to eje@claudiodesio.com
 *  
 */

public class JavaInterface extends Vector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5128961386476640809L;

	protected static int rowLength = 0;

	protected static AppEngine engineRef = AppEngine.getInstance();

	public static int getRowLength() {
		return rowLength;
	}

	public class ClassWizardCellRenderer extends JLabel implements
			ListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -646012751956602348L;

		private final ImageIcon methodIcon = new ImageIcon(engineRef.jrl
				.getImage("/sim/ui/images/method.gif"));

		private final ImageIcon fieldIcon = new ImageIcon(engineRef.jrl
				.getImage("/sim/ui/images/field.gif"));

		private final ImageIcon usrMethodIcon = new ImageIcon(engineRef.jrl
				.getImage("/sim/ui/images/method_usr.gif"));

		public ClassWizardCellRenderer() {
			setLayout(new BorderLayout());
		}

		public Component getListCellRendererComponent(JList list, Object value, // value
				// to
				// display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean cellHasFocus) // the list and the cell have the focus
		{
			String s = value.toString();
			if (s.startsWith("0")) {
				setIcon(usrMethodIcon);
				s = s.substring(1);
			} else
				setIcon((s.lastIndexOf("(") != -1) ? methodIcon : fieldIcon);
			setText(s);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			// if (s.startsWith("0")) {
			// setForeground(java.awt.Color.YELLOW);
			// }
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			if (s.startsWith("0")) {
				Font font = getFont();
				font.deriveFont(Font.BOLD, 12);
				setFont(font);
			}
			setOpaque(true);
			return this;
		}
	}

	/**
	 * Sort the members
	 */
	public void sort() {
		java.util.Collections.sort(this, new java.util.Comparator() {
			public int compare(Object o1, Object o2) {
				MemberLine m1 = (MemberLine) o1;
				MemberLine m2 = (MemberLine) o2;
				return m1.getName().compareTo(m2.getName());
			}
		});
	}

}