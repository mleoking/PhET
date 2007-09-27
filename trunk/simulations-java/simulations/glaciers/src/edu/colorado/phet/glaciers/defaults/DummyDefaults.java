/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.awt.Dimension;

import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * DummyDefaults
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DummyDefaults {

    /* Not intended for instantiation */
    private DummyDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = GlobalDefaults.CLOCK_FRAME_RATE;
    public static final double CLOCK_DT = GlobalDefaults.CLOCK_DT;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final GlaciersClock CLOCK = new GlaciersClock( CLOCK_FRAME_RATE, CLOCK_DT );
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 750, 750 );
    public static final double MODEL_TO_VIEW_SCALE = 0.5;
}
