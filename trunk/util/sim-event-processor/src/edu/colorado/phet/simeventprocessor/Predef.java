// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.text.DecimalFormat;

/**
 * @author Sam Reid
 */
public class Predef {

    public static void println() {
        println( "" );
    }

    public static void println( String string ) {
        System.out.println( string );
    }

    public static String format( long vaule ) {
        return new DecimalFormat( "0.00" ).format( vaule );
    }
}
