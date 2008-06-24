package edu.colorado.phet.phscale.util;

import java.text.DecimalFormat;
import java.text.MessageFormat;


public class PowersOfTenFormat {
    
    private static final String FORMAT = "<html>{0} x 10<sup>{1}</sup></html>";

    private final DecimalFormat _format;
    
    public PowersOfTenFormat( String pattern ) {
        _format = new DecimalFormat( pattern );
    }
    
    public String format( double value ) {
        String s = _format.format( value );
        int index = s.lastIndexOf( 'E' );
        String coefficient = s.substring( 0, index );
        String exponent = s.substring( index + 1 );
        Object[] args = { coefficient, exponent };
        return MessageFormat.format( FORMAT, args );
    }
}
