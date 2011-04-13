// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Default settings that are common to 2 or more modules.
 *
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 */
public class BuildAnAtomDefaults {

    /* Not intended for instantiation */
    private BuildAnAtomDefaults() {
    }

    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_DT = 1 / (double) CLOCK_FRAME_RATE;
    public static final int CLOCK_TIME_COLUMNS = 10;

    // Model-view transform
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
}
