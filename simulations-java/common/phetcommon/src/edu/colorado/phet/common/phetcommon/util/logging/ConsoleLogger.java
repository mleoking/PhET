/* Copyright 2008, University of Colorado */
package edu.colorado.phet.common.phetcommon.util.logging;

public class ConsoleLogger implements ILogger{
    public void log( String text ) {
        System.out.println( text );
    }
}