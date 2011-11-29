// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Server that sends and receives strings only using writeUTF and readUTF because it is safe against version changes.
 *
 * @author Sam Reid
 */
public class StringServer extends ObjectStreamMessageServer {
    public StringServer( int port, MessageHandler messageHandler ) {
        super( port, messageHandler );
    }

    @Override public Object read( ObjectInputStream readFromClient ) throws IOException, ClassNotFoundException {
        return readFromClient.readUTF();
    }

    @Override public void sendGreeting( ObjectOutputStream writeToClient ) throws IOException {
        writeToClient.writeUTF( "Greetings from the server" );
        writeToClient.flush();
    }

    public static void main( String[] args ) throws IOException {
        new StringServer( PORT, new MessageHandler() {
            public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) {
            }
        } ).start();
    }

    //See http://stackoverflow.com/questions/4009157/java-socket-writeutf-and-readutf
    //The maximum length of Strings that can be handled this way is 65535 for pure ASCII, less if you use non-ASCII characters - and you cannot easily predict the limit in that case, other than conservatively assuming 3 bytes per character. So if you're sure you'll never send Strings longer than about 20k, you'll be fine.
    public static void checkSize( String question ) {
        if ( question.length() > 20000 ) {
            System.out.println( "String probably too long to send over writeUTF, length = " + question.length() );
        }
    }
}