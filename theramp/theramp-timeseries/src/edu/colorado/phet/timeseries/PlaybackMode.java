/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;


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
        super( timeSeriesModel, "Playback", false );
        this.timeSeriesModel = timeSeriesModel;
        timer = new PhetTimer( "Playback Timer" );
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed( double playbackSpeed ) {
        this.playbackSpeed = playbackSpeed;
    }

    public void initialize() {
        timeSeriesModel.repaintBackground();
    }

    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        if( !timeSeriesModel.isPaused() ) {
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
