// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * Class that represents a block whose specific heat can be changed.
 *
 * @author John Blanco
 */
public class ConfigurableSpecificHeatBlock extends Block {

    private static final double INITIAL_SPECIFIC_HEAT = 840; // In J/kg-K, source = design document.
    private static final double DENSITY = 2000; // In kg/m^3, source = design document.

    protected ConfigurableSpecificHeatBlock( ConstantDtClock clock, ImmutableVector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, DENSITY, INITIAL_SPECIFIC_HEAT, energyChunksVisible );
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

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.BRICK;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }
}
