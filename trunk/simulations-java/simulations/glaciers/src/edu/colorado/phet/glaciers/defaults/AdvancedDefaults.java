/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * AdvancedDefaults contains default settings for AdvancedModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedDefaults {

    /* Not intended for instantiation */
    private AdvancedDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final double CLOCK_DT = GlobalDefaults.CLOCK_DT;
    public static final IntegerRange CLOCK_FRAME_RATE_RANGE = GlobalDefaults.CLOCK_FRAME_RATE_RANGE;
    public static final DecimalFormat CLOCK_DISPLAY_FORMAT = GlobalDefaults.CLOCK_DISPLAY_FORMAT;
    public static final int CLOCK_DISPLAY_COLUMNS = GlobalDefaults.CLOCK_DISPLAY_COLUMNS;
    public static final GlaciersClock CLOCK = new GlaciersClock( CLOCK_FRAME_RATE_RANGE.getDefault(), CLOCK_DT );
    
    // Valley 
    public static final double VALLEY_X_MIN = GlobalDefaults.VALLEY_X_MIN;
    public static final double VALLEY_X_MAX = GlobalDefaults.VALLEY_X_MAX;
    
    // Climate
    public static final DoubleRange SNOWFALL_RANGE = GlobalDefaults.SNOWFALL_RANGE; 
    public static final DoubleRange SNOWFALL_REFERENCE_ELEVATION_RANGE = GlobalDefaults.SNOWFALL_REFERENCE_ELEVATION_RANGE;
    public static final DoubleRange TEMPERATURE_RANGE = GlobalDefaults.TEMPERATURE_RANGE;
}
