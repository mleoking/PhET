// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.common.phetcommon.model.property3.DivideDouble;
import edu.colorado.phet.common.phetcommon.model.property3.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Salt;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Sugar;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * @author Sam Reid
 */
public class IntroModel extends SugarAndSaltSolutionModel {
    private final Property<Double> molesOfSalt = new Property<Double>( 0.0 );
    public final DivideDouble saltConcentration = new DivideDouble( molesOfSalt, water.volume );

    private final Property<Double> molesOfSugar = new Property<Double>( 0.0 );
    public final DivideDouble sugarConcentration = new DivideDouble( molesOfSugar, water.volume );

    protected void crystalAbsorbed( Crystal crystal ) {
        if ( crystal instanceof Salt ) {
            molesOfSalt.set( molesOfSalt.get() + crystal.getMoles() );
        }
        else if ( crystal instanceof Sugar ) {
            molesOfSugar.set( molesOfSugar.get() + crystal.getMoles() );
        }
    }
}