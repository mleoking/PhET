/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import edu.colorado.phet.phscale.util.PrecisionDecimal;


/**
 * PHValue is an immutable pH value that is constrained to some precision.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHValue extends PrecisionDecimal {
    
    private static final int NUMBER_OF_DECIMAL_PLACES = 2;

    public PHValue( double value ) {
        super( value, NUMBER_OF_DECIMAL_PLACES );
    }
}
