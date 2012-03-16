// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.client;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Client that uses writeUTF and readUTF because it is safe against version/serialization changes.
 *
 * @author Sam Reid
 */
public class StringActor extends ObjectStreamActor<String, String> {

    private static final Logger LOGGER = Logger.getLogger( StringActor.class.getCanonicalName() );

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
        checkSize( question );
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
        checkSize( statement );
        writeToServer.writeUTF( statement );
        writeToServer.flush();
    }

    //See http://stackoverflow.com/questions/4009157/java-socket-writeutf-and-readutf
    //The maximum length of Strings that can be handled this way is 65535 for pure ASCII, less if you use non-ASCII characters - and you cannot easily predict the limit in that case, other than conservatively assuming 3 bytes per character. So if you're sure you'll never send Strings longer than about 20k, you'll be fine.
    public static void checkSize( String question ) {
        if ( question.length() > 20000 ) {
            LOGGER.warning( "String probably too long to send over writeUTF, length = " + question.length() );
        }
    }
}