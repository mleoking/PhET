// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Model dispenser that shakes sucrose into the model.
 *
 * @author Sam Reid
 */
public class SucroseDispenser extends MicroSugarDispenser {

    public SucroseDispenser( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, final String sugarDispenserName, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MicroModel model ) {
        super( x, y, beaker, moreAllowed, sugarDispenserName, distanceScale, selectedType, type, model );
    }

    //Create and add a random sucrose crystal with 4 sucrose molecules
    @Override protected void doAddSugar( ImmutableVector2D outputPoint ) {
        model.addSucroseCrystal( new SucroseCrystal( outputPoint, randomAngle() ) {{grow( 6 ); }} );
    }
}