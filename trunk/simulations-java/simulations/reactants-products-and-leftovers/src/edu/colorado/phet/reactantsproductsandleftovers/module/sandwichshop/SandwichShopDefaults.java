/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;




/**
 * Defaults for "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopDefaults {

    /* Not intended for instantiation */
    private SandwichShopDefaults() {}
    
    private static final IntegerRange REACTANT_RANGE = new IntegerRange( 1, 4, 1 );
    public static final IntegerRange BREAD_RANGE = REACTANT_RANGE;
    public static final IntegerRange MEAT_RANGE = REACTANT_RANGE;
    public static final IntegerRange CHEESE_RANGE = REACTANT_RANGE;
}
