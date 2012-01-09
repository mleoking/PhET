// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestMessageServer {
    private int numQuestions;

    public TestMessageServer( int numQuestions ) {
        this.numQuestions = numQuestions;
    }

    private void start() throws IOException, ClassNotFoundException, InterruptedException {

        //Connect to the server
        Socket server = ObjectStreamMessageServer.connect();

        //Create streams for communicating with the server
        ObjectOutputStream writeToServer = new ObjectOutputStream( server.getOutputStream() );
        ObjectInputStream readFromServer = new ObjectInputStream( server.getInputStream() );

        //Read the initial message from the server to verify communication is working properly
        Object fromServer = readFromServer.readUTF();
        System.out.println( "MessageServer: " + fromServer );

        //Send a command to the server and process the result
        for ( int i = 0; i < numQuestions; i++ ) {
            writeToServer.writeUTF( "Add these numbers: 3,6" );
            writeToServer.flush();
            fromServer = readFromServer.readUTF();
            System.out.println( "fromServer = " + fromServer );
        }

        //Done with commands, so issue a logout command to the server thread.  Doesn't kill all connections, just this one.
        writeToServer.writeUTF( "logout" );
        writeToServer.flush();

        //All done with the server, so close our connections
        writeToServer.close();
        readFromServer.close();
        server.close();
    }

    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException {
        new TestMessageServer( 10 ).start();
    }
}