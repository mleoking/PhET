package edu.colorado.phet.rotation.graphs;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 1:00:05 PM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

public class TimeSeriesModel {
    private ArrayList listeners = new ArrayList();
    private boolean recording = false;
    private boolean paused = false;
    private boolean playback;
    private boolean slowMotion;

    public boolean isRecording() {
        return recording;
    }

    public boolean isPlayback() {
        return playback;
    }

    public boolean isSlowMotion() {
        return slowMotion;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setRecording() {
        this.recording = true;
        unpause();
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
        this.playback = true;
        unpause();
        notifyListeners();
    }

    public void setSlowMotion() {
        this.slowMotion = true;
        unpause();
        notifyListeners();
    }

    public void rewind() {
    }

    public void step() {
    }

    public void clear() {
    }

    public static interface Listener {
        void stateChanged();
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

}
