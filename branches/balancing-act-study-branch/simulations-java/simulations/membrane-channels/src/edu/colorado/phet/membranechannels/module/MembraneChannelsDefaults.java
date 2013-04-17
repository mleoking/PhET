// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.module;

import edu.umd.cs.piccolo.util.PDimension;



/**
 * Default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 */
public class MembraneChannelsDefaults {

    /* Not intended for instantiation */
    private MembraneChannelsDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    
    // Set up the clock ranges.  Note that for this sim the clock rates are
    // often several orders of magnitude slower than real time.
    
    public static final double DEFAULT_MEMBRANE_CHANNELS_CLOCK_DT = 1 / (double)CLOCK_FRAME_RATE;
    public static final double MIN_MEMBRANE_CHANNELS_CLOCK_DT = DEFAULT_MEMBRANE_CHANNELS_CLOCK_DT / 3;
    public static final double MAX_MEMBRANE_CHANNELS_CLOCK_DT = DEFAULT_MEMBRANE_CHANNELS_CLOCK_DT * 1.6;
    
    // Model-view transform
    public static final PDimension INTERMEDIATE_RENDERING_SIZE = new PDimension( 786, 786 );

}
