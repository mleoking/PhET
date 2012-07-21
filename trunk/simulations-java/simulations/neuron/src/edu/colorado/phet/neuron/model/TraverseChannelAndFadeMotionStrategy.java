// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * A motion strategy for traversing a basic membrane channel, i.e. one that
 * has only one gate, and then fading away.
 *
 * @author John Blanco
 */
public class TraverseChannelAndFadeMotionStrategy extends MembraneTraversalMotionStrategy {

    private static final Random RAND = new Random();
    private MutableVector2D velocityVector = new MutableVector2D();
    private ArrayList<Point2D> traversalPoints;
    private int currentDestinationIndex = 0;
    private boolean channelHasBeenEntered = false; // Flag that is set when the channel is entered.
    private double maxVelocity;
    protected final MembraneChannel channel;

    public TraverseChannelAndFadeMotionStrategy( MembraneChannel channel, Point2D startingLocation, double maxVelocity ) {
        this.channel = channel;
        this.maxVelocity = maxVelocity;
        traversalPoints = createTraversalPoints( channel, startingLocation );
        currentDestinationIndex = 0;
        setCourseForCurrentTraversalPoint( startingLocation );
    }

    public TraverseChannelAndFadeMotionStrategy( MembraneChannel channel, Point2D startingLocation ) {
        this( channel, startingLocation, DEFAULT_MAX_VELOCITY );
    }

    @Override
    public void move( IMovable movableModelElement, IFadable fadableModelElement, double dt ) {

        Point2D currentPositionRef = movableModelElement.getPositionReference();

        if ( !channelHasBeenEntered ) {
            // Update the flag the tracks whether this particle has made it
            // to the channel and started traversing it.
            channelHasBeenEntered = channel.isPointInChannel( currentPositionRef );
        }

        if ( channel.isOpen() || channelHasBeenEntered ) {
            // The channel is open, or we are inside it or have gone all the
            // way through, so keep executing this motion strategy.
            if ( currentDestinationIndex >= traversalPoints.size() || maxVelocity * dt < currentPositionRef.distance( traversalPoints.get( currentDestinationIndex ) ) ) {
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
                    // presumably on the other side of the membrane, so we need to
                    // start fading out of existence.
                    fadableModelElement.setFadeStrategy( new TimedFadeAwayStrategy( 0.002 ) );

                    // Slow down the speed.  Don't do this if it is already
                    // moving pretty slowly.
                    if ( maxVelocity / DEFAULT_MAX_VELOCITY >= 0.5 ) {
                        velocityVector.scale( 0.2 );
                    }
                }
            }
        }
        else {
            // The channel has closed and this element has not yet entered it.
            // Time to replace this motion strategy with a different one.
            movableModelElement.setMotionStrategy( new WanderAwayThenFadeMotionStrategy( channel.getCenterLocation(),
                                                                                         movableModelElement.getPosition(), 0, 0.002 ) );
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
        double r = channel.getChannelSize().getHeight() * 0.65; // Make the point a little outside the channel.
        Point2D outerOpeningLocation = new Point2D.Double( ctr.getX() + Math.cos( channel.getRotationalAngle() ) * r,
                                                           ctr.getY() + Math.sin( channel.getRotationalAngle() ) * r );
        Point2D innerOpeningLocation = new Point2D.Double( ctr.getX() - Math.cos( channel.getRotationalAngle() ) * r,
                                                           ctr.getY() - Math.sin( channel.getRotationalAngle() ) * r );

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
            double scaleFactor = maxVelocity / velocityVector.getMagnitude();
            velocityVector.scale( scaleFactor );
        }
        else {
            // All points have been traversed.  Change the direction a bit in
            // order to make things look a little more "Brownian".
            velocityVector.rotate( ( RAND.nextDouble() - 0.5 ) * Math.PI * 0.9 );
        }
    }
}
