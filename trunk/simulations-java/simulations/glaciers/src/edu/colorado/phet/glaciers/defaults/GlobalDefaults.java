/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private! */ class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final double CLOCK_DT = 1; // years
    public static final IntegerRange CLOCK_FRAME_RATE_RANGE = new IntegerRange( 1, 24, 12 ); // frames per second (years per second)
    public static final DecimalFormat CLOCK_DISPLAY_FORMAT = new DecimalFormat( "0" );
    public static final int CLOCK_DISPLAY_COLUMNS = 10;
    
    // Climate
    public static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 13, 20, 19 );  // temperature at sea level (degrees C)
    public static final DoubleRange SNOWFALL_RANGE = new DoubleRange( 0, 1.5, 0.95 ); // average snow accumulation (meters/year)
}
