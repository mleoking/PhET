/** Sam Reid*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 9:54:55 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DModel implements ModelElement {

    private static final double MAX_TIME = 20.0;
    private static final double EARTH_GRAVITY = 9.8;
    private double gravity = EARTH_GRAVITY;
    private double appliedForce;
    private Block block;
    private SmoothDataSeries appliedForceDataSeries;
    private SmoothDataSeries netForceDataSeries;
    private SmoothDataSeries accelerationDataSeries;
    private SmoothDataSeries frictionForceDataSeries;
    private SmoothDataSeries velocityDataSeries;
    private SmoothDataSeries positionDataSeries;
    private ArrayList listeners = new ArrayList();
    public double frictionForce;
    private Force1DPlotDeviceModel plotDeviceModel;
    private double netForce;

    public Force1DModel( Force1DModule module ) {
        block = new Block( this );
        int numSmoothingPoints = 8;
        appliedForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        netForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        accelerationDataSeries = new SmoothDataSeries( numSmoothingPoints );
        frictionForceDataSeries = new SmoothDataSeries( numSmoothingPoints );

        velocityDataSeries = new SmoothDataSeries( 8 );
        positionDataSeries = new SmoothDataSeries( 8 );
        plotDeviceModel = new Force1DPlotDeviceModel( module, this, MAX_TIME );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getStoredFrictionForceValue() {
        return frictionForce;
    }

    public double getTotalForce() {
        return getStoredFrictionForceValue() + getAppliedForce();
    }

    public PlotDeviceModel getPlotDeviceModel() {
        return plotDeviceModel;
    }

    public void reset() {
        appliedForce = 0;
        frictionForce = 0;
        netForce = 0;
        block.setPosition( 0.0 );
        block.setVelocity( 0.0 );
        plotDeviceModel.doReset();
        netForceDataSeries.reset();
        appliedForceDataSeries.reset();
        frictionForceDataSeries.reset();
        updateBlock();
    }

    public DataSeries getNetForceSeries() {
        return netForceDataSeries.getSmoothedDataSeries();
    }

    public void addAppliedForcePoint( double value, double dt ) {
        getAppliedForceDataSeries().addPoint( value );
    }

    public DataSeries getFrictionForceSeries() {
        return frictionForceDataSeries.getSmoothedDataSeries();
    }

    public static interface Listener {
        void appliedForceChanged();

        void gravityChanged();
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
        updateBlock();
        gravityChanged();
    }

    private void gravityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.gravityChanged();
        }
    }

    public double getAppliedForce() {
        return appliedForce;
    }

    public void setAppliedForce( double appliedForce ) {
        this.appliedForce = appliedForce;
        updateBlock();
        appliedForceChanged();
    }

    private void appliedForceChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.appliedForceChanged();
        }
    }

    void updateBlock() {
        frictionForce = getFrictionForce();
        netForce = appliedForce + frictionForce;
        double acc = netForce / block.getMass();
        block.setAcceleration( acc );
    }

    public double getFrictionForce() {
        if( block.isMoving() ) {
            double sign = block.getVelocity() >= 0 ? -1 : 1;
            double kineticFrictionForce = sign * block.getKineticFriction() * block.getMass() * gravity;
            return kineticFrictionForce;
        }
        else {//block was stationary
            double u = Math.max( block.getKineticFriction(), block.getStaticFriction() );
            double maxStaticFrictionForce = u * block.getMass() * gravity;
            if( Math.abs( maxStaticFrictionForce ) > Math.abs( appliedForce ) ) {
                //block stays at rest, friction balances applied force.
                return -appliedForce;
            }
            else { //applied force overcomes friction force, block starts moving
                double sign = appliedForce >= 0 ? -1 : 1;
                double frictionForce = u * block.getMass() * gravity * sign;
                return frictionForce; //should be less than applied force
            }
        }
    }

    public void stepInTime( double dt ) {
        dt /= 50.0;
        updateBlock();
        block.stepInTime( dt );
        plotDeviceModel.stepInTime( dt );
        if( plotDeviceModel.isTakingData() ) {
            netForceDataSeries.addPoint( netForce );
            frictionForceDataSeries.addPoint( frictionForce );
        }
    }

    public Block getBlock() {
        return block;
    }

    public SmoothDataSeries getAppliedForceDataSeries() {
        return appliedForceDataSeries;
    }

    public DataSeries getAccelerationDataSeries() {
        return accelerationDataSeries.getSmoothedDataSeries();
    }

    public SmoothDataSeries getVelocityDataSeries() {
        return velocityDataSeries;
    }

    public SmoothDataSeries getPositionDataSeries() {
        return positionDataSeries;
    }
}
