// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * Base class for model elements that use energy, and generally do something
 * as a result.
 *
 * @author John Blanco
 */
public class BeakerHeater extends EnergyUser {

    // Images used to represent this class in the view.
    private static final Vector2D HEATER_ELEMENT_OFFSET = new Vector2D( -0.002, 0.022 );
    public static final ModelElementImage WIRE_STRAIGHT_IMAGE = new ModelElementImage( WIRE_BLACK_86, new Vector2D( -0.042, -0.04 ) );
    public static final ModelElementImage WIRE_CURVE_IMAGE = new ModelElementImage( WIRE_BLACK_RIGHT, new Vector2D( -0.009, -0.016 ) );
    public static final ModelElementImage ELEMENT_BASE_IMAGE = new ModelElementImage( ELEMENT_BASE, new Vector2D( 0, 0 ) );
    public static final ModelElementImage HEATER_ELEMENT_OFF_IMAGE = new ModelElementImage( HEATER_ELEMENT_OFF, HEATER_ELEMENT_OFFSET );
    public static final ModelElementImage HEATER_ELEMENT_ON_IMAGE = new ModelElementImage( HEATER_ELEMENT, HEATER_ELEMENT_OFFSET );

    private static final double ENERGY_TO_FULLY_ACTIVATE = 50; // In joules/sec, a.k.a. Watts.

    // Proportion, from 0 to 1, of the heat being generated.
    public final Property<Double> heatProportion = new Property<Double>( 0.0 );

    protected BeakerHeater() {
        super( EnergyFormsAndChangesResources.Images.WATER_ICON );
    }

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {
        if ( isActive() && incomingEnergy.type == EnergyType.ELECTRICAL ) {
            heatProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / ENERGY_TO_FULLY_ACTIVATE, 1 ) );
        }
        else {
            heatProportion.set( 0.0 );
        }
    }

    @Override public void injectEnergyChunks( List<EnergyChunk> energyChunks ) {
        // TODO
    }

    @Override public void deactivate() {
        super.deactivate();
        heatProportion.set( 0.0 );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectBeakerHeaterButton;
    }
}
