// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main entry point for post-processing collected data from sim events.
 *
 * @author Sam Reid
 */
public class Processor {

    //Process a collection of files. Might be nice to plot multiple sessions together
    void process( File... files ) throws IOException {
        for ( File file : files ) {
            processFile( file );
        }
    }

    //Process a single file
    private void processFile( File file ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );

        ParseState parseState = new ParseState();
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            parseState.parseLine( line );
        }

        parseState.parseFinished();
    }

    public static void main( String[] args ) throws IOException {
        new Processor().process( new File( "C:\\Users\\Sam\\Desktop\\biglog1.txt" ) );
    }
}