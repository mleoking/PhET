/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Defaults for "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameDefaults {

    /* Not intended for instantiation */
    private GameDefaults() {}
    
    public static final IntegerRange COEFFICIENT_RANGE = new IntegerRange( 0, 3, 0 );
    public static final IntegerRange QUANTITY_RANGE = new IntegerRange( 0, 10, 0 );
}
