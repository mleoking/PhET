// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.client;

import java.io.IOException;

/**
 * Fairly general-purpose class for communicating with a server over sockets.
 *
 * @author Sam Reid
 */
public class ObjectActor<T, U> extends ObjectStreamActor<T, U> {

    public ObjectActor() throws ClassNotFoundException, IOException {
        super();
    }

    public ObjectActor( String host, int port ) throws IOException, ClassNotFoundException {
        super( host, port );
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized U ask( T question ) throws IOException, ClassNotFoundException {
        writeToServer.writeObject( question );
        writeToServer.flush();

        //Prevent multiple threads from using the read object simultaneously.  This was a problem before we created a new Client for that thread in SimView
        synchronized ( readFromServer ) {
            Object result = readFromServer.readObject();
            return (U) result;
        }
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized void tell( T statement ) throws IOException {
        writeToServer.writeObject( statement );
        writeToServer.flush();
    }
}