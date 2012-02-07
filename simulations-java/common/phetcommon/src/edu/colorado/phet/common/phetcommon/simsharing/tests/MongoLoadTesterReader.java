// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.net.UnknownHostException;

import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * Runs lots of threads which all send some data to the mongo server--helps us to see what kind of load MongoDB can support on our hardware/network configuration.
 *
 * @author Sam Reid
 */
public class MongoLoadTesterReader {

    public static void main( String[] args ) throws UnknownHostException {
        Mongo m = new Mongo( MongoLog.HOST_IP_ADDRESS, MongoLog.PORT );
        DB db = m.getDB( "loadtester" );
        boolean authenticated = db.authenticate( "", "".toCharArray() );
        System.out.println( "authenticated = " + authenticated );
        DBCollection collection = db.getCollection( "loadtesterCollection" );
        long count = collection.getCount();
        System.out.println( "count = " + count );
    }
}