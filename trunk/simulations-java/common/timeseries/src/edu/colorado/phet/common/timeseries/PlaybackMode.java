package edu.colorado.phet.common.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:16 PM
 */
public class PlaybackMode extends Mode {
    private double playbackSpeed;
    private PhetTimer timer;
    private TimeSeriesModel timeSeriesModel;
    private static final double SIMULATION_TIME_DT = 1.0;

    public PlaybackMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "playback" );
        this.timeSeriesModel = timeSeriesModel;
        timer = new PhetTimer( "playback.timer" );
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed( double playbackSpeed ) {
        this.playbackSpeed = playbackSpeed;
    }

    public void step() {
        setPlaybackSpeed( 1.0 );
        doStep( SIMULATION_TIME_DT );
    }

    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        step( dt );
    }

    private void step( double dt ) {
        if( !timeSeriesModel.isPaused() ) {
            doStep( dt );
        }
    }

    public double getPlaybackTime() {
        return timeSeriesModel.getPlaybackTimer().getTime();
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
