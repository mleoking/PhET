package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
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

    public void update( RotationModel model, double dt ) {
        //assume a constant acceleration model with the given acceleration.
//        RotationModelState origState = rotationModel.getLastState();
        double vel = RotationMath.estimateDerivative( model.getAvailablePositionTimeSeries( 10 ) );
        double acc = RotationMath.estimateDerivative( model.getAvailableVelocityTimeSeries( 10 ) );
        //todo: try 2nd order derivative directly from position data
//        return new RotationModelState( model.copyRotationBodies(), position, vel, acc, rotationModel.getLastState().getTime() + dt );
    }
}
