/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that moves things around in a way that emulates the "random
 * walk" behavior exhibited by particles in a fluid, but that also moves
 * towards a destination point.
 * 
 * @author John Blanco
 */
public class WeightedRandomWalkMotionStrategy extends AbstractMotionStrategy {
	
	private static final Random RAND = new Random();
	private static final double DIRECTED_PROPORTION = 0.9; // Proportion of motion updates that move towards
	                                                       // the destination point.
	private static final int MOTION_UPDATE_PERIOD = 20;  // Number of update calls before changing direction.
	private static double MAX_VELOCITY = 0.4;  // In nanometers per update, I guess.  Weird.
	private static double MIN_VELOCITY = 0.1;  // In nanometers per update, I guess.  Weird.

	private Rectangle2D bounds;
	private int myUpdateValue;  // Used to stagger updates, for a better look and more even computational load.
	private int updateCount = 0;
	
	public WeightedRandomWalkMotionStrategy(IModelElement modelElement, Rectangle2D bounds, Point2D destination) {
		super(modelElement);
		this.bounds = bounds;
		if (destination != null){
			setDestination(destination.getX(),	destination.getY());
		}
		
		// Initialize the bin that is used to stagger updates to the motion.
		myUpdateValue = RAND.nextInt(MOTION_UPDATE_PERIOD);
	}

	public WeightedRandomWalkMotionStrategy(IModelElement modelElement, Rectangle2D bounds) {
		this(modelElement, bounds, null);
	}

	@Override
	public void updatePositionAndMotion() {
		IModelElement modelElement = getModelElement();
		
		Point2D position = modelElement.getPositionRef();
		Vector2D velocity = modelElement.getVelocityRef();
		
		if ((position.getX() > bounds.getMaxX() && velocity.getX() > 0) ||
			(position.getX() < bounds.getMinX() && velocity.getX() < 0))	{
			// Reverse direction in the X direction.
			modelElement.setVelocity(-velocity.getX(), velocity.getY());
		}
		if ((position.getY() > bounds.getMaxY() && velocity.getY() > 0) ||
    		(position.getY() < bounds.getMinY() && velocity.getY() < 0))	{
    		// Reverse direction in the Y direction.
    		modelElement.setVelocity(velocity.getX(), -velocity.getY());
    	}
		
		modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX(), 
				modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() );
		
		// See if it is time to change the motion and, if so, do it.
		if (updateCount == myUpdateValue){
	    	double angle = 0;
	    	double scalerVelocity;
	    	scalerVelocity = MIN_VELOCITY + (MAX_VELOCITY - MIN_VELOCITY) * RAND.nextDouble();
	    	if (getDestination() != null && RAND.nextDouble() < DIRECTED_PROPORTION){
	    		// Move towards the destination.
	        	angle = Math.atan2(getDestination().getY() - modelElement.getPositionRef().getY(), 
	        			getDestination().getX() - modelElement.getPositionRef().getX());
	    	}
	    	else{
	    		// Do the normal random walk.
	    		angle = Math.PI * 2 * RAND.nextDouble();
	    	}
			
			// Set the particle's new velocity. 
	    	modelElement.setVelocity(scalerVelocity * Math.cos(angle), scalerVelocity * Math.sin(angle));
		}
		
		// Update current bin.
		updateCount = (updateCount + 1) % MOTION_UPDATE_PERIOD;
	}
}
