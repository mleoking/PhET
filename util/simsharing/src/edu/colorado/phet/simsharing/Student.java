// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.simsharingcore.DefaultActor;
import edu.colorado.phet.common.simsharingcore.IActor;
import edu.colorado.phet.common.simsharingcore.ThreadedActor;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.EndSession;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StartSession;
import edu.colorado.phet.simsharing.server.Server;

/**
 * @author Sam Reid
 */
public class Student<U extends SimState, T extends SimsharingApplication<U>> {
    protected SessionID sessionID;

    //Flag to indicate whether the state should saved to the disk for analysis, such as checking frame size
    private static final boolean analyze = false;
    private Sim<U, T> sim;
    private String host;
    private int port;
    private String studentID;

    public Student( Sim<U, T> sim, String host, int port, String studentID ) {
        this.sim = sim;
        this.host = host;
        this.port = port;
        this.studentID = studentID;
    }

    public void start() throws IOException, ClassNotFoundException {

        //Communicate with the server in a separate thread
        final DefaultActor actor = new DefaultActor( host, port );
        final IActor nonBlockingClient = new ThreadedActor( actor );

        final T application = sim.launcher.apply();
        application.setExitStrategy( new VoidFunction0() {
            public void apply() {

                if ( sessionID != null ) {
                    try {
                        //Record the session end
                        actor.tell( new EndSession( sessionID ) );

                        //Allow the server thread to exit gracefully.  Blocks to ensure it happens before we exit
                        actor.tell( "logout" );
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
                System.exit( 0 );
            }
        } );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Student Edition" );
        final int batchSize = 1;

        final VoidFunction0 updateSharing = new VoidFunction0() {
            public boolean startedMessage = false;
            public boolean finishedMessage = false;

            ArrayList<U> stateCache = new ArrayList<U>();

            public void apply() {

                U state = application.getState();

                if ( analyze ) {
                    writeExampleForAnalysis( state );
                }

                stateCache.add( state );
                if ( sessionID == null ) {
                    if ( !startedMessage ) {
                        System.out.print( "Awaiting ID" );
                        startedMessage = true;
                    }
                    else {
                        System.out.print( "." );
                    }
                }
                else {
                    if ( !finishedMessage ) {
                        System.out.println( "\nReceived ID: " + sessionID );
                        finishedMessage = true;
                    }
                    if ( stateCache.size() >= batchSize ) {
                        try {
                            //Copy the state cache because it is cleared in the next step
                            nonBlockingClient.tell( new AddSamples( sessionID, new ArrayList<SimState>( stateCache ) ) );
                        }
                        catch ( IOException e ) {
                            e.printStackTrace();
                        }
                        stateCache.clear();
                    }
                }
            }
        };
        application.addModelSteppedListener( updateSharing );

        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( application.isPaused() ) {
                    updateSharing.apply();
                }
            }
        } ).start();

        new Thread( new Runnable() {
            public void run() {
                //be careful, this part blocks:
                try {
                    sessionID = (SessionID) nonBlockingClient.ask( new StartSession( sim.name, sim.project, sim.flavor, studentID ) );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
                catch ( ClassNotFoundException e ) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ", id = " + sessionID );
                    }
                } );
            }
        } ).start();

        application.setPlayButtonPressed( true );
    }

    private void writeExampleForAnalysis( U state ) {

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream( new FileOutputStream( "C:/Users/Sam/Desktop/saved.ser" ) );
            objectOutputStream.writeObject( state );
            objectOutputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void main( final String[] args ) throws IOException, AWTException, ClassNotFoundException {
        Server.parseArgs( args );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new StudentConfigFrame().setVisible( true );
            }
        } );
    }
}