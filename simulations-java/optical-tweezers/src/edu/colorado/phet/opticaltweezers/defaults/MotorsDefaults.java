/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


/**
 * MotorsDefaults contains default settings for the "Molecular Motors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsDefaults {

    /* Not intended for instantiation */
    private MotorsDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final boolean CLOCK_PAUSED = false ;
    public static final double CLOCK_DT = 1;
    public static final OTClock CLOCK = new OTClock( CLOCK_FRAME_RATE, CLOCK_DT );
}
