// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Interface for model elements that contain energy.
 *
 * @author John Blanco
 */
public interface ThermalEnergyContainer {

    // Threshold for deciding when two temperatures can be considered equal.
    public static final double TEMPERATURES_EQUAL_THRESHOLD = 1E-6; // In Kelvin.

    /**
     * Change the amount of energy contained.  This is used to both add and
     * remove energy.
     *
     * @param deltaEnergy
     */
    void changeEnergy( double deltaEnergy );

    /**
     * Get the current amount of energy contained.
     *
     * @return
     */
    double getEnergy();

    /**
     * Reset to the initial amount of energy.
     */
    void reset();

    /**
     * Exchange energy with another energy container.  The implementation must
     * determine the amount of contact or overlap as well as the energy
     * gradient and do the exchange based on these conditions.
     *
     * @param energyContainer
     * @param dt
     */
    void exchangeEnergyWith( ThermalEnergyContainer energyContainer, double dt );

    /**
     * Find out if this energy container is low on energy chunks.
     *
     * @return True if the energy level in this container is at a value the
     *         indicates that one or more energy chuck is needed.
     */
    boolean needsEnergyChunk();

    /**
     * Find out if the energy container has extra energy chunks.
     *
     * @return
     */
    boolean hasExcessEnergyChunks();

    /**
     * Add an energy chunk to this container.
     *
     * @param ec
     */
    void addEnergyChunk( EnergyChunk ec );

    /**
     * Locate the energy chunk that is closest to the specified location,
     * remove it from this container, and return it to the caller.
     *
     * @return
     */
    EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point );

    /**
     * Get a point that represents the 2D center in model space of the energy
     * container.
     *
     * @return
     */
    ImmutableVector2D getCenterPoint();

    /**
     * Get the area that can be used to test whether one energy container is in
     * thermal contact with another, and thus able to exchange energy.
     *
     * @return
     */
    ThermalContactArea getThermalContactArea();

    /**
     * Get the temperature of the element.
     *
     * @return
     */
    double getTemperature();

    /**
     * Get the list of 'energy chunks' that are owned by this container.  These
     * chunks will either be within the bounds of the container or on their way
     * there.
     *
     * @return
     */
    ObservableList<EnergyChunk> getEnergyChunkList();

    /**
     * Get the category or type of container.  See the definition of the return
     * type for a greater understanding of what this means.
     *
     * @return
     */
    EnergyContainerCategory getEnergyContainerCategory();

    /**
     * Types of thermal energy containers, primarily used for determining the
     * rate at which heat is transferred between different items.
     */
    enum EnergyContainerCategory {
        IRON, BRICK, WATER, AIR, BURNER
    }
}
