/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.IOException;
import java.io.InputStream;

/**
 * Command runs an external command via Runtime.exec.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Command {
    
    private static boolean _debugOutputEnabled = false;
    
    /* not intended for instantiation */
    private Command() {}
    
    public static void setDebugOutputEnabled( boolean enabled ) {
        _debugOutputEnabled = enabled;
    }

    public static void run( String command, boolean waitForCompletion ) {
        if ( _debugOutputEnabled ) {
            System.out.println( "Command.run command=\"" + command + "\"" );
        }
        try {
            Process process = Runtime.getRuntime().exec( command );
            
            if ( _debugOutputEnabled ) {
                InputStream in = process.getInputStream();
                int c;
                while ( ( c = in.read() ) != -1 ) {
                    System.out.print( (char) c );
                }
                in.close();

                InputStream err = process.getErrorStream();
                while ( ( c = err.read() ) != -1 ) {
                    System.err.print( (char) c );
                }
                err.close();
            }
            
            if ( waitForCompletion ) {
                int exitValue = process.waitFor();
                if ( exitValue != 0 ) {
                    throw new RuntimeException( "\"" + command + "\"terminated abnormally, exit value = " + exitValue );
                }
            }
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        catch ( InterruptedException ie ) {
            ie.printStackTrace(); 
        } 
    }
}
