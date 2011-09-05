// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.akka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Sam Reid
 */
public class SimSharing {
    static boolean cleanup = false;

    /**
     * To run Akka, need to point to config files.  So we copy them out of the jar file and change System.properties at runtime.
     * This requires permissions.
     *
     * @throws IOException
     */
    public static void init() throws IOException {
        File tmpPhetDir = new File( System.getProperty( "java.io.tmpdir" ), "phet" );
        tmpPhetDir.mkdirs();

        //Copy akka.conf to the temp directory.
        {
            File akkaFile = new File( tmpPhetDir, "akka.conf" );
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( akkaFile ) );
            bufferedWriter.write( toString( Thread.currentThread().getContextClassLoader().getResourceAsStream( "simsharing/akka.conf" ) ) );
            bufferedWriter.close();

            //Register the copied config file with the system
            System.setProperty( "akka.config", akkaFile.getAbsolutePath() );

            //For debugging
            System.out.println( "Set akka.config = " + akkaFile.getAbsolutePath() );

            if ( cleanup ) {
                akkaFile.deleteOnExit();
            }
        }

        File logbackFile = new File( tmpPhetDir, "logback.xml" );
        BufferedWriter logbackWriter = new BufferedWriter( new FileWriter( logbackFile ) );
        logbackWriter.write( toString( Thread.currentThread().getContextClassLoader().getResourceAsStream( "simsharing/logback.xml" ) ) );
        logbackWriter.close();
        System.setProperty( "logback.configurationFile", logbackFile.getAbsolutePath() );

        System.out.println( "created temp dir: " + tmpPhetDir.getAbsolutePath() );
        System.out.println( "set property akka.config = " + System.getProperty( "akka.config" ) );
        System.out.println( "set property logback.configurationFile = " + System.getProperty( "logback.configurationFile" ) );

        if ( cleanup ) {
            tmpPhetDir.deleteOnExit();
            logbackFile.deleteOnExit();
        }
    }

    public static void main( String[] args ) throws IOException {
        SimSharing.init();
    }

    //http://www.kodejava.org/examples/266.html
    public static String toString( InputStream is )
            throws IOException {
        /*
        * To convert the InputStream to String we use the
        * Reader.read(char[] buffer) method. We iterate until the
        * Reader return -1 which means there's no more data to
        * read. We use the StringWriter class to produce the string.
        */
        if ( is != null ) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
                int n;
                while ( ( n = reader.read( buffer ) ) != -1 ) {
                    writer.write( buffer, 0, n );
                }
            }
            finally {
                is.close();
            }
            return writer.toString();
        }
        else {
            return "";
        }
    }

}
