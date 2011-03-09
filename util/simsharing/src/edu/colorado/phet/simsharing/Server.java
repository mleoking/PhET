// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

/**
 * @author Sam Reid
 */
public class Server {
    public static int PORT = 44100;
    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host

    private HashMap<StudentID, ArrayList<Object>> dataPoints = new HashMap<StudentID, ArrayList<Object>>();
    public static String[] names = new String[] { "Alice", "Bob", "Charlie", "Danielle", "Earl", "Frankie", "Gail", "Hank", "Isabelle", "Joe", "Kim", "Lucy", "Mikey", "Nathan", "Ophelia", "Parker", "Quinn", "Rusty", "Shirley", "Tina", "Uther Pendragon", "Vivian", "Walt", "Xander", "Yolanda", "Zed" };
    private int connectionCount = 0;
    private ArrayList<StudentID> students = new ArrayList<StudentID>();

    public static void main( String[] args ) throws IOException {
        Server.parseArgs( args );
        SimSharing.init();
        new Server().start();
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

    public Object getDataPoint( TeacherDataRequest request ) {
        final ArrayList<Object> objects = getData( request.getStudentID() );
        if ( objects != null ) {
            final int requestedIndex = request.getTime().equals( Time.LIVE ) ?
                                       objects.size() - 1 :
                                       ( (Time.Index) request.getTime() ).index;
            if ( requestedIndex >= 0 && requestedIndex < objects.size() ) {
                return objects.get( requestedIndex );
            }
            else {
                return null;
            }
        }
        else { return null; }
    }

    private ArrayList<Object> getData( StudentID id ) {
        return dataPoints.get( id );
    }

    private void start() {
        remote().start( HOST_IP_ADDRESS, PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof TeacherDataRequest ) {
                            TeacherDataRequest request = (TeacherDataRequest) o;
                            Object data = getDataPoint( request );
                            if ( data != null ) {
                                getContext().replySafe( new Pair<Object, StudentMetadata>( data, new StudentMetadata( request.getStudentID(), getData( request.getStudentID() ).size(), System.currentTimeMillis() ) ) );
                            }
                            else {
                                getContext().replySafe( null );
                            }
                        }
                        else if ( o instanceof RegisterStudent ) {
                            final StudentID studentID = new StudentID( connectionCount, names[connectionCount % names.length] );
                            getContext().replySafe( studentID );
                            connectionCount = connectionCount + 1;
                            students.add( studentID );
                        }
                        else if ( o instanceof StudentExit ) {
                            StudentExit studentExit = (StudentExit) o;
                            students.remove( studentExit.getStudentID() );
                        }
                        else if ( o instanceof GetStudentList ) {
                            ArrayList<Pair<StudentID, SerializableBufferedImage>> list = new ArrayList<Pair<StudentID, SerializableBufferedImage>>();
                            for ( StudentID student : students ) {
                                final GravityAndOrbitsApplicationState latestDataPoint = (GravityAndOrbitsApplicationState) getDataPoint( new TeacherDataRequest( student, Time.LIVE ) );
                                list.add( new Pair<StudentID, SerializableBufferedImage>( student, latestDataPoint.getThumbnail() ) );
                            }
                            getContext().replySafe( new StudentList( list ) );
                        }
                        else if ( o instanceof StudentDataSample ) {
                            StudentDataSample studentDataSample = (StudentDataSample) o;
                            if ( !dataPoints.containsKey( studentDataSample.getStudentID() ) ) {
                                dataPoints.put( studentDataSample.getStudentID(), new ArrayList<Object>() );
                            }
                            dataPoints.get( studentDataSample.getStudentID() ).add( studentDataSample.getData() );//TODO: storing everything in system memory will surely result in memory problems
                        }
                    }
                };
            }
        } ) );
    }
}
