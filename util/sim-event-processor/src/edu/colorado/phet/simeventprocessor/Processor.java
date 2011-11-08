// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main entry point for post-processing collected data from sim events.
 *
 * @author Sam Reid
 */
public abstract class Processor extends JavaPredef {

    //Process all files in the specified directory, but not recursively
    public void processDir( File dir ) throws IOException {
        process( dir.listFiles() );
    }

    //Process a collection of files. Might be nice to plot multiple sessions together
    public void process( File... files ) throws IOException {

        ArrayList<JavaEventLog> all = new ArrayList<JavaEventLog>();

        for ( File file : files ) {
            JavaEventLog log = processFile( file );
            all.add( log );
        }

        process( all );
    }

    public abstract void process( ArrayList<JavaEventLog> all );

    //Process a single file
    public JavaEventLog processFile( File file ) throws IOException {
        JavaEventLog eventLog = parse( file );

        process( eventLog );

        return eventLog;
    }

    public static JavaEventLog[] load( String file ) throws IOException {
        return load( new File( file ) );
    }

    //Loads all logs from a directory, recursively
    public static JavaEventLog[] load( File file ) throws IOException {
        ArrayList<JavaEventLog> all = new ArrayList<JavaEventLog>();
        File[] f = file.listFiles();
        for ( File aFile : f ) {
            if ( aFile.isFile() ) {
                JavaEventLog parsed = parse( aFile );
                all.add( parsed );
            }
            else {
                all.addAll( Arrays.asList( load( aFile ) ) );
            }
        }
        return all.toArray( new JavaEventLog[all.size()] );
    }

    public static JavaEventLog parse( File file ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );

        JavaEventLog eventLog = new JavaEventLog( file );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            eventLog.parseLine( line );
        }
        return eventLog;
    }

    public static ArrayList<EntryPair> pairs( JavaEventLog eventLog ) {
        return new PairwiseProcessor().process( eventLog.getWithoutSystemEvents() );
    }

    public abstract void process( JavaEventLog eventLog );
}