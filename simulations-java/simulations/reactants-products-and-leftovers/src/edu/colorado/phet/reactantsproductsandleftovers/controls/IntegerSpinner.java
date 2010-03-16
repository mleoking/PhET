/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.controls;

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

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;

/**
 * JSpinner that uses integer values.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerSpinner extends JSpinner {
    
    private final IntegerRange range;
    private final JFormattedTextField textField;
    
    public IntegerSpinner( IntegerRange range ) {
        super();
        
        this.range = range;

        // number model
        setModel( new SpinnerNumberModel( range.getDefault(), range.getMin(), range.getMax(), 1 ) );
        
        // editor
        NumberEditor editor = new NumberEditor( this );
        setEditor( editor );
        
        // text field, commits when Enter is pressed or focus is lost
        textField = editor.getTextField();
        textField.setColumns( String.valueOf( range.getMax() ).length() );
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
            textField.setValue( getValue() ); // revert, sync textfield to value
            Toolkit.getDefaultToolkit().beep();
            showInvalidValueDialog();
            textField.selectAll();
        }
    }
    
    private void showInvalidValueDialog() {
        Object[] args = { new Integer( range.getMin() ), new Integer( range.getMax() ) };
        String message = MessageFormat.format( RPALStrings.MESSAGE_VALUE_OUT_OF_RANGE, args );
        PhetOptionPane.showErrorDialog( this, message );
    }
}
