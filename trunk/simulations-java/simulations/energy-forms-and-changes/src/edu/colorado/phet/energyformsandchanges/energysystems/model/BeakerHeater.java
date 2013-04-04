// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
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

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.ELEMENT_BASE_BACK;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.ELEMENT_BASE_FRONT;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.HEATER_ELEMENT;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.HEATER_ELEMENT_DARK;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.WIRE_BLACK_62;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.WIRE_BLACK_RIGHT;
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
    private static final Vector2D OFFSET_TO_LEFT_SIDE_OF_WIRE = new Vector2D( -0.04, -0.04 );
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
    private static final double MAX_HEAT_GENERATION_RATE = 5000; // Joules/sec, not connected to incoming energy.
    private static final double RADIATED_ENERGY_CHUNK_TRAVEL_DISTANCE = 0.2; // In meters.
    private static final double HEAT_ENERGY_CHANGE_RATE = 0.5; // In proportion per second.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Proportion, from 0 to 1, of the max possible heat being generated by heater element.
    public final Property<Double> heatProportion = new Property<Double>( 0.0 );

    private final List<EnergyChunkPathMover> electricalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private final List<EnergyChunkPathMover> heatingElementEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private final List<EnergyChunkPathMover> radiatedEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    public final Beaker beaker;
    public final Thermometer thermometer;
    private final ObservableProperty<Boolean> energyChunksVisible;

    // Separate list for the radiated energy chunks so that they can be
    // handled a little differently in the view.
    public final ObservableList<EnergyChunk> radiatedEnergyChunkList = new ObservableList<EnergyChunk>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected BeakerHeater( ConstantDtClock clock, BooleanProperty energyChunksVisible ) {
        super( EnergyFormsAndChangesResources.Images.WATER_ICON );
        this.energyChunksVisible = energyChunksVisible;

        beaker = new Beaker( clock, BEAKER_OFFSET, BEAKER_WIDTH, BEAKER_HEIGHT, energyChunksVisible );
        thermometer = new Thermometer( clock, new ITemperatureModel() {
            public TemperatureAndColor getTemperatureAndColorAtLocation( Vector2D location ) {
                return new TemperatureAndColor( beaker.getTemperature(), EFACConstants.WATER_COLOR_OPAQUE );
            }
        }, THERMOMETER_OFFSET, true );

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
                        // the wire to the heating element.
                        electricalEnergyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk,
                                                                                   createElectricalEnergyChunkPath( getPosition() ),
                                                                                   EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    }
                    else {
                        // By design, this shouldn't happen, so warn if it does.
                        System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                    }
                }
                incomingEnergyChunks.clear();
            }

            moveElectricalEnergyChunks( dt );
            moveThermalEnergyChunks( dt );

            // Set the proportion of max heat being generated by the heater element.
            if ( ( energyChunksVisible.get() && heatingElementEnergyChunkMovers.size() > 0 ) ||
                 ( !energyChunksVisible.get() && incomingEnergy.type == EnergyType.ELECTRICAL ) ) {
                heatProportion.set( Math.min( incomingEnergy.amount / ( EFACConstants.MAX_ENERGY_PRODUCTION_RATE * dt ),
                                              heatProportion.get() + HEAT_ENERGY_CHANGE_RATE * dt ) );
            }
            else {
                heatProportion.set( Math.max( 0, heatProportion.get() - HEAT_ENERGY_CHANGE_RATE * dt ) );
            }

            // Add energy to the beaker based on heat coming from heat element.
            beaker.changeEnergy( heatProportion.get() * MAX_HEAT_GENERATION_RATE * dt );

            // Remove energy from the beaker based on loss of heat to the
            // surrounding air.
            double temperatureGradient = beaker.getTemperature() - ROOM_TEMPERATURE;
            if ( Math.abs( temperatureGradient ) > EFACConstants.TEMPERATURES_EQUAL_THRESHOLD ) {
                double thermalContactArea = ( beaker.getRawOutlineRect().getWidth() * 2 ) + ( beaker.getRawOutlineRect().getHeight() * 2 ) * beaker.fluidLevel.get();
                double thermalEnergyLost = temperatureGradient * HeatTransferConstants.WATER_AIR_HEAT_TRANSFER_FACTOR.get() * thermalContactArea * dt;
                beaker.changeEnergy( -thermalEnergyLost );
                if ( beaker.getEnergyBeyondMaxTemperature() > 0 ) {
                    // Prevent the water from going beyond the boiling point.
                    beaker.changeEnergy( -beaker.getEnergyBeyondMaxTemperature() );
                }
            }

            if ( beaker.getEnergyChunkBalance() > 0 ) {
                // Remove an energy chunk from the beaker and start it floating
                // away, a.k.a. make it "radiate".
                Vector2D extractionPoint = new Vector2D( beaker.getRect().getMinX() + RAND.nextDouble() * beaker.getRect().getWidth(),
                                                         beaker.getRect().getMinY() + RAND.nextDouble() * ( beaker.getRect().getHeight() * beaker.fluidLevel.get() ) );
                EnergyChunk ec = beaker.extractClosestEnergyChunk( extractionPoint );
                if ( ec != null ) {
                    ec.zPosition.set( 0.0 ); // Move to front of z order.
                    radiatedEnergyChunkList.add( ec );
                    radiatedEnergyChunkMovers.add( new EnergyChunkPathMover( ec, createRadiatedEnergyChunkPath( ec.position.get() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                }
            }

            moveRadiatedEnergyChunks( dt );
        }
    }

    private void moveRadiatedEnergyChunks( double dt ) {
        for ( EnergyChunkPathMover radiatedEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( radiatedEnergyChunkMovers ) ) {
            radiatedEnergyChunkMover.moveAlongPath( dt );
            if ( radiatedEnergyChunkMover.isPathFullyTraversed() ) {
                // Remove this energy chunk entirely.
                radiatedEnergyChunkList.remove( radiatedEnergyChunkMover.energyChunk );
                radiatedEnergyChunkMovers.remove( radiatedEnergyChunkMover );
            }
        }
    }

    private void moveThermalEnergyChunks( double dt ) {
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
    }

    private void moveElectricalEnergyChunks( double dt ) {
        for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( electricalEnergyChunkMovers ) ) {
            energyChunkMover.moveAlongPath( dt );
            if ( energyChunkMover.isPathFullyTraversed() ) {

                // The electrical energy chunk has reached the burner, so
                // it needs to change into thermal energy.
                electricalEnergyChunkMovers.remove( energyChunkMover );
                energyChunkMover.energyChunk.energyType.set( EnergyType.THERMAL );

                // Have the thermal energy move a little on the element
                // before moving into the beaker.
                heatingElementEnergyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                               createHeaterElementEnergyChunkPath( energyChunkMover.energyChunk.position.get() ),
                                                                               HEATING_ELEMENT_ENERGY_CHUNK_VELOCITY ) );
            }
        }
    }

    @Override public void preLoadEnergyChunks( Energy incomingEnergyRate ) {
        clearEnergyChunks();
        if ( incomingEnergyRate.amount == 0 || incomingEnergyRate.type != EnergyType.ELECTRICAL ) {
            // No energy chunk pre-loading needed.
            return;
        }

        double dt = 1 / EFACConstants.FRAMES_PER_SECOND;
        double energySinceLastChunk = EFACConstants.ENERGY_PER_CHUNK * 0.99;

        // Simulate energy chunks moving through the system.
        boolean preLoadComplete = false;
        while ( !preLoadComplete ) {
            energySinceLastChunk += incomingEnergyRate.amount * dt;

            // Determine if time to add a new chunk.
            if ( energySinceLastChunk >= EFACConstants.ENERGY_PER_CHUNK ) {
                EnergyChunk newEnergyChunk = new EnergyChunk( EnergyType.ELECTRICAL, getPosition().plus( OFFSET_TO_LEFT_SIDE_OF_WIRE ), energyChunksVisible );
                energyChunkList.add( newEnergyChunk );
                // Add a 'mover' for this energy chunk.
                // And a "mover" that will move this energy chunk through
                // the wire to the heating element.
                electricalEnergyChunkMovers.add( new EnergyChunkPathMover( newEnergyChunk,
                                                                           createElectricalEnergyChunkPath( getPosition() ),
                                                                           EFACConstants.ENERGY_CHUNK_VELOCITY ) );

                // Update energy since last chunk.
                energySinceLastChunk = energySinceLastChunk - EFACConstants.ENERGY_PER_CHUNK;
            }

            moveElectricalEnergyChunks( dt );

            if ( heatingElementEnergyChunkMovers.size() > 0 ) {
                // An energy chunks has made it to the heating element, which completes the preload.
                preLoadComplete = true;
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
        electricalEnergyChunkMovers.clear();
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
        ArrayList<Vector2D> pathPoints = new ArrayList<Vector2D>();

        // The first point is straight above the start point.  This just looks
        // good, making the chunk move straight up out of the beaker.
        Vector2D currentPosition = startPosition.plus( nominalTravelVector );
        pathPoints.add( currentPosition );

        // Add the remaining points in the path.
        for ( int i = 0; i < numDirectionChanges - 1; i++ ) {
            Vector2D movement = nominalTravelVector.getRotatedInstance( ( RAND.nextDouble() - 0.5 ) * Math.PI / 4 );
            currentPosition = currentPosition.plus( movement );
            pathPoints.add( currentPosition );
        }
        return pathPoints;
    }
}
