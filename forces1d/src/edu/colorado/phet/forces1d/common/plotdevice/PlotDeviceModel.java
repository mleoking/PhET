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
    private double timeScale;
    private boolean paused;

    private RecordMode recordMode = new RecordMode();
    private PlaybackMode playbackMode = new PlaybackMode();
    private Mode currentMode = recordMode;
    private ArrayList listeners = new ArrayList();

    private static int instanceCount = 0;

    private int myCount;

    protected PlotDeviceModel( double maxTime, double timeScale ) {
        this.maxTime = maxTime;
        this.timeScale = timeScale;
        this.myCount = instanceCount++;
        paused = true;
    }

    public void setPaused( boolean p ) {
        if( this.paused != p ) {
            this.paused = p;
//            System.out.println( "Plot device model["+myCount+"]: paused=" + this.paused );
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
        boolean currentModeNull = currentMode == null;
//        System.out.println( "SIT[" + myCount + "]: paused=" + paused );
        if( currentModeNull || paused ) {

        }
        else {
            currentMode.stepInTime( dt * timeScale );
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

    public abstract void cursorMovedToTime( double time, int index );

    public abstract void reset();

    public int convertTimeToIndex( double modelX ) {
        return (int)( modelX / playbackMode.conversionFactor );
    }

    public double convertIndexToTime( int i ) {
        return i * recordMode.conversionFactor * timeScale;
    }

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
            cursorMovedToTime( 0, 0 );
        }
    }

    private class RecordMode extends Mode {
        int recordIndex = 0;
        private double conversionFactor;

        public RecordMode() {
            super( "record" );
        }

        public void stepInTime( double dt ) {
            timer.stepInTime( dt );
            stepRecord( dt );
            if( recordIndex == 0 ) {
                conversionFactor = 1.0;
            }
            else {
                conversionFactor = timer.getTime() / ( recordIndex );
            }
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

        public void reset() {
            recordIndex = 0;
            super.reset();
        }
    }

    protected abstract void stepRecord( double dt );

    protected abstract void stepPlayback( double time, int playbackIndex );

    private class PlaybackMode extends Mode {
        int playbackIndex = 0;
        private double conversionFactor;

        public PlaybackMode() {
            super( "playback" );
        }

        public void rewind() {
            playbackIndex = 0;
            timer.reset();
        }

        public void stepInTime( double dt ) {
            timer.stepInTime( dt );

            conversionFactor = timer.getTime() / playbackIndex;

            stepPlayback( timer.getTime(), playbackIndex++ );
            //assume linear

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

        public void reset() {
            playbackIndex = 0;
            super.reset();
        }
    }

    public void doReset() {
        recordMode.reset();
        playbackMode.reset();

    }

    public void setPlaybackMode() {
        currentMode = playbackMode;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.playbackStarted();
        }
    }

    public void rewind() {
        playbackMode.rewind();
        stepPlayback( 0, 0 );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.rewind();
        }

    }

}
