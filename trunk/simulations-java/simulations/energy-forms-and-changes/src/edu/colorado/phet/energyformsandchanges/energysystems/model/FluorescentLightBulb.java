// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import javafx.beans.property.ObjectPropertyBase;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.selectFluorescentLightBulbButton;

/**
 * Class that represents a compact fluorescent light bulb in the model.
 *
 * @author John Blanco
 */
public class FluorescentLightBulb extends LightBulb {

    private static final Vector2D IMAGE_OFFSET = new Vector2D( 0, 0.04 );

    public static final ModelElementImage BACK_OFF = new ModelElementImage( FLUORESCENT_BACK, IMAGE_OFFSET );
    public static final ModelElementImage BACK_ON = new ModelElementImage( FLUORESCENT_ON_BACK, IMAGE_OFFSET );

    public static final ModelElementImage FRONT_OFF = new ModelElementImage( FLUORESCENT_FRONT, IMAGE_OFFSET );
    public static final ModelElementImage FRONT_ON = new ModelElementImage( FLUORESCENT_ON_FRONT, IMAGE_OFFSET );

    protected FluorescentLightBulb( ObservableProperty<Boolean> energyChunksVisible ) {
        super( selectFluorescentLightBulbButton, FLUORESCENT_ICON, false, energyChunksVisible );
    }
}
