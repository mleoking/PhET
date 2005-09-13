/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.util;

/**
 * User: Sam Reid
 * Date: Jul 25, 2005
 * Time: 11:04:07 AM
 * Copyright (c) Jul 25, 2005 by Sam Reid
 */

public class QMLogger {
    private static boolean debuggingEnabled = true;

    public static void debug( String str ) {
        if( debuggingEnabled ) {
            System.out.println( str );
        }
    }

    public static boolean isDebuggingEnabled() {
        return debuggingEnabled;
    }

    public static void setDebuggingEnabled( boolean debuggingEnabled ) {
        QMLogger.debuggingEnabled = debuggingEnabled;
    }
}
