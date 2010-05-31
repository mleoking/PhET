package edu.colorado.phet.common.phetcommon.util.logging;

import java.util.logging.*;

public class LoggingUtils {
    private static boolean attemptChangesToLoggingControlProperties = false;//see #2386

    /**
     * Enable logging of all levels for the particular package name
     */
    public static void enableAllLogging( String packageName ) {
        /*
        The process for enabling log levels is a bit complicated. setting levels on the logger and all of its handlers
        does not seem to enable the FINE level. The only way I have found that works is adding another handler to it.
         */

        Logger logger = Logger.getLogger( packageName );

        // the logger should receive all messages
        setLevel(logger,Level.ALL);

        ConsoleHandler handler = new ConsoleHandler();

        // the handler should output all messages
        setLevel(handler,Level.ALL);
        if (attemptChangesToLoggingControlProperties){
    
            // that even is not enough, we need to set a filter that won't double the log messages
            handler.setFilter( new Filter() {
                public boolean isLoggable( LogRecord logRecord ) {
                    return logRecord.getLevel() == Level.FINE || logRecord.getLevel() == Level.FINER || logRecord.getLevel() == Level.FINEST;
                }
            } );

            // add the handler
            logger.addHandler( handler );
        }
    }

    private static void setLevel(Handler handler, Level level) {
        if (attemptChangesToLoggingControlProperties){
            handler.setLevel(level);
        }
    }

    public static void setLevel(Logger logger, Level level) {
        if (attemptChangesToLoggingControlProperties){
            logger.setLevel(level);
        }
    }
}
