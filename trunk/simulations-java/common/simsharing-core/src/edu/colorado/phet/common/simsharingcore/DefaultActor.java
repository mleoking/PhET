// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.simsharingcore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Fairly general-purpose class for communicating with a server over sockets.
 *
 * @author Sam Reid
 */
public class DefaultActor implements IActor {
    private final Socket socket;
    public final ObjectOutputStream writeToServer;
    public final ObjectInputStream readFromServer;

    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host
//    public static String HOST_IP_ADDRESS = "localhost";//Settings for running locally

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = 44101;

    public DefaultActor() throws ClassNotFoundException, IOException {
        this( HOST_IP_ADDRESS, PORT );
    }

    public DefaultActor( String host, int port ) throws IOException, ClassNotFoundException {
        socket = new Socket( host, port );

        //Create streams for communicating with the server
        writeToServer = new ObjectOutputStream( socket.getOutputStream() );
        readFromServer = new ObjectInputStream( socket.getInputStream() );

        //Read the initial message from the server to verify communication is working properly
        Object fromServer = readFromServer.readObject();
        System.out.println( "MessageServer: " + fromServer );
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized Object ask( Object question ) throws IOException, ClassNotFoundException {
        writeToServer.writeObject( question );
        writeToServer.flush();

        //Prevent multiple threads from using the read object simultaneously.  This was a problem before we created a new Client for that thread in SimView
        synchronized ( readFromServer ) {
            Object result = readFromServer.readObject();
            return result;
        }
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized void tell( Object statement ) throws IOException {
        writeToServer.writeObject( statement );
        writeToServer.flush();
    }
}
