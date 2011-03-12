// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.tests;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.*;

/**
 * @author Sam Reid
 */
public class TestMongoDB {
    public static void main( String[] args ) throws UnknownHostException {
        Mongo m = new Mongo();
//        Mongo m = new Mongo( "localhost" );
//        Mongo m = new Mongo( "localhost", 27017 );

        DB db = m.getDB( "mydb" );
        System.out.println( "db = " + db );

        Set<String> colls = db.getCollectionNames();
        System.out.println( "colls = " + colls );
        for ( String s : colls ) {
            System.out.println( s );
        }
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
    }
}
