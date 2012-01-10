// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * @author Sam Reid
 */
public class TestMongoDB {
    public static void main( String[] args ) throws UnknownHostException {
        Mongo m = new Mongo();
        // or
//        Mongo m = new Mongo( "localhost" );
//        // or
//        Mongo m = new Mongo( "localhost" , 27017 );

        DB db = m.getDB( "mydb" );

        BasicDBObject doc = new BasicDBObject();

        doc.put( "name", "MongoDB" );
        doc.put( "type", "database" );
        doc.put( "count", 1 );

        BasicDBObject info = new BasicDBObject();

        info.put( "x", 203 );
        info.put( "y", 102 );

        doc.put( "info", info );

        DBCollection coll = db.getCollection( "testCollection" );
        coll.insert( doc );

        DBObject myDoc = coll.findOne();
        System.out.println( myDoc );

        for ( int i = 0; i < 100; i++ ) {
            coll.insert( new BasicDBObject().append( "i", i ) );
        }
    }
}
