// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * @author John Blanco
 */
public class LeadBlock extends Block {

    protected LeadBlock( ImmutableVector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( initialPosition, energyChunksVisible );
    }

    @Override public Color getColor() {
        return new Color( 130, 130, 130 );
    }

    @Override public String getLabel() {
        // TODO: i18n
        return "Lead";
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }
}
