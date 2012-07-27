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

    public static final ModelElementImage BACKGROUND_IMAGE = new ModelElementImage( EnergyFormsAndChangesResources.Images.GENERATOR,
                                                                                    EnergyFormsAndChangesResources.Images.GENERATOR.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                                    new Vector2D( 0, 0 ) );
    public static final ModelElementImage WHEEL_IMAGE = new ModelElementImage( EnergyFormsAndChangesResources.Images.GENERATOR_WHEEL,
                                                                               EnergyFormsAndChangesResources.Images.GENERATOR_WHEEL.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                               new Vector2D( 0, 0 ) );

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( BACKGROUND_IMAGE );
        add( WHEEL_IMAGE );
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private boolean active = false;
    private Property<Double> wheelRotationalAngle = new Property<Double>( 0.0 ); // In radians.
    private double wheelRotationalVelocity = Math.PI / 2; // In radians/s.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public WaterPoweredGenerator() {
        super( EnergyFormsAndChangesResources.Images.GENERATOR_ICON, IMAGE_LIST );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public double stepInTime( double dt, Energy incomingEnergy ) {
        if ( active ) {
            wheelRotationalAngle.set( wheelRotationalAngle.get() + wheelRotationalVelocity * dt );
            System.out.println( "wheelRotationalAngle = " + wheelRotationalAngle.get() );
        }
        return 0;
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
