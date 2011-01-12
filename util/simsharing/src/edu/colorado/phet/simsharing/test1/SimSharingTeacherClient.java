// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simsharing.test1;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.*;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class SimSharingTeacherClient {
    private final Socket socket;
    private ObjectInputStream objectInputStream;

    public SimSharingTeacherClient( final GravityAndOrbitsApplication application, final JFrame parentFrame ) throws AWTException, IOException {
        final JFrame displayFrame = new JFrame();
        final JLabel contentPane = new JLabel();
        displayFrame.setContentPane( contentPane );
        socket = new Socket( SimSharingServer.HOST, SimSharingServer.TEACHER_PORT );

        BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );
        bufferedWriter.write( "Hello from teacher\n" );
        bufferedWriter.flush();

        System.out.println( "socket.getLocalPort() = " + socket.getLocalPort() + ", port = " + socket.getPort() );
        new Thread( new Runnable() {
            public void run() {
                try {
                    objectInputStream = new ObjectInputStream( socket.getInputStream() );
                    while ( true ) {

                        Object obj = objectInputStream.readObject();
//                        String line = bufferedReader.readLine();
//                        System.out.println( "line = " + line );
//                        System.out.println( "obj = " + obj );
                        final GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) obj;
                        state.apply( application );
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

    public void start() {
        System.out.println( "started teacher" );
    }
}
