
package edu.colorado.phet.translationutility.test;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
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
        IntegerTextField control1 = new IntegerTextField();
        IntegerTextField control2 = new IntegerTextField();
        JButton button = new DoSomethingButton();
        
        // wiring for validation
        ValidationErrorListener listener = new ValidationErrorListener() {
            public void validationError( JTextField textField, String errorMessage ) {
                JOptionPane.showMessageDialog( TestValidation.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE );
            }
        };
        control1.addValidationErrorListener( listener );
        control2.addValidationErrorListener( listener );

        // layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.addComponent( new JLabel( "Integer A:" ), 0, 0 );
        layout.addComponent( control1, 0, 1 );
        layout.addComponent( new JLabel( "Integer B:" ), 1, 0 );
        layout.addComponent( control2, 1, 1 );
        layout.addComponent( button, 2, 0, 2, 1 );
        setContentPane( panel );

        pack();
        setResizable( false );
        SwingUtils.centerWindowOnScreen( this );
    }
    
    private interface ValidationErrorListener {
        public void validationError( JTextField textField, String errorMessage );
    }

    private static class IntegerTextField extends JTextField {
        
        private static final Color OK_COLOR = Color.WHITE;
        private static final Color ERROR_COLOR = Color.RED;
        
        private final ArrayList<ValidationErrorListener> listeners;

        public IntegerTextField() {
            super();
            listeners = new ArrayList<ValidationErrorListener>();
            setColumns( 10 );
            setBackground( OK_COLOR );
            addKeyListener( new KeyAdapter() {
                public void keyPressed( KeyEvent e ) {
                    setBackground( OK_COLOR );
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
                    setBackground( ERROR_COLOR );
                    fireValidationError( s + " is not an integer." );
                }
            }
        }
        
        public void addValidationErrorListener( ValidationErrorListener listener ) {
            listeners.add( listener );
        }
        
        public void removeValidationErrorListener( ValidationErrorListener listener ) {
            listeners.remove( listener );
        }
        
        private void fireValidationError( String errorMessage ) {
            for ( ValidationErrorListener listener : listeners ) {
                listener.validationError( this, errorMessage );
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
