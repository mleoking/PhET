// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * Get data from an existing mongodb session.
 *
 * @author Sam Reid
 */
public class QueryMongoDB {
    public QueryMongoDB() {
    }

    public static void main( String[] args ) throws UnknownHostException {
        new QueryMongoDB().start();
    }

    private void start() throws UnknownHostException {
        Mongo m = new Mongo();
        List<String> names = m.getDatabaseNames();
        System.out.println( "names = " + names );
        for ( String machineID : names ) {
            if ( !machineID.equals( "admin" ) ) {
                DB database = m.getDB( machineID );
                Set<String> sessionIDs = database.getCollectionNames();
                for ( String session : sessionIDs ) {
                    if ( !session.equals( "system.indexes" ) ) {
                        System.out.println( session );
                        DBCollection collection = database.getCollection( session );
                        System.out.println( "Number messages in session: " + collection.getCount() );
                        DBCursor cur = collection.find();
                        while ( cur.hasNext() ) {
                            DBObject obj = cur.next();
//                            System.out.println( "obj = " + obj );
                            parse( obj );
                        }
                    }
                }
            }
        }
    }

    private void parse( DBObject obj ) {
        String machineID = obj.get( "machineID" ).toString();
        String sessionID = obj.get( "sessionID" ).toString();
        String time = obj.get( "time" ).toString();
        String messageType = obj.get( "messageType" ).toString();
        String object = obj.get( "object" ).toString();
        String action = obj.get( "action" ).toString();
        DBObject parameters = (DBObject) obj.get( "parameters" );
        System.out.println( "parameters = " + parameters );
    }
}