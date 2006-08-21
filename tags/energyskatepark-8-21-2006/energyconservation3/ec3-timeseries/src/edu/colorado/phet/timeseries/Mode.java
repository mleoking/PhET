/*PhET, 2004.*/
package edu.colorado.phet.timeseries;


import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 1:12:18 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public abstract class Mode implements ClockListener {
    private String name;
    private TimeSeriesModel timeSeriesModel;

    public Mode( TimeSeriesModel timeSeriesModel, String name ) {
        this.timeSeriesModel = timeSeriesModel;
        this.name = name;
    }

    public abstract void initialize();

    public String getName() {
        return name;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public void clockStarted( ClockEvent clockEvent ) {
    }

    public void clockPaused( ClockEvent clockEvent ) {
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}