// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

/**
 * @author Sam Reid
 */
public class Server {
    public static void main( String[] args ) {
        final ActorRef teacherActor = Actors.remote().actorFor( "teacher", "localhost", Config.TEACHER_PORT );
        remote().start( "localhost", Config.SERVER_PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        teacherActor.sendOneWay( o );
                    }
                };
            }
        } ) );
    }
}
