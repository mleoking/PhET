/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

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

	private final Point2D positionDelta;
	private final double rotationDelta;
	private final double sizeChangeFactor;
	private final double primaryImageIndexDelta;
	private final double secondaryImageIndexDelta;
	private final double fadeFactorDelta;
	
	/**
	 * Constructor.
	 * 
	 * @param positionDelta
	 * @param rotationDelta
	 * @param sizeChangeFactor
	 * @param primaryImageIndexDelta
	 * @param secondaryImageIndexDelta
	 * @param fadeFactorDelta
	 */
	public ModelAnimationDelta(Point2D positionDelta, double rotationDelta,
			double sizeChangeFactor, double primaryImageIndexDelta,
			double secondaryImageIndexDelta, double fadeFactorDelta) {
		super();
		this.positionDelta = positionDelta;
		this.rotationDelta = rotationDelta;
		this.sizeChangeFactor = sizeChangeFactor;
		this.primaryImageIndexDelta = primaryImageIndexDelta;
		this.secondaryImageIndexDelta = secondaryImageIndexDelta;
		this.fadeFactorDelta = fadeFactorDelta;
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

	public double getPrimaryImageIndexDelta() {
		return primaryImageIndexDelta;
	}

	public double getSecondaryImageIndexDelta() {
		return secondaryImageIndexDelta;
	}

	public double getFadeFactorDelta() {
		return fadeFactorDelta;
	}
}
