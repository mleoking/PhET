// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.client;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.simsharing.server.StringServer;

/**
 * Client that uses writeUTF and readUTF because it is safe against version/serialization changes.
 *
 * @author Sam Reid
 */
public class StringActor extends ObjectStreamActor<String, String> {

    public StringActor() throws ClassNotFoundException, IOException {
        super();
    }

    public StringActor( String host, int port ) throws IOException, ClassNotFoundException {
        super( host, port );
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized String ask( String question ) throws IOException, ClassNotFoundException {
        StringServer.checkSize( question );
        writeToServer.writeUTF( question );
        writeToServer.flush();

        //Prevent multiple threads from using the read object simultaneously.  This was a problem before we created a new Client for that thread in SimView
        synchronized ( readFromServer ) {
            String result = readFromServer.readUTF();
            return result;
        }
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized void tell( String statement ) throws IOException {
        StringServer.checkSize( statement );
        writeToServer.writeUTF( statement );
        writeToServer.flush();
    }
}
