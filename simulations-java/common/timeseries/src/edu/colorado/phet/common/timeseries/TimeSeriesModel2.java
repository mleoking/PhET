package edu.colorado.phet.common.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 1:00:05 PM
 */

public class TimeSeriesModel2 extends ClockAdapter {
    private ArrayList listeners = new ArrayList();
    private ITimeSeries timeSeries;

    private Mode recordMode = new Mode( "record" ) {
        public void clockTicked( ClockEvent clockEvent ) {
            timeSeries.stepInTime( clockEvent.getSimulationTimeChange() );
            history.add( timeSeries.getState() );
        }
    };
    double playbackDT = 1.0;
    private Mode playbackMode = new Mode( "playback" ) {
        public void clockTicked( ClockEvent clockEvent ) {
            playbackTime += playbackDT;
        }
    };
    private Mode slowMotionMode = new Mode( "slow" ) {
        public void clockTicked( ClockEvent clockEvent ) {
        }
    };
    private Mode currentMode = recordMode;
    private boolean paused = false;
    private ArrayList history = new ArrayList();
    private double playbackTime = 0;

    public static class HistoryState {
        double time;
        Object state;

        public HistoryState( double time, Object state ) {
            this.time = time;
            this.state = state;
        }

        public double getTime() {
            return time;
        }

        public Object getState() {
            return state;
        }
    }

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
        return history.size();
    }

    public void rewind() {
        setPlaybackTime( 0 );
    }

    private void setPlaybackTime( double playbackTime ) {
        this.playbackTime = playbackTime;
        timeSeries.setState( history.get( getClosestPlaybackIndex() ) );
    }

    public void step() {
        setPlaybackTime( getClosestPlaybackIndex() + 1 );
    }

    public int getClosestPlaybackIndex() {
        //binay search on time indices
        //assume sorted
        ArrayList list=new ArrayList( );
        Collections.binarySearch( list, )
        int lowerBound = 0;
        int upperBound = history.size() - 1;
        int middle = ( upperBound - lowerBound ) / 2;
        double midTime = getTimeForIndex( middle );
        if( midTime > playbackTime) {
            return playbackIndex;
        }
    }

    private double getTimeForIndex( int middle ) {
        return getHistoryPoint( middle ).getTime();
    }

    private HistoryState getHistoryPoint( int index ) {
        return history.get( index );
    }

    public void clear() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.clear();
        }
    }

    public void clockTicked( ClockEvent clockEvent ) {
        currentMode.clockTicked( clockEvent );
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

    abstract class Mode {
        String name;

        public Mode( String name ) {
            this.name = name;
        }

        public boolean equals( Object obj ) {
            return obj instanceof Mode && ( (Mode)obj ).name.equals( name );
        }

        public abstract void clockTicked( ClockEvent clockEvent );
    }
}
