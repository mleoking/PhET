// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common;

/**
 * Shared constants used in multiple locations within the sim.
 *
 * @author John Blanco
 */
public class EFACConstants {

    public static final double ROOM_TEMPERATURE = 296; // In Kelvin.

    // Time values for normal and fast-forward motion.
    public static final double FRAMES_PER_SECOND = 30.0;
    public static final double SIM_TIME_PER_TICK_NORMAL = 1 / FRAMES_PER_SECOND;
    public static final double SIM_TIME_PER_TICK_FAST_FORWARD = SIM_TIME_PER_TICK_NORMAL * 4;

    // Constants that define the number of energy chunks in energy-containing
    // model elements.
    public static final double MIN_ENERGY = 10000;   // Min energy before any chunks are depicted (in Joules).
    public static final double ENERGY_CHUNK_MULTIPLIER = 0.0001;  // Chunks per Joule.
}
