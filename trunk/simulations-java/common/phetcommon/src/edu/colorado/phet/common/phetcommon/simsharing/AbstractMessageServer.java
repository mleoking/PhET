// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class AbstractMessageServer {

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
    public AbstractMessageServer( int port, MessageHandler messageHandler ) {
        this.port = port;
        this.messageHandler = messageHandler;
    }

    //Starts the server listening on its port
    public void start() throws IOException {

        //Listen for connections on the specified port
        final ServerSocket serverSocket = new ServerSocket( port );

        //Accept as many incoming connections as detected while listening, and create a thread to handle each one
        while ( listening ) {
            System.out.println( "MessageServer listening on port: " + serverSocket.getLocalPort() );
            final Socket socket = serverSocket.accept();
            System.out.println( "MessageServer accepted socket" );

            //Create a new thread to handle connection
            new Thread( new Runnable() {
                boolean threadAlive = true;

                public void run() {
                    try {

                        //Streams to communicate with the client, could be zipped with jzlib and could be buffered
                        final ObjectOutputStream writeToClient = new ObjectOutputStream( socket.getOutputStream() );
                        final ObjectInputStream readFromClient = new ObjectInputStream( socket.getInputStream() );

                        //Send an initial message to test the connection
                        writeToClient.writeUTF( "Greetings from the server" );
                        writeToClient.flush();

                        //Loop as long as no 'logout' command was given, and process the commands
                        while ( threadAlive ) {

                            //Read the object from the client
                            try {
                                Object fromClient = readFromClient.readObject();

                                //allow any custom handling
                                messageHandler.handle( fromClient, writeToClient, readFromClient );

                                //Handle logout commands.  Sometimes null for unknown reason, so have to check for null here
                                if ( fromClient != null && fromClient.equals( "logout" ) ) {
                                    System.out.println( "Received logout command, exiting thread" );
                                    threadAlive = false;
                                }
                            }
                            catch ( SocketException socketException ) {
                                if ( socketException.getMessage().indexOf( "Connection reset" ) >= 0 ) {
                                    System.out.println( "Lost connection to client, exiting thread" );
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

    public static Socket connect() throws IOException {
        return new Socket( HOST_IP_ADDRESS, PORT );
    }

    //See http://stackoverflow.com/questions/4009157/java-socket-writeutf-and-readutf
    //The maximum length of Strings that can be handled this way is 65535 for pure ASCII, less if you use non-ASCII characters - and you cannot easily predict the limit in that case, other than conservatively assuming 3 bytes per character. So if you're sure you'll never send Strings longer than about 20k, you'll be fine.
    public static void checkSize( String question ) {
        if ( question.length() > 20000 ) {
            System.out.println( "String probably too long to send over writeUTF, length = " + question.length() );
        }
    }

    public static void main( String[] args ) throws IOException {
        new AbstractMessageServer( PORT, new MessageHandler() {
            public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) {
            }
        } ).start();
    }
}