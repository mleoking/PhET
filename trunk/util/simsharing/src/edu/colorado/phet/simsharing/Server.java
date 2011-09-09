// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.EndSession;
import edu.colorado.phet.simsharing.messages.GetActiveStudentList;
import edu.colorado.phet.simsharing.messages.GetSample;
import edu.colorado.phet.simsharing.messages.GetSamplesAfter;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.SessionRecord;
import edu.colorado.phet.simsharing.messages.StartSession;
import edu.colorado.phet.simsharing.messages.StudentSummary;
import edu.colorado.phet.simsharing.socket.Sample;
import edu.colorado.phet.simsharing.socket.Session;
import edu.colorado.phet.simsharing.socketutil.MessageHandler;
import edu.colorado.phet.simsharing.socketutil.MessageServer;
import edu.colorado.phet.simsharing.teacher.ClearSessions;
import edu.colorado.phet.simsharing.teacher.ListAllSessions;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

/**
 * @author Sam Reid
 */
public class Server implements MessageHandler {
    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host
//    public static String HOST_IP_ADDRESS = "localhost";//Settings for running locally

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = 44101;

    //Names to assign to students for testing
    public static String[] names = new String[] { "Alice", "Bob", "Charlie", "Danielle", "Earl", "Frankie", "Gail", "Hank", "Isabelle", "Joe", "Kim", "Lucy", "Mikey", "Nathan", "Ophelia", "Parker", "Quinn", "Rusty", "Shirley", "Tina", "Uther Pendragon", "Vivian", "Walt", "Xander", "Yolanda", "Zed" };

    //Careful, used in many threads, so must threadlock
    private Map<SessionID, Session<?>> sessions = Collections.synchronizedMap( new HashMap<SessionID, Session<?>>() );

    private void start() throws IOException {
        new MessageServer( PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        if ( message instanceof GetSample ) {
            GetSample request = (GetSample) message;
            final Session<?> session = sessions.get( request.getSessionID() );
            final int requestedIndex = request.getIndex();
            final SimState sample = session.getSample( requestedIndex );
            writeToClient.writeObject( new Sample<SimState>( sample, session.getNumSamples() ) );
        }
        else if ( message instanceof StartSession ) {
            StartSession request = (StartSession) message;
            int sessionCount = sessions.size();
            final SessionID sessionID = new SessionID( sessionCount, request.studentID + "*" + sessionCount, request.simName );
            writeToClient.writeObject( sessionID );
            sessions.put( sessionID, new Session( sessionID ) );

            System.out.println( "session started: " + sessionID );
        }
        else if ( message instanceof EndSession ) {
            //Save the student info to disk and remove from system memory
            final SessionID sessionID = ( (EndSession) message ).getSessionID();
            System.out.println( "session exited: " + sessionID );
            sessions.get( sessionID ).endSession();
        }
        else if ( message instanceof GetActiveStudentList ) {
            final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{
                for ( SessionID sessionID : new ArrayList<SessionID>( sessions.keySet() ) ) {
                    final Session<?> session = sessions.get( sessionID );
                    if ( session.isActive() ) {
                        add( session.getStudentSummary() );
                    }
                }
            }} );
            writeToClient.writeObject( studentList );
        }
        else if ( message instanceof AddSamples ) {

            //Store the samples
            AddSamples request = (AddSamples) message;
            sessions.get( request.getSessionID() ).addSamples( request );
        }
        else if ( message instanceof ListAllSessions ) {
            writeToClient.writeObject( new SessionList( new ArrayList<SessionRecord>() {{
                for ( Session<?> session : sessions.values() ) {
                    add( new SessionRecord( session.getSessionID(), session.getStartTime() ) );
                    Collections.sort( this, new Comparator<SessionRecord>() {
                        public int compare( SessionRecord o1, SessionRecord o2 ) {
                            return Double.compare( o1.getTime(), o2.getTime() );
                        }
                    } );
                }
            }} ) );
        }
        else if ( message instanceof ClearSessions ) {
            sessions.clear();
        }

        //Handle request for many data points
        else if ( message instanceof GetSamplesAfter ) {
            final GetSamplesAfter request = (GetSamplesAfter) message;
            final SessionID id = request.id;

            final Session<?> session = sessions.get( id );
            final ArrayList<? extends SimState> samples = session.getSamples();
            final ArrayList<SimState> states = new ArrayList<SimState>();
            for ( int i = samples.size() - 1; i >= 0; i-- ) {
                SimState sample = samples.get( i );
                if ( sample.getTime() > request.time ) {
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
            writeToClient.writeObject( new SampleBatch( states, session.getNumSamples() ) );
        }
    }

    //Use phet-server for deployments, but localhost for local testing.
    public static void parseArgs( String[] args ) {
        final List<String> list = Arrays.asList( args );
        if ( list.contains( "-host" ) ) {
            HOST_IP_ADDRESS = args[list.indexOf( "-host" ) + 1];
        }
        System.out.println( "Using host: " + HOST_IP_ADDRESS );
    }

    public static void main( String[] args ) throws IOException {
        Server.parseArgs( args );
        new Server().start();
    }
}