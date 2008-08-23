package edu.colorado.phet.forces1d.common.plotdevice;

import java.util.ArrayList;

import edu.colorado.phet.forces1d.common_force1d.model.ModelElement;
import edu.colorado.phet.forces1d.model.DataSeries;
import edu.colorado.phet.forces1d.model.PhetTimer;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 7:55:40 PM
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

    protected PlotDeviceModel( double maxTime, double timeScale ) {
        this.maxTime = maxTime;
        this.timeScale = timeScale;
        paused = true;
    }

    public void setPaused( boolean p ) {
        if ( this.paused != p ) {
            this.paused = p;
//            System.out.println( "Plot device model["+myCount+"]: paused=" + this.paused );
            if ( paused ) {
                currentMode.firePaused();
            }
            else {
                currentMode.fireUnpaused();
            }
        }
    }

    public void setRecordMode() {
        this.currentMode = recordMode;
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.recordingStarted();
        }
    }

    public void stepInTime( double dt ) {
        boolean currentModeNull = currentMode == null;
//        System.out.println( "SIT[" + myCount + "]: paused=" + paused );
        if ( currentModeNull || paused ) {

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

    public int convertTimeToIndex( double time ) {
//        return playbackMode.convertTimeToIndex( time );
        if ( recordMode.recordedTimes.size() == 0 ) {
            return 0;               //todo shouldn't this be -1?
        }
        double last = recordMode.recordedTimes.getLastPoint();
        double lastIndex = recordMode.recordedTimes.size();
        double scale = lastIndex / last;//use the whole data series to guess the time.

        int index = (int) ( time * scale );
        if ( index < 0 ) {
            index = 0;
        }
        if ( index >= recordMode.recordedTimes.size() ) {
            index = recordMode.recordedTimes.size() - 1;
        }
        return index;//TODO check this.
        //TODO should we check in the array as well..?
    }

    public double convertIndexToTime( int i ) {
//        return i * recordMode.conversionFactor * timeScale;
        return recordMode.recordedTimes.pointAt( i );
    }

    public int getPlaybackIndex() {
        return playbackMode.playbackIndex;
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
        //        private double conversionFactor;
        DataSeries recordedTimes = new DataSeries();

        public RecordMode() {
            super( "record.path" );
        }

        public void stepInTime( double dt ) {
            timer.stepInTime( dt );
            recordedTimes.addPoint( timer.getTime() );
            stepRecord( dt );
//            if( recordIndex == 0 ) {
//                conversionFactor = 1.0;
//            }
//            else {
//                conversionFactor = timer.getTime() / ( recordIndex );
//            }
        }

        public void firePaused() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.recordingPaused();
            }
        }

        public void fireUnpaused() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.recordingStarted();
            }
        }

        public void reset() {
            recordedTimes.reset();
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
//            System.out.println( "conversionFactor = " + conversionFactor );

            stepPlayback( timer.getTime(), playbackIndex++ );
            //assume linear

        }

        public void firePaused() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.playbackPaused();
            }
        }

        public void fireUnpaused() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.playbackStarted();
            }
        }

        public void reset() {
            playbackIndex = 0;
            super.reset();
        }

        public int convertTimeToIndex( double modelX ) {
            if ( conversionFactor == 0 ) {
                conversionFactor = 0.02;//TODO MAGICK!
            }
            return (int) ( modelX / conversionFactor );
        }
    }

    public void resetRecordPointer() {
        recordMode.recordIndex = playbackMode.playbackIndex;
        recordMode.timer.setTime( playbackMode.timer.getTime() );
    }

    public void doReset() {
        setPaused( true );
        playbackMode.reset();
        recordMode.reset();
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.reset();
        }

    }


    public void setPlaybackMode() {
        currentMode = playbackMode;
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.playbackStarted();
        }
    }

    public void rewind() {
        playbackMode.rewind();
        stepPlayback( 0, 0 );
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.rewind();
        }

    }

}
