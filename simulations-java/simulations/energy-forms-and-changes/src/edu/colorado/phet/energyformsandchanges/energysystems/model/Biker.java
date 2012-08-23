// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * This class represents a bicycle being peddled by a rider in order to
 * generate energy.
 *
 * @author John Blanco
 */
public class Biker extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double MAX_ANGULAR_VELOCITY_OF_CRANK = 3 * Math.PI; // In radians/sec.
    private static final double ANGULAR_ACCELERATION = Math.PI; // In radians/(sec^2).
    // TODO: This is temp until we figure out how much it should really put out.
    private static final double MAX_ENERGY_OUTPUT_RATE = 10; // In joules / (radians / sec)
    private static final double CRANK_TO_REAR_WHEEL_RATIO = 1;

    // Offset of the bike frame center.  Most other image offsets are relative
    // to this one.
    private static final Vector2D FRAME_CENTER_OFFSET = new Vector2D( 0.0, 0.01 );

    // Images used when representing this model element in the view.  The
    // offsets, which are in meters, were empirically determined.  The values
    // aren't really to scale, since there are so many things in this model
    // with very different scales.
    public static final ModelElementImage FRAME_IMAGE = new ModelElementImage( BICYCLE_FRAME_2, FRAME_CENTER_OFFSET );
    public static final ModelElementImage REAR_WHEEL_SPOKES_IMAGE = new ModelElementImage( BICYCLE_SPOKES, FRAME_CENTER_OFFSET.plus( new Vector2D( 0.035, -0.020 ) ) );
    public static final ModelElementImage RIDER_UPPER_BODY_IMAGE = new ModelElementImage( BICYCLE_RIDER, FRAME_CENTER_OFFSET.plus( new Vector2D( -0.0025, 0.062 ) ) );

    private static final Vector2D LEG_IMAGE_OFFSET = new Vector2D( 0.009, 0.002 );
    public static final List<ModelElementImage> FRONT_LEG_IMAGES = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( FRONT_LEG_1, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_2, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_3, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_4, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_5, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_6, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_7, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_8, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_9, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_10, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_11, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_12, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
    }};

    public static final List<ModelElementImage> BACK_LEG_IMAGES = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( BACK_LEG_1, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_2, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_3, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_4, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_5, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_6, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_7, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_8, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_9, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_10, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_11, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_12, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
    }};

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( RIDER_UPPER_BODY_IMAGE );
        add( REAR_WHEEL_SPOKES_IMAGE );
        add( FRAME_IMAGE );
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private Property<Double> crankAngle = new Property<Double>( 0.0 ); // In radians.
    private Property<Double> rearWheelAngle = new Property<Double>( 0.0 ); // In radians.
    private double crankAngularVelocity = 0; // In radians/s.

    // Property through which the target pedaling rate is set.
    public Property<Double> targetCrankAngularVelocity = new Property<Double>( 0.0 ); // In radians/sec

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public Biker() {
        super( EnergyFormsAndChangesResources.Images.BICYCLE_ICON, IMAGE_LIST );

        // Monitor target rotation rate for validity.
        targetCrankAngularVelocity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double targetAngularVelocity ) {
                assert targetAngularVelocity >= 0 && targetAngularVelocity <= MAX_ANGULAR_VELOCITY_OF_CRANK;
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {
        if ( active ) {
            double angularVelocityDiffFromTarget = targetCrankAngularVelocity.get() - crankAngularVelocity;
            if ( angularVelocityDiffFromTarget != 0 ) {
                double change = ANGULAR_ACCELERATION * dt;
                if ( angularVelocityDiffFromTarget > 0 ) {
                    // Accelerate
                    crankAngularVelocity = Math.min( crankAngularVelocity + change, targetCrankAngularVelocity.get() );
                }
                else {
                    // Decelerate
                    crankAngularVelocity = Math.max( crankAngularVelocity - change, 0 );
                }
            }
            crankAngle.set( ( crankAngle.get() + crankAngularVelocity * dt ) % ( 2 * Math.PI ) );
            rearWheelAngle.set( ( rearWheelAngle.get() + crankAngularVelocity * dt * CRANK_TO_REAR_WHEEL_RATIO ) % ( 2 * Math.PI ) );
        }
        return new Energy( Energy.Type.MECHANICAL, Math.abs( crankAngularVelocity / MAX_ANGULAR_VELOCITY_OF_CRANK * MAX_ENERGY_OUTPUT_RATE ), -Math.PI / 2 );
    }

    @Override public void deactivate() {
        super.deactivate();
        crankAngularVelocity = 0;
    }

    @Override public void activate() {
        super.activate();
    }

    public ObservableProperty<Double> getRearWheelAngle() {
        return rearWheelAngle;
    }

    public Property<Double> getCrankAngle() {
        return crankAngle;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
