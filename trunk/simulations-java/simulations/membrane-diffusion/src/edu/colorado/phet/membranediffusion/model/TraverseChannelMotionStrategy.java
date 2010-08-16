package edu.colorado.phet.membranediffusion.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.membranediffusion.module.MembraneDiffusionDefaults;

/**
 * A motion strategy for traversing a basic membrane channel, i.e. one that
 * has only one gate, and then doing a random walk.
 * 
 * @author John Blanco
 */
public class TraverseChannelMotionStrategy extends MotionStrategy {

    private static final double DEFAULT_MAX_VELOCITY = 100000; // In nanometers per second of sim time.
    private static final double POST_TRAVERSAL_WALK_TIME = 
        MembraneDiffusionDefaults.DEFAULT_MEMBRANE_DIFFUSION_CLOCK_DT * 50;  // In seconds of sim time.
    private static final Random RAND = new Random();
    
	private Vector2D velocityVector = new Vector2D.Double();
	private ArrayList<Point2D> traversalPoints;
	private int currentDestinationIndex = 0;
	private boolean channelHasBeenEntered = false; // Flag that is set when the channel is entered.
	private boolean channelHasBeenTraversed = false; // Flag that is set when particle has exited the channel.
	private double maxVelocity;
	protected final MembraneChannel channel;
	private Rectangle2D postTraversalMotionBounds = new Rectangle2D.Double();
	private MotionStrategy postTraversalMotionStrategy;
	private double postTraversalCountdownTimer = Double.POSITIVE_INFINITY;

	
	public TraverseChannelMotionStrategy(MembraneChannel channel, Point2D startingLocation, Rectangle2D postTraversalMotionBounds, double maxVelocity) {
		this.channel = channel;
		this.maxVelocity = maxVelocity;
		this.postTraversalMotionBounds.setFrame(postTraversalMotionBounds);
		traversalPoints = createTraversalPoints(channel, startingLocation);
		currentDestinationIndex = 0;
		setCourseForCurrentTraversalPoint(startingLocation);
	}

	public TraverseChannelMotionStrategy(MembraneChannel channel, Point2D startingLocation, Rectangle2D postTraversalMotionBounds) {
		this(channel, startingLocation, postTraversalMotionBounds, DEFAULT_MAX_VELOCITY);
	}

	@Override
	public void move(IMovable movableModelElement, double dt) {
		
		Point2D currentPositionRef = movableModelElement.getPositionReference();
		
		if (!channelHasBeenEntered){
			// Update the flag the tracks whether this particle has made it
			// to the channel and started traversing it.
			channelHasBeenEntered = channel.isPointInChannel(currentPositionRef);
		}
		
		if (channelHasBeenTraversed){
		    // The channel has been traversed, and we are currently executing
		    // the post-traversal motion.
		    postTraversalCountdownTimer -= dt;
		    if (postTraversalCountdownTimer <= 0){
		        // The traversal process is complete, set a new Random Walk strategy.
                movableModelElement.setMotionStrategy(new RandomWalkMotionStrategy(postTraversalMotionBounds));
		    }
		    else{
		        // Move the particle.
		        postTraversalMotionStrategy.move( movableModelElement, dt );
		    }
		}
		else if (channel.isOpen() || channelHasBeenEntered){
			// The channel is open, or we are inside it, so keep executing
			// this motion strategy.
			if ( currentDestinationIndex >= traversalPoints.size() || maxVelocity * dt < currentPositionRef.distance(traversalPoints.get(currentDestinationIndex))){
				// Move according to the current velocity.
				movableModelElement.setPosition(currentPositionRef.getX() + velocityVector.getX() * dt,
						currentPositionRef.getY() + velocityVector.getY() * dt);
			}
			else{
				// We are close enough to the destination that we should just
				// position ourself there and update to the next traversal point.
				movableModelElement.setPosition(traversalPoints.get(currentDestinationIndex));
				currentDestinationIndex++;
				setCourseForCurrentTraversalPoint(movableModelElement.getPosition());
				if (currentDestinationIndex == traversalPoints.size()){
					// We have traversed through all points and are now
					// presumably on the other side of the membrane, or has
				    // reemerged from the side that it went in (i.e. it
				    // changed direction while in the channel).  Start doing
				    // a random walk, but keep it as part of this motion
				    // strategy for now, since we don't want the particle to
				    // be immediately recaptured by the same channel.
				    channelHasBeenTraversed = true;
				    postTraversalMotionStrategy = new RandomWalkMotionStrategy( postTraversalMotionBounds );
				    postTraversalCountdownTimer = POST_TRAVERSAL_WALK_TIME;
				}
			}
		}
		else{
			// The channel has closed and this element has not yet entered it.
			// Time to replace this motion strategy with a different one.
			movableModelElement.setMotionStrategy(new RandomWalkMotionStrategy(postTraversalMotionBounds));
		}
	}
	
   @Override
    public Vector2D getInstantaneousVelocity() {
        return new Vector2D.Double(velocityVector.getX(), velocityVector.getY());
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
		double r = channel.getChannelSize().getHeight() * 0.7; // Make the point a little outside the channel.
		Point2D outerOpeningLocation = new Point2D.Double(ctr.getX(), ctr.getY() + r);
		Point2D innerOpeningLocation = new Point2D.Double(ctr.getX(), ctr.getY() - r);

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
			// All points have been traversed.  Change the direction a bit in
			// order to make things look a little more "Brownian".
			velocityVector.rotate((RAND.nextDouble() - 0.5) * Math.PI * 0.9);
		}
	}
}
