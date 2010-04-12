package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
	private Rectangle2D motionBounds = new Rectangle2D.Double();
	private MembraneDiffusionModel model;
	
	/**
	 * Constructor.
	 * 
	 * @param initialLocation
	 * @param model - Model in the moveable element is being injected.  TODO:
	 * consider creating an interface with just the elements of the model that
	 * are needed here, which is primarily the bounds of the chambers.
	 * @param angle
	 */
	public InjectionMotionStrategy(Point2D initialLocation, MembraneDiffusionModel model, double angle) {
		this.initialLocation.setLocation(initialLocation);
		this.model = model;
		timeUntilNextJump = generateNewJumpTime();
		velocityVector.setComponents(MIN_INITIAL_VELOCITY, 0);
		currentLocation.setLocation(initialLocation);
		
		// Get the motion bounds set up for this strategy.
		updateMotionBounds();
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		
		// Bounce back toward the inside if we are outside of the motion bounds.
		if ((currentLocation.getX() > motionBounds.getMaxX() && velocityVector.getX() > 0) ||
			(currentLocation.getX() < motionBounds.getMinX() && velocityVector.getX() < 0))	{
			// Reverse direction in the X direction.
			velocityVector.setComponents(-velocityVector.getX(), velocityVector.getY());
		}
		if ((currentLocation.getY() > motionBounds.getMaxY() && velocityVector.getY() > 0) ||
    		(currentLocation.getY() < motionBounds.getMinY() && velocityVector.getY() < 0))	{
    		// Reverse direction in the Y direction.
			velocityVector.setComponents(velocityVector.getX(), -velocityVector.getY());
    	}

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
	
	private void updateMotionBounds(){
		if (currentLocation.getY() > model.getMembraneRect().getMaxY()){
			// Current location is above the membrane, so use the upper
			// portion of the particle chamber as the bounds.
			motionBounds.setFrame(model.getParticleChamberRect().getMinX(), model.getMembraneRect().getMaxY(),
					model.getParticleChamberRect().getWidth(), model.getParticleChamberRect().getMaxY() - model.getMembraneRect().getMaxY());
		}
		else if (currentLocation.getY() < model.getMembraneRect().getMinY()){
			// Current location is above the membrane, so use the upper
			// portion of the particle chamber as the bounds.
			motionBounds.setFrame(model.getParticleChamberRect().getMinX(), model.getParticleChamberRect().getMinY(),
					model.getParticleChamberRect().getWidth(), model.getMembraneRect().getMinY() - model.getParticleChamberRect().getMinY());
		}
		else {
			// The most likely case for encountering this clause is if the
			// particle is passing through the membrane, and the bounds should
			// not be undergoing an update at that time, so just assert.
			System.out.println(getClass().getName() + "Error: Particle is not in either sub-chamber.");
			assert false;
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
