// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * Class that represents a block of iron in the view.
 *
 * @author John Blanco
 */
public class IronBlock extends Block {

    //    private static final double SPECIFIC_HEAT = 450; // In J/kg-K
    private static final double SPECIFIC_HEAT = 275; // In J/kg-K
    private static final double DENSITY = 7800; // In kg/m^3, source = design document.

    protected IronBlock( ConstantDtClock clock, ImmutableVector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, DENSITY, SPECIFIC_HEAT, energyChunksVisible );
    }

    @Override public Color getColor() {
        return new Color( 150, 150, 150 );
    }

    @Override public String getLabel() {
        // TODO: i18n
        return "Iron";
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }
}
