/** Sam Reid*/
package edu.colorado.phet.movingman;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:41:34 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class MotionMode extends Mode {
    private int numSmoothingPointsMotion = 3;
    private MovingManModule module;

    public MotionMode( MovingManModule module ) {
        super( module, "Motion", true );
        this.module = module;
    }

    public void initialize() {
        module.getCursorGraphic().setVisible( false );
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;
        module.setReplayTime( timeIndex );
        module.setAccelerationPlotMagnitude( 4 );
        module.setVelocityPlotMagnitude( 4 );
        module.getPositionPlot().setPaintYLines( new double[]{5, 10} );
        module.getVelocityPlot().setPaintYLines( new double[]{1.5, 3} );
        module.getAccelerationPlot().setPaintYLines( new double[]{1.5, 3} );
        module.setNumSmoothingPoints( numSmoothingPointsMotion );
        module.repaintBackground();
    }

    public void setLatestTime() {
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;
        module.setReplayTime( timeIndex );
    }

    public void stepInTime( double dt ) {
        StepMotion stepMotion = module.getAccelMotion();
        if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
            timeFinished();
            return;
        }
        double x = stepMotion.stepInTime( module.getMan(), dt );
        x = Math.min( x, module.getMaxManPosition() );
        x = Math.max( x, -module.getMaxManPosition() );
        if( x == module.getMaxManPosition() ) {
            module.getMan().setVelocity( 0 );
        }
        if( x == -module.getMaxManPosition() ) {
            module.getMan().setVelocity( 0 );
        }

        module.getRecordingTimer().stepInTime( dt );
        module.getMan().setX( x );
        module.getPosition().addPoint( module.getMan().getX() );
        module.getPosition().updateSmoothedSeries();
        module.getPosition().updateDerivative( dt * MovingManModule.TIMER_SCALE );
        module.getVelocityData().updateSmoothedSeries();
        module.getVelocityData().updateDerivative( dt * MovingManModule.TIMER_SCALE );
        module.getAcceleration().updateSmoothedSeries();
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
