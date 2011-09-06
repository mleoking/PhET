// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.AWTException;
import java.io.IOException;

import edu.colorado.phet.simsharing.SimHelper;

/**
 * Launch lots of students for load-testing.
 *
 * @author Sam Reid
 */
public class Classroom {
    public static void main( final String[] args ) throws IOException, AWTException, ClassNotFoundException {
        for ( int i = 0; i < 8; i++ ) {
            new Thread( new Runnable() {
                public void run() {
                    try {
                        new Student( SimHelper.createLauncher() ).start();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    catch ( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }
    }
}