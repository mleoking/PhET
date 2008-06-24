package edu.colorado.phet.phscale.util;

import java.text.DecimalFormat;
import java.text.MessageFormat;


public class TimesTenFormat {
    
    private static final String FORMAT = "<html>{0} x 10<sup>{1}</sup></html>";

    private final DecimalFormat _format;
    
    public TimesTenFormat( String mantissaFormat ) {
        _format = new DecimalFormat( mantissaFormat + "E0" );
    }
    
    public String format( double value ) {
        String s = _format.format( value );
        int index = s.lastIndexOf( 'E' );
        String mantissa = s.substring( 0, index );
        String exponent = s.substring( index + 1 );
        Object[] args = { mantissa, exponent };
        return MessageFormat.format( FORMAT, args );
    }
}
