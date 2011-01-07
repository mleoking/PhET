// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.MathStrings;


/**
 * WavePacketSpacingSlider is a slider for controlling the
 * spacing (k1) of harmonics in a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketSpacingSlider extends AbstractFourierSlider {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String VALUE_FORMAT = "#.##";
    private static final double[] VALUES = { 0, Math.PI/4, Math.PI/2, Math.PI, 2 * Math.PI };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DecimalFormat _spacingFormatter;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public WavePacketSpacingSlider() {
        super( FourierResources.getString( "WavePacketSpacingSlider.format.space" ) );
        
        // Min/max are the indicies of the VALUES array.
        getSlider().setMinimum( 0 );
        getSlider().setMaximum( 4 );
        getSlider().setValue( 0 );
        
        // Put a label at each tick mark.
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( "0" ) );
        labelTable.put( new Integer( 1 ), new JLabel( MathStrings.C_PI + "/4" ) );
        labelTable.put( new Integer( 2 ), new JLabel( MathStrings.C_PI + "/2" ) );
        labelTable.put( new Integer( 3 ), new JLabel( "" + MathStrings.C_PI ) );
        labelTable.put( new Integer( 4 ), new JLabel( "2" + MathStrings.C_PI ) );
        getSlider().setLabelTable( labelTable );
        getSlider().setPaintLabels( true );
        
        getSlider().setMajorTickSpacing( 1 );
        getSlider().setSnapToTicks( true );
        getSlider().setPaintTicks( true );
    }
    
    //----------------------------------------------------------------------------
    // AbstractFourierSlider implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param k1Spacing the k1 spacing
     * @throws IllegalArgumentException if the value of k1Spacing is illegal
     */
    public void setValue( double k1Spacing ) {
        boolean isValid = false;
        for ( int i = 0; i < VALUES.length; i++ ) {
            if ( k1Spacing == VALUES[i] ) {
                getSlider().setValue( i );
                isValid = true;
                break;
            }
        }
        if ( ! isValid ) {
            throw new IllegalArgumentException( "illegal k1 spacing value: " + k1Spacing );
        }
    }
    
    /**
     * Gets the value.
     * 
     * @return the k1 spacing
     */
    public double getValue() {
        int sliderValue = getSlider().getValue();
        return VALUES[ sliderValue ];
    }
    
    /*
     * Updates the label when the slider is changed.
     */
    protected void updateLabel() {
        String format = getFormat();
        double spacing = getValue();
        if ( _spacingFormatter == null ) {
            _spacingFormatter = new DecimalFormat( VALUE_FORMAT );
        }
        String spacingString = _spacingFormatter.format( spacing );
        Object[] args = { spacingString };
        String text = MessageFormat.format( format, args );
        getLabel().setText( text );
    }
}
