// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.neuron.module.NeuronDefaults;


/**
 * A motion strategy that has a particle wander around for a while and then
 * fade out of existence.
 *
 * @author John Blanco
 */
public class WanderAwayThenFadeMotionStrategy extends MotionStrategy {

    private static final Random RAND = new Random();
    private static final int CLOCK_TICKS_BEFORE_MOTION_UPDATE = 5;
    private static final int CLOCK_TICKS_BEFORE_VELOCITY_UPDATE = CLOCK_TICKS_BEFORE_MOTION_UPDATE * 10;
    private static final double MOTION_UPDATE_PERIOD = NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT * CLOCK_TICKS_BEFORE_MOTION_UPDATE;
    private static final double VELOCITY_UPDATE_PERIOD = NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT * CLOCK_TICKS_BEFORE_VELOCITY_UPDATE;
    private static final double MIN_VELOCITY = 500;  // In nanometers per second of sim time.
    private static final double MAX_VELOCITY = 5000; // In nanometers per second of sim time.

    private final Point2D awayPoint;

    private double motionUpdateCountdownTimer;
    private double velocityUpdateCountdownTimer;
    private double preFadeCountdownTimer;
    private double fadeOutDuration;
    private MutableVector2D velocity = new MutableVector2D();

    /**
     * Constructor.
     *
     * @param awayPoint       - Point that should be moved away from.
     * @param currentLocation - Starting location
     * @param preFadeTime     - Time before fade out starts, in sim time
     * @param fadeOutDuration - Time of fade out
     */
    public WanderAwayThenFadeMotionStrategy( Point2D awayPoint, Point2D currentLocation, double preFadeTime,
                                             double fadeOutDuration ) {

        this.awayPoint = awayPoint;
        this.preFadeCountdownTimer = preFadeTime;
        this.fadeOutDuration = fadeOutDuration;

        // Set up random offsets so that all the particles using this motion
        // strategy don't all get updated at the same time.
        motionUpdateCountdownTimer = RAND.nextInt( CLOCK_TICKS_BEFORE_MOTION_UPDATE ) * NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT;
        velocityUpdateCountdownTimer = RAND.nextInt( CLOCK_TICKS_BEFORE_VELOCITY_UPDATE ) * NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT;

        // Set an initial velocity and direction.
        updateVelocity( currentLocation );
    }

    @Override
    public void move( IMovable movableModelElement, IFadable fadableModelElement, double dt ) {

        motionUpdateCountdownTimer -= dt;
        if ( motionUpdateCountdownTimer <= 0 ) {
            // Time to update the motion.
            movableModelElement.setPosition(
                    movableModelElement.getPositionReference().getX() + velocity.getX() * MOTION_UPDATE_PERIOD,
                    movableModelElement.getPositionReference().getY() + velocity.getY() * MOTION_UPDATE_PERIOD );

            motionUpdateCountdownTimer = MOTION_UPDATE_PERIOD;
        }

        velocityUpdateCountdownTimer -= dt;
        if ( velocityUpdateCountdownTimer <= 0 ) {
            // Time to update the velocity.
            updateVelocity( movableModelElement.getPositionReference() );
            velocityUpdateCountdownTimer = VELOCITY_UPDATE_PERIOD;
        }

        if ( preFadeCountdownTimer >= 0 ) {
            preFadeCountdownTimer -= dt;
            if ( preFadeCountdownTimer <= 0 ) {
                // Time to start the fade out.
                fadableModelElement.setFadeStrategy( new TimedFadeAwayStrategy( fadeOutDuration ) );
            }
        }
    }

    private void updateVelocity( Point2D currentPosition ) {
        // Create a velocity vector that causes this to move away from the "away
        // point".
        double awayAngle = Math.atan2( currentPosition.getY() - awayPoint.getY(),
                                       currentPosition.getX() - awayPoint.getX() ) + ( RAND.nextDouble() - 0.5 ) * Math.PI;
        double newScalerVelocity = MIN_VELOCITY + RAND.nextDouble() * ( MAX_VELOCITY - MIN_VELOCITY );
        velocity.setComponents( newScalerVelocity * Math.cos( awayAngle ), newScalerVelocity * Math.sin( awayAngle ) );
    }
}
