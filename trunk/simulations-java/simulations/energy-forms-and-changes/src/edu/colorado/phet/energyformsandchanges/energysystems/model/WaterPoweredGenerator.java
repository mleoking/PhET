// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * This class represents an electrical generator that is powered by flowing
 * water or steam.
 *
 * @author John Blanco
 */
public class WaterPoweredGenerator extends EnergyConverter {

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( EnergyFormsAndChangesResources.Images.GENERATOR,
                                    EnergyFormsAndChangesResources.Images.GENERATOR.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                    new Vector2D( 0, 0 ) ) );
        add( new ModelElementImage( EnergyFormsAndChangesResources.Images.GENERATOR_WHEEL,
                                    EnergyFormsAndChangesResources.Images.GENERATOR_WHEEL.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                    new Vector2D( 0, 0 ) ) );
    }};

    public WaterPoweredGenerator() {
        super( EnergyFormsAndChangesResources.Images.GENERATOR_ICON, IMAGE_LIST );
    }

    @Override public double stepInTime( double dt, double incomingEnergy ) {
        // TODO - Implement.
        return 0;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
