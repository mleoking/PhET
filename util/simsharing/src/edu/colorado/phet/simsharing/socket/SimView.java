// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplicationState;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.simsharing.TimeControlFrame;
import edu.colorado.phet.simsharing.messages.SessionID;

/**
 * @author Sam Reid
 */
public class SimView<U extends SimsharingApplicationState, T extends SimsharingApplication<U>> {
    private final Thread thread;
    private final TimeControlFrame timeControl;
    private T application;
    private final RemoteActor<U> sampleSource;
    private boolean running = true;

    public SimView( final SessionID sessionID, RemoteActor<U> sampleSource, boolean playbackMode, T application ) {
        this.sampleSource = sampleSource;
        this.application = application;
        timeControl = new TimeControlFrame( sessionID );
        timeControl.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                try {
                    doRun();
                }
                catch ( ClassNotFoundException e ) {
                    e.printStackTrace();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
        timeControl.live.set( !playbackMode );
        if ( playbackMode ) { timeControl.playing.set( true ); }

        application.setExitStrategy( new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControl.setVisible( false );//TODO: detach listeners
            }
        } );
    }

    private void doRun() throws ClassNotFoundException, IOException {
        while ( running ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        if ( timeControl.playing.get() ) {//TODO: may need a sleep
                            timeControl.frameToDisplay.set( Math.min( timeControl.frameToDisplay.get() + 1, timeControl.maxFrames.get() ) );
                        }
                    }
                } );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }

            final int sampleIndex = timeControl.live.get() ? -1 : timeControl.frameToDisplay.get();
            final U sample = sampleSource.getSample( sampleIndex );
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        application.setState( sample );

                        //TODO: we used to pass back the sample index so that we could handle live
                        if ( sampleIndex > 0 ) {
                            timeControl.maxFrames.set( sampleIndex );
                        }
                    }
                } );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
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
        thread.start();
    }
}