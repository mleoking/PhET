package testjavasockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private void start() throws IOException, ClassNotFoundException {
        Socket server = new Socket( "localhost", 1234 );
        ObjectOutputStream writeToServer = new ObjectOutputStream( server.getOutputStream() );
        ObjectInputStream readFromServer = new ObjectInputStream( server.getInputStream() );

        Object fromServer = readFromServer.readObject();

        System.out.println( "Server: " + fromServer );
        writeToServer.writeObject( "Add these numbers: 3,7" );
        fromServer = readFromServer.readObject();

        System.out.println( "fromServer = " + fromServer );

        writeToServer.close();
        readFromServer.close();
        server.close();
    }

    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        new Client().start();
    }
}