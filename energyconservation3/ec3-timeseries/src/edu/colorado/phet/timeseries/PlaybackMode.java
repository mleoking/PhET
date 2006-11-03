/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.ec3.EnergySkateParkApplication;
import edu.colorado.phet.ec3.EnergySkateParkStrings;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:16 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class PlaybackMode extends Mode {
    private double playbackSpeed;
    private PhetTimer timer;
    private TimeSeriesModel timeSeriesModel;

    public PlaybackMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, EnergySkateParkStrings.getString( "playback" ) );
        this.timeSeriesModel = timeSeriesModel;
        timer = new PhetTimer( EnergySkateParkStrings.getString( "playback.timer" ) );
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed( double playbackSpeed ) {
        this.playbackSpeed = playbackSpeed;
    }

    public void initialize() {
    }

    public void step() {
        setPlaybackSpeed( 1.0 );
        doStep( EnergySkateParkApplication.SIMULATION_TIME_DT );
    }

    public void clockTicked( ClockEvent event ) {
//        System.out.println( "PlaybackMode.clockTicked" );
        double dt = event.getSimulationTimeChange();
        step( dt );
    }

    private void step( double dt ) {
        if( !timeSeriesModel.isPaused() ) {
            doStep( dt );
        }
    }

    private void doStep( double dt ) {
        timeSeriesModel.getPlaybackTimer().stepInTime( dt * playbackSpeed, timeSeriesModel.getRecordTimer().getTime() );
        double playTime = timeSeriesModel.getPlaybackTimer().getTime();
        double recTime = timeSeriesModel.getRecordTimer().getTime();
        if( playTime < recTime ) {
            timeSeriesModel.setReplayTime( playTime );
        }
        else {
            timeSeriesModel.setPaused( true );
            timeSeriesModel.firePlaybackFinished();
        }
    }

    public void reset() {
        timer.reset();
    }

    public void rewind() {
        timeSeriesModel.setReplayTime( 0.0 );
        timer.setTime( 0 );
    }

    public PhetTimer getTimer() {
        return timer;
    }
}
