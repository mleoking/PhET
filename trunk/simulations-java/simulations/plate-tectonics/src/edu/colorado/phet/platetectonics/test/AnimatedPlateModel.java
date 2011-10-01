// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;

import edu.colorado.phet.platetectonics.model.PlateModel;

/**
 * Testing model that supports animation
 */
public class AnimatedPlateModel implements PlateModel {
    private double time;

    public void update( double timeElapsed ) {
        time += timeElapsed;
    }

    public double getElevation( double x, double z ) {
        return -10000 + ( x * x + z * z ) / 200000 + 1000 * ( Math.cos( x / 1000 + time ) - Math.sin( z / 1000 + 2 * time ) )
               + Math.sin( x / 10000 + time / 4 ) * 5000;
    }

    public double getDensity( double x, double y ) {
        return 3000 - y / 100;
    }

    public double getTemperature( double x, double y ) {
        return 290 - y / 1000;
    }
}
