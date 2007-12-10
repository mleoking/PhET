/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

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
    public static final String CLOCK_UNITS = GlobalDefaults.CLOCK_UNITS;
    public static final GlaciersClock CLOCK = new GlaciersClock( CLOCK_FRAME_RATE, CLOCK_DT_RANGE, CLOCK_UNITS );
    
    // World dimensions
    public static final Dimension WORLD_SIZE = new Dimension( 1500, 1500 );
    
    // Climate
    public static final DoubleRange SNOWFALL_RANGE = GlobalDefaults.SNOWFALL_RANGE;
    public static final DoubleRange TEMPERATURE_RANGE = GlobalDefaults.TEMPERATURE_RANGE;
}
