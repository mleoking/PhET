//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server.mongo;
//
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//
//import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
//import edu.colorado.phet.common.phetcommon.simsharing.SimState;
//import edu.colorado.phet.simsharing.messages.AddSamples;
//import edu.colorado.phet.simsharing.messages.SampleBatch;
//import edu.colorado.phet.simsharing.messages.SessionID;
//import edu.colorado.phet.simsharing.messages.SessionRecord;
//import edu.colorado.phet.simsharing.messages.StudentSummary;
//import edu.colorado.phet.simsharing.server.Storage;
////import edu.colorado.phet.simsharing.server.cassandra.Test;
//import edu.colorado.phet.simsharing.teacher.SessionList;
//import edu.colorado.phet.simsharing.teacher.StudentList;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.mongodb.Mongo;
//
///**
// * http://www.mongodb.org/display/DOCS/Java+Tutorial#JavaTutorial-GettingASetofDocumentsWithaQuery
// * TODO: Make sure indices are good: You can get a list of the indexes on a collection :
// * <p/>
// * List<DBObject> list = coll.getIndexInfo();
// * <p/>
// * for (DBObject o : list) {
// * System.out.println(o);
// * }
// *
// * @author Sam Reid
// */
//public class MongoStorage implements Storage {
//    private DB database;
//    private Mongo m;
//
//    public MongoStorage() {
//        try {
//            m = new Mongo();
//            database = m.getDB( "simsharing" );
//        }
//        catch ( UnknownHostException e ) {
//            e.printStackTrace();
//        }
//    }
//
//    public int getNumberSamples( SessionID id ) {
//        final DBCollection collection = database.getCollection( id.toString() + "-samples" );
//        int count = (int) collection.count();
//        return count;
//    }
//
//    public int getNumberSessions() {
//        return (int) database.getCollection( "started" ).count();
//    }
//
//    public StudentList getActiveStudentList() {
//        return new StudentList( new ArrayList<StudentSummary>() {{
//            DBCollection collection = database.getCollection( "started" );
//            DBCursor cursor = collection.find();
//            while ( cursor.hasNext() ) {
//                final DBObject next = cursor.next();
//
//                final SessionID sessionID = (SessionID) Test.toObject( (byte[]) next.get( "id" ) );
//                Integer maxIndex = (Integer) database.getCollection( sessionID.toString() + "-maxIndex" ).findOne().get( "key" );
//
//                final DBCollection samples = database.getCollection( sessionID.toString() + "-samples" );
//                BasicDBObject query = new BasicDBObject();
//                query.put( "index", new BasicDBObject( "$gte", maxIndex ) );
//                DBCursor c = samples.find( query );
//                DBObject value = c.next();
//                SimState state = (SimState) Test.toObject( (byte[]) value.get( maxIndex + "" ) );
//
//                add( new StudentSummary( sessionID, state.getImage(), 1000, 10, 10 ) );
//                System.out.println( next );
//            }
//        }} );
//    }
//
//    public SessionList listAllSessions() {
//        return new SessionList( new ArrayList<SessionRecord>() {{
//            DBCollection collection = database.getCollection( "started" );
//            DBCursor cursor = collection.find();
//            while ( cursor.hasNext() ) {
//                final DBObject next = cursor.next();
//                add( new SessionRecord( (SessionID) Test.toObject( (byte[]) next.get( "id" ) ), 0 ) );
//                System.out.println( next );
//            }
//        }} );
//    }
//
//    public void startSession( SessionID sessionID ) {
//        database.getCollection( "started" ).insert( new BasicDBObject( "id", Test.toByteArray( sessionID ) ) );
//    }
//
//    public void endSession( SessionID sessionID ) {
//        database.getCollection( "ended" ).insert( new BasicDBObject( sessionID.toString(), "ended" ) );
//    }
//
//    public SampleBatch getSamplesAfter( final SessionID id, final int index ) {
//        final DBCollection collection = database.getCollection( id.toString() + "-samples" );
//        int count = (int) collection.count();
//        return new SampleBatch<SimState>( new ArrayList<SimState>() {{
//            BasicDBObject query = new BasicDBObject();
//            query.put( "index", new BasicDBObject( "$gt", 50 ) );
//            DBCursor cursor = collection.find( query );
//            while ( cursor.hasNext() ) {
//                DBObject next = cursor.next();
//                final Integer idx = (Integer) next.get( "index" );
//                add( (SimState) Test.toObject( (byte[]) next.get( idx + "" ) ) );
//            }
//        }}, count );
//    }
//
//    public void storeAll( SessionID sessionID, AddSamples data ) {
//        //TODO: could batch DBObject calls then make one call on database.insert
//        int max = -1;
//        for ( SimState o : data.data ) {
//            database.getCollection( sessionID.toString() + "-samples" ).insert( new BasicDBObject( "index", o.getIndex() ).append( o.getIndex() + "", Test.toByteArray( o ) ) );
//            max = Math.max( o.getIndex(), max );
//        }
//        //Clear out past values
//        database.getCollection( sessionID.toString() + "-maxIndex" ).drop();
//
//        //Keep track of the max index for retrieving latest thumbnail
//        database.getCollection( sessionID.toString() + "-maxIndex" ).insert( new BasicDBObject( "key", max ) );
//    }
//
//    public void clear() {
//    }
//
//    public static void main( String[] args ) throws UnknownHostException {
//        Mongo m = new Mongo();
//        DB db = m.getDB( "mydb" );
//        DBCollection collection = db.getCollection( "testCollection" );
//        for ( int i = 0; i < 100; i++ ) {
//            collection.insert( new BasicDBObject( "key", "value" ).append( "i", i ).append( "j", Test.toByteArray( new ImmutableVector2D( i, i ) ) ) );
//        }
//
//        DBObject myDoc = collection.findOne();
//        System.out.println( "myDoc = " + myDoc );
//
//        DBCursor cur = collection.find();
//
//        while ( cur.hasNext() ) {
//            System.out.println( cur.next() );
//        }
//    }
//}