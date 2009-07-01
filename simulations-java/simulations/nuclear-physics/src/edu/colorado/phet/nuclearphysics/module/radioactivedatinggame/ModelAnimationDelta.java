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

	private final double time;
	private final Point2D positionDelta;
	private final double rotationDelta;
	private final double sizeChangeFactor;
	private final int primaryImageIndexDelta;
	private final int secondaryImageIndexDelta;
	private final double fadeFactorDelta;
	
	/**
	 * Constructor.
	 * 
	 * @param time
	 * @param positionDelta
	 * @param rotationDelta
	 * @param sizeChangeFactor
	 * @param primaryImageIndexDelta
	 * @param secondaryImageIndexDelta
	 * @param fadeFactorDelta
	 */
	public ModelAnimationDelta(double time, Point2D positionDelta, double rotationDelta, double sizeChangeFactor,
			int primaryImageIndexDelta, int secondaryImageIndexDelta, double fadeFactorDelta) {
		super();
		this.time = time;
		this.positionDelta = positionDelta;
		this.rotationDelta = rotationDelta;
		this.sizeChangeFactor = sizeChangeFactor;
		this.primaryImageIndexDelta = primaryImageIndexDelta;
		this.secondaryImageIndexDelta = secondaryImageIndexDelta;
		this.fadeFactorDelta = fadeFactorDelta;
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
}
