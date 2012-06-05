// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.umd.cs.piccolo.util.PDimension;

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
    private static final Dimension2D SIZE = new PDimension( 0.20, 0.20 );

    // The thickness of the slice of air being modeled.  This is basically the
    // z dimension, and is used solely for volume calculations.
    private static final double DEPTH = 0.1;

    // Constants that define the heat carrying capacity of the air.
    private static final double SPECIFIC_HEAT = 1012; // In J/kg-K, source = design document.
    private static final double DENSITY = 1; // In kg/m^3, source = design document (and common knowledge).

    // Derived constants.
    private static final double VOLUME = SIZE.getWidth() * SIZE.getHeight() * DEPTH;
    private static final double MASS = VOLUME * DENSITY;
    private static final double INITIAL_ENERGY = MASS * SPECIFIC_HEAT * EFACConstants.ROOM_TEMPERATURE;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private double energy = INITIAL_ENERGY;
    private ConstantDtClock clock;
    private BooleanProperty energyChunksVisible;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Air( ConstantDtClock clock, double mass, double specificHeat, BooleanProperty energyChunksVisible ) {

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
        System.out.println( "Air SIT is stubbed." );
    }

    private void addInitialEnergyChunks() {

    }

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public void reset() {
        energy = INITIAL_ENERGY;
    }

    public void exchangeEnergyWith( ThermalEnergyContainer energyContainer, double dt ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean needsEnergyChunk() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasExcessEnergyChunks() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ImmutableVector2D getCenterPoint() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ThermalContactArea getThermalContactArea() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getTemperature() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
