// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.net.UnknownHostException;

import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * Runs lots of threads which all send some data to the mongo server--helps us to see what kind of load MongoDB can support on our hardware/network configuration.
 *
 * @author Sam Reid
 */
public class MongoLoadTester {

    private static final int TIME_BETWEEN_MESSAGES_MILLIS = 1000;
    private static final int NUM_CLIENTS = 100;

    public static void main( String[] args ) {
        for ( int i = 0; i < NUM_CLIENTS; i++ ) {
            final int finalI = i;
            new Thread( new Runnable() {
                public void run() {
                    Mongo m = null;
                    try {
                        m = new Mongo( MongoLog.HOST_IP_ADDRESS, MongoLog.PORT );
                    }
                    catch ( UnknownHostException e ) {
                        e.printStackTrace();
                    }
                    DB db = m.getDB( "loadtester" );
                    DBCollection collection = db.getCollection( "loadtesterCollection" );
                    while ( true ) {
                        try {
                            collection.insert( new BasicDBObject() {{
                                put( "index", finalI );
                            }} );
                            Thread.sleep( TIME_BETWEEN_MESSAGES_MILLIS );
                        }
                        catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            } ).start();
        }
    }
}