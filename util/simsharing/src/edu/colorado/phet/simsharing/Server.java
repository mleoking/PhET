// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.io.*;
import java.util.*;

import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.colorado.phet.simsharing.teacher.GetRecordingList;
import edu.colorado.phet.simsharing.teacher.GetRecordingSample;
import edu.colorado.phet.simsharing.teacher.RecordingList;
import edu.colorado.phet.simsharing.teacher.StudentList;

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
    private HashMap<StudentID, ArrayList<Sample>> dataPoints = new HashMap<StudentID, ArrayList<Sample>>();
    private Hashtable<File, ArrayList<Sample>> recordings = new Hashtable<File, ArrayList<Sample>>(); //TODO: clear this cache so it doesn't overflow memory

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

    public Sample getSample( GetStudentData request ) {
        final ArrayList<Sample> objects = getData( request.getStudentID() );
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

    private ArrayList<Sample> getData( StudentID id ) {
        return dataPoints.get( id );
    }

    private void start() {
        remote().start( HOST_IP_ADDRESS, PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof GetStudentData ) {
                            GetStudentData request = (GetStudentData) o;
                            Sample data = getSample( request );
                            if ( data != null ) {
                                getContext().replySafe( new Pair<Sample, StudentMetadata>( data, new StudentMetadata( request.getStudentID(), getData( request.getStudentID() ).size(), System.currentTimeMillis() ) ) );
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
                        else if ( o instanceof ExitStudent ) {
                            //Save the student info to disk and remove from system memory
                            final StudentID studentID = ( (ExitStudent) o ).getStudentID();
                            students.remove( studentID );
                            store( studentID );
                            dataPoints.remove( studentID );//Free system memory
                        }
                        else if ( o instanceof GetStudentList ) {
                            ArrayList<StudentSummary> list = new ArrayList<StudentSummary>();
                            for ( StudentID student : students ) {
                                final Sample latestDataPoint = getSample( new GetStudentData( student, Time.LIVE ) );
                                SerializableBufferedImage image = null;
                                if ( latestDataPoint != null && latestDataPoint.getData() != null ) {
                                    image = ( (GravityAndOrbitsApplicationState) latestDataPoint.getData() ).getThumbnail();
                                }
                                list.add( new StudentSummary( student, image, getUpTime( student ), getTimeSinceLastEvent( student ) ) );
                            }
                            getContext().replySafe( new StudentList( list ) );
                        }
                        else if ( o instanceof AddStudentDataSample ) {
                            AddStudentDataSample studentDataSample = (AddStudentDataSample) o;
                            if ( !dataPoints.containsKey( studentDataSample.getStudentID() ) ) {
                                dataPoints.put( studentDataSample.getStudentID(), new ArrayList<Sample>() );
                            }
                            dataPoints.get( studentDataSample.getStudentID() ).add( new Sample( System.currentTimeMillis(), studentDataSample.getData() ) );//TODO: storing everything in system memory will surely result in memory problems
                        }
                        else if ( o instanceof GetRecordingList ) {
                            GetRecordingList showRecordings = (GetRecordingList) o;
                            File[] f = getSaveDir().listFiles();
                            Arrays.sort( f, new Comparator<File>() {
                                public int compare( File o1, File o2 ) {
                                    return Double.compare( o1.lastModified(), o2.lastModified() );
                                }
                            } );
                            final RecordingList recordingList = new RecordingList();
                            for ( File file : f ) {
                                recordingList.add( file );
                            }
                            getContext().replySafe( recordingList );
                        }
                        else if ( o instanceof GetRecordingSample ) {
                            GetRecordingSample getRecording = (GetRecordingSample) o;
                            File f = new File( getRecording.getFilename() );
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

                                final Sample sample = recording.get( getRecording.getIndex() );

                                Pair<Sample, StudentMetadata> result = new Pair<Sample, StudentMetadata>( sample, new StudentMetadata( new StudentID( -1, "student recording" ), recording.size(), System.currentTimeMillis() ) );
                                getContext().replySafe( result );
                            }
                            catch ( Exception e ) {
                                e.printStackTrace();
                                getContext().replySafe( null );
                            }
                        }
                    }

                    private void store( StudentID studentID ) {
                        try {
                            final ArrayList<Sample> data = getData( studentID );
                            final File saveDir = getSaveDir();
                            final File saveFile = new File( saveDir, System.nanoTime() + ".ser" );
                            new ObjectOutputStream( new FileOutputStream( saveFile ) ) {{
                                writeObject( data );
                                close();
                            }};
                            long bytes = saveFile.length();
                            long kb = bytes / 1024;
                            System.out.println( "Saved: " + saveFile.getAbsolutePath() + ", " + data.size() + " samples, " + kb + " KB" );
                        }
                        catch ( IOException e ) {
                            e.printStackTrace();
                        }
                    }

                    private File getSaveDir() {
                        return new File( "simsharing-data/" ) {{
                            mkdirs();
                        }};
                    }
                };
            }
        } ) );
    }

    private long getTimeSinceLastEvent( StudentID student ) {
        final ArrayList<Sample> data = getData( student );
        if ( data == null ) { return -1; }
        return System.currentTimeMillis() - data.get( data.size() - 1 ).getTime();
    }

    //how long has student been logged in
    private long getUpTime( StudentID student ) {
        final ArrayList<Sample> data = getData( student );
        if ( data == null ) { return -1; }
        return data.get( data.size() - 1 ).getTime() - data.get( 0 ).getTime();
    }
}