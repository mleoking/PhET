/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;


/**
 * DoubleSpinner is a spinner that contains a double precision value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DoubleSpinner extends JSpinner implements FocusListener {

    private JFormattedTextField _textField;

    public DoubleSpinner( double value, double min, double max, double step, String format, Dimension size ) {
        super();

        // Spinner 
        {
            // model
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            setModel( model );
            // editor
            NumberEditor numberEditor = new NumberEditor( this, format );
            setEditor( numberEditor );
            _textField = numberEditor.getTextField();
            _textField.addFocusListener( this );
            // size
            setPreferredSize( size );
            setMinimumSize( size );
        }
    }

    public double getDoubleValue() {
        return ( (Double) getValue() ).doubleValue();
    }

    public void setDoubleValue( double value ) {
        setValue( new Double( value ) );
    }

    public JFormattedTextField getFormattedTextField() {
        return _textField;
    }

    /*
     * When the spinner gains focus, select its contents.
     * <p>
     * NOTE: 
     * This currently does not work; see Bug ID 4699955 at bugs.sun.com
     * A workaround is to call selectAll in invokeLater.
     */
    public void focusGained( FocusEvent e ) {
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                _textField.selectAll();
            }
        } );
    }

    public void focusLost( FocusEvent e ) {}
}