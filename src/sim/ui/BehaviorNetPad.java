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

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

import sim.core.AppEngine;
import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorNetwork;
import sim.model.behavior.BehaviorPosition;
import sim.model.behavior.Edge;
import sim.ui.panels.BehaviorNetworkListener;
import sim.util.MessageUtils;
import sim.util.Point;
import sim.util.SimException;

/**
 * Behavior Network Panel
 * 
 * @author Pavel
 * @version 1.0
 */
public class BehaviorNetPad extends JComponent implements MouseListener/*
																		 * ,
																		 * ActionListener
																		 */{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8527933042713160649L;

	// Whether the mouse is active
	private boolean mouseActive = false;

	// Whether the mouse is dragged
	private boolean mouseDragged = false;

	// Action listener
	private ActionListener actionListener = null;

	// Behavior network image
	protected Image iBehaviorNetImage = null;

	// Behavior image
	private Image behaviorImage = null;

	// Background image
	private Image iBackgroundImage = null;

	// Image renderer
	protected ImageRenderer imRenderer = new ImageRenderer(this);

	// Margin distance
	private int iRdistance = 0;

	// Behavior image path
	private static final String behaviorImagePath = "/sim/ui/images/behavior.jpg";

	// Background image path
	private static final String behaviorBackgroundImagePath = "/sim/ui/images/backgr.jpg";

	// Back up behavior network
	protected BehaviorNetwork arrangement = null;

	// Default behavior network
	// private BehaviorNetwork defaultArrangement = new BehaviorNetwork();

	// Release position of the mouse
	protected BehaviorPosition releasedPosition = null;

	// Pressed position of the mouse
	protected BehaviorPosition pressedPosition = null;

	// Edges should be have the effect of pressure
	private Edge pressedBehaviorEdges[] = null;

	// Behavior move thread
	private AnimateBehaviorMove animateThread = null;

	// Application engine
	protected AppEngine engineRef = AppEngine.getInstance();

	// // Popup menu brought up by GUI elements
	// private JPopupMenu popupMenu = new JPopupMenu();
	//    
	// // Delete menu item
	// private JMenuItem menuItemDelete = new JMenuItem("Delete");


	/** Listeners for the change of behavior network */
	private BehaviorNetworkListener listener = null;

	/**
	 * @return the listener
	 */
	public BehaviorNetworkListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(BehaviorNetworkListener listener) {
		this.listener = listener;
	}

	/**
	 * Constructor with the given behavior network
	 * 
	 * @param net
	 *            The network to set
	 */
	public BehaviorNetPad(BehaviorNetwork net) {
		addMouseListener(this);

		behaviorImage = Toolkit.getDefaultToolkit().createImage(
				engineRef.jrl.getImage(behaviorImagePath));

		super.setBorder(LineBorder.createBlackLineBorder());
		this.arrangement = net;
	}

	/** Get preferred size */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/** Set application engine */
	public void setApplicationEngine(AppEngine eng) {
		engineRef = eng;
	}

	/**
	 * Get the minimum size
	 */
	public Dimension getMinimumSize() {
		int dist = imRenderer.stdDistance();
		return new Dimension(dist * BehaviorPosition.NETWORK_SQUARES
				+ iRdistance, dist * BehaviorPosition.NETWORK_SQUARES
				+ iRdistance);
	}

	/** Get the image renderer used */
	public ImageRenderer getImageRenderer() {
		return imRenderer;
	}

	/** Get net pad image */
	public Image getBehaviorNetImage() {
		return iBehaviorNetImage;
	}

	/** Setup images */
	public void addNotify() {

		super.addNotify();

		if (iBehaviorNetImage == null) {
			try {
				setupImage();
			} catch (Exception e) {
				MessageUtils.debugAndDisplay(this, "setupImage", e);
			}
		}

	}

	/**
	 * Set up images
	 * 
	 * @throws Exception
	 *             If images can not be loaded
	 */
	public void setupImage() throws Exception {

		// Prepare the behavior image
		if (behaviorImage == null)
			throw new SimException("BEHAV-IMAGE-NOT-LOADED",
					"Behavior image is not loaded.", null);

		imRenderer.prepareImage(behaviorImage);

		// background is not required
		imRenderer.setImageWithBackground(getToolkit().createImage(
				engineRef.jrl.getImage(behaviorBackgroundImagePath)));

		int distance = imRenderer.stdDistance();
		int width = distance * BehaviorPosition.NETWORK_SQUARES;
		int height = width;
		iBehaviorNetImage = new BufferedImage(width + iRdistance, height
				+ iRdistance, BufferedImage.TYPE_INT_ARGB);
		iBackgroundImage = new BufferedImage(width + iRdistance, height
				+ iRdistance, BufferedImage.TYPE_INT_ARGB);
		setSize(getPreferredSize());
		if (iBehaviorNetImage != null) {
			paintBehaviorNetwork(false);
			repaint();
		} else {
			throw new SimException("BEHAV-IMAGE-NOT-LOADED",
					"Behavior network image is not loaded.", null);
		}

		// behaviors available initially		List behaviors = arrangement.getBehaviorList();
		for (int i = 0; i < behaviors.size(); i++)
			imRenderer.addBehaviorImage((Behavior) behaviors.get(i));

	}

	/**
	 * Set the background image of this behavior network
	 * 
	 * @param imBackground
	 *            The new background image
	 */
	public void setImageBackground(Image imBackground) {
		if (iBehaviorNetImage == null)
			return;
		imRenderer.setImageWithBackground(imBackground);
		paintBehaviorNetwork(false);
		repaint();
	}

	/**
	 * Update the behavior network
	 * 
	 * @param bNet
	 *            The new behavior network
	 */
	public void updateBehaviorNetwork(BehaviorNetwork bNet) {
		if (bNet == null) {
			throw new IllegalArgumentException(
					"The network to update can not be null.");
		}
		arrangement = bNet;
		List behaviors = arrangement.getBehaviorList();
		for (int i = 0; i < behaviors.size(); i++) {
			Behavior b = (Behavior) behaviors.get(i);
			if (!imRenderer.behaviorWithBackgroundImageExists(b))
				imRenderer.addBehaviorImage(b);
		}
		paintBehaviorNetwork(false);
		repaint();
	}

	/**
	 * Paint components of this behavior network
	 */
	protected void paintComponent(Graphics g) {

		if (iBehaviorNetImage != null) {
			g.drawImage(iBehaviorNetImage, 0, 0, this);
		} else {
			g.setColor(getBackground());
			g.fillRect(0, 0, 280, 280);
			g.setColor(Color.blue);
			g.drawString("BehaviorNetPad", 10, 135);
		}

	}

	/**
	 * Draw all behaviors and coefficient edges of this behavior network
	 * 
	 * @param excludePressedEdges
	 *            Whether pressed edges should be excluded. True if the edges
	 *            should be exclused.
	 */
	protected void paintBehaviorNetwork(boolean excludePressedEdges) {
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
									.getPosition(row, col).equals(
											pressedPosition)))
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
		g.translate(distance * (1 - BehaviorPosition.NETWORK_SQUARES), distance
				* (1 - BehaviorPosition.NETWORK_SQUARES));

		Edge edges[] = arrangement.getAllEdges();
		for (int k = 0; k < edges.length; k++) {
			Edge e = edges[k];
			boolean keepDrawing = true;
			if (excludePressedEdges) {
				for (int n = 0; n < pressedBehaviorEdges.length; n++) {
					if (pressedBehaviorEdges[n] == e) {
						keepDrawing = false;
						break;
					}
				}
			}
			if (e.isVisible() && keepDrawing) {
				BehaviorPosition f = arrangement.getPosition(e.fromB());
				BehaviorPosition t = arrangement.getPosition(e.toB());
				int xFrom = f.getColumn() * distance + distance / 2;
				int yFrom = f.getRow() * distance + distance / 2;
				int xTo = t.getColumn() * distance + distance / 2;
				int yTo = t.getRow() * distance + distance / 2;
				// assume the radius of behavior node is 18.5
				Point fPoint = new Point(xFrom, yFrom);
				Point tPoint = new Point(xTo, yTo);
				double angle = tPoint.angle(fPoint);
				double delta = 0.35;
				if (e.inhibitionFT() != 0) {
					g.setColor(Color.GRAY);
					g.drawLine(xFrom - (int) (18.5 * Math.cos(angle + delta)),
							yFrom + (int) (18.5 * Math.sin(angle + delta)), xTo
									+ (int) (18.5 * Math.cos(angle - delta)),
							yTo - (int) (18.5 * Math.sin(angle - delta)));

				}
				if (e.inhibitionTF() != 0) {
					g.setColor(Color.GRAY);
					g.drawLine(xFrom - (int) (18.5 * Math.cos(angle - delta)),
							yFrom + (int) (18.5 * Math.sin(angle - delta)), xTo
									+ (int) (18.5 * Math.cos(angle + delta)),
							yTo - (int) (18.5 * Math.sin(angle + delta)));
				}
			}
		}
		g.dispose();
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 * a component.
	 */
	public void mouseClicked(MouseEvent e) {
		// if(e.getButton() == MouseEvent.BUTTON1)
		// return;
		// int x = e.getX();
		// int y = e.getY();
		// int distance = imRenderer.stdDistance();
		//
		// // position in Network diagram
		// int m = y/distance;
		// int n = x/distance;
		// //out of bounds check
		// if (m > BehaviorPosition.NETWORK_SQUARES - 1 || n >
		// BehaviorPosition.NETWORK_SQUARES - 1)
		// return;
		// Behavior behaviorToDelete =
		// arrangement.getBehavior(BehaviorPosition.getPosition(m, n));
		// if(behaviorToDelete != Behavior.NO_BEHAVIOR){
		// behaviorToDeleteName = new
		// String(behaviorToDelete.getBehaviorName());
		// behaviorToDeletePosition = BehaviorPosition.getPosition(m, n);
		// popupMenu.show(e.getComponent(), e.getX(), e.getY());
		// }

	}

	/** Get the behavior name to be deleted */

	// public String getBehaviorToDeleteName(){
	// return behaviorToDeleteName;
	// /** Set the behavior to be deleted */}
	// public void setBehaviorToDeleteName(String name){
	// behaviorToDeleteName = name;
	// /** Delete the specified behavior*/}
	// public void actionPerformed(ActionEvent e){
	// if(behaviorToDeletePosition != null){
	// // Remove the behavior
	// arrangement.removeBehavior(behaviorToDeletePosition);
	// // Update the current behavior network
	// ((BNCategory)engineRef.getCurrentEntity()).setBehaviorNetwork(arrangement);
	// // Update network GUI
	// try {	// paintBehaviorNetwork(false);
	// } catch(Exception ex) {
	// MessageUtils.debug(this, "paintBehaviorNetwork", ex);
	// };
	// fireActionEvent( new ActionEvent(this, 0, null));
	// }
	// // Update the tree
	// behaviorToDeletePosition = null;
	// if (listener != null)
	// listener.behaviorNetworkChanged(behaviorToDeleteName);
	// }
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {

		if (!isEnabled())
			return;
		mouseDragged = false;
		int x = e.getX();
		int y = e.getY();
		int distance = imRenderer.stdDistance();
		// position in Network diagram
		int m = y / distance;
		int n = x / distance;
		// out of bounds check
		if (m > BehaviorPosition.NETWORK_SQUARES - 1
				|| n > BehaviorPosition.NETWORK_SQUARES - 1)
			return;
		if (animateThread != null)
			if (animateThread.isAlive())
				return;

		synchronized (this) {

			pressedPosition = BehaviorPosition.getPosition(m, n);

			if (arrangement.getBehavior(pressedPosition) != Behavior.NO_BEHAVIOR) {
				pressedBehaviorEdges = arrangement
						.getEdgesForBehaviorNode(arrangement.getBehavior(
								pressedPosition).getMyId());
				Graphics g = iBackgroundImage.getGraphics();
				paintBehaviorNetwork(true);
				g.drawImage(iBehaviorNetImage, 0, 0, this);
				g.dispose();
				mouseActive = true;
			} else {
				mouseActive = false;
			}
		}

	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	public void mouseReleased(MouseEvent e) {
		if (!isEnabled())
			return;
		int x = e.getX();
		int y = e.getY();
		int distance = imRenderer.stdDistance();
		// position in the Network diagram
		int n = x / distance;
		int m = y / distance;
		// to do get the released position in the Network diagram
		if (!mouseActive)
			return;
		try {
			releasedPosition = BehaviorPosition.getPosition(m, n);
		} catch (Exception exception) {
			releasedPosition = null;
		}

		synchronized (this) {
			Graphics gBehavNet = iBehaviorNetImage.getGraphics();
			if (!mouseDragged) // pressed and released mouse without dragging
				;// gBehavNet.translate(xMousePrevPosition, yMousePrevPosition);
			else {
				gBehavNet.drawImage(iBackgroundImage, 0, 0, this);
			}
			if (releasedPosition != pressedPosition && releasedPosition != null) {
				if (arrangement.getBehavior(releasedPosition) == Behavior.NO_BEHAVIOR) {
					// behavior was moved into a new location - change
					// arrangement
					arrangement = arrangement.applyBehaviorMove(
							pressedPosition, releasedPosition);
					paintBehaviorNetwork(false);

					// update Behavior network in Application Engine
					fireActionEvent(new ActionEvent(this, 0, null));
				} else { // start animation or paintBehaviorNetwork();
					animateThread = new AnimateBehaviorMove(this,
							releasedPosition, pressedPosition);
					animateThread.start();
				}
			} else {
				paintBehaviorNetwork(false);
			}
			gBehavNet.dispose();
		}
		mouseDragged = false;
		mouseActive = false;
		repaint();
	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Invoked when the mouse drags a component.
	 */
	public void mouseDragged(MouseEvent e) {

		if (!isEnabled())
			return;

		if (mouseActive) {
			mouseDragged = true;

			int distance = imRenderer.stdDistance();
			int x = e.getX() - distance / 2;
			int y = e.getY() - distance / 2;
			synchronized (this) {

				Graphics gBehavNet = iBehaviorNetImage.getGraphics();
				gBehavNet.drawImage(iBackgroundImage, 0, 0, this);

				Graphics gBackground = iBackgroundImage.getGraphics();
				gBackground.drawImage(iBehaviorNetImage, 0, 0, this);
				gBehavNet.translate(x, y);
				Behavior pressedBehavior = arrangement
						.getBehavior(pressedPosition);
				gBehavNet.drawImage(imRenderer
						.getBehaviorFilteredImage(pressedBehavior), 0, 0, this);

				gBehavNet.translate(-x, -y);
				for (int k = 0; k < pressedBehaviorEdges.length; k++) {
					Edge edge = pressedBehaviorEdges[k];
					if (edge.isVisible()) {
						int xTo = distance / 2;
						int yTo = distance / 2;
						if (pressedBehavior.getMyId() == (
								edge.fromB().getMyId())) {
							BehaviorPosition t = arrangement.getPosition(edge
									.toB());
							xTo += t.getColumn() * distance;
							yTo += t.getRow() * distance;
						} else {
							BehaviorPosition f = arrangement.getPosition(edge
									.fromB());
							xTo += f.getColumn() * distance;
							yTo += f.getRow() * distance;
						}
						gBehavNet.drawLine(x + distance / 2, y + distance / 2,
								xTo, yTo);

					}
					;

					gBehavNet.dispose();
					gBackground.dispose();

				}
				repaint();
			}
		}
	}

	/**
	 * Add an action listener into the event handler
	 * 
	 * @param l
	 *            The action listener to be added
	 */
	public synchronized void addActionListener(ActionListener l) {
		if (l == null) {
			return;
		}
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

	/**
	 * Remove an action listener from the event handler
	 * 
	 * @param l
	 *            The action listener to be deleted
	 */
	public synchronized void removeActionListener(ActionListener l) {
		if (l == null) {
			return;
		}
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	/**
	 * Fire action event
	 * 
	 * @param e
	 *            The action event to be fired
	 */
	public void fireActionEvent(ActionEvent e) {
		if (actionListener != null)
			actionListener.actionPerformed(e);
	}

	/**
	 * @return the underlying behavior network
	 */
	public BehaviorNetwork currentNetwork() {
		return arrangement;
	}

}
