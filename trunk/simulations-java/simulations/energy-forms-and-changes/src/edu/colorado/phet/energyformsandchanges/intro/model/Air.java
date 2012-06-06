// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER;
import static edu.colorado.phet.energyformsandchanges.intro.model.ThermalEnergyTransferConstants.getHeatTransferFactor;

/**
 * Class that represents the air in the model.  Air can hold heat, and can
 * exchange thermal energy with other model objects.
 *
 * @author John Blanco
 */
public class Air implements ThermalEnergyContainer {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // 2D size of the air.  It is sized such that it will extend off the left,
    // right, and top edges of screen for the most common aspect ratios of the
    // view.
    private static final Dimension2D SIZE = new PDimension( 0.45, 0.25 );

    // The thickness of the slice of air being modeled.  This is basically the
    // z dimension, and is used solely for volume calculations.
    private static final double DEPTH = 0.1; // In meters.

    // Constants that define the heat carrying capacity of the air.
    private static final double SPECIFIC_HEAT = 1012; // In J/kg-K, source = design document.
    //    private static final double DENSITY = 1; // In kg/m^3, source = design document (and common knowledge).
    private static final double DENSITY = 10; // In kg/m^3, TODO tweaked version of this constant for experimenting.

    // Derived constants.
    private static final double VOLUME = SIZE.getWidth() * SIZE.getHeight() * DEPTH;
    private static final double MASS = VOLUME * DENSITY;
    private static final double INITIAL_ENERGY = MASS * SPECIFIC_HEAT * EFACConstants.ROOM_TEMPERATURE;
    private static final ThermalContactArea THERMAL_CONTACT_AREA = new ThermalContactArea( new Rectangle2D.Double( -SIZE.getWidth() / 2,
                                                                                                                   0,
                                                                                                                   SIZE.getWidth(),
                                                                                                                   SIZE.getHeight() ),
                                                                                           true );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private double energy = INITIAL_ENERGY;
    private ConstantDtClock clock;
    private BooleanProperty energyChunksVisible;
    protected final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Air( ConstantDtClock clock, BooleanProperty energyChunksVisible ) {

        this.clock = clock;
        this.energyChunksVisible = energyChunksVisible;

        // Hook up to the clock for time dependent behavior.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the initial energy chunks.
        addInitialEnergyChunks();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void stepInTime( double dt ) {
        System.out.println( "Air SIT is stubbed. Temp = " + getTemperature() );
    }

    private void addInitialEnergyChunks() {
        energyChunkList.clear();
        int targetNumChunks = ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
        Rectangle2D energyChunkBounds = getThermalContactArea().getBounds();
        while ( targetNumChunks != energyChunkList.size() ) {
            // Add a chunk at a random location.
            addEnergyChunk( new EnergyChunk( clock, EnergyChunkDistributor.generateRandomLocation( energyChunkBounds ), energyChunksVisible, false ) );
        }
    }

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public void reset() {
        energy = INITIAL_ENERGY;
        energyChunkList.clear();
        addInitialEnergyChunks();
    }

    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {
            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between the this and the other energy container.
                double heatTransferConstant = getHeatTransferFactor( this.getEnergyContainerCategory(), otherEnergyContainer.getEnergyContainerCategory() );
                double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * heatTransferConstant * dt;
                changeEnergy( thermalEnergyGained );
                otherEnergyContainer.changeEnergy( -thermalEnergyGained );
            }

            // Exchange energy chunks.
            if ( needsEnergyChunk() ) {
                if ( otherEnergyContainer.hasExcessEnergyChunks() ) {
                    energyChunkList.add( otherEnergyContainer.extractClosestEnergyChunk( getCenterPoint() ) );
                }
            }
            else if ( hasExcessEnergyChunks() ) {
                if ( otherEnergyContainer.needsEnergyChunk() ) {
                    otherEnergyContainer.addEnergyChunk( extractClosestEnergyChunk( otherEnergyContainer.getCenterPoint() ) );
                }
            }
        }
    }

    public boolean needsEnergyChunk() {
        System.out.println( "Needs energy chunk: " + ( ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) > energyChunkList.size() ) );
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) > energyChunkList.size();
    }

    public boolean hasExcessEnergyChunks() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) < energyChunkList.size();
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        energyChunkList.add( ec );
    }

    public EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point ) {
        EnergyChunk closestChunk = null;
        for ( EnergyChunk energyChunk : energyChunkList ) {
            if ( closestChunk == null || closestChunk.position.get().distance( point ) > energyChunk.position.get().distance( point ) ) {
                closestChunk = energyChunk;
            }
        }
        return closestChunk;
    }

    public ImmutableVector2D getCenterPoint() {
        return new ImmutableVector2D( 0, SIZE.getHeight() / 2 );
    }

    public ThermalContactArea getThermalContactArea() {
        return THERMAL_CONTACT_AREA;
    }

    public double getTemperature() {
        return energy / ( MASS * SPECIFIC_HEAT );
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return energyChunkList;
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.AIR;
    }
}
