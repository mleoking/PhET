/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * BasicDefaults contains default settings for BasicModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicDefaults {

    /* Not intended for instantiation */
    private BasicDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = GlobalDefaults.CLOCK_FRAME_RATE;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final DoubleRange CLOCK_DT_RANGE = GlobalDefaults.CLOCK_DT_RANGE;
    public static final GlaciersClock CLOCK = new GlaciersClock( CLOCK_FRAME_RATE, CLOCK_DT_RANGE );
    
    // World dimensions
    public static final Dimension WORLD_SIZE = new Dimension( 1500, 1500 );

    // Climate
    public static final DoubleRange SNOWFALL_RANGE = GlobalDefaults.SNOWFALL_RANGE;
    public static final DoubleRange TEMPERATURE_RANGE = GlobalDefaults.TEMPERATURE_RANGE;
    public static final DoubleRange EQUILIBRIUM_LINE_ALTITUDE_RANGE = GlobalDefaults.EQUILIBRIUM_LINE_ALTITUDE_RANGE;
    public static final DoubleRange MASS_BALANCE_SLOPE_RANGE = GlobalDefaults.MASS_BALANCE_SLOPE_RANGE;
    public static final DoubleRange MAXIMUM_MASS_BALANCE_RANGE = GlobalDefaults.MAXIMUM_MASS_BALANCE_RANGE;
}
