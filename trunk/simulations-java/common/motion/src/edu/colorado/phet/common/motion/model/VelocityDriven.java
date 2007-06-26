package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class VelocityDriven implements UpdateStrategy {
    public void update( MotionModel model, double dt ) {
        double newX = model.getPosition() + model.getVelocity() * dt;
        int windowSize = Math.min( 10, model.getVelocitySampleCount() );
        double angularAcceleration = MotionMath.estimateDerivative( model.getRecentVelocityTimeSeries( windowSize ) );
//        model.setPosition( newAngle );
        model.addPositionData( newX, model.getTime() );
        model.addVelocityData( model.getVelocity(), model.getTime() );
        model.addAccelerationPast( angularAcceleration, windowSize / 2 );
    }
}
