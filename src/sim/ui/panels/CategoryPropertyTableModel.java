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

package sim.ui.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import sim.model.entity.Property;
import sim.model.entity.PropertyType;

/**
 * <p>
 * Title: Category Property Table Model
 * </p>
 * 
 * <p>
 * Description: Table model of the category
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Fasheng
 * @version 1.0
 */
public class CategoryPropertyTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1420365377415313429L;
	// A row represents a record
	private List rows = new ArrayList();

	// Obtain the specified row
	public Property getProperty(int rowIndex) {
		return (Property) rows.get(rowIndex);
	}

	// Obtain all the rows present in the table
	public List getRows() {
		return new ArrayList(rows);
	}

	/**
	 * Add A record into the table
	 * 
	 * @param name
	 *            Property Name
	 * @param type
	 *            Property type
	 * @param defaultValue
	 *            Property default value
	 */
	public void addRow(String name, int type, Object defaultValue) {
		Property temp = new Property("FALSE", type, name, defaultValue);
		temp.typeName = PropertyType.types[type];
		if (rows.add(temp)) {
			int k = rows.size() - 1;
			fireTableRowsInserted(k, k);
		} else {
			throw new RuntimeException("Failed to add a property!");
		}

	}

	/**
	 * Remove a property with the name
	 * 
	 * @param name
	 *            property Name
	 */
	public void removeRow(String name) {
		for (int i = 0; i < getRowCount(); i++) {
			if (name.equals(getValueAt(i, 1))) {
				rows.remove(i);
				fireTableRowsDeleted(i, i);
			}
		}
	}

	/**
	 * Remove all rows
	 */
	public void removeAll() {
		int rowCount = getRowCount();
		if (rowCount > 0) {
			rows.clear();
			super.fireTableRowsDeleted(0, rowCount - 1);
		}
	}

	/**
	 * Remove a specified row
	 * 
	 * @param name
	 *            property Name
	 */
	public void removeRow(int row) {
		for (int i = 0; i < getRowCount(); i++) {
			if (row == i) {
				rows.remove(i);
				fireTableRowsDeleted(i, i);
			}
		}
	}

	/**
	 * Returns the number of columns in the model.
	 * 
	 * @return the number of columns in the model
	 * @todo Implement this javax.swing.table.TableModel method
	 */
	public int getColumnCount() {
		return 4;
	}

	// Get the column name
	public String getColumnName(int index) {
		switch (index) {
		case 0:
			return "Index";
		case 1:
			return "Name";
		case 2:
			return "Type";
		case 3:
			return "Value";
		default:
			return "";
		}
	}

	public Class getColumnClass(int index) {
		// if (index == 1){
		// return Double.class;
		// }
		return String.class;
	}

	/**
	 * Returns the number of rows in the model.
	 * 
	 * @return the number of rows in the model
	 * @todo Implement this javax.swing.table.TableModel method
	 */
	public int getRowCount() {
		return rows.size();
	}

	/**
	 * This empty implementation is provided so users don't have to implement
	 * this method if their data model is not editable.
	 * 
	 * @param aValue
	 *            value to assign to cell
	 * @param rowIndex
	 *            row of cell
	 * @param columnIndex
	 *            column of cell
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex < 0) {
			return;
		}
		Property pw = (Property) rows.get(rowIndex);
		if (pw != null) {
			switch (columnIndex) {
			case 0:
				pw.buttonText = ((String) aValue);
				break;
			case 1:
				pw.name = ((String) aValue);
				break;
			case 2:
				pw.type = Integer.parseInt((String) aValue);
				break;
			case 3:
				pw.value = ((String) aValue);
				break;
			}
			super.fireTableRowsUpdated(rowIndex, columnIndex);
		}
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * 
	 * @param rowIndex
	 *            the row whose value is to be queried
	 * @param columnIndex
	 *            the column whose value is to be queried
	 * @return the value Object at the specified cell
	 * @todo Implement this javax.swing.table.TableModel method
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0)
			return null;
		String result;
		switch (columnIndex) {
		case 0:
			result = ((Property) rows.get(rowIndex)).buttonText;
			break;
		case 1:
			result = ((Property) rows.get(rowIndex)).name;
			break;
		case 2:
			result = PropertyType.types[((Property) rows.get(rowIndex)).type];
			break;
		case 3:
			result = ((Property) rows.get(rowIndex)).value + "";
			break;
		default:
			result = null;
		}
		return result;
	}

	/**
	 * 
	 * Let the value and description column be editable
	 * 
	 */
	public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return true;
		}
		return false;
	}

}
