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
        module.getCursorGraphic().setVisible( false );
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;//smoothedPosition.size() - 1;
        module.setReplayTime( timeIndex );
        module.setAccelerationPlotMagnitude( 75 );
        module.setVelocityPlotMagnitude( 25 );
        module.getPositionPlot().setPaintYLines( new double[]{5, 10} );
        module.getVelocityPlot().setPaintYLines( new double[]{10, 20} );
        module.getAccelerationPlot().setPaintYLines( new double[]{25, 50} );

        module.getPositionGraphic().setSliderVisible( false );
        module.getVelocityGraphic().setSliderVisible( false );
        module.getAccelerationGraphic().setSliderVisible( false );

        module.setNumSmoothingPoints( numRecordSmoothingPoints );
        module.repaintBackground();
    }

    public void stepInTime( double dt ) {
        if( !module.isPaused() ) {
            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
//                    moduleTimeFinished();
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
            module.getPosition().addPoint( module.getMan().getX() );
            module.getPosition().updateSmoothedSeries();
            module.getPosition().updateDerivative( dt * MovingManModule.TIMER_SCALE );
            module.getVelocityData().updateSmoothedSeries();
            module.getVelocityData().updateDerivative( dt * MovingManModule.TIMER_SCALE );
            module.getAcceleration().updateSmoothedSeries();
            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                module.setPaused( true );
                module.getMovingManControlPanel().finishedRecording();
                return;
            }
        }
    }


}
