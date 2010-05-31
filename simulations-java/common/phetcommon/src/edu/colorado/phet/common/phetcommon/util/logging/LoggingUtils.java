
package edu.colorado.phet.common.phetcommon.util.logging;

import java.util.logging.*;

public class LoggingUtils {

    /**
     * Enable logging of all levels for the particular package name
     */
    public static void enableAllLogging( String packageName ) {
        /*
        The process for enabling log levels is a bit complicated. setting levels on the logger and all of its handlers
        does not seem to enable the FINE level. The only way I have found that works is adding another handler to it.
         */

        Logger logger = getLogger( packageName );

        // the logger should receive all messages
        logger.setLevel( Level.ALL );

        ConsoleHandler handler = new ConsoleHandler();

        // add the handler before mutating it so it gets the anonymous logger permissons, according to http://www.velocityreviews.com/forums/t302488-logger-info.html
        logger.addHandler( handler );

        // the handler should output all messages
        handler.setLevel( Level.ALL );
        // that even is not enough, we need to set a filter that won't double the log messages
        handler.setFilter( new Filter() {

            public boolean isLoggable( LogRecord logRecord ) {
                return logRecord.getLevel() == Level.FINE || logRecord.getLevel() == Level.FINER || logRecord.getLevel() == Level.FINEST;
            }
        } );
    }

    /**
     * Obtain a logger, ignoring the specified namespace.  This method centralizes log creation to make it easy to
     * specify configuration such as whether it is anonymous or not, see #2386
     *
     * @param name the name of the log to create or access, currently ignored.
     * @return the log
     */
    public static Logger getLogger( String name ) {
        return Logger.getAnonymousLogger( null );
    }
}
