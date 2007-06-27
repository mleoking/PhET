package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class AccelerationDriven implements UpdateStrategy {

    public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {
        //assume a constant acceleration model with the given acceleration.
        double origAngVel = state.getVelocity();
        model.addAccelerationData( state.getAcceleration(), time );
        model.addVelocityData( state.getVelocity() + state.getAcceleration() * dt, time );
        model.addPositionData( state.getPosition() + ( state.getVelocity() + origAngVel ) / 2.0 * dt, time );
    }
}
