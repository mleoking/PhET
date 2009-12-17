/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This is a motion strategy that moves to the specified location and then
 * comes back to the original location.
 * 
 * @author John Blanco
 */
public class ThereAndBackMotionStrategy extends LinearMotionStrategy {

	
	private static final double DEFAULT_VELOCITY = 10;
	
	private Point2D startingPoint = new Point2D.Double();
	private boolean outwardBound = true;
	private boolean tripCompleted = false;
	
	public ThereAndBackMotionStrategy(IModelElement modelElement, Point2D destination, Rectangle2D bounds) {
		super(modelElement, bounds, destination, DEFAULT_VELOCITY);
		startingPoint.setLocation(modelElement.getPositionRef());
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		super.updatePositionAndMotion(dt);
		if (outwardBound && isDestinationReached()){
			// We've reached the remote destination, time for the return trip.
			setDestination(startingPoint);
			outwardBound = false;
		}
	}
	
	public boolean isTripCompleted(){
		return tripCompleted;
	}
}
