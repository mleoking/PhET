package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * A motion strategy for traversing through a dual-gate channel, meaning one
 * that has a gate and an inactivation level.
 * 
 * This strategy makes several assumptions about the nature of the dual-gate
 * channel and how it is portrayed.  These assumptions depend both on the
 * model representation and the view representation of the dual-gated channel.
 * If changes are made to either, this class would need to be revised to
 * handle them.
 * 
 * @author John Blanco
 */
public class DualGateChannelTraversalMotionStrategy extends MembraneTraversalMotionStrategy {
	
	private static final Random RAND = new Random();
	private Vector2D velocityVector = new Vector2D.Double();
	private ArrayList<Point2D> traversalPoints;
	private int currentDestinationIndex = 0;
	private boolean channelHasBeenEntered = false; // Flag that is set when the channel is entered.
	private double maxVelocity;
	protected final MembraneChannel channel;

	public DualGateChannelTraversalMotionStrategy(MembraneChannel channel, Point2D startingLocation, double maxVelocity) {
		this.channel = channel;
		this.maxVelocity = maxVelocity;
		traversalPoints = createTraversalPoints(channel, startingLocation);
		currentDestinationIndex = 0;
		setCourseForCurrentTraversalPoint(startingLocation);
	}

	public DualGateChannelTraversalMotionStrategy(MembraneChannel channel, Point2D startingLocation) {
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
					
					// Slow down the speed.  Don't do this if it is already
					// moving pretty slowly.
					if (maxVelocity / DEFAULT_MAX_VELOCITY >= 0.5){
						if (channel.getHasInactivationGate()){
							// Scale less for inactivaction gate versions,
							// since otherwise it looks like ions go through
							// the gate.
							velocityVector.scale(0.4);
						}
						else{
							velocityVector.scale(0.2);
						}
					}
				}
			}
		}
		else{
			// The channel has closed and this element has not yet entered it.
			// Time to replace this motion strategy with a different one.
			movableModelElement.setMotionStrategy(new WanderAwayThenFadeMotionStrategy(channel.getCenterLocation(),
					movableModelElement.getPosition(), 0, 0.002));
		}
	}
	
	/**
	 * Create the points through which a particle must move when traversing
	 * this channel.
	 * 
	 * @param channel
	 * @param startingLocation
	 * @return
	 */
	private ArrayList<Point2D> createTraversalPoints(MembraneChannel channel, Point2D startingLocation){
		
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		Point2D ctr = channel.getCenterLocation();
		double r = channel.getChannelSize().getHeight() * 0.6; // Make the point a little outside the channel.
		Point2D outerOpeningLocation = new Point2D.Double(ctr.getX() + Math.cos(channel.getRotationalAngle()) * r,
				ctr.getY() + Math.sin(channel.getRotationalAngle()) * r);
		Point2D innerOpeningLocation = new Point2D.Double(ctr.getX() - Math.cos(channel.getRotationalAngle()) * r,
				ctr.getY() - Math.sin(channel.getRotationalAngle()) * r);

		if (startingLocation.distance(innerOpeningLocation) < startingLocation.distance(outerOpeningLocation)){
			points.add(innerOpeningLocation);
			points.add(outerOpeningLocation);
		}
		else{
			points.add(outerOpeningLocation);
			points.add(innerOpeningLocation);
		}

		return points;
	}
	
	private void setCourseForCurrentTraversalPoint(Point2D currentLocation){
		if (currentDestinationIndex < traversalPoints.size()){
			Point2D dest = traversalPoints.get(currentDestinationIndex);
			velocityVector.setComponents(dest.getX() - currentLocation.getX(), dest.getY() - currentLocation.getY());
			double scaleFactor = maxVelocity / velocityVector.getMagnitude();
			velocityVector.scale(scaleFactor);
		}
		else{
			// All points have been traversed.  The behavior at this point
			// depends on whether the channel has an inactivation gate, since
			// such a gate is depicted on the cell-interior side of the
			// channel in this sim.  No matter whether such a gate exists or
			// not, the particle is re-routed a bit in order to create a bit
			// of a brownian look.  If the gate exists, there are more
			// limitations to where the particle can go.
			if (channel.getHasInactivationGate()){
				// NOTE: The following is tweaked to work with a particular
				// visual representation of the inactivation gate, and may
				// need to be changed if that representation is changed.
				double velocityRotationAngle = 0;
				double minRotation = 0;
				double maxRotation = 0;
				if (RAND.nextDouble() > 0.3){
					// Move out to the right (assuming channel is vertical).
					// The angle at which we can move gets more restricted
					// as the inactivation gate closes.
					maxRotation = Math.PI * 0.4;
					double angularRange = (1 - channel.getInactivationAmt()) * Math.PI * 0.3;
					minRotation = maxRotation - angularRange;
				}
				else{
					// Move out to the left (assuming channel is vertical).
					// The angle at which we can move gets more restricted
					// as the inactivation gate closes.
					maxRotation = -Math.PI * 0.4;
					double angularRange = (1 - channel.getInactivationAmt()) * -Math.PI * 0.1;
					minRotation = maxRotation - angularRange;
				}
				velocityRotationAngle = minRotation + RAND.nextDouble() * (maxRotation - minRotation);
				velocityVector.rotate(velocityRotationAngle);
			}
			else{
				velocityVector.rotate((RAND.nextDouble() - 0.5) * ( Math.PI * 0.9 ) * maxVelocity / DEFAULT_MAX_VELOCITY);
			}
		}
	}
}
