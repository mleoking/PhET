// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemComponents;

/**
 * Launch a sim several times to make sure the server can accommodate lots of sessions.
 * This guarantees that each client will be using a different Mongo instance, to simulation a more realistic client/server relationship.
 *
 * @author Sam Reid
 */
public class MongoLoadTesterSimLauncher {

    //Number of simultaneous threads to be running on client machine.
    private static final int NUM_CLIENTS = 10;

    //To be used in conjunction with MongoLoadTesterSimLauncher for load testing the server.  Needs to be manually enabled when a load testing jar is built.
    public static final boolean PERFORM_LOAD_TESTING = false;

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

    //Note this is called from another JAR compiled manually and launched via Runtime.exec() in main()
    public static void performLoadTesting() {

        if ( PERFORM_LOAD_TESTING ) {
            //For testing only, send 10 big messages every second for load testing.
            while ( true ) {
                SimSharingManager.sendSystemMessage( SystemComponents.loadTester, SystemComponentTypes.application, SystemActions.sentEvent, ParameterSet.parameterSet( ParameterKeys.averageValue, Math.random() ) );
                try {
                    Thread.sleep( 100 );
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}