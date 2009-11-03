/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util.logging;

public class NullLogger extends AbstractLogger {

    public NullLogger() {
        super( false );
    }
    
    public void log( String message ) {}
    
    public static void main( String[] args ) {
        ILogger logger = new NullLogger();
        logger.test();
    }
}
