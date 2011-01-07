/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.greenhouse;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Global defaults contains default settings that are common to 2 or more modules.
 *
 * @author John Blanco
 */
public class GreenhouseDefaults {

    /* Not intended for instantiation */
    private GreenhouseDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    
    // Model-view transform for intermediate coordinates.
    public static final PDimension INTERMEDIATE_RENDERING_SIZE = new PDimension( 786, 786 );
}
