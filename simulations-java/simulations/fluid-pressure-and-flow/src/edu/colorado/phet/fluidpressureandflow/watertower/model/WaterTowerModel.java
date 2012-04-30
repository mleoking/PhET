// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.VelocitySensorContext;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.createPolar;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.METRIC;
import static java.lang.Math.sqrt;

/**
 * Model for the "water tower" tab, which has a water tower that the user can raise/lower and empty/fill.
 *
 * @author Sam Reid
 */
public class WaterTowerModel extends FluidPressureAndFlowModel implements VelocitySensorContext {

    //Model for the water tower and the water
    public final WaterTower waterTower = new WaterTower();

    //The hose that can optionally be attached to the water tower
    public final Hose hose;

    //drops coming out of the water tower
    private final ArrayList<WaterDrop> waterTowerDrops = new ArrayList<WaterDrop>();

    //drops coming from the faucet into the water tower
    private final ArrayList<WaterDrop> faucetDrops = new ArrayList<WaterDrop>();

    //source of randomness for creating drops
    private final Random random = new Random();

    //Rate of filling up the water tower from the faucet
    public final FaucetFlowRate faucetFlowRate = new FaucetFlowRate();

    //Listeners
    private final ArrayList<VoidFunction1<WaterDrop>> waterTowerDropAddedListeners = new ArrayList<VoidFunction1<WaterDrop>>();
    private final ArrayList<VoidFunction1<WaterDrop>> faucetDropAddedListeners = new ArrayList<VoidFunction1<WaterDrop>>();
    private final ArrayList<SimpleObserver> velocityUpdateListeners = new ArrayList<SimpleObserver>();

    public WaterTowerModel() {
        super( METRIC );

        //Add the pressure and velocity sensors
        addPressureSensor( new PressureSensor( pressureSensor0, this, 30, 25 ) );
        addPressureSensor( new PressureSensor( pressureSensor1, this, 30, 25 ) );
        addVelocitySensor( new FPAFVelocitySensor( velocitySensor0, this, 30, 30 ) );
        addVelocitySensor( new FPAFVelocitySensor( velocitySensor1, this, 30, 30 ) );

        //Step when the clock ticks
        getClock().addSimulationTimeChangeListener( new VoidFunction1<Double>() {
            public void apply( final Double dt ) {
                if ( dt > 0 ) { stepInTime( dt ); }
            }
        } );

        hose = new Hose( new CompositeProperty<ImmutableVector2D>( new Function0<ImmutableVector2D>() {
            public ImmutableVector2D apply() {
                return new ImmutableVector2D( waterTower.getHoleLocation() );
            }
        }, waterTower.tankBottomCenter ), WaterTower.HOLE_SIZE );
    }

    //Update the simulation when the clock ticks
    private void stepInTime( double dt ) {

        //Update water tower
        double origFluidVolume = updateWaterTower();

        //Update faucet
        updateFaucet( dt, origFluidVolume );

        //Notify listeners that velocity values may have changed
        for ( SimpleObserver velocityUpdateListener : velocityUpdateListeners ) {
            velocityUpdateListener.update();
        }
    }

    //Allow water to flow out of the water tower if there is any water left, and if the door is open
    private double updateWaterTower() {

        //Compute the velocity of water leaving the water tower at the bottom from Toricelli's theorem, one of the main learning goals of this tab
        final double waterHeight = waterTower.getWaterLevel();
        double velocity = !hose.enabled.get() ? sqrt( 2 * EARTH_GRAVITY * waterHeight ) : sqrt( 2 * EARTH_GRAVITY * ( waterHeight + waterTower.tankBottomCenter.get().getY() - hose.y.get() ) );

        //If the user moved the hose above the water level, then do not let any water flow out of the hose
        if ( velocity > 0 ) {

            //Determine how much fluid should leave, and how much will be left
            //Since water is incompressible, the volume that can flow out per second is proportional to the expelled velocity
            double waterVolumeExpelled = velocity / 10;
            double remainingVolume = waterTower.fluidVolume.get();

            //the decrease in volume of the water tower should be proportional to the velocity at the output hole
            double dropVolume = remainingVolume > waterVolumeExpelled ? waterVolumeExpelled : remainingVolume;
            double origFluidVolume = waterTower.fluidVolume.get();

            //Handle any water that should leak out
            if ( waterTower.isHoleOpen() && remainingVolume > 0 ) {

                //Create the water drop
                double radius = Math.pow( dropVolume * 3.0 / 4.0 / Math.PI, 1.0 / 3.0 );
                double waterDropY = waterTower.getHoleLocation().getY() + random.nextGaussian() * 0.04 + radius;
                if ( waterDropY + radius > waterTower.getWaterLevel() + waterTower.getTankShape().getY() ) {

                    //shift up a bit, otherwise looks like it's coming off the bottom of the outside of the tank
                    waterDropY = waterTower.getWaterLevel() + waterTower.getTankShape().getY() - radius + 0.1;
                }

                //Create a water drop either at the water tower opening or at the opening of the hose
                final WaterDrop drop = !hose.enabled.get() ?
                                       new WaterDrop( new ImmutableVector2D( waterTower.getHoleLocation().getX() + random.nextGaussian() * 0.04, waterDropY ), new ImmutableVector2D( velocity, 0 ), dropVolume, true ) :
                                       new WaterDrop( new ImmutableVector2D( hose.outputPoint.get().getX() + random.nextGaussian() * 0.04, hose.outputPoint.get().getY() ), createPolar( velocity, hose.angle.get() ), dropVolume, false );

                //Add the drop to the list and notify listeners
                waterTowerDrops.add( drop );
                for ( VoidFunction1<WaterDrop> dropAddedListener : waterTowerDropAddedListeners ) {
                    dropAddedListener.apply( drop );
                }

                //Decrease the volume of the water tower accordingly
                waterTower.setFluidVolume( waterTower.fluidVolume.get() - drop.getVolume() );
            }
            return origFluidVolume;
        }
        else {
            return waterTower.fluidVolume.get();
        }
    }

    //Perform any model updates related to the faucet, creating new drops to fall into the water tower and propagating them
    private void updateFaucet( double dt, double origFluidVolume ) {

        //emit drops from faucet that will keep the tank at a constant volume (time averaged)
        double newVolume = waterTower.fluidVolume.get();
        double faucetDropVolume = faucetFlowRate.automatic.get() ?
                                  origFluidVolume - newVolume :
                                  faucetFlowRate.flow.get();

        if ( faucetDropVolume > 0 && !waterTower.full.get() ) {

            //Randomly spread out the water in x and y so it doesn't look so discrete when it falls a long way and separates
            double spreadX = 0.02;

            //Setting the velocity and y-spread to zero ensures smooth velocity values when measuring the fluid coming out of the faucet
            double spreadY = 0.0;
            double velocitySpreadX = 0.00;
            double velocitySpreadY = 0.00;
            WaterDrop faucetDrop = new WaterDrop( new ImmutableVector2D( -3 + random.nextGaussian() * spreadX,//magic number picked based on graphics
                                                                         WaterTower.MAX_Y + WaterTower.TANK_HEIGHT + 2 + random.nextGaussian() * spreadY ),
                                                  new ImmutableVector2D( random.nextGaussian() * velocitySpreadX, random.nextGaussian() * velocitySpreadY ), faucetDropVolume, true );
            faucetDrops.add( faucetDrop );
            for ( VoidFunction1<WaterDrop> listener : faucetDropAddedListeners ) {
                listener.apply( faucetDrop );
            }
        }

        //graphics looked weird if we had them remove at y=0 since the water drops would flicker
        updateWaterDrops( -2, waterTowerDrops, dt, new VoidFunction1.Null<WaterDrop>() );

        //absorb the water from the faucet and increase the water tower volume
        updateWaterDrops( waterTower.getWaterShape().getBounds2D().getMaxY(), faucetDrops, dt, new VoidFunction1<WaterDrop>() {
            public void apply( WaterDrop drop ) {
                waterTower.setFluidVolume( Math.min( waterTower.fluidVolume.get() + drop.getVolume(), WaterTower.TANK_VOLUME ) );
            }
        } );
    }

    //Propagate the specified water drops
    private void updateWaterDrops( double thresholdY, ArrayList<WaterDrop> waterDrops, double dt, VoidFunction1<WaterDrop> collision ) {
        ArrayList<WaterDrop> toRemove = new ArrayList<WaterDrop>();
        for ( WaterDrop drop : waterDrops ) {
            drop.stepInTime( dt );
            if ( drop.position.get().getY() < thresholdY ) {
                collision.apply( drop );
                toRemove.add( drop );
            }
        }
        removeDrops( waterDrops, toRemove );
    }

    //Remove any water drops that should exit the model
    private void removeDrops( ArrayList<WaterDrop> waterDrops, ArrayList<WaterDrop> toRemove ) {
        for ( WaterDrop waterDrop : toRemove ) {
            waterDrop.notifyRemoved();
        }
        waterDrops.removeAll( toRemove );
    }

    public void addWaterTowerDropAddedListener( VoidFunction1<WaterDrop> dropAddedListener ) {
        waterTowerDropAddedListeners.add( dropAddedListener );
    }

    public void addFaucetDropAddedListener( VoidFunction1<WaterDrop> dropAddedListener ) {
        faucetDropAddedListeners.add( dropAddedListener );
    }

    //Find the velocity at the specified point
    public Option<ImmutableVector2D> getVelocity( double x, double y ) {
        for ( WaterDrop waterTowerDrop : waterTowerDrops ) {
            if ( waterTowerDrop.contains( x, y ) ) {
                return new Option.Some<ImmutableVector2D>( waterTowerDrop.velocity.get() );
            }
        }
        return new Option.None<ImmutableVector2D>();
    }

    public void addVelocityUpdateListener( SimpleObserver observer ) {
        velocityUpdateListeners.add( observer );
    }

    @Override public void reset() {
        super.reset();
        waterTower.reset();
        faucetFlowRate.reset();
        removeDrops( faucetDrops, faucetDrops );
        removeDrops( waterTowerDrops, waterTowerDrops );
        hose.reset();
    }

    //Get the pressure at the specified point
    @Override public double getPressure( double x, double y ) {
        if ( waterTower.getWaterShape().contains( x, y ) ) {
            final double waterTopY = waterTower.getWaterShape().getBounds2D().getMaxY();
            double distanceUnderwater = waterTopY - y;
            double airPressureOnTop = super.getPressure( x, waterTopY );
            return airPressureOnTop + liquidDensity.get() * gravity.get() * distanceUnderwater;
        }
        else {
            return super.getPressure( x, y );
        }
    }

    //Listen for change in the fluid pressure
    @Override public void addPressureChangeObserver( SimpleObserver updatePressure ) {
        super.addPressureChangeObserver( updatePressure );
        waterTower.fluidVolume.addObserver( updatePressure );
        waterTower.tankBottomCenter.addObserver( updatePressure );
    }

    @Override public boolean isInWaterTowerWater( final double x, final double y ) {
        return waterTower.getWaterShape().contains( x, y );
    }
}