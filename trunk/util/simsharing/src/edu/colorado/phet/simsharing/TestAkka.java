// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

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

}