// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.io.IOException;

/**
 * Runs lots of threads which all send some data to the mongo server--helps us to see what kind of load MongoDB can support on our hardware/network configuration.
 *
 * @author Sam Reid
 */
public class MongoLoadTester {

    private static final int TIME_BETWEEN_MESSAGES_MILLIS = 1000;
    private static final int NUM_CLIENTS = 30;

    public static void main( String[] args ) {
        for ( int i = 0; i < NUM_CLIENTS; i++ ) {
            final int finalI = i;
            new Thread( new Runnable() {
                public void run() {

                    try {
                        Process p = Runtime.getRuntime().exec( new String[] { "java", "-jar", "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\acid-base-solutions\\deploy\\acid-base-solutions_all.jar" } );
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
//                    Mongo m = null;
//                    try {
//                        m = new Mongo( MongoLog.HOST_IP_ADDRESS, MongoLog.PORT );
//                    }
//                    catch ( UnknownHostException e ) {
//                        e.printStackTrace();
//                    }
//                    DB db = m.getDB( "loadtester" );
//                    DBCollection collection = db.getCollection( "loadtesterCollection" );
//                    while ( true ) {
//                        try {
//                            collection.insert( new BasicDBObject() {{
//                                put( "index", finalI );
//                            }} );
//                            Thread.sleep( TIME_BETWEEN_MESSAGES_MILLIS );
//                        }
//                        catch ( InterruptedException e ) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            } ).start();
        }
    }
}