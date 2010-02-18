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
	
	private final MembraneChannel channel; // Channel through which to move. 
	private final double velocity;         // Scaler velocity, in nanometers per second of simulation time.
	
	private Vector2D velocityVector = new Vector2D.Double();
	private ArrayList<Point2D> traversalPoints;
	private int currentDestinationIndex = 0;
	
	public MembraneChannelTraversalMotionStrategy(MembraneChannel channel, double velocity, Point2D startingLocation) {
		this.channel = channel;
		this.velocity = velocity;
		traversalPoints = channel.getTraversalPoints(startingLocation);
		currentDestinationIndex = 0;
		setCourseForCurrentTraversalPoint(startingLocation);
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		Point2D currentPosition = movableModelElement.getPosition();
		if ( currentDestinationIndex >= traversalPoints.size() || velocity * dt < currentPosition.distance(traversalPoints.get(currentDestinationIndex))){
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
				// We have traverse through all points and are now
				// presumably on the other side of the membrane, so we need to
				// start fading out of existence.
				fadableModelElement.setFadeStrategy(new FadeAwayStrategy(0.001));
			}
		}
	}
	
	private void setCourseForCurrentTraversalPoint(Point2D currentLocation){
		if (currentDestinationIndex < traversalPoints.size()){
			Point2D dest = traversalPoints.get(currentDestinationIndex);
			velocityVector.setComponents(dest.getX() - currentLocation.getX(), dest.getY() - currentLocation.getY());
			double scaleFactor = velocity / velocityVector.getMagnitude();
			velocityVector.scale(scaleFactor);
		}
		else{
			// All points have been traversed.  The behavior at this point is
			// to make a random change to the direction of travel so that
			// things look a little "Brownian".
			velocityVector.rotate((RAND.nextDouble() - 0.5) * ( Math.PI / 2 ));
		}
	}
}
