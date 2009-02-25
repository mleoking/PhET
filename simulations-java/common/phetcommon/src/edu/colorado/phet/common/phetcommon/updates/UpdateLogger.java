/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.util.logging.ConsoleLogger;
import edu.colorado.phet.common.phetcommon.util.logging.FileLogger;
import edu.colorado.phet.common.phetcommon.util.logging.ILogger;


/**
 * This class is responsible for producing a log file to help debug issues with the update process.
 */
public class UpdateLogger {

    private static final String LOG_FILE_NAME = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-log.txt";

    private static final ILogger FILE_LOGGER = new FileLogger( LOG_FILE_NAME, true /* append */ );
    private static final ILogger CONSOLE_LOGGER = new ConsoleLogger();
    private static boolean ECHO_TO_CONSOLE = false;

    /* not intended for instantiation */
    private UpdateLogger() {
    }

    /**
     * Write a message to the console (if enabled) and the log file.
     *
     * @param message
     */
    public static void log( String message ) {
        if ( ECHO_TO_CONSOLE ) {
            CONSOLE_LOGGER.log( message );
        }
        FILE_LOGGER.log( message );
    }
}