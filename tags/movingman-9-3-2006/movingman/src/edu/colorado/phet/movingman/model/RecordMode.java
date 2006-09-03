/** Sam Reid*/
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MovingManModule;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class RecordMode extends Mode {
    private MovingManModule module;
    private MMTimer timer;

    public RecordMode( final MovingManModule module, MovingManTimeModel movingManTimeModel ) {
        super( module, SimStrings.get( "RecordMode.ModeName" ), true );
        timer = new MMTimer( SimStrings.get( "MovingManModule.RecordTimerLabel" ) );//, MovingManModel.TIMER_SCALE );
        this.module = module;
        movingManTimeModel.addListener( new TimeListenerAdapter() {
            public void recordingStarted() {
//                module.setNumSmoothingPoints( 2 );
                module.setSmoothingSharp();
//                module.setSmoothingSmooth();
//                System.out.println( "Recording started." );
            }
        } );
    }

    public void initialize() {
        module.initialize();
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
            module.getRecordingTimer().stepInTime( dt, maxTime );//this could go over the max.
            module.getMan().stepInTime( dt );
            module.step( dt );

            if( newTime >= maxTime ) {
                module.recordingFinished();
                return;
            }
        }
    }

    public void reset() {
        timer.reset();
    }

    public MMTimer getTimer() {
        return timer;
    }
}
