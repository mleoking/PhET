// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Base class for all models in this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class RPALModel {
    
    public static final IntegerRange COEFFICIENT_RANGE = new IntegerRange( 0, 3, 0 );
    public static final IntegerRange QUANTITY_RANGE = new IntegerRange( 0, 10, 0 );
    
    public static IntegerRange getCoefficientRange() {
        return COEFFICIENT_RANGE;
    }
    
    public static IntegerRange getQuantityRange() {
        return QUANTITY_RANGE;
    }

}
