/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.movingman.motion.MotionSuite;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:41:34 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class MotionMode extends Mode {
    private int numSmoothingPointsMotion = 3;
    private MotionSuite motionSuite;
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
        module.getPositionPlot().getGrid().setPaintYLines( new double[]{-10, -5, 0, 5, 10} );
        module.getVelocityPlot().getGrid().setPaintYLines( new double[]{-3, -1.5, 0, 1.5, 3} );
        module.getAccelerationPlot().getGrid().setPaintYLines( new double[]{-3, -1.5, 0, 1.5, 3} );
        module.setNumSmoothingPoints( numSmoothingPointsMotion );
        motionSuite.initialize( module.getMan() );
        module.repaintBackground();
    }

    public void setLatestTime() {
        int timeIndex = module.getPosition().numSmoothedPoints() - 1;
        module.setReplayTime( timeIndex );
    }

    public void setMotionSuite( MotionSuite motionSuite ) {
        if( this.motionSuite != motionSuite ) {
            if( this.motionSuite != null ) {
                this.motionSuite.deactivate();
            }
            this.motionSuite = motionSuite;
        }
    }

    public void stepInTime( double dt ) {
        if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
            timeFinished();
            return;
        }
        double x = motionSuite.getStepMotion().stepInTime( module.getMan(), dt );
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
        motionSuite.timeFinished();
        module.getMovingManControlPanel().finishedRecording();
    }

    public void collidedWithWall() {
        motionSuite.collidedWithWall();
    }

    public void reset() {
        if( motionSuite != null ) {
            motionSuite.reset();
        }
    }
}
