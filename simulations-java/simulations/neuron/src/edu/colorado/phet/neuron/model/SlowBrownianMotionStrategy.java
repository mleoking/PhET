// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * A motion strategy for showing some slow Brownian motion, which is basically
 * just an occasional small jump from its initial location to a new nearby
 * location and then back.  This is intended to create noticable but
 * non-distracting motion that doesn't consume much processor time.
 * 
 * @author John Blanco
 */
public class SlowBrownianMotionStrategy extends MotionStrategy {

	private static final double MAX_JUMP_DISTANCE = 1; // In nanometers.
	private static final double MIN_JUMP_DISTANCE = 0.1;  // In nanometers.
	private static final double MIN_TIME_TO_NEXT_JUMP = 0.0009;  // In seconds of sim time, not wall time.
	private static final double MAX_TIME_TO_NEXT_JUMP = 0.0015;  // In seconds of sim time, not wall time.
	private static final Random RAND = new Random();
	
	private final Point2D initialLocation = new Point2D.Double();
	private double timeUntilNextJump; // In seconds of sim time.
	
	public SlowBrownianMotionStrategy(Point2D initialLocation) {
		this.initialLocation.setLocation(initialLocation);
		timeUntilNextJump = generateNewJumpTime();
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		timeUntilNextJump -= dt;
		if (timeUntilNextJump <= 0){
			// It is time to jump.
			if (movableModelElement.getPosition().equals(initialLocation)){
				// Jump away from this location.
				double jumpAngle = generateNewJumpAngle();
				double jumpDistance = generateNewJumpDistance();
				Point2D currentPosRef = movableModelElement.getPositionReference();
				movableModelElement.setPosition(currentPosRef.getX() + jumpDistance * Math.cos(jumpAngle),
						currentPosRef.getY() + jumpDistance * Math.sin(jumpAngle));
			}
			else{
				// Jump back to initial location.
				movableModelElement.setPosition(initialLocation);
			}
			
			// Reset the jump counter time.
			timeUntilNextJump = generateNewJumpTime();
		}
	}
	
	private double generateNewJumpTime(){
		return MIN_TIME_TO_NEXT_JUMP + RAND.nextDouble() * (MAX_TIME_TO_NEXT_JUMP - MIN_TIME_TO_NEXT_JUMP);
	}
	
	private double generateNewJumpDistance(){
		return MIN_JUMP_DISTANCE + RAND.nextDouble() * (MAX_JUMP_DISTANCE - MIN_JUMP_DISTANCE);
	}
	
	private double generateNewJumpAngle(){
		return RAND.nextDouble() * Math.PI * 2;
	}
}
