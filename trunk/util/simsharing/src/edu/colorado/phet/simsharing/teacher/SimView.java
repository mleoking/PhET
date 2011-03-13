// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

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

    public static interface SampleSource {
        Pair<Sample, Integer> getSample( int index );

        public static class RemoteActor implements SampleSource {
            final ActorRef server;
            private final SessionID studentID;

            public RemoteActor( ActorRef server, SessionID studentID ) {
                this.server = server;
                this.studentID = studentID;
            }

            public Pair<Sample, Integer> getSample( int index ) {
                return (Pair<Sample, Integer>) server.sendRequestReply( new GetStudentData( studentID, index ) );
            }
        }
    }

    public SimView( final String[] args, final SessionID studentID, SampleSource sampleSource ) {
        this.args = args;
        this.sampleSource = sampleSource;
        timeControl = new TimeControlFrame( studentID );
        timeControl.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                doRun();
            }
        } );
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
                final GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) sample._1.getData();
                if ( state != null ) {
                    try {
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                state.apply( application );
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
        }
    }

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

    public static void main( String[] args ) {
        new SimView( args, new SessionID( 0, "Testing!" ), null ).start();
    }
}
