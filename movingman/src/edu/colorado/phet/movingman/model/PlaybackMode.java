/** Sam Reid*/
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MovingManModule;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:16 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class PlaybackMode extends Mode {
    private double playbackSpeed;
    private MovingManModule module;

    public PlaybackMode( MovingManModule module, MovingManTimeModel movingManTimeModel ) {
        super( module, SimStrings.get( "PlaybackMode.ModeName" ), false );
        this.module = module;
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed( double playbackSpeed ) {
        this.playbackSpeed = playbackSpeed;
    }

    public void initialize() {
        module.setCursorsVisible( true );
        module.repaintBackground();
    }

    public void stepInTime( double dt ) {
        if( !module.isPaused() ) {
            module.getPlaybackTimer().stepInTime( dt * playbackSpeed, module.getRecordingTimer().getTime() );
            double playTime = module.getPlaybackTimer().getTime();
            double recTime = module.getRecordingTimer().getTime();
            if( playTime < recTime ) {
                module.setReplayTime( playTime );
            }
            else {
                module.setPaused( true );
                module.firePlaybackFinished();
            }
        }
    }
}
