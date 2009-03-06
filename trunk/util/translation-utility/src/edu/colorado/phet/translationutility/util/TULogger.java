package edu.colorado.phet.translationutility.util;

import edu.colorado.phet.common.phetcommon.util.logging.ConsoleLogger;
import edu.colorado.phet.common.phetcommon.util.logging.ILogger;


public class TULogger {

    private static final ILogger LOGGER = new ConsoleLogger( true ); // enable using -log commandline arg
    
    private static final String MESSAGE_PREFIX = "TULogger: ";
    
    private TULogger() {}
    
    public static void setEnabled( boolean enabled ) {
        LOGGER.setEnabled( enabled );
        log( "console logging enabled=" + enabled );
    }
    
    public static boolean isEnabled() {
        return LOGGER.isEnabled();
    }

    public static void log( String message ) {
        LOGGER.log( MESSAGE_PREFIX + message );
    }

    public static void warning( String message ) {
        LOGGER.warning( MESSAGE_PREFIX + message );
    }

    public static void error( String message ) {
        LOGGER.error( MESSAGE_PREFIX + message );
    }
    
    public static void test() {
        log( "test" );
        warning( "test warning" );
        error( "test error" );
    }
    
    public static void main( String[] args ) {
        TULogger.test();
    }
}
