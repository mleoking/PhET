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
 * KWidthSlider is a slider for controling the 
 * Gaussian wave packet width in k space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeltaKSlider extends AbstractFourierSlider {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String VALUE_FORMAT = "#.##";

    // k-space width is from 1 to 4pi
    public static final double MIN_WIDTH = 1;
    public static final double MAX_WIDTH = 4 * Math.PI;
    
    /*
     * The width values are doubles, but JSlider deals with integers.
     * This MULTIPLIER is used to convert the doubles to integers.
     * It should be a power of 10.  Larger values provide better precision.
     */
    private static final int MULTIPLIER = 1000;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DecimalFormat _widthFormatter;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public DeltaKSlider() {
        super( SimStrings.get( "DeltaKSlider.format.space" ) );
        
        getSlider().setMinimum( (int)( MIN_WIDTH * MULTIPLIER) );
        getSlider().setMaximum( (int)( MAX_WIDTH * MULTIPLIER ) );

        /*
         * The distance between the lowest two ticks is (1 and PI) is 
         * smaller than the distance between all other ticks.  In order
         * to pull this off with a JSlider, we need to invert the slider
         * and make use of both major and minor tick spacing.
         */
        getSlider().setInverted( true );
        
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( (int)( MAX_WIDTH * MULTIPLIER ) ), new JLabel( "1" ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + (3 * Math.PI) ) * MULTIPLIER ) ), new JLabel( "" + MathStrings.C_PI ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + (2 * Math.PI) ) * MULTIPLIER ) ), new JLabel( "2" + MathStrings.C_PI ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + (1 * Math.PI) ) * MULTIPLIER ) ), new JLabel( "3" + MathStrings.C_PI ) );
        labelTable.put( new Integer( (int)( MIN_WIDTH * MULTIPLIER ) ), new JLabel( "4" + MathStrings.C_PI ) );
        getSlider().setLabelTable( labelTable );
        getSlider().setPaintLabels( true );
        
        getSlider().setMajorTickSpacing( (int) ( (MAX_WIDTH - MIN_WIDTH) * MULTIPLIER ) );
        getSlider().setMinorTickSpacing( (int) ( Math.PI * MULTIPLIER ) );
        getSlider().setPaintTicks( true );
    }

    //----------------------------------------------------------------------------
    // AbstractFourierSlider implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param kWidth the k-space width
     */
    public void setValue( double kWidth ) {
        // Careful - remember that the slider is inverted!
        int sliderValue = (int) ( ((MAX_WIDTH + MIN_WIDTH) * MULTIPLIER ) - ( kWidth * MULTIPLIER ) );
        getSlider().setValue( sliderValue );
    }
    
    /**
     * Gets the value.
     * 
     * @return the k-space width
     */
    public double getValue() {
        int sliderValue = getSlider().getValue();
        // Careful - remember that the slider is inverted!
        double kWidth = ( ( (MAX_WIDTH + MIN_WIDTH) * MULTIPLIER ) - sliderValue ) / MULTIPLIER;
        return kWidth;
    }
    
    /*
     * Updates the label when the slider is changed.
     */
    protected void updateLabel() {
        String format = getFormat();
        double width = getValue();
        if ( _widthFormatter == null ) {
            _widthFormatter = new DecimalFormat( VALUE_FORMAT );
        }
        String widthString = _widthFormatter.format( width );
        Object[] args = { widthString };
        String text = MessageFormat.format( format, args );
        getLabel().setText( text );
    }
}
