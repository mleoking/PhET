// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.SessionRecord;
import edu.colorado.phet.simsharing.messages.StudentSummary;
import edu.colorado.phet.simsharing.socket.Session;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

/**
 * @author Sam Reid
 */
public class RAMStorage implements Storage {

    //Careful, used in many threads, so must threadlock
    private Map<SessionID, Session<?>> sessions = Collections.synchronizedMap( new HashMap<SessionID, Session<?>>() );

    public SimState getSample( SessionID sessionID, int index ) {
        return sessions.get( sessionID ).getSample( index );
    }

    public int getNumberSamples( SessionID sessionID ) {
        return sessions.get( sessionID ).getSamples().size();
    }

    public int getNumberSessions() {
        return sessions.size();
    }

    public void startSession( SessionID sessionID ) {
        sessions.put( sessionID, new Session<SimState>( sessionID ) );
    }

    public void endSession( SessionID sessionID ) {
        sessions.get( sessionID ).endSession();
    }

    public void clear() {
        sessions.clear();
    }

    public void storeAll( SessionID sessionID, AddSamples data ) {
        sessions.get( sessionID ).addSamples( data );
    }

    public StudentList getActiveStudentList() {

        final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{
            for ( SessionID sessionID : new ArrayList<SessionID>( sessions.keySet() ) ) {
                final Session<?> session = sessions.get( sessionID );
                if ( session.isActive() ) {
                    add( session.getStudentSummary() );
                }
            }
        }} );
        return studentList;
    }

    public SampleBatch getSamplesAfter( SessionID id, int index ) {
        final Session<?> session = sessions.get( id );
        final ArrayList<? extends SimState> samples = session.getSamples();
        final ArrayList<SimState> states = new ArrayList<SimState>();
        for ( int i = samples.size() - 1; i >= 0; i-- ) {
            SimState sample = samples.get( i );
            if ( sample.getIndex() > index ) {
                states.add( sample );
            }
            else {
                break;
            }

            //Not sure why they need to be sorted, but if they aren't then the sim playback skips and runs backwards
            //Maybe they are not in order when received on the server
            Collections.sort( states, new Comparator<SimState>() {
                public int compare( SimState o1, SimState o2 ) {
                    return Double.compare( o1.getTime(), o2.getTime() );
                }
            } );
//                    System.out.println( "Server has " + session.getNumSamples() + " states, sending " + size() );

        }
        return new SampleBatch( states, session.getNumSamples() );
    }

    public SessionList listAllSessions() {
        return new SessionList( new ArrayList<SessionRecord>() {{
            for ( Session<?> session : sessions.values() ) {
                add( new SessionRecord( session.getSessionID(), session.getStartTime() ) );
                Collections.sort( this, new Comparator<SessionRecord>() {
                    public int compare( SessionRecord o1, SessionRecord o2 ) {
                        return Double.compare( o1.getTime(), o2.getTime() );
                    }
                } );
            }
        }} );
    }

    public void init() {

    }
}