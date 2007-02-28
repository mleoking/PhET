package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 * Copyright (c) Dec 29, 2006 by Sam Reid
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

    public RotationModelState update( RotationModel rotationModel, double dt ) {
        //assume a constant acceleration model with the given acceleration.
        RotationModelState origState = rotationModel.getLastState();
        double newAngle = origState.getAngle() + velocity * dt;
        double angularAcceleration = RotationMath.estimateDerivative( rotationModel.getAvailableVelocityTimeSeries( 10 ) );
        return new RotationModelState( origState.copyBodies(), newAngle, velocity, angularAcceleration, origState.getTime() + dt );
    }
}
