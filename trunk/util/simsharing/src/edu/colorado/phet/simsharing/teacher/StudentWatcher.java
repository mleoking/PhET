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
public class StudentWatcher {
    public Thread thread;
    public TimeControlFrame timeControlFrame;
    public GravityAndOrbitsApplication application;
    private final String[] args;
    private final StudentID studentID;
    private final ActorRef server;

    public StudentWatcher( final String[] args, final StudentID studentID, final ActorRef server ) {
        this.args = args;
        this.studentID = studentID;
        this.server = server;
        timeControlFrame = new TimeControlFrame( studentID );
        timeControlFrame.setVisible( true );
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
                        if ( timeControlFrame.playing.getValue() ) {//TODO: may need a sleep
                            timeControlFrame.frameToDisplay.setValue( Math.min( timeControlFrame.frameToDisplay.getValue() + 1, timeControlFrame.maxFrames.getValue() ) );
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

            final Pair<Sample, StudentMetadata> pair = (Pair<Sample, StudentMetadata>) server.sendRequestReply( new GetStudentData( studentID, timeControlFrame.live.getValue() ? Time.LIVE : new Time.Index( timeControlFrame.frameToDisplay.getValue() ) ) );
            if ( pair != null ) {
                final GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) pair._1.getData();
                if ( state != null ) {
                    try {
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                state.apply( application );
                                timeControlFrame.maxFrames.setValue( pair._2.getNumSamples() );
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
        timeControlFrame.pack();
        timeControlFrame.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        timeControlFrame.setSize( application.getPhetFrame().getWidth(), timeControlFrame.getHeight() );
    }

    boolean running = true;

    public void start() {
        //Have to launch from non-swing-thread otherwise receive:
        //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
        application = GAOHelper.launchApplication( args, new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControlFrame.setVisible( false );//TODO: detach listeners
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
