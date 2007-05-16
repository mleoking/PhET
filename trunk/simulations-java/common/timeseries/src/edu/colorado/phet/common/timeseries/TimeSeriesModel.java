/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:00:14 AM
 */

public class TimeSeriesModel extends ClockAdapter {
    private ArrayList listeners = new ArrayList();
    private boolean paused = false;

    private Mode mode;//the current mode.
    private RecordMode recordMode;
    private PlaybackMode playbackMode;
    private LiveMode liveMode;

    public static double TIME_SCALE = 1.0;// for dynamic model.
    private double maxAllowedTime;
    private TimeStateSeries series = new TimeStateSeries();
    private double simulationDT = 0.03;
    private TimeSeries timeSeries;

    public TimeSeriesModel( TimeSeries timeSeries, double maxAllowedTime ) {
        this.timeSeries = timeSeries;
        recordMode = new RecordMode( this );
        playbackMode = new PlaybackMode( this );
        liveMode = new LiveMode( this );
        this.maxAllowedTime = maxAllowedTime;
        this.mode = liveMode;
    }

    public boolean isPaused() {
        return paused;
    }

    public void addPlaybackTimeChangeListener( final PlaybackTimeListener playbackTimeListener ) {
        playbackMode.addListener( playbackTimeListener );
    }

    public double getPlaybackTime() {
        return playbackMode.getPlaybackTime();
    }

    public void setReplayTime( double requestedTime ) {
        if( requestedTime < 0 || requestedTime > getRecordTime() ) {
        }
        else {
            playbackMode.setTime( requestedTime );
            TimeState value = series.getValueForTime( requestedTime );
            if( value != null ) {
                Object v = value.getValue();
                if( v != null ) {
                    timeSeries.setState( v );
                }
            }
        }
    }

    public Mode getMode() {
        return mode;
    }

    public RecordMode getRecordMode() {
        return recordMode;
    }

    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    public static double getTimeScale() {
        return TIME_SCALE;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getRecordTime() {
        return recordMode.getRecordTime();
    }

    public void setPaused( boolean paused ) {
        if( paused != this.paused ) {
            this.paused = paused;
            notifyStateChanged();
        }
    }

    public boolean isLiveMode() {
        return mode == liveMode;
    }

    public void reset() {
        boolean origPauseState = isPaused();
        setPaused( true );
        recordMode.reset();
        playbackMode.reset();
        series.reset();
        timeSeries.resetTime();
        setPaused( origPauseState );
        notifyStateChanged();
    }

    public int getTimeIndex( double requestedTime ) {
        return (int)( requestedTime / TIME_SCALE );
    }

    public boolean isRecordMode() {
        return mode == recordMode;
    }

    public void setRecordMode() {
        setMode( recordMode );
    }

    void setLastPoint() {
        if( series.size() > 0 ) {
            TimeState lastPoint = series.getLastPoint();
            timeSeries.setState( lastPoint.getValue() );
        }
    }


    public void confirmAndApplyReset() {
        if( confirmReset() ) {
            reset();
        }
    }

    private boolean confirmReset() {
        return true;
    }

    public void setMode( Mode mode ) {
        boolean same = mode == this.mode;
        if( !same ) {
            this.mode = mode;
            System.out.println( "Changed mode to: " + mode.getClass().getName() );
        }
    }

    public void rewind() {
        playbackMode.rewind();
        notifyStateChanged();
    }

    private void notifyStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.stateChanged();
        }
    }

    public void startPlaybackMode( double playbackSpeed ) {
        playbackMode.setPlaybackSpeed( playbackSpeed );
        setMode( playbackMode );
        setPaused( false );
        notifyStateChanged();
    }

    public boolean isPlaybackMode() {
        return mode == playbackMode;
    }

    public boolean isPlaybackMode( double speed ) {
        return isPlaybackMode() && playbackMode.getPlaybackSpeed() == speed;
    }

    public boolean isRecording() {
        return mode == recordMode && !isPaused();
    }

    public TimeStateSeries getSeries() {
        return series;
    }

    public double getMaxAllowedTime() {
        return maxAllowedTime;
    }

    public double getTime() {
        if( isRecordMode() ) {
            return getRecordTime();
        }
        else {
            return getPlaybackTime();
        }
    }

    public void addSeriesPoint( Object state, double recordTime ) {
        series.addPoint( state, recordTime );
        notifyStateChanged();
    }

    public void startRecording() {
        setRecordMode();
        setPaused( false );
        notifyStateChanged();
    }

    public void startLiveMode() {
        setLiveMode();
        setPaused( false );
        notifyStateChanged();
    }

    public void setLiveMode() {
        setMode( liveMode );
    }

    private void ifRecordTooMuchSwitchToLive() {
        int MAX = 1500;
        //todo magic number: this is the number of recorded points in the 40-second interval that shows up on the time series chart under a particular set of conditions.

        if( isRecordMode() && recordMode.getTimeSeriesModel().getSeries().size() > MAX ) {
            setLiveMode();
            notifyStateChanged();
        }
    }

    public boolean isThereRecordedData() {
        return getSeries().size() > 0;
    }

    public boolean isFirstPlaybackPoint() {
        return getSeries().size() > 0 && isPlaybackMode() && playbackMode.getPlaybackTime() == 0;
    }

    public double getSimulationDT() {
        return simulationDT;
    }

    public void updateModel( double dt ) {
        timeSeries.stepInTime( dt );
    }

    public Object getModelState() {
        return timeSeries.getState();
    }

    public void clear() {
    }

    public int numPlaybackStates() {
        return series.numPoints();
    }

    public interface PlaybackTimeListener {
        public void timeChanged();
    }

    public void clockTicked( ClockEvent event ) {
        if( mode != null ) {
            ifRecordTooMuchSwitchToLive();
            if( !isPaused() ) {
                stepMode( event.getSimulationTimeChange() );
            }
        }
    }

    public void stepMode() {
        stepMode( getSimulationDT() );
    }

    public void stepMode( double simulationTimeChange ) {
        mode.step( simulationTimeChange );
    }

    public void setPlaybackMode() {
        setMode( getPlaybackMode() );
    }

    public static interface Listener {
        void stateChanged();
    }

    public static class Adapter implements Listener {
        public void stateChanged() {
        }
    }
}
