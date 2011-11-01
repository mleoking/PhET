// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main entry point for post-processing collected data from sim events.
 *
 * @author Sam Reid
 */
public abstract class Processor extends Predef {

    //Process a collection of files. Might be nice to plot multiple sessions together
    void process( File... files ) throws IOException {

        ArrayList<EventLog> all = new ArrayList<EventLog>();

        for ( File file : files ) {
            EventLog log = processFile( file );
            all.add( log );
        }

        process( all );
    }

    public abstract void process( ArrayList<EventLog> all );

    //Process a single file
    private EventLog processFile( File file ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );

        EventLog eventLog = new EventLog();
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            eventLog.parseLine( line );
        }

        process( eventLog );

        return eventLog;
    }

    public abstract void process( EventLog eventLog );
}