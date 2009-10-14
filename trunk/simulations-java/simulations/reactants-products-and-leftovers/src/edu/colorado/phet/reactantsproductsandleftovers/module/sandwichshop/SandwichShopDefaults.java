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
    
    private static final IntegerRange COEFFICIENT_RANGE = new IntegerRange( 0, 3, 1 );
    public static final IntegerRange BREAD_COEFFICIENT_RANGE = COEFFICIENT_RANGE;
    public static final IntegerRange MEAT_COEFFICIENT_RANGE = COEFFICIENT_RANGE;
    public static final IntegerRange CHEESE_COEFFICIENT_RANGE = COEFFICIENT_RANGE;
    
    private static final IntegerRange REACTANT_QUANTITY_RANGE = new IntegerRange( 0, 10, 0 );
    public static final IntegerRange BREAD_QUANTITY_RANGE = REACTANT_QUANTITY_RANGE;
    public static final IntegerRange MEAT_QUANTITY_RANGE = REACTANT_QUANTITY_RANGE;
    public static final IntegerRange CHEESE_QUANTITY_RANGE = REACTANT_QUANTITY_RANGE;
}
