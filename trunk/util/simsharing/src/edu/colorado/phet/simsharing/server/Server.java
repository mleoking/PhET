// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.simeventdatacollectionserver.MessageHandler;
import edu.colorado.phet.simeventdatacollectionserver.ObjectServer;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.EndSession;
import edu.colorado.phet.simsharing.messages.GetActiveStudentList;
import edu.colorado.phet.simsharing.messages.GetSamplesAfter;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StartSession;
import edu.colorado.phet.simsharing.server.file.FileStorage;
import edu.colorado.phet.simsharing.teacher.ClearSessions;
import edu.colorado.phet.simsharing.teacher.ListAllSessions;

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

    //    private Storage storage = new CassandraStorage();
    private Storage storage = new FileStorage( new File( "C:/simsharing-data/" ) );
//    private Storage storage = new MongoStorage();
//    private Storage storage = new HibernateStorage();

    private void start() throws IOException {
        new ObjectServer( PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        if ( message instanceof StartSession ) {
            StartSession request = (StartSession) message;
            int sessionCount = storage.getNumberSessions();
            final SessionID sessionID = new SessionID( sessionCount, request.studentID + "*" + sessionCount, request.simName );
            writeToClient.writeObject( sessionID );
            storage.startSession( sessionID );

            System.out.println( "session started: " + sessionID );
        }
        else if ( message instanceof EndSession ) {
            //Save the student info to disk and remove from system memory
            final SessionID sessionID = ( (EndSession) message ).getSessionID();
            System.out.println( "session exited: " + sessionID );
            storage.endSession( sessionID );
        }
        else if ( message instanceof GetActiveStudentList ) {
            writeToClient.writeObject( storage.getActiveStudentList() );
        }
        else if ( message instanceof AddSamples ) {

            //Store the samples
            AddSamples request = (AddSamples) message;
            storage.storeAll( request.sessionID, request );

            debugSampleCount();
        }
        else if ( message instanceof ListAllSessions ) {
            writeToClient.writeObject( storage.listAllSessions() );
        }
        else if ( message instanceof ClearSessions ) {
            storage.clear();
        }

        //Handle request for many data points
        else if ( message instanceof GetSamplesAfter ) {
            final GetSamplesAfter request = (GetSamplesAfter) message;
            writeToClient.writeObject( storage.getSamplesAfter( request.id, request.index ) );
        }
        else {
            System.out.println( message );
        }
    }

    private void debugSampleCount() {
//        int sum = 0;
//        System.out.println( storage. );
//        for ( Session<?> session : storage.values() ) {
//            sum += session.getNumSamples();
//        }
//        System.out.println( "sum = " + sum );

//        if ( sum == 150 ) {
//            XStream xStream = new XStream();
//            String xml = xStream.toXML( sessions );
//            System.out.println( "xml = \n" + xml );
//            try {
//                FileUtils.writeString( new File( "C:/Users/Sam/Desktop/gravity-and-orbits-5-sec.xml" ), xml);
//            }
//            catch ( IOException e ) {
//                e.printStackTrace();
//            }
//        }
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