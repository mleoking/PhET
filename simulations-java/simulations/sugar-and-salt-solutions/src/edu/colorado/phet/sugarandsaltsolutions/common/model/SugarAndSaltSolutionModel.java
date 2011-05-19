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
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.sugarandsaltsolutions.common.view.VerticalRangeContains;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.SoluteModel;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.canvasSize;

/**
 * Base class model for Sugar and Salt Solutions, which keeps track of the physical model as well as the MVC model for view components (such as whether certain components are enabled).
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionModel implements ResetModel {
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

    //Set a max amount of water that the user can add to the system so they can't overflow it
    private final double MAX_WATER = beaker.getMaxFluidVolume() * 0.75;

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

    //Force due to gravity near the surface of the earth
    private final ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );

    //Flow controls vary between 0 and 1, this scales it down to a good model value
    private static final double FLOW_SCALE = 0.0005;

    //Which dispenser the user has selected
    public final Property<DispenserType> dispenserType = new Property<DispenserType>( SALT );

    //Listeners which are notified when the sim is reset.
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    //Model for the conductivity tester
    public final ConductivityTester conductivityTester = new ConductivityTester();

    //True if the values should be shown in the user interface
    public final Property<Boolean> showConcentrationValues = new Property<Boolean>( false );

    //Saturation points for salt and sugar assume 25 degrees C
    private static final double saltSaturationPoint = 6.14 * 1000;//6.14 moles per liter, converted to SI
    private static final double sugarSaturationPoint = 5.85 * 1000;//5.85 moles per liter, converted to SI

    //volume in SI (m^3).  Start at 1 L (halfway up the 2L beaker).  Note that 0.001 cubic meters = 1L
    public final DoubleProperty waterVolume = new DoubleProperty( 0.001 );

    //Model moles, concentration, amount dissolved, amount precipitated, etc. for salt and sugar
    public final SoluteModel salt = new SoluteModel( waterVolume, saltSaturationPoint, 0.02699 / 1000.0, MacroSalt.molarMass );
    public final SoluteModel sugar = new SoluteModel( waterVolume, sugarSaturationPoint, 0.2157 / 1000.0, MacroSugar.molarMass );

    //Total volume of the water plus any solid precipitate submerged under the water (and hence pushing it up)
    public final CompositeDoubleProperty solidVolume = salt.solidVolume.plus( sugar.solidVolume );

    //The y value where the solution will sit, it moves up and down with any solid that has precipitated
    public final CompositeDoubleProperty solutionY = new CompositeDoubleProperty( new Function0<Double>() {
        public Double apply() {
            return beaker.getHeightForVolume( solidVolume.get() );
        }
    }, solidVolume );

    //Rule of thumb for how much volume is occupied by a dissolved solute
    private double litersPerMoleDissolvedSalt = salt.volumePerSolidMole * 0.15;
    private double litersPerMoleDissolvedSugar = sugar.volumePerSolidMole * 0.15;

    //Create the solution, which sits atop the solid precipitate (if any)
    public final Solution solution = new Solution( waterVolume, beaker, solutionY, salt.molesDissolved.times( litersPerMoleDissolvedSalt ), sugar.molesDissolved.times( litersPerMoleDissolvedSugar ) );

    //The concentration in the liquid in moles / m^3
    //These have to be defined here instead of in SoluteModel because they depend on the total volume of the solution (which in turn depends on the amount of solute dissolved in the solvent).
    public final CompositeDoubleProperty saltConcentration = salt.molesDissolved.dividedBy( solution.volume );
    public final CompositeDoubleProperty sugarConcentration = sugar.molesDissolved.dividedBy( solution.volume );

    //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    public final ObservableProperty<Boolean> anySolutes = salt.moles.greaterThan( 0 ).or( sugar.moles.greaterThan( 0 ) );
    public final Property<Boolean> showConcentrationBarChart = new Property<Boolean>( true );

    //Keep track of how many moles of crystal are in the air, since we need to prevent user from adding more than 10 moles to the system
    //This shuts off salt/sugar when there is salt/sugar in the air that could get added to the solution
    private final CompositeDoubleProperty airborneSaltGrams = new AirborneCrystalMoles( saltList ).times( salt.gramsPerMole );
    private final CompositeDoubleProperty airborneSugarGrams = new AirborneCrystalMoles( sugarList ).times( sugar.gramsPerMole );

    //Properties to indicate if the user is allowed to add more of the solute.  If not allowed the dispenser is shown as empty.
    private ObservableProperty<Boolean> moreSaltAllowed = salt.grams.plus( airborneSaltGrams ).lessThan( 500 );
    private ObservableProperty<Boolean> moreSugarAllowed = sugar.grams.plus( airborneSugarGrams ).lessThan( 500 );

    //Convenience composite properties for determining whether the beaker is full or empty so we can shut off the faucets when necessary
    public final ObservableProperty<Boolean> beakerFull = solution.volume.greaterThanOrEqualTo( MAX_WATER );

    //Determine if the lower faucet is allowed to let fluid flow out.  It can if any part of the fluid overlaps any part of the pipe range.
    //These values were sampled from the model with debug mode.
    //This logic is used in the model update step to determine if water can flow out, as well as in the user interface to determine if the user can turn on the output faucet
    public final VerticalRangeContains lowerFaucetCanDrain = new VerticalRangeContains( solution.shape, 0.011746031746031754, 0.026349206349206344 );

    //When a crystal is absorbed by the water, increase the number of moles in solution
    protected void crystalAbsorbed( MacroCrystal crystal ) {
        if ( crystal instanceof MacroSalt ) {
            salt.moles.set( salt.moles.get() + crystal.getMoles() );
        }
        else if ( crystal instanceof MacroSugar ) {
            sugar.moles.set( sugar.moles.get() + crystal.getMoles() );
        }
    }

    //Called when the user presses a button to clear the solutes, removes all solutes from the sim
    public void removeSaltAndSugar() {
        removeCrystals( sugarList, sugarList );
        removeCrystals( saltList, saltList );
        salt.moles.set( 0.0 );
        sugar.moles.set( 0.0 );
    }

    //Called when water (with dissolved solutes) flows out of the beaker, so that subclasses can update concentrations if necessary.
    //Have some moles of salt and sugar flow out so that the concentration remains unchanged
    protected void waterDrained( double outVolume, double initialSaltConcentration, double initialSugarConcentration ) {

        //Make sure to keep the concentration the same when water flowing out.  Use the values recorded before the model stepped to ensure conservation of solute moles
        updateConcentration( outVolume, initialSaltConcentration, salt.moles );
        updateConcentration( outVolume, initialSugarConcentration, sugar.moles );
    }

    //Make sure to keep the concentration the same when water flowing out
    private void updateConcentration( double outVolume, double concentration, SettableProperty<Double> moles ) {
        double molesOfSoluteLeaving = concentration * outVolume;
        moles.set( moles.get() - molesOfSoluteLeaving );
    }

    //Model for the salt shaker
    public SaltShaker saltShaker = new SaltShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSaltAllowed ) {{
        //Wire up the SugarDispenser so it is enabled when the model has the SALT type dispenser selected
        dispenserType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == SALT );
            }
        } );
    }};

    //Model for the sugar dispenser
    public final SugarDispenser sugarDispenser = new SugarDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed ) {{
        //Wire up the SugarDispenser so it is enabled when the model has the SUGAR type dispenser selected
        dispenserType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == SUGAR );
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
        new RichSimpleObserver() {
            @Override public void update() {
                updateConductivityTesterBrightness();
            }
        }.observe( saltConcentration, solution.shape );

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
                //Have to callback here too since the battery or bulb could get sumberged and short the circuit
                updateConductivityTesterBrightness();
            }
        } );
    }

    //Update the conductivity tester brightness when the probes come into contact with (or stop contacting) the fluid
    protected void updateConductivityTesterBrightness() {

        //Check for a collision with the probe, using the full region of each probe (so if any part intersects, there is still an electrical connection).
        Rectangle2D waterBounds = solution.shape.get().getBounds2D();
        boolean bothProbesTouching = waterBounds.intersects( conductivityTester.getPositiveProbeRegion().toRectangle2D() ) &&
                                     waterBounds.intersects( conductivityTester.getNegativeProbeRegion().toRectangle2D() );

        //Check to see if the circuit is shorted out (if light bulb or battery is submerged).
        //Null checks are necessary since those regions are computed from view components and may not have been computed yet (but will be non-null if the user dragged out the conductivity tester from the toolbox)
        boolean batterySubmerged = conductivityTester.getBatteryRegion() != null && waterBounds.intersects( conductivityTester.getBatteryRegion().getBounds2D() );
        boolean bulbSubmerged = conductivityTester.getBulbRegion() != null && waterBounds.intersects( conductivityTester.getBulbRegion().getBounds2D() );

        //Set the brightness to be a linear function of the salt concentration (but keeping it bounded between 0 and 1 which are the limits of the conductivity tester brightness
        //Use a scale factor that matches up with the limits on saturation (manually sampled at runtime)
        conductivityTester.brightness.set( bothProbesTouching && !batterySubmerged && !bulbSubmerged ? MathUtil.clamp( 0, saltConcentration.get() * 1.62E-4, 1 ) : 0.0 );
    }

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
    protected void updateModel( double dt ) {

        //Have to record the concentrations before the model updates since the concentrations change if water is added or removed.
        double initialSaltConcentration = saltConcentration.get();
        double initialSugarConcentration = sugarConcentration.get();

        //Add any new crystals from the salt & sugar dispensers
        sugarDispenser.updateModel( this );
        saltShaker.updateModel( this );

        //Change the water volume based on input and output flow
        double inputWater = dt * inputFlowRate.get() * FLOW_SCALE;
        double drainedWater = dt * outputFlowRate.get() * FLOW_SCALE;
        double evaporatedWater = dt * evaporationRate.get() * EVAPORATION_SCALE;

        //Compute the new water volume, but making sure it doesn't overflow or underflow
        //TODO: use the solution volume here?
        double newVolume = waterVolume.get() + inputWater - drainedWater - evaporatedWater;
        if ( newVolume > MAX_WATER ) {
            inputWater = MAX_WATER + drainedWater + evaporatedWater - waterVolume.get();
        }
        //Only allow drain to use up all the water if user is draining the liquid
        else if ( newVolume < 0 && outputFlowRate.get() > 0 ) {
            drainedWater = inputWater + waterVolume.get();
        }
        //Conversely, only allow evaporated water to use up all remaining water if the user is evaporating anything
        else if ( newVolume < 0 && evaporationRate.get() > 0 ) {
            evaporatedWater = inputWater + waterVolume.get();
        }
        //Note that the user can't be both evaporating and draining fluid at the same time, since the controls are one-at-a-time controls.
        //This simplifies the logic here.

        //Set the true value of the new volume based on clamped inputs and outputs
        newVolume = waterVolume.get() + inputWater - drainedWater - evaporatedWater;

        //Turn off the input flow if the beaker would overflow
        if ( newVolume >= MAX_WATER ) {
            inputFlowRate.set( 0.0 );
            //TODO: make the cursor drop the slider?
        }

        //Turn off the output flow if no water is adjacent to it
        if ( !lowerFaucetCanDrain.get() ) {
            outputFlowRate.set( 0.0 );
        }

        //Turn off evaporation if beaker is empty of water
        if ( newVolume <= 0 ) {
            evaporationRate.set( 0 );
            //TODO: make the cursor drop the slider?
        }

        //Update the water volume
        waterVolume.set( newVolume );

        //Notify listeners that some water (with solutes) exited the system, so they can decrease the amounts of solute (mols, not molarity) in the system
        //Only call when draining, would have the wrong behavior for evaporation
        if ( drainedWater > 0 ) {
            waterDrained( drainedWater, initialSaltConcentration, initialSugarConcentration );
        }

        //Move about the sugar and salt crystals, and maybe absorb them
        updateCrystals( dt, saltList );
        updateCrystals( dt, sugarList );
    }

    //Propagate the sugar and salt crystals, and absorb them if they hit the water
    private void updateCrystals( double dt, final ArrayList<? extends MacroCrystal> crystalList ) {
        ArrayList<MacroCrystal> hitTheWater = new ArrayList<MacroCrystal>();
        for ( MacroCrystal crystal : crystalList ) {
            //Store the initial location so we can use the (final - start) line to check for collision with water, so it can't jump over the water rectangle
            ImmutableVector2D initialLocation = crystal.position.get();

            //slow the motion down a little bit or it moves too fast//TODO: can this be fixed?
            crystal.stepInTime( gravity.times( crystal.mass ), dt / 10, beaker.getLeftWall(), beaker.getRightWall(), beaker.getFloor(),
                                new Line2D.Double( beaker.getFloor().getX1(), solutionY.get(), beaker.getFloor().getX2(), solutionY.get() ) );

            //If the salt hits the water during any point of its initial -> final trajectory, absorb it.
            //This is necessary because if the water layer is too thin, the crystal could have jumped over it completely
            if ( new Line2D.Double( initialLocation.toPoint2D(), crystal.position.get().toPoint2D() ).intersects( solution.shape.get().getBounds2D() ) ) {
                hitTheWater.add( crystal );
            }
        }
        //Remove the salt crystals that hit the water
        removeCrystals( crystalList, hitTheWater );

        //increase concentration in the water for crystals that hit
        for ( MacroCrystal crystal : hitTheWater ) {
            crystalAbsorbed( crystal );
        }

        //Update the properties representing how many crystals are in the air, to make sure we stop pouring out crystals if we have reached the limit
        // (even if poured out crystals didn't get dissolved yet)
        airborneSaltGrams.notifyIfChanged();
        airborneSugarGrams.notifyIfChanged();
    }

    //Remove the specified crystals.  Note that the toRemove
    private void removeCrystals( ArrayList<? extends MacroCrystal> crystalList, ArrayList<? extends MacroCrystal> toRemove ) {
        for ( MacroCrystal crystal : new ArrayList<MacroCrystal>( toRemove ) ) {
            crystal.remove();
            crystalList.remove( crystal );
        }
    }

    public void reset() {
        //Reset the model state
        removeSaltAndSugar();
        waterVolume.reset();
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