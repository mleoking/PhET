package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    int velWindow = 10;

    public void update( MotionModel model, double dt ) {
        double newX = model.getMotionBody().getPosition() + model.getMotionBody().getVelocity() * dt;
        TimeData a = MotionMath.getDerivative( model.getRecentVelocityTimeSeries( Math.min( velWindow, model.getAccelerationSampleCount() ) ) );

        model.addPositionData( newX, model.getTime() );
        model.addVelocityData( model.getMotionBody().getVelocity(), model.getTime() );
        model.addAccelerationData( a.getValue(), a.getTime() + dt );//todo: why is it necessary that velocity be offset by dt in order to be correct?
    }
}
