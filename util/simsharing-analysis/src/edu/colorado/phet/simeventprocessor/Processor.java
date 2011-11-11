// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Processor extends JavaPredef {

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

}