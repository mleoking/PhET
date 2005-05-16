/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.timeseries;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.timeseries.ObjectTimeSeries;
import edu.colorado.phet.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 8:33:35 AM
 * Copyright (c) May 16, 2005 by Sam Reid
 */

public class RampTimeSeriesModel extends TimeSeriesModel {
    private RampModule rampModule;
    private ObjectTimeSeries series = new ObjectTimeSeries();

    public RampTimeSeriesModel( RampModule rampModule ) {
        this.rampModule = rampModule;
    }

    public void repaintBackground() {
        rampModule.repaintBackground();
    }

    public void setCursorsVisible( boolean b ) {
        rampModule.setCursorsVisible( b );
    }

    public void updateModel( ClockTickEvent clockEvent ) {
        rampModule.updateModel( clockEvent.getDt() );
        RampModel state = rampModule.getRampModel().getState();
//        timeSeries.addPoint( state, time );
        series.addPoint( state, clockEvent.getClock().getRunningTime() );
        System.out.println( "series.numPoints() = " + series.numPoints() );
    }


}
