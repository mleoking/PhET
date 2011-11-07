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

    //Process all files in the specified directory, but not recursively
    public void processDir( File dir ) throws IOException {
        process( dir.listFiles() );
    }

    //Process a collection of files. Might be nice to plot multiple sessions together
    public void process( File... files ) throws IOException {

        ArrayList<EventLog> all = new ArrayList<EventLog>();

        for ( File file : files ) {
            EventLog log = processFile( file );
            all.add( log );
        }

        process( all );
    }

    public abstract void process( ArrayList<EventLog> all );

    //Process a single file
    public EventLog processFile( File file ) throws IOException {
        EventLog eventLog = parse( file );

        process( eventLog );

        return eventLog;
    }

    public static EventLog parse( File file ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );

        EventLog eventLog = new EventLog( file );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            eventLog.parseLine( line );
        }
        return eventLog;
    }

    public static ArrayList<EntryPair> pairs( EventLog eventLog ) {
        return new PairwiseProcessor().process( eventLog.getWithoutSystemEvents() );
    }

    public abstract void process( EventLog eventLog );
}