// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class WaterTowerModel extends FluidPressureAndFlowModel implements VelocitySensor.Context {

    private WaterTower waterTower = new WaterTower();
    private ArrayList<WaterDrop> waterTowerDrops = new ArrayList<WaterDrop>();
    private ArrayList<WaterDrop> faucetDrops = new ArrayList<WaterDrop>();
    private Random random = new Random();
    private FaucetFlowLevel faucetFlowLevel = new FaucetFlowLevel();
    private double g = 9.8;
    private ArrayList<VoidFunction1<WaterDrop>> dropAddedListeners = new ArrayList<VoidFunction1<WaterDrop>>();

    public WaterTowerModel() {
        addPressureSensor( new PressureSensor( this, 0, 0 ) );
        addVelocitySensor( new VelocitySensor( this, 0, 0 ) );
        getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double remainingVolume = waterTower.fluidVolume.getValue();
                double dropVolume = remainingVolume > 1 ? 1 : remainingVolume;
                double origFluidVolume = waterTower.fluidVolume.getValue();
                if ( waterTower.isHoleOpen() && remainingVolume > 0 ) {
                    double velocity = Math.sqrt( 2 * g * waterTower.getWaterHeight() );//Toricelli's theorem, one of the main learning goals of this tab
                    final WaterDrop drop = new WaterDrop(
                            new ImmutableVector2D( waterTower.getHoleLocation().getX() + random.nextDouble() * 0.1, waterTower.getHoleLocation().getY() + random.nextDouble() * 0.1 ),
                            new ImmutableVector2D( velocity, 0 ),
                            dropVolume );
                    waterTowerDrops.add( drop );
                    for ( int i = 0; i < dropAddedListeners.size(); i++ ) {dropAddedListeners.get( i ).apply( drop );}
                    //TODO: the decrease in volume of the water tower should be proportional to the velocity at the output hole
                    waterTower.setFluidVolume( waterTower.fluidVolume.getValue() - drop.getVolume() );
                }
                double newVolume = waterTower.fluidVolume.getValue();
                //emit drops from faucet that will keep the tank at a constant volume (time averaged)
                if ( faucetFlowLevel.automatic.getValue() ) {
                    double changeInVolume = origFluidVolume - newVolume;
                    WaterDrop faucetDrop = new WaterDrop( new ImmutableVector2D( 4, 20 ), new ImmutableVector2D( 0, 0 ), changeInVolume );
                    faucetDrops.add( faucetDrop );
                    for ( int i = 0; i < dropAddedListeners.size(); i++ ) {
                        dropAddedListeners.get( i ).apply( faucetDrop );
                    }
                }

                for ( WaterDrop drop : waterTowerDrops ) {
                    drop.stepInTime( clockEvent.getSimulationTimeChange() );
                }
                ArrayList<WaterDrop> toRemove = new ArrayList<WaterDrop>();
                for ( WaterDrop drop : faucetDrops ) {
                    drop.stepInTime( clockEvent.getSimulationTimeChange() );
                    if ( drop.position.getValue().getY() < waterTower.getWaterShape().getBounds2D().getMaxY() ) {
                        //absorb the water from the faucet and increase the water tower volume
                        waterTower.setFluidVolume( Math.min( waterTower.fluidVolume.getValue() + drop.getVolume(), WaterTower.tankVolume ) );
                        toRemove.add( drop );
                    }
                }
                faucetDrops.removeAll( toRemove );//TODO: also remove graphics
            }
        } );
    }

    public void addDropAddedListener( VoidFunction1<WaterDrop> dropAddedListener ) {
        dropAddedListeners.add( dropAddedListener );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        return new ImmutableVector2D();
    }

    public void addVelocityUpdateListener( SimpleObserver observer ) {

    }

    public WaterTower getWaterTower() {
        return waterTower;
    }

    public FaucetFlowLevel getFaucetFlowLevel() {
        return faucetFlowLevel;
    }
}
