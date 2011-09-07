// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.simsharing.messages.GetSample;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socket.Sample;
import edu.colorado.phet.simsharing.socketutil.IActor;

/**
 * @author Sam Reid
 */
public class SimView<U extends SimState, T extends SimsharingApplication<U>> {
    private final Thread thread;
    private final TimeControlFrame timeControl;
    private T application;
    private SessionID sessionID;
    private final IActor sampleSource;
    private boolean running = true;

    public SimView( final SessionID sessionID, IActor sampleSource, boolean playbackMode, final T application ) {
        this.sessionID = sessionID;
        this.sampleSource = sampleSource;
        this.application = application;
        timeControl = new TimeControlFrame( sessionID );
        timeControl.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                while ( running ) {
                    step();
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

    //TODO: rewrite to account for push
    private void step() {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    if ( timeControl.playing.get() ) {//TODO: may need a sleep
                        timeControl.frameToDisplay.set( Math.min( timeControl.frameToDisplay.get() + 1, timeControl.maxFrames.get() ) );
                    }
                }
            } );

            final int sampleIndex = timeControl.live.get() ? -1 : timeControl.frameToDisplay.get();
            final Sample<U> sample = (Sample<U>) sampleSource.ask( new GetSample( sessionID, sampleIndex ) );
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    application.setState( sample.state );
                    timeControl.maxFrames.set( sample.totalSampleCount );
                }
            } );
        }
        catch ( Exception e ) {
            e.printStackTrace();
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