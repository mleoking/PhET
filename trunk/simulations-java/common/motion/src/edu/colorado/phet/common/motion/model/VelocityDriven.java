package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    int velWindow = 10;

    public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {
        double newX = state.getPosition() + state.getVelocity() * dt;
        TimeData a = MotionMath.getDerivative( model.getRecentVelocityTimeSeries( Math.min( velWindow, model.getAccelerationSampleCount() ) ) );
        model.addPositionData( newX, time );
        model.addVelocityData( state.getVelocity(), time );
        model.addAccelerationData( a.getValue(), a.getTime() + dt );//todo: why is it necessary that velocity be offset by dt in order to be correct?
    }
}
