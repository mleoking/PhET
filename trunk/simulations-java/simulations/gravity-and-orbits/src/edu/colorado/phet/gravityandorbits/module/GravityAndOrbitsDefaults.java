/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;


public class GravityAndOrbitsDefaults {
    
    public static final double SECONDS_PER_MINUTE = 60;

    /* Not intended for instantiation */
    private GravityAndOrbitsDefaults() {}

    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double DAYS_PER_TICK = 1;
    public static final int SECONDS_PER_DAY = 86400;
    public static final double DEFAULT_DT = DAYS_PER_TICK * SECONDS_PER_DAY;

    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );
}
