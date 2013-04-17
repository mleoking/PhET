// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * A motion strategy for traversing through a dual-gate channel, meaning one
 * that has a gate and an inactivation level.
 * <p/>
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

    // Threshold at which particles will "bounce" back out of the channel
    // rather than traversing it.
    private static final double INACTIVATION_BOUNCE_THRESHOLD = 0.5;

    private MutableVector2D velocityVector = new MutableVector2D();
    private ArrayList<Point2D> traversalPoints;
    private int currentDestinationIndex = 0;
    private double maxVelocity;
    protected final MembraneChannel channel;
    private boolean bouncing = false;

    public DualGateChannelTraversalMotionStrategy( MembraneChannel channel, Point2D startingLocation, double maxVelocity ) {
        this.channel = channel;
        this.maxVelocity = maxVelocity;
        traversalPoints = createTraversalPoints( channel, startingLocation );
        setCourseForCurrentTraversalPoint( startingLocation );
    }

    public DualGateChannelTraversalMotionStrategy( MembraneChannel channel, Point2D startingLocation ) {
        this( channel, startingLocation, DEFAULT_MAX_VELOCITY );
    }

    @Override
    public void move( IMovable movableModelElement, IFadable fadableModelElement, double dt ) {

        assert currentDestinationIndex < traversalPoints.size();  // Error checking.

        Point2D currentPositionRef = movableModelElement.getPositionReference();

        if ( currentDestinationIndex == 0 ) {
            // Currently moving towards the first destination point.  Is the
            // channel still open?
            if ( !channel.isOpen() ) {
                // The channel has closed, so there is no sense in continuing
                // towards it.  The particle should wander and then fade out.
                movableModelElement.setMotionStrategy( new WanderAwayThenFadeMotionStrategy( channel.getCenterLocation(),
                                                                                             movableModelElement.getPosition(), 0, 0.002 ) );
                // For error checking, set the destination index really high.
                // That way it will be detected if this strategy instance is
                // used again.
                currentDestinationIndex = Integer.MAX_VALUE;
            }
            else if ( currentPositionRef.distance( traversalPoints.get( currentDestinationIndex ) ) < velocityVector.magnitude() * dt ) {
                // We have arrived at the first traversal point, so now start
                // heading towards the second.
                movableModelElement.setPosition( traversalPoints.get( currentDestinationIndex ) );
                currentDestinationIndex++;
                setCourseForPoint( movableModelElement.getPosition(), traversalPoints.get( currentDestinationIndex ),
                                   velocityVector.magnitude() );
            }
            else {
                // Keep moving towards current destination.
                moveBasedOnCurrentVelocity( movableModelElement, dt );
            }
        }
        else if ( currentDestinationIndex == 1 ) {
            // Currently moving towards the 2nd point, which is in the
            // channel just above where the inactivation gate appears.
            if ( channel.getInactivationAmt() > INACTIVATION_BOUNCE_THRESHOLD ) {
                // The inactivation threshold has been reached, so we can't
                // finish traversing the channel and need to bounce.  Check
                // whether we've already handled this.
                if ( !bouncing ) {
                    // Set the particle up to "bounce", i.e. to turn around
                    // and go back whence it came once it reaches the 2nd
                    // point.
                    traversalPoints.get( 2 ).setLocation( traversalPoints.get( 0 ) );
                    bouncing = true; // Flag for tracking that we need to bounce.
                }
            }
            if ( currentPositionRef.distance( traversalPoints.get( currentDestinationIndex ) ) < velocityVector.magnitude() * dt ) {
                // The element has reached the current traversal point, so
                // it should start moving towards the next.
                movableModelElement.setPosition( traversalPoints.get( currentDestinationIndex ) );
                currentDestinationIndex++;
                setCourseForPoint( movableModelElement.getPosition(), traversalPoints.get( currentDestinationIndex ),
                                   velocityVector.magnitude() );
                if ( bouncing ) {
                    // Slow down if we are bouncing - it looks better this way.
                    velocityVector.scale( 0.5 );
                }
            }
            else {
                // Keep moving towards current destination.
                moveBasedOnCurrentVelocity( movableModelElement, dt );
            }
        }
        else if ( currentDestinationIndex == 2 ) {
            // Currently moving towards the 3rd point.
            if ( currentPositionRef.distance( traversalPoints.get( currentDestinationIndex ) ) < velocityVector.magnitude() * dt ) {
                // The element has reached the last traversal point, so a
                // new motion strategy is set to have it move away and then
                // fade out.
                movableModelElement.setPosition( traversalPoints.get( currentDestinationIndex ) );
                currentDestinationIndex = Integer.MAX_VALUE;
                MutableVector2D newVelocityVector = new MutableVector2D( velocityVector );
                if ( bouncing ) {
                    // This particle should be back where it entered the
                    // channel, and can head off in any direction except
                    // back toward the membrane.
                    newVelocityVector.rotate( ( RAND.nextDouble() - 0.5 ) * Math.PI );
                    newVelocityVector.scale( 0.3 + ( RAND.nextDouble() * 0.2 ) );
                    movableModelElement.setMotionStrategy( new LinearMotionStrategy( newVelocityVector ) );
                }
                else {
                    // The particle is existing the part of the channel where
                    // the inactivation gate exists, so it needs to take on a
                    // new course that avoids the gate.  Note that this is set
                    // up to work with a specific representation of the
                    // inactivation gate, and will need to be changed if the
                    // representation of the gate is changed.
                    newVelocityVector.scale( 0.5 + ( RAND.nextDouble() * 0.3 ) );
                    double maxRotation, minRotation;
                    if ( RAND.nextDouble() > 0.3 ) {
                        // Move out to the right (assuming channel is vertical).
                        // The angle at which we can move gets more restricted
                        // as the inactivation gate closes.
                        maxRotation = Math.PI * 0.4;
                        double angularRange = ( 1 - channel.getInactivationAmt() ) * Math.PI * 0.3;
                        minRotation = maxRotation - angularRange;
                    }
                    else {
                        // Move out to the left (assuming channel is vertical).
                        // The angle at which we can move gets more restricted
                        // as the inactivation gate closes.
                        maxRotation = -Math.PI * 0.4;
                        double angularRange = ( 1 - channel.getInactivationAmt() ) * -Math.PI * 0.1;
                        minRotation = maxRotation - angularRange;
                    }
                    newVelocityVector.rotate( minRotation + RAND.nextDouble() * ( maxRotation - minRotation ) );
                    movableModelElement.setMotionStrategy( new SpeedChangeLinearMotionStrategy( newVelocityVector, 0.2, 0.0002 ) );
                }
                fadableModelElement.setFadeStrategy( new TimedFadeAwayStrategy( 0.003 ) );
            }
            else {
                // Keep moving towards current destination.
                moveBasedOnCurrentVelocity( movableModelElement, dt );
            }
        }
    }

    private void moveBasedOnCurrentVelocity( IMovable movable, double dt ) {
        movable.setPosition( movable.getPosition().getX() + velocityVector.getX() * dt,
                             movable.getPosition().getY() + velocityVector.getY() * dt );
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
        double r = channel.getChannelSize().getHeight() * 0.5;

        // Create points that represent the inner and outer mouths of the channel.
        Point2D outerOpeningLocation = new Point2D.Double( ctr.getX() + Math.cos( channel.getRotationalAngle() ) * r,
                                                           ctr.getY() + Math.sin( channel.getRotationalAngle() ) * r );
        Point2D innerOpeningLocation = new Point2D.Double( ctr.getX() - Math.cos( channel.getRotationalAngle() ) * r,
                                                           ctr.getY() - Math.sin( channel.getRotationalAngle() ) * r );

        // Create a point that just above where the inactivation gate would
        // be if the channel were inactivated.  Since the model doesn't
        // actually track the location of the inactivation gate (it is left
        // up to the view), this location is a guess, and may have to be
        // tweaked in order to work well with the view.
        Point2D aboveInactivationGateLocation =
                new Point2D.Double( ctr.getX() - Math.cos( channel.getRotationalAngle() ) * r * 0.5,
                                    ctr.getY() - Math.sin( channel.getRotationalAngle() ) * r * 0.5 );

        if ( startingLocation.distance( innerOpeningLocation ) < startingLocation.distance( outerOpeningLocation ) ) {
            points.add( innerOpeningLocation );
            points.add( aboveInactivationGateLocation );
            points.add( outerOpeningLocation );
        }
        else {
            points.add( outerOpeningLocation );
            points.add( aboveInactivationGateLocation );
            points.add( innerOpeningLocation );
        }

        return points;
    }

    private void setCourseForPoint( Point2D startLocation, Point2D destination, double velocityScaler ) {
        velocityVector.setComponents( destination.getX() - startLocation.getX(),
                                      destination.getY() - startLocation.getY() );
        double scaleFactor = maxVelocity / velocityVector.magnitude();
        velocityVector.scale( scaleFactor );
    }

    private void setCourseForCurrentTraversalPoint( Point2D currentLocation ) {
        if ( currentDestinationIndex < traversalPoints.size() ) {
            Point2D dest = traversalPoints.get( currentDestinationIndex );
            velocityVector.setComponents( dest.getX() - currentLocation.getX(), dest.getY() - currentLocation.getY() );
            double scaleFactor = maxVelocity / velocityVector.magnitude();
            velocityVector.scale( scaleFactor );
        }
        else {
            // All points have been traversed.  The behavior at this point
            // depends on whether the channel has an inactivation gate, since
            // such a gate is depicted on the cell-interior side of the
            // channel in this sim.  No matter whether such a gate exists or
            // not, the particle is re-routed a bit in order to create a bit
            // of a brownian look.  If the gate exists, there are more
            // limitations to where the particle can go.
            if ( channel.getHasInactivationGate() ) {
                // NOTE: The following is tweaked to work with a particular
                // visual representation of the inactivation gate, and may
                // need to be changed if that representation is changed.
                double velocityRotationAngle = 0;
                double minRotation = 0;
                double maxRotation = 0;
                if ( RAND.nextDouble() > 0.3 ) {
                    // Move out to the right (assuming channel is vertical).
                    // The angle at which we can move gets more restricted
                    // as the inactivation gate closes.
                    maxRotation = Math.PI * 0.4;
                    double angularRange = ( 1 - channel.getInactivationAmt() ) * Math.PI * 0.3;
                    minRotation = maxRotation - angularRange;
                }
                else {
                    // Move out to the left (assuming channel is vertical).
                    // The angle at which we can move gets more restricted
                    // as the inactivation gate closes.
                    maxRotation = -Math.PI * 0.4;
                    double angularRange = ( 1 - channel.getInactivationAmt() ) * -Math.PI * 0.1;
                    minRotation = maxRotation - angularRange;
                }
                velocityRotationAngle = minRotation + RAND.nextDouble() * ( maxRotation - minRotation );
                velocityVector.rotate( velocityRotationAngle );
            }
            else {
                velocityVector.rotate( ( RAND.nextDouble() - 0.5 ) * ( Math.PI * 0.9 ) * maxVelocity / DEFAULT_MAX_VELOCITY );
            }
        }
    }
}
