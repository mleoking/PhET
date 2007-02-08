/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.ec3.EnergySkateParkApplication;
import edu.colorado.phet.ec3.EnergySkateParkStrings;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class RecordMode extends Mode {
    private PhetTimer timer;

    public RecordMode( final TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, EnergySkateParkStrings.getString( "record" ) );
        timer = new PhetTimer( EnergySkateParkStrings.getString( "record.timer" ) );
    }

    public void initialize() {
//        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
//        double recTime = timeSeriesModel.getRecordTime();
//        timeSeriesModel.setReplayTime( recTime );
    }

    public void step() {
        doStep( EnergySkateParkApplication.SIMULATION_TIME_DT );
    }

    public void reset() {
        timer.reset();
    }

    public PhetTimer getTimer() {
        return timer;
    }

    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        if( !getTimeSeriesModel().isPaused() ) {
            doStep( dt );
        }
    }

    private void doStep( double dt ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        double recorderTime = timer.getTime();
        double maxTime = timeSeriesModel.getMaxAllowedTime();
        double newTime = recorderTime + dt;// * timer.getTimerScale();
        if( newTime > maxTime ) {
            dt = ( maxTime - recorderTime );// / timer.getTimerScale();
        }
        timer.stepInTime( dt, maxTime );//this could go over the max.
        timeSeriesModel.updateModel( dt );
        timeSeriesModel.addSeriesPoint( timeSeriesModel.getModelState(), timeSeriesModel.getRecordTime() );

//        System.out.println( "timeSeriesModel.getSeries().size() = " + timeSeriesModel.getSeries().size() );
//        int NUM_TO_RECORD_ADDITIONAL = 20;
//        for( int i = 0; i < NUM_TO_RECORD_ADDITIONAL; i++ ) {
//            timeSeriesModel.addSeriesPoint( timeSeriesModel.getModelState(), timeSeriesModel.getRecordTime() );
//        }

//        while( timeSeriesModel.getSeries().size() > MAX ) {
////            timeSeriesModel.getSeries().remove( 0 );
//            timeSeriesModel.getSeries().remove( 0 );
//        }
    }

    public void initAgain( TimeSeriesModel timeSeriesModel ) {
//        timeSeriesModel.setLastPoint();
    }
}
