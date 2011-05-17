// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSugar;

/**
 * Introductory model that keeps track of moles of solute dissolved in the liquid.
 *
 * @author Sam Reid
 */
public class IntroModel extends SugarAndSaltSolutionModel {

    //Saturation points for salt and sugar assume 25 degrees C
    private static final double saltSaturationPoint = 6.14 * 1000;//6.14 moles per liter, converted to SI
    private static final double sugarSaturationPoint = 5.85 * 1000;//5.85 moles per liter, converted to SI

    //Model moles, concentration, amount dissolved, amount precipitated, etc. for salt and sugar
    public final SoluteModel salt = new SoluteModel( water.volume, saltSaturationPoint );
    public final SoluteModel sugar = new SoluteModel( water.volume, sugarSaturationPoint );

    //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    public final ObservableProperty<Boolean> anySolutes = salt.moles.greaterThan( 0 ).or( sugar.moles.greaterThan( 0 ) );
    public final Property<Boolean> showConcentrationBarChart = new Property<Boolean>( true );
    private double initialSaltConcentration;
    private double initialSugarConcentration;

    public IntroModel() {
        salt.concentration.addObserver( new VoidFunction1<Double>() {
            public void apply( Double concentration ) {
//                System.out.println( "moles of salt = " + molesOfSalt + ", water volume = " + water.volume + ", => conc = " + concentration );

                //Update the conductivity tester brightness since the brightness is a function of the salt concentration
                updateConductivityTesterBrightness();
            }
        } );
    }

    @Override protected void updateModel( double dt ) {
        //Have to record the concentrations before the model updates since the concentrations change if water is added or removed.
        initialSaltConcentration = salt.concentration.get();
        initialSugarConcentration = sugar.concentration.get();
        super.updateModel( dt );
    }

    @Override public double getSaltConcentration() {
        if ( salt == null ) { return 0.0; }//this is called during super call before saltConcentration is set, so is null, so just return 0 in that case.  TODO: fix
        return salt.concentration.get();
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

    //Removes all sugar and salt from the liquid
    @Override public void removeSaltAndSugar() {
        super.removeSaltAndSugar();
        salt.moles.set( 0.0 );
        sugar.moles.set( 0.0 );
    }

    //Have some moles of salt and sugar flow out so that the concentration remains unchanged
    @Override protected void waterDrained( double outVolume ) {
        super.waterDrained( outVolume );

        //Make sure to keep the concentration the same when water flowing out.  Use the values recorded before the model stepped to ensure conservation of solute moles
        updateConcentration( outVolume, initialSaltConcentration, salt.moles );
        updateConcentration( outVolume, initialSugarConcentration, sugar.moles );
    }

    //Make sure to keep the concentration the same when water flowing out
    private void updateConcentration( double outVolume, double concentration, SettableProperty<Double> moles ) {
        double molesOfSoluteLeaving = concentration * outVolume;
        moles.set( moles.get() - molesOfSoluteLeaving );
    }
}