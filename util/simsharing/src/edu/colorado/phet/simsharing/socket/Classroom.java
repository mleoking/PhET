// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.AWTException;
import java.io.IOException;

/**
 * Launch lots of students for load-testing.
 *
 * @author Sam Reid
 */
public class Classroom {
    public static void main( String[] args ) throws IOException, AWTException, ClassNotFoundException {
        for ( int i = 0; i < 30; i++ ) {
            new Student( args ).start();
        }
    }
}