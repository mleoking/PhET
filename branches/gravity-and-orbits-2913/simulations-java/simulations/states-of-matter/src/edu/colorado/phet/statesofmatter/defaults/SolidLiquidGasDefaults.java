// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.defaults;


/**
 * This class contains the default settings for the "Solid, Liquid, Gas" Module.
 *
 * @author John Blanco
 */
public class SolidLiquidGasDefaults {
    
    /* Not intended for instantiation */
    private SolidLiquidGasDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = 30; // Frames per second.
    public static final int CLOCK_FRAME_DELAY = 1000 / CLOCK_FRAME_RATE;
    public static final double CLOCK_DT = 5; // Milliseconds per tick.
}
