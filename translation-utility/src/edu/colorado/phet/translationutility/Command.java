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
    
    private Command() {}

    public static void run( String command, boolean waitForCompletion ) {
        System.out.println( "runCommand command=\"" + command + "\"" );
        try {
            Process process = Runtime.getRuntime().exec( command );
            
            if ( waitForCompletion ) {
                
                InputStream in = process.getInputStream();
                int c;
                while ( ( c = in.read() ) != -1 ) {
                    System.out.print( (char) c );
                }
                in.close();

                InputStream err = process.getErrorStream();
                while ( ( c = err.read() ) != -1 ) {
                    System.out.print( (char) c );
                }
                err.close();

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
