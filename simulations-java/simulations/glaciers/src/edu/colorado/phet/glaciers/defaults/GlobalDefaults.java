/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;


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
    public static final int CLOCK_FRAME_RATE = 24; // fps, frames per second (wall time)
    public static final int CLOCK_TIME_COLUMNS = 10;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( 0.1, 10, 1 ); // years
    public static final DecimalFormat CLOCK_DISPLAY_FORMAT = new DecimalFormat( "0.0" );
    
    // Valley
    public static final double VALLEY_X_MIN = 0; // meters
    public static final double VALLEY_X_MAX = 80000; // meters
    
    // Climate
    public static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 13, 20, 19 );  // temperature at sea level (degrees C)
    public static final DoubleRange SNOWFALL_RANGE = new DoubleRange( 0, 2, 1 ); // snow accumulation (meters/year)
    public static final DoubleRange SNOWFALL_REFERENCE_ELEVATION_RANGE = new DoubleRange( 2200, 6000, 4000 ); // reference elevation for snowfall (meters)

}
