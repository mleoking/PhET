/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control.sliders;

import java.text.MessageFormat;



/**
 * DefaultFourierSlider
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DefaultFourierSlider extends AbstractFourierSlider {

    /**
     * Sole constructor.
     * 
     * @param format
     */
    public DefaultFourierSlider( String format ) {
        super( format );
    }

    /**
     * Sets the slider's value.
     * The default implementation assumes that the slider uses 
     * the same values that are made visibile to the user.
     * 
     * @param value
     */
    public void setValue( double value ) {
        getSlider().setValue( (int)value );
        updateLabel();
    }
    
    /**
     * Gets the slider's value.
     * 
     * @return the value
     */
    public double getValue() {
        return getSlider().getValue();
    }
    
    /*
     * Updates the label when the slider value changes.
     */
    protected void updateLabel() {
        int value = getSlider().getValue();
        Object[] args = { new Integer( value ) };
        String text = MessageFormat.format( getFormat(), args );
        getLabel().setText( text );
    }
}
