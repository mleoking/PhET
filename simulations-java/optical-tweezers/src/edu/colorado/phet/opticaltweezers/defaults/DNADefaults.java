/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;

import edu.colorado.phet.common.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * DNADefaults contains default settings for the "Fun with DNA" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNADefaults {

    /* Not intended for instantiation */
    private DNADefaults() {}
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 750, 750 );
    public static final double MODEL_TO_VIEW_SCALE = 0.5;
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final boolean CLOCK_PAUSED = false ;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( 1, 1, 1, 0 );
    public static final OTClock CLOCK = new OTClock( CLOCK_FRAME_RATE, CLOCK_DT_RANGE );
}
