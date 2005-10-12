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

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.JLabel;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.MathStrings;


/**
 * WavePacketCenterSlider is a slider for controlling the
 * center point (k0) of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketCenterSlider extends AbstractFourierSlider {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String VALUE_FORMAT = "#.#";
    private static final int MULTIPLIER = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DecimalFormat _formatter;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public WavePacketCenterSlider() {
        super( SimStrings.get( "WavePacketCenterSlider.format.space" ) );
        
        // Range is 900 to 1500, which will be mapped to 9pi to 15pi.
        getSlider().setMinimum( 9 * MULTIPLIER );
        getSlider().setMaximum( 15 * MULTIPLIER );
        getSlider().setValue( 12 * MULTIPLIER );
        
        // Put a label at 9pi, 12pi and 15pi
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer(  9 * MULTIPLIER ), new JLabel(  "9" + MathStrings.C_PI ) );
        labelTable.put( new Integer( 12 * MULTIPLIER ), new JLabel( "12" + MathStrings.C_PI ) );
        labelTable.put( new Integer( 15 * MULTIPLIER ), new JLabel( "15" + MathStrings.C_PI ) );
        getSlider().setLabelTable( labelTable );
        getSlider().setPaintLabels( true );
        
        // Major ticks at intervals of 3pi
        getSlider().setMajorTickSpacing( 3 * MULTIPLIER );
        // Minor ticks at intervals of pi
        getSlider().setMinorTickSpacing( 1 * MULTIPLIER );
        
        getSlider().setSnapToTicks( false );
        getSlider().setPaintTicks( true );
    }
    
    //----------------------------------------------------------------------------
    // AbstractFourierSlider implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param k0 the center point, k0
     * @throws IllegalArgumentException if the value of k0 is illegal
     */
    public void setValue( double k0 ) {
        if ( k0 > 15 * Math.PI || k0 < 9 * Math.PI ) {
            throw new IllegalArgumentException( "illegal k0 value: " + k0 );
        }
        int value = (int) ( k0 * MULTIPLIER / Math.PI );
        getSlider().setValue( value );
    }
    
    /**
     * Gets the value.
     * 
     * @return the k1 spacing
     */
    public double getValue() {
        int sliderValue = getSlider().getValue();
        return sliderValue * Math.PI / MULTIPLIER;
    }
    
    /*
     * Updates the label when the slider is changed.
     */
    protected void updateLabel() {
        String format = getFormat();
        double value = getValue();
        if ( _formatter == null ) {
            _formatter = new DecimalFormat( VALUE_FORMAT );
        }
        String valueString = _formatter.format( value );
        Object[] args = { valueString };
        String text = MessageFormat.format( format, args );
        getLabel().setText( text );
    }
}
