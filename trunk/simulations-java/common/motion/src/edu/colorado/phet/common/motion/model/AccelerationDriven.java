package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class AccelerationDriven implements UpdateStrategy {

    public void update( MotionModel model, double dt ) {
        //assume a constant acceleration model with the given acceleration.
        double origAngVel = model.getVelocity();
        model.setVelocity( model.getVelocity() + model.getAcceleration() * dt );
        model.setAngle( model.getPosition() + ( model.getVelocity() + origAngVel ) / 2.0 * dt );
    }
}
