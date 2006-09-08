/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class RecordMode extends Mode {
    private PhetTimer timer;
    private TimeSeriesModel timeSeriesModel;

    public RecordMode( final TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "Record", true );
        timer = new PhetTimer( "Record Timer" );
        this.timeSeriesModel = timeSeriesModel;
    }

    public void initialize() {
        timeSeriesModel.repaintBackground();
        double recTime = timeSeriesModel.getRecordTime();
        timeSeriesModel.setReplayTime( recTime );
        timeSeriesModel.repaintBackground();
    }

    public void reset() {
        timer.reset();
    }

    public PhetTimer getTimer() {
        return timer;
    }

    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        double recorderTime = timer.getTime();
        double maxTime = timeSeriesModel.getMaxAllowedTime();
        if( !timeSeriesModel.isPaused() ) {
//            System.out.println( "System.currentTimeMillis() = " + System.currentTimeMillis() );
//            if( recorderTime >= maxTime ) {
//                timeSeriesModel.recordingFinished();
//                return;
//            }

            double newTime = recorderTime + dt;// * timer.getTimerScale();
            if( newTime > maxTime ) {
                dt = ( maxTime - recorderTime );// / timer.getTimerScale();
            }
            timer.stepInTime( dt, maxTime );//this could go over the max.

            timeSeriesModel.updateModel( event );

//            if( newTime >= maxTime ) {
//                timeSeriesModel.recordingFinished();
//                return;
//            }
        }
    }

    public void clockTickedORIG( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        double recorderTime = timer.getTime();
        double maxTime = timeSeriesModel.getMaxAllowedTime();
        if( !timeSeriesModel.isPaused() ) {
            if( recorderTime >= maxTime ) {
                timeSeriesModel.recordingFinished();
                return;
            }

            double newTime = recorderTime + dt;// * timer.getTimerScale();
            if( newTime > maxTime ) {
                dt = ( maxTime - recorderTime );// / timer.getTimerScale();
            }
            timer.stepInTime( dt, maxTime );//this could go over the max.
            timeSeriesModel.updateModel( event );

            if( newTime >= maxTime ) {
                timeSeriesModel.recordingFinished();
                return;
            }
        }
    }
}
