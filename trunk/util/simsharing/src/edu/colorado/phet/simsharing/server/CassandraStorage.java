//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//import edu.colorado.phet.common.phetcommon.simsharing.SimState;
//import edu.colorado.phet.simsharing.messages.AddSamples;
//import edu.colorado.phet.simsharing.messages.SampleBatch;
//import edu.colorado.phet.simsharing.messages.SessionID;
//import edu.colorado.phet.simsharing.messages.SessionRecord;
//import edu.colorado.phet.simsharing.messages.StudentSummary;
//import edu.colorado.phet.simsharing.socket.Session;
//import edu.colorado.phet.simsharing.teacher.SessionList;
//import edu.colorado.phet.simsharing.teacher.StudentList;
//
///**
// * @author Sam Reid
// */
//public class CassandraStorage implements Storage {
//
//    public void init() {
//    }
//
//    public SimState getSample( SessionID sessionID, int index ) {
//        return null;
//    }
//
//    public int getNumberSamples( SessionID sessionID ) {
//        return 0;
//    }
//
//    public int getNumberSessions() {
//        return 0;
//    }
//
//    public void startSession( SessionID sessionID ) {
//    }
//
//    public void endSession( SessionID sessionID ) {
//    }
//
//    public void clear() {
//    }
//
//    public Object getActiveStudentList() {
//        final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{
//            for ( SessionID sessionID : new ArrayList<SessionID>( storage.keySet() ) ) {
//                final Session<?> session = storage.get( sessionID );
//                if ( session.isActive() ) {
//                    add( session.getStudentSummary() );
//                }
//            }
//        }} );
//        return studentList;
//    }
//
//    public void storeAll( SessionID sessionID, AddSamples data ) {
//    }
//
//    public Object getSamplesAfter( SessionID id, long time ) {
//
//
//        final Session<?> session = storage.getFramesAfter( id, request.time );
//        final ArrayList<? extends SimState> samples = session.getSamples();
//        final ArrayList<SimState> states = new ArrayList<SimState>();
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
//            new SampleBatch( states, session.getNumSamples() );
//        }
//    }
//
//    public Object listAllSessions() {
//        return new SessionList( new ArrayList<SessionRecord>() {{
//            for ( Session<?> session : storage.values() ) {
//                add( new SessionRecord( session.getSessionID(), session.getStartTime() ) );
//                Collections.sort( this, new Comparator<SessionRecord>() {
//                    public int compare( SessionRecord o1, SessionRecord o2 ) {
//                        return Double.compare( o1.getTime(), o2.getTime() );
//                    }
//                } );
//            }
//        }} );
//    }
//}