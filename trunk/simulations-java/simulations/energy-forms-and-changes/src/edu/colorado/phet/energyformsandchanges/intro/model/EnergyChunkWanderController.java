// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class is used to make an energy chunk wander, i.e. to perform somewhat
 * of a random walk while moving towards a destination.
 *
 * @author John Blanco
 */
public final class EnergyChunkWanderController {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double MIN_VELOCITY = 0.03; // In m/s.
    private static final double MAX_VELOCITY = 0.06; // In m/s.
    private static final Random RAND = new Random();
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.5;
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1;
    private static final double DISTANCE_AT_WHICH_TO_STOP_WANDERING = 0.05; // In meters, empirically chosen.
    private static final double DISTANCE_AT_WHICH_TO_JUMP_TO_DESTINATION = 0.001; // In meters, empirically chosen.
    private static final double MAX_ANGLE_VARIATION = Math.PI * 0.2; // Max deviation from angle to destination, in radians, empirically chosen.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final EnergyChunk energyChunk;
    private final ImmutableVector2D destination;
    private Vector2D velocity = new Vector2D( 0, MAX_VELOCITY );
    private double countdownTimer = 0;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergyChunkWanderController( EnergyChunk energyChunk, ImmutableVector2D destination ) {
        this.energyChunk = energyChunk;
        this.destination = destination;
        resetCountdownTimer();
        changeVelocityVector();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void updatePosition( double dt ) {
        double distanceToDestination = energyChunk.position.get().distance( destination );
        if ( distanceToDestination < DISTANCE_AT_WHICH_TO_JUMP_TO_DESTINATION && !energyChunk.position.get().equals( destination ) ) {
            // Destination reached.
            energyChunk.position.set( destination );
            velocity.setMagnitude( 0 );
        }
        else if ( energyChunk.position.get().distance( destination ) < dt * velocity.getMagnitude() ) {
            // Prevent overshoot.
            velocity.getScaledInstance( energyChunk.position.get().distance( destination ) * dt );
        }

        if ( velocity.getMagnitude() > 0 ) {
            energyChunk.position.set( energyChunk.position.get().getAddedInstance( velocity.getScaledInstance( dt ) ) );
            countdownTimer -= dt;
            if ( countdownTimer <= 0 ) {
                changeVelocityVector();
                resetCountdownTimer();
            }
        }
    }

    private void changeVelocityVector() {
        ImmutableVector2D vectorToDestination = destination.getSubtractedInstance( energyChunk.position.get() );
        double angle = vectorToDestination.getAngle();
        if ( vectorToDestination.getMagnitude() > DISTANCE_AT_WHICH_TO_STOP_WANDERING ) {
            // Add some randomness to the direction of travel.
            angle = angle + ( RAND.nextDouble() - 0.5 ) * 2 * MAX_ANGLE_VARIATION;
        }
        double scalarVelocity = MIN_VELOCITY + ( MAX_VELOCITY - MIN_VELOCITY ) * RAND.nextDouble();
        velocity.setComponents( scalarVelocity * Math.cos( angle ), scalarVelocity * Math.sin( angle ) );
    }

    private void resetCountdownTimer() {
        countdownTimer = MIN_TIME_IN_ONE_DIRECTION + ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION ) * RAND.nextDouble();
    }

    public EnergyChunk getEnergyChunk() {
        return energyChunk;
    }

    public ImmutableVector2D getDestination() {
        return destination;
    }

    public boolean isDestinationReached() {
        return energyChunk.position.get().distance( destination ) < DISTANCE_AT_WHICH_TO_JUMP_TO_DESTINATION;
    }
}
