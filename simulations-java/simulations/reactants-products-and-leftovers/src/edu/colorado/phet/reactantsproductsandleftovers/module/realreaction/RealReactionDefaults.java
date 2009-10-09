/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.Dimension;


/**
 * Defaults for "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionDefaults {

    /* Not intended for instantiation */
    private RealReactionDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_DT = 1;
    public static final int CLOCK_TIME_COLUMNS = 10;

    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );

}
