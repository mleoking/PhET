package edu.colorado.phet.movingman.motion.movingman;

import bsh.Interpreter;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.misc.ExpressionFrame;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:37:57 PM
 */
public class MovingManMotionModel extends MotionModel implements UpdateableObject, IMovingManModel, IMotionBody, UpdateStrategy.DefaultUpdateStrategy.Listener {
    private ITemporalVariable x = new DefaultTemporalVariable();
    private ITemporalVariable v = new DefaultTemporalVariable();
    private ITemporalVariable a = new DefaultTemporalVariable();

    private ControlGraphSeries xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), null, x, true );
    private ControlGraphSeries vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), null, v, true );
    private ControlGraphSeries aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), null, a, true );

    public static final int MAX_T = 20;

    private double min = -10;
    private double max = 10;

    private UpdateStrategy.PositionDriven positionDriven = new UpdateStrategy.PositionDriven( min, max );
    private UpdateStrategy.VelocityDriven velocityDriven = new UpdateStrategy.VelocityDriven( min, max );
    private UpdateStrategy.AccelerationDriven accelDriven = new UpdateStrategy.AccelerationDriven( min, max );

    private UpdateStrategy updateStrategy = positionDriven;
    private ArrayList listeners = new ArrayList();
    private boolean boundaryOpen = false;

    public MovingManMotionModel( ConstantDtClock clock ) {
        super( clock, new TimeSeriesFactory.Default() );
        setMaxAllowedRecordTime( MAX_T );

        positionDriven.addListener( this );
        velocityDriven.addListener( this );
        accelDriven.addListener( this );
        addTemporalVariables( new ITemporalVariable[]{x, v, a} );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        updateStrategy.update( this, dt, super.getTime() );
    }

    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
    }

    public void setPosition( double x ) {
        double origX = getPosition();
        final double newX = MathUtil.clamp( min, x, max );
        this.x.setValue( newX );
        double dt = getTimeSeriesModel().getTimeModelClock().getDt();
        if ( origX > min && newX == min ) {
            crashedMin( ( newX - origX ) / dt );
        }
        else if ( origX < max && newX == max ) {
            crashedMax( ( newX - origX ) / dt );
        }
    }

    public ITemporalVariable getXVariable() {
        return x;
    }

    public ITemporalVariable getVVariable() {
        return v;
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

    public void crashedMin( double velocity ) {
        System.out.println( "MovingManMotionModel.crashedMin, v=" + velocity );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).crashedMin( velocity );
        }
    }

    public void crashedMax( double velocity ) {
        System.out.println( "MovingManMotionModel.crashedMax, v=" + velocity );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).crashedMax( velocity );
        }
    }

    public ITemporalVariable getAVariable() {
        return a;
    }

    public boolean isBoundaryOpen() {
        return boundaryOpen;
    }

    public void setBoundaryOpen( boolean boundaryOpen ) {
        this.boundaryOpen = boundaryOpen;
        min = boundaryOpen ? Double.NEGATIVE_INFINITY : -10;
        max = boundaryOpen ? Double.POSITIVE_INFINITY : +10;
        positionDriven.setMin( min );
        positionDriven.setMax( max );
        velocityDriven.setMin( min );
        velocityDriven.setMax( max );
        accelDriven.setMin( min );
        accelDriven.setMax( max );
        notifyBoundaryChanged();
    }

    private void notifyBoundaryChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).boundaryChanged();
        }
    }

    public void setExpressionUpdate( String text ) {
        setUpdateStrategy( new MovingManExpressionUpdate( text ) );
    }

    public static interface Listener {
        void crashedMin( double velocity );

        void crashedMax( double velocity );

        void boundaryChanged();
    }

    public static class Adapter implements Listener {
        public void crashedMin( double velocity ) {
        }

        public void crashedMax( double velocity ) {
        }

        public void boundaryChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private class MovingManExpressionUpdate extends UpdateStrategy.PositionDriven {
        private String text;
        private Interpreter interpreter = new Interpreter();

        public MovingManExpressionUpdate( String text ) {
            this.text = text;
        }

        public TimeData getNewX( IMotionBody motionBody, double dt, double time ) {
            double x = ExpressionFrame.evaluate( time, text, interpreter );
            return new TimeData( x, time );
        }
    }
}
