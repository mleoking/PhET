// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.simsharingcore.Client;
import edu.colorado.phet.simsharing.messages.GetSamplesAfter;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;

/**
 * @author Sam Reid
 */
public class SimView<U extends SimState, T extends SimsharingApplication<U>> {
    private final TimeControlFrame timeControl;
    private final T application;
    private boolean running = true;
    private ArrayList<U> states = new ArrayList<U>();
    private boolean debugElapsedTime = false;
    private final Timer timer;
    private final Thread thread;

    public SimView( final SessionID sessionID, final T application ) {

        this.application = application;
        timeControl = new TimeControlFrame( sessionID );
        timeControl.setVisible( true );

        application.setExitStrategy( new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControl.setVisible( false );//TODO: detach listeners
            }
        } );

        timeControl.frame.addObserver( new VoidFunction1<Integer>() {
            public void apply( final Integer frame ) {
                if ( frame >= 0 && frame < states.size() ) {
                    long t = System.currentTimeMillis();
                    application.setState( states.get( frame ) );
                    long t2 = System.currentTimeMillis();
                    if ( debugElapsedTime ) {
                        System.out.println( "elapsed: " + ( t2 - t ) );
                    }
                }
            }
        } );

        //Timer that shows the loaded states in order
        timer = new Timer( 33, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( timeControl.playing.get() && timeControl.frame.get() < states.size() ) {
                    timeControl.frame.set( timeControl.frame.get() + 1 );
                }
            }
        } );

        //Acquire new states from the server
        thread = new Thread( new Runnable() {
            public void run() {
                //Create a new Client (including a new thread on the server) to avoid synchronization problems with other client
                Client client = null;
                try {
                    client = new Client();
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }

                while ( running ) {
                    try {
                        int index = -1;
                        if ( states.size() > 0 ) {
                            index = states.get( states.size() - 1 ).getIndex();
                        }
                        final SampleBatch<U> sample = (SampleBatch<U>) client.ask( new GetSamplesAfter( sessionID, index ) );

                        timeControl.numFrames.set( sample.totalNumberStates );

                        for ( U u : sample ) {
                            if ( !states.contains( u ) ) {
                                states.add( u );
                            }
                        }

                        //Download a batch of states this often
                        Thread.sleep( 1000 );
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );
    }

    private void alignControls() {
        timeControl.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        timeControl.setSize( application.getPhetFrame().getWidth(), timeControl.getPreferredSize().height );
    }

    public void start() {
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" );
        application.getPhetFrame().addComponentListener( new ComponentAdapter() {
            @Override
            public void componentMoved( ComponentEvent e ) {
                alignControls();
            }

            @Override
            public void componentResized( ComponentEvent e ) {
                alignControls();
            }
        } );
        alignControls();
        application.setTeacherMode( true );
        timer.start();
        thread.start();
    }
}