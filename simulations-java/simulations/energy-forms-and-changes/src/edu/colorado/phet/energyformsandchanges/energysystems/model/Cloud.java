// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.CLOUD_1;

/**
 * Class that represents a cloud in the view.
 *
 * @author John Blanco
 */
public class Cloud {

    private static final double CLOUD_WIDTH = 0.07; // In meters, though obviously not to scale.

    public static final ModelElementImage CLOUD_IMAGE = new ModelElementImage( CLOUD_1, CLOUD_WIDTH, new Vector2D( 0, 0 ) );
    public final Vector2D offset;
    public final Property<Double> existenceStrength = new Property<Double>( 1.0 );

    public Cloud( Vector2D initialPosition ) {
        offset = initialPosition;
    }
}
