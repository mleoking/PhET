
package edu.colorado.phet.translationutility.test;

import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Demonstrates a user interface issue in Translation Utility related to Unfuddle #1629. 
 * <p>
 * The text fields are validated when they loses focus, and an
 * error dialog opens if a text field's value is invalid. If focus is lost as
 * the result of moving to another text field, this is OK. But if focus is lost
 * as the result of pressing a button, we want to prevent the action associated
 * with the button from taking place if the text field is invalid.
 * <p>
 * Additionally, we need to validate all fields before performing the button
 * action, since some text fields may not have lost focus (and therefore have
 * not been validated.) That issue is not addressed here, but needs to be 
 * addressed in Translation Utility.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestValidation extends JFrame {

    public TestValidation() {
        super( "TestValidation" );

        // controls
        IntegerControl control1 = new IntegerControl();
        IntegerControl control2 = new IntegerControl();
        JButton button = new DoSomethingButton();

        // layout
        Box box = new Box( BoxLayout.Y_AXIS );
        box.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        box.add( control1 );
        box.add( control2 );
        box.add( button );
        setContentPane( box );

        pack();
        setResizable( false );
        SwingUtils.centerWindowOnScreen( this );
    }

    private static class IntegerControl extends Box {

        public IntegerControl() {
            super( BoxLayout.X_AXIS );
            JLabel label = new JLabel( "Enter an integer:" );
            JTextField textField = new IntegerTextField();
            add( label );
            add( textField );
        }
    }

    private static class IntegerTextField extends JTextField {

        public IntegerTextField() {
            super();
            setColumns( 10 );
            setBackground( Color.WHITE );
            addKeyListener( new KeyAdapter() {
                public void keyPressed( KeyEvent e ) {
                    setBackground( Color.WHITE );
                }
            } );
            addFocusListener( new FocusAdapter() {
                public void focusLost( FocusEvent e ) {
                    validateValue();
                }
            } );
        }

        private void validateValue() {
            String s = getText();
            if ( s != null && s.length() != 0 ) {
                try {
                    Integer.parseInt( s );
                }
                catch ( NumberFormatException e ) {
                    setBackground( Color.RED );
                    String message = s + " is not an integer.";
                    JOptionPane.showMessageDialog( this, message, "Error", JOptionPane.ERROR_MESSAGE );
                }
            }
        }
    }

    public static class DoSomethingButton extends JButton {

        public DoSomethingButton() {
            super( "Do something" );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JOptionPane.showMessageDialog( DoSomethingButton.this, "Doing something." );
                }
            } );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestValidation();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
