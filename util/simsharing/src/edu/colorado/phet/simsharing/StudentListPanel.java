// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.ActorRef;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * @author Sam Reid
 */
public class StudentListPanel extends PSwingCanvas {
    private final ActorRef server;
    private final String[] args;
    public PNode studentNode;

    public StudentListPanel( final ActorRef server, String[] args ) {
        this.server = server;
        this.args = args;
        studentNode = new PNode();
        getLayer().addChild( studentNode );
        //Look for new students this often
        new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateStudentList();
            }
        } ) {{
            setInitialDelay( 0 );
        }}.start();
    }

    private StudentComponent getComponent( StudentID studentID ) {
        for ( int i = 0; i < studentNode.getChildrenCount(); i++ ) {
            PNode child = studentNode.getChild( i );
            if ( child instanceof StudentComponent && ( (StudentComponent) child ).studentID.equals( studentID ) ) {
                return (StudentComponent) child;
            }
        }
        return null;
    }

    private void updateStudentList() {
        double y = 0;
        final StudentList newData = (StudentList) server.sendRequestReply( new GetThumbnails() );
        for ( final Pair<StudentID, SerializableBufferedImage> elm : newData.getStudentIDs() ) {
            final StudentID studentID = elm._1;
            StudentComponent component = getComponent( studentID );
            if ( component == null ) {
                component = new StudentComponent( studentID, new VoidFunction0() {
                    public void apply() {
                        watch( studentID );
                    }
                } );
                studentNode.addChild( component );
            }
            component.setThumbnail( elm._2.getBufferedImage() );
            component.setOffset( 0, y + 2 );
            y = component.getFullBounds().getMaxY();
        }

        //Remove components for students that have exited
        ArrayList<PNode> toRemove = new ArrayList<PNode>();
        for ( int i = 0; i < studentNode.getChildrenCount(); i++ ) {
            PNode child = studentNode.getChild( i );
            if ( child instanceof StudentComponent && !newData.containsStudent( ( (StudentComponent) child ).getStudentID() ) ) {
                toRemove.add( child );
            }
        }
        studentNode.removeChildren( toRemove );
    }

    public void watch( final StudentID studentID ) {
        final Property<Integer> maxFrames = new Property<Integer>( 0 );
        final BooleanProperty live = new BooleanProperty( true );
        final Property<Integer> spinnerValue = new Property<Integer>( 0 );
        JFrame controlFrame = new JFrame( "Time controls: " + studentID ) {{
            setContentPane( new JPanel() {{
                add( new PropertyRadioButton<Boolean>( "Live", live, true ) );
                add( new PropertyRadioButton<Boolean>( "Playback", live, false ) );
                add( new JSpinner( new SpinnerNumberModel( 0, 0, 10000, 1 ) ) {{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            spinnerValue.setValue( (Integer) getValue() );
                        }
                    } );
                }} );
                add( new JLabel() {{
                    maxFrames.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            setText( "Frames: " + integer );
                        }
                    } );
                }} );
            }} );
            pack();
        }};
        controlFrame.setVisible( true );
        Thread t = new Thread( new Runnable() {
            public void run() {
                //Have to launch from non-swing-thread otherwise receive:
                //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
                final GravityAndOrbitsApplication application = GAOHelper.launchApplication( args );
                System.out.println( "application = " + application );
                application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" );
                application.getIntro().setTeacherMode( true );

                while ( true ) {
                    Pair<Object, StudentMetadata> pair = (Pair<Object, StudentMetadata>) server.sendRequestReply( new TeacherDataRequest( studentID, live.getValue() ? Time.LIVE : new Time.Index( spinnerValue.getValue() ) ) );
                    final GravityAndOrbitsApplicationState state = (GravityAndOrbitsApplicationState) pair._1;
                    maxFrames.setValue( pair._2.getNumSamples() );
                    System.out.println( "Received metadata = " + pair._2 );
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
        } );
        t.start();
    }
}
