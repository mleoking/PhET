// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.TEMP_SUN;

/**
 * Class that represents the sun (as an energy source) in the model.
 *
 * @author John Blanco
 */
public class Sun extends EnergySource {

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( TEMP_SUN, TEMP_SUN.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new ImmutableVector2D( -0.1, 0.1 ) ) );
    }};

    protected Sun() {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON, IMAGE_LIST );
    }

    @Override public double stepInTime( double dt ) {
        // TODO: Implement.
        return 0;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
