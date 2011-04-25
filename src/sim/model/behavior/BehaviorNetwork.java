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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sim.core.AppEngine;
import sim.core.ConfigParameters;
import sim.model.entity.BNCategory;

/**
 * Behavior network for autonomous agents. It is the central
 * location for behavior information and the network configuration
 * including coefficients or weights.
 * 
 * @author Pavel, Fasheng Qiu
 * @version 1.0
 */

public class BehaviorNetwork {

	/** Behavior -> Behavior Position (in the network) */
	private LinkedHashMap map = new LinkedHashMap(); // maps behavior to its
	// position

	/** List of edges */
	private List list = new ArrayList(); // list of edges

	/** ID list of added behaviors */
	private List behaviorIds = new ArrayList(); // list of behavior ids

	/** Minimum value */
	private static final double BEHAVIORTHRESHOLD = -1.0;// 4.0;
	private static final double BEHAVIORSTRENGTHTHRESHOLD = 0.0;

	/** Constants */
	public static final int NODYNAMICS = -1;
	public static final int MUTUAL = 0;
	public static final int COOPERATIVE = 1;
	public static final int DYNAMICS = 2;

	/** Whether the weights are constant or dynamically constructed */
	private boolean dynamic = false;
	/** Code which is used to construct the weights/coefficients dynamically */
	private String dynamicStr = null;
	/** The name of method call which contains the code to construct the table */
	private String dynamicStrMethodName = null;

	/**
	 * Default constructor
	 */
	public BehaviorNetwork() {

	}

	/**
	 * 
	 * Return a copy of this behavior network
	 * 
	 * @param newBehaviorId
	 *            Whether a different behavior id should be used for each
	 *            behavior
	 * @return
	 */
	public BehaviorNetwork copy(boolean newBehaviorId) {
		BehaviorNetwork copy = new BehaviorNetwork();
		// Add behaviors, and edges
		if (map.entrySet().size() > 0) {
			for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				BehaviorPosition tP = (BehaviorPosition) e.getKey();
				Behavior tB = (Behavior) map.get(tP);
				copy.addBehavior(tB.copy(newBehaviorId), tP.copy());
			}
		}
		// Update weights/coefficients
		if (dynamic) {
			copy.dynamic = true;
			copy.dynamicStr = dynamicStr;
		} else {
			AppEngine engine = AppEngine.getInstance();

			List behaviorList = getBehaviorList();
			int ids[] = new int[behaviorList.size()];
			for (int k = 0; k < behaviorList.size(); k++)
				ids[k] = ((Behavior) behaviorList.get(k)).getMyId();

			Edge edges[] = getEdgesGivenBehaviors(ids);
			double coefficients[][] = new double[ids.length][ids.length];
			for (int m = 0; m < edges.length; m++) {
				Behavior fB = edges[m].fromB();
				Behavior tB = edges[m].toB();
				int f, t;
				for (f = 0; f < ids.length; f++) {
					if (fB.getMyId() == (ids[f])) {
						for (t = 0; t < ids.length; t++)
							if (tB.getMyId() == (ids[t])) {
								coefficients[f][t] = edges[m].inhibitionFT();
								coefficients[t][f] = edges[m].inhibitionTF();
								break;
							}
						break;
					}
				}

			}

			double[] weights = new double[ids.length];
			for (int k = 0; k < behaviorList.size(); k++)
				weights[k] = ((Behavior) behaviorList.get(k)).getWeight();

			// Update coefficients
			engine.updateCoefficients(copy, coefficients, ids);

			// Update weights
			engine.updateWeights(copy, weights, ids);

		}

		return copy;
	}

	/**
	 * Using another behavior network to initialize this behavior network
	 * 
	 * @param another
	 *            Another behavior network
	 * @param bn
	 *            The entity who owns this behavior network
	 */
	public void paste(BNCategory bn, BehaviorNetwork another) throws Exception {
		// Empty behavior network
		if (another == null)
			return;
		// Clear the existing information
		clearAll();
		// Create a copy of behaviors; Add behaviors, and add edges
		if (another.map.entrySet().size() > 0) {
			for (Iterator i = another.map.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				BehaviorPosition tP = (BehaviorPosition) e.getKey();
				Behavior tB = (Behavior) another.map.get(tP);
				// Create a new behavior for the target agent in the system
				AppEngine eng = AppEngine.getInstance();
				Behavior anCopy = eng.createNewBehavior(bn, 
						tB.getBehaviorName(), 
						tB.getBehaviorEquation(), 
						tB.isResumable(), 
						eng.getBehaviorActionString(tB.getMyId()));		
				// Add the behavior into this behavior network
				eng.behaviorAddedToNetwork(bn, new int[]{anCopy.getMyId()});
			}
		}
		// Update weights/coefficients
		try {
			if (another.dynamic) {
				setDynamicBehaviorNetwork(bn, another.getDynamicStr());
			} else {
				List behaviorList = another.getBehaviorList();
				int ids[] = new int[behaviorList.size()];
				for (int k = 0; k < behaviorList.size(); k++)
					ids[k] = ((Behavior) behaviorList.get(k))
							.getMyId();

				AppEngine engine = AppEngine.getInstance();
				int inx = bn.getActionSelectionMechanismIndex();
				if (inx == MUTUAL) {
					Edge edges[] = another.getEdgesGivenBehaviors(ids);
					double coefficients[][] = new double[ids.length][ids.length];
					for (int m = 0; m < edges.length; m++) {
						Behavior fB = edges[m].fromB();
						Behavior tB = edges[m].toB();
						int f, t;
						for (f = 0; f < ids.length; f++) {
							if (fB.getMyId() == (ids[f])) {
								for (t = 0; t < ids.length; t++)
									if (tB.getMyId() == (ids[t])) {
										coefficients[f][t] = edges[m]
												.inhibitionFT();
										coefficients[t][f] = edges[m]
												.inhibitionTF();
										break;
									}
								break;
							}
						}

					}
					// Update coefficients
					engine.updateCoefficients(this, coefficients, ids);
				} else if (inx == COOPERATIVE) {
					double[] weights = new double[ids.length];
					for (int k = 0; k < behaviorList.size(); k++)
						weights[k] = ((Behavior) behaviorList.get(k))
								.getWeight();
					// Update weights
					engine.updateWeights(this, weights, ids);
				}
			}
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Remove all behaviors from this behavior network
	 */
	public void clearAll() {
		map.clear();
		list.clear();
		behaviorIds.clear();
	}

	/**
	 * @return the dynamic
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * @param dynamic
	 *            the dynamic to set
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	/**
	 * @return the dynamicStr
	 */
	public String getDynamicStr() {
		return dynamicStr;
	}

	/**
	 * @param dynamicStr
	 *            the dynamicStr to set
	 */
	public void setDynamicStr(String dynamicStr) {
		this.dynamicStr = dynamicStr;
	}

	/**
	 * @return the dynamicStrMethodName
	 */
	public String getDynamicStrMethodName() {
		return dynamicStrMethodName;
	}

	/**
	 * @param dynamicStrMethodName
	 *            the dynamicStrMethodName to set
	 */
	public void setDynamicStrMethodName(String dynamicStrMethodName) {
		this.dynamicStrMethodName = dynamicStrMethodName;
	}

	/**
	 * Setup the behavior network dynamically. Typically, it is the
	 * coefficients/weights which are setup dynamically through the dynamic
	 * string which in turn is wrapped in a method call defined in the current
	 * entity.
	 * 
	 * @param bn
	 *            The target entity with behavior network
	 * @param dynamicStr
	 *            The dynamic string used to setup coefficients
	 */
	public void setDynamicBehaviorNetwork(BNCategory bn, String dynamicStr)
			throws Exception {
		// boolean ret = false;
		this.dynamic = true;
		this.dynamicStr = dynamicStr;
		String methodName = "_proxy" + (ConfigParameters.methodIndex++);
		// try {
		StringBuffer code = new StringBuffer();
		code.append("public void ").append(methodName).append("(){").append(
				dynamicStr).append("}");
		AppEngine ae = AppEngine.getInstance();
		ae.createANewMethod(bn.getEntityType(), false, false, methodName, code
				.toString());
		// } catch(Exception e) {
		// ret = false;
		// }
		// try {
		// AppEngine ae = AppEngine.getInstance();
		// ae.replaceEntities(bn.getName());
		ae.system.updateEntity(bn.getEntityType(), bn);
		// } catch(Exception e) {
		// ;// FIXME:: IGNORE???
		// }
		this.dynamicStrMethodName = methodName;
		// return ret;
	}

	/**
	 * Setup the behavior network dynamically. Typically, it is the
	 * coefficients/weights which are setup dynamically through the dynamic
	 * string which in turn is wrapped in a method call defined in the current
	 * entity.
	 * 
	 * @param dynamicStr
	 *            The dynamic string used to setup coefficients
	 */
	public void setDynamicBehaviorNetwork(String dynamicStr) throws Exception {
		// boolean ret = false;
		this.dynamic = true;
		this.dynamicStr = dynamicStr;
		String methodName = "_proxy" + (ConfigParameters.methodIndex++);
		// try {
		StringBuffer code = new StringBuffer();
		code.append("public void ").append(methodName).append("(){").append(
				dynamicStr).append("}");
		AppEngine ae = AppEngine.getInstance();
		ae.createANewMethod(ae.appManager.currentApp.currentEntity
				.getEntityType(), false, false, methodName, code.toString());
		// } catch(Exception e) {
		// ret = false;
		// }
		// try {
		// AppEngine ae = AppEngine.getInstance();
		// ae.replaceEntities(ae.appManager.currentApp.currentEntity.getName());
		ae.system.updateEntity(ae.appManager.currentApp.currentEntity
				.getEntityType(), ae.appManager.currentApp.currentEntity);
		// } catch(Exception e) {
		// ;// FIXME:: IGNORE???
		// }
		this.dynamicStrMethodName = methodName;
		// return ret;
	}

	/**
	 * Whether this behavior is defined.
	 * 
	 * This means that both the behaviors and their coefficients are defined.
	 * 
	 * @return
	 */
	public boolean isBehaviorNetworkDefined() {
		return !this.getCoefficientsString().trim().equals("");
	}

	/**
	 * Whether the user can define coefficients between behaviors.
	 * 
	 * @return True if two or more behaviors are in the network.
	 */
	public boolean canDefineCoefficients() {
		if (map.entrySet().size() >= 2)
			return true;
		else
			return false;
	}

	/**
	 * Add a behavior into the network
	 * 
	 * @param behavior
	 *            Behavior to add
	 * @param position
	 *            Position of the behavior
	 */
	public void addBehavior(Behavior behavior, BehaviorPosition position) {
		if (map.entrySet().size() > 0) {
			for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				BehaviorPosition tP = (BehaviorPosition) e.getKey();
				Behavior tB = (Behavior) map.get(tP);
				Edge edge = new Edge(behavior, position, tB, tP);
				addEdge(edge);
			}
		}
		map.put(position, behavior);
		behaviorIds.add(new Integer(behavior.getMyId()));
	}

	/**
	 * Remove the behavior specified by the position.
	 * 
	 * 1) remove "from" and "to" edges connecting the position 2) remove the
	 * behavior name from the behavior network 3) remove the behavior
	 * information from the map
	 * 
	 * @param position
	 *            The position of the behavior to be deleted
	 * 
	 */
	public void removeBehavior(BehaviorPosition position) {
		if (position == null)
			return;
		Behavior behavior = (Behavior) map.get(position);
		if (behavior != null) {
			boolean removed = false;
			for (int i = 0; i < list.size(); i++) {
				Edge e = (Edge) list.get(i);
				if (e.fromP().equals(position) || e.toP().equals(position)) {
					// removeEdge(e); BUG!!!!
					removeEdgeAsNULL(i);
					removed = true;
				}
			}
			// Remove the null element in the list
			if (removed) {
				List newList = new ArrayList(list.size());
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) != null) {
						newList.add(list.get(i));
					}
				}
				list.clear();
				list.addAll(newList);
			}
			behaviorIds.remove(new Integer(behavior.getMyId()));
			map.remove(position);
		}
	}

	/**
	 * Return the behavior of the specified position
	 * 
	 * @param position
	 *            Behaviour position
	 * @return
	 */
	public Behavior getBehavior(BehaviorPosition position) {
		Behavior behavior = null;
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			BehaviorPosition key = (BehaviorPosition) i.next();
			if (key.getRow() == position.getRow()
					&& key.getColumn() == position.getColumn()) {
				behavior = (Behavior) map.get(key);
			}
		}
		if (behavior == null)
			behavior = Behavior.NO_BEHAVIOR;
		return behavior;
	}

	/**
	 * Return the behavior with specified name
	 * 
	 * @param name
	 *            Name of the target behavior
	 * @return
	 */
	public Behavior getBehavior(String name) {
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			Behavior behavior = (Behavior) map.get(key);
			if (behavior.getBehaviorName().equals(name))
				return behavior;
		}
		return null;
	}

	/**
	 * Return the behavior with specified name
	 * 
	 * @param id
	 *            ID of the target behavior
	 * @return
	 */
	public Behavior getBehavior(int id) {
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			Behavior behavior = (Behavior) map.get(key);
			if (behavior.getMyId() == id)
				return behavior;
		}
		return null;
	}

	/**
	 * Return the position of the specified behavior
	 * 
	 * @param behavior
	 * @return
	 */
	public BehaviorPosition getPosition(Behavior behavior) {
		BehaviorPosition position = null;
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			Behavior behavior1 = (Behavior) map.get(key);
			if (behavior1.getMyId() == (behavior.getMyId())) {
				position = (BehaviorPosition) key;
				break;
			}
		}
		return position;
	}

	/**
	 * Return the first unoccupied position in the network
	 * 
	 * @return
	 */
	public BehaviorPosition getFirstUnoccupiedPosition() {
		BehaviorPosition position = null;
		for (int i = 0; i < BehaviorPosition.NETWORK_SQUARES; i++)
			for (int j = 0; j < BehaviorPosition.NETWORK_SQUARES; j++) {
				if (map.get(BehaviorPosition.getPosition(i, j)) == null) {
					position = BehaviorPosition.getPosition(i, j);
					return position;
				}
			}
		return position;
	}

	/**
	 * Save the edge
	 * 
	 * @param e
	 */
	public void addEdge(Edge e) {
		list.add(e);
	}

	/**
	 * Return all edges indicating the behavior coefficients
	 * 
	 * @return
	 */
	public Edge[] getAllEdges() {
		int edgesN = list.size();
		Edge[] edges = new Edge[edgesN];
		for (int i = 0; i < edgesN; i++) {
			edges[i] = (Edge) list.get(i);
		}
		return edges;
	}

	/**
	 * Get all edges starting from specified behavior or ending with specified
	 * behavior.
	 * 
	 * @param id
	 * @return
	 */
	public Edge[] getEdgesForBehaviorNode(int id) {
		ArrayList l = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Edge e = (Edge) list.get(i);
			if (e.fromB().getMyId() == (id)
					|| e.toB().getMyId() == (id))
				l.add(e);
		}
		Edge[] edges = new Edge[l.size()];
		for (int i = 0; i < l.size(); i++) {
			edges[i] = (Edge) l.get(i);
		}
		return edges;
	}

	/**
	 * Return all edges associated with behavior names.
	 * 
	 * @param ids 
	 * @return
	 */
	public Edge[] getEdgesGivenBehaviors(int ids[]) {
		int edgesN = list.size();
		// there is one edge for two inhibitions (1 to 2 and 2 to 1)
		// Edge [] edges = new Edge[edgesN];
		ArrayList l = new ArrayList();
		for (int i = 0; i < edgesN; i++) {
			Edge e = (Edge) list.get(i);
			Behavior b1 = e.fromB();
			Behavior b2 = e.toB();
			boolean onePresent = false, twoPresent = false;
			for (int j = 0; j < ids.length; j++)
				if (ids[j] == b1.getMyId()) {
					onePresent = true;
					break;
				}
			for (int k = 0; k < ids.length; k++)
				if (ids[k] == (b2.getMyId())) {
					twoPresent = true;
					break;
				}
			if (onePresent && twoPresent)
				l.add(e);
		}
		Edge[] edges = new Edge[l.size()];
		for (int i = 0; i < l.size(); i++) {
			edges[i] = (Edge) l.get(i);
		}
		return edges;

	}

	/**
	 * Return the weights string, in the case of constant weights.
	 * 
	 * @return the weights string
	 */
	public String getWeightsString() {
		StringBuffer sb = new StringBuffer();
		List behaviors = getBehaviorList();
		for (int i = 0; i < behaviors.size(); i++) {
			sb.append(((Behavior) behaviors.get(i)).getWeight()).append("\t");
		}
		return sb.toString();
	}

	/**
	 * Return the string representation of the coefficients
	 * 
	 * @return
	 */
	public String getCoefficientsString() {
		String coefficientsString = new String("");
		List behaviors = getBehaviorList();
		double coefficients[][] = new double[behaviors.size()][behaviors.size()];
		for (int m = 0; m < list.size(); m++) {
			Behavior fB = ((Edge) list.get(m)).fromB();
			Behavior tB = ((Edge) list.get(m)).toB();
			int f, t;
			for (f = 0; f < behaviors.size(); f++) {
				if (fB.getMyId() == 
						((Behavior) behaviors.get(f)).getMyId()) {
					for (t = 0; t < behaviors.size(); t++)
						if (tB.getMyId() == ((Behavior) behaviors.get(t)).getMyId()) {
							coefficients[f][t] = ((Edge) list.get(m))
									.inhibitionFT();
							coefficients[t][f] = ((Edge) list.get(m))
									.inhibitionTF();
							break;
						}
					break;
				}
			}
		}
		for (int i = 0; i < coefficients.length; i++)
			for (int j = 0; j < coefficients.length; j++) {
				if (i != j) {
					coefficientsString += "\t";
					coefficientsString += coefficients[i][j];
				}
			}

		return coefficientsString;
	}

	/**
	 * Update the weight of a single behavior. It is only used in the
	 * cooperation mechanism.
	 * 
	 * @param name
	 *            The name of the behavior to set
	 * @param weight
	 *            The new weight of the behavior
	 */
	public void updateWeight(String name, double weight) {
		List behaviors = getBehaviorList();
		for (int i = 0; i < behaviors.size(); i++) {
			Behavior behavior = (Behavior) behaviors.get(i);
			if (behavior.getBehaviorName().equals(name))
				behavior.setWeight(weight);
		}
	}

	/**
	 * Update the coefficients of the behavior network. For the cooperative
	 * mechanism, the coefficients are the weights for behaviors.
	 * 
	 * @param coefficients
	 *            Coefficients/Weights
	 */
	public void updateEdgesFromCoefficientsList(boolean mutal, List coefficients) {
		List behaviors = getBehaviorList();
		if (!mutal) {
			/** Cooperative mechanism */
			for (int i = 0; i < behaviors.size(); i++) {
				Behavior behavior = (Behavior) behaviors.get(i);
				if (i > coefficients.size()) {
					behavior.setWeight(0.0D); // FIXME: Set to 0.0D ?
					continue;
				}
				behavior
						.setWeight(((Double) coefficients.get(i)).doubleValue());
			}
			return;
		}
		int n = behaviors.size();
		if (n * n - n != coefficients.size())
			return;
		int ids[] = new int[behaviors.size()];
		for (int k = 0; k < behaviors.size(); k++)
			ids[k] = ((Behavior) behaviors.get(k)).getMyId();

		double coefficientsDouble[][] = new double[ids.length][ids.length];
		int index = 0;
		for (int i = 0; i < ids.length; i++)
			for (int j = 0; j < ids.length; j++) {
				if (i == j)
					coefficientsDouble[i][j] = 0;
				else {
					coefficientsDouble[i][j] = ((Double) coefficients
							.get(index)).doubleValue();
					index++;
				}
			}

		for (int m = 0; m < list.size(); m++) {
			Behavior fB = ((Edge) list.get(m)).fromB();
			Behavior tB = ((Edge) list.get(m)).toB();
			int f, t;
			for (f = 0; f < ids.length; f++) {
				if (fB.getMyId() == (ids[f])) {
					for (t = 0; t < ids.length; t++)
						if (tB.getMyId() == (ids[t])) {
							((Edge) list.get(m))
									.setInhibitionFT(coefficientsDouble[f][t]);
							((Edge) list.get(m))
									.setInhibitionTF(coefficientsDouble[t][f]);
							break;
						}
					break;
				}
			}

		}

	}

	/**
	 * Update edge's inhibition weight
	 * 
	 * @param edges
	 */
	public void updateEdges(Edge edges[]) {
		for (int i = 0; i < edges.length; i++)
			for (int j = 0; j < list.size(); j++) {
				if (edges[i].fromB().getMyId() == (
						((Edge) list.get(j)).fromB().getMyId())
						&& edges[i].toB().getMyId() == (
								((Edge) list.get(j)).toB().getMyId())) {
					((Edge) list.get(j)).setInhibitionFT(edges[i]
							.inhibitionFT());
					((Edge) list.get(j)).setInhibitionTF(edges[i]
							.inhibitionTF());
					break;
				}

			}
	}

	/**
	 * Remove an edge from the behavior network
	 * 
	 * @param e
	 */
	public void removeEdge(Edge e) {
		list.remove(e);
	}

	/**
	 * Remove the edge at the index by setting it as NULL.
	 * 
	 * 
	 * @param index
	 *            The index of the specified edge in the edge list
	 */
	private void removeEdgeAsNULL(int index) {
		list.set(index, null);
	}

	/**
	 * Return a list of added behaviors.
	 * 
	 * @return A list of added behaviors
	 */
	public List getBehaviorList() {
		List list = new ArrayList(0);
		for (int k = 0; k < behaviorIds.size(); k++) {
			Integer id = (Integer) behaviorIds.get(k);
			Behavior behaviorToAdd = null;
			for (Iterator i = map.keySet().iterator(); i.hasNext();) {
				Object key = i.next();
				Behavior behavior = (Behavior) map.get(key);
				if (behavior.getMyId() == id.intValue()) {
					behaviorToAdd = behavior;
					break;
				}
			}
			if (behaviorToAdd != null)
				list.add(behaviorToAdd);
		}
		return list;
	}

	/**
	 * Returns an Iterator for iterating through the occupied positions
	 */
	public Iterator behaviorLocationIterator() {
		return java.util.Collections.unmodifiableCollection(map.values())
				.iterator();
	}

	/**
	 * Return a description of list of behaviors
	 */
	public String toString() {
		StringBuffer network = new StringBuffer(100);
		for (int row = 0; row < BehaviorPosition.NETWORK_SQUARES; row++) {
			for (int col = 0; col < BehaviorPosition.NETWORK_SQUARES; col++)
				network.append(getBehavior(BehaviorPosition.getPosition(row,
						col)));
		}
		return network.toString();
	}

	/**
	 * Move the behavior image to the specified position
	 * 
	 * @param start
	 *            Original position
	 * @param end
	 *            The specified destination
	 * @return
	 */
	public BehaviorNetwork applyBehaviorMove(BehaviorPosition start,
			BehaviorPosition end) {
		Behavior behavior = (Behavior) map.get(start);
		if (behavior == null)
			return this;
		this.map.remove(start);
		this.map.put(end, behavior);
		for (int i = 0; i < list.size(); i++) {
			Edge e = (Edge) list.get(i);
			if (e.fromP() == start)
				e.setFromP(end);
			if (e.toP() == start)
				e.setToP(end);
		}
		return this;
	}

	/**
	 * Compute the behavior strength for the specified time step. The behavior
	 * excitation should be calculated and the coefficients pairs should be
	 * specified first.
	 * 
	 * @param timetick
	 *            which time step
	 */
	public void updateBehaviorStrengths(int timetick) {

		// Get behavior list
		List behaviors = getBehaviorList();

		// Set initial behavior strength as the behavior excitation
		for (int i = 0; i < behaviors.size(); i++) {
			Behavior behavior = (Behavior) behaviors.get(i);
			behavior.setBehaviorStrength(behavior.getExcitation(timetick),
					timetick);
		}

		// Update coefficients
		double coefficients[][] = new double[behaviors.size()][behaviors.size()];
		for (int m = 0; m < list.size(); m++) {
			Behavior fB = ((Edge) list.get(m)).fromB();
			Behavior tB = ((Edge) list.get(m)).toB();
			int f, t;
			for (f = 0; f < behaviors.size(); f++) {
				if (fB.getMyId() == (
						((Behavior) behaviors.get(f)).getMyId())) {
					for (t = 0; t < behaviors.size(); t++)
						if (tB.getMyId()
								 == (
										((Behavior) behaviors.get(t))
												.getMyId())) {
							coefficients[f][t] = ((Edge) list.get(m))
									.inhibitionFT();
							coefficients[t][f] = ((Edge) list.get(m))
									.inhibitionTF();
							break;
						}
					break;
				}
			}
		}

		// Calculate the behavior strength
		for (int i = 0; i < behaviors.size(); i++) {

			// Get behavior
			Behavior behavior = (Behavior) behaviors.get(i);

			// Wrap around implementation
			double bStrength_i_minus_1 = timetick != 0 ? behavior
					.getBehaviorStrength(timetick - 1) : behavior
					.getBehaviorStrength(behavior.getTotalTimeSteps() - 1);

			// Mutual inhibition
			if (bStrength_i_minus_1 > BEHAVIORSTRENGTHTHRESHOLD) {
				for (int j = 0; j < behaviors.size(); j++) {
					behavior = (Behavior) behaviors.get(j);
					double bStrength_i = behavior.getBehaviorStrength(timetick);
					behavior.setBehaviorStrength(bStrength_i
							- coefficients[j][i] * bStrength_i_minus_1,
							timetick);
				}
			}

		}

	}

	/**
	 * Select the winner. It would be <code>NO_BEHAVIOR</code> if all the
	 * behavior strength is too low.
	 * 
	 * @param timetick
	 *            Which time step
	 * @return The winner.
	 */
	public Behavior selectBehavior(int timetick) {
		List behaviors = getBehaviorList();
		Behavior result = Behavior.NO_BEHAVIOR;

		double threshold = BEHAVIORTHRESHOLD;
		double temp = threshold;
		for (int i = 0; i < behaviors.size(); i++) {
			Behavior behavior = (Behavior) behaviors.get(i);
			double bStrength = behavior.getBehaviorStrength(timetick);
			if (bStrength > temp) {
				result = behavior;
				// threshold = bStrength;
				temp = bStrength;
			}
		}
		return result;
	}
}
