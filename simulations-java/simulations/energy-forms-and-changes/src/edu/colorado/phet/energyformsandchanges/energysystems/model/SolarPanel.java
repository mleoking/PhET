// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * Class that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanel extends EnergyConverter {

    private static final double CONVERSION_EFFICIENCY = 0.3;

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( SOLAR_PANEL_BASE, new Vector2D( 0.015, -0.025 ) ) );
        add( new ModelElementImage( SOLAR_PANEL, new Vector2D( 0, 0.044 ) ) );
        add( new ModelElementImage( WIRE_BLACK_MIDDLE, new Vector2D( 0.075, -0.04 ) ) );
        add( new ModelElementImage( CONNECTOR, new Vector2D( 0.058, -0.04 ) ) );
    }};

    protected SolarPanel() {
        super( EnergyFormsAndChangesResources.Images.SOLAR_PANEL_ICON, IMAGE_LIST );
    }

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {
        double energyProduced = 0;
        if ( active && incomingEnergy.type == Energy.Type.SOLAR ) {
            energyProduced = active ? incomingEnergy.amount * CONVERSION_EFFICIENCY : 0;
        }
        return new Energy( Energy.Type.ELECTRICAL, energyProduced, 0 );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSolarPanelButton;
    }
}
