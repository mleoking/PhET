package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    public void update( MotionModel model, double dt ) {
        double newAngle = model.getPosition() + model.getVelocity() * dt;
        double angularAcceleration = MotionMath.estimateDerivative( model.getAvailableVelocityTimeSeries( 10 ) );
        model.setAngle( newAngle );
        model.setAcceleration( angularAcceleration );
    }
}
