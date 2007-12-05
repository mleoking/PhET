package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.mri.model.MriModel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:37:57 PM
 */
public class MovingManMotionModel extends MotionModel implements UpdateableObject, IMovingManModel, IMotionBody, UpdateStrategy.DefaultUpdateStrategy.Listener {
    private ITemporalVariable x = new DefaultTemporalVariable();
    private ITemporalVariable v = new DefaultTemporalVariable();
    private ITemporalVariable a = new DefaultTemporalVariable();

    private ControlGraphSeries xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, x );
    private ControlGraphSeries vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, v );
    private ControlGraphSeries aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, a );

    public static final int MAX_T = 500;

    private double min = -10;
    private double max = 10;

    private UpdateStrategy.PositionDriven positionDriven = new UpdateStrategy.PositionDriven( min, max );
    private UpdateStrategy.VelocityDriven velocityDriven = new UpdateStrategy.VelocityDriven( min, max );
    private UpdateStrategy.AccelerationDriven accelDriven = new UpdateStrategy.AccelerationDriven( min, max );

    private UpdateStrategy updateStrategy = positionDriven;

    public MovingManMotionModel( ConstantDtClock clock ) {
        super( clock, new TimeSeriesFactory.Default() );
        setMaxAllowedRecordTime( MAX_T );

        positionDriven.addListener( this );
        velocityDriven.addListener( this );
        accelDriven.addListener( this );
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        x.setPlaybackTime( time );
        v.setPlaybackTime( time );
        a.setPlaybackTime( time );
    }

    public void clear() {
        super.clear();
        x.clear();
        v.clear();
        a.clear();
    }

    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
    }

    public void setPosition( double x ) {
        double origX=getPosition();
        final double newX = MathUtil.clamp( min, x, max );
        this.x.setValue( newX );
        if (origX>min&&newX==min){
            crashedMin();
        }else if (origX<max&&newX==max){
            crashedMax();
        }
    }

    public ITemporalVariable getXVariable() {
        return x;
    }

    public ITemporalVariable getVVariable(){
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

    public void crashedMin() {
        System.out.println( "MovingManMotionModel.crashedMin" );
    }

    public void crashedMax() {
        System.out.println( "MovingManMotionModel.crashedMax" );
    }

    public ITemporalVariable getAVariable() {
        return a;
    }
}
