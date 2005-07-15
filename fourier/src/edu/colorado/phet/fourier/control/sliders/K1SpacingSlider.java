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
 * K1SpacingSlider
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class K1SpacingSlider extends AbstractFourierSlider {

    private static final String VALUE_FORMAT = "0.00";
    
    private DecimalFormat _spacingFormatter;
    
    /**
     * Sole constructor.
     */
    public K1SpacingSlider() {
        super( SimStrings.get( "K1SpacingSlider.format" ) );
        
        // WARNING: Don't change the max & min or you'll need to rewrite most of this.
        getSlider().setMinimum( 0 );
        getSlider().setMaximum( 100 );
        
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( "0" ) );
        labelTable.put( new Integer( 20 ), new JLabel( MathStrings.C_PI + "/8" ) );
        labelTable.put( new Integer( 40 ), new JLabel( MathStrings.C_PI + "/4" ) );
        labelTable.put( new Integer( 60 ), new JLabel( MathStrings.C_PI + "/2" ) );
        labelTable.put( new Integer( 80 ), new JLabel( "" + MathStrings.C_PI ) );
        labelTable.put( new Integer( 100 ), new JLabel( "2" + MathStrings.C_PI ) );
        getSlider().setLabelTable( labelTable );
        getSlider().setPaintLabels( true );
        
        getSlider().setMajorTickSpacing( 20 );
        getSlider().setPaintTicks( true );
    }
    
    /*
     * Overrides FourierSlider.
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
    
    public double getValue() {
        int sliderValue = getSlider().getValue();
        double k1Spacing = 0;
        if ( sliderValue > 0 ) {
            /* Serious voodoo here...
             * spacing = 2^^(x-1) where x denotes one of the ticks (values 0-5).
             * 20.0 is the slider's tick spacing.
             * 4 is the max of x (5) minus 1.
             */
            k1Spacing = ( 2 * Math.PI * Math.pow( 2, (sliderValue/20.0)-1 ) ) / Math.pow( 2, 4 );    
        }
        return k1Spacing;
    }
    
    public void setValue( double k1Spacing ) {
        if ( k1Spacing == 0 ) {
            getSlider().setValue( 0 );
        }
        else {
            //XXX implement this
            throw new RuntimeException( "not implemented" );//XXX
        }
    }
}
