// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER;

/**
 * A movable model element that contains thermal energy and that, at least in
 * the model, has an overall shape that can be represented as a rectangle.
 *
 * @author John Blanco
 */
public abstract class RectangularThermalMovableModelElement extends UserMovableModelElement implements ThermalEnergyContainer {

    protected final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();
    public final BooleanProperty energyChunksVisible;
    protected double energy = 0; // In Joules.
    protected final double specificHeat; // In J/kg-K
    protected final double mass; // In kg
    protected final ConstantDtClock clock;
    protected final double width;
    protected final double height;

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

        // Update positions of contained energy chunks when this element moves.
        position.addObserver( new ChangeObserver<ImmutableVector2D>() {
            public void update( ImmutableVector2D newPosition, ImmutableVector2D oldPosition ) {
                ImmutableVector2D movement = newPosition.getSubtractedInstance( oldPosition );
                for ( EnergyChunk energyChunk : energyChunkList ) {
                    energyChunk.position.set( energyChunk.position.get().getAddedInstance( movement ) );
                }
            }
        } );

        // Add the initial energy chunks.
        addInitialEnergyChunks();
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
        EnergyChunkDistributor.updatePositions( energyChunkList, getThermalContactArea().getBounds(), dt );
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return energyChunkList;
    }

    public boolean needsEnergyChunk() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) > energyChunkList.size();
    }

    public boolean hasExcessEnergyChunks() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) < energyChunkList.size();
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        energyChunkList.add( ec );
    }

    public EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point ) {
        EnergyChunk closestEnergyChunk = energyChunkList.isEmpty() ? null : energyChunkList.get( 0 );
        for ( EnergyChunk energyChunk : energyChunkList ) {
            if ( energyChunk.position.get().distance( point ) < closestEnergyChunk.position.get().distance( point ) ) {
                // New closest chunk.
                closestEnergyChunk = energyChunk;
            }
        }
        if ( closestEnergyChunk != null ) {
            energyChunkList.remove( closestEnergyChunk );
        }
        return closestEnergyChunk;
    }

    protected void addInitialEnergyChunks() {
        energyChunkList.clear();
        int targetNumChunks = ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
        Rectangle2D energyChunkBounds = getThermalContactArea().getBounds();
        while ( targetNumChunks != getEnergyChunkList().size() ) {
            // Add a chunk at a random location in the block.
            addEnergyChunk( new EnergyChunk( clock, EnergyChunkDistributor.generateRandomLocation( energyChunkBounds ), energyChunksVisible, false ) );
            System.out.println( "Added a chunk" );
        }
    }

    // TODO: May be able to move this up to parent class.  Would be shared with beaker, though, so needs investigating.
    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {
            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between the this and the other energy container.
                // TODO: The following is a first attempt and likely to need much adjustment.  Transfer constant was empirically chosen
                double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * 1000 * dt;
                changeEnergy( thermalEnergyGained );
                otherEnergyContainer.changeEnergy( -thermalEnergyGained );
            }

            // Exchange energy chunks.
            if ( otherEnergyContainer.needsEnergyChunk() && hasExcessEnergyChunks() ) {
                // The other energy container needs an energy chunk, and this
                // container has excess, so transfer one.
                otherEnergyContainer.addEnergyChunk( extractClosestEnergyChunk( otherEnergyContainer.getCenterPoint() ) );
            }
            else if ( otherEnergyContainer.hasExcessEnergyChunks() && needsEnergyChunk() ) {
                // This energy container needs a chunk, and the other has
                // excess, so take one.
                energyChunkList.add( otherEnergyContainer.extractClosestEnergyChunk( getCenterPoint() ) );
            }
        }
    }

    public ImmutableVector2D getCenterPoint() {
        return new ImmutableVector2D( position.get().getX(), position.get().getY() + height / 2 );
    }
}
