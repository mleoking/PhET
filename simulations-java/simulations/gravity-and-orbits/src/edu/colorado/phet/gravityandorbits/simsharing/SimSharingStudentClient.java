package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.gravityandorbits.model.BodyState;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class SimSharingStudentClient {
    int N = 1;
    int count = 0;
    private final Socket socket;
//    private final BufferedWriter bufferedWriter;
    private ObjectOutputStream objectOutputStream;

    public SimSharingStudentClient( final GravityAndOrbitsModule module, final JFrame parentFrame ) throws AWTException, IOException {
        final Robot robot = new Robot();
        final JFrame displayFrame = new JFrame();
        final JLabel contentPane = new JLabel();
        displayFrame.setContentPane( contentPane );
        final boolean[] firstTime = { true };
        socket = new Socket( SimSharingServer.host, SimSharingServer.STUDENT_PORT );
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream() );
//        bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );

        module.getGravityAndOrbitsModel().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                count++;
                if ( count % N == 0 ) {
                    new Thread( new Runnable() {
                        public void run() {
                            BufferedImage bufferedImage = robot.createScreenCapture( parentFrame.getBounds() );
                            System.out.println( "robot received image: " + bufferedImage );
                            contentPane.setIcon( new ImageIcon( bufferedImage ) );
                            if ( firstTime[0] ) {
                                displayFrame.pack();
                                displayFrame.setVisible( true );
                                firstTime[0] = false;
                            }
                            BodyState bodyState = module.getGravityAndOrbitsModel().getPlanet().toBodyState();
                            sendToServer( bodyState );
                        }
                    } ).start();
                }
            }
        } );
    }
    String mymonitor = "hello";

    private void sendToServer( BodyState bodyState ) {
        synchronized(mymonitor){
        try {
            objectOutputStream.writeObject( bodyState );
            objectOutputStream.flush();
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
