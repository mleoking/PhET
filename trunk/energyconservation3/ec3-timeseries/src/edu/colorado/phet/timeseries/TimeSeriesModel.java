/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:00:14 AM
 * Copyright (c) Mar 24, 2005 by Sam Reid
 */

public abstract class TimeSeriesModel implements ClockListener {
    private ArrayList listeners = new ArrayList();
    private boolean paused = false;

    private Mode mode;//the current mode.
    private RecordMode recordMode;
    private PlaybackMode playbackMode;
    private LiveMode liveMode;

    public static double TIME_SCALE = 1.0;// for dynamic model.
    private double maxAllowedTime;
    private ObjectTimeSeries series = new ObjectTimeSeries();

    public TimeSeriesModel( double maxAllowedTime ) {
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
        getPlaybackTimer().addListener( new PhetTimer.Listener() {
            public void timeChanged() {
                playbackTimeListener.timeChanged();
            }
        } );
    }

    public double getPlaybackTime() {
        return getPlaybackTimer().getTime();
    }

    public void setReplayTime( double requestedTime ) {
        if( requestedTime < 0 || requestedTime > getRecordTime() ) {
            return;
        }
        else {
            getPlaybackTimer().setTime( requestedTime );
            ObjectTimePoint value = series.getValueForTime( requestedTime );
            if( value != null ) {
                Object v = value.getValue();
                if( v != null ) {
                    setModelState( v );
                }
            }
        }
    }


    public PhetTimer getRecordTimer() {
        return recordMode.getTimer();
    }

    public PhetTimer getPlaybackTimer() {
        return playbackMode.getTimer();
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

    public void fireReset() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.reset();
        }
    }

    private void firePause() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            if( mode == recordMode ) {
                timeSeriesModelListener.recordingPaused();
            }
            else if( mode == liveMode ) {
                timeSeriesModelListener.liveModePaused();
            }
            else {
                timeSeriesModelListener.playbackPaused();
            }
        }
    }

    public void firePlaybackFinished() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.playbackFinished();
        }
    }

    public void fireFinishedRecording() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.recordingFinished();
        }
    }

    public void addListener( TimeSeriesModelListener timeSeriesModelListener ) {
        listeners.add( timeSeriesModelListener );
    }

    public double getRecordTime() {
        return getRecordTimer().getTime();
    }

    public void setPaused( boolean paused ) {
        if( paused != this.paused ) {
            this.paused = paused;
            if( paused ) {
                firePause();
            }
            else if( isRecording() ) {
                fireRecordStarted();
            }
            else if( isPlayback() ) {
                firePlaybackStarted();
            }
            else if( isLiveMode() ) {
                fireLiveModeStarted();
            }
        }
    }

    private void fireLiveModeStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.liveModeStarted();
        }
    }

    private void firePlaybackStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.playbackStarted();
        }
    }

    public boolean isLiveMode() {
        return mode == liveMode;
    }

    private boolean isPlayback() {
        return mode == playbackMode;
    }

    private void fireRecordStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.recordingStarted();
        }
    }

    public void resetOrig() {
        setPaused( true );
        recordMode.reset();
        playbackMode.reset();
        series.reset();
        fireReset();
    }

    public void reset() {
        boolean origPauseState = isPaused();
        setPaused( true );
        recordMode.reset();
        playbackMode.reset();
        series.reset();
        fireReset();
        setPaused( origPauseState );
    }

    public int getTimeIndex( double requestedTime ) {
        return (int)( requestedTime / TIME_SCALE );
    }

    public boolean isRecordMode() {
        return mode == recordMode;
    }

    public void setRecordMode() {

        recordMode.initAgain( this );
        setMode( recordMode );
    }

    void setLastPoint() {
        if( series.size() > 0 ) {
            ObjectTimePoint lastPoint = series.getLastPoint();
            setModelState( lastPoint.getValue() );
        }
    }


    public void confirmAndApplyReset() {
        if( confirmReset() ) {
            reset();
        }
    }


    public void setMode( Mode mode ) {
        boolean same = mode == this.mode;
        if( !same ) {
            this.mode = mode;
            this.mode.initialize();
            System.out.println( "Changed mode to: " + mode.getName() );
        }
    }

    private void fireRewind() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.rewind();
        }
    }

    public void rewind() {
        playbackMode.rewind();
        fireRewind();
    }

    public void startPlaybackMode( double playbackSpeed ) {
        playbackMode.setPlaybackSpeed( playbackSpeed );
        setMode( playbackMode );
        setPaused( false );
        firePlaybackStarted();
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

    public ObjectTimeSeries getSeries() {
        return series;
    }

    public abstract void updateModel( double simulationTimeChange );

    public abstract Object getModelState();

    protected abstract void setModelState( Object v );

    protected abstract boolean confirmReset();

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
    }

    public void startRecording() {
        setRecordMode();
        setPaused( false );
        fireRecordStarted();
    }

    public void startLiveMode() {
//        setLastPoint();
        setLiveMode();
        setPaused( false );
        notifyLiveModeStarted();
    }

    private void notifyLiveModeStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeSeriesModelListener timeSeriesModelListener = (TimeSeriesModelListener)listeners.get( i );
            timeSeriesModelListener.liveModeStarted();
        }
    }

    public void setLiveMode() {
        setMode( liveMode );
    }

    public abstract void stepLive();

    public void stepMode() {
        mode.step();
    }

    public boolean isThereRecordedData() {
        return getSeries().size() > 0;
    }

    public boolean isFirstPlaybackPoint() {
        return getSeries().size() > 0 && isPlaybackMode() && playbackMode.getPlaybackTime() == 0;
    }

    public interface PlaybackTimeListener {
        public void timeChanged();
    }

    public void clockTicked( ClockEvent event ) {
        if( mode != null ) {
            mode.clockTicked( event );
        }
    }

    public void setPlaybackMode() {
        setMode( getPlaybackMode() );
    }

    public void clockStarted( ClockEvent clockEvent ) {
    }

    public void clockPaused( ClockEvent clockEvent ) {
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}
