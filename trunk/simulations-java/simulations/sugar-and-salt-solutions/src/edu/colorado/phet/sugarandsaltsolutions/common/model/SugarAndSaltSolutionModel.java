// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.common.view.VerticalRangeContains;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroCrystal;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.SoluteModel;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.canvasSize;

/**
 * Base class model for Sugar and Salt Solutions, which keeps track of the physical model as well as the MVC model for view components (such as whether certain components are enabled).
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionModel extends AbstractSugarAndSaltSolutionsModel implements ISugarAndSaltModel {
    //Use the same aspect ratio as the view to minimize insets with blank regions
    private final double aspectRatio = canvasSize.getWidth() / canvasSize.getHeight();

    //Model for input and output flows
    public final Property<Double> inputFlowRate = new Property<Double>( 0.0 );//rate that water flows into the beaker in m^3/s
    public final Property<Double> outputFlowRate = new Property<Double>( 0.0 );//rate that water flows out of the beaker in m^3/s

    //Sugar and its listeners
    public final ArrayList<MacroSugar> sugarList = new ArrayList<MacroSugar>();//The sugar crystals that haven't been dissolved
    public final Notifier<MacroSugar> sugarAdded = new Notifier<MacroSugar>();//Listeners for when sugar crystals are added

    //Salt and its listeners
    public final ArrayList<MacroSalt> saltList = new ArrayList<MacroSalt>();//The salt crystals that haven't been dissolved
    public final Notifier<MacroSalt> saltAdded = new Notifier<MacroSalt>();//Listeners for when salt crystals are added

    //Force due to gravity near the surface of the earth
    private final ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );

    //Flow controls vary between 0 and 1, this scales it down to a good model value
    private final double faucetFlowRate;
    public final double distanceScale;

    //Which dispenser the user has selected
    public final Property<DispenserType> dispenserType = new Property<DispenserType>( SALT );

    //True if the values should be shown in the user interface
    public final Property<Boolean> showConcentrationValues = new Property<Boolean>( false );

    //Saturation points for salt and sugar assume 25 degrees C
    private static final double saltSaturationPoint = 6.14 * 1000;//6.14 moles per liter, converted to SI
    private static final double sugarSaturationPoint = 5.85 * 1000;//5.85 moles per liter, converted to SI

    //volume in SI (m^3).  Start at 1 L (halfway up the 2L beaker).  Note that 0.001 cubic meters = 1L
    public final DoubleProperty waterVolume;

    //Model moles, concentration, amount dissolved, amount precipitated, etc. for salt and sugar
    public final SoluteModel salt;
    public final SoluteModel sugar;

    //Total volume of the water plus any solid precipitate submerged under the water (and hence pushing it up)
    public final CompositeDoubleProperty solidVolume;

    //The y value where the solution will sit, it moves up and down with any solid that has precipitated
    public final CompositeDoubleProperty solutionY;

    //Beaker model
    public final Beaker beaker;

    //Solution model, the fluid + any dissolved solutes
    public final Solution solution;

    //The concentration in the liquid in moles / m^3
    public final CompositeDoubleProperty saltConcentration;
    public final CompositeDoubleProperty sugarConcentration;

    //Max amount of water before the beaker overflows
    private double maxWater;

    //Flag to indicate whether there is enough solution to flow through the lower drain.
    public final VerticalRangeContains lowerFaucetCanDrain;

    //Amounts of sugar and salt in crystal form falling from the dispenser
    private CompositeDoubleProperty airborneSaltGrams;
    private CompositeDoubleProperty airborneSugarGrams;

    //User setting: whether the concentration bar chart should be shown
    public final Property<Boolean> showConcentrationBarChart;

    //Part of the model that must be visible within the view
    public final ImmutableRectangle2D visibleRegion;

    //Observable flag which determines whether the beaker is full of solution, for purposes of preventing overflow
    public final ObservableProperty<Boolean> beakerFull;

    //True if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    public final ObservableProperty<Boolean> anySolutes;

    //Models for dispensers that can be used to add solute to the beaker solution
    public final ArrayList<Dispenser> dispensers;
    public final ObservableProperty<Boolean> moreSaltAllowed;
    public final ObservableProperty<Boolean> moreSugarAllowed;

    //Method to give the name displayed on the side of the salt shaker, necessary since it says e.g. "salt" in macro tab and "sodium chloride" in micro tab
    protected String getSaltShakerName() {
        return SugarAndSaltSolutionsResources.Strings.SALT;
    }

    //Method to give the name displayed on the side of the sugar dispenser, necessary since it says e.g. "sugar" in macro tab and "sucrose" in micro tab
    protected String getSugarDispenserName() {
        return SugarAndSaltSolutionsResources.Strings.SUGAR;
    }

    //Rate at which liquid (but no solutes) leaves the model
    public final SettableProperty<Integer> evaporationRate = new Property<Integer>( 0 );//Between 0 and 100

    //Rate at which liquid evaporates
    private final double evaporationRateScale;

    //The elapsed running time of the model
    protected double time;

    public SugarAndSaltSolutionModel( ConstantDtClock clock,
                                      //Dimensions of the beaker
                                      BeakerDimension beakerDimension,
                                      double faucetFlowRate,
                                      final double drainPipeBottomY,
                                      final double drainPipeTopY,

                                      //Scale to help accommodate micro tab, for Macro tab the scale is 1.0
                                      double distanceScale ) {
        super( clock );
        this.faucetFlowRate = faucetFlowRate;
        this.distanceScale = distanceScale;
        this.evaporationRateScale = faucetFlowRate / 300.0;//Scaled down since the evaporation control rate is 100 times bigger than flow scales

        //Start the water halfway up the beaker
        this.waterVolume = new DoubleProperty( beakerDimension.getVolume() / 2 );

        //Model moles, concentration, amount dissolved, amount precipitated, etc. for salt and sugar
        salt = new SoluteModel( waterVolume, saltSaturationPoint, 0.02699 / 1000.0, MacroSalt.molarMass );
        sugar = new SoluteModel( waterVolume, sugarSaturationPoint, 0.2157 / 1000.0, MacroSugar.molarMass );

        //Total volume of the water plus any solid precipitate submerged under the water (and hence pushing it up)
        solidVolume = salt.solidVolume.plus( sugar.solidVolume );

        //Inset so the beaker doesn't touch the edge of the model bounds
        final double inset = beakerDimension.width * 0.1;
        final double modelWidth = beakerDimension.width + inset * 2;

        //Beaker model
        beaker = new Beaker( beakerDimension.x, 0, beakerDimension.width, beakerDimension.height, beakerDimension.depth, beakerDimension.wallThickness );

        //Visible model region: a bit bigger than the beaker, used to set the stage aspect ratio in the canvas
        visibleRegion = new ImmutableRectangle2D( -modelWidth / 2, -inset, modelWidth, modelWidth / aspectRatio );

        //Set a max amount of water that the user can add to the system so they can't overflow it
        maxWater = beaker.getMaxFluidVolume();

        solutionY = new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return beaker.getHeightForVolume( solidVolume.get() );
            }
        }, solidVolume );

        //Create the solution, which sits atop the solid precipitate (if any)
        //TODO: are units correct on this line?
        solution = new Solution( waterVolume, beaker, solutionY );

        //Determine the concentration of dissolved solutes
        //When we were accounting for volume effects of dissolved solutes, the concentrations had to be defined here instead of in SoluteModel because they depend on the total volume of the solution (which in turn depends on the amount of solute dissolved in the solvent).
        saltConcentration = salt.molesDissolved.dividedBy( solution.volume );
        sugarConcentration = sugar.molesDissolved.dividedBy( solution.volume );

        //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
        anySolutes = salt.moles.greaterThan( 0 ).or( sugar.moles.greaterThan( 0 ) );

        //User setting: whether the concentration bar chart should be shown
        showConcentrationBarChart = new Property<Boolean>( true );

        //Keep track of how many moles of crystal are in the air, since we need to prevent user from adding more than 10 moles to the system
        //This shuts off salt/sugar when there is salt/sugar in the air that could get added to the solution
        airborneSaltGrams = new AirborneCrystalMoles( saltList ).times( salt.gramsPerMole );
        airborneSugarGrams = new AirborneCrystalMoles( sugarList ).times( sugar.gramsPerMole );

        //Properties to indicate if the user is allowed to add more of the solute.  If not allowed the dispenser is shown as empty.
        moreSaltAllowed = salt.grams.plus( airborneSaltGrams ).lessThan( 100 );
        moreSugarAllowed = sugar.grams.plus( airborneSugarGrams ).lessThan( 100 );

        //Convenience composite properties for determining whether the beaker is full or empty so we can shut off the faucets when necessary
        beakerFull = solution.volume.greaterThanOrEqualTo( maxWater );

        //Determine if the lower faucet is allowed to let fluid flow out.  It can if any part of the fluid overlaps any part of the pipe range.
        //This logic is used in the model update step to determine if water can flow out, as well as in the user interface to determine if the user can turn on the output faucet
        lowerFaucetCanDrain = new VerticalRangeContains( solution.shape, drainPipeBottomY, drainPipeTopY );

        //Create the list of dispensers
        dispensers = new ArrayList<Dispenser>();
    }

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
        removeSalt();
        removeSugar();
    }

    //Called when the user presses a button to clear the salt, removes all salt (dissolved and crystals) from the sim
    public void removeSalt() {
        removeCrystals( saltList, saltList );
        salt.moles.set( 0.0 );
    }

    //Called when the user presses a button to clear the sugar, removes all sugar (dissolved and crystals) from the sim
    public void removeSugar() {
        removeCrystals( sugarList, sugarList );
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
        time += dt;

//        System.out.println( "SugarAndSaltSolutionModel.updateModel: beaker volume = "+beaker.getMaxFluidVolume() );

        //Have to record the concentrations before the model updates since the concentrations change if water is added or removed.
        double initialSaltConcentration = saltConcentration.get();
        double initialSugarConcentration = sugarConcentration.get();

        //Add any new crystals from the salt & sugar dispensers
        for ( Dispenser dispenser : dispensers ) {
            dispenser.updateModel( this );
        }

        //Change the water volume based on input and output flow
        double inputWater = dt * inputFlowRate.get() * faucetFlowRate;
        double drainedWater = dt * outputFlowRate.get() * faucetFlowRate;
        double evaporatedWater = dt * evaporationRate.get() * evaporationRateScale;

        //Compute the new water volume, but making sure it doesn't overflow or underflow
        //TODO: use the solution volume here?
        double newVolume = waterVolume.get() + inputWater - drainedWater - evaporatedWater;
        if ( newVolume > maxWater ) {
            inputWater = maxWater + drainedWater + evaporatedWater - waterVolume.get();
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
        if ( newVolume >= maxWater ) {
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

        //Notify subclasses that water evaporated in case they need to update anything
        if ( evaporatedWater > 0 ) {
            waterEvaporated( evaporatedWater );
        }

        //Move about the sugar and salt crystals, and maybe absorb them
        updateCrystals( dt, saltList );
        updateCrystals( dt, sugarList );
    }

    //Callback when water has evaporated from the solution
    protected void waterEvaporated( double evaporatedWater ) {
        //Nothing to do in the base class
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
            //Any crystals that landed on the beaker base or on top of precipitate should immediately precipitate into solid so that they take up the right volume and are consistent with our other representations
            else if ( crystal.isLanded() ) {
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
        for ( Dispenser dispenser : dispensers ) {
            dispenser.reset();
        }
        dispenserType.reset();
        showConcentrationValues.reset();
        showConcentrationBarChart.reset();

        notifyReset();
    }

    //Determine if any salt can be removed for purposes of displaying a "remove salt" button
    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return salt.moles.greaterThan( 0.0 );
    }

    //Determine if any sugar can be removed for purposes of displaying a "remove sugar" button
    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return sugar.moles.greaterThan( 0.0 );
    }
}