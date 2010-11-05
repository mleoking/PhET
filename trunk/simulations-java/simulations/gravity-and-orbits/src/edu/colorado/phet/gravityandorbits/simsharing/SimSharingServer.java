package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Sam Reid
 */
public class SimSharingServer {
    private final ServerSocket incomingSocket;
    private final ServerSocket outgoingSocket;

    public static String host = "localhost";
    public static final int STUDENT_PORT = 3752;
    public static final int TEACHER_PORT = 3753;

    public SimSharingServer() throws IOException {
        incomingSocket = new ServerSocket( STUDENT_PORT );
        outgoingSocket = new ServerSocket( TEACHER_PORT );
    }

    public static void main( String[] args ) throws IOException {
        new SimSharingServer().start();
    }

    private void start() throws IOException {
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Socket socket = incomingSocket.accept();
                        final BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
                        new Thread( new Runnable() {
                            public void run() {
                                try {
                                    while ( true ) {
                                        String line = bufferedReader.readLine();
                                        System.out.println( "line = " + line );
                                        if ( line == null ) {
                                            break;
                                        }
                                    }
                                }
                                catch ( IOException e ) {
                                    e.printStackTrace();
                                }
                            }
                        } ).start();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Socket socket = outgoingSocket.accept();
                        final BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
                        new Thread( new Runnable() {
                            public void run() {
                                try {
                                    while ( true ) {
                                        String line = bufferedReader.readLine();
                                        System.out.println( "line = " + line );
                                        if ( line == null ) {
                                            break;
                                        }
                                    }
                                }
                                catch ( IOException e ) {
                                    e.printStackTrace();
                                }
                            }
                        } ).start();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();

    }
}
