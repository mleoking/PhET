package edu.colorado.phet.rotation.motion;

import edu.colorado.phet.rotation.model.RotationMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    public void update( MotionModel model, double dt ) {
        double newAngle = model.getAngle() + model.getAngularVelocity() * dt;
        double angularAcceleration = RotationMath.estimateDerivative( model.getAvailableVelocityTimeSeries( 10 ) );
        model.setAngle( newAngle );
        model.setAngularAcceleration( angularAcceleration );
    }
}
