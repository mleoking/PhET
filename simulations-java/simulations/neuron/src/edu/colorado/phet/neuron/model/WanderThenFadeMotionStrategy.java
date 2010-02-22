package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.neuron.module.NeuronDefaults;


/**
 * A motion strategy that has a particle wander around for a while and then
 * fade out of existence.
 * 
 * @author John Blanco
 */
public class WanderThenFadeMotionStrategy extends MotionStrategy {

	private static final Random RAND = new Random();
	private static final int CLOCK_TICKS_BEFORE_MOTION_UPDATE = 10;
	private static final int CLOCK_TICKS_BEFORE_VELOCITY_UPDATE = CLOCK_TICKS_BEFORE_MOTION_UPDATE * 10;
	private static final double MOTION_UPDATE_PERIOD = NeuronDefaults.CLOCK_DT * CLOCK_TICKS_BEFORE_MOTION_UPDATE;
	private static final double VELOCITY_UPDATE_PERIOD = NeuronDefaults.CLOCK_DT * CLOCK_TICKS_BEFORE_VELOCITY_UPDATE;
	private static final double MIN_VELOCITY = 100;  // In nanometers per second of sim time.
	private static final double MAX_VELOCITY = 1000; // In nanometers per second of sim time.
	
	public enum InOrOut {INSIDE, OUTSIDE};
	
	private final double radius;
	private final Point2D centerOfCircle;
	private final InOrOut inOrOut;
	
	private double motionUpdateCountdownTimer;
	private double velocityUpdateCountdownTimer;
	private Vector2D velocity = new Vector2D.Double();
	
	public WanderThenFadeMotionStrategy(double radius, Point2D center, Point2D currentLocation, InOrOut inOrOut) {
		this.centerOfCircle = center;
		this.radius = radius;
		this.inOrOut = inOrOut;
		
		// Set up random offsets so that all the particles using this motion
		// strategy don't all get updated at the same time.
		motionUpdateCountdownTimer = RAND.nextInt(CLOCK_TICKS_BEFORE_MOTION_UPDATE) * NeuronDefaults.CLOCK_DT;
		velocityUpdateCountdownTimer = RAND.nextInt(CLOCK_TICKS_BEFORE_VELOCITY_UPDATE) * NeuronDefaults.CLOCK_DT;
		
		// Set an initial velocity and direction.
		updateVelocity(currentLocation);
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		
		motionUpdateCountdownTimer -= dt;
		if (motionUpdateCountdownTimer <= 0){
			// Time to update the motion.
			movableModelElement.setPosition(
					movableModelElement.getPosition().getX() + velocity.getX() * MOTION_UPDATE_PERIOD,
					movableModelElement.getPosition().getY() + velocity.getY() * MOTION_UPDATE_PERIOD);
			
			motionUpdateCountdownTimer = MOTION_UPDATE_PERIOD;
		}
		
		velocityUpdateCountdownTimer -= dt;
		if (velocityUpdateCountdownTimer <= 0){
			// Time to update the velocity.
			updateVelocity(movableModelElement.getPosition());
			velocityUpdateCountdownTimer = VELOCITY_UPDATE_PERIOD;
		}
	}
	
	private void updateVelocity(Point2D currentPosition){
		
		if (inOrOut == InOrOut.INSIDE && centerOfCircle.distance(currentPosition) > radius){
			// The particle has wandered outside the allowable radius, so send
			// it back towards the center until the next update occurs.
			double angle = Math.atan2(currentPosition.getY() - centerOfCircle.getY(),
					currentPosition.getX() - currentPosition.getY());
			double newScalerVelocity = MIN_VELOCITY + RAND.nextDouble() * (MAX_VELOCITY - MIN_VELOCITY);
			velocity.setComponents(newScalerVelocity * Math.cos(angle), newScalerVelocity * Math.sin(angle));
		}
		else if (inOrOut == InOrOut.OUTSIDE && centerOfCircle.distance(currentPosition) < radius){
			// The particle has wandered inside the excluded radius, so send
			// it away from the center until the next update occurs.
			double angle = Math.atan2(currentPosition.getY() - centerOfCircle.getY(),
					currentPosition.getX() - currentPosition.getY()) + Math.PI;
			double newScalerVelocity = MIN_VELOCITY + RAND.nextDouble() * (MAX_VELOCITY - MIN_VELOCITY);
			velocity.setComponents(newScalerVelocity * Math.cos(angle), newScalerVelocity * Math.sin(angle));
		}
		else{
			// The particle is safely outside or inside the radius, so just
			// pick a random direction.
			velocity.setComponents(1, 1);
			velocity.scale(MIN_VELOCITY + RAND.nextDouble() * (MAX_VELOCITY - MIN_VELOCITY));
			velocity.rotate(RAND.nextDouble() * 2 * Math.PI);
		}
	}
}
