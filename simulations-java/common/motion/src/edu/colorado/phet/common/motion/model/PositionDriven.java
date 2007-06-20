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
        model.setVelocity( MotionMath.estimateDerivative( model.getAvailablePositionTimeSeries( velocityWindowSize ) ) );
        model.setAcceleration( MotionMath.estimateDerivative( model.getAvailableVelocityTimeSeries( accelerationWindowSize ) ) );
    }

    public double getAccelerationWindowSize() {
        return accelerationWindowSize;
    }
}
