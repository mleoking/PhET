// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;

/**
 * @author Sam Reid
 */
public abstract class SugarAndSaltSolutionModel {
    public final double width = 1.04;//visible width in meters
    public final double height = 0.7;//visible height in meters

    //Center the beaker's base at x=0 and have it go halfway up the screen
    public final double beakerWidth = width * 0.6;
    public final double beakerX = -beakerWidth / 2;
    public final double beakerHeight = height * 0.5;

    public final Beaker beaker = new Beaker( beakerX, 0, beakerWidth, beakerHeight );//The beaker into which you can add water, salt and sugar.
    public final Water water = new Water( beaker );
    public final Property<Double> inputFlowRate = new Property<Double>( 0.0 );//rate that water flows into the beaker in m^3/s
    public final Property<Double> outputFlowRate = new Property<Double>( 0.0 );//rate that water flows out of the beaker in m^3/s
    public final ConstantDtClock clock;

    //Sugar and its listeners
    public final ArrayList<Sugar> sugarList = new ArrayList<Sugar>();//The sugar crystals that haven't been dissolved
    public final Notifier<Sugar> sugarAdded = new Notifier<Sugar>();//Listeners for when sugar crystals are added

    //Salt and its listeners
    public final ArrayList<Salt> saltList = new ArrayList<Salt>();//The salt crystals that haven't been dissolved
    public final Notifier<Salt> saltAdded = new Notifier<Salt>();//Listeners for when salt crystals are added

    private ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );//Force due to gravity near the surface of the earth

    private static final double FLOW_SCALE = 0.02;//Flow controls vary between 0 and 1, this scales it down to a good model value
    public final Property<DispenserType> dispenserType = new Property<DispenserType>( SALT );//Which dispenser the user has selected

    //Listeners which are notified when the sim is reset.
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    //Convenience composite properties for determining whether the beaker is full or empty so we can shut off the faucets when necessary
    public final ObservableProperty<Boolean> beakerFull = water.volume.valueEquals( beaker.getMaxFluidVolume() );
    public final ObservableProperty<Boolean> beakerEmpty = water.volume.valueEquals( 0.0 );

    public final ConductivityTester conductivityTester = new ConductivityTester();

    //Model for the sugar dispenser
    public final SugarDispenser sugarDispenser = new SugarDispenser() {{
        //Wire up the SugarDispenser so it is enabled when the model has the SUGAR type dispenser selected
        dispenserType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == SUGAR );
            }
        } );
    }};

    public SaltShaker saltShaker = new SaltShaker() {{
        //Wire up the SugarDispenser so it is enabled when the model has the SALT type dispenser selected
        dispenserType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == SALT );
            }
        } );
    }};

    public SugarAndSaltSolutionModel() {
        clock = new ConstantDtClock( 30 );

        //Wire up to the clock so we can update when it ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateModel( clockEvent.getSimulationTimeChange() );
            }
        } );

        //Update the conductivity tester when the water level changes, since it might move up to touch a probe (or move out from underneath a submerged probe)
        water.volume.addObserver( new SimpleObserver() {
            public void update() {
                updateConductivityTesterBrightness();
            }
        } );

        //When the conductivity tester probe locations change, also update the conductivity tester brightness since they may come into contact (or leave contact) with the fluid
        conductivityTester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {
            public void brightnessChanged() {
            }

            public void positiveProbeLocationChanged() {
                updateConductivityTesterBrightness();
            }

            public void negativeProbeLocationChanged() {
                updateConductivityTesterBrightness();
            }
        } );
    }

    //Update the conductivity tester brightness when the probes come into contact with (or stop contacting) the fluid
    protected void updateConductivityTesterBrightness() {

        //Check for a collision with the probes
        Rectangle2D waterBounds = water.getShape().getBounds2D();
        boolean bothProbesSubmerged = waterBounds.contains( conductivityTester.getPositiveProbeLocationReference() ) &&
                                      waterBounds.contains( conductivityTester.getNegativeProbeLocationReference() );

        //Set the brightness to be a linear function of the salt concentration (but keeping it bounded between 0 and 1 which are the limits of the conductivity tester brightness
        conductivityTester.brightness.set( bothProbesSubmerged ? MathUtil.clamp( 0, getSaltConcentration() * 1E3, 1 ) : 0.0 );
    }

    public abstract double getSaltConcentration();

    //Adds the specified Sugar crystal to the model
    public void addSugar( final Sugar sugar ) {
        sugarList.add( sugar );
        sugarAdded.updateListeners( sugar );
    }

    //Adds the specified salt crystal to the model
    public void addSalt( Salt salt ) {
        this.saltList.add( salt );
        saltAdded.updateListeners( salt );
    }

    //Update the model when the clock ticks
    private void updateModel( double dt ) {
        //Add any new crystals from the salt & sugar dispensers
        sugarDispenser.updateModel( this );
        saltShaker.updateModel( this );

        //Change the water volume based on input and output flow
        double inVolume = dt * inputFlowRate.get() * FLOW_SCALE;
        double outVolume = dt * outputFlowRate.get() * FLOW_SCALE;

        //Compute the new water volume, but making sure it doesn't overflow or underflow
        double newVolume = water.volume.get() + inVolume - outVolume;
        if ( newVolume > beaker.getMaxFluidVolume() ) {
            inVolume = beaker.getMaxFluidVolume() + outVolume - water.volume.get();
        }
        else if ( newVolume < 0 ) {
            outVolume = inVolume + water.volume.get();
        }

        //Set the true value of the new volume based on clamped inputs and outputs
        newVolume = water.volume.get() + inVolume - outVolume;

        //Turn off the input flow if the beaker would overflow
        if ( newVolume >= beaker.getMaxFluidVolume() ) {
            inputFlowRate.set( 0.0 );
            //TODO: make the cursor drop the slider?
        }
        //Turn off the output flow if the beaker is empty
        if ( newVolume <= 0 ) {
            outputFlowRate.set( 0.0 );
        }

        //Update the water volume
        water.volume.set( newVolume );
        waterExited( outVolume );

        //Move about the sugar and salt crystals
        updateCrystals( dt, saltList );
        updateCrystals( dt, sugarList );
    }

    //Called when water (with dissolved solutes) flows out of the beaker, so that subclasses can update concentrations if necessary.
    protected void waterExited( double outVolume ) {
    }

    //Propagate the sugar and salt crystals, and absorb them if they hit the water
    private void updateCrystals( double dt, final ArrayList<? extends Crystal> crystalList ) {
        ArrayList<Crystal> hitTheWater = new ArrayList<Crystal>();
        for ( Crystal crystal : crystalList ) {
            //slow the motion down a little bit or it moves too fast
            crystal.stepInTime( gravity.times( crystal.mass ), dt / 10 );

            //If the salt hits the water, absorb it
            if ( water.getShape().getBounds2D().contains( crystal.position.get().toPoint2D() ) ) {
                hitTheWater.add( crystal );
            }
        }
        //Remove the salt crystals that hit the water
        removeCrystals( crystalList, hitTheWater );

        //increase concentration in the water for crystals that hit
        for ( Crystal crystal : hitTheWater ) {
            crystalAbsorbed( crystal );
        }
    }

    //Remove the specified crystals.  Note that the toRemove
    private void removeCrystals( ArrayList<? extends Crystal> crystalList, ArrayList<? extends Crystal> toRemove ) {
        for ( Crystal crystal : new ArrayList<Crystal>( toRemove ) ) {
            crystal.remove();
            crystalList.remove( crystal );
        }
    }

    //Called when a crystal is absorbed by the water.
    // For instance, in the first tab it computes the resulting concentration change
    protected void crystalAbsorbed( Crystal crystal ) {
        removeSaltAndSugar();
    }

    //Called when the user presses a button to clear the solutes, removes all solutes from the sim
    public void removeSaltAndSugar() {
        removeCrystals( sugarList, sugarList );
        removeCrystals( saltList, saltList );
    }

    public void reset() {
        //Reset the model state
        removeSaltAndSugar();
        water.reset();
        inputFlowRate.reset();
        outputFlowRate.reset();
        sugarDispenser.reset();
        conductivityTester.reset();
        dispenserType.reset();

        //Notify listeners that registered for a reset message
        for ( VoidFunction0 resetListener : resetListeners ) {
            resetListener.apply();
        }
    }

    //Adds a listener that will be notified when the model is reset
    public void addResetListener( VoidFunction0 listener ) {
        resetListeners.add( listener );
    }
}