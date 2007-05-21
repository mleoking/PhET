/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.timeseries.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:00:14 AM
 */

public class TimeSeriesModel extends ClockAdapter {
    private RecordableModel recordableModel;
    private TimeStateSeries series = new TimeStateSeries();
    private double singleStepDT;
    private boolean paused = false;
    private double maxRecordTime = Double.POSITIVE_INFINITY;

    private RecordMode recordMode = new RecordMode( this );
    private PlaybackMode playbackMode = new PlaybackMode( this );
    private LiveMode liveMode = new LiveMode( this );

    private Mode mode = liveMode;//the current mode.

    private ArrayList listeners = new ArrayList();

    public TimeSeriesModel( RecordableModel recordableModel, double singleStepDT ) {
        this.recordableModel = recordableModel;
        this.singleStepDT = singleStepDT;
        this.mode = liveMode;
    }

    public boolean isPaused() {
        return paused;
    }

    public void addPlaybackTimeChangeListener( final PlaybackTimeListener playbackTimeListener ) {
        playbackMode.addListener( playbackTimeListener );
    }

    private double getLiveTime() {
        return liveMode.getTime(); 
    }

    public double getPlaybackTime() {
        return playbackMode.getPlaybackTime();
    }

    public void setPlaybackTime( double requestedTime ) {
        if( requestedTime >= 0 && requestedTime <= getRecordTime() && numPlaybackStates() > 0 ) {
            playbackMode.setTime( requestedTime );
            recordableModel.setState( series.getTimeStateValue( requestedTime ) );
        }
    }

    public Mode getMode() {
        return mode;
    }

    public PlaybackMode getPlaybackMode() {
        return playbackMode;
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
            notifyDataAdded();
            notifyPauseChanged();
        }
    }

    private void notifyPauseChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.pauseChanged();
        }
    }

    public void reset() {
        boolean origPauseState = isPaused();
        setPaused( true );
        recordMode.reset();
        rewind();
        series.clear();
        recordableModel.resetTime();
        setPaused( origPauseState );
        notifyDataAdded();
    }

    public boolean isLiveMode() {
        return mode == liveMode;
    }

    public boolean isRecordMode() {
        return mode == recordMode;
    }

    public void setRecordMode() {
        setMode( recordMode );
    }

    public void confirmAndApplyReset() {
        if( confirmReset() ) {
            reset();
        }
    }

    private boolean confirmReset() {
        return true;
    }

    protected void setMode( Mode mode ) {
        boolean same = mode == this.mode;
        if( !same ) {
            this.mode = mode;
            notifyModeChanged();
            System.out.println( "Changed mode to: " + mode.getClass().getName() );
        }
    }

    private void notifyModeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.modeChanged();
        }
    }

    public void rewind() {
        setPlaybackTime( getRecordStartTime() );
        notifyDataAdded();
    }

    private double getRecordStartTime() {
        if( series.numPoints() > 0 ) {
            return series.getStartTime();
        }
        else {
            return 0.0;
        }
    }

    private void notifyDataAdded() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.dataAdded();
        }
    }

    public void startPlaybackMode( double playbackSpeed ) {
        playbackMode.setPlaybackSpeed( playbackSpeed );
        setMode( playbackMode );
        setPaused( false );
        notifyDataAdded();
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

    public double getMaxRecordTime() {
        return maxRecordTime;
    }

    public void addSeriesPoint( Object state, double recordTime ) {
        series.addPoint( state, recordTime );
        notifyDataAdded();
    }

    public void startRecording() {
        setRecordMode();
        setPaused( false );
        notifyDataAdded();
    }

    public void startLiveMode() {
        setLiveMode();
        setPaused( false );
        notifyDataAdded();
    }

    public void setLiveMode() {
        setMode( liveMode );
    }

    private void ifRecordTooMuchSwitchToLive() {
        int MAX = 1500;
        //todo magic number: this is the number of recorded points in the 40-second interval that shows up on the time series chart under a particular set of conditions.

        if( isRecordMode() && recordMode.getTimeSeriesModel().getSeries().size() > MAX ) {
            setLiveMode();
            notifyDataAdded();
        }
    }

    public boolean isThereRecordedData() {
        return getSeries().size() > 0;
    }

    public boolean isFirstPlaybackPoint() {
        return getSeries().size() > 0 && isPlaybackMode() && playbackMode.getPlaybackTime() == 0;
    }

    public double getSingleStepDT() {
        return singleStepDT;
    }

    public void setSingleStepDT( double singleStepDT ) {
        this.singleStepDT = singleStepDT;
    }

    public void updateModel( double dt ) {
        recordableModel.stepInTime( dt );
    }

    public Object getModelState() {
        return recordableModel.getState();
    }

    public void clear() {
        series.clear();
        notifyDataAdded();
    }

    public int numPlaybackStates() {
        return series.numPoints();
    }

    public void setMaxRecordTime( double maxRecordTime ) {
        this.maxRecordTime = maxRecordTime;
    }

    /**
     * Returns the current time in the current mode.
     * @return the current time in the current mode.
     */
    public double getTime() {
        if (isPlaybackMode() ){
            return getPlaybackTime();
        }else if (isRecordMode()){
            return getRecordTime();
        }
        else if (isLiveMode() ){
            return getLiveTime();
        }else{
            System.out.println( "Time not available for mode: "+getMode() );
            return Double.NaN;
        }
    }


    public interface PlaybackTimeListener {
        public void timeChanged();
    }

    public void clockTicked( ClockEvent event ) {
        ifRecordTooMuchSwitchToLive();
        if( !isPaused() ) {
            stepMode( event.getSimulationTimeChange() );
        }
    }

    public void stepMode() {
        stepMode( getSingleStepDT() );
    }

    public void stepMode( double simulationTimeChange ) {
        mode.step( simulationTimeChange );
    }

    public void setPlaybackMode() {
        setMode( playbackMode );
    }

    public static interface Listener {
        void dataAdded();
        void modeChanged();
        void pauseChanged();
    }

    public static class Adapter implements Listener {
        public void dataAdded() {
        }

        public void modeChanged() {
        }

        public void pauseChanged() {
        }
    }
}
