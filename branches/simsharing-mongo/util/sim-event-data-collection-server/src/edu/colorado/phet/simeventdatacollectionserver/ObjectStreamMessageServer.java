// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

//TODO class doc
public abstract class ObjectStreamMessageServer {

    private static final Logger LOGGER = Logger.getLogger( ObjectStreamMessageServer.class.getCanonicalName() );

    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host
//    public static String HOST_IP_ADDRESS = "localhost";//Settings for running locally

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = 44101;

    //Flag to indicate the server is ready to accept new connections
    private boolean listening = true;

    //The port the server will use for listening
    public int port;
    private MessageHandler messageHandler;

    //Create a server that will listen on the specified port.  Does not start listening until start() is called
    public ObjectStreamMessageServer( int port, MessageHandler messageHandler ) {
        this.port = port;
        this.messageHandler = messageHandler;
    }

    //Starts the server listening on its port
    public void start() throws IOException {

        //Listen for connections on the specified port
        final ServerSocket serverSocket = new ServerSocket( port );

        //Accept as many incoming connections as detected while listening, and create a thread to handle each one
        while ( listening ) {
            LOGGER.info( "MessageServer listening on port: " + serverSocket.getLocalPort() );
            final Socket socket = serverSocket.accept();
            LOGGER.info( "MessageServer accepted socket" );

            //Create a new thread to handle connection
            new Thread( new Runnable() {
                boolean threadAlive = true;

                public void run() {
                    try {

                        //Streams to communicate with the client, could be zipped with jzlib and could be buffered
                        final ObjectOutputStream writeToClient = new ObjectOutputStream( socket.getOutputStream() );
                        final ObjectInputStream readFromClient = new ObjectInputStream( socket.getInputStream() );

                        //Send an initial message to test the connection
                        sendGreeting( writeToClient );

                        //Loop as long as no 'logout' command was given, and process the commands
                        while ( threadAlive ) {

                            //Read the object from the client
                            try {
                                Object fromClient = read( readFromClient );

                                //allow any custom handling
                                messageHandler.handle( fromClient, writeToClient, readFromClient );

                                //Handle logout commands.  Sometimes null for unknown reason, so have to check for null here
                                if ( fromClient != null && fromClient.equals( "logout" ) ) {
                                    LOGGER.info( "Received logout command, exiting thread" );
                                    threadAlive = false;
                                }
                            }
                            catch ( SocketException socketException ) {
                                if ( socketException.getMessage().indexOf( "Connection reset" ) >= 0 ) {
                                    LOGGER.info( "Lost connection to client, exiting thread" );
                                    threadAlive = false;
                                }
                                else {
                                    throw socketException;
                                }
                            }
                        }

                        //The thread has been killed
                        writeToClient.close();
                        readFromClient.close();
                        socket.close();
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }

        //No longer listening for any connections, so close the socket
        serverSocket.close();
    }

    public abstract Object read( ObjectInputStream readFromClient ) throws IOException, ClassNotFoundException;

    public abstract void sendGreeting( ObjectOutputStream writeToClient ) throws IOException;

    public static Socket connect() throws IOException {
        return new Socket( HOST_IP_ADDRESS, PORT );
    }
}