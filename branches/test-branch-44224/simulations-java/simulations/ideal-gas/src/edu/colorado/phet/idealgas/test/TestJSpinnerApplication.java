
package edu.colorado.phet.idealgas.test;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * This test isolates problem #1372, JSpinners are broken on Mac OS.
 * Typing Return/Enter in the spinner does not result in a call to stateChanged.
 */
public class TestJSpinnerApplication {

    public static void main( final String[] args ) throws UnsupportedLookAndFeelException {
//        UIManager.setLookAndFeel( new MetalLookAndFeel() ); // workaround: problem is in Aqua L&F
//        new JSpinner();// workaround
        new JTextField() {
            public String getUIClassID() {
                return super.getUIClassID();
//                return "TextAreaUI"; // workaround
            }
        };
        final JSpinner spinner = new JSpinner();
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "spinner value = " + spinner.getValue() );
            }
        } );
        JFrame frame = new JFrame();
        frame.getContentPane().add( spinner );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
