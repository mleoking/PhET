// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;

import edu.colorado.phet.platetectonics.model.PlateModel;

public class VerySimplePlateModel extends PlateModel {

    private static final int LEFT_BOUNDARY = -50000;
    private static final int RIGHT_BOUNDARY = 50000;

    @Override public double getElevation( double x, double z ) {
        if ( x < LEFT_BOUNDARY ) {
            // left "oceanic" plate
            return -3000;
        }
        if ( x > RIGHT_BOUNDARY ) {
            return 5000;
        }
        return ( -x ) / 10 + 1000 * ( Math.cos( x / 1000 ) );
    }

    @Override public double getDensity( double x, double y ) {
        if ( x < LEFT_BOUNDARY ) {
            if ( y > -10000 ) {
                return 3000;
            }
            else {
                return 3300;
            }
        }
        if ( x > RIGHT_BOUNDARY ) {
            if ( y > -40000 ) {
                return 2700;
            }
            else {
                return 3300;
            }
        }
        if ( y > -18000 - 2 * getElevation( x, 0 ) ) {
            return 2800;
        }
        else {
            return 3300;
        }
    }

    @Override public double getTemperature( double x, double y ) {
        return 290 - y / 1000;
    }
}
