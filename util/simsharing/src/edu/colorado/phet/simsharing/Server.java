// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

/**
 * @author Sam Reid
 */
public class Server {
    public static int PORT = 2552;
    //    public static String IP_ADDRESS = "localhost";
    public static String IP_ADDRESS = "128.138.145.107";

    private HashMap<StudentID, Object> dataPoints = new HashMap<StudentID, Object>();
    public static String[] names = new String[] { "Alice", "Bob", "Charlie", "Danielle", "Earl", "Frankie", "Gail", "Hank", "Isabelle", "Joe", "Kim", "Lucy", "Mikey", "Nathan", "Ophelia", "Parker", "Quinn", "Rusty", "Shirley", "Tina", "Uther Pendragon", "Vivian", "Walt", "Xander", "Yolanda", "Zed" };
    private int connectionCount = 0;
    private ArrayList<StudentID> students = new ArrayList<StudentID>();

    public static void main( String[] args ) throws IOException {
        SimSharing.init();
        new Server().start();
    }

    private void start() {
        remote().start( IP_ADDRESS, PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof TeacherDataRequest ) {
                            TeacherDataRequest request = (TeacherDataRequest) o;
                            getContext().replySafe( dataPoints.get( request.getStudentID() ) );
                        }
                        else if ( o instanceof GetStudentID ) {
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
                            getContext().replySafe( new StudentList( students ) );
                        }
                        else if ( o instanceof StudentDataSample ) {
                            StudentDataSample studentDataSample = (StudentDataSample) o;
                            dataPoints.put( studentDataSample.getStudentID(), studentDataSample.getData() );
                        }
                    }
                };
            }
        } ) );
    }
}
