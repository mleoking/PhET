package edu.colorado.phet.phetupdater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is responsible for producing a log file to help debug issues with the update process.
 */
public class DebugLogger {
    private static File logFile = new File( System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-log.txt" );
    private static FileWriter fileWriter;

    static {
        System.out.println( "Inited data file: " + logFile.getAbsolutePath() );
        try {
            boolean a = logFile.createNewFile();
            fileWriter = new FileWriter( logFile, true );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Write a message to the console and the log file.
     *
     * @param message
     */
    public static void println( String message ) {
        try {
            fileWriter.write( message + "\n" );
            fileWriter.flush();
            System.out.println( message );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}