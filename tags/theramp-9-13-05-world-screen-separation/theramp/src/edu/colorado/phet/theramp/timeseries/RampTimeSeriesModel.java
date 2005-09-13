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
    private boolean recordedLastTime = false;

    public RampTimeSeriesModel( RampModule rampModule ) {
        super( RampModule.MAX_TIME );
        this.rampModule = rampModule;
    }

    public void repaintBackground() {
        rampModule.repaintBackground();
    }

    public void updateModel( ClockTickEvent clockEvent ) {
        rampModule.updateModel( clockEvent.getDt() );

//        timeSeries.addPoint( state, time );
        if( getRecordTime() <= RampModule.MAX_TIME && !recordedLastTime ) {
//            System.out.println( "getRecordTime() = " + getRecordTime() );
            RampPhysicalModel state = rampModule.getRampPhysicalModel().getState();
            series.addPoint( state, getRecordTime() );
            rampModule.updatePlots( state, getRecordTime() );
            if( getRecordTime() >= RampModule.MAX_TIME ) {
                recordedLastTime = true;
            }
        }
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
        recordedLastTime = false;
        rampModule.clearHeatSansFiredog();
    }

    protected ApparatusPanel getApparatusPanel() {
        return rampModule.getApparatusPanel();
    }

    protected boolean confirmReset() {
        return rampModule.resetDialogOk();
//        int answer = JOptionPane.showConfirmDialog( getApparatusPanel(), "Are you sure you'd like to clear?", "Confirm Clear", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
//        return answer == JOptionPane.OK_OPTION;
    }


    public RampModule getRampModule() {
        return rampModule;
    }

}
