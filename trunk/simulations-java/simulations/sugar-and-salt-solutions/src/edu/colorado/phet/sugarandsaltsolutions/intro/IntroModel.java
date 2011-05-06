// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.common.phetcommon.model.property3.DivideDouble;
import edu.colorado.phet.common.phetcommon.model.property3.Property;
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
    private final Property<Double> molesOfSalt = new Property<Double>( 0.0 );
    public final DivideDouble saltConcentration = new DivideDouble( molesOfSalt, water.volume );

    //Sugar moles and concentration
    private final Property<Double> molesOfSugar = new Property<Double>( 0.0 );
    public final DivideDouble sugarConcentration = new DivideDouble( molesOfSugar, water.volume );

    //When a crystal is absorbed by the water, increase the number of moles in solution
    protected void crystalAbsorbed( Crystal crystal ) {
        if ( crystal instanceof Salt ) {
            molesOfSalt.set( molesOfSalt.get() + crystal.getMoles() );
        }
        else if ( crystal instanceof Sugar ) {
            molesOfSugar.set( molesOfSugar.get() + crystal.getMoles() );
        }
    }

    //Removes all sugar and salt from the liquid
    @Override public void removeSaltAndSugar() {
        super.removeSaltAndSugar();
        molesOfSalt.set( 0.0 );
        molesOfSugar.set( 0.0 );
    }
}