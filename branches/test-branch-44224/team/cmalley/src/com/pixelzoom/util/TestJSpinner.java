package com.pixelzoom.util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Test related to #1372, to confirm that general JSpinners work properly on Mac OS.
 * Values will be printed to System.out when the value is changed by using the spinner
 * arrows, typing a value followed by Enter, or typing a value and changing focus.
 */
public class TestJSpinner extends JFrame {
    
    public TestJSpinner() {
        
        final JSpinner spinner1 = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
        spinner1.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "spinner1=" + spinner1.getValue() );
            }
        });
        
        final JSpinner spinner2 = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
        spinner2.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "spinner2=" + spinner2.getValue() );
            }
        });
         
        Box box = new Box( BoxLayout.Y_AXIS );
        box.add( spinner1 );
        box.add( spinner2 );
        
        getContentPane().add( box );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        new TestJSpinner().setVisible( true );
    }
}
