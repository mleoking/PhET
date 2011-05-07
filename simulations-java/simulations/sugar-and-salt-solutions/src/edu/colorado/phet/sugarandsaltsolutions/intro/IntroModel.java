// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.common.phetcommon.model.property5.DivideDouble;
import edu.colorado.phet.common.phetcommon.model.property5.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property5.Property;
import edu.colorado.phet.common.phetcommon.model.property5.doubleproperty.DoubleProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Salt;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Sugar;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * Introductory model that keeps track of moles of solute dissolved in the liquid.
 *
 * @author Sam Reid
 */
public class IntroModel extends SugarAndSaltSolutionModel {
    //Salt moles and concentration
    private final DoubleProperty molesOfSalt = new DoubleProperty( 0.0 );
    public final DivideDouble saltConcentration = molesOfSalt.dividedBy( water.volume );

    //Sugar moles and concentration
    private final DoubleProperty molesOfSugar = new DoubleProperty( 0.0 );
    public final DivideDouble sugarConcentration = molesOfSugar.dividedBy( water.volume );
    public final ObservableProperty<Boolean> anySolutes = molesOfSalt.greaterThan( 0 ).or( molesOfSugar.greaterThan( 0 ) );

    //When a crystal is absorbed by the water, increase the number of moles in solution
    protected void crystalAbsorbed( Crystal crystal ) {
        if ( crystal instanceof Salt ) {
            molesOfSalt.setValue( molesOfSalt.getValue() + crystal.getMoles() );
        }
        else if ( crystal instanceof Sugar ) {
            molesOfSugar.setValue( molesOfSugar.getValue() + crystal.getMoles() );
        }
    }

    //Removes all sugar and salt from the liquid
    @Override public void removeSaltAndSugar() {
        super.removeSaltAndSugar();
        molesOfSalt.setValue( 0.0 );
        molesOfSugar.setValue( 0.0 );
    }

    //Have some moles of salt and sugar flow out so that the concentration remains unchanged
    @Override protected void waterExited( double outVolume ) {
        super.waterExited( outVolume );

        //Make sure to keep the concentration the same when water flowing out
        updateConcentration( outVolume, molesOfSalt );
        updateConcentration( outVolume, molesOfSugar );
    }

    //Make sure to keep the concentration the same when water flowing out
    private void updateConcentration( double outVolume, Property<Double> property ) {
        //Find what the concentration was before water exited
        double initConcentration = property.getValue() / ( water.volume.getValue() + outVolume );

        //Find the new number of moles that keeps the concentration the same
        double finalMoles = initConcentration * water.volume.getValue();

        //Set the number of moles
        property.setValue( finalMoles );
    }
}