// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common;

/**
 * Shared constants used in multiple locations within the sim.
 *
 * @author John Blanco
 */
public class EFACConstants {
    public static final double ROOM_TEMPERATURE = 296; // In Kelvin.
    // DT values for normal and slow motion.
    public static final double FRAMES_PER_SECOND = 30.0;
    public static final double SIM_TIME_PER_TICK_NORMAL = 1 / FRAMES_PER_SECOND;
    public static final double SIM_TIME_PER_TICK_FAST_FORWARD = SIM_TIME_PER_TICK_NORMAL * 4;
}
