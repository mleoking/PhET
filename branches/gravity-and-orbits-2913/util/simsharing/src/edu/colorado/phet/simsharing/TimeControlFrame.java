// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;

import static edu.colorado.phet.common.phetcommon.model.property.SettableNot.not;

/**
 * @author Sam Reid
 */
public class TimeControlFrame extends JFrame {
    public final Property<Integer> maxFrames = new Property<Integer>( 0 );
    public final BooleanProperty live = new BooleanProperty( true );
    public final Property<Integer> frameToDisplay = new Property<Integer>( 0 );
    public final BooleanProperty playing = new BooleanProperty( false );

    public TimeControlFrame( SessionID sessionID ) throws HeadlessException {
        super( "Time controls: " + sessionID );
        setContentPane( new JPanel( new BorderLayout() ) {{
            add( new JPanel() {{
                add( new PropertyRadioButton<Boolean>( "Live", live, true ) );
                add( new PropertyRadioButton<Boolean>( "Playback", live, false ) );
                add( new JSpinner( new SpinnerNumberModel( 0, 0, 10000, 1 ) ) {{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            frameToDisplay.set( (Integer) getValue() );
                        }
                    } );
                    frameToDisplay.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            setValue( integer );
                        }
                    } );
                }} );
                add( new JButton( "Play" ) {
                    {
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                playing.set( true );
                            }
                        } );
                        setEnabled( not( playing ).and( not( live ) ) );
                    }

                    private void setEnabled( CompositeBooleanProperty enabled ) {
                        enabled.addObserver( new VoidFunction1<Boolean>() {
                            public void apply( Boolean value ) {
                                setEnabled( value );
                            }
                        } );
                    }
                } );
                add( new JButton( "Pause" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            playing.set( false );
                        }
                    } );
                    playing.and( not( live ) ).addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean v ) {
                            setEnabled( v );
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
                        frameToDisplay.set( getValue() );
                    }
                } );
                frameToDisplay.addObserver( new VoidFunction1<Integer>() {
                    public void apply( Integer integer ) {
                        setValue( integer );
                    }
                } );
                live.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean aBoolean ) {
                        setEnabled( !aBoolean );
                    }
                } );
            }}, BorderLayout.CENTER );
        }} );
        pack();
    }

    public static void main( String[] args ) {
        new TimeControlFrame( new SessionID( 0, "Tester" ) ) {{
            pack();
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}
