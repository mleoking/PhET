package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * A motion strategy for injecting particles into the particle chambers.  They
 * start out moving linearly with some randomness, and eventually become a
 * random walk.
 * 
 * @author John Blanco
 */
public class InjectionMotionStrategy extends MotionStrategy {

	private static final double MAX_JUMP_DISTANCE = 1; // In nanometers.
	private static final double MIN_JUMP_DISTANCE = 0.1;  // In nanometers.
	private static final double MIN_TIME_TO_NEXT_JUMP = 0.0001;  // In seconds of sim time, not wall time.
	private static final double MAX_TIME_TO_NEXT_JUMP = 0.0005;  // In seconds of sim time, not wall time.
	private static final Random RAND = new Random();
	private static final double MIN_INITIAL_VELOCITY = 10000;
	private static final double MAX_INITIAL_VELOCITY = 20000;
	
	private final Point2D initialLocation = new Point2D.Double();
	private double timeUntilNextJump; // In seconds of sim time.
	private Vector2D velocityVector = new Vector2D.Double();
	private Point2D currentLocation = new Point2D.Double();
	
	public InjectionMotionStrategy(Point2D initialLocation, double angle) {
		this.initialLocation.setLocation(initialLocation);
		timeUntilNextJump = generateNewJumpTime();
		velocityVector.setComponents(MIN_INITIAL_VELOCITY, 0);
		currentLocation.setLocation(initialLocation);
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		
		currentLocation.setLocation(currentLocation.getX() + velocityVector.getX() * dt,
				currentLocation.getY() + velocityVector.getY() * dt);
		movableModelElement.setPosition(currentLocation);
		
		/*
		timeUntilNextJump -= dt;
		if (timeUntilNextJump <= 0){
			// It is time to jump.
			if (movableModelElement.getPosition().equals(initialLocation)){
				// Jump away from this location.
				double jumpAngle = generateNewJumpAngle();
				double jumpDistance = generateNewJumpDistance();
				Point2D currentPos = movableModelElement.getPosition();
				movableModelElement.setPosition(currentPos.getX() + jumpDistance * Math.cos(jumpAngle),
						currentPos.getY() + jumpDistance * Math.sin(jumpAngle));
			}
			else{
				// Jump back to initial location.
				movableModelElement.setPosition(initialLocation);
			}
			
			// Reset the jump counter time.
			timeUntilNextJump = generateNewJumpTime();
		}
		*/
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
