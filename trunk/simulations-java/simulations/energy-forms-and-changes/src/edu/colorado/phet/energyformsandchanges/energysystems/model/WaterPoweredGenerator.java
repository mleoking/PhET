// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * This class represents an electrical generator that is powered by flowing
 * water or steam.
 *
 * @author John Blanco
 */
public class WaterPoweredGenerator extends EnergyConverter {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final ModelElementImage HOUSING_IMAGE = new ModelElementImage( GENERATOR,
                                                                                 GENERATOR.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                 new Vector2D( 0, 0 ) );
    public static final ModelElementImage WHEEL_IMAGE = new ModelElementImage( GENERATOR_WHEEL_LONG,
                                                                               GENERATOR_WHEEL_LONG.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                               new Vector2D( 0, 0.03 ) );
    public static final ModelElementImage CONNECTOR_IMAGE = new ModelElementImage( CONNECTOR,
                                                                                   CONNECTOR.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                   new Vector2D( 0.058, -0.04 ) ); // Offset empirically determined for optimal look.
    public static final ModelElementImage WIRE_CURVED_IMAGE = new ModelElementImage( WIRE_BLACK_LEFT,
                                                                                     WIRE_BLACK_LEFT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                     new Vector2D( 0.0185, -0.015 ) ); // Offset empirically determined for optimal look.
    public static final ModelElementImage WIRE_STRAIGHT_IMAGE = new ModelElementImage( WIRE_BLACK_MIDDLE,
                                                                                       WIRE_BLACK_MIDDLE.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                       new Vector2D( 0.075, -0.04 ) ); // Offset empirically determined for optimal look.
    private static final double WHEEL_RADIUS = WHEEL_IMAGE.getWidth() / 2;

    private static final double WHEEL_MOMENT_OF_INERTIA = 5; // In kg.

    private static final double RESISTANCE_CONSTANT = 5; // Controls max speed and rate of slow down, empirically determined.

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( WIRE_CURVED_IMAGE );
        add( WIRE_STRAIGHT_IMAGE );
        add( HOUSING_IMAGE );
        add( CONNECTOR_IMAGE );
        add( WHEEL_IMAGE );
    }};

    private static final double ENERGY_OUTPUT_RATE = 10; // In joules / (radians / sec)

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private boolean active = false;
    private Property<Double> wheelRotationalAngle = new Property<Double>( 0.0 ); // In radians.
    private double wheelRotationalVelocity = 0; // In radians/s.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public WaterPoweredGenerator() {
        super( EnergyFormsAndChangesResources.Images.GENERATOR_ICON, IMAGE_LIST );

    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {
        if ( active ) {
            double torqueFromIncomingEnergy = 0;
            if ( incomingEnergy.type == Energy.Type.MECHANICAL ) {
                torqueFromIncomingEnergy = incomingEnergy.amount * WHEEL_RADIUS * 30 * ( Math.sin( incomingEnergy.direction ) > 0 ? 1 : -1 );
            }
            double torqueFromResistance = -wheelRotationalVelocity * RESISTANCE_CONSTANT;
            double angularAcceleration = ( torqueFromIncomingEnergy + torqueFromResistance ) / WHEEL_MOMENT_OF_INERTIA;
            wheelRotationalVelocity += angularAcceleration * dt;
            wheelRotationalAngle.set( wheelRotationalAngle.get() + wheelRotationalVelocity * dt );
        }
        return new Energy( Energy.Type.ELECTRICAL, Math.abs( wheelRotationalVelocity * ENERGY_OUTPUT_RATE ) );
    }

    @Override public void activate() {
        active = true;
    }

    @Override public void deactivate() {
        active = false;
        wheelRotationalVelocity = 0;
    }

    public ObservableProperty<Double> getWheelRotationalAngle() {
        return wheelRotationalAngle;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
