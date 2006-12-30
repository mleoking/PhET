package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class AccelerationDriven implements UpdateStrategy {

    private double acceleration;

    public AccelerationDriven( double acceleration ) {
        this.acceleration = acceleration;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public RotationModelState update( RotationModel rotationModel, double dt ) {
        //assume a constant acceleration model with the given acceleration.
        RotationModelState state = rotationModel.getLastState();
        double newAngVel = state.getAngularVelocity() + acceleration * dt;
        double newAngle = state.getAngle() + ( state.getAngularVelocity() + newAngVel ) / 2.0 * dt;
        return new RotationModelState( state.copyBodies(), newAngle, newAngVel, acceleration, state.getTime() + dt );
    }
}
