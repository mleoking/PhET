// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.membranechannels.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * A motion strategy for traversing a basic membrane channel, i.e. one that
 * has only one gate, and then doing a random walk.
 *
 * @author John Blanco
 */
public class TraverseChannelMotionStrategy extends MotionStrategy {

    private static final double DEFAULT_MAX_VELOCITY = 10; // In nanometers per second of sim time.
    private static final Random RAND = new Random();

    // This constant controls the length of time that this strategy continues
    // to control the particle after the traversal has completed.  It may need
    // to be adjusted to prevent immediate recapture.
    private static final double POST_TRAVERSAL_HYSTERESIS_TIME = 1; // In seconds of sim time.

    private MutableVector2D velocityVector = new MutableVector2D();
    private ArrayList<Point2D> traversalPoints;
    private int currentDestinationIndex = 0;
    private boolean channelHasBeenEntered = false; // Flag that is set when the channel is entered.
    private boolean channelHasBeenTraversed = false; // Flag that is set when particle has exited the channel.
    private double velocityScaler;
    protected final MembraneChannel channel;
    private Rectangle2D preTraversalMotionBounds = new Rectangle2D.Double();
    private Rectangle2D postTraversalMotionBounds = new Rectangle2D.Double();
    private MotionStrategy postTraversalMotionStrategy;
    private double postTraversalCountdownTimer = Double.POSITIVE_INFINITY;


    public TraverseChannelMotionStrategy( MembraneChannel channel, Point2D startingLocation,
                                          Rectangle2D preTraversalMotionBounds, Rectangle2D postTraversalMotionBounds, double velocity ) {
        this.channel = channel;
        this.velocityScaler = velocity;
        this.preTraversalMotionBounds.setFrame( preTraversalMotionBounds );
        this.postTraversalMotionBounds.setFrame( postTraversalMotionBounds );
        traversalPoints = createTraversalPoints( channel, startingLocation );
        currentDestinationIndex = 0;
        setCourseForCurrentTraversalPoint( startingLocation );
    }

    public TraverseChannelMotionStrategy( MembraneChannel channel, Point2D startingLocation,
                                          Rectangle2D preTraversalMotionBounds, Rectangle2D postTraversalMotionBounds ) {
        this( channel, startingLocation, preTraversalMotionBounds, postTraversalMotionBounds, DEFAULT_MAX_VELOCITY );
    }

    @Override
    public void move( IMovable movableModelElement, double dt ) {

        Point2D currentPositionRef = movableModelElement.getPositionReference();

        if ( !channelHasBeenEntered ) {
            // Update the flag the tracks whether this particle has made it
            // to the channel and started traversing it.
            channelHasBeenEntered = channel.isPointInChannel( currentPositionRef );
        }

        if ( channelHasBeenTraversed ) {
            // The channel has been traversed, and we are currently executing
            // the post-traversal motion.
            postTraversalCountdownTimer -= dt;
            if ( postTraversalCountdownTimer <= 0 ) {
                // The traversal process is complete, set a new Random Walk strategy.
                notifyStrategyComplete( movableModelElement );
                movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( postTraversalMotionBounds ) );
            }
            else {
                // Move the particle.
                postTraversalMotionStrategy.move( movableModelElement, dt );
            }
        }
        else if ( channel.isOpen() || channelHasBeenEntered ) {
            // The channel is open, or we are inside it, so keep executing
            // this motion strategy.
            if ( currentDestinationIndex >= traversalPoints.size() || velocityScaler * dt < currentPositionRef.distance( traversalPoints.get( currentDestinationIndex ) ) ) {
                // Move according to the current velocity.
                movableModelElement.setPosition( currentPositionRef.getX() + velocityVector.getX() * dt,
                                                 currentPositionRef.getY() + velocityVector.getY() * dt );
            }
            else {
                // We are close enough to the destination that we should just
                // position ourself there and update to the next traversal point.
                movableModelElement.setPosition( traversalPoints.get( currentDestinationIndex ) );
                currentDestinationIndex++;
                setCourseForCurrentTraversalPoint( movableModelElement.getPosition() );
                if ( currentDestinationIndex == traversalPoints.size() ) {
                    // We have traversed through all points and are now
                    // presumably on the other side of the membrane, or have
                    // reemerged from the side that it went in (i.e. it
                    // changed direction while in the channel).  Move linearly
                    // away from the mouth of the channel in order to
                    // minimize the chances of being recaptured, but don't
                    // set a completely new motion strategy, since this
                    // prevents the particle from appearing to be ready for
                    // recapture.
                    channelHasBeenTraversed = true;
                    // Set a random angle for the new direction.
                    double newAngle = Math.PI / 2 + ( RAND.nextDouble() - 0.5 ) * Math.PI * 0.75;
                    if ( getInstantaneousVelocity().getAngle() < 0 ) {
                        // Use the angle, but go down instead.
                        newAngle = -newAngle;
                    }
                    MutableVector2D newVelocityVector = new MutableVector2D();
                    newVelocityVector.setMagnitudeAndAngle( velocityScaler, newAngle );
                    postTraversalMotionStrategy = new BoundedLinearMotionStrategy( newVelocityVector,
                                                                                   postTraversalMotionBounds );
                    postTraversalCountdownTimer = POST_TRAVERSAL_HYSTERESIS_TIME;
                }
            }
        }
        else {
            // The channel has closed and this element has not yet entered it.
            // Time to replace this motion strategy with a different one.
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( preTraversalMotionBounds ) );
            notifyStrategyComplete( movableModelElement );
        }
    }

    @Override
    public MutableVector2D getInstantaneousVelocity() {
        return new MutableVector2D( velocityVector.getX(), velocityVector.getY() );
    }

    /**
     * Abort the traversal.  This was created for the case where a particle is
     * traversing the channel as the user grabs the channel.
     * <p/>
     * IMPORTANT NOTE: Because the motion strategy doesn't maintain a reference
     * to the element that it is moving (it works the other way around), the
     * movable element must be supplied.  If the wrong element is supplied,
     * it would obviously cause weird behavior.  So, like, don't do it.
     * <p/>
     * ANOTHER IMPORTANT NOTE: This does not send out notification of the
     * strategy having completed, since it didn't really complete.
     */
    public void abortTraversal( IMovable movableModelElement ) {
        Point2D currentPos = movableModelElement.getPositionReference();
        if ( preTraversalMotionBounds.contains( currentPos ) ) {
            // Hasn't started traversing yet.
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( preTraversalMotionBounds ) );
        }
        else if ( postTraversalMotionBounds.contains( currentPos ) ) {
            // It is all the way across, just still under the channel's control.
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( postTraversalMotionBounds ) );
        }
        else {
            // The particle is actually inside the channel.  In this case, we
            // just go ahead and put it at the last traversal point and set the
            // post-traversal strategy, and hope it doesn't look too weird.
            movableModelElement.setPosition( traversalPoints.get( traversalPoints.size() - 1 ) );
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( postTraversalMotionBounds ) );
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
    private ArrayList<Point2D> createTraversalPoints( MembraneChannel channel, Point2D startingLocation ) {

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        Point2D ctr = channel.getCenterLocation();
        double r = channel.getChannelSize().getHeight() * 0.7; // Make the point a little outside the channel.
        Point2D outerOpeningLocation = new Point2D.Double( ctr.getX(), ctr.getY() + r );
        Point2D innerOpeningLocation = new Point2D.Double( ctr.getX(), ctr.getY() - r );

        if ( startingLocation.distance( innerOpeningLocation ) < startingLocation.distance( outerOpeningLocation ) ) {
            points.add( innerOpeningLocation );
            points.add( outerOpeningLocation );
        }
        else {
            points.add( outerOpeningLocation );
            points.add( innerOpeningLocation );
        }

        return points;
    }

    private void setCourseForCurrentTraversalPoint( Point2D currentLocation ) {
        if ( currentDestinationIndex < traversalPoints.size() ) {
            Point2D dest = traversalPoints.get( currentDestinationIndex );
            velocityVector.setComponents( dest.getX() - currentLocation.getX(), dest.getY() - currentLocation.getY() );
            double scaleFactor = velocityScaler / velocityVector.magnitude();
            velocityVector.scale( scaleFactor );
        }
        else {
            // All points have been traversed.  Change the direction a bit in
            // order to make things look a little more "Brownian".
            velocityVector.rotate( ( RAND.nextDouble() - 0.5 ) * Math.PI * 0.9 );
        }
    }
}
