/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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
    public static final int CLOCK_FRAME_RATE = GlobalDefaults.CLOCK_FRAME_RATE;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final DoubleRange CLOCK_DT_RANGE = GlobalDefaults.CLOCK_DT_RANGE;
    public static final GlaciersClock CLOCK = new GlaciersClock( CLOCK_FRAME_RATE, CLOCK_DT_RANGE );
    
    // Valley 
    public static final double VALLEY_X_MAX = GlobalDefaults.VALLEY_X_MAX;
    
    // Climate
    public static final DoubleRange SNOWFALL_LAPSE_RATE_RANGE = GlobalDefaults.SNOWFALL_LAPSE_RATE_RANGE;
    public static final DoubleRange TEMPERATURE_OFFSET_RANGE = GlobalDefaults.TEMPERATURE_OFFSET_RANGE;
}
