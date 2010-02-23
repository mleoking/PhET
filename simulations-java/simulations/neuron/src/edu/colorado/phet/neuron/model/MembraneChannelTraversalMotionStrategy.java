package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * A motion strategy for traversing a membrane channel, e.g. going from
 * outside the cell to inside.
 * 
 * @author John Blanco
 */
public class MembraneChannelTraversalMotionStrategy extends MotionStrategy {

	private static final Random RAND = new Random();
	private static final double DEFAULT_MAX_VELOCITY = 40000; // Velocity that particles move, in nm/sec (sim time).
	
	private final MembraneChannel channel; // Channel through which to move. 
	
	private Vector2D velocityVector = new Vector2D.Double();
	private ArrayList<Point2D> traversalPoints;
	private int currentDestinationIndex = 0;
	private boolean channelHasBeenEntered = false; // Flag that is set when the channel is entered.
	private double maxVelocity;
	
	public MembraneChannelTraversalMotionStrategy(MembraneChannel channel, Point2D startingLocation, double maxVelocity) {
		this.channel = channel;
		this.maxVelocity = maxVelocity;
		traversalPoints = channel.getTraversalPoints(startingLocation);
		currentDestinationIndex = 0;
		setCourseForCurrentTraversalPoint(startingLocation);
	}

	public MembraneChannelTraversalMotionStrategy(MembraneChannel channel, Point2D startingLocation) {
		this(channel, startingLocation, DEFAULT_MAX_VELOCITY);
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		
		Point2D currentPosition = movableModelElement.getPosition();
		
		if (!channelHasBeenEntered){
			// Update the flag the tracks whether this particle has made it
			// to the channel and started traversing it.
			channelHasBeenEntered = channel.isPointInChannel(currentPosition);
		}
		
		if (channel.isOpen() || channelHasBeenEntered){
			// The channel is open, or we are inside it or have gone all the
			// way through, so keep executing this motion strategy.
			if ( currentDestinationIndex >= traversalPoints.size() || maxVelocity * dt < currentPosition.distance(traversalPoints.get(currentDestinationIndex))){
				// Move according to the current velocity.
				movableModelElement.setPosition(currentPosition.getX() + velocityVector.getX() * dt,
						currentPosition.getY() + velocityVector.getY() * dt);
			}
			else{
				// We are close enough to the destination that we should just
				// position ourself there and update to the next traversal point.
				movableModelElement.setPosition(traversalPoints.get(currentDestinationIndex));
				currentDestinationIndex++;
				setCourseForCurrentTraversalPoint(movableModelElement.getPosition());
				if (currentDestinationIndex == traversalPoints.size()){
					// We have traversed through all points and are now
					// presumably on the other side of the membrane, so we need to
					// start fading out of existence.
					fadableModelElement.setFadeStrategy(new TimedFadeAwayStrategy(0.002));
					
					// Slow down.
					velocityVector.scale(0.2);
				}
			}
		}
		else{
			// The channel has closed and this element has not yet entered it.
			// Time to override this motion strategy with a different one.
			movableModelElement.setMotionStrategy(new WanderAwayThenFadeMotionStrategy(channel.getCenterLocation(),
					movableModelElement.getPosition(), 0.001, 0.001));
		}
	}
	
	private void setCourseForCurrentTraversalPoint(Point2D currentLocation){
		if (currentDestinationIndex < traversalPoints.size()){
			Point2D dest = traversalPoints.get(currentDestinationIndex);
			velocityVector.setComponents(dest.getX() - currentLocation.getX(), dest.getY() - currentLocation.getY());
			double scaleFactor = maxVelocity / velocityVector.getMagnitude();
			velocityVector.scale(scaleFactor);
		}
		else{
			// All points have been traversed.  The behavior at this point is
			// to make a random change to the direction of travel so that
			// things look a little "Brownian".  The severity of the allowed
			// angle depends on the velocity.
			velocityVector.rotate((RAND.nextDouble() - 0.5) * ( Math.PI * 0.9 ) * maxVelocity / DEFAULT_MAX_VELOCITY);
		}
	}
}
