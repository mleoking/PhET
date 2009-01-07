package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.movingman.MovingManModule;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:16 PM
 */
public class PlaybackMode extends Mode {
    private double playbackSpeed;
    private MovingManModule module;
    private MMTimer timer;

    public PlaybackMode( MovingManModule module, MovingManTimeModel movingManTimeModel ) {
        super( module, SimStrings.get( "controls.playback" ), false );
        timer = new MMTimer( SimStrings.get( "controls.playback" ) );//, MovingManModel.TIMER_SCALE );
        this.module = module;
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed( double playbackSpeed ) {
        this.playbackSpeed = playbackSpeed;
    }

    public void initialize() {
        module.initialize();
        module.getMovingManApparatusPanel().setCursorsVisible( true );
    }

    public void stepInTime( double dt ) {
        if ( !module.isPaused() ) {
            module.getPlaybackTimer().stepInTime( dt * playbackSpeed, module.getRecordingTimer().getTime() );
            double playTime = module.getPlaybackTimer().getTime();
            double recTime = module.getRecordingTimer().getTime();
            if ( playTime < recTime ) {
                module.setReplayTime( playTime );
            }
            else {
                module.setPaused( true );
                module.firePlaybackFinished();
            }
        }
    }

    public void reset() {
        timer.reset();
    }

    public void rewind() {
        timer.setTime( 0 );
    }

    public MMTimer getTimer() {
        return timer;
    }
}
