// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.HEATER_ELEMENT;

/**
 * @author John Blanco
 */
public class BeakerHeater extends EnergyUser {

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( HEATER_ELEMENT, HEATER_ELEMENT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new ImmutableVector2D( 0, 0 ) ) );
    }};


    protected BeakerHeater() {
        super( EnergyFormsAndChangesResources.Images.WATER_ICON, IMAGE_LIST );
    }

    @Override public void stepInTime( double dt, double incomingEnergy ) {
        // TODO: Implement.
    }
}
