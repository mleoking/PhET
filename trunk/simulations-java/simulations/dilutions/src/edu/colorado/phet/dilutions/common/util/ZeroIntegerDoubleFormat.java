// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.util;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * A double formatter that provides special treatment of zero, formatting it as "0".
 * Implemented via composition because NumberFormat.format is final.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZeroIntegerDoubleFormat {

    private DefaultDecimalFormat defaultFormat;

    /**
     * Constructor
     *
     * @param pattern the NumberFormat pattern used to format non-zero values
     */
    public ZeroIntegerDoubleFormat( String pattern ) {
        this.defaultFormat = new DefaultDecimalFormat( pattern );
    }

    // Zero is formatted as "0", other values are formatted using pattern.
    public String format( double value ) {
        String s;
        if ( value == 0 ) {
            s = "0";
        }
        else {
            s = defaultFormat.format( value );
        }
        return s;
    }
}
