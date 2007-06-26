package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class AccelerationDriven implements UpdateStrategy {

    public void update( MotionModel model, double dt ) {
        //assume a constant acceleration model with the given acceleration.
        double origAngVel = model.getMotionBody().getVelocity();
        model.addAccelerationData( model.getMotionBody().getAcceleration(), model.getTime() );
        model.addVelocityData( model.getMotionBody().getVelocity() + model.getMotionBody().getAcceleration() * dt, model.getTime() );
        model.addPositionData( model.getMotionBody().getPosition() + ( model.getMotionBody().getVelocity() + origAngVel ) / 2.0 * dt, model.getTime() );
    }
}
