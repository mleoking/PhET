// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.net.UnknownHostException;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * Runs lots of threads which all send some data to the mongo server--helps us to see what kind of load MongoDB can support on our hardware/network configuration.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class MongoLoadTesterReader {

    public static void main( String[] args ) throws UnknownHostException {
        Mongo m = new Mongo( MongoLog.HOST_IP_ADDRESS, MongoLog.PORT );
        DB db = m.getDB( MongoLoadTester.LOAD_TESTING_DB_NAME );
        boolean authenticated = db.authenticate( MongoLoadTester.DB_USER_NAME,
                                                 ( MongoLoadTester.MER + SimSharingManager.MONGO_PASSWORD + "" + ( 2 * 2 * 2 ) + "ss0O88723otbubaoue" ).toCharArray() );

        System.out.println( "authenticated = " + authenticated );
        if ( !authenticated ) {
            System.out.println( "Authentication failed, aborting test." );
            return;
        }
        DBCollection collection = db.getCollection( MongoLoadTester.DB_COLLECTION );
        long count = collection.getCount();
        System.out.println( "count = " + count );
    }
}