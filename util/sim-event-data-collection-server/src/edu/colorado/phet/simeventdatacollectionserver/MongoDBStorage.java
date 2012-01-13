// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringTokenizer;

import edu.colorado.phet.simsharinganalysis.Entry;
import edu.colorado.phet.simsharinganalysis.Parser;

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
    private boolean clearAllOnRestart = false;

    public MongoDBStorage() throws UnknownHostException {
        mongo = new Mongo();

        //During development, keep database clear
        if ( clearAllOnRestart ) {
            for ( String name : mongo.getDatabaseNames() ) {
                mongo.getDB( name ).dropDatabase();
            }
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

        //Use the analysis code (in Scala) to parse the message
        //I permitted this use of Scala from Java in this case because
        //1. Scala parsing code already written
        //2. Expect to add more server dependencies on analysis code for real time result presentation.
        Entry e = new Parser().parseMessage( m );
        final Map<String, String> params = e.parametersToHashMap();

        BasicDBObject doc = new BasicDBObject() {{
            put( "machineID", machineID );
            put( "sessionID", sessionID );
            put( "time", time );
            put( "messageType", messageType );
            put( "object", object );
            put( "action", action );
            put( "parameters", new BasicDBObject() {{
                for ( String key : params.keySet() ) {
                    put( key, params.get( key ) );
                }
            }} );
        }};
        coll.insert( doc );
    }

    public void close( String machineID, String sessionID ) {
        //Nothing needed for mongo
    }
}