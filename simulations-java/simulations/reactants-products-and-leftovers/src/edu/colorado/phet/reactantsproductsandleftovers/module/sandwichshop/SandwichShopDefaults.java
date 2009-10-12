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
    
    private static final IntegerRange FORMULA_REACTANT_RANGE = new IntegerRange( 0, 3, 1 );
    public static final IntegerRange FORMULA_BREAD_RANGE = FORMULA_REACTANT_RANGE;
    public static final IntegerRange FORMULA_MEAT_RANGE = FORMULA_REACTANT_RANGE;
    public static final IntegerRange FORMULA_CHEESE_RANGE = FORMULA_REACTANT_RANGE;
    
    private static final IntegerRange REACTION_REACTANT_RANGE = new IntegerRange( 0, 10, 0 );
    public static final IntegerRange REACTION_BREAD_RANGE = REACTION_REACTANT_RANGE;
    public static final IntegerRange REACTION_MEAT_RANGE = REACTION_REACTANT_RANGE;
    public static final IntegerRange REACTION_CHEESE_RANGE = REACTION_REACTANT_RANGE;
}
