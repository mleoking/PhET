// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * @author John Blanco
 */
public class Brick extends Block {

    private static final double SPECIFIC_HEAT = 0.84; // In J/gK, source = design document.
    private static final double DENSITY = 2.0; // In g/cm^3, source = design document.

    protected Brick( ImmutableVector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( initialPosition, DENSITY, SPECIFIC_HEAT, energyChunksVisible );
    }

    @Override public Image getFrontTextureImage() {
        return EnergyFormsAndChangesResources.Images.BRICK_TEXTURE_FRONT;
    }

    @Override public Image getTopTextureImage() {
        return EnergyFormsAndChangesResources.Images.BRICK_TEXTURE_TOP;
    }

    @Override public Image getSideTextureImage() {
        return EnergyFormsAndChangesResources.Images.BRICK_TEXTURE_RIGHT;
    }

    @Override public Color getColor() {
        return new Color( 200, 22, 11 );
    }

    @Override public String getLabel() {
        // TODO: i18n
        return "Brick";
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }
}
