/** Sam Reid*/
package edu.colorado.phet.movingman;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class RecordMode extends Mode {
    private int numRecordSmoothingPoints = 12;
    private MovingManModule module;

    public RecordMode( MovingManModule module ) {
        super( module, "Record", true );
        this.module = module;
    }

    public void initialize() {
        module.setCursorsVisible( false );
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;//smoothedPosition.size() - 1;
        module.setReplayTime( timeIndex );
        module.setNumSmoothingPoints( numRecordSmoothingPoints );
        module.repaintBackground();
    }

    public void stepInTime( double dt ) {
        if( !module.isPaused() ) {
            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                module.setPaused( true );
                module.getMovingManControlPanel().finishedRecording();
                return;
            }

            //TODO Should fix the overshoot problem.  Test Me first!
//            double newTime = recordingTimer.getTime() + dt;
//            if( newTime > maxTime ) {
//                dt = maxTime - recordingTimer.getTime();
//            }
            module.getRecordingTimer().stepInTime( dt );//this could go over the max.
            module.step( dt );
            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                module.setPaused( true );
                module.getMovingManControlPanel().finishedRecording();
                return;
            }
        }
    }


}
