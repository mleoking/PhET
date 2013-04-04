// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

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

    private static final double MIN_VELOCITY = 0.06; // In m/s.
    private static final double MAX_VELOCITY = 0.10; // In m/s.
    private static final Random RAND = new Random();
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.4;
    private static final double MAX_TIME_IN_ONE_DIRECTION = 0.8;
    private static final double DISTANCE_AT_WHICH_TO_STOP_WANDERING = 0.05; // In meters, empirically chosen.
    private static final double MAX_ANGLE_VARIATION = Math.PI * 0.2; // Max deviation from angle to destination, in radians, empirically chosen.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final EnergyChunk energyChunk;
    private final Property<Vector2D> destination;
    private final MutableVector2D velocity = new MutableVector2D( 0, MAX_VELOCITY );
    private double countdownTimer = 0;
    private final Rectangle2D initialWanderConstraint;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergyChunkWanderController( EnergyChunk energyChunk, Property<Vector2D> destination ) {
        this( energyChunk, destination, null );
    }

    public EnergyChunkWanderController( EnergyChunk energyChunk, Property<Vector2D> destination, Rectangle2D initialWanderConstraint ) {
        this.energyChunk = energyChunk;
        this.destination = destination;
        this.initialWanderConstraint = initialWanderConstraint;
        resetCountdownTimer();
        changeVelocityVector();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void updatePosition( double dt ) {
        double distanceToDestination = energyChunk.position.get().distance( destination.get() );
        if ( distanceToDestination < velocity.magnitude() * dt && !energyChunk.position.get().equals( destination.get() ) ) {
            // Destination reached.
            energyChunk.position.set( destination.get() );
            velocity.setMagnitude( 0 );
        }
        else if ( energyChunk.position.get().distance( destination.get() ) < dt * velocity.magnitude() ) {
            // Prevent overshoot.
            velocity.times( energyChunk.position.get().distance( destination.get() ) * dt );
        }

        // Stay within the horizontal confines of the initial bounds.
        if ( initialWanderConstraint != null && energyChunk.position.get().getY() < initialWanderConstraint.getMaxY() ){
            Vector2D proposedPosition = energyChunk.position.get().plus( velocity.times( dt ) );
            if ( proposedPosition.getX() < initialWanderConstraint.getMinX() || proposedPosition.getX() > initialWanderConstraint.getMaxX() ){
                // Bounce in the x direction to prevent going outside initial bounds.
                velocity.setComponents( -velocity.getX(), velocity.getY() );
            }
        }

        energyChunk.position.set( energyChunk.position.get().plus( velocity.times( dt ) ) );
        countdownTimer -= dt;
        if ( countdownTimer <= 0 ) {
            changeVelocityVector();
            resetCountdownTimer();
        }
    }

    private void changeVelocityVector() {
        Vector2D vectorToDestination = destination.get().minus( energyChunk.position.get() );
        double angle = vectorToDestination.getAngle();
        if ( vectorToDestination.magnitude() > DISTANCE_AT_WHICH_TO_STOP_WANDERING ) {
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

    public boolean destinationReached() {
        return destination.get().distance( energyChunk.position.get() ) < 1E-7;
    }
}
