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

package sim.model.behavior;

/**
 * <p>
 * Title: Crayfish simulation application
 * </p>
 * 
 * <p>
 * Description: Simulation of the crayfish behavior
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Pavel
 * @version 1.0
 */
public class BehaviorPosition extends Object {

	private int row;
	private int column;
	private static BehaviorPosition positions[][] = null;

	public static final int NETWORK_SQUARES = 5;

	static {
		positions = new BehaviorPosition[NETWORK_SQUARES][NETWORK_SQUARES];
		for (int i = 0; i < NETWORK_SQUARES; i++)
			for (int j = 0; j < NETWORK_SQUARES; j++)
				positions[i][j] = new BehaviorPosition(i, j);
	}

	public BehaviorPosition(int r, int c) {
		row = r;
		column = c;
	}

	public BehaviorPosition copy() {
		return new BehaviorPosition(row, column);
	}

	// From Object class
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else {
			try {
				return ((BehaviorPosition) obj).row == this.row
						&& ((BehaviorPosition) obj).column == this.column;
			} catch (Exception e) {
				return false;
			}
		}
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	static public BehaviorPosition getPosition(int r, int c) {
		return positions[r][c];
	}
}
