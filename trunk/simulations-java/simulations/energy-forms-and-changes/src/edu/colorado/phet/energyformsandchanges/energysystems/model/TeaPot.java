// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.geom.Rectangle2D;
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
    public static final Vector2D TEAPOT_OFFSET = new Vector2D( 0.0, 0.015 );
    public static final ModelElementImage TEAPOT_IMAGE = new ModelElementImage( TEAPOT_LARGE, TEAPOT_OFFSET );

    // Offsets and other constants used for energy paths.  These are mostly
    // empirically determined and coordinated with the image.
    private static final Vector2D SPOUT_BOTTOM_OFFSET = new Vector2D( 0.03, 0.02 );
    private static final Vector2D SPOUT_TIP_OFFSET = new Vector2D( 0.25, 0.3 );
    private static final Vector2D DISTANT_TARGET_OFFSET = new Vector2D( 1, 1 );
    private static final double THERMAL_ENERGY_CHUNK_TRAVEL_DISTANCE = 0.05; // In meters.
    private static final double THERMAL_ENERGY_CHUNK_Y_ORIGIN = -0.05; // In meters, must be coordinated with heater position.
    private static final DoubleRange THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE = new DoubleRange( -0.015, 0.015 ); // In meters, must be coordinated with heater position.

    // Miscellaneous other constants.
    public static final double MAX_ENERGY_PRODUCTION_RATE = 200; // In joules/second
    private static final double MAX_ENERGY_CHANGE_RATE = 20; // In joules/second
    private static final double COOLING_CONSTANT = 0.1; // Controls rate at which tea pot cools down, empirically determined.
    private static final double COOL_DOWN_COMPLETE_THRESHOLD = 30; // In joules/second
    public static final double ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT = 100; // In joules, but empirically determined.
    public static final double MAX_ENERGY_CHUNK_DISTANCE = 0.5; // In meters.
    private static final DoubleRange ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE = new DoubleRange( 0.12, 0.15 );
    private static final Random RAND = new Random();

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final Property<Double> heatCoolAmount = new Property<Double>( 0.0 );
    private Property<Double> energyProductionRate = new Property<Double>( 0.0 );
    private double heatEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
    private double steamEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
    private ObservableProperty<Boolean> energyChunksVisible;
    private ObservableProperty<Boolean> steamPowerableElementInPlace;
    private List<EnergyChunkPathMover> energyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    // List of chunks that are not being transferred to the next energy system
    // element.
    public final List<EnergyChunk> exemptFromTransferEnergyChunks = new ArrayList<EnergyChunk>();

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
                                                    MAX_ENERGY_PRODUCTION_RATE ) ); // Analogous to velocity.
            }
            else {
                // Clamp the energy production rate to zero so that it doesn't
                // trickle on forever.
                energyProductionRate.set( 0.0 );
                steamEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
            }

            // See if it's time to emit a new energy chunk from the heater.
            heatEnergyProducedSinceLastChunk += Math.max( heatCoolAmount.get(), 0 ) * MAX_ENERGY_PRODUCTION_RATE * dt;
            if ( heatEnergyProducedSinceLastChunk >= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT ) {
                // Emit a new thermal energy chunk.
                Vector2D initialPosition = new Vector2D( getPosition().getX() + THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE.getMin() + RAND.nextDouble() * THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE.getLength(),
                                                         getPosition().getY() + THERMAL_ENERGY_CHUNK_Y_ORIGIN );
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.THERMAL, initialPosition, energyChunksVisible );
                energyChunkList.add( energyChunk );
                heatEnergyProducedSinceLastChunk -= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT;
                energyChunkMovers.add( new EnergyChunkPathMover( energyChunk, createThermalEnergyChunkPath( initialPosition ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
            }

            // Move all energy chunks that are under this element's control.
            Rectangle2D teapotBounds = new Rectangle2D.Double( getPosition().getX() + TEAPOT_OFFSET.getX() - TEAPOT_IMAGE.getWidth() / 2,
                                                               getPosition().getY() + TEAPOT_OFFSET.getY() - TEAPOT_IMAGE.getHeight() / 2,
                                                               TEAPOT_IMAGE.getWidth(),
                                                               TEAPOT_IMAGE.getHeight() );
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                EnergyChunk energyChunk = energyChunkMover.energyChunk;
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    energyChunkMovers.remove( energyChunkMover );
                    if ( teapotBounds.contains( energyChunkMover.energyChunk.position.get().toPoint2D() ) ) {
                        if ( RAND.nextDouble() > 0.2 ) {
                            // Turn the chunk into mechanical energy.
                            energyChunk.energyType.set( EnergyType.MECHANICAL );
                        }
                        // Set this chunk on a path out of the tea pot.
                        energyChunkMovers.add( new EnergyChunkPathMover( energyChunk, createMechanicalEnergyChunkPath( getPosition() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
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
        return new Energy( EnergyType.MECHANICAL, energyProductionRate.get() * dt, Math.PI / 2 );
    }

    private static List<Vector2D> createThermalEnergyChunkPath( final Vector2D startPosition ) {
        return new ArrayList<Vector2D>() {{
            add( startPosition.plus( 0, THERMAL_ENERGY_CHUNK_TRAVEL_DISTANCE ) );
        }};
    }

    private static List<Vector2D> createMechanicalEnergyChunkPath( final Vector2D parentElementPosition ) {
        return new ArrayList<Vector2D>() {{
            add( parentElementPosition.plus( SPOUT_BOTTOM_OFFSET ) );
            add( parentElementPosition.plus( SPOUT_TIP_OFFSET ) );
            add( parentElementPosition.plus( DISTANT_TARGET_OFFSET ) );
        }};
    }

    @Override public void deactivate() {
        super.deactivate();
        heatCoolAmount.reset();
        energyProductionRate.reset();
        steamEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
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
