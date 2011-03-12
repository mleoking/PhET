// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.colorado.phet.simsharing.teacher.GetRecordingList;
import edu.colorado.phet.simsharing.teacher.GetRecordingSample;
import edu.colorado.phet.simsharing.teacher.RecordingList;
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
    private int connectionCount = 0;
    private ArrayList<StudentID> students = new ArrayList<StudentID>();
    private Hashtable<File, ArrayList<Sample>> recordings = new Hashtable<File, ArrayList<Sample>>(); //TODO: clear this cache so it doesn't overflow memory
    final Morphia morphia = new Morphia() {{
        map( GravityAndOrbitsApplicationState.class );
    }};
    Datastore ds;
    private Hashtable<StudentID, Integer> latestIndexTable = new Hashtable<StudentID, Integer>();

    public Server() {
        //Testing mongo
        try {
            ds = morphia.createDatastore( new Mongo(), "simsharing-test-" + System.currentTimeMillis() );
            ds.ensureIndexes(); //creates all defined with @Indexed
            ds.ensureCaps(); //creates all collections for @Entity(cap=@CappedAt(...))
        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }
    }

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

    public Sample getSample( StudentID id, int index ) {
//        long start = System.currentTimeMillis();
        if ( index == -1 ) {//just get the latest
            index = getLastIndex( id );
        }
        Query<Sample> found = ds.find( Sample.class, "studentID", id ).filter( "index", index );
        final Sample sample = found.get();
//        long end = System.currentTimeMillis();
//        System.out.println( "found one, elapsed = " + ( end - start ) );
        return sample;
    }

    private void start() {
        remote().start( HOST_IP_ADDRESS, PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof GetStudentData ) {
                            GetStudentData request = (GetStudentData) o;
                            Sample data = getSample( request.getStudentID(), request.getIndex() );
                            getContext().replySafe( data );//could be null
                        }
                        else if ( o instanceof RegisterStudent ) {
                            final StudentID studentID = new StudentID( connectionCount, names[connectionCount % names.length] );
                            getContext().replySafe( studentID );
                            connectionCount = connectionCount + 1;
                            students.add( studentID );
                        }
                        else if ( o instanceof ExitStudent ) {
                            //Save the student info to disk and remove from system memory
                            final StudentID studentID = ( (ExitStudent) o ).getStudentID();
                            students.remove( studentID );
                            System.out.println( "student exited: " + studentID );
                            //TODO: clear from hashtable
                        }
                        else if ( o instanceof GetStudentList ) {
                            ArrayList<StudentSummary> list = new ArrayList<StudentSummary>();
                            for ( StudentID student : students ) {
                                final Sample latestDataPoint = getSample( student, getLastIndex( student ) );
                                SerializableBufferedImage image = null;
                                if ( latestDataPoint != null && latestDataPoint.getData() != null ) {
                                    image = ( (GravityAndOrbitsApplicationState) latestDataPoint.getData() ).getThumbnail();
                                }
                                list.add( new StudentSummary( student, image, getUpTime( student ), getTimeSinceLastEvent( student ) ) );
                            }
                            getContext().replySafe( new StudentList( list ) );
                        }
                        else if ( o instanceof AddStudentDataSample ) {
                            AddStudentDataSample request = (AddStudentDataSample) o;
                            int newIndex = getLastIndex( request.getStudentID() ) + 1;
                            final Sample sample = new Sample( System.currentTimeMillis(), request.getStudentID(), request.getData(), newIndex, newIndex );
                            latestIndexTable.put( request.getStudentID(), newIndex );
                            ds.save( sample );
                        }
                        else if ( o instanceof GetRecordingList ) {
                            GetRecordingList showRecordings = (GetRecordingList) o;
                            final RecordingList recordingList = new RecordingList();
                            for ( StudentID student : students ) {
                                recordingList.add( student );
                            }
                            getContext().replySafe( recordingList );
                        }
                        else if ( o instanceof GetRecordingSample ) {
                            GetRecordingSample request = (GetRecordingSample) o;
                            File f = new File( request.getFilename() );
                            try {
                                ArrayList<Sample> recording;
                                if ( recordings.containsKey( f ) ) {
                                    recording = recordings.get( f );
                                }
                                else {
                                    ObjectInputStream objectInputStream = new ObjectInputStream( new FileInputStream( f ) );
                                    recording = (ArrayList<Sample>) objectInputStream.readObject();
                                    objectInputStream.close();
                                    recordings.put( f, recording );
                                }
                                if ( recording.size() > 0 && request.getIndex() < recording.size() ) {
                                    final Sample sample = recording.get( request.getIndex() );
                                    getContext().replySafe( sample );
                                }
                                else {
                                    getContext().replySafe( null );
                                }
                            }
                            catch ( Exception e ) {
                                e.printStackTrace();
                                getContext().replySafe( null );
                            }
                        }
                    }
                };
            }
        } ) );
    }

    private int getLastIndex( StudentID studentID ) {
        return ( latestIndexTable.containsKey( studentID ) ? latestIndexTable.get( studentID ) : 0 );
    }

    private long getTimeSinceLastEvent( StudentID student ) {
        return -1;
//        final ArrayList<Sample> data = getData( student );
//        if ( data == null ) { return -1; }
//        return System.currentTimeMillis() - data.get( data.size() - 1 ).getTime();
    }

    //how long has student been logged in
    private long getUpTime( StudentID student ) {
        return -1;
//        final ArrayList<Sample> data = getData( student );
//        if ( data == null ) { return -1; }
//        return data.get( data.size() - 1 ).getTime() - data.get( 0 ).getTime();
    }

}