// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import edu.colorado.phet.sugarandsaltsolutions.common.model.AirborneCrystalMoles;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ConductivityTester;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroSugarDispenser;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;

/**
 * Introductory (macro) model that keeps track of moles of solute dissolved in the liquid.
 *
 * @author Sam Reid
 */
public class MacroModel extends SugarAndSaltSolutionModel {
    //Model for the conductivity tester which is in the macro tab but not other tabs
    public final ConductivityTester conductivityTester;

    //Sugar and its listeners
    public final ArrayList<MacroSugar> sugarList = new ArrayList<MacroSugar>();//The sugar crystals that haven't been dissolved
    public final Notifier<MacroSugar> sugarAdded = new Notifier<MacroSugar>();//Listeners for when sugar crystals are added

    //Salt and its listeners
    public final ArrayList<MacroSalt> saltList = new ArrayList<MacroSalt>();//The salt crystals that haven't been dissolved
    public final Notifier<MacroSalt> saltAdded = new Notifier<MacroSalt>();//Listeners for when salt crystals are added

    //Saturation points for salt and sugar assume 25 degrees C
    private static final double saltSaturationPoint = 6.14 * 1000;//6.14 moles per liter, converted to SI
    private static final double sugarSaturationPoint = 5.85 * 1000;//5.85 moles per liter, converted to SI

    //Model moles, concentration, amount dissolved, amount precipitated, etc. for salt and sugar
    public final SoluteModel salt;
    public final SoluteModel sugar;

    //Total volume of the water plus any solid precipitate submerged under the water (and hence pushing it up)
    public final CompositeDoubleProperty solidVolume;

    //The concentration in the liquid in moles / m^3
    public final CompositeDoubleProperty saltConcentration;
    public final CompositeDoubleProperty sugarConcentration;

    //Amounts of sugar and salt in crystal form falling from the dispenser
    protected CompositeDoubleProperty airborneSaltGrams;
    protected CompositeDoubleProperty airborneSugarGrams;

    //Force due to gravity near the surface of the earth in m/s^2
    private final ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );

    //Flag to indicate if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    public ObservableProperty<Boolean> anySolutes;

    public MacroModel() {
        super( new ConstantDtClock( 30 ), new BeakerDimension( 0.2 ), 0.0005,

               //These values were sampled from the model with debug mode.
               0.011746031746031754, 0.026349206349206344,

               //In macro model scales are already tuned so no additional scaling is needed
               1 );

        //Model moles, concentration, amount dissolved, amount precipitated, etc. for salt and sugar
        salt = new SoluteModel( waterVolume, saltSaturationPoint, SoluteModel.VOLUME_PER_SOLID_MOLE_SALT, MacroSalt.molarMass );
        sugar = new SoluteModel( waterVolume, sugarSaturationPoint, 0.2157 / 1000.0, MacroSugar.molarMass );

        //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
        anySolutes = salt.moles.greaterThan( 0 ).or( sugar.moles.greaterThan( 0 ) );

        //Total volume of the water plus any solid precipitate submerged under the water (and hence pushing it up)
        solidVolume = salt.solidVolume.plus( sugar.solidVolume );

        //Determine the concentration of dissolved solutes
        //When we were accounting for volume effects of dissolved solutes, the concentrations had to be defined here instead of in SoluteModel because they depend on the total volume of the solution (which in turn depends on the amount of solute dissolved in the solvent).
        saltConcentration = salt.molesDissolved.dividedBy( solution.volume );
        sugarConcentration = sugar.molesDissolved.dividedBy( solution.volume );

        //Keep track of how many moles of crystal are in the air, since we need to prevent user from adding more than 10 moles to the system
        //This shuts off salt/sugar when there is salt/sugar in the air that could get added to the solution
        airborneSaltGrams = new AirborneCrystalMoles( saltList ).times( salt.gramsPerMole );
        airborneSugarGrams = new AirborneCrystalMoles( sugarList ).times( sugar.gramsPerMole );

        //Properties to indicate if the user is allowed to add more of the solute.  If not allowed the dispenser is shown as empty.
        ObservableProperty<Boolean> moreSaltAllowed = salt.grams.plus( airborneSaltGrams ).lessThan( 100 );
        ObservableProperty<Boolean> moreSugarAllowed = sugar.grams.plus( airborneSugarGrams ).lessThan( 100 );

        //Add models for the various dispensers: sugar, salt, etc.
        dispensers.add( new MacroSaltShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSaltAllowed, Strings.SALT, distanceScale, dispenserType, SALT, this ) );
        dispensers.add( new MacroSugarDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, Strings.SUGAR, distanceScale, dispenserType, SUGAR, this ) );

        //Model for the conductivity tester
        conductivityTester = new ConductivityTester( beaker.getWidth(), beaker.getHeight() );

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

        //Update the conductivity tester when the water level changes, since it might move up to touch a probe (or move out from underneath a submerged probe)
        new RichSimpleObserver() {
            @Override public void update() {
                updateConductivityTesterBrightness();
            }
        }.observe( saltConcentration, solution.shape, outputWater );
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
    public final void removeSalt() {
        removeCrystals( saltList, saltList );
        salt.moles.set( 0.0 );
    }

    //Called when the user presses a button to clear the sugar, removes all sugar (dissolved and crystals) from the sim
    public final void removeSugar() {
        removeCrystals( sugarList, sugarList );
        sugar.moles.set( 0.0 );
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

    //Propagate the sugar and salt crystals, and absorb them if they hit the water
    private void updateCrystals( double dt, final ArrayList<? extends MacroCrystal> crystalList ) {
        ArrayList<MacroCrystal> hitTheWater = new ArrayList<MacroCrystal>();
        for ( MacroCrystal crystal : crystalList ) {
            //Store the initial location so we can use the (final - start) line to check for collision with water, so it can't jump over the water rectangle
            ImmutableVector2D initialLocation = crystal.position.get();

            //slow the motion down a little bit or it moves too fast
            //TODO: Why can't this run at full speed?
            crystal.stepInTime( gravity.times( crystal.mass ), dt / 10, beaker.getLeftWall(), beaker.getRightWall(), beaker.getFloor(),
                                new Line2D.Double( beaker.getFloor().getX1(), 0, beaker.getFloor().getX2(), 0 ) );

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

    //Determine if a conductivity tester probe is touching water in the beaker, or water flowing out of the beaker (which would have the same concentration as the water in the beaker)
    private boolean isProbeTouchingWaterThatMightHaveSalt( ImmutableRectangle2D region ) {
        Rectangle2D waterBounds = solution.shape.get().getBounds2D();

        final Rectangle2D regionBounds = region.toRectangle2D();
        return waterBounds.intersects( region.toRectangle2D() ) || outputWater.get().getBounds2D().intersects( regionBounds );
    }

    //Update the conductivity tester brightness when the probes come into contact with (or stop contacting) the fluid
    protected void updateConductivityTesterBrightness() {

        //Check for a collision with the probe, using the full region of each probe (so if any part intersects, there is still an electrical connection).
        Rectangle2D waterBounds = solution.shape.get().getBounds2D();

        //See if both probes are touching water that might have salt in it
        boolean bothProbesTouching = isProbeTouchingWaterThatMightHaveSalt( conductivityTester.getPositiveProbeRegion() ) && isProbeTouchingWaterThatMightHaveSalt( conductivityTester.getNegativeProbeRegion() );

        //Check to see if the circuit is shorted out (if light bulb or battery is submerged).
        //Null checks are necessary since those regions are computed from view components and may not have been computed yet (but will be non-null if the user dragged out the conductivity tester from the toolbox)
        boolean batterySubmerged = conductivityTester.getBatteryRegion() != null && waterBounds.intersects( conductivityTester.getBatteryRegion().getBounds2D() );
        boolean bulbSubmerged = conductivityTester.getBulbRegion() != null && waterBounds.intersects( conductivityTester.getBulbRegion().getBounds2D() );

        //The circuit should short out if the battery or bulb is submerged
        boolean shortCircuited = batterySubmerged || bulbSubmerged;

        //Set the brightness to be a linear function of the salt concentration (but keeping it bounded between 0 and 1 which are the limits of the conductivity tester brightness
        //Use a scale factor that matches up with the limits on saturation (manually sampled at runtime)
        conductivityTester.brightness.set( bothProbesTouching && !shortCircuited ? MathUtil.clamp( 0, saltConcentration.get() * 1.62E-4, 1 ) : 0.0 );
        conductivityTester.shortCircuited.set( shortCircuited );
    }

    @Override public ObservableProperty<Boolean> getAnySolutes() {
        return anySolutes;
    }

    @Override public void reset() {
        super.reset();
        removeSaltAndSugar();
        conductivityTester.reset();
    }

    //Determine if any salt can be removed for purposes of displaying a "remove salt" button
    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return salt.moles.greaterThan( 0.0 );
    }

    //Determine if any sugar can be removed for purposes of displaying a "remove sugar" button
    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return sugar.moles.greaterThan( 0.0 );
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

    //Update the model when the clock ticks
    protected double updateModel( double dt ) {

        //Have to record the concentrations before the model updates since the concentrations change if water is added or removed.
        double initialSaltConcentration = saltConcentration.get();
        double initialSugarConcentration = sugarConcentration.get();

        double drainedWater = super.updateModel( dt );

        //Notify listeners that some water (with solutes) exited the system, so they can decrease the amounts of solute (mols, not molarity) in the system
        //Only call when draining, would have the wrong behavior for evaporation
        if ( drainedWater > 0 ) {
            waterDrained( drainedWater, initialSaltConcentration, initialSugarConcentration );
        }

        //Move about the sugar and salt crystals, and maybe absorb them
        updateCrystals( dt, saltList );
        updateCrystals( dt, sugarList );

        return drainedWater;
    }

    //Remove the specified crystals.  Note that the toRemove
    private void removeCrystals( ArrayList<? extends MacroCrystal> crystalList, ArrayList<? extends MacroCrystal> toRemove ) {
        for ( MacroCrystal crystal : new ArrayList<MacroCrystal>( toRemove ) ) {
            crystal.remove();
            crystalList.remove( crystal );
        }
    }

}