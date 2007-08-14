package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.rotation.model.DefaultTemporalVariable;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 6:19:17 PM
 */
public class MotionBody implements IUpdateStrategy {

    private DefaultTemporalVariable x = new DefaultTemporalVariable();
    private DefaultTemporalVariable v = new DefaultTemporalVariable();
    private DefaultTemporalVariable a = new DefaultTemporalVariable();
    private MotionBodySeries motionBodySeries = new MotionBodySeries( x, v, a );
    private MotionBodyState motionBodyState = new MotionBodyState( x, v, a );//current state

    public MotionBody() {
    }

    public MotionBodyState getMotionBodyState() {
        return motionBodyState;
    }

    public void setTime( double time ) {
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValueForTime( time ) );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValueForTime( time ) );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValueForTime( time ) );
    }

    public void stepInTime( double time, double dt ) {
        motionBodySeries.stepInTime( this,time, dt );
        updateStateFromSeries();
    }

    public void updateStateFromSeries() {
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValue() );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValue() );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValue() );
    }

    public void clear() {
        motionBodySeries.clear();
    }

    public double getAcceleration() {
        return motionBodyState.getAcceleration();
    }

    public double getVelocity() {
        return motionBodyState.getVelocity();
    }

    public double getPosition() {
        return motionBodyState.getPosition();
    }

    public void setPosition( double position ) {
        motionBodyState.setPosition( position );
    }

    public PositionDriven getPositionDriven() {
        return motionBodySeries.getPositionDriven();
    }

    public void setPositionDriven() {
        motionBodySeries.setPositionDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        motionBodySeries.setUpdateStrategy( updateStrategy );
    }

    public UpdateStrategy getUpdateStrategy() {
        return motionBodySeries.getUpdateStrategy();
    }

    public ITemporalVariable getPositionVariable() {
        return x;
    }

    public ITemporalVariable getVelocityVariable() {
        return v;
    }

    public ITemporalVariable getAccelerationVariable() {
        return a;
    }

    public VelocityDriven getVelocityDriven() {
        return motionBodySeries.getVelocityDriven();
    }

    public AccelerationDriven getAccelDriven() {
        return motionBodySeries.getAccelDriven();
    }

    public boolean isPositionDriven() {
        return motionBodySeries.isPositionDriven();
    }

    public boolean isVelocityDriven() {
        return motionBodySeries.isVelocityDriven();
    }

    public boolean isAccelerationDriven() {
        return motionBodySeries.isAccelerationDriven();
    }

    public void reset() {
        clear();
        getPositionVariable().setValue( 0.0 );
        getVelocityVariable().setValue( 0.0 );
        getAccelerationVariable().setValue( 0.0 );
    }

    public int getPositionSampleCount() {
        return motionBodySeries.getPositionSampleCount();
    }

    public TimeData[] getRecentPositionTimeSeries( int numPts ) {
        return motionBodySeries.getRecentPositionTimeSeries( numPts);
    }

    public int getVelocitySampleCount() {
        return motionBodySeries.getVelocitySampleCount();
    }

    public TimeData[] getRecentVelocityTimeSeries( int num ) {
        return motionBodySeries.getRecentVelocityTimeSeries( num );
    }

    public void addPositionData( double position, double time ) {
        motionBodySeries.addPositionData( position, time );
    }

    public void addVelocityData( double value, double time ) {
        motionBodySeries.addVelocityData( value, time );
    }

    public void addAccelerationData( double value, double time ) {
        motionBodySeries.addAccelerationData( value, time );
    }

    public int getAccelerationSampleCount() {
        return motionBodySeries.getAccelerationSampleCount();
    }

    public TimeData getLastAcceleration() {
        return motionBodySeries.getLastAcceleration();
    }

    public TimeData getLastVelocity() {
        return motionBodySeries.getLastVelocity();
    }

    public TimeData[] getRecentAccelerationTimeSeries( int i ) {
        return motionBodySeries.getRecentAccelerationTimeSeries( i );
    }

    public TimeData getLastPosition() {
        return motionBodySeries.getLastPosition();
    }

    public void setAccelerationDriven() {
        motionBodySeries.setAccelerationDriven();
    }

    public void setVelocityDriven() {
        motionBodySeries.setVelocityDriven();
    }

    public TimeData getMaxVelocity() {
        return motionBodySeries.getMaxVelocity();
    }

    public TimeData getMaxAcceleration() {
        return motionBodySeries.getMaxAcceleration();
    }

    public TimeData getMinAcceleration() {
        return motionBodySeries.getMinAcceleration();
    }

    public TimeData getVelocity( int index ) {
        return motionBodySeries.getVelocity( index );
    }
}
