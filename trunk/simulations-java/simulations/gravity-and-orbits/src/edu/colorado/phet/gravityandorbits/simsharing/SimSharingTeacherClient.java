package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.io.*;
import java.net.Socket;

import javax.swing.*;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class SimSharingTeacherClient {
    private final Socket socket;
    private final ObjectInputStream  objectInputStream;

    public SimSharingTeacherClient( final GravityAndOrbitsModule module, final JFrame parentFrame ) throws AWTException, IOException {
        final JFrame displayFrame = new JFrame();
        final JLabel contentPane = new JLabel();
        displayFrame.setContentPane( contentPane );
        socket = new Socket( SimSharingServer.HOST, SimSharingServer.TEACHER_PORT );

        BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );
        bufferedWriter.write( "Hello from teacher\n" );
        bufferedWriter.flush();

        System.out.println( "socket.getLocalPort() = " + socket.getLocalPort() + ", port = " + socket.getPort() );
        objectInputStream=new ObjectInputStream( socket.getInputStream() );
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Object obj = objectInputStream.readObject();
//                        String line = bufferedReader.readLine();
//                        System.out.println( "line = " + line );
                        System.out.println( "obj = " + obj );
                        module.setState( (GravityAndOrbitsState) obj );
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    catch ( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    public void start() {
        System.out.println( "started teacher" );
    }
}
