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

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.BICYCLE_FRAME;

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

    public static final ModelElementImage FRAME_IMAGE = new ModelElementImage( BICYCLE_FRAME,
                                                                               BICYCLE_FRAME.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                                                               new Vector2D( 0, 0 ) );

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
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
