/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.JLabel;


/**
 * BSMassMultiplierSlider is the slider used to see the mass multiplier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSMassMultiplierSlider extends SliderControl {

    /**
     * Constructor.
     * 
     * @param value
     * @param min
     * @param max
     * @param tickSpacing
     * @param tickDecimalPlaces
     * @param valueDecimalPlaces
     * @param label
     * @param units
     * @param columns
     * @param insets
     */
    public BSMassMultiplierSlider( double value, double min, double max, 
            double tickSpacing, int tickDecimalPlaces, int valueDecimalPlaces, 
            String label, String units, int columns, Insets insets ) 
    {
        super( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns, insets );
        
        setTextEditable( true );
        
        // Put symbolic labels at min and max.
        {
            // Determine the integer equivalents of the min/max range of the underlying JSlider.
            final int minInt = getSlider().getMinimum();
            final int maxInt = getSlider().getMaximum();

            // Construct labels for the min and max...
            String formatString = "0.";
            for ( int i = 0; i < tickDecimalPlaces; i++ ) {
                formatString += "0";
            }
            DecimalFormat format = new DecimalFormat( formatString );
            String minString = format.format( min );
            String maxString = format.format( max );

            // Create the label table...
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Integer( minInt ), new JLabel( "<html>" + minString + "m<sub>e</sub></html>" ) );
            labelTable.put( new Integer( maxInt ), new JLabel( "<html>" + maxString + "m<sub>e</sub></html>" ) );
            getSlider().setLabelTable( labelTable );
        }
    }
}
