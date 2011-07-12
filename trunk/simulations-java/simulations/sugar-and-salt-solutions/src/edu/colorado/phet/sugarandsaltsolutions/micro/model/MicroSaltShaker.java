// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Shaker;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;

/**
 * @author Sam Reid
 */
public class MicroSaltShaker extends Shaker<MicroModel> {
    public MicroSaltShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addSalt( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, final ImmutableVector2D crystalVelocity ) {
        //Add the salt
        model.addMicroSalt( new MacroSalt( outputPoint, volumePerSolidMole ) {{
            //Give the salt an appropriate velocity when it comes out so it arcs
            velocity.set( crystalVelocity );
        }} );
    }
}
