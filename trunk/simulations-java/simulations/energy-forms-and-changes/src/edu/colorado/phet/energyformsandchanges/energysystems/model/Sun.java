// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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

    // Energy production per square meter of the Earth's surface.
    private static final double ENERGY_PRODUCTION_RATE = 1000; // In joules/second per square meter of Earth.

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( TEMP_SUN, TEMP_SUN.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( -0.05, 0.1 ) ) );
    }};

    protected Sun() {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON, IMAGE_LIST );
    }

    @Override public Energy stepInTime( double dt ) {
        // TODO: Implement.
        return new Energy( Energy.Type.SOLAR, ENERGY_PRODUCTION_RATE * dt );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
