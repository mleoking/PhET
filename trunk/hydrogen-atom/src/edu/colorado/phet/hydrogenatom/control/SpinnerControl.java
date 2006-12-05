/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Insets;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;


public class SpinnerControl extends HorizontalLayoutPanel {

    private static final Insets DEFAULT_INSETS = new Insets( 3, 3, 3, 3 );
    
    private JSpinner _spinner;
    
    public SpinnerControl( double value, double min, double max, double stepSize, String labelString, String unitsString ) {
        super();
        
        JLabel label = new JLabel( labelString );
        JLabel units = new JLabel( unitsString );
        
        SpinnerModel model = new SpinnerNumberModel( value, min, max, stepSize );
        _spinner = new JSpinner( model );
        
        setInsets( DEFAULT_INSETS );
        add( label );
        add( _spinner );
        add( units );
    }
    
    public JSpinner getSpinner() {
        return _spinner;
    }
    
    public void setEditable( boolean editable ) {
        JFormattedTextField tf = ( (JSpinner.DefaultEditor) _spinner.getEditor() ).getTextField();
        tf.setEditable( false );
    }
    
    public int getIntValue() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _spinner.getModel();
        return spinnerModel.getNumber().intValue();
    }
    
    public double getDoubleValue() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _spinner.getModel();
        return spinnerModel.getNumber().doubleValue();
    }
}
