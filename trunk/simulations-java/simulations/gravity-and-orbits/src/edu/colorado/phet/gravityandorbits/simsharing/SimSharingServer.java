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

//    public static String host = "phet-server.colorado.edu";
    public static String host = "localhost";
    public static final int STUDENT_PORT = 3752;
    public static final int TEACHER_PORT = 3753;
    private Socket teacherSocket;
    private ObjectOutputStream teacherOutputStream;

    public SimSharingServer() throws IOException {
        System.out.println( "Started simsharing server" );
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
                        final ObjectInputStream objectInputStream = new ObjectInputStream( socket.getInputStream() );
//                        final BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
                        new Thread( new Runnable() {
                            public void run() {
                                try {
                                    while ( true ) {
                                        Object obj = objectInputStream.readObject();
                                        System.out.println( "obj = " + obj );
//                                        String line = bufferedReader.readLine();
//                                        System.out.println( "line = " + line );
                                        if ( obj == null ) {
                                            break;
                                        }
                                        if ( teacherOutputStream != null ) {
                                            teacherOutputStream.writeObject( obj );
//                                            teacherOutputStream.write( line + "\n" );
                                            teacherOutputStream.flush();
                                            System.out.println( "wrote to teacher: " + obj);
                                        }
                                    }
                                }
                                catch ( IOException e ) {
                                    e.printStackTrace();
                                }
                                catch ( ClassNotFoundException e ) {
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
                        teacherOutputStream = new ObjectOutputStream( teacherSocket.getOutputStream() );

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
