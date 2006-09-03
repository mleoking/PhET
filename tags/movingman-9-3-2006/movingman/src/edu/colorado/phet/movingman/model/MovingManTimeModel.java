/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.movingman.MovingManModule;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:00:14 AM
 * Copyright (c) Mar 24, 2005 by Sam Reid
 */

public class MovingManTimeModel {

    private ArrayList listeners = new ArrayList();
    private boolean paused = true;

    private Mode mode;//the current mode.
    private RecordMode recordMode;
    private PlaybackMode playbackMode;
    private ModelElement mainModelElement;
//    public static final double TIME_SCALE = 1.0 / 50.0;//for static model
    public static double TIME_SCALE = 1.0;// for dynamic model.
    private int numSmoothingPoints;
    private boolean stopped = false;
    public static boolean dynamicTime;
    private MovingManModule module;

    public MovingManTimeModel( MovingManModule module ) {
        mainModelElement = new ModelElement() {
            public void stepInTime( double dt ) {
                if( !paused ) {
                    mode.stepInTime( dt * TIME_SCALE );
                }
            }
        };
        recordMode = new RecordMode( module, this );
        playbackMode = new PlaybackMode( module, this );
        this.module = module;
    }

    public int getNumSmoothingPoints() {
        return numSmoothingPoints;
    }

    public ArrayList getListeners() {
        return listeners;
    }

    public boolean isPaused() {
        return paused;
    }

    public MMTimer getRecordTimer() {
        return recordMode.getTimer();
    }

    public MMTimer getPlaybackTimer() {
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

    public ModelElement getMainModelElement() {
        return mainModelElement;
    }

    public static double getTimeScale() {
        return TIME_SCALE;
    }

    public void fireReset() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            timeListener.reset();
        }
    }

    private void firePause() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            if( mode == recordMode ) {
                timeListener.recordingPaused();
            }
            else {
                timeListener.playbackPaused();
            }
        }
    }

    public void firePlaybackFinished() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            timeListener.playbackFinished();
        }
    }

    public void fireFinishedRecording() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            timeListener.recordingFinished();
        }
    }

    public void addListener( TimeListener timeListener ) {
        listeners.add( timeListener );
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
        }
    }

    private void firePlaybackStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            timeListener.playbackStarted();
        }
    }

    private boolean isPlayback() {
        return mode == playbackMode;
    }

    private void fireRecordStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            timeListener.recordingStarted();
        }
    }

    public void setNumSmoothingPoints( int n ) {
        this.numSmoothingPoints = n;
    }

    public void reset() {
        setPaused( true );
        recordMode.reset();
        playbackMode.reset();
        fireReset();
    }

    public int getTimeIndex( double requestedTime ) {
        return (int)( requestedTime / TIME_SCALE );
    }

    public boolean isRecordMode() {
        return mode == recordMode;
    }

    public boolean isTakingData() {
        return !isPaused() && mode.isTakingData();
    }

    public void setRecordMode() {
        setMode( recordMode );
    }

    public void setMode( Mode mode ) {

        boolean same = mode == this.mode;
        if( !same ) {
            this.mode = mode;
            this.mode.initialize();
//            System.out.println( "Changed mode to: " + mode.getName() );
        }
    }

    private void fireRewind() {
        for( int i = 0; i < listeners.size(); i++ ) {
            TimeListener timeListener = (TimeListener)listeners.get( i );
            timeListener.rewind();
        }
    }

    public void rewind() {
        playbackMode.rewind();
        fireRewind();
    }

    public void startPlaybackMode( double playbackSpeed ) {

        stopped = false;
        playbackMode.setPlaybackSpeed( playbackSpeed );
        setMode( playbackMode );
        setPaused( false );
    }

    public boolean isRecording() {
        return mode == recordMode && !isPaused();
    }

    public void setDynamicTime( boolean dynamicTime ) {
        this.dynamicTime = dynamicTime;
        if( dynamicTime ) {
            TIME_SCALE = 1.0;
        }
        else {
            TIME_SCALE = 1.0 / 50.0;
        }
    }

    public boolean isAtEndOfTime() {
        return getRecordTimer().getTime() == module.getMovingManModel().getMaxTime();
    }
}
