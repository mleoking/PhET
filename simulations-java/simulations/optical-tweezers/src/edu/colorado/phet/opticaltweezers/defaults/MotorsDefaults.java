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
    public static final OTClock CLOCK = new OTClock( GlobalDefaults.FRAME_RATE, 
            GlobalDefaults.SLOW_DT_RANGE, GlobalDefaults.FAST_DT_RANGE, GlobalDefaults.DEFAULT_DT );
}
