// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class StudentWatcher {
    public Thread thread;
    public TimeControlFrame controlFrame;
    public GravityAndOrbitsApplication application;
    private final String[] args;
    private final StudentID studentID;
    private final ActorRef server;

    public StudentWatcher( final String[] args, final StudentID studentID, final ActorRef server ) {
        this.args = args;
        this.studentID = studentID;
        this.server = server;
        controlFrame = new TimeControlFrame( studentID );
        controlFrame.setVisible( true );
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
                        if ( controlFrame.playing.getValue() ) {//TODO: may need a sleep
                            controlFrame.frameToDisplay.setValue( Math.min( controlFrame.frameToDisplay.getValue() + 1, controlFrame.maxFrames.getValue() ) );
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

            final Pair<Object, StudentMetadata> pair = (Pair<Object, StudentMetadata>) server.sendRequestReply( new TeacherDataRequest( studentID, controlFrame.live.getValue() ? Time.LIVE : new Time.Index( controlFrame.frameToDisplay.getValue() ) ) );
            if ( pair != null ) {
                final GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) pair._1;
                if ( state != null ) {
                    try {
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                state.apply( application );
                                controlFrame.maxFrames.setValue( pair._2.getNumSamples() );
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
        controlFrame.pack();
        controlFrame.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        controlFrame.setSize( application.getPhetFrame().getWidth(), controlFrame.getHeight() );
    }

    boolean running = true;

    public void start() {
        //Have to launch from non-swing-thread otherwise receive:
        //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
        application = GAOHelper.launchApplication( args, new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                controlFrame.setVisible( false );//TODO: detach listeners
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
        new StudentWatcher( args, new StudentID( 0, "Testing!" ), null ).start();
    }
}
