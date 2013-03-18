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
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a faucet that can be turned on to provide mechanical
 * energy to other energy system elements.
 *
 * @author John Blanco
 */
public class FaucetAndWater extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final Vector2D OFFSET_FROM_CENTER_TO_WATER_ORIGIN = new Vector2D( 0.065, 0.08 );
    private static final double FALLING_ENERGY_CHUNK_VELOCITY = 0.09; // In meters/second.
    private static final double FLOW_PER_CHUNK = 0.4;  // Empirically determined to get desired energy chunk emission rate.
    private static final double MAX_WATER_WIDTH = 0.015; // In meters.
    private static final double MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER = 0.5; // In meters.
    private static final Random RAND = new Random();
    private static final DoubleRange ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE = new DoubleRange( 0.05, 0.06 );

    // The following acceleration constant defines the rate at which the water
    // flows from the faucet.  The value used is not the actual value in
    // Earth's gravitational field - it has been tweaked for optimal visual
    // effect.
    private static final Vector2D ACCELERATION_DUE_TO_GRAVITY = new Vector2D( 0, -0.15 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Proportion of full available flow that is occurring.
    public final Property<Double> flowProportion = new Property<Double>( 0.0 );

    // Water drops that comprise the stream of water.
    public final ObservableList<WaterDrop> waterDrops = new ObservableList<WaterDrop>();

    // List of chunks that are not being transferred to the next energy system
    // element.
    public final List<EnergyChunk> exemptFromTransferEnergyChunks = new ArrayList<EnergyChunk>();

    private double energySinceLastChunk = 0;
    private final BooleanProperty energyChunksVisible;

    // Flag that is used to decide whether to pass energy chunks to the next
    // energy system element.
    private final ObservableProperty<Boolean> waterPowerableElementInPlace;

    // Flag for whether next chunk should be transferred or kept, used to
    // alternate transfer with non-transfer.
    private boolean transferNextAvailableChunk = true;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected FaucetAndWater( BooleanProperty energyChunksVisible, ObservableProperty<Boolean> waterPowerableElementInPlace ) {
        super( EnergyFormsAndChangesResources.Images.FAUCET_ICON );
        this.energyChunksVisible = energyChunksVisible;
        this.waterPowerableElementInPlace = waterPowerableElementInPlace;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {

        if ( isActive() ) {

            // Add water droplets as needed based on flow rate.
            if ( flowProportion.get() > 0 ) {
                double initialWidth = flowProportion.get() * MAX_WATER_WIDTH * ( 1 + ( RAND.nextDouble() - 0.5 ) * 0.2 );
                waterDrops.add( new WaterDrop( OFFSET_FROM_CENTER_TO_WATER_ORIGIN.plus( 0, 0.01 ),
                                               new Vector2D( 0, 0 ),
                                               new PDimension( initialWidth, initialWidth ) ) );
            }

            // Make the water droplets fall.
            for ( WaterDrop waterDrop : new ArrayList<WaterDrop>( waterDrops ) ) {
                waterDrop.velocity.set( waterDrop.velocity.get().plus( ACCELERATION_DUE_TO_GRAVITY.times( dt ) ) );
                waterDrop.offsetFromParent.set( waterDrop.offsetFromParent.get().plus( waterDrop.velocity.get().times( dt ) ) );
                if ( waterDrop.offsetFromParent.get().distance( getPosition() ) > MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) {
                    waterDrops.remove( waterDrop );
                }
            }

            // Check if time to emit an energy chunk and, if so, do it.
            energySinceLastChunk += EFACConstants.MAX_ENERGY_PRODUCTION_RATE * flowProportion.get() * dt;
            if ( energySinceLastChunk >= EFACConstants.ENERGY_PER_CHUNK ) {
                Vector2D initialPosition = getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ).plus( ( RAND.nextDouble() - 0.5 ) * flowProportion.get() * MAX_WATER_WIDTH / 2, 0 );
                energyChunkList.add( new EnergyChunk( EnergyType.MECHANICAL,
                                                      initialPosition,
                                                      new Vector2D( 0, -FALLING_ENERGY_CHUNK_VELOCITY ),
                                                      energyChunksVisible ) );
                energySinceLastChunk = energySinceLastChunk - EFACConstants.ENERGY_PER_CHUNK;
            }

            // Update energy chunk positions.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {

                // Make the chunk fall.
                energyChunk.translateBasedOnVelocity( dt );

                // See if chunk is in the location where it can be transferred
                // to the next energy system.
                if ( waterPowerableElementInPlace.get() &&
                     ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE.contains( getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ).getY() - energyChunk.position.get().y ) &&
                     !exemptFromTransferEnergyChunks.contains( energyChunk ) ) {

                    if ( transferNextAvailableChunk ) {
                        // Send this chunk to the next energy system.
                        outgoingEnergyChunks.add( energyChunk );

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

                // Remove it if it is out of visible range.
                if ( getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ).distance( energyChunk.position.get() ) > MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) {
                    energyChunkList.remove( energyChunk );
                    exemptFromTransferEnergyChunks.remove( energyChunk );
                }
            }
        }

        // Generate the appropriate amount of energy.
        return new Energy( EnergyType.MECHANICAL, EFACConstants.MAX_ENERGY_PRODUCTION_RATE * flowProportion.get() * dt, -Math.PI / 2 );
    }

    @Override public void preLoadEnergyChunks() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void deactivate() {
        super.deactivate();
        waterDrops.clear();
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        exemptFromTransferEnergyChunks.clear();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectFaucetButton;
    }
}