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
    private SmoothDataSeries accelerationDataSeries;
    private SmoothDataSeries velocityDataSeries;
    private SmoothDataSeries positionDataSeries;
    private ArrayList listeners = new ArrayList();
    public double frictionForce;
    private Force1DPlotDeviceModel plotDeviceModel;

    public Force1DModel( Force1DModule module ) {
        block = new Block( this );
        appliedForceDataSeries = new SmoothDataSeries( 8 );
        accelerationDataSeries = new SmoothDataSeries( 8 );
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
        block.setPosition( 0.0 );
        block.setVelocity( 0.0 );
        plotDeviceModel.doReset();
    }

    public static interface Listener {
        public void appliedForceChanged();
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
        updateBlock();
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
        double netForce = appliedForce + frictionForce;
        double acc = netForce / block.getMass();
//        double velocity = block.getVelocity();
//        System.out.println( "frictionForce= " + frictionForce + ", appliedForce=" + appliedForce + ", netforce=" + netForce + ", acc=" + acc + ", veloc=" + velocity + ", position=" + block.getPosition() );
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
    }

    public Block getBlock() {
        return block;
    }

    public SmoothDataSeries getAppliedForceDataSeries() {
        return appliedForceDataSeries;
    }

    public SmoothDataSeries getAccelerationDataSeries() {
        return accelerationDataSeries;
    }

    public SmoothDataSeries getVelocityDataSeries() {
        return velocityDataSeries;
    }

    public SmoothDataSeries getPositionDataSeries() {
        return positionDataSeries;
    }
}
