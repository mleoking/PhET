/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.ec3.EnergySkateParkApplication;
import edu.colorado.phet.ec3.EnergySkateParkStrings;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 12:20:16 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class LiveMode extends Mode {
    public LiveMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, EnergySkateParkStrings.getString( "play" ) );
    }

    public void initialize() {
        //11-29-2006: Removed this code to fix this problem:
        //Record, pause, switch location, go should remain in the newly selected location.

//        if( getTimeSeriesModel().getSeries().getLastPoint() != null ) {
//            getTimeSeriesModel().setModelState( getTimeSeriesModel().getSeries().getLastPoint().getValue() );
//        }
    }

    public void step() {
        getTimeSeriesModel().updateModel( EnergySkateParkApplication.SIMULATION_TIME_DT );
    }

    public void clockTicked( ClockEvent event ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        if( !timeSeriesModel.isPaused() ) {
            timeSeriesModel.updateModel( event.getSimulationTimeChange() );
        }
    }
}
