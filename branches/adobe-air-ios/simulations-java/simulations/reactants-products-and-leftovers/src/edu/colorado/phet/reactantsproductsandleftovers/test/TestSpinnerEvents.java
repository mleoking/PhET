// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers.test;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;


public class TestSpinnerEvents extends JFrame {

    private static final IntegerRange SPINNER_RANGE = new IntegerRange( 0, 10, 0 );

    private final IntegerSpinner spinner1, spinner2;

    public static class TestIntegerSpinner extends IntegerSpinner {
        public TestIntegerSpinner( final IUserComponent userComponent, IntegerRange range ) {
            super( userComponent, range );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    System.out.println( userComponent + ".stateChanged value=" + getValue() );
                }
            } );
            addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed( MouseEvent event ) {
                    System.out.println( userComponent + ".mousePressed" );
                }

                @Override
                public void mouseReleased( MouseEvent event ) {
                    System.out.println( userComponent + ".mouseRelease" );
                }
            } );
            ( (NumberEditor) getEditor() ).getTextField().addFocusListener( new FocusListener() {
                public void focusGained( FocusEvent e ) {
                    System.out.println( userComponent + ".focusGained" );
                }

                public void focusLost( FocusEvent e ) {
                    System.out.println( userComponent + ".focusLost" );
                }
            } );
        }
    }

    public TestSpinnerEvents() {
        super( TestSpinnerEvents.class.getName() );
        spinner1 = new TestIntegerSpinner( new IUserComponent() {
        }, SPINNER_RANGE );
        spinner2 = new TestIntegerSpinner( new IUserComponent() {
        }, SPINNER_RANGE );
        JPanel panel = new JPanel();
        panel.add( spinner1 );
        panel.add( spinner2 );
        setContentPane( panel );
        setSize( new Dimension( 300, 200 ) );
    }

    public static void main( String[] args ) {
        JFrame frame = new TestSpinnerEvents();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
