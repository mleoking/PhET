/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class defines a specialize motion strategy that makes an element
 * appear to traverse horizontally, then move upward, and then into a random
 * walk.  This is meant to simulate motion that looks like traversal of a
 * strand of DNA.
 * 
 * NOTE: This assumes that the model element is already attached to the DNA.
 * 
 * @author John Blanco
 */
public class TraverseDnaMotionStrategy extends AbstractMotionStrategy {
	
	private static final double DNA_TRAVERSAL_SPEED = 6; // In nanometers per second.
	private LinearMotionStrategy linearMotionStrategy;
	private DetachFromDnaThenRandomMotionWalkStrategy detachStrategy;
	private boolean attached;
	
	public TraverseDnaMotionStrategy(IModelElement modelElement, Rectangle2D bounds, double traversalDistance) {
		
		super(modelElement, bounds);
		
		// Create two motion strategies - one to use initially, and one to use
		// once the DNA has been traversed.
		Point2D detachPoint = new Point2D.Double(getModelElement().getPositionRef().getX() + traversalDistance, 
				getModelElement().getPositionRef().getY());
		linearMotionStrategy = new LinearMotionStrategy(modelElement, bounds, detachPoint, DNA_TRAVERSAL_SPEED);
		detachStrategy = new DetachFromDnaThenRandomMotionWalkStrategy(modelElement, bounds);
		attached = true;
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		if (attached){
			linearMotionStrategy.updatePositionAndMotion(dt);
			if ( linearMotionStrategy.isDestinationReached() ){
				// Time to detach.
				attached = false;
			}
		}
		else{
			detachStrategy.updatePositionAndMotion(dt);
		}
	}
	
}
