package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class AccelerationDriven implements UpdateStrategy {

    public void update( MotionBody motionBody, double dt, double time ) {
        //assume a constant acceleration model with the given acceleration.
//        System.out.println( "AccelerationDriven.update" );
        double origAngVel = motionBody.getVelocity();
        motionBody.addAccelerationData( motionBody.getAcceleration(), time );
        motionBody.addVelocityData( motionBody.getVelocity() + motionBody.getAcceleration() * dt, time );
        motionBody.addPositionData( motionBody.getPosition() + ( motionBody.getVelocity() + origAngVel ) / 2.0 * dt, time );
    }
}
