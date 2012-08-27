// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

/**
 * Class that represents the sun (as an energy source) in the model.
 *
 * @author John Blanco
 */
public class Sun extends EnergySource {

    public static final double RADIUS = 0.02; // In meters, apparent size, not (obviously) actual size.
    public static final Vector2D OFFSET_TO_CENTER_OF_SUN = new Vector2D( -0.05, 0.12 );

    // Energy production per square meter of the Earth's surface.
    private static final double ENERGY_PRODUCTION_RATE = 1000; // In joules/second per square meter of Earth.

    //TODO: This, and all image lists, should be removed once the prototypes have all been replaced.
    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
    }};

    protected Sun() {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON, IMAGE_LIST );
    }

    @Override public Energy stepInTime( double dt ) {
        double energyProduced = active ? ENERGY_PRODUCTION_RATE * dt : 0;
        return new Energy( Energy.Type.SOLAR, energyProduced );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
