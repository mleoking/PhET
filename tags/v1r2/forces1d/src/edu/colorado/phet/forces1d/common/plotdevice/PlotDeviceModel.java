/** Sam Reid*/
package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.forces1d.model.PhetTimer;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 7:55:40 PM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public abstract class PlotDeviceModel implements ModelElement {
    private double minTime = 0;
    private double maxTime;
    private boolean paused = true;

    private Mode recordMode = new RecordMode();
    private Mode playbackMode = new PlaybackMode();
    private Mode currentMode;
    private ArrayList listeners = new ArrayList();

    protected PlotDeviceModel( double maxTime ) {
        this.maxTime = maxTime;
    }

    public void setPaused( boolean paused ) {
        if( this.paused != paused ) {
            this.paused = paused;
            if( paused ) {
                currentMode.firePaused();
            }
            else {
                currentMode.fireUnpaused();
            }
        }
    }

    public void setRecordMode() {
        this.currentMode = recordMode;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.recordingStarted();
        }
    }

    public void stepInTime( double dt ) {
        if( currentMode != null && !isPaused() ) {
            currentMode.stepInTime( dt );
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public PhetTimer getRecordingTimer() {
        return recordMode.timer;
    }

    public PhetTimer getPlaybackTimer() {
        return playbackMode.timer;
    }

    public boolean isTakingData() {
        return currentMode == recordMode && !paused;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public abstract void cursorMovedToTime( double time );

    public abstract void reset();

    public interface Listener {
        public void recordingStarted();

        public void recordingPaused();

        public void recordingFinished();

        public void playbackStarted();

        public void playbackPaused();

        public void playbackFinished();

        public void reset();

        public void rewind();
    }

    public static class ListenerAdapter implements Listener {
        public void recordingStarted() {
        }

        public void recordingPaused() {
        }

        public void recordingFinished() {
        }

        public void playbackStarted() {
        }

        public void playbackPaused() {
        }

        public void playbackFinished() {
        }

        public void reset() {
        }

        public void rewind() {
        }
    }

    private abstract class Mode {
        PhetTimer timer;

        protected Mode( String name ) {
            this.timer = new PhetTimer( name );
        }

        public abstract void stepInTime( double dt );

        public abstract void firePaused();

        public abstract void fireUnpaused();

        public void reset() {
            timer.reset();
        }
    }

    private class RecordMode extends Mode {

        public RecordMode() {
            super( "record" );
        }

        public void stepInTime( double dt ) {
//            double data=recordDataPoint();
            timer.stepInTime( dt );
            stepRecord( dt );
        }

        public void firePaused() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.recordingPaused();
            }
        }

        public void fireUnpaused() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.recordingStarted();
            }
        }
    }

    protected abstract void stepRecord( double dt );

    protected abstract void stepPlayback( double dt );

    private class PlaybackMode extends Mode {

        public PlaybackMode() {
            super( "playback" );
        }

        public void stepInTime( double dt ) {
            timer.stepInTime( dt );
            stepPlayback( dt );
        }

        public void firePaused() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.playbackPaused();
            }
        }

        public void fireUnpaused() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.playbackStarted();
            }
        }
    }

    public void doReset() {
        recordMode.reset();
        playbackMode.reset();

    }
}
