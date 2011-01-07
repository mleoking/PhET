// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class SimSharingStudentClient {
    int N = 1;
    int count = 0;
    //    private final BufferedWriter bufferedWriter;
    private ObjectOutputStream objectOutputStream;
    private final GravityAndOrbitsApplication application;

    public SimSharingStudentClient( final GravityAndOrbitsApplication application, final JFrame parentFrame ) throws AWTException, IOException {
        this.application = application;
        final JFrame displayFrame = new JFrame();
        final JLabel contentPane = new JLabel();
        displayFrame.setContentPane( contentPane );
        Socket socket = new Socket( SimSharingServer.HOST, SimSharingServer.STUDENT_PORT );
        objectOutputStream = new ObjectOutputStream( new BufferedOutputStream( socket.getOutputStream() ) );
//        bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );

        application.getGravityAndOrbitsModule().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing();
            }
        } );
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( application.getGravityAndOrbitsModule().getModeProperty().getValue().getModel().getClock().isPaused() ) {
                    updateSharing();
                }
            }
        } ).start();
    }

    private void updateSharing() {
        //                System.out.println( "In update" );
        if ( count % N == 0 ) {
//                    System.out.println( "mod = true" );
//                    new Thread( new Runnable() {
//                        public void run() {
//                            BufferedImage bufferedImage = robot.createScreenCapture( parentFrame.getBounds() );
//                            System.out.println( "robot received image: " + bufferedImage );
//                            contentPane.setIcon( new ImageIcon( bufferedImage ) );
//                            if ( firstTime[0] ) {
//                                displayFrame.pack();
//                                displayFrame.setVisible( true );
//                                firstTime[0] = false;
//                            }
            GravityAndOrbitsApplicationState state = new GravityAndOrbitsApplicationState( application );
//            System.out.println( "got app state" );
            sendToServer( state );
//                        }
//                    } ).start();
        }
        count++;
    }

    String mymonitor = "hello";

    private void sendToServer( GravityAndOrbitsApplicationState state ) {
//        System.out.println( "About to deliver message: " + state );
        synchronized ( mymonitor ) {
            try {
                objectOutputStream.writeObject( state );
                objectOutputStream.flush();
//                System.out.println( "delivered message: " + state );
//            bufferedWriter.write( bodyState.toString() + "\n" );
//            bufferedWriter.flush();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        System.out.println( "started student" );
    }
}
