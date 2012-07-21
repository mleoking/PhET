// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.genenetwork.module.LacOperonDefaults;

/**
 * Motion strategy that moves things around in a way that emulates the "random
 * walk" behavior exhibited by particles in a fluid, but that also moves
 * towards a destination point.
 *
 * @author John Blanco
 */
public class DirectedRandomWalkMotionStrategy extends AbstractMotionStrategy {

    private static final Random RAND = new Random();
    private static final double DIRECTED_PROPORTION = 0.90; // Proportion of motion updates that move towards
    // the destination point.
    private static final double MOTION_UPDATE_PERIOD = 20 * LacOperonDefaults.CLOCK_DT;  // Time before changing direction.
    protected static final double MAX_DIRECTED_VELOCITY = 15;  // In nanometers per second
    protected static final double MIN_DIRECTED_VELOCITY = 5;  // In nanometers per second
    protected static final double MAX_WANDERING_VELOCITY = 8;  // In nanometers per second
    protected static final double MIN_WANDERING_VELOCITY = 3;  // In nanometers per second

    // Range within which the moving item should not exhibit any random
    // motion and should just head toward the destination.
    protected static double DIRECT_MOVEMENT_RANGE = 13; // In nanometers.

    private double updateCountdownTimer = 0;

    public DirectedRandomWalkMotionStrategy( Rectangle2D bounds, Point2D destination ) {
        super( bounds );
        if ( destination != null ) {
            setDestination( destination.getX(), destination.getY() );
        }

        // Initialize the countdown timer with a random value so that the
        // users of this strategy end up updating at different times.  This
        // creates a more realistic look and keeps the computation load more
        // even.
        updateCountdownTimer = RAND.nextDouble() * MOTION_UPDATE_PERIOD;
    }

    public DirectedRandomWalkMotionStrategy( Rectangle2D bounds ) {
        this( bounds, null );
    }

    @Override
    public void updatePositionAndMotion( double dt, SimpleModelElement modelElement ) {

        Point2D position = modelElement.getPositionRef();
        MutableVector2D velocity = modelElement.getVelocityRef();

        // Bounce back toward the inside if we are outside of the motion bounds.
        if ( ( position.getX() > getBounds().getMaxX() && velocity.getX() > 0 ) ||
             ( position.getX() < getBounds().getMinX() && velocity.getX() < 0 ) ) {
            // Reverse direction in the X direction.
            modelElement.setVelocity( -velocity.getX(), velocity.getY() );
        }
        if ( ( position.getY() > getBounds().getMaxY() && velocity.getY() > 0 ) ||
             ( position.getY() < getBounds().getMinY() && velocity.getY() < 0 ) ) {
            // Reverse direction in the Y direction.
            modelElement.setVelocity( velocity.getX(), -velocity.getY() );
        }

        if ( !modelElement.isUserControlled() ) {
            modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX() * dt,
                                      modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() * dt );
        }

        // See if it is time to change the motion and, if so, do it.
        updateCountdownTimer -= dt;
        if ( updateCountdownTimer <= 0 ) {
            double angle = 0;
            double scalarVelocity;
            if ( getDestinationRef() != null ) {
                scalarVelocity = MIN_DIRECTED_VELOCITY + ( MAX_DIRECTED_VELOCITY - MIN_DIRECTED_VELOCITY ) * RAND.nextDouble();
            }
            else {
                scalarVelocity = MIN_WANDERING_VELOCITY + ( MAX_WANDERING_VELOCITY - MIN_WANDERING_VELOCITY ) * RAND.nextDouble();
            }
            if ( getDestinationRef() != null && ( getDestinationRef().distance( modelElement.getPositionRef() ) < DIRECT_MOVEMENT_RANGE || RAND.nextDouble() < DIRECTED_PROPORTION ) ) {
                // Move towards the destination.
                angle = Math.atan2( getDestinationRef().getY() - modelElement.getPositionRef().getY(),
                                    getDestinationRef().getX() - modelElement.getPositionRef().getX() );
            }
            else {
                // Do the random walk thing.
                angle = Math.PI * 2 * RAND.nextDouble();
            }

            // Set the particle's new velocity.
            modelElement.setVelocity( scalarVelocity * Math.cos( angle ), scalarVelocity * Math.sin( angle ) );

            // Update the countdown timer value.
            updateCountdownTimer = MOTION_UPDATE_PERIOD;
        }
    }
}
