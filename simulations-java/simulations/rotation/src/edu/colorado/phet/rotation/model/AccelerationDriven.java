package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class AccelerationDriven implements UpdateStrategy {

    public void update( RotationModel model, double dt ) {
        //assume a constant acceleration model with the given acceleration.
        model.setAngularVelocity( model.getAngularVelocity() + model.getAngularAcceleration() * dt );
        model.setAngle( model.getAngle() + ( model.getAngularVelocity() + model.getAngularVelocity() ) / 2.0 * dt );
    }
}
