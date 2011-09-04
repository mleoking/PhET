package testjavasockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private void start() throws IOException {
        Socket server = new Socket( "localhost", 1234 );
        PrintWriter writeToServer = new PrintWriter( server.getOutputStream(), true );
        BufferedReader readFromServer = new BufferedReader( new InputStreamReader( server.getInputStream() ) );

        String fromServer = readFromServer.readLine();

        System.out.println( "Server: " + fromServer );
        writeToServer.println( "Add these numbers: 3,7" );
        fromServer = readFromServer.readLine();

        System.out.println( "fromServer = " + fromServer );

        writeToServer.close();
        readFromServer.close();
        server.close();
    }

    public static void main( String[] args ) throws IOException {
        new Client().start();
    }
}