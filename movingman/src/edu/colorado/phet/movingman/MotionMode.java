/** Sam Reid*/
package edu.colorado.phet.movingman;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:41:34 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class MotionMode extends Mode {
    private MovingManModule module;
    private int numSmoothingPoints = 15;

    public MotionMode( MovingManModule module ) {
        super( module, "Motion", true );
        this.module = module;
    }

    public void initialize() {
        module.setCursorsVisible( false );
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;
        module.setReplayTime( timeIndex );
        module.setAccelerationPlotMagnitude( 4 );
        module.setVelocityPlotMagnitude( 4 );
        module.getPositionPlot().setPaintYLines( new double[]{5, 10} );
        module.getVelocityPlot().setPaintYLines( new double[]{1.5, 3} );
        module.getAccelerationPlot().setPaintYLines( new double[]{1.5, 3} );
        module.setNumSmoothingPoints( numSmoothingPoints );
        module.repaintBackground();
    }

    public void setLatestTime() {
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;
        module.setReplayTime( timeIndex );
    }

    public void stepInTime( double dt ) {
        if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
            timeFinished();
            return;
        }
        module.getMan().stepInTime( dt );
        module.getRecordingTimer().stepInTime( dt );
        module.step( dt );
        if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
            timeFinished();
            return;
        }
    }

    private void timeFinished() {
        module.getMovingManControlPanel().finishedRecording();
    }

    public void collidedWithWall() {
    }
}
