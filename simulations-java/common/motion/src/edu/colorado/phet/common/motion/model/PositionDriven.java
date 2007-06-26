package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class PositionDriven implements UpdateStrategy {
    private int velocityWindowSize = 6;
    private int accelerationWindowSize = 6;

    public int getVelocityWindowSize() {
        return velocityWindowSize;
    }

    //todo: try 2nd order derivative directly from position data?
    public void update( MotionModel model, double dt ) {

        int velWindow = Math.min( velocityWindowSize, model.getVelocitySampleCount() );
        double v = MotionMath.estimateDerivative( model.getRecentPositionTimeSeries( velWindow ) );
        double vt = MotionMath.averageTime( model.getRecentPositionTimeSeries( velWindow ) );

        int accWindow = Math.min( accelerationWindowSize, model.getAccelerationSampleCount() );
        double a = MotionMath.estimateDerivative( model.getRecentVelocityTimeSeries( accWindow ) );
        double at = MotionMath.averageTime( model.getRecentVelocityTimeSeries( accWindow ) );

        model.addPositionData( model.getPosition(), model.getTime() );
        model.addVelocityData( v, vt + dt );
        model.addAccelerationData( a, at );
    }

    public double getAccelerationWindowSize() {
        return accelerationWindowSize;
    }

    public void setVelocityWindowSize( int maxWindowSize ) {
        this.velocityWindowSize = maxWindowSize;
    }
}
