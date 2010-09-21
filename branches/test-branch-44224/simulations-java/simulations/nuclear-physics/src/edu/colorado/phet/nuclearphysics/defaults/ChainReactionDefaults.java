/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.defaults;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * This class contains the default settings for the Chain Reaction module.
 *
 * @author John Blanco
 */
public class ChainReactionDefaults {
    
    /* Not intended for instantiation */
    private ChainReactionDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = 25; // Frames per second.
    public static final double CLOCK_DT = 5; // Milliseconds per tick.
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final NuclearPhysicsClock CLOCK = new NuclearPhysicsClock( CLOCK_FRAME_RATE, CLOCK_DT );
}
