/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.timeseries.model;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 12:20:16 PM
 */

public class LiveMode extends Mode {

    private double time = 0;

    public LiveMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel );
    }

    public void step( double dt ) {
        time += dt;
        getTimeSeriesModel().updateModel( dt );
    }

    public double getTime() {
        return time;
    }
}
