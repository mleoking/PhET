// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;

import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.util.Grid3D;

/**
 * Testing model that supports animation
 */
public class AnimatedPlateModel extends PlateModel {
    private double time;

    public AnimatedPlateModel( Grid3D grid ) {
        super( grid );
    }

    @Override public void update( double timeElapsed ) {
        super.update( timeElapsed );
        time += timeElapsed;
        modelChanged.updateListeners();
    }

    @Override public double getElevation( double x, double z ) {
        x = x / 5;
        z = z / 5;
        return 1000 * ( Math.cos( x / 1000 + time ) - Math.sin( z / 1000 + 2 * time ) )
               + Math.sin( x / 10000 + time / 4 ) * 8000;
    }

    @Override public double getDensity( double x, double y ) {
        double elevation = getElevation( x, 0 );
        if ( y > elevation ) {
            return 2700;
        }
        else if ( y + 35000 > elevation ) {
            return 2700;
        }
        else if ( y + 120000 > elevation ) {
            return 3000;
        }
        else {
            return 3300;
        }
//        return x * x / 50000 > y + 50000 ? 2700 : 3300;
    }

    @Override public double getTemperature( double x, double y ) {
        return 290 - y / 1000;
    }
}
