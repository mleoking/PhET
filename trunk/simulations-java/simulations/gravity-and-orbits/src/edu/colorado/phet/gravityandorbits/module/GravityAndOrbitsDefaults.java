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
    public static final double SECONDS_PER_MINUTE = 60;

    private GravityAndOrbitsDefaults() {/* Not intended for instantiation */
    }

    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double NUMBER_DAYS_PER_TICK = 4;
    public static final int SECONDS_PER_DAY = 86400;
    public static final double DEFAULT_DT = NUMBER_DAYS_PER_TICK * SECONDS_PER_DAY;
    public static final int CLOCK_TIME_COLUMNS = 10;

    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );
}
