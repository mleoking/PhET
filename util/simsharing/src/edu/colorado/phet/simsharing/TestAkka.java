// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

public class TestAkka {
    protected static int SERVER_PORT = 2552;
    protected static int TEACHER_PORT = 2553;

    public static class ServerActor extends UntypedActor {
        protected ActorRef teacherActor;

        public ServerActor() {
            teacherActor = Actors.remote().actorFor( "teacher", "localhost", TEACHER_PORT );
        }

        public void onReceive( Object o ) {
//            GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) o;
//            System.out.println( "Server received state: " + state );
//            getContext().replySafe( "received state from student" );
            teacherActor.sendOneWay( o );
        }
    }

    public static class Server {
        public static void main( String[] args ) {
            remote().start( "localhost", SERVER_PORT ).register( "server", actorOf( ServerActor.class ) );
        }
    }

    public static class Teacher {

        public static void main( String[] args ) {
            final GravityAndOrbitsApplication[] launchedApp = new GravityAndOrbitsApplication[1];
            new PhetApplicationLauncher().launchSim( args, GravityAndOrbitsApplication.PROJECT_NAME, new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    launchedApp[0] = new GravityAndOrbitsApplication( config );
                    return launchedApp[0];
                }
            } );
            final GravityAndOrbitsApplication application = launchedApp[0];
            PhetFrame frame = application.getPhetFrame();
            frame.setTitle( frame.getTitle() + ": Teacher Edition" ); //simsharing, append command line args to title
            application.getGravityAndOrbitsModule().setTeacherMode( true );

            remote().start( "localhost", TEACHER_PORT ).register( "teacher", actorOf( new Creator<Actor>() {
                public Actor create() {
                    return new UntypedActor() {
                        public void onReceive( Object o ) {
                            GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) o;
//                            System.out.println( "Teacher received state from server: " + state );
//                            System.out.println( "state = " + state );
                            state.apply( application );
                        }
                    };
                }
            } ) );
        }
    }
}