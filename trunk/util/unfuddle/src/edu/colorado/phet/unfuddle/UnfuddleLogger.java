// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.unfuddle;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized code for creating loggers so that we can ensure they have a consistent logging level.
 *
 * @author Sam Reid
 */
public class UnfuddleLogger {

    //Get a logger that will log all messages
    public static Logger getLogger( Class clazz ) {
        final Logger logger = Logger.getLogger( clazz.getCanonicalName() );
        logger.setLevel( Level.ALL );
        return logger;
    }
}
