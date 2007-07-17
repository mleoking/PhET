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
        TimeData v = MotionMath.getDerivative( smooth( motionBodySeries.getRecentPositionTimeSeries( Math.min( velocityWindow, motionBodySeries.getPositionSampleCount() ) ), 1 ) );
        TimeData a = MotionMath.getDerivative( smooth( motionBodySeries.getRecentVelocityTimeSeries( Math.min( accelerationWindow, motionBodySeries.getVelocitySampleCount() ) ), 1 ) );
//        TimeData a = MotionMath.getSecondDerivative( smooth( motionBodySeries.getRecentPositionTimeSeries( Math.min( accelerationWindow, motionBodySeries.getPositionSampleCount() ) ), 20 ) );

        motionBodySeries.addPositionData( state.getPosition(), time );
        motionBodySeries.addVelocityData( v.getValue(), v.getTime() );
        motionBodySeries.addAccelerationData( a.getValue(), a.getTime() );
    }

    public TimeData[] smooth( TimeData[] series, int numSmooth ) {
        for( int i = 0; i < numSmooth; i++ ) {
            series = smooth( series );
        }
        return series;
    }

    private TimeData[] smooth( TimeData[] datas ) {
        TimeData[] smooth = new TimeData[datas.length];
        for( int i = 0; i < smooth.length; i++ ) {
            if( i > 0 && i < smooth.length - 1 ) {
                smooth[i] = new TimeData( ( datas[i - 1].getValue() + datas[i].getValue() + datas[i + 1].getValue() ) / 3.0, datas[i].getTime() );
            }
            else {
                smooth[i] = new TimeData( datas[i].getValue(), datas[i].getTime() );
            }
        }

        return smooth;
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
