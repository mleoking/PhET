// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp.model;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.timeseries.ObjectTimePoint;
import edu.colorado.phet.theramp.timeseries.ObjectTimeSeries;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 8:33:35 AM
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

    public void updateModel( ClockEvent clockEvent ) {
        rampModule.updateModel( clockEvent.getSimulationTimeChange() );

//        timeSeries.addPoint( state, time );
        if ( getRecordTime() <= RampModule.MAX_TIME && !recordedLastTime ) {
//            System.out.println( "getRecordTime() = " + getRecordTime() );
            RampPhysicalModel state = rampModule.getRampPhysicalModel().getState();
            series.addPoint( state, getRecordTime() );
            rampModule.updatePlots( state, getRecordTime() );
            if ( getRecordTime() >= RampModule.MAX_TIME ) {
                recordedLastTime = true;
            }
        }
        else {
            rampModule.updateReadouts();
        }
    }

    public void setReplayTime( double requestedTime ) {
        super.setReplayTime( requestedTime );

        ObjectTimePoint value = series.getValueForTime( requestedTime );
        if ( value != null ) {
            RampPhysicalModel v = (RampPhysicalModel) value.getValue();
            if ( v != null ) {
                rampModule.getRampPhysicalModel().setState( v );
            }
        }
        rampModule.updateReadouts();
    }

    public void reset() {
        super.reset();
        series.reset();
        rampModule.getRampPlotSet().reset();
        recordedLastTime = false;
        rampModule.clearHeatSansFiredog();
    }

//    protected ApparatusPanel getApparatusPanel() {
//        return rampModule.getApparatusPanel();
//    }

    protected boolean confirmReset() {
//        return rampModule.resetDialogOk();
        int answer = JOptionPane.showConfirmDialog( getSimulationPanel(), TheRampStrings.getString( "confirm-clear-graphs" ), TheRampStrings.getString( "confirm.clear" ), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
        return answer == JOptionPane.OK_OPTION;
    }

    public JComponent getSimulationPanel() {
        return rampModule.getSimulationPanel();
    }

    public RampModule getRampModule() {
        return rampModule;
    }

    public void clockTicked( ClockEvent event ) {
        super.clockTicked( event );
        if ( isPaused() ) {
            rampModule.updateReadouts();
        }
    }

//    public void setRecordMode() {
//        Mode origMode = getMode();
//        super.setRecordMode();
//        if( origMode != getRecordMode() ) {
//            if( series.size() > 0 ) {
//                ObjectTimePoint value = series.getLastPoint();
//                if( value != null ) {
//                    RampPhysicalModel v = (RampPhysicalModel)value.getValue();
//                    if( v != null ) {
//                        rampModule.getRampPhysicalModel().setState( v );
//                    }
//                }
//            }
//        }
//    }

}
