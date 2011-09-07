// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.GetSample;
import edu.colorado.phet.simsharing.messages.RegisterPushConnection;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socket.Sample;
import edu.colorado.phet.simsharing.socketutil.Client;

/**
 * @author Sam Reid
 */
public class SimView<U extends SimState, T extends SimsharingApplication<U>> {
    private final Thread thread;
    private final TimeControlFrame timeControl;
    private final T application;
    private final SessionID sessionID;
    private final Client client;
    private boolean running = true;

    public SimView( final SessionID sessionID, final Client client, boolean playbackMode, final T application ) {
        this.sessionID = sessionID;
        this.client = client;
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
        if ( playbackMode ) {
            timeControl.playing.set( true );
        }

        application.setExitStrategy( new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControl.setVisible( false );//TODO: detach listeners
            }
        } );

        new Thread( new Runnable() {
            public void run() {

                //Create a new client and dedicated thread on the server so that we don't accidentally intercept messages like StudentList
                Client client = null;
                try {
                    client = new Client();
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
                try {
                    client.tell( new RegisterPushConnection( sessionID ) );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }

                //Whenever we get a new sample, show it on the screen
                while ( running ) {
                    try {
                        synchronized ( client.readFromServer ) {
                            AddSamples<U> s = (AddSamples<U>) client.readFromServer.readObject();
                            if ( s.getData().size() > 0 ) {
                                final U lastSample = s.getData().get( s.getData().size() - 1 );

                                //Rendering is delayed unless you set state in the swing thread
                                SwingUtilities.invokeAndWait( new Runnable() {
                                    public void run() {
                                        application.setState( lastSample );
                                    }
                                } );
                            }
                        }
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    //TODO: rewrite to account for push
    private void step() {

        //Read samples when not live.  When live, data is pushed to the SimView
        if ( !timeControl.live.get() ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        try {
                            if ( timeControl.playing.get() ) {//TODO: may need a sleep
                                timeControl.frameToDisplay.set( Math.min( timeControl.frameToDisplay.get() + 1, timeControl.maxFrames.get() ) );
                            }
                            final int sampleIndex = timeControl.live.get() ? -1 : timeControl.frameToDisplay.get();
                            final Sample<U> sample;

                            sample = (Sample<U>) client.ask( new GetSample( sessionID, sampleIndex ) );

                            application.setState( sample.state );
                            timeControl.maxFrames.set( sample.totalSampleCount );
                        }
                        catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                } );
            }
            catch ( Exception e ) {
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