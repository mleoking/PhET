// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.net.UnknownHostException;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

/**
 * Runs lots of threads which all send some data to the mongo server--helps us
 * to see what kind of load MongoDB can support on our hardware/network
 * configuration.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class MongoLoadTester {

    public static final String LOAD_TESTING_DB_NAME = "loadtesting";
    public static final String DB_USER_NAME = "phetsimclient";
    public static final String DB_COLLECTION = "loadtesterCollection";

    private static final int TIME_BETWEEN_MESSAGES_MILLIS = 1000;
    private static final int NUM_CLIENTS = 1;

    //Part of the mongoDB password, see #3231
    public static final String MER = "meR".toLowerCase();

    public static void main( String[] args ) {
        for ( int i = 0; i < NUM_CLIENTS; i++ ) {
            final int clientIndex = i;
            new Thread( new Runnable() {
                public void run() {
                    try {
                        Mongo m = new Mongo( MongoLog.HOST_IP_ADDRESS, MongoLog.PORT );
                        DB db = m.getDB( LOAD_TESTING_DB_NAME );
                        boolean authenticated = db.authenticate( DB_USER_NAME, ( MER + SimSharingManager.MONGO_PASSWORD + "" + ( 2 * 2 * 2 ) + "ss0O88723otbubaoue" ).toCharArray() );
                        System.out.println( "authenticated = " + authenticated );
                        DBCollection collection = db.getCollection( DB_COLLECTION );
                        int messageIndex = 0;
                        for ( int i = 0; i < 10; i++ ) {
                            try {
                                final int finalMessageIndex = messageIndex;
                                WriteResult result = collection.insert( new BasicDBObject() {{
                                    put( "index", clientIndex );
                                    put( "messageIndex", finalMessageIndex );
                                    for ( int k = 0; k < 100; k++ ) {
                                        put( "value_" + k, k );
                                    }
                                }} );
                                System.out.println( "result.getError() = " + result.getError() );
                                Thread.sleep( TIME_BETWEEN_MESSAGES_MILLIS );
                                messageIndex++;
                            }
                            catch ( InterruptedException e ) {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch ( UnknownHostException e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }
    }
}