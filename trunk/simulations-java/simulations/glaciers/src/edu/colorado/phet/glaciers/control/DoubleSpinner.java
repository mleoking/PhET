/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.*;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeListener;


/**
 * DoubleSpinner is a spinner control that contains a double precision value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DoubleSpinner extends JPanel {
    
    private final JSpinner _spinner;
    
    public DoubleSpinner( double value, double min, double max, double step, String format, Dimension size ) {
        super();

        // Spinner 
        _spinner = new JSpinner();
        add( _spinner );
        
        // model
        SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
        _spinner.setModel( model );
        
        // editor
        NumberEditor numberEditor = new NumberEditor( _spinner, format );
        _spinner.setEditor( numberEditor );
        final JFormattedTextField textField = numberEditor.getTextField();
        textField.addFocusListener( new FocusAdapter() {
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
                        textField.selectAll();
                    }
                });
            }
        });
        
        // size
        _spinner.setPreferredSize( size );
        _spinner.setMinimumSize( size );
    }

    public double getValue() {
        return ((Double) _spinner.getValue()).doubleValue();
    }
    
    public void setValue( double value ) {
        _spinner.setValue( new Double( value ) );
    }
    
    public void addChangeListener( ChangeListener listener ) {
        _spinner.addChangeListener( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        _spinner.removeChangeListener( listener );
    }
}
