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
import javax.swing.JSpinner.NumberEditor;


public class DoubleSpinner extends JSpinner implements FocusListener { 
    
    private JFormattedTextField _textField;
    
    public DoubleSpinner( double value, double min, double max, double step, String format, Dimension size ) {
        super( );
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

    public double getDoubleValue() {
        return ((Double) getValue()).doubleValue();
    }
    
    public void setDoubleValue( double value ) {
        setValue( new Double( value ) );
    }
    
    /*
     * When the spinner gains focus, select its contents.
     * NOTE: This currently does not work; see Bug ID 4699955 at bugs.sun.com
     */
    public void focusGained( FocusEvent e ) {
        _textField.selectAll();
    }

    public void focusLost( FocusEvent e ) {}
}