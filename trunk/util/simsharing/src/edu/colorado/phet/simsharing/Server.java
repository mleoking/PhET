// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import org.codehaus.jackson.map.ObjectMapper;

import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.colorado.phet.simsharing.teacher.ClearDatabase;
import edu.colorado.phet.simsharing.teacher.GetSessionList;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

/**
 * @author Sam Reid
 */
public class Server {
    public static int PORT = 44101;
    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host
    public static String[] names = new String[] { "Alice", "Bob", "Charlie", "Danielle", "Earl", "Frankie", "Gail", "Hank", "Isabelle", "Joe", "Kim", "Lucy", "Mikey", "Nathan", "Ophelia", "Parker", "Quinn", "Rusty", "Shirley", "Tina", "Uther Pendragon", "Vivian", "Walt", "Xander", "Yolanda", "Zed" };
    private ArrayList<SessionID> students = new ArrayList<SessionID>();
    private Morphia morphia;
    private Datastore ds;
    private Mongo mongo;
    public String databaseName = "simsharing-test-1";

    public Server() {
        try {
            mongo = new Mongo();
            morphia = new Morphia();
            morphia.map( LatestIndex.class );
            morphia.map( Sample.class );
            ds = morphia.createDatastore( mongo, databaseName );//change index on datastore name instead of clearing datastore?
            ds.ensureIndexes(); //creates all defined with @Indexed
            ds.ensureCaps(); //creates all collections for @Entity(cap=@CappedAt(...))
        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }
    }

    /*
    * Use phet-server for deployments, but localhost for local testing.
     */
    public static void parseArgs( String[] args ) {
        final List<String> list = Arrays.asList( args );
        if ( list.contains( "-host" ) ) {
            HOST_IP_ADDRESS = args[list.indexOf( "-host" ) + 1];
        }
        System.out.println( "Using host: " + HOST_IP_ADDRESS );
    }

    public Sample getSample( SessionID id, int index ) {
//        long start = System.currentTimeMillis();
        if ( index == -1 ) {//just get the latest
            index = getLastIndex( id );
        }
        Query<Sample> found = ds.find( Sample.class, "sessionID", id ).filter( "index", index );
        final Sample sample = found.get();
//        long end = System.currentTimeMillis();
//        System.out.println( "found one, elapsed = " + ( end - start ) );
        return sample;
    }

    public static <T> T time( String name, Function0<T> f ) {
        long start = System.currentTimeMillis();
        T value = f.apply();
        long end = System.currentTimeMillis();
        System.out.println( name + ": " + ( end - start ) );
        return value;
    }

    private void start() {
        remote().start( HOST_IP_ADDRESS, PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof GetStudentData ) {
                            GetStudentData request = (GetStudentData) o;
                            Sample data = getSample( request.getSessionID(), request.getIndex() );//could be null
                            getContext().replySafe( data == null ? null : new Pair<Sample, Integer>( data, getLastIndex( request.getSessionID() ) ) );
                        }
                        else if ( o instanceof StartSession ) {
                            if ( ds.createQuery( SessionCount.class ).get() == null ) {
                                ds.save( new SessionCount( 1 ) );
                            }
                            else {
                                ds.update( ds.createQuery( SessionCount.class ), ds.createUpdateOperations( SessionCount.class ).inc( "count" ) );
                            }
                            int sessionCount = ds.createQuery( SessionCount.class ).get().getCount();

                            final SessionID sessionID = new SessionID( sessionCount, names[sessionCount % names.length] );
                            getContext().replySafe( sessionID );
                            students.add( sessionID );
                            System.out.println( "session started: " + sessionID );
                            ds.save( new SessionStarted( sessionID, System.currentTimeMillis() ) );
                        }
                        else if ( o instanceof EndSession ) {
                            //Save the student info to disk and remove from system memory
                            final SessionID sessionID = ( (EndSession) o ).getSessionID();
                            students.remove( sessionID );
                            System.out.println( "session exited: " + sessionID );
                            ds.save( new SessionEnded( sessionID, System.currentTimeMillis() ) );
                        }
                        else if ( o instanceof GetStudentList ) {
                            ArrayList<StudentSummary> list = new ArrayList<StudentSummary>();
                            for ( SessionID student : students ) {
                                final Sample latestDataPoint = getSample( student, getLastIndex( student ) );
                                GravityAndOrbitsApplicationState state = null;
                                if ( latestDataPoint != null && latestDataPoint.getJson() != null ) {
                                    try {
                                        //TODO: this part is still expensive
                                        state = mapper.readValue( latestDataPoint.getJson(), GravityAndOrbitsApplicationState.class );
                                    }
                                    catch ( IOException e ) {
                                        e.printStackTrace();
                                    }
                                }
                                //TODO: back from json too expensive here
                                list.add( new StudentSummary( student, state == null ? null : new SerializableBufferedImage( SerializableBufferedImage.fromByteArray( state.getThumbnail() ) ), getSessionTime( student ), getTimeSinceLastEvent( student ) ) );
                            }
                            getContext().replySafe( new StudentList( list ) );
                        }
//                        else if ( o instanceof AddStudentDataSample ) {
//                            addSample( (AddStudentDataSample) o );
//                        }
                        else if ( o instanceof AddMultiSample ) {
                            long s = System.currentTimeMillis();
                            AddMultiSample request = (AddMultiSample) o;
                            int newIndex = getLastIndex( request.getSessionID() ) + 1;

                            for ( int i = 0; i < request.getData().size(); i++ ) {
                                final String data = request.getData().get( i );
                                final Sample sampleInstance = new Sample( System.currentTimeMillis(), request.getSessionID(), data, newIndex, newIndex );
                                ds.save( sampleInstance );
                                newIndex++;
                            }
                            newIndex--;

                            ds.delete( ds.createQuery( LatestIndex.class ).filter( "sessionID", request.getSessionID() ) );
                            ds.save( new LatestIndex( request.getSessionID(), newIndex ) );

                            ds.delete( ds.createQuery( EventReceived.class ).filter( "sessionID", request.getSessionID() ) );
                            ds.save( new EventReceived( request.getSessionID(), System.currentTimeMillis() ) );

//                            System.out.println( "Processed multisample from: " + request.getSessionID() + " in " + ( System.currentTimeMillis() - s ) + " msec" );
                        }
                        else if ( o instanceof GetSessionList ) {
                            final SessionList sessionList = new SessionList();
                            final List<SessionStarted> sessionStarted = ds.find( SessionStarted.class ).asList();
                            Collections.sort( sessionStarted, new Comparator<SessionStarted>() {
                                public int compare( SessionStarted o1, SessionStarted o2 ) {
                                    return Double.compare( o1.getTime(), o2.getTime() );
                                }
                            } );
                            for ( SessionStarted started : sessionStarted ) {
                                sessionList.add( started );
                            }
                            getContext().replySafe( sessionList );
                        }
                        else if ( o instanceof ClearDatabase ) {
                            mongo.dropDatabase( databaseName );//resets the database
                        }
                    }
                };
            }
        } ) );
    }

    ObjectMapper mapper = new ObjectMapper();

//    private void addSample( AddStudentDataSample request ) {
//        int newIndex = getLastIndex( request.getSessionID() ) + 1;
//        ds.delete( ds.createQuery( LatestIndex.class ).filter( "sessionID", request.getSessionID() ) );
//        ds.save( new LatestIndex( request.getSessionID(), newIndex ) );
//
//        try {
//            ds.save( new Sample( System.currentTimeMillis(), request.getSessionID(), request.getData(), newIndex, newIndex ) );
//        }
//        catch ( IOException e ) {
//            e.printStackTrace();
//        }
//
//        ds.delete( ds.createQuery( EventReceived.class ).filter( "sessionID", request.getSessionID() ) );
//        ds.save( new EventReceived( request.getSessionID(), System.currentTimeMillis() ) );
//    }

    private int getLastIndex( SessionID sessionID ) {
        final LatestIndex index = ds.createQuery( LatestIndex.class ).filter( "sessionID", sessionID ).get();
        return index == null ? -1 : index.getIndex();
    }

    private long getTimeSinceLastEvent( SessionID sessionID ) {
        final EventReceived eventReceived = ds.find( EventReceived.class, "sessionID", sessionID ).get();
        return eventReceived == null ? -1 : System.currentTimeMillis() - eventReceived.getTime();
    }

    //how long has student been logged in
    private long getSessionTime( SessionID sessionID ) {
        final SessionStarted sessionStarted = ds.find( SessionStarted.class, "sessionID", sessionID ).get();
        return sessionStarted == null ? -1 : System.currentTimeMillis() - sessionStarted.getTime();
    }

    public static void main( String[] args ) throws IOException {
        Server.parseArgs( args );
        SimSharing.init();
        new Server().start();
    }
}