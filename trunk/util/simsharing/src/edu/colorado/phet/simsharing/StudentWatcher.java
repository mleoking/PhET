// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class StudentWatcher {
    public Thread thread;
    public JFrame controlFrame;
    public GravityAndOrbitsApplication application;

    public StudentWatcher( final String[] args, final StudentID studentID, final ActorRef server ) {
        final Property<Integer> maxFrames = new Property<Integer>( 0 );
        final BooleanProperty live = new BooleanProperty( true );
        final Property<Integer> frameToDisplay = new Property<Integer>( 0 );
        controlFrame = new JFrame( "Time controls: " + studentID ) {{
            setContentPane( new JPanel( new BorderLayout() ) {{
                add( new JPanel() {{
                    add( new PropertyRadioButton<Boolean>( "Live", live, true ) );
                    add( new PropertyRadioButton<Boolean>( "Playback", live, false ) );
                    add( new JSpinner( new SpinnerNumberModel( 0, 0, 10000, 1 ) ) {{
                        addChangeListener( new ChangeListener() {
                            public void stateChanged( ChangeEvent e ) {
                                frameToDisplay.setValue( (Integer) getValue() );
                            }
                        } );
                        frameToDisplay.addObserver( new VoidFunction1<Integer>() {
                            public void apply( Integer integer ) {
                                setValue( integer );
                            }
                        } );
                    }} );
                }}, BorderLayout.WEST );
                add( new JLabel() {{
                    maxFrames.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            setText( "Frames: " + integer );
                        }
                    } );
                }}, BorderLayout.EAST );
                add( new JSlider( 0, 1, 0 ) {{
                    maxFrames.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            setMaximum( integer );
                        }
                    } );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            frameToDisplay.setValue( getValue() );
                        }
                    } );
                    frameToDisplay.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            setValue( integer );
                        }
                    } );
                }}, BorderLayout.CENTER );
            }} );
            pack();
        }};
        controlFrame.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                final boolean[] running = { true };

                //Have to launch from non-swing-thread otherwise receive:
                //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
                application = GAOHelper.launchApplication( args, new VoidFunction0() {
                    public void apply() {
                        //Just let the window close but do not system.exit the whole VM
                        running[0] = false;
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

                while ( running[0] ) {
                    Pair<Object, StudentMetadata> pair = (Pair<Object, StudentMetadata>) server.sendRequestReply( new TeacherDataRequest( studentID, live.getValue() ? Time.LIVE : new Time.Index( frameToDisplay.getValue() ) ) );
                    if ( pair != null ) {
                        final GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) pair._1;
                        maxFrames.setValue( pair._2.getNumSamples() );
                        if ( state != null ) {
                            try {
                                SwingUtilities.invokeAndWait( new Runnable() {
                                    public void run() {
                                        state.apply( application );
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
        } );
    }

    private void alignControls() {
        controlFrame.pack();
        controlFrame.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        controlFrame.setSize( application.getPhetFrame().getWidth(), controlFrame.getHeight() );
    }

    public void start() {
        thread.start();
    }
}
