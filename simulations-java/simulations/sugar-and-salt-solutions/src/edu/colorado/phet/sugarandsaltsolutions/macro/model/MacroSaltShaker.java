// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SaltShaker;

/**
 * @author Sam Reid
 */
public class MacroSaltShaker extends SaltShaker<MacroModel> {
    public MacroSaltShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MacroModel model ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type, model );
    }

    @Override protected void addSalt( MacroModel model, Vector2D outputPoint, double volumePerSolidMole, final Vector2D crystalVelocity ) {

        //Add the salt
        model.addMacroSalt( new MacroSalt( outputPoint, volumePerSolidMole ) {{

            //Give the salt an appropriate velocity when it comes out so it arcs
            velocity.set( crystalVelocity );
        }} );
    }
}
