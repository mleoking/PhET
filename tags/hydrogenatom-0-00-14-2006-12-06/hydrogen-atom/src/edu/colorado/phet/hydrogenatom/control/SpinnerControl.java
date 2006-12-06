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

/**
 * SpinnerControl combines a JSpinner and two JLabels into a compound component.
 * The labels indicate the purpose of the control and the units of the value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpinnerControl extends HorizontalLayoutPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets DEFAULT_INSETS = new Insets( 3, 3, 3, 3 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JSpinner _spinner;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param value
     * @param min
     * @param max
     * @param stepSize
     * @param labelString
     * @param unitsString
     */
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
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets a reference to the control's JSpinner.
     * @return JSpinner
     */
    public JSpinner getSpinner() {
        return _spinner;
    }
    
    /**
     * Sets whether the spinner's text field is editable.
     * @param editable
     * @return true or false
     */
    public void setEditable( boolean editable ) {
        JFormattedTextField tf = ( (JSpinner.DefaultEditor) _spinner.getEditor() ).getTextField();
        tf.setEditable( false );
    }
    
    /**
     * Gets the spinner's value in integer precision.
     * @return int
     */
    public int getIntValue() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _spinner.getModel();
        return spinnerModel.getNumber().intValue();
    }
    
    /**
     * Gets the spinner's value in double precision.
     * @return double
     */
    public double getDoubleValue() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _spinner.getModel();
        return spinnerModel.getNumber().doubleValue();
    }
}
