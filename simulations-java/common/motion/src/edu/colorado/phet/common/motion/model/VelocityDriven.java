package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    int velWindow = 10;

    public void update( MotionBody motionBody, double dt, double time ) {
        double newX = motionBody.getPosition() + motionBody.getVelocity() * dt;
        TimeData a = MotionMath.getDerivative( motionBody.getRecentVelocityTimeSeries( Math.min( velWindow, motionBody.getAccelerationSampleCount() ) ) );
        motionBody.addPositionData( newX, time );
        motionBody.addVelocityData( motionBody.getVelocity(), time );
        motionBody.addAccelerationData( a.getValue(), a.getTime() + dt );//todo: why is it necessary that velocity be offset by dt in order to be correct?
    }
}
