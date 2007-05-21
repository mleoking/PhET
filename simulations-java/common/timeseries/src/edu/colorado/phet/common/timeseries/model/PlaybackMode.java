package edu.colorado.phet.common.timeseries.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:16 PM
 */
public class PlaybackMode extends Mode {
    private double playbackSpeed;
    private double playbackTime;
    private ArrayList listeners = new ArrayList();

    public PlaybackMode( TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel );
        playbackTime = 0.0;
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed( double playbackSpeed ) {
        this.playbackSpeed = playbackSpeed;
    }

    public double getPlaybackTime() {
        return playbackTime;
    }

    public void step( double dt ) {
        playbackTime += dt * playbackSpeed;
        if( playbackTime > getTimeSeriesModel().getRecordTime() ) {
            playbackTime = getTimeSeriesModel().getRecordTime();
        }
        if( playbackTime < getTimeSeriesModel().getRecordTime() ) {
            getTimeSeriesModel().setPlaybackTime( playbackTime );
        }
        else {
            getTimeSeriesModel().setPaused( true );
        }
        notifyPlaybackTimeChanged();
    }

    private void notifyPlaybackTimeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModel.PlaybackTimeListener playbackTimeListener = (TimeSeriesModel.PlaybackTimeListener)listeners.get( i );
            playbackTimeListener.timeChanged();
        }
    }

    public void setTime( double requestedTime ) {
        if( this.playbackTime != requestedTime ) {
            this.playbackTime = requestedTime;
            notifyPlaybackTimeChanged();
        }
    }

    public void addListener( TimeSeriesModel.PlaybackTimeListener playbackTimeListener ) {
        listeners.add( playbackTimeListener );
    }
}
