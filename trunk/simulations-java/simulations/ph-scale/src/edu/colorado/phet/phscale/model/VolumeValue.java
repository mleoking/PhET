/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import edu.colorado.phet.phscale.util.PrecisionDecimal;


/**
 * VolumeValue is an immutable volume value that is constrained to some precision.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VolumeValue extends PrecisionDecimal {
    
    private static final int NUMBER_OF_DECIMAL_PLACES = 2;

    public VolumeValue( double value ) {
        super( value, NUMBER_OF_DECIMAL_PLACES );
    }
}
