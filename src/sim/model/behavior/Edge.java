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

import java.awt.Color;

/**
 * <p>
 * Title: Behavior network edges. Each edge describes the fromBehavior and
 * toBehavior, and the corresponding position respectively.
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */
public class Edge {

	/** Inhibition of the 'from' behavior to the 'to' behavior */
	private double inhibitionFT = 0.0;

	/** Inhibition of the 'to' behavior to the 'from' behavior */
	private double inhibitionTF = 0.0;

	/** The 'from' behavior */
	private Behavior from = null;

	/** The 'to' behavior */
	private Behavior to = null;

	/** Position of the 'from' behavior */
	private BehaviorPosition fromP = null;

	/** Position of the 'to' behavior */
	private BehaviorPosition toP = null;

	/** Color of the edge, used in behavior network rendering */
	private Color color = Color.BLACK;

	public Edge(Behavior fb, BehaviorPosition fP, Behavior tb,
			BehaviorPosition tP) {
		from = fb;
		fromP = fP;
		to = tb;
		toP = tP;
	}

	public Edge copy(boolean newBehaviorId) {
		Edge e = new Edge(from.copy(newBehaviorId), fromP.copy(), to
				.copy(newBehaviorId), toP.copy());
		return e;
	}

	public void setInhibitionFT(double ft) {
		inhibitionFT = ft;
	}

	public double inhibitionFT() {
		return inhibitionFT;
	}

	public void setInhibitionTF(double tf) {
		inhibitionTF = tf;
	}

	public double inhibitionTF() {
		return inhibitionTF;
	}

	public void setColor(Color c) {
		color = c;
	}

	public Color color() {
		return color;
	}

	public Behavior fromB() {
		return from;
	}

	public Behavior toB() {
		return to;
	}

	public BehaviorPosition fromP() {
		return fromP;
	}

	public BehaviorPosition toP() {
		return toP;
	}

	public void setToP(BehaviorPosition t) {
		toP = t;
	}

	public void setFromP(BehaviorPosition f) {
		fromP = f;
	}

	public boolean isVisible() {
		return (inhibitionFT != 0 || inhibitionTF != 0);
	}

}
