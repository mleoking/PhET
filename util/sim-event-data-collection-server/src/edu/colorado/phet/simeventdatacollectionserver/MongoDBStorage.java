// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.net.UnknownHostException;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * Uses a MongoDB database for storing and retrieving the messages.
 *
 * @author Sam Reid
 */
public class MongoDBStorage {

    private final Mongo mongo;

    public MongoDBStorage() throws UnknownHostException {
        mongo = new Mongo();

        //During development, keep database clear
        for ( String name : mongo.getDatabaseNames() ) {
            mongo.getDB( name ).dropDatabase();
        }
    }

    public void store( String m, final String machineID, final String sessionID ) {
        StringTokenizer st = new StringTokenizer( m, "\t" );
        String machineID_ = st.nextToken();
        String sessionID_ = st.nextToken();

        final String time = st.nextToken();
        final String messageType = st.nextToken();
        final String object = st.nextToken();
        final String action = st.nextToken();

        //one database per machine
        DB database = mongo.getDB( machineID );

        //One collection per session, lets us easily iterate and add messages per session.
        DBCollection coll = database.getCollection( sessionID );

        BasicDBObject doc = new BasicDBObject() {{
            put( "machineID", machineID );
            put( "sessionID", sessionID );
            put( "time", time );
            put( "messageType", messageType );
            put( "object", object );
            put( "action", action );
            String delimiter = SimSharingManager.DELIMITER;
            put( "parameters", new BasicDBObject() {{

            }} );
        }};
        coll.insert( doc );
    }

    public void close( String machineID, String sessionID ) {
        //Nothing needed for mongo
    }
}