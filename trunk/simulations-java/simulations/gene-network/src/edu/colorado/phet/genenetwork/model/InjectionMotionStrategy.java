// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * Motion strategy that moves seeks to look like something that was injected,
 * and thus moves linearly for a while, slows down, and then goes to a random
 * walk.
 *
 * @author John Blanco
 */
public class InjectionMotionStrategy extends AbstractMotionStrategy {

    private static final double RATE_OF_SLOWING = 0.3;          // Proportion per second.
    private static final double PRE_RANDOM_WALK_TIME = 4.0;     // In seconds.
    private static final Random RAND = new Random();

    private double preRandomWalkCountdown = PRE_RANDOM_WALK_TIME;
    private RandomWalkMotionStrategy randomWalkMotionStrategy;
    private final MutableVector2D initialVelocity = new MutableVector2D();
    private boolean initialVelocitySet = false;

    public InjectionMotionStrategy( Rectangle2D bounds, MutableVector2D initialVelocity ) {
        super( bounds );
        this.initialVelocity.setComponents( initialVelocity.getX(), initialVelocity.getY() );
        randomWalkMotionStrategy = new RandomWalkMotionStrategy( bounds );
    }

    @Override
    public void updatePositionAndMotion( double dt, SimpleModelElement modelElement ) {

        if ( !initialVelocitySet ) {
            modelElement.setVelocity( initialVelocity );
            initialVelocitySet = true;
        }

        Point2D position = modelElement.getPositionRef();
        MutableVector2D velocity = modelElement.getVelocityRef();

        if ( preRandomWalkCountdown > 0 ) {
            if ( ( position.getX() > getBounds().getMaxX() && velocity.getX() > 0 ) ||
                 ( position.getX() < getBounds().getMinX() && velocity.getX() < 0 ) ||
                 ( position.getY() > getBounds().getMaxY() && velocity.getY() > 0 ) ||
                 ( position.getY() < getBounds().getMinY() && velocity.getY() < 0 ) ) {

                // We are out of bounds, so we need to "bounce".
                if ( ( position.getX() > getBounds().getMaxX() && velocity.getX() > 0 ) ||
                     ( position.getX() < getBounds().getMinX() && velocity.getX() < 0 ) ) {
                    // Reverse velocity in the X direction.
                    velocity.setComponents( -velocity.getX(), velocity.getY() );
                }
                if ( ( position.getY() > getBounds().getMaxY() && velocity.getY() > 0 ) ||
                     ( position.getY() < getBounds().getMinY() && velocity.getY() < 0 ) ) {
                    // Reverse velocity in the Y direction.
                    velocity.setComponents( velocity.getX(), -velocity.getY() );
                }
            }
            else {
                // We are in bounds.  Based on a probability that increases as
                // time goes on, decide whether to simulate a "bump".
                if ( RAND.nextDouble() > 0.9 + ( 0.1 * ( preRandomWalkCountdown / PRE_RANDOM_WALK_TIME ) ) ) {
                    velocity.rotate( RAND.nextDouble() * Math.PI / 4 );
                }
            }

            if ( modelElement.getVelocityRef().getMagnitude() > 0 ) {
                // Update position.
                modelElement.setPosition(
                        modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX() * dt,
                        modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() * dt );
                // Slow down.
                velocity.scale( 1 - ( RATE_OF_SLOWING * dt ) );
            }
            preRandomWalkCountdown -= dt;
        }
        else {
            randomWalkMotionStrategy.doUpdatePositionAndMotion( dt, modelElement );
        }
    }
}
