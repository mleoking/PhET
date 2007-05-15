package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    private double velocity;

    public VelocityDriven( double velocity ) {
        this.velocity = velocity;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity( double velocity ) {
        this.velocity = velocity;
    }

    public void update( RotationModel model, double dt ) {
        //assume a constant acceleration model with the given acceleration.
//        RotationModelState origState = rotationModel.getLastState();
        double newAngle = model.getAngle() + velocity * dt;
        double angularAcceleration = RotationMath.estimateDerivative( model.getAvailableVelocityTimeSeries( 10 ) );
        model.setAngle( newAngle );
        model.setAngularAcceleration( angularAcceleration );
//        return new RotationModelState( rotationModel.copyRotationBodies(), newAngle, velocity, angularAcceleration, origState.getTime() + dt );
    }
}
