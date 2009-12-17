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
public class ThereAndBackMotionStrategy extends AbstractMotionStrategy {

	
	private static final double DEFAULT_VELOCITY = 10;
	
	private Point2D startingPoint = new Point2D.Double();
	private boolean outwardBound = true;
	private boolean tripCompleted = false;
	private LinearMotionStrategy linearMotionStrategy;
	
	public ThereAndBackMotionStrategy(IModelElement modelElement, Point2D turnAroundPoint, Rectangle2D bounds) {
		super(modelElement, bounds);
		linearMotionStrategy = new LinearMotionStrategy(modelElement, bounds, turnAroundPoint, DEFAULT_VELOCITY);
		startingPoint.setLocation(modelElement.getPositionRef());
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		linearMotionStrategy.updatePositionAndMotion(dt);
		if (outwardBound){
			if (linearMotionStrategy.isDestinationReached()){
				// We've reached the remote destination, time for the return trip.
				linearMotionStrategy = new LinearMotionStrategy(getModelElement(), getBounds(), startingPoint, DEFAULT_VELOCITY);
				outwardBound = false;
			}	
		}
		else{
			if (linearMotionStrategy.isDestinationReached()){
				tripCompleted = true;
			}
		}
	}
	
	public boolean isTripCompleted(){
		return tripCompleted;
	}
}
