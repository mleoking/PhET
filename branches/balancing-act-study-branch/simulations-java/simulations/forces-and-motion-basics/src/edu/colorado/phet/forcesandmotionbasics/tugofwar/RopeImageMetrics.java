// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.F;
import fj.data.List;

import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;

/**
 * Location of hot spots within images, used by TugOfWarCanvas to correct the layout and behavior of the knots.
 *
 * @author Sam Reid
 */
class RopeImageMetrics {
    public static final List<Double> blueKnots = List.list( 10.0, 90.0, 170.0, 250.0 );
    public static final List<Double> redKnots = blueKnots.map( new F<Double, Double>() {
        @Override public Double f( final Double a ) {
            return Images.ROPE.getWidth() - a;
        }
    } );
}