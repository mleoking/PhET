// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.util;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * A double formatter that provides special treatment of zero and integers.
 * Implemented via composition because NumberFormat.format is final.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SmartDoubleFormat {

    private DefaultDecimalFormat defaultFormat;
    private String pattern;
    private final boolean formatIntegersAsIntegers;
    private final boolean formatZeroAsInteger;

    /**
     * Constructor
     *
     * @param pattern                  the NumberFormat pattern used to format values
     * @param formatIntegersAsIntegers if true, format integers with no decimal places; otherwise format using pattern
     * @param showZeroAsInteger        if true, show 0 as "0"; otherwise format using pattern
     */
    public SmartDoubleFormat( String pattern, boolean formatIntegersAsIntegers, boolean showZeroAsInteger ) {
        this.defaultFormat = new DefaultDecimalFormat( pattern );
        this.pattern = pattern;
        this.formatIntegersAsIntegers = formatIntegersAsIntegers;
        this.formatZeroAsInteger = showZeroAsInteger;
    }

    // Constructor that behaves like a DefaultDecimalFormat, formatting all values using pattern.
    public SmartDoubleFormat( String pattern ) {
        this( pattern, false, false );
    }

    public String format( double value ) {
        String s;
        if ( value == 0 && formatZeroAsInteger ) {
            s = "0";
        }
        else if ( ( value % 1 == 0 ) && formatIntegersAsIntegers ) {
            s = String.valueOf( (int) value );
        }
        else {
            s = defaultFormat.format( value );
        }
        return s;
    }

    // Gets the number of columns needs to hold a value formatted with this formatter.
    public int getColumns() {
        return pattern.length();
    }
}
