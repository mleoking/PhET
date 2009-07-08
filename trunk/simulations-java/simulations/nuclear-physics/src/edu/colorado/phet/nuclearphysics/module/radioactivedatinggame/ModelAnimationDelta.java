/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.EventObject;

/**
 * This class defines an "Animation Delta" for a model element, which means
 * that it defines some change to the position, rotational angle, etc. of an
 * animated (i.e. moving and otherwise changing in appearance) model element.
 * The general idea is to use a bunch of these in a sequence to animate an
 * appropriately defined model element.
 * 
 * @author John Blanco
 */
public class ModelAnimationDelta {

	private final double time;
	private final Point2D positionDelta;
	private final double rotationDelta;
	private final double sizeChangeFactor;
	private final int primaryImageIndexDelta;
	private final int secondaryImageIndexDelta;
	private final double fadeFactorDelta;
	private final EventObject event;
	
	/**
	 * Constructor.
	 * 
	 * @param time - The time at which the event should occur.
	 * @param positionDelta - Change to the element's position.  Null means no change.
	 * @param rotationDelta - Change to the element's rotational position.  Zero (0) means no change.
	 * @param sizeChangeFactor - Change to the element's size.  One (1) means no change.
	 * @param primaryImageIndexDelta - Change to the index of the primary image.  Zero (0) means no change.
	 * @param secondaryImageIndexDelta - Change to the index of the secondary image.  Zero (0) means no change.
	 * @param fadeFactorDelta - Change to the fade factor between the primary and secondary images.  Zero (0) means
	 * no change.
	 * @param event - Arbitrary event that can be fired to any listeners, generally used to synchronize outside
	 * model events with the animation sequence.
	 */
	public ModelAnimationDelta(double time, Point2D positionDelta, double rotationDelta, double sizeChangeFactor,
			int primaryImageIndexDelta, int secondaryImageIndexDelta, double fadeFactorDelta, EventObject event) {
		this.time = time;
		this.positionDelta = positionDelta == null ? null : new Point2D.Double(positionDelta.getX(), positionDelta.getY());
		this.rotationDelta = rotationDelta;
		this.sizeChangeFactor = sizeChangeFactor;
		this.primaryImageIndexDelta = primaryImageIndexDelta;
		this.secondaryImageIndexDelta = secondaryImageIndexDelta;
		this.fadeFactorDelta = fadeFactorDelta;
		this.event = event;
	}

	public double getTime() {
		return time;
	}

	public Point2D getPositionDelta() {
		return positionDelta;
	}

	public double getRotationDelta() {
		return rotationDelta;
	}

	public double getSizeChangeFactor() {
		return sizeChangeFactor;
	}

	public int getPrimaryImageIndexDelta() {
		return primaryImageIndexDelta;
	}

	public int getSecondaryImageIndexDelta() {
		return secondaryImageIndexDelta;
	}

	public double getFadeFactorDelta() {
		return fadeFactorDelta;
	}
	
	public EventObject getEvent() {
		return event;
	}
}
