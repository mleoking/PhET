// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server.cassandra;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.SessionRecord;
import edu.colorado.phet.simsharing.messages.StudentSummary;
import edu.colorado.phet.simsharing.server.Storage;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

import static edu.colorado.phet.simsharing.server.cassandra.Test.toByteArray;
import static edu.colorado.phet.simsharing.server.cassandra.Test.toObject;

/**
 * @author Sam Reid
 */
public class CassandraStorage implements Storage {
    public static final String KEYSPACE_NAME = "testkeyspace" + System.currentTimeMillis();
    private ColumnFamilyTemplate<String, String> template;

    public CassandraStorage() {
        Cluster myCluster = HFactory.getOrCreateCluster( "test-cluster", "localhost:9160" );
        System.out.println( "myCluster = " + myCluster );

        KeyspaceDefinition newKeyspace = createSchema();

        // Add the schema to the cluster.
        // "true" as the second param means that Hector will block until all nodes see the change.
        myCluster.addKeyspace( newKeyspace, true );

        KeyspaceDefinition keyspaceDef = myCluster.describeKeyspace( KEYSPACE_NAME );

// If keyspace does not exist, the CFs don't exist either. => create them.
        if ( keyspaceDef == null ) {
            createSchema();
        }

        Keyspace ksp = HFactory.createKeyspace( KEYSPACE_NAME, myCluster );

        System.out.println( "ksp = " + ksp );

        template = new ThriftColumnFamilyTemplate<String, String>( ksp, "ColumnFamilyName", StringSerializer.get(), StringSerializer.get() );
    }

    public static KeyspaceDefinition createSchema() {
        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition( KEYSPACE_NAME, "ColumnFamilyName", ComparatorType.BYTESTYPE );
        return HFactory.createKeyspaceDefinition( KEYSPACE_NAME, ThriftKsDef.DEF_STRATEGY_CLASS, 1, Arrays.asList( cfDef ) );
    }

    public SimState getSample( SessionID sessionID, int index ) {
        return (SimState) toObject( template.queryColumns( sessionID.toString() ).getByteArray( "frame_" + index ) );
    }

    public int getNumberSamples( SessionID sessionID ) {
        return template.queryColumns( sessionID.toString() ).getInteger( "sampleCount" );
    }

    public int getNumberSessions() {
        if ( globalQuery().getInteger( "sessionCount" ) == null ) {
            final ColumnFamilyUpdater<String, String> updater = globalUpdate();
            updater.setInteger( "sessionCount", 0 );
            template.update( updater );
        }
        final ColumnFamilyResult<String, String> result = globalQuery();
        System.out.println( "result = " + result );
        final Integer count = result.getInteger( "sessionCount" );
        System.out.println( "count = " + count );
        return count;
    }

    public void startSession( SessionID sessionID ) {
        final ColumnFamilyUpdater<String, String> updater = globalUpdate();
        updater.setInteger( "sessionCount", getNumberSessions() + 1 );
        updater.setByteArray( "sessionID_" + sessionID.getIndex(), toByteArray( sessionID ) );
        updater.setLong( "sessionStartTime_" + sessionID.getIndex(), System.currentTimeMillis() );
        template.update( updater );
        System.out.println( "Started session: " + sessionID );
    }

    public void endSession( SessionID sessionID ) {
        //TODO: store the end time for the session
    }

    public void clear() {
        System.out.println( "Clear not implemented" );
    }

    public StudentList getActiveStudentList() {
        final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{

//            for ( SessionID sessionID : new ArrayList<SessionID>( storage.keySet() ) ) {
//                final Session<?> session = storage.get( sessionID );
//                if ( session.isActive() ) {
//                    add( session.getStudentSummary() );
//                }
//            }
        }} );
        return studentList;
    }

    public void storeAll( SessionID sessionID, AddSamples data ) {
    }

    public SampleBatch getSamplesAfter( SessionID id, long time ) {

        final ArrayList<SimState> states = new ArrayList<SimState>();

//        final Session<?> session = storage.getFramesAfter( id, request.time );
//        final ArrayList<? extends SimState> samples = session.getSamples();
//
//        for ( int i = samples.size() - 1; i >= 0; i-- ) {
//            SimState sample = samples.get( i );
//            if ( sample.getTime() > request.time ) {
//                states.add( sample );
//            }
//            else {
//                break;
//            }
//
//            //Not sure why they need to be sorted, but if they aren't then the sim playback skips and runs backwards
//            //Maybe they are not in order when received on the server
//            Collections.sort( states, new Comparator<SimState>() {
//                public int compare( SimState o1, SimState o2 ) {
//                    return Double.compare( o1.getTime(), o2.getTime() );
//                }
//            } );
////                    System.out.println( "Server has " + session.getNumSamples() + " states, sending " + size() );
//
//        }
        return new SampleBatch( states, getNumberSamples( id ) );
    }

    public SessionList listAllSessions() {
        return new SessionList( new ArrayList<SessionRecord>() {{
            for ( int i = 0; i < getNumberSessions(); i++ ) {
                add( new SessionRecord( getSessionID( i ), getSessionStartTime( i ) ) );
            }
            Collections.sort( this, new Comparator<SessionRecord>() {
                public int compare( SessionRecord o1, SessionRecord o2 ) {
                    return Double.compare( o1.getTime(), o2.getTime() );
                }
            } );
        }} );
    }

    private long getSessionStartTime( int i ) {
        return globalQuery().getLong( "sessionStartTime_" + i );
    }

    private ColumnFamilyResult<String, String> globalQuery() {
        return template.queryColumns( "global" );
    }

    private ColumnFamilyUpdater<String, String> globalUpdate() {
        return template.createUpdater( "global" );
    }

    private SessionID getSessionID( int i ) {
        return (SessionID) toObject( globalQuery().getByteArray( "sessionID_" + i ) );
    }
}