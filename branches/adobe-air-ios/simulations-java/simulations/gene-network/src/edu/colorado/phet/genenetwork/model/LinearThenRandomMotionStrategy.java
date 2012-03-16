// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that starts off linear and then becomes a random walk.
 * This tends to be useful when things need to get out of the way of one
 * another and then start moving randomly.
 * 
 * @author John Blanco
 */
public class LinearThenRandomMotionStrategy extends AbstractMotionStrategy {
	
	private static final double MAX_VEL_ON_TRANSITION = 5;
	private static final Random RAND = new Random();
	
	private RandomWalkMotionStrategy randomWalkStrategy;
	private LinearMotionStrategy linearMotionStrategy;
	private boolean movingLinearly = true;
	
	public LinearThenRandomMotionStrategy(Rectangle2D bounds, Point2D initialLocation, Vector2D initialVelocity, double timeBeforeTransition) {
		super();
		linearMotionStrategy = new LinearMotionStrategy(bounds, initialLocation, initialVelocity, timeBeforeTransition);
		randomWalkStrategy = new RandomWalkMotionStrategy(bounds);
	}

	@Override
	public void updatePositionAndMotion(double dt, SimpleModelElement modelElement) {
		if (movingLinearly){
			linearMotionStrategy.updatePositionAndMotion(dt, modelElement);
			if (linearMotionStrategy.isDestinationReached() || linearMotionStrategy.isBoundsReached()){
				// Time to switch to a random walk.
				movingLinearly = false;
				linearMotionStrategy = null;
				
				// Since the linear motion strategy stops the model element
				// when the destination is reached, we need to set some sort
				// of initial velocity or the element will appear to freeze.
				modelElement.setVelocity(MAX_VEL_ON_TRANSITION * RAND.nextDouble(),
						MAX_VEL_ON_TRANSITION * RAND.nextDouble());
			}
		}
		else{
			randomWalkStrategy.updatePositionAndMotion(dt, modelElement);
		}
	}
}
