// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.io.IOException;

/**
 * Launch a sim several times to make sure the server can accommodate lots of sessions.
 *
 * @author Sam Reid
 */
public class MongoLoadTesterSimLauncher {

    private static final int NUM_CLIENTS = 10;

    public static void main( String[] args ) {
        for ( int i = 0; i < NUM_CLIENTS; i++ ) {
            new Thread( new Runnable() {
                public void run() {
                    try {
                        //I tested with a sim I built that has only one flavor, which is a study flavor
                        Runtime.getRuntime().exec( new String[] { "java", "-jar", "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\acid-base-solutions\\deploy\\acid-base-solutions_all.jar" } );
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }
    }
}