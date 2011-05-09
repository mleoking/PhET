/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A number formatter that formats a number and also takes a specification for
 * the number of decimal places.
 *
 * @author John Blanco
 */
class VariablePrecisionNumberFormat {

    private static final NumberFormat FORMAT_FOR_100 = new DecimalFormat( "0" );

    static public String format( double value, int decimalDigits ) {
        if ( value == 100 ){
            return FORMAT_FOR_100.format( value );
        }
        else {
            NumberFormat variableLengthFormat = new DecimalFormat( createVariableLengthFormatString( decimalDigits ) );
            return variableLengthFormat.format( value );
        }
    }

    static private String createVariableLengthFormatString( int numDecimalDigits ){
        String formatString = "0.";
        for ( int i = 0; i < numDecimalDigits; i++){
            formatString = formatString.concat( "0" );
        }
        return formatString;
    }
}