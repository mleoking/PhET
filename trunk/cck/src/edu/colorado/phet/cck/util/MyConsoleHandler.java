/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.util;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * User: Sam Reid
 * Date: Dec 3, 2003
 * Time: 11:59:53 PM
 * Copyright (c) Dec 3, 2003 by Sam Reid
 */
public class MyConsoleHandler extends Handler {
    PrintStream output;
    Formatter formatter;

    public MyConsoleHandler() {
        this( System.err, new SimpleFormatter() );
    }

    public MyConsoleHandler( Formatter formatter ) {
        this( System.err, formatter );
    }

    public MyConsoleHandler( PrintStream output, Formatter formatter ) {
        this.output = output;
        this.formatter = formatter;
    }

    public void publish( LogRecord record ) {
        String out = formatter.format( record );
        output.println( out );
    }

    public void flush() {
        output.flush();
    }

    public void close() throws SecurityException {
//        super.sealed=true
        output.close();
    }


}


