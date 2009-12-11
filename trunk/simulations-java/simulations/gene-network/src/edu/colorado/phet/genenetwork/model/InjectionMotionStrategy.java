/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that moves seeks to look like something that was injected,
 * and thus moves linearly for a while, slows down, and then goes to a random
 * walk.
 * 
 * @author John Blanco
 */
public class InjectionMotionStrategy extends AbstractMotionStrategy {
	
	private static final double RATE_OF_SLOWING = 0.3;  // Proportion per second.
	private static final double LINEAR_TIME = 1.5;        // In seconds.
	
	private Rectangle2D bounds;
	private double linearMotionCountdown = LINEAR_TIME;
	private RandomWalkMotionStrategy randomWalkMotionStrategy;
	
	public InjectionMotionStrategy(IModelElement modelElement, Rectangle2D bounds, Vector2D initialVelocity) {
		super(modelElement);
		this.bounds = bounds;
		getModelElement().setVelocity(initialVelocity);
		randomWalkMotionStrategy = new RandomWalkMotionStrategy(modelElement, bounds);
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		IModelElement modelElement = getModelElement();
		
		Point2D position = modelElement.getPositionRef();
		Vector2D velocity = modelElement.getVelocityRef();
		
		if (linearMotionCountdown > 0){
			if ((position.getX() > bounds.getMaxX() && velocity.getX() > 0) ||
					(position.getX() < bounds.getMinX() && velocity.getX() < 0) ||
					(position.getY() > bounds.getMaxY() && velocity.getY() > 0) ||
					(position.getY() < bounds.getMinY() && velocity.getY() < 0)){
				
				// We are at or past the boundary, so stop forward motion.
				modelElement.setVelocity(0, 0);
			}
			
			if (modelElement.getVelocityRef().getMagnitude() > 0){
				// Update position.
				modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX() * dt, 
						modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() * dt );
				// Slow down.
				velocity.scale(1 - (RATE_OF_SLOWING * dt));
			}
			linearMotionCountdown -= dt;
		}
		else{
			randomWalkMotionStrategy.doUpdatePositionAndMotion(dt);
		}
	}
	
	public boolean isDestinationReached(){
		return (getModelElement().getPositionRef().distance(getDestination()) == 0);
	}
}
