package edu.colorado.phet.membranediffusion.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * A motion strategy for making a particle (or whatever) perform a random
 * walk, meaning that it moves in one direction for a bit and then changes
 * direction.
 * 
 * @author John Blanco
 */
public class RandomWalkMotionStrategy extends MotionStrategy {

	private static final Random RAND = new Random();
	
	// Parameters that control the way that injected particles change course
	// over time.  These can be adjusted to achieve the desired appearance.
	private static final double MIN_TIME_BETWEEN_VELOCITY_UPDATES = 0.0002;  // In seconds of sim time, not wall time.
	private static final double MAX_TIME_BETWEEN_VELOCITY_UPDATES = 0.0010;  // In seconds of sim time, not wall time.
	private static final double MIN_VELOCITY = 12000;
	private static final double MAX_VELOCITY = 25000;
	
	private double timeUntilNextVelocityChange; // In seconds of sim time.
	private Vector2D velocityVector = new Vector2D.Double(0, 0);
	private Point2D currentLocation = new Point2D.Double();
	private Rectangle2D motionBounds = new Rectangle2D.Double();
	
	/**
	 * Constructor.
	 * 
	 * @param motionBounds
	 * @param angle - Initial injection angle, in radians.
	 */
	public RandomWalkMotionStrategy(Rectangle2D motionBounds) {
		this.motionBounds.setFrame(motionBounds.getMinX(), motionBounds.getMinY(), motionBounds.getWidth(),
				motionBounds.getHeight());
		timeUntilNextVelocityChange = generateNewVelocityChangeTime();
		changeVelocityVector();
	}

	@Override
	public void move(IMovable movableModelElement, double dt) {
		
		currentLocation.setLocation(movableModelElement.getPositionReference());
		double radius = movableModelElement.getRadius();
		
		// Bounce back toward the inside if we are outside of the motion bounds.
		if ((currentLocation.getX() + radius > motionBounds.getMaxX() && velocityVector.getX() > 0) ||
			(currentLocation.getX() - radius < motionBounds.getMinX() && velocityVector.getX() < 0)) {
			// Reverse direction in the X direction.
			velocityVector.setComponents(-velocityVector.getX(), velocityVector.getY());
		}
		if ((currentLocation.getY() + radius > motionBounds.getMaxY() && velocityVector.getY() > 0) ||
    		(currentLocation.getY() - radius < motionBounds.getMinY() && velocityVector.getY() < 0)) {
    		// Reverse direction in the Y direction.
			velocityVector.setComponents(velocityVector.getX(), -velocityVector.getY());
    	}

		// Update the position of the model element.
		currentLocation.setLocation(currentLocation.getX() + velocityVector.getX() * dt,
				currentLocation.getY() + velocityVector.getY() * dt);
		movableModelElement.setPosition(currentLocation);
		
		// Is it time to change direction?
		timeUntilNextVelocityChange -= dt;
		if (timeUntilNextVelocityChange <= 0){
			// Yes it is, so change the velocity vector.
			changeVelocityVector();
			
			// Reset the countdown.
			timeUntilNextVelocityChange = generateNewVelocityChangeTime();
		}
	}

	private double generateNewVelocityChangeTime(){
		return MIN_TIME_BETWEEN_VELOCITY_UPDATES + RAND.nextDouble() * (MAX_TIME_BETWEEN_VELOCITY_UPDATES - MIN_TIME_BETWEEN_VELOCITY_UPDATES);
	}
	
	/**
	 * Change the velocity vector in a random way in order to simulate
	 * collisions with other particles.
	 */
	private void changeVelocityVector(){
		double rotationAngle = RAND.nextDouble() * Math.PI * 2;
		double magnitude = MIN_VELOCITY + RAND.nextDouble() * (MAX_VELOCITY - MIN_VELOCITY);
		velocityVector.setComponents(magnitude * Math.cos(rotationAngle), magnitude * Math.sin(rotationAngle));
	}
}
