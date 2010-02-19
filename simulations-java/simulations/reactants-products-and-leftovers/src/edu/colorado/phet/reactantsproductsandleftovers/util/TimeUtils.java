/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.util;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * Utilities for dealing with time.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TimeUtils {
    
    private static final NumberFormat ONE_DIGIT_TIME_FORMAT = new DecimalFormat( "0" );
    private static final NumberFormat TWO_DIGIT_TIME_FORMAT = new DecimalFormat( "00" );

    public static String getTimeString( long elapsedMillis ) {
        String s = "";
        int hours = (int) ( elapsedMillis / ( 1000 * 60 * 60 ) );
        int minutes = (int) ( ( elapsedMillis % ( 1000 * 60 * 60 ) ) / ( 1000 * 60 ) );
        int seconds = (int) ( ( ( elapsedMillis % ( 1000 * 60 * 60 ) ) % ( 1000 * 60 ) ) / 1000 );
        if ( hours > 0 ) {
            // hours:minutes:seconds
            Object[] args = { ONE_DIGIT_TIME_FORMAT.format( hours ), TWO_DIGIT_TIME_FORMAT.format( minutes ), TWO_DIGIT_TIME_FORMAT.format( seconds ) };
            s = MessageFormat.format( "{0}:{1}:{2}", args );
        }
        else {
            // minutes:seconds
            Object[] args = { ONE_DIGIT_TIME_FORMAT.format( minutes ), TWO_DIGIT_TIME_FORMAT.format( seconds ) };
            s = MessageFormat.format( "{0}:{1}", args );
        }
        return s;
    }
}
