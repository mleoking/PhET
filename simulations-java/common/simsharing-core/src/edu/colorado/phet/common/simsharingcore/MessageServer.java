package edu.colorado.phet.common.simsharingcore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class MessageServer {

    //Default host the server will run on, must be a publicly accessible IP address so clients can connect to it
    public static String DEFAULT_HOST = "localhost";

    //Default port the server will use for listening
    public static int DEFAULT_PORT = 1234;

    //Flag to indicate the server is ready to accept new connections
    private boolean listening = true;

    //The port the server will use for listening
    public int port;
    private MessageHandler messageHandler;

    //Create a server that will listen on the specified port.  Does not start listening until start() is called
    public MessageServer( int port, MessageHandler messageHandler ) {
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
                        writeToClient.writeObject( "Greetings from the server" );

                        //Loop as long as no 'logout' command was given, and process the commands
                        while ( threadAlive ) {

                            //Read the object from the client
                            try {
                                Object fromClient = readFromClient.readObject();

                                //allow any custom handling
                                messageHandler.handle( fromClient, writeToClient, readFromClient );

                                //Process the command and respond
                                if ( fromClient instanceof String && fromClient.toString().startsWith( "Add" ) ) {
                                    StringTokenizer st = new StringTokenizer( fromClient.toString().substring( fromClient.toString().indexOf( ":" ) + 1 ), ", " );
                                    int x = Integer.parseInt( st.nextToken() );
                                    int y = Integer.parseInt( st.nextToken() );
                                    writeToClient.writeObject( "added your numbers, " + x + "+" + y + " = " + ( x + y ) );
                                }

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
        return new Socket( DEFAULT_HOST, DEFAULT_PORT );
    }

    public static void main( String[] args ) throws IOException {
        new MessageServer( DEFAULT_PORT, new MessageHandler() {
            public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) {
            }
        } ).start();
    }
}