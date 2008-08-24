package edu.colorado.phet.forces1d.model;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.phetcommon.model.ModelElement;
import edu.colorado.phet.forces1d.phetcommon.util.EventChannel;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 9:54:55 PM
 */
public class Force1DModel implements ModelElement {

    private static final double MAX_TIME = 20.0;
    private static final double EARTH_GRAVITY = 9.8;
    private double gravity = EARTH_GRAVITY;
    private double appliedForce;
    public double frictionForce;
    private double netForce;
    private double wallForce = 0.0;

    private Block block;
    private SmoothDataSeries appliedForceDataSeries;
    private SmoothDataSeries netForceDataSeries;
    private SmoothDataSeries accelerationDataSeries;
    private SmoothDataSeries frictionForceDataSeries;
    private SmoothDataSeries velocityDataSeries;
    private SmoothDataSeries positionDataSeries;
    private DataSeries timeSeries;
    private SmoothDataSeries gravitySeries;
    private SmoothDataSeries staticSeries;
    private SmoothDataSeries kineticSeries;
    private SmoothDataSeries massSeries;

    private ArrayList listeners = new ArrayList();
    private Force1DPlotDeviceModel plotDeviceModel;
    private BoundaryCondition open;
    private BoundaryCondition walls;
    private BoundaryCondition boundaryCondition;
    private ArrayList boundaryConditionListeners = new ArrayList();

    private ArrayList imageSeries = new ArrayList();
    private Forces1DModule module;
    private boolean friction = true;

    public Force1DModel( Forces1DModule module ) {
        this.module = module;
        block = new Block( this );
        open = new BoundaryCondition.Open( this );
        walls = new BoundaryCondition.Walls( this );
        boundaryCondition = open;
        int numSmoothingPoints = 8;
        plotDeviceModel = new Force1DPlotDeviceModel( module, this, MAX_TIME, 1 / 50.0 );

        appliedForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        netForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        accelerationDataSeries = new SmoothDataSeries( numSmoothingPoints );
        frictionForceDataSeries = new SmoothDataSeries( numSmoothingPoints );
        velocityDataSeries = new SmoothDataSeries( numSmoothingPoints );
        positionDataSeries = new SmoothDataSeries( numSmoothingPoints );
        gravitySeries = new SmoothDataSeries( numSmoothingPoints );
        staticSeries = new SmoothDataSeries( numSmoothingPoints );
        kineticSeries = new SmoothDataSeries( numSmoothingPoints );
        massSeries = new SmoothDataSeries( numSmoothingPoints );
        timeSeries = new DataSeries();
    }

    public double getNetForce() {
        return netForce;
    }

    public void addBoundaryConditionListener( BoundaryCondition.Listener boundaryConditionListener ) {
        boundaryConditionListeners.add( boundaryConditionListener );
    }

    void setWallForce( double wallForce ) {
        if ( this.wallForce != wallForce ) {
            this.wallForce = wallForce;
        }
    }

    public double getWallForce() {
        return wallForce;
    }

    public void setBoundsOpen() {
        this.boundaryCondition = open;
        for ( int i = 0; i < boundaryConditionListeners.size(); i++ ) {
            BoundaryCondition.Listener boundaryConditionListener = (BoundaryCondition.Listener) boundaryConditionListeners.get( i );
            boundaryConditionListener.boundaryConditionOpen();
        }
    }

    public void setBoundsWalled() {
        this.boundaryCondition = walls;
        for ( int i = 0; i < boundaryConditionListeners.size(); i++ ) {
            ( (BoundaryCondition.Listener) boundaryConditionListeners.get( i ) ).boundaryConditionWalls();
        }
    }

    public void setPlaybackIndex( int index ) {
        int numDataPoints = netForceDataSeries.numSmoothedPoints();
        if ( index < numDataPoints ) {
            if ( index == 0 ) {
                this.netForce = 0;
                this.frictionForce = 0;//TODO this could cause more problems than it solves.
            }
            else {
                this.netForce = netForceDataSeries.smoothedPointAt( index );
                this.frictionForce = frictionForceDataSeries.smoothedPointAt( index );
            }
            setAppliedForce( appliedForceDataSeries.smoothedPointAt( index ) );
            block.setAcceleration( accelerationDataSeries.smoothedPointAt( index ) );
            block.setVelocity( velocityDataSeries.smoothedPointAt( index ) );
            block.setPosition( positionDataSeries.smoothedPointAt( index ) );
            this.appliedForceChanged();
            double newGravity = gravitySeries.smoothedPointAt( index );
            if ( newGravity != gravity ) {
                this.gravity = newGravity;
                fireGravityChanged();
            }
            double newKinetic = kineticSeries.smoothedPointAt( index );
            if ( newKinetic != block.getStaticFriction() ) {
                block.setKineticFriction( newKinetic );
            }
            double newStatic = staticSeries.smoothedPointAt( index );
            if ( newStatic != block.getStaticFriction() ) {
                block.setStaticFriction( newStatic );
            }
            double newMass = massSeries.smoothedPointAt( index );
            if ( newMass != block.getMass() ) {
                block.setMass( newMass );
            }
            int imageIndex = ( (Integer) imageSeries.get( index ) ).intValue();
            module.setImageIndex( imageIndex );
        }//TODO else we should stop playback

    }

    public void stepInTime( double dt ) {
        plotDeviceModel.stepInTime( dt );
    }

    public void stepRecord( double dt ) {
        updateBlockAcceleration();

        block.stepInTime( dt );
        boundaryCondition.apply();

        netForceDataSeries.addPoint( netForce );
        frictionForceDataSeries.addPoint( frictionForce );
        appliedForceDataSeries.addPoint( getAppliedForce() );

        accelerationDataSeries.addPoint( block.getAcceleration() );
        velocityDataSeries.addPoint( block.getVelocity() );
        positionDataSeries.addPoint( block.getPosition() );
        gravitySeries.addPoint( getGravity() );
        kineticSeries.addPoint( block.getKineticFriction() );
        staticSeries.addPoint( block.getStaticFriction() );
        massSeries.addPoint( block.getMass() );
        timeSeries.addPoint( plotDeviceModel.getRecordingTimer().getTime() );
        imageSeries.add( new Integer( module.getImageIndex() ) );
    }

    public void stepPlayback( double time, int playbackIndex ) {
        plotDeviceModel.cursorMovedToTime( time, playbackIndex );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getStoredFrictionForceValue() {
        return frictionForce;
    }

    public PlotDeviceModel getPlotDeviceModel() {
        return plotDeviceModel;
    }

    public void reset() {

//        plotDeviceModel.doReset();//this reinits the model with initial parameters of the graph.
        frictionForce = 0;//so we must clear them afterwards.
        netForce = 0;
        block.setPosition( 0.0 );
        block.setVelocity( 0.0 );
        setAppliedForce( 0.0 );//to inform listeners.

        netForceDataSeries.reset();
        appliedForceDataSeries.reset();
        frictionForceDataSeries.reset();
        accelerationDataSeries.reset();
        positionDataSeries.reset();
        velocityDataSeries.reset();
        gravitySeries.reset();
        kineticSeries.reset();
        staticSeries.reset();
        massSeries.reset();
        timeSeries.reset();
        imageSeries.clear();
        updateBlockAcceleration();
        fireGravityChanged();

        plotDeviceModel.doReset();//reset the model again to inform the plotDeviceListeners.
        plotDeviceModel.clearData();//kind of a hack.
    }

    public DataSeries getNetForceSeries() {
        return netForceDataSeries.getSmoothedDataSeries();
    }

    public DataSeries getFrictionForceSeries() {
        return frictionForceDataSeries.getSmoothedDataSeries();
    }

    public void setPlaybackMode() {
        plotDeviceModel.setPlaybackMode();
    }

    public void setPaused( boolean paused ) {
        plotDeviceModel.setPaused( paused );
    }

    public void resetRecordPointer( int index ) {
        clear( netForceDataSeries, index );
        clear( frictionForceDataSeries, index );
        clear( appliedForceDataSeries, index );
        clear( accelerationDataSeries, index );
        clear( velocityDataSeries, index );
        clear( positionDataSeries, index );
        clear( gravitySeries, index );
        clear( kineticSeries, index );
        clear( staticSeries, index );
        clear( massSeries, index );
//        clear( timeSeries, index );//TODO clear this.

        List retain = imageSeries.subList( 0, index );
        imageSeries.clear();
        imageSeries.addAll( retain );

        module.relayoutPlots();
    }

    private void clear( SmoothDataSeries dataSeries, int index ) {
        dataSeries.clearAfter( index );
    }

    public void setFrictionEnabled( boolean friction ) {
        this.friction = friction;
    }

    EventChannel eventChannel = new EventChannel( CollisionListener.class );

    public SmoothDataSeries getAppliedForceSeries() {
        return appliedForceDataSeries;
    }

    public static class CollisionEvent {
        double momentum = 0.0;

        public CollisionEvent( double momentum ) {
            this.momentum = momentum;
        }

        public double getMomentum() {
            return momentum;
        }
    }

    public static interface CollisionListener extends EventListener {
        public void collisionOccurred( CollisionEvent ce );
    }

    public void fireCollisionHappened( double momentum ) {
        CollisionListener cl = (CollisionListener) eventChannel.getListenerProxy();
        CollisionEvent ce = new CollisionEvent( momentum );
        cl.collisionOccurred( ce );
    }

    public void addCollisionListener( CollisionListener collisionListener ) {
        eventChannel.addListener( collisionListener );
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
        updateBlockAcceleration();
        fireGravityChanged();
    }

    private void fireGravityChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.gravityChanged();
        }
    }

    public double getAppliedForce() {
        return appliedForce;
    }

    public void setAppliedForce( double appliedForce ) {
//        System.out.println( "appliedForce = " + appliedForce );
//        new Exception().printStackTrace( );
        if ( appliedForce != this.appliedForce ) {
            this.appliedForce = appliedForce;
            updateBlockAcceleration();
            appliedForceChanged();
        }
    }

    private void appliedForceChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.appliedForceChanged();
        }
    }

    void updateBlockAcceleration() {
        frictionForce = getFrictionForce();
        wallForce = boundaryCondition.getWallForce( appliedForce, frictionForce );
        netForce = appliedForce + frictionForce + wallForce;
        double acc = netForce / block.getMass();
        block.setAcceleration( acc );
//        System.out.println( "frictionForce = " + frictionForce );
    }

    public double getFrictionForce() {
        if ( !friction ) {
            return 0.0;
        }
        if ( block.isMoving() ) {
            double sign = block.getVelocity() >= 0 ? -1 : 1;
            double kineticFrictionForce = sign * block.getKineticFriction() * block.getMass() * gravity;
            return kineticFrictionForce;
        }
        else {//block was stationary
            double u = Math.max( block.getKineticFriction(), block.getStaticFriction() );
            double maxStaticFrictionForce = u * block.getMass() * gravity;
            if ( Math.abs( maxStaticFrictionForce ) > Math.abs( appliedForce ) ) {
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
