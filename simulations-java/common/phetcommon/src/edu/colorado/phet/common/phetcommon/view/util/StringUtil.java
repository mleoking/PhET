/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

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
        if (s.length() != 1) {
            System.err.println("StringUtil: " + s + " is not a character" );

            return defaultValue;
        }


        return s.charAt( 0 );
    }
}
