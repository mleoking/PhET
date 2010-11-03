/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;


/**
 * Default settings that are common to 2 or more modules.
 * <p/>
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 */
public class GravityAndOrbitsDefaults {

    /* Not intended for instantiation */

    private GravityAndOrbitsDefaults() {
    }

    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    private static final double NUMBER_DAYS_PER_TICK = 10;
    public static final double CLOCK_DT = NUMBER_DAYS_PER_TICK * 86400;//seconds in a day
    public static final int CLOCK_TIME_COLUMNS = 10;

    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );

}
