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
 * <p>
 * Two-dimensional vector class. Two coordinations are included: X and Y.
 * </p>
 * 
 * @author Steve, Fasheng Qiu
 * @version 1.0
 */
public class Vect {
	/** The x coordination */
	public double dx;

	/** The y coordination */
	public double dy;

	/** Zero vector */
	public static final Vect ZERO = new Vect(0.0, 0.0);

	/** X unit vector */
	public static final Vect UNIT_X = new Vect(1.0, 0.0);

	/** Y unit vector */
	public static final Vect UNIT_Y = new Vect(0.0, 1.0);

	/**
	 * Default constructor. A zero vector will be constructed.
	 * 
	 */
	public Vect() {
		set(ZERO);
	}

	/**
	 * Constructor
	 * 
	 * @param dx
	 *            The x coordination
	 * @param dy
	 *            The y coordination
	 */
	public Vect(double dx, double dy) {
		set(dx, dy);
	}

	/**
	 * Set the two coordinations of this vector
	 * 
	 * @param x
	 *            X coordination
	 * @param y
	 *            Y coordination
	 */
	public void set(double x, double y) {
		this.setDx(x);
		this.setDy(y);
	}

	/**
	 * Set the two coordinations same as the specified vector.
	 * 
	 * @param another
	 *            Another vector
	 */
	public void set(Vect another) {
		set(another.dx, another.dy);
	}

	/**
	 * Return the length of this vector. <code>Math.sqrt(dx*dx + dy*dy)</code>
	 * 
	 * @return the length of this vector.
	 */
	public double mag() {
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Return the dot production between this vector and the specified vector
	 * 
	 * @param v
	 *            Another vector in dot production
	 * @return The dot production
	 */
	public double dot(Vect v) {
		return dot(v.dx, v.dy);
	}

	/**
	 * Return the dot production between this vector and the specified vector
	 * 
	 * @param x
	 *            X coordination
	 * @param y
	 *            Y coordination
	 * @return The dot production
	 */
	public double dot(double x, double y) {
		return this.dx * x + this.dy * y;
	}

	/**
	 * Return the unit vector from this vector.
	 * 
	 * @return the unit vector. If the length of this vector is 0, return the
	 *         vector whose x and y components are 0.
	 */
	public Vect unit() {
		double mag = Math.sqrt(dx * dx + dy * dy);
		if (mag == 0.0)
			return new Vect(0, 0);
		else
			return new Vect(dx / mag, dy / mag);
	}

	/**
	 * Return the angle of this vector. same as <code>Math.atan2(dy, dx)</code>
	 * 
	 * @return the angle of this vector
	 */
	public double angle() {
		return Math.atan2(dy, dx);
	}

	/**
	 * Return a new vector whose x and y component are the addition of those of
	 * this vector and another given vector represented by two coordinations.
	 * 
	 * @param x
	 *            X coordination
	 * @param y
	 *            Y coordination
	 * @return The new vector after addition
	 */
	public Vect add(double x, double y) {
		return new Vect(this.dx + x, this.dy + y);
	}

	/**
	 * Return a new vector whose x and y component are the addition of those of
	 * this vector and another given vector.
	 * 
	 * @param v
	 *            Another vector for addition
	 * @return The new vector after addition
	 */
	public Vect add(Vect v) {
		return add(v.dx, v.dy);
	}

	/**
	 * Return a new vector which is the result of multiplying a scalar value
	 * with this vector.
	 * 
	 * @param a
	 *            The scale value to multiply
	 * @return The new result vector
	 */
	public Vect scalarMultiply(double a) {
		return new Vect(a * dx, a * dy);
	}

	/**
	 * Return two coordinations of this vector
	 * 
	 * @return Two coordinations of this vector
	 */
	public String toString() {
		return "(" + dx + ", " + dy + ")";
	}

	/**
	 * Check whether the two vectors represent the same vector.
	 * 
	 * @param o
	 *            Another object to compare
	 * @return true if the two coordinations are equal.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Vect))
			return false;
		Vect p = (Vect) o;
		return p.dx == this.dx && p.dy == this.dy;
	}

	/**
	 * Return the hash code of this vector
	 * 
	 * @return the hash code of this vector
	 */
	public int hashCode() {
		return (dx + " " + dy).hashCode();
	}

	/**
	 * Get the x coordination of this vector
	 * 
	 * @return the dx
	 */
	public double getDx() {
		return dx;
	}

	/**
	 * Set the x coordination of this vector
	 * 
	 * @param dx
	 *            the dx to set
	 */
	public void setDx(double dx) {
		this.dx = dx;
	}

	/**
	 * Get the y coordination of this vector
	 * 
	 * @return the dy
	 */
	public double getDy() {
		return dy;
	}

	/**
	 * Set the y coordination of this vector
	 * 
	 * @param dy
	 *            the dy to set
	 */
	public void setDy(double dy) {
		this.dy = dy;
	}
}
