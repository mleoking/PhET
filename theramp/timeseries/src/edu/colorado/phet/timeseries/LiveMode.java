/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockTickEvent;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 12:20:16 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class LiveMode extends Mode {
    public LiveMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "Live" );
    }

    public void initialize() {
    }

    public void clockTicked( ClockTickEvent event ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
//        double dt = event.getDt();
//        double recorderTime = timer.getTime();
//        double maxTime = timeSeriesModel.getMaxAllowedTime();
        if( !timeSeriesModel.isPaused() ) {
//            double newTime = recorderTime + dt;// * timer.getTimerScale();
//            if( newTime > maxTime ) {
//                dt = ( maxTime - recorderTime );// / timer.getTimerScale();
//            }
//            timer.stepInTime( dt, maxTime );//this could go over the max.
            timeSeriesModel.updateModel( event );
        }
    }
}
