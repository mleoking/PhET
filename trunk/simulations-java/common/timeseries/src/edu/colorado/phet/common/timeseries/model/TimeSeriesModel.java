/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.timeseries.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:00:14 AM
 */

public class TimeSeriesModel extends ClockAdapter {
    private RecordableModel recordableModel;
    private ConstantDtClock clock;
    private TimeStateSeries series = new TimeStateSeries();
    private double maxRecordTime = Double.POSITIVE_INFINITY;

    private Mode.Record record = new Mode.Record( this );
    private Mode.Playback playback = new Mode.Playback( this );
    private Mode.Live live = new Mode.Live( this );

    private Mode mode = live;//the current mode.

    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private boolean paused;

    public TimeSeriesModel( RecordableModel recordableModel, final ConstantDtClock clock ) {
        this.recordableModel = recordableModel;
        this.clock = clock;
        this.paused = clock.isPaused();
        clock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updatePauseStateFromClock();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updatePauseStateFromClock();
            }
        } );
    }

    private void updatePauseStateFromClock() {
        if ( this.paused != clock.isPaused() ) {
            this.paused = clock.isPaused();
            notifyPauseChanged();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void addPlaybackTimeChangeListener( final PlaybackTimeListener playbackTimeListener ) {
        playback.addListener( playbackTimeListener );
    }

    private double getLiveTime() {
        return live.getTime();
    }

    public double getPlaybackTime() {
        return playback.getPlaybackTime();
    }

    public void setPlaybackTime( double requestedTime ) {
        if ( requestedTime > getRecordTime() ) {
            requestedTime = getRecordTime();
        }
        if ( requestedTime >= 0 && requestedTime <= getRecordTime() ) {
            playback.setTime( requestedTime );
            if ( numPlaybackStates() > 0 ) {
                recordableModel.setState( series.getTimeStateValue( requestedTime ) );
            }
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getRecordTime() {
        return record.getRecordTime();
    }

    public void setPaused( boolean paused ) {
        if ( this.paused != paused ) {
            if ( paused ) {
                clock.pause();
            }
            else {
                clock.start();
            }
            notifyPauseChanged();
        }
    }

    private void notifyPauseChanged() {
        for ( Listener listener : listeners ) {
            listener.pauseChanged();
        }
    }

    public void reset() {
        boolean origPauseState = isPaused();
        setPaused( true );
        record.reset();
        series.clear();
//        rewind();
        recordableModel.resetTime();
        setPaused( origPauseState );
        notifyDataSeriesChanged();
    }

    public boolean isLiveMode() {
        return mode == live;
    }

    public boolean isRecordMode() {
        return mode == record;
    }

    public void setRecordMode() {
        setMode( record );
    }

    protected void setMode( Mode mode ) {
        boolean same = mode == this.mode;
        if ( !same ) {
            this.mode = mode;
            notifyModeChanged();
//            System.out.println( "Changed mode to: " + mode.getClass().getName() );
        }
    }

    private void notifyModeChanged() {
        for ( Listener listener : listeners ) {
            listener.modeChanged();
        }
    }

    public void rewind() {
        setPlaybackTime( getRecordStartTime() );
    }

    private double getRecordStartTime() {
        if ( series.numPoints() > 0 ) {
            return series.getStartTime();
        }
        else {
            return 0.0;
        }
    }

    private void notifyDataSeriesChanged() {
        for ( Listener listener : listeners ) {
            listener.dataSeriesChanged();
        }
    }

    public boolean isPlaybackMode() {
        return mode == playback;
    }

    public boolean isRecording() {
        return mode == record && !isPaused();
    }

    public TimeStateSeries getSeries() {
        return series;
    }

    public double getMaxRecordTime() {
        return maxRecordTime;
    }

    public void addSeriesPoint( Object state, double recordTime ) {
        series.addPoint( state, recordTime );
        notifyDataSeriesChanged();
    }

    public void startRecording() {
        setRecordMode();
        setPaused( false );
        notifyDataSeriesChanged();
    }

    public void startLiveMode() {
        setLiveMode();
        setPaused( false );
        notifyDataSeriesChanged();
    }

    public void setLiveMode() {
        setMode( live );
    }

    public boolean isThereRecordedData() {
        return getSeries().size() > 0;
    }

    public void updateModel( double dt ) {
        recordableModel.stepInTime( dt );
    }

    public Object getModelState() {
        return recordableModel.getState();
    }

    public void clear() {
        clear(false);
    }

    public void clear( boolean overrideClear ) {
        if ( series.size() > 0 || overrideClear) {
            series.clear();
            record.reset();
            recordableModel.clear();
            setPaused( true );
            setPlaybackTime( 0.0 );
            notifyDataSeriesChanged();
            notifyDataSeriesCleared();
        }
    }

    private void notifyDataSeriesCleared() {
        for ( Listener listener : listeners ) {
            listener.dataSeriesCleared();
        }
    }

    public int numPlaybackStates() {
        return series.numPoints();
    }

    public void setMaxAllowedRecordTime( double maxRecordTime ) {
        this.maxRecordTime = maxRecordTime;
    }

    /**
     * Returns the current time in the current mode.
     *
     * @return the current time in the current mode.
     */
    public double getTime() {
        if ( isPlaybackMode() ) {
            return getPlaybackTime();
        }
        else if ( isRecordMode() ) {
            return getRecordTime();
        }
        else if ( isLiveMode() ) {
            return getLiveTime();
        }
        else {
            System.out.println( "Time not available for mode: " + getMode() );
            return Double.NaN;
        }
    }

    public void recordFinished() {
        setLiveMode();    //Used in energy skate park
    }

    public ConstantDtClock getTimeModelClock() {
        return clock;
    }

    public boolean isLiveAndNotPaused() {
        return mode==live && !paused;
    }

    public interface PlaybackTimeListener {
        public void timeChanged();
    }

    public void clockTicked( ClockEvent event ) {
//        ifRecordTooMuchSwitchToLive();
        stepMode( event.getSimulationTimeChange() );
    }

    public void clockStarted( ClockEvent clockEvent ) {
        notifyPauseChanged();
    }

    public void clockPaused( ClockEvent clockEvent ) {
        notifyPauseChanged();
    }

    public void stepMode( double simulationTimeChange ) {
        mode.step( simulationTimeChange );
    }

    public void setPlaybackMode() {
        setMode( playback );
    }

    public static interface Listener {
        void dataSeriesChanged();

        void modeChanged();

        void pauseChanged();

        void dataSeriesCleared();
    }

    public static class Adapter implements Listener {
        public void dataSeriesChanged() {
        }

        public void modeChanged() {
        }

        public void pauseChanged() {
        }

        public void dataSeriesCleared() {
        }
    }
}
