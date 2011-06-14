
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.translationutility.test;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.TUImages;

/**
 * Test bed for Translation Utility user interface issues related to Unfuddle #1629. 
 * <p>
 * Originally tried to validate and report errors (via a dialog) each time a 
 * field loses focus.  This becomes problematic when focus is lost due to 
 * pressing a button that uses the field.  We end up with multiple error dialogs;
 * one for the field validation, one for the operation that can't be completed.
 * We also need to validate all fields related to the button in case some field
 * hasn't lost focus and therefore hasn't been validated.
 * <p>
 * A different approach (demonstrated here) is to still validate individual fields 
 * as they lose focus. But instead of automatically opening a dialog, add a visual
 * indicator that the field's value is invalid. We set the field's background to 
 * red and put an error icon to the right of the field.  Pressing the icon opens
 * a dialog that describes specific problems with the field.  When the button is
 * pressed, all related fields are validated. If any errors are found, a general
 * error message is displayed telling how to explore errors. Only if all fields are
 * valid does the action associated with the button proceeds. 
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestValidation extends JFrame {
    
    public TestValidation() {
        super( "TestValidation" );
        
        // controls
        IntegerControl field1 = new IntegerControl( "Integer A:", 0 );
        IntegerControl field2 = new IntegerControl( "Integer B:", 0 );
        JButton button = new AddButton( field1, field2 );
        
        // layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.addComponent( field1, 0, 0 );
        layout.addComponent( field2, 1, 0 );
        layout.addComponent( button, 2, 0 );
        setContentPane( panel );

        pack();
        setResizable( false );
        SwingUtils.centerWindowOnScreen( this );
    }
    
    private static class IntegerControl extends JPanel {
        
        private static final Color OK_COLOR = Color.WHITE;
        private static final Color INVALID_COLOR = Color.RED;
        
        private final JTextField textField;
        private final JLabel errorDetailsButton;
        private String errorMessage;
        private boolean isValid;
        
        public IntegerControl( String labelText, int value ) {
            super();
            
            JLabel label = new JLabel( labelText );
            
            textField = new JTextField( String.valueOf( value ) );
            textField.setColumns( 5 );
            textField.setBackground( OK_COLOR );
            textField.addKeyListener( new KeyAdapter() {
                public void keyPressed( KeyEvent e ) {
                    textField.setBackground( OK_COLOR );
                    errorDetailsButton.setVisible( false );
                    errorMessage = null;
                }
            } );
            textField.addFocusListener( new FocusAdapter() {
                public void focusLost( FocusEvent e ) {
                    validateValue();
                }
            } );
            
            errorDetailsButton = new JLabel( TUImages.ERROR_BUTTON );
            errorDetailsButton.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
            errorDetailsButton.addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent event ) {
                    JOptionPane.showMessageDialog( IntegerControl.this, errorMessage, "Details", JOptionPane.ERROR_MESSAGE );
                }
            });
            
            // layout
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            layout.addComponent( label, 0, 0 );
            layout.addComponent( textField, 0, 1 );
            layout.addComponent( errorDetailsButton, 0, 2 );
            layout.setMinimumWidth( 2, errorDetailsButton.getPreferredSize().width + 20 );
            
            // initial state
            validateValue();
        }
        
        public boolean isValid() {
            validateValue();
            return isValid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public int getValue() throws NumberFormatException {
            return Integer.parseInt( textField.getText() );
        }
        
        private void validateValue() {
            isValid = true;
            textField.setBackground( OK_COLOR );
            errorDetailsButton.setVisible( false );
            errorMessage = null;
            try {
                getValue();
            }
            catch ( NumberFormatException e ) {
                isValid = false;
                textField.setBackground( INVALID_COLOR );
                errorDetailsButton.setVisible( true );
                errorMessage = "\"" + textField.getText() + "\" is not an integer.";
            }
        }
    }
    
    public static class AddButton extends JButton {
        
        private final IntegerControl control1, control2;

        public AddButton( final IntegerControl field1, final IntegerControl field2 ) {
            super( "A + B = ..." );
            this.control1 = field1;
            this.control2 = field2;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleAction();
                }
            } );
        }
        
        private void handleAction() {
            if ( control1.isValid() && control2.isValid() ) {
                try {
                    int sum = control1.getValue() + control2.getValue();
                    JOptionPane.showMessageDialog( AddButton.this, "A + B = " + sum );
                }
                catch ( NumberFormatException e ) {
                    // this should never happen, since we checked validity above
                    e.printStackTrace();
                }
            }
            else {
                String message = 
                    "One or more fields are invalid.\n\n" +
                    "The invalid fields are RED.\n" +
                    "Click the error icon to the right of a field for details.";
                JOptionPane.showMessageDialog( AddButton.this, message, "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestValidation();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
