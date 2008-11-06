package edu.colorado.phet.updater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DebugLogger {
    
    public static File logFile = new File( System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "updater-log.txt" );
    private static FileWriter fileWriter;

    static {
        System.out.println( "Inited data file: " + logFile.getAbsolutePath() );
        try {
            logFile.createNewFile();
            fileWriter = new FileWriter( logFile, true );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

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