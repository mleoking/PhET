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
 * XWidthSlider is a slider for controling the 
 * Gaussian wave packet width in x space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeltaXSlider extends AbstractFourierSlider {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String VALUE_FORMAT = "#.###";
    
    // dx = 1/dk
    private static final double MIN_WIDTH = 1 / DeltaKSlider.MAX_WIDTH;
    private static final double MAX_WIDTH = 1 / DeltaKSlider.MIN_WIDTH;
    
    /*
     * The width values are doubles, but JSlider deals with integers.
     * This MULTIPLIER is used to convert the doubles to integers.
     * It should be a power of 10.  Larger values provide better precision.
     */
    private static final int MULTIPLIER = 1000;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DecimalFormat _xWidthFormatter;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public DeltaXSlider() {
        super( SimStrings.get( "DeltaXSlider.format.space" ) );
        
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
        labelTable.put( new Integer( (int)( MAX_WIDTH * MULTIPLIER ) ), new JLabel( ".08" ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + .8 ) * MULTIPLIER ) ), new JLabel( ".2" ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + .6 ) * MULTIPLIER ) ), new JLabel( ".4" ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + .4 ) * MULTIPLIER ) ), new JLabel( ".6" ) );
        labelTable.put( new Integer( (int)( ( MIN_WIDTH + .2 ) * MULTIPLIER ) ), new JLabel( ".8" ) );
        labelTable.put( new Integer( (int)( MIN_WIDTH * MULTIPLIER ) ), new JLabel( "1" ) );
        getSlider().setLabelTable( labelTable );
        getSlider().setPaintLabels( true );
        
        getSlider().setMajorTickSpacing( (int) ( (MAX_WIDTH - MIN_WIDTH) * MULTIPLIER ) );
        getSlider().setMinorTickSpacing( (int) ( .2 * MULTIPLIER ) );
        getSlider().setPaintTicks( true );
    }
    
    //----------------------------------------------------------------------------
    // AbstractFourierSlider implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param xWidth the x-space width
     */
    public void setValue( double xWidth ) {
        // Careful - remember that the slider is inverted!
        int sliderValue = (int) ( ((MAX_WIDTH + MIN_WIDTH) * MULTIPLIER ) - ( xWidth * MULTIPLIER ) );
        getSlider().setValue( sliderValue );
    }
    
    /**
     * Gets the value.
     * 
     * @return the x-space width
     */
    public double getValue() {
        int sliderValue = getSlider().getValue();
        // Careful - remember that the slider is inverted!
        double xWidth = ( ( (MAX_WIDTH + MIN_WIDTH) * MULTIPLIER ) - sliderValue ) / MULTIPLIER;
        if ( xWidth > MAX_WIDTH ) {
            xWidth = MAX_WIDTH; // adjust for rounding error
        }
        return xWidth;
    }
    
    /*
     * Updates the label when the slider is changed.
     */
    protected void updateLabel() {
        String format = getFormat();
        double _xWidth = getValue();
        if ( _xWidthFormatter == null ) {
            _xWidthFormatter = new DecimalFormat( VALUE_FORMAT );
        }
        String xWidthString = _xWidthFormatter.format( _xWidth );
        Object[] args = { xWidthString };
        String text = MessageFormat.format( format, args );
        getLabel().setText( text );
    }
}
