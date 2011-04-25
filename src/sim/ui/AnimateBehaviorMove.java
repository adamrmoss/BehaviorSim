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
import java.awt.Image;

import sim.model.behavior.Behavior;
import sim.model.behavior.BehaviorPosition;

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
public class AnimateBehaviorMove extends Thread {
	private BehaviorNetPad pad = null;
	private BehaviorPosition startPosition = null;
	private BehaviorPosition endPosition = null;
	private int delay = 15;

	public AnimateBehaviorMove(BehaviorNetPad p, BehaviorPosition start,
			BehaviorPosition end) {
		super();
		pad = p;
		startPosition = start;
		endPosition = end;
	}

	public void run() {
		int distance = pad.getImageRenderer().stdDistance();

		int deltaX = endPosition.getColumn() - startPosition.getColumn();
		int deltaY = endPosition.getRow() - startPosition.getRow();

		int startX = startPosition.getColumn() * distance;
		int startY = startPosition.getRow() * distance;
		int currentX = startX;
		int currentY = startY;

		// determine the behavior to animate
		Behavior behavior = pad.currentNetwork().getBehavior(endPosition);
		Image im = pad.getImageRenderer().getBehaviorFilteredImage(behavior);
		Image bgIm = pad.getImageRenderer().getBehaviorWithBackgroundImage(
				pad.currentNetwork().getBehavior(startPosition));
		Image background = pad.createImage(distance, distance);
		Graphics bgGraphics = background.getGraphics();
		bgGraphics.drawImage(bgIm, 0, 0, pad);
		Graphics padGraphics = pad.getBehaviorNetImage().getGraphics();

		try {
			synchronized (pad) {
				padGraphics.translate(startX, startY);
				for (int i = 0; i < distance; i++) {
					padGraphics.drawImage(background, 0, 0, pad);
					currentX += deltaX;
					currentY += deltaY;

					bgGraphics.drawImage(pad.getBehaviorNetImage(), -currentX,
							-currentY, pad);
					padGraphics.translate(deltaX, deltaY);
					padGraphics.drawImage(im, 0, 0, pad);
					pad.repaint();
					pad.wait(delay);
				}
			}
		} catch (Exception x) {
		} finally {
			padGraphics.dispose();
			bgGraphics.dispose();
			pad.repaint();
		}

	}

}
