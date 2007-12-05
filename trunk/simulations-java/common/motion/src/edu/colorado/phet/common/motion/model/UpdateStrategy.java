package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:36:26 PM
 */
public interface UpdateStrategy {
    void update( IMotionBody motionBody, double dt, double time );

    public static class PositionDriven implements UpdateStrategy {
        private int velocityWindow = 6;
        private int accelerationWindow = 6;

        //todo: try 2nd order derivative directly from position data?
        public void update( IMotionBody motionBody, double dt, double time ) {
            //        TimeData a = MotionMath.getSecondDerivative( smooth( motionBodySeries.getRecentPositionTimeSeries( Math.min( accelerationWindow, motionBodySeries.getPositionSampleCount() ) ), 20 ) );
            motionBody.addPositionData( getNewX( motionBody, time ) );
            motionBody.addVelocityData( getNewVelocity( motionBody ) );
            motionBody.addAccelerationData( getNewAcceleration( motionBody ) );
        }

        protected TimeData getNewX( IMotionBody motionBody, double time ) {
            return new TimeData( motionBody.getPosition(), time );
        }

        protected TimeData getNewAcceleration( IMotionBody motionBody ) {
            return MotionMath.getDerivative( MotionMath.smooth( motionBody.getRecentVelocityTimeSeries( Math.min( accelerationWindow, motionBody.getVelocitySampleCount() ) ), 1 ) );
        }

        protected TimeData getNewVelocity( IMotionBody motionBody ) {
            return MotionMath.getDerivative( MotionMath.smooth( motionBody.getRecentPositionTimeSeries( Math.min( velocityWindow, motionBody.getPositionSampleCount() ) ), 1 ) );
        }

        public void setVelocityWindow( int maxWindowSize ) {
            this.velocityWindow = maxWindowSize;
        }
    }

    public static class VelocityDriven implements UpdateStrategy {
        int velWindow = 10;

        public void update( IMotionBody motionBody, double dt, double time ) {
            TimeData newX = getNewPosition( motionBody, dt, time );
            TimeData newV = getNewVelocity( motionBody, dt, time );
            TimeData a = getNewAcceleration( motionBody, dt );

            motionBody.addPositionData( newX );
            motionBody.addVelocityData( newV );
            motionBody.addAccelerationData( a );//todo: why is it necessary that velocity be offset by dt in order to be correct?
        }

        protected TimeData getNewVelocity( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getVelocity(), time );
        }

        protected TimeData getNewAcceleration( IMotionBody motionBody, double dt ) {
            TimeData data = MotionMath.getDerivative( motionBody.getRecentVelocityTimeSeries( Math.min( velWindow, motionBody.getAccelerationSampleCount() ) ) );
            data = new TimeData( data.getValue(), data.getTime() + dt );
            return data;
        }

        protected TimeData getNewPosition( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getPosition() + motionBody.getVelocity() * dt, time );
        }
    }

    public static class AccelerationDriven implements UpdateStrategy {

        public void update( IMotionBody motionBody, double dt, double time ) {
            //assume a constant acceleration model with the given acceleration.
            //        System.out.println( "AccelerationDriven.update" );
            motionBody.addAccelerationData( motionBody.getAcceleration(), time );
            motionBody.addVelocityData( getNewVelocity( motionBody, dt ), time );
            motionBody.addPositionData( getNewPosition( motionBody, dt ), time );
        }

        protected double getNewPosition( IMotionBody motionBody, double dt ) {
            return motionBody.getPosition() + ( motionBody.getVelocity() + getNewVelocity( motionBody, dt ) ) / 2.0 * dt;
        }

        protected double getNewVelocity( IMotionBody motionBody, double dt ) {
            return motionBody.getVelocity() + motionBody.getAcceleration() * dt;
        }
    }
}
