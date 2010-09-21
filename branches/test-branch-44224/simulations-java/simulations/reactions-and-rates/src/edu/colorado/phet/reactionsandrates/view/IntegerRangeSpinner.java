/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactionsandrates.view;

import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.reactionsandrates.MRConfig;

/**
 * A JSpinner subclass for integer values that have a min/max range.
 * Has an editable text field, which is validated when Enter is pressed or focus is lost.
 * If the value is not a valid integer between the min and max, beeps and reverts to the previous value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerRangeSpinner extends JSpinner {
    
    private static final boolean SHOW_INVALID_VALUE_DIALOG = true; // default behavior
    private static final int DEFAULT_TEXTFIELD_COLUMNS = 3;
    
    private final JFormattedTextField textField;
    private final int min, max;
    private final boolean showInvalidValueDialog;
    
    public IntegerRangeSpinner( int min, int max ) {
        this( min, max, SHOW_INVALID_VALUE_DIALOG );
    }
    
    public IntegerRangeSpinner( int min, int max, boolean showInvalidValueDialog ) {
        super();
        
        this.min = min;
        this.max = max;
        this.showInvalidValueDialog = showInvalidValueDialog;
        
        // model
        setModel( new SpinnerNumberModel( 0, min, max, 1 ) );
        
        // editor
        NumberEditor editor = new NumberEditor( this );
        setEditor( editor );
        
        // text field, commits when Enter is pressed or focus is lost
        textField = editor.getTextField();
        textField.setColumns( DEFAULT_TEXTFIELD_COLUMNS );
        textField.addKeyListener( new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    commit();
                }
            }
        });
        textField.addFocusListener( new FocusAdapter() {
            
            public void focusLost( FocusEvent e ) {
                commit();
            }
            
            /*
             * Workaround to select contents when textfield get focus.
             * See bug ID 4699955 at bugs.sun.com
             */
            public void focusGained( FocusEvent e ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        textField.selectAll();
                    }
                });
            }
        } );
    }
    
    public void setTextFieldColumns( int columns ) {
        textField.setColumns( columns );
    }
    
    public void setIntValue( int value ) {
        setValue( new Integer( value ) );
    }
    
    public int getIntValue() {
        return ( (Integer) getValue() ).intValue();
    }
    
    /*
     * If we can't commit the value in the text field, then revert.
     */
    private void commit() {
        try {
            //TODO this converts invalid entries like "12abc" to "12", standard JSpinner behavior but not desirable for PhET
            commitEdit();
        }
        catch ( ParseException pe ) {
            Toolkit.getDefaultToolkit().beep();
            textField.setValue( getValue() ); // revert, sync textfield to value
            textField.selectAll();
            if ( showInvalidValueDialog ) {
                showInvalidValueDialog();
            }
        }
    }
    
    private void showInvalidValueDialog() {
        String pattern = MRConfig.RESOURCES.getLocalizedString( "messages.invalidValue" );
        Object[] args = { new Integer( min ), new Integer( max ) };
        String message = MessageFormat.format( pattern, args );
        PhetOptionPane.showErrorDialog( this, message );
    }
}