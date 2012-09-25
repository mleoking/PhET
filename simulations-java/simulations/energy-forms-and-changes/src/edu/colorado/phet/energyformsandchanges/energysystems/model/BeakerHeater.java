// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
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

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Images used to represent this class in the view.
    private static final Vector2D HEATER_ELEMENT_OFFSET = new Vector2D( -0.002, 0.022 );
    public static final ModelElementImage WIRE_STRAIGHT_IMAGE = new ModelElementImage( WIRE_BLACK_86, new Vector2D( -0.042, -0.04 ) );
    public static final ModelElementImage WIRE_CURVE_IMAGE = new ModelElementImage( WIRE_BLACK_RIGHT, new Vector2D( -0.009, -0.016 ) );
    public static final ModelElementImage ELEMENT_BASE_IMAGE = new ModelElementImage( ELEMENT_BASE, new Vector2D( 0, 0 ) );
    public static final ModelElementImage HEATER_ELEMENT_OFF_IMAGE = new ModelElementImage( HEATER_ELEMENT_OFF, HEATER_ELEMENT_OFFSET );
    public static final ModelElementImage HEATER_ELEMENT_ON_IMAGE = new ModelElementImage( HEATER_ELEMENT, HEATER_ELEMENT_OFFSET );

    // Offsets need for creating the path followed by the energy chunks.  These
    // were empirically determined based on images, will need to change if the
    // images are changed.
    private static final Vector2D OFFSET_TO_LEFT_SIDE_OF_WIRE_BEND = new Vector2D( -0.02, -0.04 );
    private static final Vector2D OFFSET_TO_FIRST_WIRE_CURVE_POINT = new Vector2D( -0.01, -0.0375 );
    private static final Vector2D OFFSET_TO_SECOND_WIRE_CURVE_POINT = new Vector2D( -0.001, -0.025 );
    private static final Vector2D OFFSET_TO_THIRD_WIRE_CURVE_POINT = new Vector2D( -0.0005, -0.0175 );
    private static final Vector2D OFFSET_TO_BOTTOM_OF_CONNECTOR = new Vector2D( 0, -0.01 );
    private static final Vector2D OFFSET_TO_RADIATE_POINT = new Vector2D( 0, 0.02 );

    private static final double RADIATED_ENERGY_CHUNK_MAX_DISTANCE = 0.5;

    private static final Random RAND = new Random();

    private static final double ENERGY_TO_FULLY_ACTIVATE = 50; // In joules/sec, a.k.a. Watts.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Proportion, from 0 to 1, of the heat being generated.
    public final Property<Double> heatProportion = new Property<Double>( 0.0 );

    private List<EnergyChunkPathMover> electricalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> thermalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected BeakerHeater() {
        super( EnergyFormsAndChangesResources.Images.WATER_ICON );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {

        // Handle any incoming energy chunks.
        if ( !incomingEnergyChunks.isEmpty() ) {
            for ( EnergyChunk incomingEnergyChunk : incomingEnergyChunks ) {
                if ( incomingEnergyChunk.energyType.get() == EnergyType.ELECTRICAL ) {
                    // Add the energy chunk to the list of those under management.
                    energyChunkList.add( incomingEnergyChunk );

                    // And a "mover" that will move this energy chunk through
                    // the wire to the bulb.
                    electricalEnergyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk, getEnergyChunkPath( getPosition() ), EFACConstants.ELECTRICAL_ENERGY_CHUNK_VELOCITY ) );
                }
                else {
                    // By design, this shouldn't happen, so warn if it does.
                    System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                }
            }
            incomingEnergyChunks.clear();
        }

        // Move the electrical energy chunks that are currently under management.
        for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( electricalEnergyChunkMovers ) ) {
            energyChunkMover.moveAlongPath( dt );
            if ( energyChunkMover.isPathFullyTraversed() ) {
                electricalEnergyChunkMovers.remove( energyChunkMover );
                // Cause this energy chunk to be radiated from the bulb.
                energyChunkMover.energyChunk.energyType.set( EnergyType.THERMAL );
                List<Vector2D> lightPath = new ArrayList<Vector2D>() {{
                    add( getPosition().plus( OFFSET_TO_RADIATE_POINT ).plus( new Vector2D( RADIATED_ENERGY_CHUNK_MAX_DISTANCE, 0 ).getRotatedInstance( RAND.nextDouble() * 2 * Math.PI ) ) );
                }};
                thermalEnergyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk, lightPath, EFACConstants.LIGHT_ENERGY_CHUNK_VELOCITY ) );
            }
        }

        // Move the thermal energy chunks.
        for ( EnergyChunkPathMover lightEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( thermalEnergyChunkMovers ) ) {
            lightEnergyChunkMover.moveAlongPath( dt );
            if ( lightEnergyChunkMover.isPathFullyTraversed() ) {
                // Remove the chunk and its mover.
                energyChunkList.remove( lightEnergyChunkMover.energyChunk );
                thermalEnergyChunkMovers.remove( lightEnergyChunkMover );
            }
        }

        if ( isActive() && incomingEnergy.type == EnergyType.ELECTRICAL ) {
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

    private static List<Vector2D> getEnergyChunkPath( final Vector2D centerPosition ) {
        return new ArrayList<Vector2D>() {{
            add( centerPosition.plus( OFFSET_TO_LEFT_SIDE_OF_WIRE_BEND ) );
            add( centerPosition.plus( OFFSET_TO_FIRST_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_SECOND_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_THIRD_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_BOTTOM_OF_CONNECTOR ) );
            add( centerPosition.plus( OFFSET_TO_RADIATE_POINT ) );
        }};
    }
}
