// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

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

    // Attributes of the wheel and generator.
    private static final double WHEEL_MOMENT_OF_INERTIA = 5; // In kg.
    private static final double RESISTANCE_CONSTANT = 5; // Controls max speed and rate of slow down, empirically determined.
    private static final double DIRECT_COUPLING_ENERGY_TO_ROTATION_CONSTANT = 0.2;

    // Images used to represent this model element in the view.
    public static final ModelElementImage HOUSING_IMAGE = new ModelElementImage( GENERATOR, new Vector2D( 0, 0 ) );
    public static final Vector2D WHEEL_CENTER_OFFSET = new Vector2D( 0, 0.03 );
    public static final ModelElementImage WHEEL_PADDLES_IMAGE = new ModelElementImage( GENERATOR_WHEEL_PADDLES_SHORT, WHEEL_CENTER_OFFSET );
    public static final ModelElementImage WHEEL_TEXTURE_IMAGE = new ModelElementImage( GENERATOR_WHEEL_TEXTURE, WHEEL_CENTER_OFFSET );
    public static final ModelElementImage WHEEL_HUB_IMAGE = new ModelElementImage( GENERATOR_WHEEL_HUB, WHEEL_CENTER_OFFSET );
    public static final ModelElementImage CONNECTOR_IMAGE = new ModelElementImage( CONNECTOR, new Vector2D( 0.058, -0.04 ) ); // Offset empirically determined for optimal look.
    public static final ModelElementImage WIRE_CURVED_IMAGE = new ModelElementImage( WIRE_BLACK_LEFT, new Vector2D( 0.0185, -0.015 ) ); // Offset empirically determined for optimal look.
    private static final double WHEEL_RADIUS = WHEEL_HUB_IMAGE.getWidth() / 2;

    // Offsets used to create the paths followed by the energy chunks.
    private static final Vector2D START_OF_WIRE_CURVE_OFFSET = WHEEL_CENTER_OFFSET.plus( 0.01, -0.05 );
    private static final Vector2D WIRE_CURVE_POINT_1_OFFSET = WHEEL_CENTER_OFFSET.plus( 0.015, -0.06 );
    private static final Vector2D WIRE_CURVE_POINT_2_OFFSET = WHEEL_CENTER_OFFSET.plus( 0.03, -0.07 );
    private static final Vector2D END_OF_WIRE_OFFSET = WHEEL_CENTER_OFFSET.plus( 0.07, -0.07 );

    private static final double ENERGY_OUTPUT_RATE = 10; // In joules / (radians / sec)

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private Property<Double> wheelRotationalAngle = new Property<Double>( 0.0 ); // In radians.
    private double wheelRotationalVelocity = 0; // In radians/s.
    private List<EnergyChunkPathMover> mechanicalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> electricalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    // Flag that controls "direct coupling mode", which means that the
    // generator wheel turns at a rate that is directly proportionate to the
    // incoming energy, with no rotational inertia.
    public BooleanProperty directCouplingMode = new BooleanProperty( false );

    // The electrical energy chunks are kept on a separate list to support
    // placing them on a different layer in the view.
    public ObservableList<EnergyChunk> electricalEnergyChunks = new ObservableList<EnergyChunk>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public WaterPoweredGenerator() {
        super( EnergyFormsAndChangesResources.Images.GENERATOR_ICON );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {
        if ( isActive() ) {

            // Handle different wheel rotation modes.
            if ( directCouplingMode.get() ) {
                // Treat the wheel as though it is directly coupled to the
                // energy source, e.g. through a belt or drive shaft.
                if ( incomingEnergy.type == EnergyType.MECHANICAL ) {
                    wheelRotationalVelocity = incomingEnergy.amount * DIRECT_COUPLING_ENERGY_TO_ROTATION_CONSTANT * ( Math.sin( incomingEnergy.direction ) > 0 ? -1 : 1 ); // Convention is positive is counter clockwise.
                    wheelRotationalAngle.set( wheelRotationalAngle.get() + wheelRotationalVelocity * dt );
                }
            }
            else {
                // Treat the wheel like it is being moved from an external
                // energy, such as water, and has inertia.
                double torqueFromIncomingEnergy = 0;
                if ( incomingEnergy.type == EnergyType.MECHANICAL ) {
                    torqueFromIncomingEnergy = incomingEnergy.amount * WHEEL_RADIUS * 30 * ( Math.sin( incomingEnergy.direction ) > 0 ? -1 : 1 ); // Convention is positive is counter clockwise.
                }
                double torqueFromResistance = -wheelRotationalVelocity * RESISTANCE_CONSTANT;
                double angularAcceleration = ( torqueFromIncomingEnergy + torqueFromResistance ) / WHEEL_MOMENT_OF_INERTIA;
                wheelRotationalVelocity += angularAcceleration * dt;
                wheelRotationalAngle.set( wheelRotationalAngle.get() + wheelRotationalVelocity * dt );
            }

            // Handle any incoming energy chunks.
            if ( !incomingEnergyChunks.isEmpty() ) {
                for ( EnergyChunk incomingEnergyChunk : new ArrayList<EnergyChunk>( incomingEnergyChunks ) ) {
                    if ( incomingEnergyChunk.energyType.get() == EnergyType.MECHANICAL ) {

                        energyChunkList.add( incomingEnergyChunk );
                        incomingEnergyChunks.remove( incomingEnergyChunk );

                        // And a "mover" that will move this energy chunk to
                        // the center of the wheel.
                        mechanicalEnergyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk,
                                                                                   createMechanicalEnergyChunkPath( getPosition() ),
                                                                                   EFACConstants.ELECTRICAL_ENERGY_CHUNK_VELOCITY ) );
                    }
                    else {
                        // By design, this shouldn't happen, so warn if it does.
                        System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                    }
                }
                incomingEnergyChunks.clear();
            }

            // Move the mechanical energy chunks and update their state.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( mechanicalEnergyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    // The mechanical energy chunk has traveled to the end of
                    // its path, so change it to electrical and send it on its
                    // way.
                    mechanicalEnergyChunkMovers.remove( energyChunkMover );
                    energyChunkList.remove( energyChunkMover.energyChunk );
                    energyChunkMover.energyChunk.energyType.set( EnergyType.ELECTRICAL );
                    electricalEnergyChunks.add( energyChunkMover.energyChunk );
                    electricalEnergyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                               createElectricalEnergyChunkPath( getPosition() ),
                                                                               EFACConstants.ELECTRICAL_ENERGY_CHUNK_VELOCITY ) );
                }
            }

            // Move the electrical energy chunks and update their state.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( electricalEnergyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    // The electrical energy chunk has traveled to the end of
                    // its path, so transfer it to the next energy system.
                    electricalEnergyChunkMovers.remove( energyChunkMover );
                    outgoingEnergyChunks.add( energyChunkMover.energyChunk );
                }
            }
        }

        // Produce the appropriate amount of energy.
        return new Energy( EnergyType.ELECTRICAL, Math.abs( wheelRotationalVelocity * ENERGY_OUTPUT_RATE ) );
    }

    private static List<Vector2D> createMechanicalEnergyChunkPath( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( WHEEL_CENTER_OFFSET ) );
        }};
    }

    private static List<Vector2D> createElectricalEnergyChunkPath( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( START_OF_WIRE_CURVE_OFFSET ) );
            add( panelPosition.plus( WIRE_CURVE_POINT_1_OFFSET ) );
            add( panelPosition.plus( WIRE_CURVE_POINT_2_OFFSET ) );
            add( panelPosition.plus( END_OF_WIRE_OFFSET ) );
        }};
    }

    @Override public void deactivate() {
        super.deactivate();
        electricalEnergyChunks.clear();
        electricalEnergyChunkMovers.clear();
        mechanicalEnergyChunkMovers.clear();
        wheelRotationalVelocity = 0;
    }

    public ObservableProperty<Double> getWheelRotationalAngle() {
        return wheelRotationalAngle;
    }

    // Have to override, since outgoing chunks are on a separate list.
    @Override public List<EnergyChunk> extractOutgoingEnergyChunks() {
        List<EnergyChunk> retVal = new ArrayList<EnergyChunk>( outgoingEnergyChunks );
        electricalEnergyChunks.removeAll( outgoingEnergyChunks );
        outgoingEnergyChunks.clear();
        return retVal;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
