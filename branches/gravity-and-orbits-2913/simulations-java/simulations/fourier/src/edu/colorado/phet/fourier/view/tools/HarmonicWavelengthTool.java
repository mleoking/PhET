// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * HarmonicWavelengthTool is the tool used to measure a harmonic's wavelength.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicWavelengthTool extends AbstractHarmonicMeasurementTool {

    private static final char SYMBOL = MathStrings.C_WAVELENGTH;
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param harmonic
     * @param chart
     */
    public HarmonicWavelengthTool( Component component, Harmonic harmonic, Chart chart ) {
        super( component, harmonic, chart );
    }   
    
    //----------------------------------------------------------------------------
    // AbstractHarmonicMeasurementTool implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the tool.
     */
    protected void updateTool() {

        Harmonic harmonic = getHarmonic();
        Chart chart = getChart();
        
        // The label
        String subscript = String.valueOf( harmonic.getOrder() + 1 );
        setLabel( "<html>" + SYMBOL + "<sub>" + subscript + "</sub></html>" );
        
        // The bar color
        Color color = HarmonicColors.getInstance().getColor( harmonic );
        setFillColor( color );
        
        // The bar size
        {
            // The harmonic's cycle length, in model coordinates.
            double cycleLength = FourierConstants.L / ( harmonic.getOrder() + 1 );

            // Convert the cycle length to view coordinates.
            Point2D p1 = chart.transformDouble( 0, 0 );
            Point2D p2 = chart.transformDouble( cycleLength, 0 );
            float width = (float) ( p2.getX() - p1.getX() );

            // Adjust the tool to match the cycle length.
            setToolWidth( width );
        }
    }
}
