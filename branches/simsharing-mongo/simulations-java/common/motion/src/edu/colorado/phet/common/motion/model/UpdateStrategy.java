// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:36:26 PM
 */
public interface UpdateStrategy {
    void update( IMotionBody motionBody, double dt, double time );

    public static abstract class DefaultUpdateStrategy implements UpdateStrategy {
        private double min;
        private double max;

        private ArrayList listeners = new ArrayList();

        protected DefaultUpdateStrategy() {
            this( Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY );
        }

        public DefaultUpdateStrategy( double min, double max ) {
            this.min = min;
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public void setMin( double min ) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax( double max ) {
            this.max = max;
        }

        public abstract TimeData getNewX( IMotionBody motionBody, double dt, double time );

        public abstract TimeData getNewV( IMotionBody motionBody, double dt, double time );

        public abstract TimeData getNewA( IMotionBody motionBody, double dt, double time );

        public void doDefaultUpdate( IMotionBody motionBody, double dt, double time ) {
            double prevX = motionBody.getPosition();
            double prevV = motionBody.getVelocity();
            double prevA = motionBody.getAcceleration();
            TimeData newX = getNewX( motionBody, dt, time );
            TimeData newV = getNewV( motionBody, dt, time );
            TimeData newA = getNewA( motionBody, dt, time );

            if ( newX.getValue() > max ) {
                newX = new TimeData( max, newX.getTime() );
                newV = new TimeData( 0, newV.getTime() );
                newA = new TimeData( 0, newA.getTime() );
                if ( prevX < max ) {
                    notifyCrashedMax( prevV );
                }
            }
            else if ( newX.getValue() < min ) {
                newX = new TimeData( min, newX.getTime() );
                newV = new TimeData( 0, newV.getTime() );
                newA = new TimeData( 0, newA.getTime() );
                if ( prevX > min ) {
                    notifyCrashedMin( prevV );
                }
            }

            motionBody.addPositionData( newX );
            motionBody.addVelocityData( newV );
            motionBody.addAccelerationData( newA );
        }

        private void notifyCrashedMin( double velocity ) {
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).crashedMin( velocity );
            }
        }

        private void notifyCrashedMax( double velocity ) {
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).crashedMax( velocity );
            }
        }

        public static interface Listener {
            void crashedMin( double velocity );

            void crashedMax( double velocity );
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void update( IMotionBody motionBody, double dt, double time ) {
            doDefaultUpdate( motionBody, dt, time );
        }
    }

    public static class PositionDriven extends DefaultUpdateStrategy {
        private int velocityWindow;
        private int accelerationWindow;

        public PositionDriven( int velocityWindow, int accelerationWindow, double minX, double maxX ) {
            super( minX, maxX );
            this.velocityWindow = velocityWindow;
            this.accelerationWindow = accelerationWindow;
        }

        public TimeData getNewX( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getPosition(), time );
        }

        public TimeData getNewV( IMotionBody motionBody, double dt, double time ) {
            return MotionMath.getDerivative( MotionMath.smooth( motionBody.getRecentPositionTimeSeries( Math.min( velocityWindow, motionBody.getPositionSampleCount() ) ), 1 ) );
        }

        //todo: try 2nd order derivative directly from position data?
        public TimeData getNewA( IMotionBody motionBody, double dt, double time ) {
            return MotionMath.getDerivative( MotionMath.smooth( motionBody.getRecentVelocityTimeSeries( Math.min( accelerationWindow, motionBody.getVelocitySampleCount() ) ), 1 ) );
        }

        public void setVelocityWindow( int maxWindowSize ) {
            this.velocityWindow = maxWindowSize;
        }
    }

    public static class VelocityDriven extends DefaultUpdateStrategy {
        private int velWindow;

        public VelocityDriven( int velWindow, double min, double max ) {
            super( min, max );
            this.velWindow = velWindow;
        }

        public TimeData getNewV( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getVelocity(), time );
        }

        public TimeData getNewA( IMotionBody motionBody, double dt, double time ) {
            TimeData data = MotionMath.getDerivative( motionBody.getRecentVelocityTimeSeries( Math.min( velWindow, motionBody.getAccelerationSampleCount() ) ) );
            //todo: why is it necessary that velocity be offset by dt in order to be correct?
            data = new TimeData( data.getValue(), data.getTime() + dt );
            return data;
        }

        public TimeData getNewX( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getPosition() + motionBody.getVelocity() * dt, time );
        }
    }

    public static class AccelerationDriven extends DefaultUpdateStrategy {
        public AccelerationDriven() {
        }

        public AccelerationDriven( double min, double max ) {
            super( min, max );
        }

        public TimeData getNewX( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getPosition() + ( motionBody.getVelocity() + getNewV( motionBody, dt, time ).getValue() ) / 2.0 * dt, time );
        }

        public TimeData getNewV( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getVelocity() + motionBody.getAcceleration() * dt, time );
        }

        public TimeData getNewA( IMotionBody motionBody, double dt, double time ) {
            return new TimeData( motionBody.getAcceleration(), time );
        }
    }
}
