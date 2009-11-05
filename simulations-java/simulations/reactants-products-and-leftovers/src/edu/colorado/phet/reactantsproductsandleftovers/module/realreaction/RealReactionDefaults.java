/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;



/**
 * Defaults for "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionDefaults {

    /* Not intended for instantiation */
    private RealReactionDefaults() {}
    
    public static final IntegerRange COEFFICIENT_RANGE = new IntegerRange( 0, 3, 0 );
    public static final IntegerRange QUANTITY_RANGE = new IntegerRange( 0, 10, 0 );
}
