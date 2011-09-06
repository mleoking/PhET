// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.codehaus.jackson.map.ObjectMapper;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.ImageFactory;
import edu.colorado.phet.simsharing.GAOHelper;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.EndSession;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StartSession;
import edu.colorado.phet.simsharing.socketutil.Client;
import edu.colorado.phet.simsharing.socketutil.IActor;
import edu.colorado.phet.simsharing.socketutil.ThreadedActor;

/**
 * @author Sam Reid
 */
public class Student {
    private final String[] args;
    protected SessionID sessionID;
    private final ImageFactory imageFactory = new ImageFactory();
    private final ObjectMapper mapper = new ObjectMapper();

    //Flag to indicate whether the state should saved to the disk for analysis, such as checking frame size
    private static final boolean analyze = false;

    public Student( String[] args ) {
        this.args = args;
    }

    public void start() throws IOException, ClassNotFoundException {

        //Communicate with the server in a separate thread
        final Client client = new Client();
        final IActor server = new ThreadedActor( client );

        final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args, new VoidFunction0() {
            //TODO: could move exit listeners here instead of in PhetExit
            public void apply() {
                if ( sessionID != null ) {
                    try {
                        //Record the session end
                        server.tell( new EndSession( sessionID ) );

                        //Allow the server thread to exit gracefully.  Blocks to ensure it happens before we exit
                        client.tell( "logout" );
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

            ArrayList<GravityAndOrbitsApplicationState> stateCache = new ArrayList<GravityAndOrbitsApplicationState>();

            public void apply() {

                GravityAndOrbitsApplicationState state = new GravityAndOrbitsApplicationState( application, imageFactory );

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
                            server.tell( new AddSamples<GravityAndOrbitsApplicationState>( sessionID, new ArrayList<GravityAndOrbitsApplicationState>( stateCache ) ) );
                        }
                        catch ( IOException e ) {
                            e.printStackTrace();
                        }
                        stateCache.clear();
                    }
                }
            }
        };
        application.getIntro().addModelSteppedListener( new SimpleObserver() {
            public void update() {
                updateSharing.apply();
            }
        } );
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( application.getIntro().modeProperty.get().getModel().getClock().isPaused() ) {
                    updateSharing.apply();
                }
            }
        } ).start();

        new Thread( new Runnable() {
            public void run() {
                //be careful, this part blocks:
                try {
                    sessionID = (SessionID) server.ask( new StartSession() );
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
        application.getIntro().playButtonPressed.set( true );
    }

    private void writeExampleForAnalysis( GravityAndOrbitsApplicationState state ) {

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
        new Student( args ).start();
    }
}