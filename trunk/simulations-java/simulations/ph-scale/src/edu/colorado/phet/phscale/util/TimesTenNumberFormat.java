/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * TimesTenNumberFormat displays numbers in this format:
 * <code>
 * M x 10<sup>E</sup>
 * </code>
 * where M is the mantissa and E is some exponent.
 * <p>
 * Zero is optionally formatted as 0.
 * <p>
 * The implementation of the NumberFormat interface is currently incomplete.
 * It is recommended that you restrict use to format(double).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TimesTenNumberFormat extends NumberFormat {
    
    private static final String FORMAT = "<html>{0} x 10<sup>{1}</sup></html>";

    private final DefaultDecimalFormat _decimalFormat;
    private boolean _simpleZeroFormat;
    
    /**
     * Constructor.
     * 
     * @param mantissaFormat format of the mantissa, specified using DecimalFormat's syntax
     */
    public TimesTenNumberFormat( String mantissaFormat ) {
        _decimalFormat = new DefaultDecimalFormat( mantissaFormat + "E0" );
        _simpleZeroFormat = true;
    }
    
    /**
     * Specifies whether to display zero as "0".
     * If true, display zero as "0"; this is the default.
     * If false, display zero in the same format as other values.
     * 
     * @param b
     */
    public void setSimpleZeroFormat( boolean b ) {
        _simpleZeroFormat = b;
    }
    
    /**
     * Is zero displayed as "0" ?
     * 
     * @return true or false
     */
    public boolean isSimpleZeroFormat() {
        return _simpleZeroFormat;
    }
    
    /**
     * TODO: handle the pos argument
     */
    public StringBuffer format( double number, StringBuffer toAppendTo, FieldPosition pos ) {
        String valueString = "0";
        if ( number != 0 || !_simpleZeroFormat ) {
            String scientificString = _decimalFormat.format( number );
            int index = scientificString.lastIndexOf( 'E' );
            String mantissa = scientificString.substring( 0, index );
            String exponent = scientificString.substring( index + 1 );
            Object[] args = { mantissa, exponent };
            valueString = MessageFormat.format( FORMAT, args );
        }
        toAppendTo.append( valueString );
        return toAppendTo;
    }

    /**
     * TODO: handle anything that needs to be done specially for long values
     */
    public StringBuffer format( long number, StringBuffer toAppendTo, FieldPosition pos ) {
        return format( (double)number, toAppendTo, pos );
    }

    /**
     * TODO: support this
     */
    public Number parse( String source, ParsePosition parsePosition ) {
        throw new UnsupportedOperationException( "not supported" );
    }
}
