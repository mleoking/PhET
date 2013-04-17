// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.util;

import java.text.DecimalFormat;

/**
 * ScientificNotation
 *
 * @author Ron LeMaster
 */
public class ScientificNotation {

    /**
     * @param num
     * @param numSignificantDigits
     * @param prefix
     * @param suffix
     * @return
     */
    public static String toHtml( double num, int numSignificantDigits, String prefix, String suffix ) {
        String s = null;

        StringBuffer formatString = new StringBuffer( "0." );
        for ( int i = 0; i < numSignificantDigits; i++ ) {
            formatString.append( '0' );
        }
        formatString.append( "E0" );
        DecimalFormat format = new DecimalFormat( formatString.toString() );
        String str = format.format( num );
        String mant = str.substring( 0, str.indexOf( 'E' ) );
        String exp = str.substring( str.indexOf( 'E' ) + 1 );

        s = ( "<html>" + prefix + mant + "x10<sup>" + exp + "</sup>" + suffix + "</html>" );
        return s;
    }
}
