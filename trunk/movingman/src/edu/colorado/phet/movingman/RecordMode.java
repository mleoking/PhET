/** Sam Reid*/
package edu.colorado.phet.movingman;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class RecordMode extends Mode {
    private MovingManModule module;

    public RecordMode( final MovingManModule module ) {
        super( module, "Record", true );
        this.module = module;
        module.addListener( new MovingManModule.ListenerAdapter() {
            public void recordingStarted() {
                module.setNumSmoothingPoints( 2 );
            }
        } );
    }

    public void initialize() {
        module.setCursorsVisible( false );
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;//smoothedPosition.size() - 1;
        module.setReplayTime( timeIndex );
        module.repaintBackground();
    }

    public void stepInTime( double dt ) {
        double recorderTime = module.getRecordingTimer().getTime();
        double maxTime = module.getMaxTime();
        if( !module.isPaused() ) {
            if( recorderTime >= maxTime ) {
                module.recordingFinished();
                return;
            }

            double newTime = recorderTime + dt;// * timer.getTimerScale();
            if( newTime > maxTime ) {
                dt = ( maxTime - recorderTime );// / timer.getTimerScale();
            }
            module.getRecordingTimer().stepInTime( dt );//this could go over the max.
            module.getMan().stepInTime( dt );
            module.step( dt );

            if( newTime >= maxTime ) {
                module.recordingFinished();
                return;
            }
        }
    }

}
