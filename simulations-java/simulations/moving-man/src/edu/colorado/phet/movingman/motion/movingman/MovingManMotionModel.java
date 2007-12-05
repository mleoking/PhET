package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:37:57 PM
 */
public class MovingManMotionModel extends MotionModel implements UpdateableObject, IMovingManModel, IMotionBody {
    private ITemporalVariable x = new DefaultTemporalVariable();
    private ITemporalVariable v = new DefaultTemporalVariable();
    private ITemporalVariable a = new DefaultTemporalVariable();

    private ControlGraphSeries xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, x );
    private ControlGraphSeries vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, v );
    private ControlGraphSeries aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, a );

    public static final int MAX_T = 500;

    private UpdateStrategy positionDriven = new PositionDriven();
    private UpdateStrategy velocityDriven = new VelocityDriven();
    private UpdateStrategy accelDriven = new AccelDriven();

    private UpdateStrategy updateStrategy = positionDriven;

    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
    }

    public void setPosition( double x ) {
        if ( x > 10 ) {
            x = 10;
        }
        this.x.setValue( x );
    }

    public ITemporalVariable getXVariable() {
        return x;
    }

    public double getVelocity() {
        return v.getValue();
    }

    public double getAcceleration() {
        return a.getValue();
    }

    public double getPosition() {
        return x.getValue();
    }

    public void addAccelerationData( double acceleration, double time ) {
        a.addValue( acceleration, time );
    }

    public void addVelocityData( double v, double time ) {
        this.v.addValue( v, time );
    }

    public void addPositionData( double v, double time ) {
        this.x.addValue( v, time );
    }

    public void addPositionData( TimeData data ) {
        addPositionData( data.getValue(), data.getTime() );
    }

    public void addVelocityData( TimeData data ) {
        addVelocityData( data.getValue(), data.getTime() );
    }

    public void addAccelerationData( TimeData data ) {
        addAccelerationData( data.getValue(), data.getTime() );
    }

    public int getAccelerationSampleCount() {
        return a.getSampleCount();
    }

    public TimeData[] getRecentVelocityTimeSeries( int i ) {
        return v.getRecentSeries( i );
    }

    public int getPositionSampleCount() {
        return x.getSampleCount();
    }

    public int getVelocitySampleCount() {
        return v.getSampleCount();
    }

    public TimeData[] getRecentPositionTimeSeries( int i ) {
        return x.getRecentSeries( i );
    }

    public void setAcceleration( double value ) {
        a.setValue( value );
    }

    public void setVelocityDriven() {
        setUpdateStrategy( velocityDriven );
    }

    public void setVelocity( double v ) {
        this.v.setValue( v );
    }

    public void startRecording() {
        getTimeSeriesModel().startRecording();
    }

    public static class PositionDriven extends UpdateStrategy.PositionDriven {
        public void update( IMotionBody motionBody, double dt, double time ) {
            MovingManMotionModel m = (MovingManMotionModel) motionBody;
            double prevPosition = m.getPosition();
            TimeData newPosition = getNewX( motionBody, time );
            if ( prevPosition < 10 && newPosition.getValue() >= 10 ) {
                newPosition = new TimeData( 10, time );
                //signify a crash
            }
            motionBody.addPositionData( newPosition );
            motionBody.addVelocityData( getNewVelocity( motionBody ) );
            motionBody.addAccelerationData( getNewAcceleration( motionBody ) );
        }
    }

    public static class VelocityDriven extends UpdateStrategy.VelocityDriven {
        public void update( IMotionBody motionBody, double dt, double time ) {
            MovingManMotionModel m = (MovingManMotionModel) motionBody;
            double prevPosition = m.getPosition();
            TimeData newX = getNewPosition( motionBody, dt, time );
            TimeData newV = getNewVelocity( motionBody, dt, time );
            TimeData newA = getNewAcceleration( motionBody, dt );
            if ( prevPosition < 10 && newX.getValue() >= 10 ) {
                newX = new TimeData( 10, newX.getTime() );
                newV = new TimeData( 0, newV.getTime() );
                newA = new TimeData( 0, newA.getTime() );

                m.setPositionDriven();
                //signify a crash
            }
            motionBody.addPositionData( newX );
            motionBody.addVelocityData( newV );
            motionBody.addAccelerationData( newA );
        }
    }

    public static class AccelDriven extends UpdateStrategy.AccelerationDriven {
        public void update( IMotionBody motionBody, double dt, double time ) {
            MovingManMotionModel m = (MovingManMotionModel) motionBody;
            double prevPosition = m.getPosition();
            double newX = getNewPosition( motionBody, dt );
            double newV = getNewVelocity( motionBody, dt );
            double newA = m.getAcceleration();
            if ( prevPosition != 10 && newX >= 10 ) {
                newX = 10;
                newV = 0;
                newA = 0;

                m.setPositionDriven();
                //signify a crash
            }
            motionBody.addPositionData( newX, time );
            motionBody.addVelocityData( newV, time );
            motionBody.addAccelerationData( newA, time );
        }
    }

    public MovingManMotionModel( ConstantDtClock clock ) {
        super( clock, new TimeSeriesFactory.Default() );
        setMaxAllowedRecordTime( MAX_T );
    }

    public ControlGraphSeries[] getControlGraphSeriesArray() {
        return new ControlGraphSeries[]{xSeries, vSeries, aSeries};
    }

    public ControlGraphSeries getXSeries() {
        return xSeries;
    }

    public ControlGraphSeries getVSeries() {
        return vSeries;
    }

    public ControlGraphSeries getASeries() {
        return aSeries;
    }

    public UpdateStrategy getPositionDriven() {
        return positionDriven;
    }

    public UpdateStrategy getVelocityDriven() {
        return velocityDriven;
    }

    public UpdateStrategy getAccelDriven() {
        return accelDriven;
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        updateStrategy.update( this, dt, super.getTime() );
    }
}
