// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

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
    private static final Dimension2D SIZE = new PDimension( 0.7, 0.3 );

    // The thickness of the slice of air being modeled.  This is basically the
    // z dimension, and is used solely for volume calculations.
    private static final double DEPTH = 0.1; // In meters.

    // Constants that define the heat carrying capacity of the air.
    private static final double SPECIFIC_HEAT = 1012; // In J/kg-K, source = design document.
    //    private static final double DENSITY = 0.001; // In kg/m^3, source = design document (and common knowledge).
    private static final double DENSITY = 10; // In kg/m^3, TODO tweaked version of this value for experimenting.

    // Derived constants.
    private static final double VOLUME = SIZE.getWidth() * SIZE.getHeight() * DEPTH;
    private static final double MASS = VOLUME * DENSITY;
    private static final double INITIAL_ENERGY = MASS * SPECIFIC_HEAT * EFACConstants.ROOM_TEMPERATURE;
    private static final ThermalContactArea THERMAL_CONTACT_AREA = new ThermalContactArea( new Rectangle2D.Double( -SIZE.getWidth() / 2, 0, SIZE.getWidth(), SIZE.getHeight() ), true );
    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private double energy = INITIAL_ENERGY;
    private ConstantDtClock clock;
    private BooleanProperty energyChunksVisible;
    protected final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();
    protected final List<EnergyChunkWanderController> energyChunkWanderControllers = new ArrayList<EnergyChunkWanderController>();

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
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void stepInTime( double dt ) {
        // Update the position of any energy chunks.
        for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
            energyChunkWanderController.updatePosition( dt );
            if ( !getThermalContactArea().getBounds().contains( energyChunkWanderController.getEnergyChunk().position.get().toPoint2D() ) ) {
                // Remove this energy chunk.
                energyChunkList.remove( energyChunkWanderController.getEnergyChunk() );
                energyChunkWanderControllers.remove( energyChunkWanderController );
            }
        }

        // Gravitate back towards room temperature over time.
        if ( Math.abs( getTemperature() - EFACConstants.ROOM_TEMPERATURE ) > EFACConstants.SIGNIFICANT_TEMPERATURE_DIFFERENCE ) {
            double thermalEnergyLost = ( getTemperature() - EFACConstants.ROOM_TEMPERATURE ) * 10000 * dt;
            changeEnergy( -thermalEnergyLost );
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
        energyChunkWanderControllers.clear();
    }

    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {
            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > EFACConstants.TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between this and the other energy container.
                double heatTransferConstant = getHeatTransferFactor( this.getEnergyContainerCategory(), otherEnergyContainer.getEnergyContainerCategory() );
                double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * heatTransferConstant * dt;
                changeEnergy( thermalEnergyGained );
                otherEnergyContainer.changeEnergy( -thermalEnergyGained );
            }
        }
    }

    public boolean needsEnergyChunk() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) > energyChunkList.size() + ENERGY_TO_NUM_CHUNKS_MAPPER.apply( INITIAL_ENERGY ) - 1;
    }

    public boolean hasExcessEnergyChunks() {
        return ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy ) < energyChunkList.size() + ENERGY_TO_NUM_CHUNKS_MAPPER.apply( INITIAL_ENERGY ) - 1;
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        ec.zPosition.set( 0.0 );
        energyChunkList.add( ec );
        energyChunkWanderControllers.add( new EnergyChunkWanderController( ec, new ImmutableVector2D( ec.position.get().getX(), SIZE.getHeight() ) ) );
    }

    public EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point ) {
        EnergyChunk closestChunk = null;
        for ( EnergyChunk energyChunk : energyChunkList ) {
            if ( closestChunk == null || closestChunk.position.get().distance( point ) > energyChunk.position.get().distance( point ) ) {
                closestChunk = energyChunk;
            }
        }
        if ( closestChunk == null ) {
            // Create a new chunk at the top of the air above the specified point.
            closestChunk = new EnergyChunk( clock, point.getX(), SIZE.getHeight(), energyChunksVisible, false );
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

    public boolean canAcceptEnergyChunk() {
        return true;
    }

    public boolean canSupplyEnergyChunk() {
        return true;
    }

}
