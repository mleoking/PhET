/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

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
