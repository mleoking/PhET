package edu.colorado.phet.rotation.timeseries;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 1:00:05 PM
 */

public class TimeSeriesModel {
    private ArrayList listeners = new ArrayList();

    private Mode recordMode = new Mode( "record" );
    private Mode playbackMode = new Mode( "playback" );
    private Mode slowMotionMode = new Mode( "slow" );
    private Mode currentMode = recordMode;
    private boolean paused = false;
    private ArrayList playbackStates = new ArrayList();
    private int playbackIndex = 0;

    public boolean isRecording() {
        return currentMode == recordMode;
    }

    public boolean isPlayback() {
        return currentMode == playbackMode;
    }

    public boolean isSlowMotion() {
        return currentMode == slowMotionMode;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setRecording() {
        setMode( recordMode, true );
    }

    private void setMode( Mode mode, boolean unpause ) {
        this.currentMode = mode;
        if( unpause ) {
            unpause();
        }
        notifyListeners();
    }

    private void unpause() {
        this.paused = false;
        notifyListeners();
    }

    public void setPaused() {
        this.paused = true;
        notifyListeners();
    }

    public void setPlayback() {
        setMode( playbackMode, true );
    }

    public void setSlowMotion() {
        setMode( slowMotionMode, true );
    }

    public int numPlaybackStates() {
        return playbackStates.size();
    }

    public void rewind() {
        setPlaybackIndex( 0 );
    }

    private void setPlaybackIndex( int i ) {
        this.playbackIndex = i;
        setPlaybackState( playbackStates.get( i ) );
    }

    protected void setPlaybackState( Object o ) {
    }

    public void step() {
        setPlaybackIndex( getPlaybackIndex() + 1 );
    }

    public int getPlaybackIndex() {
        return playbackIndex;
    }

    public void clear() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.clear();
        }
    }

    public static interface Listener {
        void stateChanged();

        void clear();
    }
    

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.stateChanged();
        }
    }

    class Mode {
        String name;

        public Mode( String name ) {
            this.name = name;
        }

        public boolean equals( Object obj ) {
            return obj instanceof Mode && ( (Mode)obj ).name.equals( name );
        }
    }
}
