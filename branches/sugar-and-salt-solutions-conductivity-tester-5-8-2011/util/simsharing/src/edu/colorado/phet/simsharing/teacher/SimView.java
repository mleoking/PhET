// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import org.codehaus.jackson.map.ObjectMapper;

import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.simsharing.*;

/**
 * @author Sam Reid
 */
public class SimView {
    private final Thread thread;
    private final TimeControlFrame timeControl;
    private GravityAndOrbitsApplication application;
    private final String[] args;
    private final SampleSource sampleSource;
    private final boolean autoplay;

    public static interface SampleSource {
        Pair<Sample, Integer> getSample( int index );

        public static class RemoteActor implements SampleSource {
            final ActorRef server;
            private final SessionID sessionID;

            public RemoteActor( ActorRef server, SessionID sessionID ) {
                this.server = server;
                this.sessionID = sessionID;
            }

            public Pair<Sample, Integer> getSample( int index ) {
                return (Pair<Sample, Integer>) server.sendRequestReply( new GetStudentData( sessionID, index ) );
            }
        }
    }

    public SimView( final String[] args, final SessionID sessionID, SampleSource sampleSource, boolean playbackMode ) {
        this.args = args;
        this.sampleSource = sampleSource;
        this.autoplay = playbackMode;
        timeControl = new TimeControlFrame( sessionID );
        timeControl.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                doRun();
            }
        } );
        timeControl.live.setValue( !playbackMode );
        if ( playbackMode ) { timeControl.playing.setValue( true ); }
    }

    private void doRun() {
        while ( running ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        if ( timeControl.playing.getValue() ) {//TODO: may need a sleep
                            timeControl.frameToDisplay.setValue( Math.min( timeControl.frameToDisplay.getValue() + 1, timeControl.maxFrames.getValue() ) );
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

            final Pair<Sample, Integer> sample = sampleSource.getSample( timeControl.live.getValue() ? -1 : timeControl.frameToDisplay.getValue() );
            if ( sample != null ) {
                GravityAndOrbitsApplicationState state = null;
                try {
                    state = mapper.readValue( sample._1.getJson(), GravityAndOrbitsApplicationState.class );
                    if ( state != null ) {
                        try {
                            final GravityAndOrbitsApplicationState finalState = state;
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    finalState.apply( application );
                                    timeControl.maxFrames.setValue( sample._2 );
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
                catch ( IOException e ) {
                    e.printStackTrace();
                }

            }
        }
    }

    ObjectMapper mapper = new ObjectMapper();

    private void alignControls() {
        timeControl.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        timeControl.setSize( application.getPhetFrame().getWidth(), timeControl.getPreferredSize().height );
    }

    boolean running = true;

    public void start() {
        //Have to launch from non-swing-thread otherwise receive:
        //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
        application = GAOHelper.launchApplication( args, new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControl.setVisible( false );//TODO: detach listeners
            }
        } );
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
        application.getIntro().setTeacherMode( true );
        application.getToScale().setTeacherMode( true );
        thread.start();
    }

}
