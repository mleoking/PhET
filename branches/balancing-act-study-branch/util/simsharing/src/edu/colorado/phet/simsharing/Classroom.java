// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.AWTException;
import java.io.IOException;

import edu.colorado.phet.simsharing.server.Server;

/**
 * Launch lots of students for load-testing.
 *
 * @author Sam Reid
 */
public class Classroom {
    public static void main( final String[] args ) throws IOException, AWTException, ClassNotFoundException {
        Server.parseArgs( args );
        for ( int i = 0; i < 30; i++ ) {
            final int finalI = i;
            new Thread( new Runnable() {
                public void run() {
                    try {
                        new Student( Sim.TEST_SIM, Server.HOST_IP_ADDRESS, Server.PORT, Server.names[finalI % ( Server.names.length )] + "*" + finalI ).start();
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