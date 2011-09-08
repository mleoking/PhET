// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.simsharing.messages.SessionID;

/**
 * @author Sam Reid
 */
public class TimeControlFrame extends JFrame {
    public final Property<Integer> numFrames = new Property<Integer>( 0 );
    public final Property<Integer> frame = new Property<Integer>( 0 );
    public final BooleanProperty playing = new BooleanProperty( true );

    public TimeControlFrame( SessionID sessionID ) throws HeadlessException {
        super( "Time controls: " + sessionID );
        setContentPane( new JPanel( new BorderLayout() ) {{
            add( new JPanel() {{
                add( new JButton( "Live" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            frame.set( numFrames.get() - 1 );
                        }
                    } );
                }} );
                add( new JSpinner( new SpinnerNumberModel( 0, 0, 10000, 1 ) ) {{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            frame.set( (Integer) getValue() );
                        }
                    } );
                    frame.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            setValue( integer );
                        }
                    } );
                }} );
                add( new JButton( "Play" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            playing.set( true );
                        }
                    } );
                    playing.addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean playing ) {
                            setEnabled( !playing );
                        }
                    } );
                }} );
                add( new JButton( "Pause" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            playing.set( false );
                        }
                    } );
                    playing.addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean v ) {
                            setEnabled( v );
                        }
                    } );
                }} );
            }}, BorderLayout.WEST );
            add( new JLabel() {{
                numFrames.addObserver( new VoidFunction1<Integer>() {
                    public void apply( Integer integer ) {
                        setText( "Frames: " + integer );
                    }
                } );
            }}, BorderLayout.EAST );
            add( new JSlider( 0, 1, 0 ) {{
                numFrames.addObserver( new VoidFunction1<Integer>() {
                    public void apply( Integer integer ) {
                        setMaximum( integer );
                    }
                } );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        frame.set( getValue() );
                    }
                } );
                frame.addObserver( new VoidFunction1<Integer>() {
                    public void apply( Integer integer ) {
                        setValue( integer );
                    }
                } );
            }}, BorderLayout.CENTER );
        }} );
        pack();
    }

    public static void main( String[] args ) {
        new TimeControlFrame( new SessionID( 0, "Tester", "test" ) ) {{
            pack();
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}
