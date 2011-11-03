//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server.cassandra;
//
//import me.prettyprint.cassandra.serializers.StringSerializer;
//import me.prettyprint.cassandra.service.ThriftKsDef;
//import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
//import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
//import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
//import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
//import me.prettyprint.hector.api.Cluster;
//import me.prettyprint.hector.api.Keyspace;
//import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
//import me.prettyprint.hector.api.ddl.ComparatorType;
//import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
//import me.prettyprint.hector.api.factory.HFactory;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//
//import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;
//import edu.colorado.phet.simsharing.messages.AddSamples;
//import edu.colorado.phet.simsharing.messages.SampleBatch;
//import edu.colorado.phet.simsharing.messages.SessionID;
//import edu.colorado.phet.simsharing.messages.SessionRecord;
//import edu.colorado.phet.simsharing.messages.StudentSummary;
//import edu.colorado.phet.simsharing.server.Storage;
//import edu.colorado.phet.simsharing.teacher.SessionList;
//import edu.colorado.phet.simsharing.teacher.StudentList;
//
//import static edu.colorado.phet.simsharing.server.cassandra.Test.toByteArray;
//import static edu.colorado.phet.simsharing.server.cassandra.Test.toObject;
//
///**
// * @author Sam Reid
// */
//public class CassandraStorage implements Storage {
//    public static final String KEYSPACE_NAME = "testkeyspace" + System.currentTimeMillis();
//    private ColumnFamilyTemplate<String, String> template;
//    private String COLUMN_SAMPLE_COUNT = "sampleCount";
//
//    public CassandraStorage() {
//        Cluster myCluster = HFactory.getOrCreateCluster( "test-cluster", "localhost:9160" );
//        System.out.println( "myCluster = " + myCluster );
//
//        KeyspaceDefinition newKeyspace = createSchema();
//
//        // Add the schema to the cluster.
//        // "true" as the second param means that Hector will block until all nodes see the change.
//        myCluster.addKeyspace( newKeyspace, true );
//
//        KeyspaceDefinition keyspaceDef = myCluster.describeKeyspace( KEYSPACE_NAME );
//
//// If keyspace does not exist, the CFs don't exist either. => create them.
//        if ( keyspaceDef == null ) {
//            createSchema();
//        }
//
//        Keyspace ksp = HFactory.createKeyspace( KEYSPACE_NAME, myCluster );
//
//        System.out.println( "ksp = " + ksp );
//
//        template = new ThriftColumnFamilyTemplate<String, String>( ksp, "ColumnFamilyName", StringSerializer.get(), StringSerializer.get() );
//
////        System.out.println( "Testing server" );
////        for ( int i = 0; i < 10000; i++ ) {
////            ColumnFamilyUpdater<String, String> sessionUpdater = template.createUpdater( "hello" );
////            sessionUpdater.setInteger( "test int", i );
////            template.update( sessionUpdater );
////
////            Integer value = template.queryColumns( "hello" ).getInteger( "test int" );
////            System.out.println( "value = " + value );
////
////            ColumnFamilyUpdater<String, String> sessionUpdater2 = template.createUpdater( "hello" );
////            sessionUpdater.setInteger( "test int_" + i, i );
////            template.update( sessionUpdater2 );
////        }
//    }
//
//    public static KeyspaceDefinition createSchema() {
//        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition( KEYSPACE_NAME, "ColumnFamilyName", ComparatorType.BYTESTYPE );
//        return HFactory.createKeyspaceDefinition( KEYSPACE_NAME, ThriftKsDef.DEF_STRATEGY_CLASS, 1, Arrays.asList( cfDef ) );
//    }
//
//    public SimState getSample( SessionID sessionID, int index ) {
//        return (SimState) toObject( template.queryColumns( sessionID.toString() ).getByteArray( "frame_" + index ) );
//    }
//
//    public int getNumberSamples( SessionID sessionID ) {
//        final Integer sampleCount = template.queryColumns( sessionID.toString() ).getInteger( COLUMN_SAMPLE_COUNT );
//        System.out.println( "Query for key=" + sessionID.toString() + ", column = " + COLUMN_SAMPLE_COUNT + " => " + sampleCount );
//        return sampleCount == null ? 0 : sampleCount;
//    }
//
//    public int getNumberSessions() {
//        final Integer sessionCount = globalQuery().getInteger( "sessionCount" );
//        return sessionCount == null ? 0 : sessionCount;
//    }
//
//    public synchronized void startSession( SessionID sessionID ) {
//        final ColumnFamilyUpdater<String, String> updater = globalUpdate();
//        updater.setInteger( "sessionCount", getNumberSessions() + 1 );
//        updater.setByteArray( "sessionID_" + sessionID.getIndex(), toByteArray( sessionID ) );
//        updater.setLong( "sessionStartTime_" + sessionID.getIndex(), System.currentTimeMillis() );
//        template.update( updater );
//        System.out.println( "Started session: " + sessionID );
//    }
//
//    public void endSession( SessionID sessionID ) {
//        //TODO: store the end time for the session
//    }
//
//    public void clear() {
//        System.out.println( "Clear not implemented" );
//    }
//
//    public synchronized StudentList getActiveStudentList() {
//        final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{
//            SessionList allSessions = listAllSessions();
//            for ( Object o : allSessions.toArray() ) {
//                SessionRecord sessionRecord = (SessionRecord) o;
//
//                final int numberSamples = getNumberSamples( sessionRecord.getSessionID() );
//                if ( numberSamples > 0 ) {
//                    SimState lastSample = getSample( sessionRecord.getSessionID(), numberSamples - 1 );
//                    //todo: make sure is active
//                    StudentSummary studentSummary = new StudentSummary( sessionRecord.getSessionID(), lastSample.getImage(), 1000, 1000, 1000 );
//                    add( studentSummary );
//                }
//            }
//        }} );
//        return studentList;
//    }
//
//    public synchronized void storeAll( SessionID sessionID, AddSamples data ) {
//        System.out.println( "Received " + data.data.size() + " frames" );
//        int maxIndex = -1;
//        ColumnFamilyUpdater<String, String> sessionUpdater = template.createUpdater( sessionID.toString() );
//        for ( int i = 0; i < data.data.size(); i++ ) {
//            SimState state = (SimState) data.data.get( i );
//            sessionUpdater.setByteArray( "frame_" + state.getIndex(), toByteArray( state ) );
//            maxIndex = Math.max( maxIndex, state.getIndex() );
//        }
//        sessionUpdater.setInteger( COLUMN_SAMPLE_COUNT, maxIndex + 1 );
//        template.update( sessionUpdater );
//
//        System.out.println( "Set integer for key = " + sessionID.toString() + ", column = " + COLUMN_SAMPLE_COUNT + ", value = " + ( maxIndex + 1 ) );
//
//        int samples = getNumberSamples( sessionID );
//        System.out.println( "samples = " + samples );
//    }
//
//    public SampleBatch getSamplesAfter( SessionID id, int index ) {
//
//        final ArrayList<SimState> states = new ArrayList<SimState>();
//
////        final Session<?> session = storage.getFramesAfter( id, request.time );
////        final ArrayList<? extends SimState> samples = session.getSamples();
////
////        for ( int i = samples.size() - 1; i >= 0; i-- ) {
////            SimState sample = samples.get( i );
////            if ( sample.getTime() > request.time ) {
////                states.add( sample );
////            }
////            else {
////                break;
////            }
////
////            //Not sure why they need to be sorted, but if they aren't then the sim playback skips and runs backwards
////            //Maybe they are not in order when received on the server
////            Collections.sort( states, new Comparator<SimState>() {
////                public int compare( SimState o1, SimState o2 ) {
////                    return Double.compare( o1.getTime(), o2.getTime() );
////                }
////            } );
//////                    System.out.println( "Server has " + session.getNumSamples() + " states, sending " + size() );
////
////        }
//        return new SampleBatch( states, getNumberSamples( id ) );
//    }
//
//    public SessionList listAllSessions() {
//        return new SessionList( new ArrayList<SessionRecord>() {{
//            for ( int i = 0; i < getNumberSessions(); i++ ) {
//                add( new SessionRecord( getSessionID( i ), getSessionStartTime( i ) ) );
//            }
//            Collections.sort( this, new Comparator<SessionRecord>() {
//                public int compare( SessionRecord o1, SessionRecord o2 ) {
//                    return Double.compare( o1.getTime(), o2.getTime() );
//                }
//            } );
//        }} );
//    }
//
//    private long getSessionStartTime( int i ) {
//        return globalQuery().getLong( "sessionStartTime_" + i );
//    }
//
//    private ColumnFamilyResult<String, String> globalQuery() {
//        return template.queryColumns( "global" );
//    }
//
//    private synchronized ColumnFamilyUpdater<String, String> globalUpdate() {
//        return template.createUpdater( "global" );
//    }
//
//    private SessionID getSessionID( int i ) {
//        return (SessionID) toObject( globalQuery().getByteArray( "sessionID_" + i ) );
//    }
//}