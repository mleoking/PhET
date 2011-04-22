// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensorContext;

/**
 * Model class for the water tower, which has a water tower that the user can raise/lower and empty/fill.
 *
 * @author Sam Reid
 */
public class WaterTowerModel extends FluidPressureAndFlowModel implements VelocitySensorContext {
    private WaterTower waterTower = new WaterTower();
    private ArrayList<WaterDrop> waterTowerDrops = new ArrayList<WaterDrop>();//drops coming out of the water tower
    private ArrayList<WaterDrop> faucetDrops = new ArrayList<WaterDrop>();//drops coming from the faucet into the water tower
    private Random random = new Random();//source of randomness for creating drops
    private FaucetFlowRate faucetFlowLevel = new FaucetFlowRate();
    private double g = 9.8;
    private ArrayList<VoidFunction1<WaterDrop>> dropAddedListeners = new ArrayList<VoidFunction1<WaterDrop>>();
    private ArrayList<SimpleObserver> velocityUpdateListeners = new ArrayList<SimpleObserver>();

    public WaterTowerModel() {
        //Add the pressure and velocity sensors
        addPressureSensor( new PressureSensor( this, 29, 20.1 ) );
        addPressureSensor( new PressureSensor( this, 29, 26.5 ) );
        addVelocitySensor( new FPAFVelocitySensor( this, 37.7, 30.6 ) );
        addVelocitySensor( new FPAFVelocitySensor( this, 37.7, 33.6 ) );

        //Step when the clock ticks
        getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
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

    private double updateWaterTower() {
        //Compute the velocity of water leaving the water tower at the bottom from Toricelli's theorem
        double velocity = Math.sqrt( 2 * g * waterTower.getWaterLevel() );//Toricelli's theorem, one of the main learning goals of this tab

        //Determine how much fluid should leave, and how much will be left
        double waterVolumeExpelled = velocity / 10;//Since water is incompressible, the volume that can flow out per second is proportional to the expelled velocity
        double remainingVolume = waterTower.fluidVolume.getValue();

        //the decrease in volume of the water tower should be proportional to the velocity at the output hole
        double dropVolume = remainingVolume > waterVolumeExpelled ? waterVolumeExpelled : remainingVolume;
        double origFluidVolume = waterTower.fluidVolume.getValue();

        //Handle any water that should leak out
        if ( waterTower.isHoleOpen() && remainingVolume > 0 ) {
            //Create the water drop
            double radius = Math.pow( dropVolume * 3.0 / 4.0 / Math.PI, 1.0 / 3.0 );
            double waterDropY = waterTower.getHoleLocation().getY() + random.nextGaussian() * 0.04 + radius;
            if ( waterDropY + radius > waterTower.getWaterLevel() + waterTower.getTankShape().getY() ) {
                waterDropY = waterTower.getWaterLevel() + waterTower.getTankShape().getY() - radius +
                             0.1;//shift up a bit, otherwise looks like it's coming off the bottom of the outside of the tank
            }
            final WaterDrop drop = new WaterDrop( new ImmutableVector2D( waterTower.getHoleLocation().getX() + random.nextGaussian() * 0.04, waterDropY ), new ImmutableVector2D( velocity, 0 ), dropVolume );

            //Add the drop to the list and notify listeners
            waterTowerDrops.add( drop );
            for ( VoidFunction1<WaterDrop> dropAddedListener : dropAddedListeners ) {
                dropAddedListener.apply( drop );
            }

            //Decrease the volume of the water tower accordingly
            waterTower.setFluidVolume( waterTower.fluidVolume.getValue() - drop.getVolume() );
        }
        return origFluidVolume;
    }

    //Perform any model updates related to the faucet, creating new drops to fall into the water tower and propagating them
    private void updateFaucet( double dt, double origFluidVolume ) {
        //emit drops from faucet that will keep the tank at a constant volume (time averaged)
        double newVolume = waterTower.fluidVolume.getValue();
        double faucetDropVolume = faucetFlowLevel.automatic.getValue() ?
                                  origFluidVolume - newVolume :
                                  faucetFlowLevel.flow.getValue();
        if ( faucetDropVolume > 0 && !waterTower.isFull() ) {
            //Randomly spread out the water in x and y so it doesn't look so discrete when it falls a long way and separates
            double spreadX = 0.02;
            double spreadY = 0.15;
            double velocitySpreadX = 0.02;
            double velocitySpreadY = 0.02;
            WaterDrop faucetDrop = new WaterDrop( new ImmutableVector2D( -3 + random.nextGaussian() * spreadX,//magic number picked based on graphics
                                                                         WaterTower.MAX_Y + WaterTower.TANK_HEIGHT + 2 + random.nextGaussian() * spreadY ),
                                                  new ImmutableVector2D( random.nextGaussian() * velocitySpreadX, random.nextGaussian() * velocitySpreadY ), faucetDropVolume );
            faucetDrops.add( faucetDrop );
            for ( int i = 0; i < dropAddedListeners.size(); i++ ) {
                dropAddedListeners.get( i ).apply( faucetDrop );
            }
        }

        updateWaterDrops( -2,//graphics looked weird if we had them remove at y=0 since the water drops would flicker
                          waterTowerDrops, dt, new VoidFunction1.Null<WaterDrop>() );
        updateWaterDrops( waterTower.getWaterShape().getBounds2D().getMaxY(), faucetDrops, dt, new VoidFunction1<WaterDrop>() {
            public void apply( WaterDrop drop ) {
                //absorb the water from the faucet and increase the water tower volume
                waterTower.setFluidVolume( Math.min( waterTower.fluidVolume.getValue() + drop.getVolume(), WaterTower.tankVolume ) );
            }
        } );
    }

    private void updateWaterDrops( double thresholdY, ArrayList<WaterDrop> waterDrops, double dt, VoidFunction1<WaterDrop> collision ) {
        ArrayList<WaterDrop> toRemove = new ArrayList<WaterDrop>();
        for ( WaterDrop drop : waterDrops ) {
            drop.stepInTime( dt );
            if ( drop.position.getValue().getY() < thresholdY ) {
                collision.apply( drop );
                toRemove.add( drop );
            }
        }
        removeDrops( waterDrops, toRemove );
    }

    private void removeDrops( ArrayList<WaterDrop> waterDrops, ArrayList<WaterDrop> toRemove ) {
        for ( WaterDrop waterDrop : toRemove ) {
            waterDrop.notifyRemoved();
        }
        waterDrops.removeAll( toRemove );
    }

    public void addDropAddedListener( VoidFunction1<WaterDrop> dropAddedListener ) {
        dropAddedListeners.add( dropAddedListener );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        for ( WaterDrop waterTowerDrop : waterTowerDrops ) {
            if ( waterTowerDrop.contains( x, y ) ) {
                return waterTowerDrop.velocity.getValue();
            }
        }
        for ( WaterDrop waterTowerDrop : faucetDrops ) {
            if ( waterTowerDrop.contains( x, y ) ) {
                return waterTowerDrop.velocity.getValue();
            }
        }
        return new ImmutableVector2D();
    }

    public void addVelocityUpdateListener( SimpleObserver observer ) {
        velocityUpdateListeners.add( observer );
    }

    public WaterTower getWaterTower() {
        return waterTower;
    }

    public FaucetFlowRate getFaucetFlowLevel() {
        return faucetFlowLevel;
    }

    @Override
    public void reset() {
        super.reset();
        waterTower.reset();
        faucetFlowLevel.reset();
        g = 9.8;
        removeDrops( faucetDrops, faucetDrops );
        removeDrops( waterTowerDrops, waterTowerDrops );
    }

    @Override
    public double getPressure( double x, double y ) {
        if ( getWaterTower().getWaterShape().contains( x, y ) ) {
            final double waterTopY = getWaterTower().getWaterShape().getBounds2D().getMaxY();
            double distanceUnderwater = waterTopY - y;
            double airPressureOnTop = super.getPressure( x, waterTopY );
            return airPressureOnTop + liquidDensity.getValue() * gravity.getValue() * distanceUnderwater;
        }
        else {
            return super.getPressure( x, y );
        }
    }

    @Override
    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        super.addFluidChangeObserver( updatePressure );
        getWaterTower().fluidVolume.addObserver( updatePressure );
        getWaterTower().tankBottomCenter.addObserver( updatePressure );
    }
}
