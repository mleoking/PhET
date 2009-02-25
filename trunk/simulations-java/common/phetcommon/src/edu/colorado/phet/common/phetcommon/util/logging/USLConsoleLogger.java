package edu.colorado.phet.common.phetcommon.util.logging;

/**
 * Allows enabling/disabling of logging messages to the console related to Updates, Statistics, License features.
 * TODO: This class could be generalized to have a private implementation that takes a message and a key, and uses a lookup table on keys
 * to decide whether to output text.  The key in this case would be "USL" or we could have separate keys for "updates" "Statistics", etc.
 * so that console output could be enabled for each independently.
 */
public class USLConsoleLogger {

    private static final boolean displayOutputToConsole = false;
    private static final Impl instance = new Impl();

    public static void log( String message ) {
        instance.log( message );
    }

    public static void logError( String message ) {
        instance.logError( message );
    }

    private static class Impl extends ConsoleLogger {
        public void log( String message ) {
            if ( displayOutputToConsole ) {
                super.log( message );
            }
        }

        public void logError( String message ) {
            if ( displayOutputToConsole ) {
                super.logError( message );
            }
        }
    }
}
