/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;


/**
 * SpinnerControl
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DoubleSpinnerControl extends JPanel implements FocusListener {
    
    private JSpinner _spinner;
    private JFormattedTextField _textField;
    
    public DoubleSpinnerControl( String label, double value, double min, double max, double step, String format, Dimension size ) {
        super( );
        
        // Label
        JLabel spinnerLabel = new JLabel( label );
        
        // Spinner 
        {
            _spinner = new JSpinner();
            // model
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            _spinner.setModel( model );
            // editor
            NumberEditor numberEditor = new NumberEditor( _spinner, format );
            _spinner.setEditor( numberEditor );
            _textField = numberEditor.getTextField();
            _textField.addFocusListener( this );
            // size
            _spinner.setPreferredSize( size );
            _spinner.setMinimumSize( size );
        }

        // Layout
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            layout.addAnchoredComponent( spinnerLabel, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _spinner, 0, 1, GridBagConstraints.WEST );
        }
    }

    public double getValue() {
        return ((Double) _spinner.getValue()).doubleValue();
    }
    
    public void setValue( double value ) {
        _spinner.setValue( new Double( value ) );
    }
    
    public void addChangeListener( ChangeListener l ) {
        _spinner.addChangeListener( l );
    }
    
    public void removeChangeListener( ChangeListener l ) {
        _spinner.removeChangeListener( l );
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
