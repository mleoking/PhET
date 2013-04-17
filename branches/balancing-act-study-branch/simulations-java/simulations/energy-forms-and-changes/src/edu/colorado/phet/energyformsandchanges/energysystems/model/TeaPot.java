// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.TEAPOT_LARGE;
import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.ENERGY_PER_CHUNK;

/**
 * Class that represents the steam-generating tea pot in the model.
 *
 * @author John Blanco
 */
public class TeaPot extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Images used in representing this energy system element in the view.
    private static final Vector2D TEAPOT_OFFSET = new Vector2D( 0.0, 0.015 );
    public static final ModelElementImage TEAPOT_IMAGE = new ModelElementImage( TEAPOT_LARGE, TEAPOT_OFFSET );

    // Offsets and other constants used for energy paths.  These are mostly
    // empirically determined and coordinated with the image.
    private static final Vector2D SPOUT_BOTTOM_OFFSET = new Vector2D( 0.03, 0.02 );
    private static final Vector2D SPOUT_TIP_OFFSET = new Vector2D( 0.25, 0.3 );
    private static final Vector2D DISTANT_TARGET_OFFSET = new Vector2D( 1, 1 );
    private static final double WATER_SURFACE_HEIGHT_OFFSET = 0; // From teapot position, in meters.
    private static final double THERMAL_ENERGY_CHUNK_Y_ORIGIN = -0.05; // In meters, must be coordinated with heater position.
    private static final DoubleRange THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE = new DoubleRange( -0.015, 0.015 ); // In meters, must be coordinated with heater position.

    // Miscellaneous other constants.
    private static final double MAX_ENERGY_CHANGE_RATE = EFACConstants.MAX_ENERGY_PRODUCTION_RATE / 5; // In joules/second
    private static final double COOLING_CONSTANT = 0.1; // Controls rate at which tea pot cools down, empirically determined.
    private static final double COOL_DOWN_COMPLETE_THRESHOLD = 30; // In joules/second
    private static final DoubleRange ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE = new DoubleRange( 0.12, 0.15 );
    private static final Random RAND = new Random();
    private static final double ENERGY_CHUNK_WATER_TO_SPOUT_TIME = 0.7; // Used to keep chunks evenly spaced.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final Property<Double> heatCoolAmount = new Property<Double>( 0.0 );
    private final Property<Double> energyProductionRate = new Property<Double>( 0.0 );
    private double heatEnergyProducedSinceLastChunk = ENERGY_PER_CHUNK / 2;
    private final ObservableProperty<Boolean> energyChunksVisible;
    private final ObservableProperty<Boolean> steamPowerableElementInPlace;
    private final List<EnergyChunkPathMover> energyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    // List of chunks that are not being transferred to the next energy system
    // element.
    private final List<EnergyChunk> exemptFromTransferEnergyChunks = new ArrayList<EnergyChunk>();

    // Flag for whether next chunk should be transferred or kept, used to
    // alternate transfer with non-transfer.
    private boolean transferNextAvailableChunk = true;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public TeaPot( BooleanProperty energyChunksVisible, ObservableProperty<Boolean> steamPowerableElementInPlace ) {
        super( EnergyFormsAndChangesResources.Images.TEAPOT_ICON );
        this.energyChunksVisible = energyChunksVisible;
        this.steamPowerableElementInPlace = steamPowerableElementInPlace;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {
        if ( isActive() ) {
            if ( heatCoolAmount.get() > 0 || energyProductionRate.get() > COOL_DOWN_COMPLETE_THRESHOLD ) {
                // Calculate the energy production rate.
                double energyProductionIncreaseRate = heatCoolAmount.get() * MAX_ENERGY_CHANGE_RATE; // Analogous to acceleration.
                double energyProductionDecreaseRate = energyProductionRate.get() * COOLING_CONSTANT; // Analogous to friction.
                energyProductionRate.set( Math.min( energyProductionRate.get() + energyProductionIncreaseRate * dt - energyProductionDecreaseRate * dt,
                                                    EFACConstants.MAX_ENERGY_PRODUCTION_RATE ) ); // Analogous to velocity.
            }
            else {
                // Clamp the energy production rate to zero so that it doesn't
                // trickle on forever.
                energyProductionRate.set( 0.0 );
            }

            // See if it's time to emit a new energy chunk from the heater.
            heatEnergyProducedSinceLastChunk += Math.max( heatCoolAmount.get(), 0 ) * EFACConstants.MAX_ENERGY_PRODUCTION_RATE * dt;
            if ( heatEnergyProducedSinceLastChunk >= ENERGY_PER_CHUNK ) {
                // Emit a new thermal energy chunk.
                Vector2D initialPosition = new Vector2D( getPosition().getX() + THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE.getMin() + RAND.nextDouble() * THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE.getLength(),
                                                         getPosition().getY() + THERMAL_ENERGY_CHUNK_Y_ORIGIN );
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.THERMAL, initialPosition, energyChunksVisible );
                energyChunkList.add( energyChunk );
                heatEnergyProducedSinceLastChunk -= ENERGY_PER_CHUNK;
                energyChunkMovers.add( new EnergyChunkPathMover( energyChunk,
                                                                 createThermalEnergyChunkPath( initialPosition, getPosition() ),
                                                                 EFACConstants.ENERGY_CHUNK_VELOCITY ) );
            }

            // Move all energy chunks that are under this element's control.
            moveEnergyChunks( dt );
        }
        return new Energy( EnergyType.MECHANICAL, energyProductionRate.get() * dt, Math.PI / 2 );
    }

    private void moveEnergyChunks( double dt ) {
        for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
            energyChunkMover.moveAlongPath( dt );
            EnergyChunk energyChunk = energyChunkMover.energyChunk;
            if ( energyChunkMover.isPathFullyTraversed() ) {
                energyChunkMovers.remove( energyChunkMover );
                if ( energyChunk.energyType.get() == EnergyType.THERMAL && energyChunk.position.get().getY() == getPosition().getY() + WATER_SURFACE_HEIGHT_OFFSET ) {
                    // This is a thermal chunk that is coming out of the water.
                    if ( RAND.nextDouble() > 0.2 ) {
                        // Turn the chunk into mechanical energy.
                        energyChunk.energyType.set( EnergyType.MECHANICAL );
                    }
                    // Set this chunk on a path to the base of the spout.
                    double travelDistance = energyChunk.position.get().distance( getPosition().plus( SPOUT_BOTTOM_OFFSET ) );
                    energyChunkMovers.add( new EnergyChunkPathMover( energyChunk,
                                                                     createPathToSpoutBottom( getPosition() ),
                                                                     travelDistance / ENERGY_CHUNK_WATER_TO_SPOUT_TIME ) );
                }
                else if ( energyChunk.position.get().equals( getPosition().plus( SPOUT_BOTTOM_OFFSET ) ) ) {
                    // The chunk is moving out of the spout.
                    energyChunkMovers.add( new EnergyChunkPathMover( energyChunk,
                                                                     createSpoutExitPath( getPosition() ),
                                                                     EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                }
                else {
                    // This chunk is out of view, and we are done with it.
                    energyChunkList.remove( energyChunk );
                }
            }
            else {
                // See if this energy chunks should be transferred to the
                // next energy system.
                if ( energyChunk.energyType.get() == EnergyType.MECHANICAL &&
                     steamPowerableElementInPlace.get() &&
                     ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE.contains( getPosition().distance( energyChunk.position.get() ) ) &&
                     !exemptFromTransferEnergyChunks.contains( energyChunk ) ) {

                    if ( transferNextAvailableChunk ) {
                        // Send this chunk to the next energy system.
                        outgoingEnergyChunks.add( energyChunk );
                        energyChunkMovers.remove( energyChunkMover );

                        // Alternate sending or keeping chunks.
                        transferNextAvailableChunk = false;
                    }
                    else {
                        // Don't transfer this chunk.
                        exemptFromTransferEnergyChunks.add( energyChunk );

                        // Set up to transfer the next one.
                        transferNextAvailableChunk = true;
                    }
                }

            }
        }
    }

    @Override public void preLoadEnergyChunks() {
        clearEnergyChunks();
        if ( energyProductionRate.get() == 0 ) {
            // No chunks to add.
            return;
        }
        boolean preLoadComplete = false;
        double dt = 1 / EFACConstants.FRAMES_PER_SECOND;
        double energySinceLastChunk = EFACConstants.ENERGY_PER_CHUNK * 0.99;

        // Simulate energy chunks moving through the system.
        while ( !preLoadComplete ) {
            energySinceLastChunk += energyProductionRate.get() * dt;
            if ( energySinceLastChunk >= EFACConstants.ENERGY_PER_CHUNK ) {
                // Create a chunk inside the teapot (at the water surface).
                Vector2D initialPosition = new Vector2D( getPosition().getX(), getPosition().getY() + WATER_SURFACE_HEIGHT_OFFSET );
                EnergyType energyType = RAND.nextDouble() > 0.2 ? EnergyType.MECHANICAL : EnergyType.THERMAL;
                EnergyChunk newEnergyChunk = new EnergyChunk( energyType, initialPosition, energyChunksVisible );
                energyChunkList.add( newEnergyChunk );
                double travelDistance = newEnergyChunk.position.get().distance( getPosition().plus( SPOUT_BOTTOM_OFFSET ) );
                energyChunkMovers.add( new EnergyChunkPathMover( newEnergyChunk,
                                                                 createPathToSpoutBottom( getPosition() ),
                                                                 travelDistance / ENERGY_CHUNK_WATER_TO_SPOUT_TIME ) );
                energySinceLastChunk = energySinceLastChunk - EFACConstants.ENERGY_PER_CHUNK;
            }

            // Update energy chunk positions.
            moveEnergyChunks( dt );

            if ( outgoingEnergyChunks.size() > 0 ){
                // An energy chunk has traversed to the output of this system, completing the preload.
                preLoadComplete = true;
            }
        }
    }

    @Override public Energy getEnergyOutputRate() {
        return new Energy( EnergyType.MECHANICAL, energyProductionRate.get() );
    }

    private static List<Vector2D> createThermalEnergyChunkPath( final Vector2D startPosition, final Vector2D teapotPosition ) {
        return new ArrayList<Vector2D>() {{
            add( new Vector2D( startPosition.getX(), teapotPosition.getY() + WATER_SURFACE_HEIGHT_OFFSET ) );
        }};
    }

    private static List<Vector2D> createPathToSpoutBottom( final Vector2D parentElementPosition ) {
        return new ArrayList<Vector2D>() {{
            add( parentElementPosition.plus( SPOUT_BOTTOM_OFFSET ) );
        }};
    }

    private static List<Vector2D> createSpoutExitPath( final Vector2D parentElementPosition ) {
        return new ArrayList<Vector2D>() {{
            add( parentElementPosition.plus( SPOUT_TIP_OFFSET ) );
            add( parentElementPosition.plus( DISTANT_TARGET_OFFSET ) );
        }};
    }

    @Override public void deactivate() {
        super.deactivate();
        heatCoolAmount.reset();
        energyProductionRate.reset();
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        exemptFromTransferEnergyChunks.clear();
        energyChunkMovers.clear();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectTeapotButton;
    }

    public ObservableProperty<Double> getEnergyProductionRate() {
        return energyProductionRate;
    }
}
