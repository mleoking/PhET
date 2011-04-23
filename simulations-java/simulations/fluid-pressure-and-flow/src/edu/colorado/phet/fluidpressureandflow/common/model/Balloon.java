// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

/**
 * This was a feasibility test for showing a balloon and its compression under different pressures.
 * Currently not used since under real world scenarios the change in size of a balloon would be too small to see
 * But we left this feature here in case we add support for it later (or we may use a cartoon scaling to make the effect more apparent).
 *
 * @author Sam Reid
 */
public class Balloon extends PressureSensor {
    public Balloon( final Context context, double x, double y ) {
        super( context, x, y );
    }

    double K = 5000;

    //Boyle's law pV = constant = K
    //V = 4/3pi R3
    public double getRadius() {
        final double volume = K / getValue();
        double fourThirdsPi = 4.0 / 3.0 * Math.PI;
        final double arg = volume / fourThirdsPi;
        final double radius = Math.pow( arg, 1.0 / 3.0 );
//        return radius;
        return Math.pow( radius, 3 ) * 30;//TODO: this will give incorrect values, maybe we need a separate mode that lets you submerge to a much deeper level
    }
}
