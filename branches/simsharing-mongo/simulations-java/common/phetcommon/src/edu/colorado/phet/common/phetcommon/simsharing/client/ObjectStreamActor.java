// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Fairly general-purpose class for communicating with a server over sockets using an ObjectStream.
 * We send java.lang.Strings over an ObjectStream.
 *
 * @author Sam Reid
 */
public abstract class ObjectStreamActor<T, U> implements IActor<T, U> {

    private static final Logger LOGGER = Logger.getLogger( ObjectStreamActor.class.getCanonicalName() );

    private final Socket socket;
    public final ObjectOutputStream writeToServer;
    public final ObjectInputStream readFromServer;

    //Default to phet-server, but you can use this command line arg for local testing:
    //-Dsim-event-data-collection-server-host-ip-address=localhost
    public static String HOST_IP_ADDRESS = System.getProperty( "sim-event-data-collection-server-host-ip-address", "128.138.145.107" );

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = 44101;

    public ObjectStreamActor() throws ClassNotFoundException, IOException {
        this( HOST_IP_ADDRESS, PORT );
    }

    public ObjectStreamActor( String host, int port ) throws IOException, ClassNotFoundException {
        socket = new Socket( host, port );

        //Create streams for communicating with the server
        writeToServer = new ObjectOutputStream( socket.getOutputStream() );
        readFromServer = new ObjectInputStream( socket.getInputStream() );

        //Read the initial message from the server to verify communication is working properly
        Object fromServer = readFromServer.readUTF();
        LOGGER.info( "MessageServer: " + fromServer );
    }
}