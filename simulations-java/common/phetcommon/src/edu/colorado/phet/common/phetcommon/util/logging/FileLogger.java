package edu.colorado.phet.common.phetcommon.util.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Logs to a file.
 */
public class FileLogger implements ILogger {
    
    private FileWriter fileWriter;
    
    public FileLogger( String logFileName, boolean append ) {
        this( new File( logFileName ), append );
    }

    public FileLogger( File logFile, boolean append ) {
        try {
            logFile.createNewFile(); // creates a new file if the file doesn't already exist
            fileWriter = new FileWriter( logFile, append );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void log( String text ) {
        try {
            fileWriter.write( text + "\n" );
            fileWriter.flush();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
    public void logError( String text ) {
        log( "ERROR: " + text );
    }
    
    public static void main( String[] args ) {
        String logFileName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "log-test.txt";
        ILogger logger = new FileLogger( logFileName, true /* append */ );
        logger.log( "good news" );
        logger.logError( "bad news" );
    }

}
