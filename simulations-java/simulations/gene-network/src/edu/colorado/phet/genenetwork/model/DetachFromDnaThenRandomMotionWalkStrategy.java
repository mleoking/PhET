// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class defines a motion strategy that causes an element to appear as
 * though it detaches from the DNA strand - meaning that it moves up a bit -
 * and then starts a random walk.  This arose due to model elements sometimes
 * detaching from the DNA and then moving right behind it, which didn't look
 * so good.
 *
 * @author John Blanco
 */
public class DetachFromDnaThenRandomMotionWalkStrategy extends RandomWalkMotionStrategy {

    private static double MOVE_AWAY_TIME = 3; // In seconds.
    private static Random RAND = new Random();

    private double delayCounter;
    private double linearMotionCountdown = MOVE_AWAY_TIME;
    private Vector2D initialVelocity = new Vector2D();

    public DetachFromDnaThenRandomMotionWalkStrategy( Rectangle2D bounds, double preMovementDelay,
                                                      Vector2D initialVelocity, double linearMotionTime ) {

        super( bounds );

        delayCounter = preMovementDelay;

        if ( linearMotionTime >= 0 ) {
            linearMotionCountdown = linearMotionTime;
        }
        if ( initialVelocity != null ) {
            this.initialVelocity.setComponents( initialVelocity.getX(), initialVelocity.getY() );
        }
        else {
            // Create an initial somewhat random velocity that is generally up.
            double initialTotalVelocity = RAND.nextDouble() * ( MAX_DIRECTED_VELOCITY - MIN_DIRECTED_VELOCITY ) + MIN_DIRECTED_VELOCITY;
            double initialAngle = Math.PI / 4.0 + ( RAND.nextDouble() * ( Math.PI / 2.0 ) );
            this.initialVelocity.setX( initialTotalVelocity * Math.cos( initialAngle ) );
            this.initialVelocity.setY( initialTotalVelocity * Math.sin( initialAngle ) );
        }
    }

    @Override
    public void updatePositionAndMotion( double dt, SimpleModelElement modelElement ) {

        if ( delayCounter > 0 ) {
            // Do nothing.
            delayCounter -= dt;
        }
        else if ( linearMotionCountdown > 0 ) {
            // Move up.
            linearMotionCountdown -= dt;
            Point2D currentPos = modelElement.getPositionRef();
            modelElement.setPosition( currentPos.getX() + initialVelocity.getX() * dt,
                                      currentPos.getY() + initialVelocity.getY() * dt );
        }
        else {
            // Done delaying and moving up, so just do random walk.
            super.updatePositionAndMotion( dt, modelElement );
        }
    }
}
