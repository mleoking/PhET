// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Beaker;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.colorado.phet.energyformsandchanges.common.model.ITemperatureModel;
import edu.colorado.phet.energyformsandchanges.common.model.Thermometer;
import edu.colorado.phet.energyformsandchanges.intro.model.TemperatureAndColor;

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
    public static final ModelElementImage ELEMENT_BASE_BACK_IMAGE = new ModelElementImage( ELEMENT_BASE_BACK, new Vector2D( 0, 0 ) );
    public static final ModelElementImage ELEMENT_BASE_FRONT_IMAGE = new ModelElementImage( ELEMENT_BASE_FRONT, new Vector2D( 0, 0.0005 ) );
    public static final ModelElementImage HEATER_ELEMENT_OFF_IMAGE = new ModelElementImage( HEATER_ELEMENT_DARK, HEATER_ELEMENT_OFFSET );
    public static final ModelElementImage HEATER_ELEMENT_ON_IMAGE = new ModelElementImage( HEATER_ELEMENT, HEATER_ELEMENT_OFFSET );

    // Offsets need for creating the path followed by the energy chunks.  These
    // were empirically determined based on images, will need to change if the
    // images are changed.
    private static final Vector2D OFFSET_TO_LEFT_SIDE_OF_WIRE_BEND = new Vector2D( -0.02, -0.04 );
    private static final Vector2D OFFSET_TO_FIRST_WIRE_CURVE_POINT = new Vector2D( -0.01, -0.0375 );
    private static final Vector2D OFFSET_TO_SECOND_WIRE_CURVE_POINT = new Vector2D( -0.001, -0.025 );
    private static final Vector2D OFFSET_TO_THIRD_WIRE_CURVE_POINT = new Vector2D( -0.0005, -0.0175 );
    private static final Vector2D OFFSET_TO_BOTTOM_OF_CONNECTOR = new Vector2D( 0, -0.01 );
    private static final Vector2D OFFSET_TO_CONVERSION_POINT = new Vector2D( 0, 0.012 );

    // Miscellaneous other constants.
    private static final Random RAND = new Random();
    private static final double ENERGY_TO_FULLY_ACTIVATE = 50; // In joules/sec, a.k.a. Watts.
    private static final double BEAKER_WIDTH = 0.075; // In meters.
    private static final double BEAKER_HEIGHT = BEAKER_WIDTH * 0.9;
    private static final Vector2D BEAKER_OFFSET = new Vector2D( 0, 0.025 );
    private static final Vector2D THERMOMETER_OFFSET = new Vector2D( 0.033, 0.035 );
    private static final double THERMAL_ENERGY_CHUNK_VELOCITY = 0.01; // In meters/sec, quite slow.
    private static final double HEATER_ELEMENT_2D_HEIGHT = HEATER_ELEMENT_OFF_IMAGE.getHeight();

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Proportion, from 0 to 1, of the heat being generated.
    public final Property<Double> heatProportion = new Property<Double>( 0.0 );

    private List<EnergyChunkPathMover> electricalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> thermalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    public Beaker beaker;
    public Thermometer thermometer;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected BeakerHeater( ConstantDtClock clock, BooleanProperty energyChunksVisible ) {
        super( EnergyFormsAndChangesResources.Images.WATER_ICON );

        beaker = new Beaker( clock, BEAKER_OFFSET, BEAKER_WIDTH, BEAKER_HEIGHT, energyChunksVisible );
        thermometer = new Thermometer( clock, new ITemperatureModel() {
            public TemperatureAndColor getTemperatureAndColorAtLocation( Vector2D location ) {
                return new TemperatureAndColor( beaker.getTemperature(), EFACConstants.WATER_COLOR_OPAQUE );
            }
        }, THERMOMETER_OFFSET );

        // Update the position of the beaker and thermometer when the overall
        // model element position changes.
        getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D position ) {
                beaker.position.set( position.plus( BEAKER_OFFSET ) );
                thermometer.position.set( position.plus( THERMOMETER_OFFSET ) );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {

        if ( isActive() ) {

            // Handle any incoming energy chunks.
            if ( !incomingEnergyChunks.isEmpty() ) {
                for ( EnergyChunk incomingEnergyChunk : incomingEnergyChunks ) {
                    if ( incomingEnergyChunk.energyType.get() == EnergyType.ELECTRICAL ) {
                        // Add the energy chunk to the list of those under management.
                        energyChunkList.add( incomingEnergyChunk );

                        // And a "mover" that will move this energy chunk through
                        // the wire to the bulb.
                        electricalEnergyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk, createElectricalEnergyChunkPath( getPosition() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
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

                    // The electrical energy chunk has reached the burner, so
                    // it needs to change into thermal energy.
                    electricalEnergyChunkMovers.remove( energyChunkMover );
                    energyChunkMover.energyChunk.energyType.set( EnergyType.THERMAL );

                    // Have the thermal energy move a little on the element
                    // before moving into the beaker.
                    thermalEnergyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                            createThermalEnergyChunkPath( energyChunkMover.energyChunk.position.get() ),
                                                                            THERMAL_ENERGY_CHUNK_VELOCITY ) );
                }
            }

            // Move the thermal energy chunks.
            for ( EnergyChunkPathMover thermalEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( thermalEnergyChunkMovers ) ) {
                thermalEnergyChunkMover.moveAlongPath( dt );
                if ( thermalEnergyChunkMover.isPathFullyTraversed() ) {
                    // The chunk is ready to move to the beaker.  We remove it
                    // from here, and the beaker takes over management of the
                    // chunk.
                    beaker.addEnergyChunk( thermalEnergyChunkMover.energyChunk );
                    energyChunkList.remove( thermalEnergyChunkMover.energyChunk );
                    thermalEnergyChunkMovers.remove( thermalEnergyChunkMover );
                }
            }

            if ( incomingEnergy.type == EnergyType.ELECTRICAL ) {
                heatProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / ENERGY_TO_FULLY_ACTIVATE, 1 ) );
            }
            else {
                heatProportion.set( 0.0 );
            }
        }
    }

    @Override public void deactivate() {
        super.deactivate();
        heatProportion.set( 0.0 );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectBeakerHeaterButton;
    }

    private static List<Vector2D> createThermalEnergyChunkPath( final Vector2D startPosition ) {
        return new ArrayList<Vector2D>() {{
            // The path for the thermal energy chunks is meant to look like it
            // is moving on the burner element.  This must be manually
            // coordinated with the burner element image.
            double angle = RAND.nextBoolean() ? RAND.nextDouble() * Math.PI * 0.45 : -RAND.nextDouble() * Math.PI * 0.3;
            add( startPosition.plus( new Vector2D( 0, HEATER_ELEMENT_2D_HEIGHT ).getRotatedInstance( angle ) ) );
        }};
    }

    private static List<Vector2D> createElectricalEnergyChunkPath( final Vector2D centerPosition ) {
        return new ArrayList<Vector2D>() {{
            add( centerPosition.plus( OFFSET_TO_LEFT_SIDE_OF_WIRE_BEND ) );
            add( centerPosition.plus( OFFSET_TO_FIRST_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_SECOND_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_THIRD_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_BOTTOM_OF_CONNECTOR ) );
            add( centerPosition.plus( OFFSET_TO_CONVERSION_POINT ) );
        }};
    }
}
