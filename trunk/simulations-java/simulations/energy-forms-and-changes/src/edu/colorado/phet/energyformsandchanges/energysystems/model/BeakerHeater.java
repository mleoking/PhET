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
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Beaker;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.colorado.phet.energyformsandchanges.common.model.ITemperatureModel;
import edu.colorado.phet.energyformsandchanges.common.model.Thermometer;
import edu.colorado.phet.energyformsandchanges.intro.model.HeatTransferConstants;
import edu.colorado.phet.energyformsandchanges.intro.model.TemperatureAndColor;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;
import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.BOILING_POINT_TEMPERATURE;
import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.ROOM_TEMPERATURE;

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
    public static final ModelElementImage WIRE_STRAIGHT_IMAGE = new ModelElementImage( WIRE_BLACK_62, new Vector2D( -0.036, -0.04 ) );
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
    private static final double BEAKER_WIDTH = 0.075; // In meters.
    private static final double BEAKER_HEIGHT = BEAKER_WIDTH * 0.9;
    private static final Vector2D BEAKER_OFFSET = new Vector2D( 0, 0.025 );
    private static final Vector2D THERMOMETER_OFFSET = new Vector2D( 0.033, 0.035 );
    private static final double HEATING_ELEMENT_ENERGY_CHUNK_VELOCITY = 0.0075; // In meters/sec, quite slow.
    private static final double HEATER_ELEMENT_2D_HEIGHT = HEATER_ELEMENT_OFF_IMAGE.getHeight();
    private static final double MAX_HEAT_GENERATION_RATE = 3000; // Joules/sec, not connected to incoming energy.
    private static final double RADIATED_ENERGY_CHUNK_TRAVEL_DISTANCE = 0.2; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Proportion, from 0 to 1, of the max possible heat being generated by heater element.
    public final Property<Double> heatProportion = new Property<Double>( 0.0 );

    private List<EnergyChunkPathMover> wireEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> heatingElementEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> radiatedEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    public Beaker beaker;
    public Thermometer thermometer;

    // Separate list for the radiated energy chunks so that they can be
    // handled a little differently in the view.
    public final ObservableList<EnergyChunk> radiatedEnergyChunkList = new ObservableList<EnergyChunk>();

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
                        wireEnergyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk, createElectricalEnergyChunkPath( getPosition() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    }
                    else {
                        // By design, this shouldn't happen, so warn if it does.
                        System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                    }
                }
                incomingEnergyChunks.clear();
            }

            // Move the electrical energy chunks.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( wireEnergyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {

                    // The electrical energy chunk has reached the burner, so
                    // it needs to change into thermal energy.
                    wireEnergyChunkMovers.remove( energyChunkMover );
                    energyChunkMover.energyChunk.energyType.set( EnergyType.THERMAL );

                    // Have the thermal energy move a little on the element
                    // before moving into the beaker.
                    heatingElementEnergyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                                   createHeaterElementEnergyChunkPath( energyChunkMover.energyChunk.position.get() ),
                                                                                   HEATING_ELEMENT_ENERGY_CHUNK_VELOCITY ) );
                }
            }

            // Move the thermal energy chunks.
            for ( EnergyChunkPathMover thermalEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( heatingElementEnergyChunkMovers ) ) {
                thermalEnergyChunkMover.moveAlongPath( dt );
                if ( thermalEnergyChunkMover.isPathFullyTraversed() ) {
                    // The chunk is ready to move to the beaker.  We remove it
                    // from here, and the beaker takes over management of the
                    // chunk.
                    beaker.addEnergyChunk( thermalEnergyChunkMover.energyChunk );
                    energyChunkList.remove( thermalEnergyChunkMover.energyChunk );
                    heatingElementEnergyChunkMovers.remove( thermalEnergyChunkMover );
                }
            }

            // Set the proportion of max heat being generated by the heater element.
            System.out.println( "incomingEnergy = " + incomingEnergy.amount );
            if ( incomingEnergy.type == EnergyType.ELECTRICAL ) {
                heatProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / ( EFACConstants.MAX_ENERGY_RATE * dt ), 1 ) );
            }
            else {
                heatProportion.set( 0.0 );
            }

            // Add energy to the beaker based on heat coming from heat element.
            beaker.changeEnergy( heatProportion.get() * MAX_HEAT_GENERATION_RATE * dt );

            // Remove energy from the beaker based on loss of heat to the
            // surrounding air.
            double temperatureGradient = beaker.getTemperature() - ROOM_TEMPERATURE;
            if ( Math.abs( temperatureGradient ) > EFACConstants.TEMPERATURES_EQUAL_THRESHOLD ) {
                double halfwayPoint = ROOM_TEMPERATURE + ( BOILING_POINT_TEMPERATURE - ROOM_TEMPERATURE ) / 2;
                double heatExchangeConstant = HeatTransferConstants.WATER_AIR_HEAT_TRANSFER_FACTOR.get();
                if ( beaker.getTemperature() > halfwayPoint ) {
                    // Prevent the beaker from ever reaching the boiling point
                    // by asymptotically increasing the heat exchange rate as
                    // the temperature approaches boiling.
                    double adder = BOILING_POINT_TEMPERATURE * ( ( 1 / ( BOILING_POINT_TEMPERATURE - beaker.getTemperature() ) - ( 1 / ( BOILING_POINT_TEMPERATURE - halfwayPoint ) ) ) );
                    heatExchangeConstant += adder;
                }
                double thermalContactArea = ( beaker.getRawOutlineRect().getWidth() * 2 ) + ( beaker.getRawOutlineRect().getHeight() * 2 ) * beaker.fluidLevel.get();
                double thermalEnergyLost = temperatureGradient * heatExchangeConstant * thermalContactArea * dt;
                beaker.changeEnergy( -thermalEnergyLost );
            }

            if ( beaker.getEnergyChunkBalance() > 0 ) {
                // Remove an energy chunk from the beaker and start it floating
                // away, a.k.a. make it "radiate".
                Vector2D extractionPoint = new Vector2D( beaker.getRect().getMinX() + RAND.nextDouble() * beaker.getRect().getWidth(),
                                                         beaker.getRect().getMinY() + RAND.nextDouble() * ( beaker.getRect().getHeight() * beaker.fluidLevel.get() ) );
                EnergyChunk ec = beaker.extractClosestEnergyChunk( extractionPoint );
                ec.zPosition.set( 0.0 ); // Move to front of z order.
                radiatedEnergyChunkList.add( ec );
                radiatedEnergyChunkMovers.add( new EnergyChunkPathMover( ec, createRadiatedEnergyChunkPath( ec.position.get() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
            }

            // Move the radiated energy chunks.
            for ( EnergyChunkPathMover radiatedEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( radiatedEnergyChunkMovers ) ) {
                radiatedEnergyChunkMover.moveAlongPath( dt );
                if ( radiatedEnergyChunkMover.isPathFullyTraversed() ) {
                    // Remove this energy chunk entirely.
                    radiatedEnergyChunkList.remove( radiatedEnergyChunkMover.energyChunk );
                    radiatedEnergyChunkMovers.remove( radiatedEnergyChunkMover );
                }
            }
        }
    }

    @Override public void deactivate() {
        super.deactivate();
        heatProportion.set( 0.0 );
        beaker.reset();
        beaker.position.set( getPosition().plus( BEAKER_OFFSET ) );
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        wireEnergyChunkMovers.clear();
        heatingElementEnergyChunkMovers.clear();
        radiatedEnergyChunkMovers.clear();
        radiatedEnergyChunkList.clear();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectBeakerHeaterButton;
    }

    private static List<Vector2D> createHeaterElementEnergyChunkPath( final Vector2D startPosition ) {
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

    private static List<Vector2D> createRadiatedEnergyChunkPath( final Vector2D startPosition ) {
        int numDirectionChanges = 4; // Empirically chosen.
        Vector2D nominalTravelVector = new Vector2D( 0, RADIATED_ENERGY_CHUNK_TRAVEL_DISTANCE / numDirectionChanges );
        ArrayList<Vector2D> pathPoints = new ArrayList<Vector2D>(  );

        // The first point is straight above the start point.  This just looks
        // good, making the chunk move straight up out of the beaker.
        Vector2D currentPosition = startPosition.plus( nominalTravelVector );
        pathPoints.add( currentPosition );

        // Add the remaining points in the path.
        for ( int i = 0; i < numDirectionChanges - 1; i++ ){
            Vector2D movement = nominalTravelVector.getRotatedInstance( (RAND.nextDouble() - 0.5) * Math.PI / 4 );
            currentPosition = currentPosition.plus( movement );
            pathPoints.add( currentPosition );
        }
        return pathPoints;
    }
}
