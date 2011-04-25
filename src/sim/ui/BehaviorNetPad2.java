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

package sim.ui;

import java.awt.Graphics;


import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.behavior.BehaviorPosition;

public class BehaviorNetPad2 extends BehaviorNetPad {

	// /** Summation background image */
	// private Image sumImage = null;
	//	
	// /** Arrow image */
	// private Image arrowImage = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8016457248728967259L;

	/** Summation background image path */
	// private static final String sumImagePath = "/sim/ui/images/sum.jpg";
	//    
	// /** Arrow background image path */
	// private static final String arrowImagePath = "/sim/ui/images/arrow.jpg";
	/**
	 * Constructor with the given behavior network
	 * 
	 * @param net
	 *            The network to set
	 * @exception If
	 *                the images can not be loaded
	 */
	public BehaviorNetPad2(BehaviorNetwork net) throws Exception {

		super(net);

		// // Load summation background image
		// sumImage = Toolkit.getDefaultToolkit().createImage
		// (engineRef.jrl.getImage(
		// sumImagePath));
		// arrowImage = Toolkit.getDefaultToolkit().createImage
		// (engineRef.jrl.getImage(
		// arrowImagePath));
		this.arrangement = net;

	}

	/** Paint behavior network */
	protected void paintBehaviorNetwork(boolean excludePressedEdges) {
		// Draw the list of behaviors first
		Graphics g = iBehaviorNetImage.getGraphics();
		int distance = imRenderer.stdDistance();
		int currX = 0, currY = 0, nextX = 0, nextY = 0;
		for (int row = 0; row < BehaviorPosition.NETWORK_SQUARES; row++) {
			for (int col = 0; col < BehaviorPosition.NETWORK_SQUARES; col++) {
				nextX = col * distance;
				nextY = row * distance;
				synchronized (this) {
					g.translate(nextX - currX, nextY - currY);
					Behavior behavior = arrangement
							.getBehavior(BehaviorPosition.getPosition(row, col));
					if (behavior == Behavior.NO_BEHAVIOR
							|| (excludePressedEdges && BehaviorPosition
									.getPosition(row, col) == pressedPosition))
						g
								.drawImage(imRenderer.getBackgroundImage(), 0,
										0, this);
					else
						g.drawImage(imRenderer
								.getBehaviorWithBackgroundImage(behavior), 0,
								0, this);

					currX = nextX;
					currY = nextY;
				}

			}
		}
		// // Draw the summation image
		// BehaviorPosition available =
		// arrangement.getFirstUnoccupiedPosition();
		// if (available == null) {
		// MessageUtils.displayError("No available space for the summation image.");
		// return;
		// }
		// int row = available.getRow(); int col = available.getColumn();
		// nextX = col * distance;
		// nextY = row * distance;
		// g.translate(nextX - currX, nextY - currY);
		// g.drawImage(sumImage, 0, 0, this);
		//        
		// // Draw the edges from behaviors to the summation image
		// g.translate(0, 0);
		// Graphics2D g2 = (Graphics2D)g;
		// List behaviors = arrangement.getBehaviorList();
		// for(int k = 0; k < behaviors.size(); k++){
		// Behavior behavior = (Behavior)behaviors.get(k);
		// BehaviorPosition f = arrangement.getPosition(behavior);
		// BehaviorPosition t = available;
		// int xFrom = f.getColumn() * distance + distance / 2;
		// int yFrom = f.getRow() * distance + distance / 2;
		// int xTo = t.getColumn() * distance + distance / 2;
		// int yTo = t.getRow() * distance + distance / 2;
		// //assume the radius of behavior node is 18.5
		// Point fPoint = new Point(xFrom, yFrom);
		// Point tPoint = new Point(xTo, yTo);
		// double angle = tPoint.angle(fPoint);
		// double delta = 0.35;
		// g.setColor(Color.GRAY);
		// g.drawLine(xFrom + (int) (18.5 * Math.cos(angle - delta)),
		// yFrom + (int) (18.5 * Math.sin(angle - delta)),
		// xTo + (int) (18.5 * Math.cos(angle + delta)),
		// yTo + (int) (18.5 * Math.sin(angle + delta)));
		// //draw arrow
		// drawArrow(g2,
		// xTo + (int) (18.5 * Math.cos(angle + delta)),
		// yTo + (int) (18.5 * Math.sin(angle + delta)),
		// angle);
		//            
		// }
		g.dispose();
	}

	// /**
	// * Draw the arrow from a position in the given direction.
	// * @param g2 The graphics
	// * @param posX The x position
	// * @param posY The y position
	// * @param dir The direction of the arrow
	// */
	// private void drawArrow(Graphics2D g2, double posX, double posY, double
	// dir) {
	//    	
	// // set transform, first toCenter then rotate
	// AffineTransform at = new AffineTransform();
	// at.rotate(dir);
	// AffineTransform toCenterAt = new AffineTransform();
	// toCenterAt.translate (posX, posY);
	// toCenterAt.concatenate(at);
	//      
	// // save old transform
	// AffineTransform saveXform = g2.getTransform ();
	//      
	// // transform
	// g2.transform(toCenterAt);
	// // draw
	// g2.drawImage(arrowImage,
	// (int)(-1*arrowImage.getWidth(this)/2),
	// (int)(-1*arrowImage.getHeight(this)/2),
	// (int)arrowImage.getWidth(this),
	// (int)arrowImage.getHeight(this),
	// null);
	// // restore old transform
	// g2.setTransform(saveXform);
	// }
}
