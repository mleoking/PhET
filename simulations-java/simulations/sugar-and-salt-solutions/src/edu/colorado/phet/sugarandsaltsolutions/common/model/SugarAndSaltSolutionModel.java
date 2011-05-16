// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSugar;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.canvasSize;

/**
 * @author Sam Reid
 */
public abstract class SugarAndSaltSolutionModel implements ResetModel {
    //Beaker dimensions and location in meters, public so other classes can use them for layout
    public static final double BEAKER_WIDTH = 0.2;
    public static final double BEAKER_X = -BEAKER_WIDTH / 2;
    public static final double BEAKER_HEIGHT = 0.1;
    public static final double BEAKER_DEPTH = 0.1;//Depth is z-direction z-depth

    //Use the same aspect ratio as the view to minimize insets with blank regions
    private final double aspectRatio = canvasSize.getWidth() / canvasSize.getHeight();

    //Inset so the beaker doesn't touch the edge of the model bounds
    public final double inset = BEAKER_WIDTH * 0.1;
    public final double modelWidth = BEAKER_WIDTH + inset * 2;

    //Visible model region: a bit bigger than the beaker, used to set the stage aspect ratio in the canvas
    public final ImmutableRectangle2D visibleRegion = new ImmutableRectangle2D( -modelWidth / 2, -inset, modelWidth, modelWidth / aspectRatio );

    //Beaker and water models
    public final Beaker beaker = new Beaker( BEAKER_X, 0, BEAKER_WIDTH, BEAKER_HEIGHT, BEAKER_DEPTH );//The beaker into which you can add water, salt and sugar.
    public final Water water = new Water( beaker );

    //Model for input and output flows
    public final Property<Double> inputFlowRate = new Property<Double>( 0.0 );//rate that water flows into the beaker in m^3/s
    public final Property<Double> outputFlowRate = new Property<Double>( 0.0 );//rate that water flows out of the beaker in m^3/s

    //Model clock
    public final ConstantDtClock clock;

    //Sugar and its listeners
    public final ArrayList<MacroSugar> sugarList = new ArrayList<MacroSugar>();//The sugar crystals that haven't been dissolved
    public final Notifier<MacroSugar> sugarAdded = new Notifier<MacroSugar>();//Listeners for when sugar crystals are added

    //Salt and its listeners
    public final ArrayList<MacroSalt> saltList = new ArrayList<MacroSalt>();//The salt crystals that haven't been dissolved
    public final Notifier<MacroSalt> saltAdded = new Notifier<MacroSalt>();//Listeners for when salt crystals are added

    private final ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );//Force due to gravity near the surface of the earth

    private static final double FLOW_SCALE = 0.0005;//Flow controls vary between 0 and 1, this scales it down to a good model value
    public final Property<DispenserType> dispenserType = new Property<DispenserType>( SALT );//Which dispenser the user has selected

    //Listeners which are notified when the sim is reset.
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    //Convenience composite properties for determining whether the beaker is full or empty so we can shut off the faucets when necessary
    public final ObservableProperty<Boolean> beakerFull = water.volume.valueEquals( beaker.getMaxFluidVolume() );

    public final ConductivityTester conductivityTester = new ConductivityTester();

    public final Property<Boolean> showConcentrationValues = new Property<Boolean>( false );//True if the values should be shown

    //Model for the sugar dispenser
    public final SugarDispenser sugarDispenser = new SugarDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker ) {{
        //Wire up the SugarDispenser so it is enabled when the model has the SUGAR type dispenser selected
        dispenserType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == SUGAR );
            }
        } );
    }};

    //Model for the salt shaker
    public SaltShaker saltShaker = new SaltShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker ) {{
        //Wire up the SugarDispenser so it is enabled when the model has the SALT type dispenser selected
        dispenserType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == SALT );
            }
        } );
    }};

    //Rate at which liquid (but no solutes) leaves the model
    public final SettableProperty<Integer> evaporationRate = new Property<Integer>( 0 );//Between 0 and 100
    private static final double EVAPORATION_SCALE = FLOW_SCALE / 100.0;//Scaled down by 100 since the evaporation rate is 100 times bigger than flow scales

    //Make it so that when the water level is below the drain faucet, then no more water can flow through that pipe.
    //This value was hand-tuned based on the graphical location of the lower part of the pipe in the view
    //If the view changes, this value will need to change
    public static final double MIN_DRAIN_VOLUME = 0.00025;

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

            public void locationChanged() {
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
        conductivityTester.brightness.set( bothProbesSubmerged ? MathUtil.clamp( 0, getSaltConcentration() * 2 * 1E-4, 1 ) : 0.0 );
    }

    public abstract double getSaltConcentration();

    //Adds the specified Sugar crystal to the model
    public void addMacroSugar( final MacroSugar sugar ) {
        sugarList.add( sugar );
        sugarAdded.updateListeners( sugar );
    }

    //Adds the specified salt crystal to the model
    public void addMacroSalt( MacroSalt salt ) {
        this.saltList.add( salt );
        saltAdded.updateListeners( salt );
    }

    //Update the model when the clock ticks
    private void updateModel( double dt ) {
        //Add any new crystals from the salt & sugar dispensers
        sugarDispenser.updateModel( this );
        saltShaker.updateModel( this );

        //Change the water volume based on input and output flow
        double inputWater = dt * inputFlowRate.get() * FLOW_SCALE;
        double drainedWater = dt * outputFlowRate.get() * FLOW_SCALE;
        double evaporatedWater = dt * evaporationRate.get() * EVAPORATION_SCALE;

        //Compute the new water volume, but making sure it doesn't overflow or underflow
        double newVolume = water.volume.get() + inputWater - drainedWater - evaporatedWater;
        if ( newVolume > beaker.getMaxFluidVolume() ) {
            inputWater = beaker.getMaxFluidVolume() + drainedWater + evaporatedWater - water.volume.get();
        }
        //Only allow drain to use up all the water if user is draining the liquid
        else if ( newVolume < 0 && outputFlowRate.get() > 0 ) {
            drainedWater = inputWater + water.volume.get();
        }
        //Conversely, only allow evaporated water to use up all remaining water if the user is evaporating anything
        else if ( newVolume < 0 && evaporationRate.get() > 0 ) {
            evaporatedWater = inputWater + water.volume.get();
        }
        //Note that the user can't be both evaporating and draining fluid at the same time, since the controls are one-at-a-time controls.
        //This simplifies the logic here.

        //Set the true value of the new volume based on clamped inputs and outputs
        newVolume = water.volume.get() + inputWater - drainedWater - evaporatedWater;

        //Turn off the input flow if the beaker would overflow
        if ( newVolume >= beaker.getMaxFluidVolume() ) {
            inputFlowRate.set( 0.0 );
            //TODO: make the cursor drop the slider?
        }
        //Turn off the output flow if the beaker is empty
        if ( newVolume <= MIN_DRAIN_VOLUME ) {
            outputFlowRate.set( 0.0 );
            //TODO: make the cursor drop the slider?
        }

        //Turn off evaporation if beaker is empty of water
        if ( newVolume <= 0 ) {
            evaporationRate.set( 0 );
            //TODO: make the cursor drop the slider?
        }

        //Update the water volume
        water.volume.set( newVolume );

        //Notify listeners that some water (with solutes) exited the system, so they can decrease the amounts of solute (mols, not molarity) in the system
        waterDrained( drainedWater );

        //Move about the sugar and salt crystals
        updateCrystals( dt, saltList );
        updateCrystals( dt, sugarList );
    }

    //Called when water (with dissolved solutes) flows out of the beaker, so that subclasses can update concentrations if necessary.
    protected void waterDrained( double outVolume ) {
    }

    //Propagate the sugar and salt crystals, and absorb them if they hit the water
    private void updateCrystals( double dt, final ArrayList<? extends MacroCrystal> crystalList ) {
        ArrayList<MacroCrystal> hitTheWater = new ArrayList<MacroCrystal>();
        for ( MacroCrystal crystal : crystalList ) {
            //Store the initial location so we can use the (final - start) line to check for collision with water, so it can't jump over the water rectangle
            ImmutableVector2D initialLocation = crystal.position.get();

            //slow the motion down a little bit or it moves too fast
            crystal.stepInTime( gravity.times( crystal.mass ), dt / 10 );

            //If the salt hits the water during any point of its initial -> final trajectory, absorb it.
            //This is necessary because if the water layer is too thin, the crystal could have jumped over it completely
            if ( new Line2D.Double( initialLocation.toPoint2D(), crystal.position.get().toPoint2D() ).intersects( water.getShape().getBounds2D() ) ) {
                hitTheWater.add( crystal );
            }
        }
        //Remove the salt crystals that hit the water
        removeCrystals( crystalList, hitTheWater );

        //increase concentration in the water for crystals that hit
        for ( MacroCrystal crystal : hitTheWater ) {
            crystalAbsorbed( crystal );
        }
    }

    //Remove the specified crystals.  Note that the toRemove
    private void removeCrystals( ArrayList<? extends MacroCrystal> crystalList, ArrayList<? extends MacroCrystal> toRemove ) {
        for ( MacroCrystal crystal : new ArrayList<MacroCrystal>( toRemove ) ) {
            crystal.remove();
            crystalList.remove( crystal );
        }
    }

    //Called when a crystal is absorbed by the water.
    // For instance, in the first tab it computes the resulting concentration change
    protected void crystalAbsorbed( MacroCrystal crystal ) {
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
        saltShaker.reset();
        conductivityTester.reset();
        dispenserType.reset();
        showConcentrationValues.reset();

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