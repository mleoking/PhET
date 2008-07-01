/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;


/**
 * Formats a number using a constant power of ten.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConstantPowerOfTenNumberFormat extends NumberFormat {
    
    private static final String FORMAT = "<html>{0} x 10<sup>{1}</sup></html>";

    private final DefaultDecimalFormat _decimalFormat;
    private boolean _simpleZeroFormat;
    private final int _constantExponent;
    
    /**
     * Constructor.
     * 
     * @param mantissaFormat format of the mantissa, specified using DecimalFormat's syntax
     * @param constantExponent exponent for the constant power of 10
     */
    public ConstantPowerOfTenNumberFormat( String mantissaFormat, int constantExponent ) {
        _decimalFormat = new DefaultDecimalFormat( mantissaFormat );
        _constantExponent = constantExponent;
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
            // determine the mantissa
            double mantissa = number / Math.pow( 10, _constantExponent );
            // use a DecimalFormat to format the mantissa
            String mantissaString = _decimalFormat.format( mantissa );
            // put the mantissa and exponent into our format
            Object[] args = { mantissaString, new Integer( _constantExponent ) };
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
    
    /* examples */
    public static void main( String[] args ) {
        
        final double zero = 0;
        final double value = 4.23E7;
        
        String p1 = "0.00";
        String p2 = "0.0";
        String p3 = "#.0";
        String p4 = "0";
        
        ConstantPowerOfTenNumberFormat f1 = new ConstantPowerOfTenNumberFormat( p1, 5 );
        f1.setSimpleZeroFormat( false );
        NumberFormat f2 = new ConstantPowerOfTenNumberFormat( p2, 5 );
        NumberFormat f3 = new ConstantPowerOfTenNumberFormat( p3, 5);
        NumberFormat f4 = new ConstantPowerOfTenNumberFormat( p4, 5 );
        
        System.out.println( "pattern=" + p1 + " value=" + zero  + " formatted=" + f1.format( zero ) );
        System.out.println( "pattern=" + p1 + " value=" + value + " formatted=" + f1.format( value ) );
        System.out.println( "pattern=" + p2 + " value=" + zero  + " formatted=" + f2.format( zero ) );
        System.out.println( "pattern=" + p2 + " value=" + value + " formatted=" + f2.format( value ) );
        System.out.println( "pattern=" + p3 + " value=" + zero  + " formatted=" + f3.format( zero ) );
        System.out.println( "pattern=" + p3 + " value=" + value + " formatted=" + f3.format( value ) );
        System.out.println( "pattern=" + p4 + " value=" + zero  + " formatted=" + f4.format( zero ) );
        System.out.println( "pattern=" + p4 + " value=" + value + " formatted=" + f4.format( value ) );
    }
}
