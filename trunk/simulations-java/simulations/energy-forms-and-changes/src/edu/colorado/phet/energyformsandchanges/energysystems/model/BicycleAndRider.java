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

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.BICYCLE_FRAME;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.BICYCLE_REAR_WHEEL;

/**
 * This class represents a bicycle being peddled by a rider in order to
 * generate energy.
 *
 * @author John Blanco
 */
public class BicycleAndRider extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Vector2D FRAME_CENTER_OFFSET = new Vector2D( -0.01, 0.01 );

    // Offsets, which are in meters, were empirically determined.
    public static final ModelElementImage FRAME_IMAGE = new ModelElementImage( BICYCLE_FRAME, FRAME_CENTER_OFFSET );
    public static final ModelElementImage REAR_WHEEL_IMAGE = new ModelElementImage( BICYCLE_REAR_WHEEL, FRAME_CENTER_OFFSET.plus( new Vector2D( 0.053, -0.026 ) ) );

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( REAR_WHEEL_IMAGE );
        add( FRAME_IMAGE );
    }};

    private static final double ENERGY_OUTPUT_RATE = 10; // In joules / (radians / sec)

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private boolean active = false;
    private Property<Double> rearWheelRotationalAngle = new Property<Double>( 0.0 ); // In radians.
    private double rearWheelRotationalVelocity = 0; // In radians/s.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BicycleAndRider() {
        super( EnergyFormsAndChangesResources.Images.BICYCLE_ICON, IMAGE_LIST );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {
        if ( active ) {
            // TODO: Implement
        }
        return new Energy( Energy.Type.ELECTRICAL, Math.abs( rearWheelRotationalVelocity * ENERGY_OUTPUT_RATE ) );
    }

    @Override public void deactivate() {
        super.deactivate();
        rearWheelRotationalVelocity = 0;
    }

    public ObservableProperty<Double> getRearWheelRotationalAngle() {
        return rearWheelRotationalAngle;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
