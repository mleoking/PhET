package edu.colorado.phet.common.phetcommon.util.logging;

/**
 * Allows enabling/disabling of logging messages to the console related to Updates & Statistics (US) features.
 * TODO: This class could be generalized to have a private implementation that takes a message and a key, and uses a lookup table on keys
 * to decide whether to output text.  The key in this case would be "USL" or we could have separate keys for "updates" "Statistics", etc.
 * so that console output could be enabled for each independently.
 */
public class USLogger {
    
    // to enable logging, use -log program arg on the command line
    private static final ILogger LOGGER = new ConsoleLogger( false );
    
    public static void setEnabled( boolean enabled ) {
        LOGGER.setEnabled( enabled );
    }

    public static void log( String message ) {
        LOGGER.log( message );
    }

    public static void warning( String message ) {
        LOGGER.warning( message );
    }

    public static void error( String message ) {
        LOGGER.error( message );
    }
    
    public static void main( String[] args ) {
        LOGGER.test();
    }

    public static void setLoggingEnabled( boolean enabled ) {
        LOGGER.setEnabled( enabled );
    }
}
