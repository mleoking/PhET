package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.*;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class SimSharingTeacherClient {
    private final Socket socket;
    private final BufferedReader bufferedReader;

    public SimSharingTeacherClient( final GravityAndOrbitsModule module, final JFrame parentFrame ) throws AWTException, IOException {
        final JFrame displayFrame = new JFrame();
        final JLabel contentPane = new JLabel();
        displayFrame.setContentPane( contentPane );
        socket = new Socket( SimSharingServer.host, SimSharingServer.TEACHER_PORT );
        bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        String line = bufferedReader.readLine();
                        System.out.println( "line = " + line );
                    }
                    catch ( IOException e ) {
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
