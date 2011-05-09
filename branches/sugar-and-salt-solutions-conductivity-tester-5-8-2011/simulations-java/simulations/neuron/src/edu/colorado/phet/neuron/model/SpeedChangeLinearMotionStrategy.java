// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This is a very specialized motion strategy that is basically a linear
 * motion but that starts at one speed and then changes to another.  It was
 * created for a very specific application - getting particles to move quickly
 * away from the exit of a channel with an inactivation gate, and then slowing
 * down.  It may have other applications.
 * 
 * @author John Blanco
 */
public class SpeedChangeLinearMotionStrategy extends MotionStrategy {
	
	private Vector2D velocityVector = new Vector2D();
	private double firstSpeedCountdownTimer = 0;
	private final double speedScaleFactor;
	
	public SpeedChangeLinearMotionStrategy( Vector2D initialVelocity, double speedScaleFactor, double timeAtFirstSpeed){
		this.velocityVector.setComponents(initialVelocity.getX(), initialVelocity.getY());
		this.speedScaleFactor = speedScaleFactor;
		this.firstSpeedCountdownTimer = timeAtFirstSpeed;
	}

	@Override
	public void move(IMovable movable, IFadable fadableModelElement, double dt) {
		movable.setPosition(movable.getPosition().getX() + velocityVector.getX() * dt, 
				movable.getPosition().getY() + velocityVector.getY() * dt);
		if (firstSpeedCountdownTimer > 0){
			firstSpeedCountdownTimer -= dt;
			if (firstSpeedCountdownTimer <= 0){
				// Scale the speed.
				velocityVector.scale(speedScaleFactor);
			}
		}
	}
}
