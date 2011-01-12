// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

public class TestAkka {
    public static class MyActor extends UntypedActor {
        public void onReceive( Object o ) {
            System.out.println( "o = " + o );
            getContext().replySafe( "reply" );
        }
    }

    public static class Server {
        public static void main( String[] args ) {
            remote().start( "192.168.1.7", 2552 ).register( "hello-service-2", actorOf( MyActor.class ) );
        }
    }

    public static class Client {
        public static void main( String[] args ) throws InterruptedException {
            ActorRef actor = Actors.remote().actorFor( "hello-service", "128.138.145.107", 2552 );
            while ( true ) {
//                actor.sendOneWay( "Hello from "+System.currentTimeMillis() );
                final long start = System.currentTimeMillis();
                Object response = actor.sendRequestReply( "Hello from " + start );
                System.out.println( "response = " + response + ", round trip = " + ( System.currentTimeMillis() - start ) );
                Thread.sleep(1000);
            }
        }
    }
}
