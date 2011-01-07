// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.test;

import java.io.IOException;
import java.io.InputStream;

/**
 * TestRuntimeExec is an example of how to use execute a command and read it's output.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestRuntimeExec {

    public static void main( String[] args ) {
        
        try {
            String command = "ls -la";
            Process process = Runtime.getRuntime().exec( command );
            InputStream in = process.getInputStream();
            int c;
            while ( ( c = in.read() ) != -1 ) {
                System.out.print( (char) c );
            }
            in.close();
            int exitValue = process.waitFor();
            if ( exitValue != 0 ) {
                System.err.println( "process terminated abnormally, exit value = " + exitValue );
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
