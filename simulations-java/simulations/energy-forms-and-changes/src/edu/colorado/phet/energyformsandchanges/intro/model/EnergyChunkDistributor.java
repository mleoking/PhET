// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A class that contains static methods for redistributing a set of energy
 * chunks.
 *
 * @author John Blanco
 */
public class EnergyChunkDistributor {

    private static final double MAX_TIME_STEP = 0.1; // In seconds.
    private static final Random RAND = new Random();

    public static void distribute( Rectangle2D rect, List<EnergyChunk> energyChunkList ) {
        for ( EnergyChunk energyChunk : energyChunkList ) {
            energyChunk.position.set( new ImmutableVector2D( rect.getX() + RAND.nextDouble() * rect.getWidth(),
                                                             rect.getY() + RAND.nextDouble() * rect.getHeight() ) );
        }
    }
}
