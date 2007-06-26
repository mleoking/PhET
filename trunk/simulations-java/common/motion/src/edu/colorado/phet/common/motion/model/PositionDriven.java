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
        int velWindow = Math.min( velocityWindowSize, model.getVelocitySampleCount() );
        int accWindow = Math.min( accelerationWindowSize, model.getAccelerationSampleCount() );
        model.addPositionData( model.getPosition(), model.getTime() );
        model.addVelocityPast( MotionMath.estimateDerivative( model.getRecentPositionTimeSeries( velWindow ) ), velWindow / 2 );
        model.addAccelerationPast( MotionMath.estimateDerivative( model.getRecentVelocityTimeSeries( accWindow ) ), velWindow / 2 + accWindow / 2 );
    }

    public double getAccelerationWindowSize() {
        return accelerationWindowSize;
    }
}
