/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries_ramp;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 12:20:16 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class LiveMode extends Mode {
    public LiveMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "Live", false );
    }

    public void initialize() {
    }

    public void clockTicked( ClockEvent event ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        if( !timeSeriesModel.isPaused() ) {
            timeSeriesModel.updateModel( event );
        }
    }
}
