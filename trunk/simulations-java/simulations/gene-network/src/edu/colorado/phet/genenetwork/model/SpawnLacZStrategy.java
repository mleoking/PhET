// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * Spawning strategy that creates LacZ.
 *
 * @author John Blanco
 */
public class SpawnLacZStrategy extends MessengerRnaSpawningStrategy {

    private static final double PRE_SPAWN_TIME = 4;          // In seconds of sim time.
    private static final double TIME_BETWEEN_SPAWNINGS = 2;  // In seconds of sim time.
    private static final Random RAND = new Random( 4324 );
    private static final int MIN_AUTO_GEN_SPAWN_COUNT = 2;
    private static final int MAX_AUTO_GEN_SPAWN_COUNT = 3;

    private double spawnCountdownTimer = PRE_SPAWN_TIME;
    private int spawnCount = Integer.MAX_VALUE;

    /**
     * Constructor where the number of elements to spawn is specified.
     *
     * @param numberToSpawn
     */
    public SpawnLacZStrategy( int numberToSpawn ) {
        super();
        spawnCount = numberToSpawn;
    }

    /**
     * Constructor that will choose the number to spawn.
     */
    public SpawnLacZStrategy() {
        this( MIN_AUTO_GEN_SPAWN_COUNT + RAND.nextInt( MAX_AUTO_GEN_SPAWN_COUNT - MIN_AUTO_GEN_SPAWN_COUNT + 1 ) );
    }

    @Override
    public boolean isSpawningComplete() {
        // True if all the progeny has been created.
        return spawnCount == 0;
    }

    @Override
    public void stepInTime( double dt, SimpleModelElement parentModelElement ) {
        if ( spawnCountdownTimer != Double.POSITIVE_INFINITY ) {
            spawnCountdownTimer -= dt;
            if ( spawnCountdownTimer <= 0 ) {
                // Time to spawn.
                assert spawnCount > 0;
                spawnLacZ( parentModelElement );
                spawnCount--;
                if ( spawnCount > 0 ) {
                    // Set the timer for the next spawning.
                    spawnCountdownTimer = TIME_BETWEEN_SPAWNINGS;
                }
                else {
                    // No more spawning to be done.
                    spawnCountdownTimer = Double.POSITIVE_INFINITY;
                }

            }
        }
    }

    protected void spawnLacZ( SimpleModelElement parentModelElement ) {
        // Create and position the transformation arrow, which will in turn
        // create the LacZ.
        Rectangle2D bounds = parentModelElement.getShape().getBounds2D();
        Point2D transformationArrowPos = new Point2D.Double( bounds.getCenterX() +
                                                             parentModelElement.getPositionRef().getX() + 3, bounds.getMaxY() +
                                                                                                             parentModelElement.getPositionRef().getY() + 1 );
        LacZTransformationArrow transformationArrow = new LacZTransformationArrow( parentModelElement.getModel(),
                                                                                   transformationArrowPos, new LacZ( parentModelElement.getModel(), true ), Math.PI / 4 );
        transformationArrow.setMotionStrategy( new LinearMotionStrategy(
                parentModelElement.getModel().getInteriorMotionBoundsAboveDna(), transformationArrowPos,
                new MutableVector2D( parentModelElement.getVelocityRef() ), 5.0 ) );
        parentModelElement.getModel().addTransformationArrow( transformationArrow );
    }
}
