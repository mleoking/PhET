/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * User: Sam Reid
 * Date: Dec 3, 2003
 * Time: 9:50:25 AM
 * Copyright (c) Dec 3, 2003 by Sam Reid
 */
public class TestLogger {
    public static void main( String[] args ) {
        Logger log = Logger.getLogger( "tester.logger" );
        log.setLevel( Level.ALL );

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel( Level.ALL );
        log.addHandler( ch );
        log.log( new LogRecord( Level.FINEST, "hey there" ) );
        log.fine( "Hello" );
        ch.flush();
    }
}
