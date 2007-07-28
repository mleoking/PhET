package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class PositionDriven implements UpdateStrategy {
    private int velocityWindow = 6;
    private int accelerationWindow = 6;

    //todo: try 2nd order derivative directly from position data?
    public void update( MotionBodySeries motionBodySeries, double dt, MotionBodyState state, double time ) {
        TimeData v = MotionMath.getDerivative( MotionMath.smooth( motionBodySeries.getRecentPositionTimeSeries( Math.min( velocityWindow, motionBodySeries.getPositionSampleCount() ) ), 1 ) );
        TimeData a = MotionMath.getDerivative( MotionMath.smooth( motionBodySeries.getRecentVelocityTimeSeries( Math.min( accelerationWindow, motionBodySeries.getVelocitySampleCount() ) ), 1 ) );
//        TimeData a = MotionMath.getSecondDerivative( smooth( motionBodySeries.getRecentPositionTimeSeries( Math.min( accelerationWindow, motionBodySeries.getPositionSampleCount() ) ), 20 ) );

        motionBodySeries.addPositionData( state.getPosition(), time );
        motionBodySeries.addVelocityData( v.getValue(), v.getTime() );
        motionBodySeries.addAccelerationData( a.getValue(), a.getTime() );
    }

    public double getAccelerationWindow() {
        return accelerationWindow;
    }

    public void setVelocityWindow( int maxWindowSize ) {
        this.velocityWindow = maxWindowSize;
    }

    public int getVelocityWindow() {
        return velocityWindow;
    }

}
