package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class PositionDriven implements UpdateStrategy {
    private double position;

    public PositionDriven( double position ) {
        this.position = position;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition( double position ) {
        this.position = position;
    }

    public RotationModelState update( RotationModel rotationModel, double dt ) {
        //assume a constant acceleration model with the given acceleration.
        RotationModelState origState = rotationModel.getLastState();
        double vel = RotationMath.estimateDerivative( rotationModel.getAvailablePositionTimeSeries( 10 ) );
        double acc = RotationMath.estimateDerivative( rotationModel.getAvailableVelocityTimeSeries( 10 ) );
        //todo: try 2nd order derivative directly from position data
        return new RotationModelState( origState.copyBodies(), position, vel, acc, origState.getTime() + dt );
    }
}
