/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.glaciers.model.Climate;


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
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final int CLOCK_TIME_COLUMNS = 10;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( 1, 100, 1 ); // years
    
    // Valley 
    public static final double VALLEY_X_MIN = 0; // meters
    public static final double VALLEY_X_MAX = 80000; // meters
    
    // Climate -- snowfall & temperature view
    public static final DoubleRange SNOWFALL_RANGE = new DoubleRange( 0, 3, 1 ); // snow accumulation (meters/year)
    public static final DoubleRange SNOWFALL_REFERENCE_ELEVATION_RANGE = new DoubleRange( 0, 7E3, Climate.getModernSnowfallReferenceElevation() ); // reference elevation for snowfall (meters)
    public static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 5, 30, Climate.getModernTemperature() );  // temperature at sea level (degrees C)
    
    // Climate -- mass balance view
    public static final DoubleRange MAXIMUM_SNOWFALL_RANGE = new DoubleRange( 2 * SNOWFALL_RANGE.getMin(), 2 * SNOWFALL_RANGE.getMax(), 2 * SNOWFALL_RANGE.getDefault() ); //XXX mapping should be encapsulated in Climate 
    public static final DoubleRange EQUILIBRIUM_LINE_ALTITUDE_RANGE = new DoubleRange( 0, 10E3, 0 ); //XXX should be derived from other climate domain
}
