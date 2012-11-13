// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.selectIncandescentLightBulbButton;

/**
 * @author John Blanco
 */
public class IncandescentLightBulb extends LightBulb {

    private static final Vector2D IMAGE_OFFSET = new Vector2D( 0, 0.055 );

    public static final ModelElementImage NON_ENERGIZED_BULB = new ModelElementImage( INCANDESCENT_NARROW, IMAGE_OFFSET );

    public static final ModelElementImage ENERGIZED_BULB = new ModelElementImage( INCANDESCENT_ON_NARROW, IMAGE_OFFSET );

    protected IncandescentLightBulb() {
        super( selectIncandescentLightBulbButton, INCANDESCENT_ICON, true );
    }
}
