package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Sam Reid
 */
public class SimSharingServer {
    private final ServerSocket studentServerSocket;
    private final ServerSocket teacherServerSocket;

    public static String host = "phet-server.colorado.edu";
    public static final int STUDENT_PORT = 3752;
    public static final int TEACHER_PORT = 3753;
    private Socket teacherSocket;
    private BufferedWriter teacherBufferedWriter;

    public SimSharingServer() throws IOException {
        studentServerSocket = new ServerSocket( STUDENT_PORT );
        teacherServerSocket = new ServerSocket( TEACHER_PORT );
    }

    public static void main( String[] args ) throws IOException {
        new SimSharingServer().start();
    }

    private void start() throws IOException {
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Socket socket = studentServerSocket.accept();
                        System.out.println( "connected to student" );
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
                                        if ( teacherBufferedWriter != null ) {
                                            teacherBufferedWriter.write( line );
                                            teacherBufferedWriter.flush();
                                            System.out.println( "wrote to teacher: " + line );
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
                        teacherSocket = teacherServerSocket.accept();
                        System.out.println( "connected to teacher" );
                        teacherBufferedWriter = new BufferedWriter( new OutputStreamWriter( teacherSocket.getOutputStream() ) );

                        final BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( teacherSocket.getInputStream() ) );
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
