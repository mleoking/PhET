// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * @author John Blanco
 */
public class IncandescentLightBulb extends EnergyUser {

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( EnergyFormsAndChangesResources.Images.INCANDESCENT,
                                    EnergyFormsAndChangesResources.Images.INCANDESCENT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                    new ImmutableVector2D( 0, 0 ) ) );
    }};


    protected IncandescentLightBulb() {
        super( EnergyFormsAndChangesResources.Images.INCANDESCENT, IMAGE_LIST );
    }

    @Override public void stepInTime( double dt, double incomingEnergy ) {
        // TODO: Implement.
    }
}
