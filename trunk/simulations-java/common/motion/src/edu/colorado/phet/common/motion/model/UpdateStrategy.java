package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:36:26 PM
 */
public interface UpdateStrategy {
    void update( MotionBody motionBody, double dt, double time );

    /**
     * User: Sam Reid
     * Date: Dec 29, 2006
     * Time: 11:37:32 PM
     */

    public static class AccelerationDriven implements UpdateStrategy {

        public void update( MotionBody motionBody, double dt, double time ) {
            //assume a constant acceleration model with the given acceleration.
            //        System.out.println( "AccelerationDriven.update" );
            double origAngVel = motionBody.getVelocity();
            motionBody.addAccelerationData( motionBody.getAcceleration(), time );
            motionBody.addVelocityData( motionBody.getVelocity() + motionBody.getAcceleration() * dt, time );
            motionBody.addPositionData( motionBody.getPosition() + ( motionBody.getVelocity() + origAngVel ) / 2.0 * dt, time );
        }
    }

    /**
     * User: Sam Reid
     * Date: Dec 29, 2006
     * Time: 11:37:32 PM
     */

    public static class VelocityDriven implements UpdateStrategy {
        int velWindow = 10;

        public void update( MotionBody motionBody, double dt, double time ) {
            double newX = motionBody.getPosition() + motionBody.getVelocity() * dt;
            TimeData a = MotionMath.getDerivative( motionBody.getRecentVelocityTimeSeries( Math.min( velWindow, motionBody.getAccelerationSampleCount() ) ) );
            motionBody.addPositionData( newX, time );
            motionBody.addVelocityData( motionBody.getVelocity(), time );
            motionBody.addAccelerationData( a.getValue(), a.getTime() + dt );//todo: why is it necessary that velocity be offset by dt in order to be correct?
        }
    }

    /**
     * User: Sam Reid
     * Date: Dec 29, 2006
     * Time: 11:37:32 PM
     */

    public static class PositionDriven implements UpdateStrategy {
        private int velocityWindow = 6;
        private int accelerationWindow = 6;

        //todo: try 2nd order derivative directly from position data?
        public void update( MotionBody motionBody, double dt, double time ) {
            TimeData v = MotionMath.getDerivative( MotionMath.smooth( motionBody.getRecentPositionTimeSeries( Math.min( velocityWindow, motionBody.getPositionSampleCount() ) ), 1 ) );
            TimeData a = MotionMath.getDerivative( MotionMath.smooth( motionBody.getRecentVelocityTimeSeries( Math.min( accelerationWindow, motionBody.getVelocitySampleCount() ) ), 1 ) );
            //        TimeData a = MotionMath.getSecondDerivative( smooth( motionBodySeries.getRecentPositionTimeSeries( Math.min( accelerationWindow, motionBodySeries.getPositionSampleCount() ) ), 20 ) );

            motionBody.addPositionData( motionBody.getPosition(), time );
            motionBody.addVelocityData( v.getValue(), v.getTime() );
            motionBody.addAccelerationData( a.getValue(), a.getTime() );
        }

        public double getAccelerationWindow() {
            return accelerationWindow;
        }

        public void setVelocityWindow( int maxWindowSize ) {
            this.velocityWindow = maxWindowSize;
        }

        public int getVelocityWindow() {
            return velocityWindow;
        }

    }
}
