// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Communicates with clients using readObject/writeObject.
 *
 * @author Sam Reid
 */
public class ObjectServer extends ObjectStreamMessageServer {
    public ObjectServer( int port, MessageHandler messageHandler ) {
        super( port, messageHandler );
    }

    @Override public Object read( ObjectInputStream readFromClient ) throws IOException, ClassNotFoundException {
        return readFromClient.readObject();
    }

    @Override public void sendGreeting( ObjectOutputStream writeToClient ) throws IOException {
        writeToClient.writeObject( "Greetings from object server" );
        writeToClient.flush();
    }
}
