// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER;
import static edu.colorado.phet.energyformsandchanges.intro.model.ThermalEnergyTransferConstants.getHeatTransferFactor;

/**
 * A movable model element that contains thermal energy and that, at least in
 * the model, has an overall shape that can be represented as a rectangle.
 *
 * @author John Blanco
 */
public abstract class RectangularThermalMovableModelElement extends UserMovableModelElement implements ThermalEnergyContainer {

    public final BooleanProperty energyChunksVisible;
    protected double energy = 0; // In Joules.
    protected final double specificHeat; // In J/kg-K
    protected final double mass; // In kg
    protected final ConstantDtClock clock;
    protected final double width;
    protected final double height;
    private int nextSliceIndex;

    // 2D "slices" of the container, used for 3D layering of energy chunks.
    protected final List<EnergyChunkContainerSlice> slices = new ArrayList<EnergyChunkContainerSlice>();

    /**
     * Constructor.
     */
    public RectangularThermalMovableModelElement( ConstantDtClock clock, ImmutableVector2D initialPosition, double width, double height, double mass, double specificHeat, BooleanProperty energyChunksVisible ) {
        super( initialPosition );
        this.clock = clock;
        this.mass = mass;
        this.width = width;
        this.height = height;
        this.specificHeat = specificHeat;
        this.energyChunksVisible = energyChunksVisible;

        energy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;

        // Hook up to the clock for time dependent behavior.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the slices, a.k.a. layers, where the energy chunks will live.
        addEnergyChunkSlices();
        nextSliceIndex = slices.size() / 2;

        // Add the initial energy chunks.
        addInitialEnergyChunks();

        // Clock ourself for a while to allow the chunks to get distributed
        // to their initial positions.
        for ( int i = 0; i < 1000; i++ ) {
            stepInTime( clock.getDt() );
        }
    }

    /**
     * Get the rectangle that defines this elements position and shape in
     * model space.
     */
    public abstract Rectangle2D getRect();

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public double getTemperature() {
        return energy / ( mass * specificHeat );
    }

    @Override public void reset() {
        super.reset();
        energy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;
        addInitialEnergyChunks();
    }

    protected void stepInTime( double dt ) {
        EnergyChunkDistributor.updatePositions( slices, dt );
    }

    public boolean needsEnergyChunk() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) > getNumContainedEnergyChunks();
    }

    public boolean hasExcessEnergyChunks() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) < getNumContainedEnergyChunks();
    }

    private static final Random RAND = new Random();

    public void addEnergyChunk( EnergyChunk ec ) {
        slices.get( nextSliceIndex ).addEnergyChunk( ec );
        nextSliceIndex = ( nextSliceIndex + 1 ) % slices.size();
    }

    public boolean removeEnergyChunk( EnergyChunk ec ) {
        for ( EnergyChunkContainerSlice slice : slices ) {
            if ( slice.energyChunkList.remove( ec ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract the closest energy chunk on a randomly chosen energy chunk
     * slice.  The random part prevents odd situations where all the chunks
     * get pulled off of one slice.
     *
     * @param point
     * @return
     */
    public EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point ) {
        EnergyChunk closestEnergyChunk = null;
        double closestCompensatedDistance = Double.POSITIVE_INFINITY;

        // Identify the closest energy chunk.
        for ( EnergyChunkContainerSlice slice : slices ) {
            for ( EnergyChunk ec : slice.energyChunkList ) {
                // Compensate for the Z offset.  Otherwise front chunk will
                // almost always be chosen.
                ImmutableVector2D compensatedEcPosition = ec.position.get().getSubtractedInstance( 0, EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER * ec.zPosition.get() );
                double compensatedDistance = compensatedEcPosition.distance( point );
                if ( compensatedDistance < closestCompensatedDistance ) {
                    closestEnergyChunk = ec;
                    closestCompensatedDistance = compensatedDistance;
                }
            }
        }

        removeEnergyChunk( closestEnergyChunk );
        return closestEnergyChunk;
    }

    /**
     * Initialization method that add the "slices" where the energy chunks
     * reside.  Should be called only once at initialization.
     */
    protected void addEnergyChunkSlices() {

        assert ( slices.size() == 0 ); // Make sure this method isn't being misused.

        // Defaults to a single slice matching the outline rectangle, override
        // for more sophisticated behavior.
        slices.add( new EnergyChunkContainerSlice( getRect(), 0, position ) );
    }

    protected void addInitialEnergyChunks() {
        for ( EnergyChunkContainerSlice slice : slices ) {
            slice.energyChunkList.clear();
        }
        int targetNumChunks = ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
        Rectangle2D energyChunkBounds = getThermalContactArea().getBounds();
        while ( getNumContainedEnergyChunks() < targetNumChunks ) {
            // Add a chunk at a random location in the block.
            addEnergyChunk( new EnergyChunk( clock, EnergyChunkDistributor.generateRandomLocation( energyChunkBounds ), energyChunksVisible, false ) );
        }
    }

    protected int getNumContainedEnergyChunks() {
        int numChunks = 0;
        for ( EnergyChunkContainerSlice slice : slices ) {
            numChunks += slice.getNumEnergyChunks();
        }
        return numChunks;
    }

    protected Shape getEnergyChunkContainmentShape() {
        // Override for more elaborate or complex shapes.
        return getThermalContactArea().getBounds();
    }

    public List<EnergyChunkContainerSlice> getSlices() {
        return slices;
    }

    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {
            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > EFACConstants.TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between the this and the other energy container.
                double heatTransferConstant = getHeatTransferFactor( this.getEnergyContainerCategory(), otherEnergyContainer.getEnergyContainerCategory() );
                double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * heatTransferConstant * dt;
                changeEnergy( thermalEnergyGained );
                otherEnergyContainer.changeEnergy( -thermalEnergyGained );
            }
        }
    }

    public ImmutableVector2D getCenterPoint() {
        return new ImmutableVector2D( position.get().getX(), position.get().getY() + height / 2 );
    }

    /**
     * Get a number indicating the balance between the energy level and the
     * number of energy chunks owned by this model element.
     *
     * @return 0 if the number of energy chunks matches the energy level, a
     *         negative value if there is a deficit, and a positive value if there is
     *         a surplus.
     */
    public int getEnergyChunkBalance() {
        return getNumContainedEnergyChunks() - EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
    }
}
