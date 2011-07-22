// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Provides physical locations (positions) of the atoms within a Sucrose molecule.
 * Positions sampled from a 2d rasterized view of sucrose from JMol with ProjectorUtil
 *
 * @author Sam Reid
 */
public class EthanolPositions {
    public static final String positions = "H 426 253\n" +
                                           "H 412 508\n" +
                                           "H 230 437\n" +
                                           "C 333 483\n" +
                                           "C 405 348\n" +
                                           "O 554 393\n" +
                                           "H 603 304\n" +
                                           "H 315 574\n" +
                                           "H 324 305\n";

    //Origin was copied from the first position
    private static final ImmutableVector2D origin = new ImmutableVector2D( 426, 253 );

    //Normalize by subtracting the origin and scaling
    public static ImmutableVector2D normalize( final ImmutableVector2D position ) {
        return position.minus( origin ).times( WaterMolecule.oxygenRadius * 10 / 800 * 0.7 * 0.4 );
    }
}