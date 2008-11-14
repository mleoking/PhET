/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {
    private StringUtil() {
    }

    public static int asInt( String s, int defaultValue ) {
        int value;

        try {
            value = Integer.parseInt( s );
        }
        catch( NumberFormatException nfe ) {
            System.err.println( "StringUtil: " + s + " is not an int" );
            value = defaultValue;
        }

        return value;
    }

    public static double asDouble( String s, double defaultValue ) {
        double value;

        try {
            value = Double.parseDouble( s );
        }
        catch( NumberFormatException nfe ) {
            System.err.println( "StringUtil: " + s + " is not a double" );
            value = defaultValue;
        }

        return value;
    }

    public static char asChar( String s, char defaultValue ) {
        if ( s.length() != 1 ) {
            System.err.println( "StringUtil: " + s + " is not a character" );

            return defaultValue;
        }


        return s.charAt( 0 );
    }
    
    /**
     * Converts an exception's stack trace to a string.
     * Useful for displaying strings in error dialogs.
     * 
     * @param e
     * @return String
     */
    public static String stackTraceToString( Exception e ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        return sw.toString();
    }
}
