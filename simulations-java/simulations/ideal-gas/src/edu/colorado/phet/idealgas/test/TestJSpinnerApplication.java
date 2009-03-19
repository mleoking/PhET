
package edu.colorado.phet.idealgas.test;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This test isolates problem #1372, JSpinners are broken on Mac OS.
 * Typing Return/Enter in the spinner does not result in a call to stateChanged.
 */
public class TestJSpinnerApplication {

    public static void main( final String[] args ) {
//        new JSpinner();// if this is done first, no problem
        new JTextField() {
            public String getUIClassID() {
                return super.getUIClassID();
//                return "TextAreaUI"; // if we use this UI class id, no problem
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
