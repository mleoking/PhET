// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

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
}
