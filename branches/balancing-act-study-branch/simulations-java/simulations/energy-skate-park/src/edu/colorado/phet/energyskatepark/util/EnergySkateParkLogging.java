// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.util;

public class EnergySkateParkLogging {
    private static final boolean debugging = false;

    public static void println( String s ) {
        if ( debugging ) {
            System.out.println( s );
        }
    }

    public static void println( double s ) {
        println( s + "" );
    }
}
