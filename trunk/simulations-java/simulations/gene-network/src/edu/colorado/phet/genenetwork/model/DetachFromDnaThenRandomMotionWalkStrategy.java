package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class defines a motion strategy that causes an element to appear as
 * though it detaches from the DNA strand - meaning that it moves up a bit -
 * and then starts a random walk.  This arose due to model elements sometimes
 * detaching from the DNA and then moving right behind it, which didn't look
 * so good.
 * 
 * @author John Blanco
 */
public class DetachFromDnaThenRandomMotionWalkStrategy extends RandomWalkMotionStrategy {
	
	private static double MOVE_UP_TIME = 3; // In seconds.
	private static Random RAND = new Random();

	private double upwardMovementCounter = MOVE_UP_TIME;
	private Vector2D initialVelocity = new Vector2D.Double();
	
	public DetachFromDnaThenRandomMotionWalkStrategy( IModelElement modelElement, Rectangle2D bounds ) {
		super(modelElement, bounds);
		
		double initialTotalVelocity = RAND.nextDouble() * (MAX_VELOCITY - MIN_VELOCITY) + MIN_VELOCITY;
		double initialAngle = Math.PI / 4.0 + (RAND.nextDouble() * (Math.PI / 2.0));
		initialVelocity.setX(initialTotalVelocity * Math.cos(initialAngle));
		initialVelocity.setY(initialTotalVelocity * Math.sin(initialAngle));
	}

	@Override
	public void updatePositionAndMotion(double dt) {

		if (upwardMovementCounter > 0){
			upwardMovementCounter -= dt;
			Point2D currentPos = getModelElement().getPositionRef();
			getModelElement().setPosition( currentPos.getX() + initialVelocity.getX() * dt, 
					currentPos.getY() + initialVelocity.getY() * dt);
		}
		else{
			// Done moving up, so just do random walk.
			super.updatePositionAndMotion(dt);
		}
	}
}
