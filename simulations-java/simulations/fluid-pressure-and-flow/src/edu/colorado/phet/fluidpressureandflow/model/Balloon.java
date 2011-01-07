// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

/**
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
