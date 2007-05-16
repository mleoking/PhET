/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 12:20:16 PM
 *
 */

public class LiveMode extends Mode {
    private static final double SIMULATION_TIME_DT = 1.0;

    public LiveMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "live");
    }

    public void step() {
        getTimeSeriesModel().updateModel( SIMULATION_TIME_DT );
    }

    public void clockTicked( ClockEvent event ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        if( !timeSeriesModel.isPaused() ) {
            timeSeriesModel.updateModel( event.getSimulationTimeChange() );
        }
    }
}
