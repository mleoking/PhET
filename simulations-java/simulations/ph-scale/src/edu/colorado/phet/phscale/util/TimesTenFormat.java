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
        String valueString = "0";
        if ( value != 0 ) {
            String scientificString = _format.format( value );
            int index = scientificString.lastIndexOf( 'E' );
            String mantissa = scientificString.substring( 0, index );
            String exponent = scientificString.substring( index + 1 );
            Object[] args = { mantissa, exponent };
            valueString = MessageFormat.format( FORMAT, args );
        }
        return valueString;
    }
}
