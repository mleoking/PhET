// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

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
}