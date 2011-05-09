// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;

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
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    commitEdit();
                }
            }
        } );
        textField.addFocusListener( new FocusAdapter() {

            public void focusLost( FocusEvent e ) {
                commitEdit();
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
                } );
            }
        } );
    }

    public void setIntValue( int value ) {
        setValue( new Integer( value ) );
    }

    public int getIntValue() {
        return ( (Integer) getValue() ).intValue();
    }

    @Override
    public void commitEdit() {
        try {
            //TODO this converts invalid entries like "12abc" to "12", standard JSpinner behavior but not desirable for PhET
            super.commitEdit();
        }
        catch ( ParseException pe ) {
            handleInvalidValueDelayed();
        }
    }

    // Workaround for #2218.
    private void handleInvalidValueDelayed() {
        Timer t = new Timer( 500, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleInvalidValue();
            }
        } );
        t.setRepeats( false );
        t.start();
    }

    private void handleInvalidValue() {
        textField.setValue( getValue() ); // revert, sync textfield to value
        Toolkit.getDefaultToolkit().beep();
        showInvalidValueDialog();
    }

    private void showInvalidValueDialog() {
        Object[] args = { new Integer( range.getMin() ), new Integer( range.getMax() ) };
        String message = MessageFormat.format( PhetCommonResources.getString( "message.valueOutOfRange" ), args );
        PhetOptionPane.showErrorDialog( this, message );
    }
}
