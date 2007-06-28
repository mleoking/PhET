/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * MotorsDefaults contains default settings for MotorsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsDefaults {

    /* Not intended for instantiation */
    private MotorsDefaults() {}
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 750, 750 );
    public static final double MODEL_TO_VIEW_SCALE = 0.5;
    
    // Clock
    public static final boolean CLOCK_PAUSED = false;
    private static final int FRAME_RATE = 25; // fps, frames per second (wall time)
    private static final double MAX_DT = ( 1E-3 / FRAME_RATE );
    private static final double MIN_DT = 140E-16 * MAX_DT;
    private static final DoubleRange SLOW_DT_RANGE = new DoubleRange( MIN_DT, 1E-11, MIN_DT );
    private static final DoubleRange FAST_DT_RANGE = new DoubleRange( 1E-10, MAX_DT, MAX_DT );
    public static final double DEFAULT_DT = MAX_DT;
    public static final OTClock CLOCK = new OTClock( FRAME_RATE, SLOW_DT_RANGE, FAST_DT_RANGE, DEFAULT_DT );
    public static final String CLOCK_TIME_PATTERN = "0.0000000000000000000";
    public static final int CLOCK_TIME_COLUMNS = 15;
}
