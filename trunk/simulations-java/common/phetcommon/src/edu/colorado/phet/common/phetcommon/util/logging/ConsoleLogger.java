/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util.logging;

import java.io.PrintStream;

public class ConsoleLogger extends AbstractLogger {
    
    public ConsoleLogger() {
        super( true /* enabled */ );
    }
    
    public ConsoleLogger( boolean enabled ) {
        super( enabled );
    }
    
    public void log( String message ) {
        log( message, System.out );
    }
    
    public void error( String message ) {
        log( "ERROR: " + message , System.err );
    }
    
    private void log( String message, PrintStream printStream ) {
        if ( isEnabled() ) {
            printStream.println( message );
            printStream.flush();
        }
    }

    public static void main( String[] args ) {
        ILogger logger = new ConsoleLogger();
        logger.test();
    }
}