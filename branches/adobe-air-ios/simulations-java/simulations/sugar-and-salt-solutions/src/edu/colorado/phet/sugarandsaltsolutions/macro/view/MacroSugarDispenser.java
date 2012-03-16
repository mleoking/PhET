// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;

/**
 * @author Sam Reid
 */
public class MacroSugarDispenser extends SugarDispenser<MacroModel> {
    public MacroSugarDispenser( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, final String sugarDispenserName, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MacroModel model ) {
        super( x, y, beaker, moreAllowed, sugarDispenserName, distanceScale, selectedType, type, model );
    }

    @Override protected void addSugarToModel( final ImmutableVector2D outputPoint ) {
        //Add the sugar, with some randomness in the velocity
        model.addMacroSugar( new MacroSugar( outputPoint, model.sugar.volumePerSolidMole ) {{
            velocity.set( getCrystalVelocity( outputPoint ).plus( ( random.nextDouble() - 0.5 ) * 0.05, ( random.nextDouble() - 0.5 ) * 0.05 ) );
        }} );
    }
}