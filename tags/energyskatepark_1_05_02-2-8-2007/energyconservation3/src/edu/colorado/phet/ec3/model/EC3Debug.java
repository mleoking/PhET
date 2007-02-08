/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 12:22:46 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class EC3Debug {
    //    private static boolean debug = true;
    private static boolean debug = false;

    public static void debug( String s ) {
        if( debug ) {
            System.out.println( s );
        }
    }
}
