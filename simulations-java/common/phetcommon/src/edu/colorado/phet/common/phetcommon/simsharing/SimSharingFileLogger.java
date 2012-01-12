// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.PhetExit;

/**
 * Sends messages to a log file, see #3215
 *
 * @author Sam Reid
 */
public class SimSharingFileLogger {
    private BufferedWriter logWriter;
    private final String machineCookie;
    private final String sessionId;

    public SimSharingFileLogger( String machineCookie, String sessionId ) {
        this.machineCookie = machineCookie;
        this.sessionId = sessionId;
    }

    private void createLogWriter() {
        File file = new File( System.getProperty( "user.home" ), "phet-logs/" +
                                                                 new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" ).format( new Date() ) + "_" + machineCookie + "_" + sessionId + ".txt" );
        file.getParentFile().mkdirs();
        System.out.println( "Logging to file: " + file );
        try {
            file.createNewFile();
            logWriter = new BufferedWriter( new FileWriter( file, true ) );

            PhetExit.addExitListener( new VoidFunction0() {
                public void apply() {
                    try {
                        logWriter.close();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void sendToLogFile( AugmentedMessage message ) throws IOException {
        if ( logWriter == null ) {
            createLogWriter();
        }
        if ( logWriter != null ) {
            logWriter.write( message.toString() );
            logWriter.newLine();
            logWriter.flush();
        }
    }
}