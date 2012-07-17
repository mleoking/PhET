// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL_BASE;

/**
 * Class that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanel extends EnergyConverter {

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( SOLAR_PANEL, SOLAR_PANEL.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new ImmutableVector2D( 0, 0 ) ) );
        add( new ModelElementImage( SOLAR_PANEL_BASE, SOLAR_PANEL_BASE.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new ImmutableVector2D( 0, 0 ) ) );
    }};

    protected SolarPanel() {
        super( EnergyFormsAndChangesResources.Images.SOLAR_PANEL_ICON, IMAGE_LIST );
    }

    @Override public double stepInTime( double dt, double incomingEnergy ) {
        // TODO: Implement.
        return 0;
    }
}
