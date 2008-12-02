/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util.logging;

public class ConsoleLogger implements ILogger{
    
    public void log( String text ) {
        System.out.println( text );
        System.out.flush();
    }
    
    public void logError( String text ) {
        System.err.println( text );
        System.err.flush();
    }
    
    public static void main( String[] args ) {
        ILogger logger = new ConsoleLogger();
        logger.log( "good news" );
        logger.logError( "bad news" );
    }
}