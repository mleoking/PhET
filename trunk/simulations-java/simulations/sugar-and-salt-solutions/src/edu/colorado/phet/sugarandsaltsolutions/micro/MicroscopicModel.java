// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DividedBy;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.MacroCrystal;
import edu.colorado.phet.sugarandsaltsolutions.common.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.common.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;

/**
 * @author Sam Reid
 */
public class MicroscopicModel extends SugarAndSaltSolutionModel {
    //Salt moles and concentration
    private final DoubleProperty molesOfSalt = new DoubleProperty( 0.0 );
    public final DividedBy saltConcentration = molesOfSalt.dividedBy( water.volume );

    //Sugar moles and concentration
    private final DoubleProperty molesOfSugar = new DoubleProperty( 0.0 );

    public MicroscopicModel() {
    }

    @Override public double getSaltConcentration() {
        if ( saltConcentration == null ) { return 0.0; }//this is called during super call before saltConcentration is set, so is null, so just return 0 in that case.  TODO: fix
        return saltConcentration.get();
    }

    //When a crystal is absorbed by the water, increase the number of moles in solution
    protected void crystalAbsorbed( MacroCrystal crystal ) {
        if ( crystal instanceof MacroSalt ) {
            molesOfSalt.set( molesOfSalt.get() + crystal.getMoles() );
        }
        else if ( crystal instanceof MacroSugar ) {
            molesOfSugar.set( molesOfSugar.get() + crystal.getMoles() );
        }
    }

    //Removes all sugar and salt from the liquid
    @Override public void removeSaltAndSugar() {
        super.removeSaltAndSugar();
        molesOfSalt.set( 0.0 );
        molesOfSugar.set( 0.0 );
    }

    //Have some moles of salt and sugar flow out so that the concentration remains unchanged
    @Override protected void waterDrained( double outVolume ) {
        super.waterDrained( outVolume );

        //Make sure to keep the concentration the same when water flowing out
        updateConcentration( outVolume, molesOfSalt );
        updateConcentration( outVolume, molesOfSugar );
    }

    //Make sure to keep the concentration the same when water flowing out
    private void updateConcentration( double outVolume, Property<Double> property ) {
        //Find what the concentration was before water exited
        double initConcentration = property.get() / ( water.volume.get() + outVolume );

        //If the water volume was zero, it could cause the initConcentration to be NaN, so handle error cases and just have all crystal moles exit in those cases.
        if ( initConcentration <= 0 || isInfinite( initConcentration ) || isNaN( initConcentration ) ) {
            initConcentration = 0;
        }

        //Find the new number of moles that keeps the concentration the same
        double finalMoles = initConcentration * water.volume.get();

        //Set the number of moles
        property.set( finalMoles );
    }
}
