// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * @author John Blanco
 */
public class BeakerHeater extends EnergyUser {

    private static final double ENERGY_TO_FULLY_ACTIVATE = 50; // In joules/sec, a.k.a. Watts.

    // Proportion, from 0 to 1, of the heat being generated.
    public final Property<Double> heatProportion = new Property<Double>( 0.0 );

    private static final Vector2D HEATER_ELEMENT_OFFSET = new Vector2D( -0.002, 0.022 );

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( WIRE_BLACK_MIDDLE_62, WIRE_BLACK_MIDDLE_62.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( -0.037, -0.04 ) ) );
        add( new ModelElementImage( WIRE_BLACK_RIGHT, WIRE_BLACK_RIGHT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( -0.009, -0.016 ) ) );
        add( new ModelElementImage( ELEMENT_BASE, ELEMENT_BASE.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( 0, 0 ) ) );
        add( new ModelElementImage( HEATER_ELEMENT_OFF, HEATER_ELEMENT_OFF.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, HEATER_ELEMENT_OFFSET ) );
        add( new ModelElementImage( HEATER_ELEMENT, HEATER_ELEMENT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, HEATER_ELEMENT_OFFSET ) );
    }};

    protected BeakerHeater() {
        super( EnergyFormsAndChangesResources.Images.WATER_ICON, IMAGE_LIST );
    }

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {
        if ( incomingEnergy.type == Energy.Type.ELECTRICAL ) {
            heatProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / ENERGY_TO_FULLY_ACTIVATE, 1 ) );
        }
        else {
            heatProportion.set( 0.0 );
        }
    }

    @Override public void deactivate() {
        super.deactivate();
        heatProportion.set( 0.0 );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectBeakerHeaterButton;
    }
}
