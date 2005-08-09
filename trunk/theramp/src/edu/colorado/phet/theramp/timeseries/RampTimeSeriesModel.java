/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.timeseries;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.timeseries.ObjectTimePoint;
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

    public void updateModel( ClockTickEvent clockEvent ) {
        rampModule.updateModel( clockEvent.getDt() );
        RampPhysicalModel state = rampModule.getRampPhysicalModel().getState();
//        timeSeries.addPoint( state, time );
        series.addPoint( state, getRecordTime() );
        rampModule.updatePlots( state, getRecordTime() );
//        System.out.println( "series.numPoints() = " + series.numPoints() + ", running Time=" + clockEvent.getClock().getRunningTime() );
    }

    public void setReplayTime( double requestedTime ) {
//        System.out.println( "RampTimeSeriesModel.setReplayTime: " + requestedTime );
        super.setReplayTime( requestedTime );

        ObjectTimePoint value = series.getValueForTime( requestedTime );
        if( value != null ) {
            RampPhysicalModel v = (RampPhysicalModel)value.getValue();
            if( v != null ) {
                rampModule.getRampPhysicalModel().setState( v );
            }
        }
    }

    public void reset() {
        super.reset();
        series.reset();
        rampModule.getRampPlotSet().reset();
    }

    protected ApparatusPanel getApparatusPanel() {
        return rampModule.getApparatusPanel();
    }

}
