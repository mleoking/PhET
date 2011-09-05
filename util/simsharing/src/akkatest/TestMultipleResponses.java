//// Copyright 2002-2011, University of Colorado
//package akkatest;
//
//import akka.actor.Actor;
//import akka.actor.ActorRef;
//import akka.actor.Actors;
//import akka.actor.UntypedActor;
//import akka.japi.Creator;
//
//import static akka.actor.Actors.actorOf;
//import static akka.actor.Actors.remote;
//
////Requires 1.2
//public class TestMultipleResponses {
//    public static String SERVER_IP_ADDRESS = "localhost";
//    public static String CLIENT_IP_ADDRESS = "localhost";
//    public static int SERVER_PORT = 1234;
//    public static int CLIENT_PORT = 1235;
//
//    static class Server {
//        public void start() {
//            remote().start( SERVER_IP_ADDRESS, SERVER_PORT ).register( "server", actorOf( new Creator<Actor>() {
//                public Actor create() {
//                    return new UntypedActor() {
//                        public void onReceive( Object o ) {
//                            System.out.println( "Server received: " + o );
//
//                            getContext().channel().tell( "channeltell 1!" );
//                            getContext().channel().tell( "channeltell 2!" );
//                            getContext().channel().tell( "channeltell 3!" );
//                        }
//                    };
//                }
//            } ) );
//        }
//
//        public static void main( String[] args ) {
//            new Server().start();
//        }
//    }
//
//    static class TestClient {
//        private void start() {
//            final ActorRef server = Actors.remote().actorFor( "server", SERVER_IP_ADDRESS, SERVER_PORT );
//
//            remote().start( CLIENT_IP_ADDRESS, CLIENT_PORT ).register( "client", actorOf( new Creator<Actor>() {
//                public Actor create() {
//                    return new UntypedActor() {
//                        public void onReceive( Object o ) {
//                            System.out.println( "getContext() = " + getContext() );
//                            if ( o.equals( "Call server" ) ) {
//                                System.out.println( "Calling the server" );
//                                server.tell( "hello there", getContext() );
//                            }
//                            else {
//                                System.out.println( "TestClient received: " + o );
//                            }
//                        }
//                    };
//                }
//            } ) );
//
//            ActorRef client = Actors.remote().actorFor( "client", CLIENT_IP_ADDRESS, CLIENT_PORT );
//            client.tell( "Call server" );
//        }
//
//        public static void main( String[] args ) {
//            new TestClient().start();
//        }
//    }
//}