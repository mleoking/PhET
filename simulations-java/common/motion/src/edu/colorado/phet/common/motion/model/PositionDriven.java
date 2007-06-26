package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class PositionDriven implements UpdateStrategy {
    private int velocityWindowSize = 10;
    private int accelerationWindowSize = 10;

    public int getVelocityWindowSize() {
        return velocityWindowSize;
    }

    //todo: try 2nd order derivative directly from position data
    public void update( MotionModel model, double dt ) {
        //todo: this should set the velocity windowSize/2 steps ago
        model.addPositionData( model.getPosition(), model.getTime() );

        int velWindow = Math.min( velocityWindowSize, model.getVelocitySampleCount() );
        double v = MotionMath.estimateDerivative( model.getRecentPositionTimeSeries( velWindow ) );
        double vt=MotionMath.averageTime(model.getRecentPositionTimeSeries( velWindow ));
        System.out.println( "vt = " + vt );
        model.addVelocityData( v, vt );

        int accWindow = Math.min( accelerationWindowSize, model.getAccelerationSampleCount() );
        double a = MotionMath.estimateDerivative( model.getRecentVelocityTimeSeries( accWindow ) );
        double at=MotionMath.averageTime( model.getRecentVelocityTimeSeries( accWindow ) );
        model.addAccelerationData( a, at );
    }

    public double getAccelerationWindowSize() {
        return accelerationWindowSize;
    }
}
