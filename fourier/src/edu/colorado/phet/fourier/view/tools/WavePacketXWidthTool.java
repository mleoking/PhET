/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.tools;

import java.awt.Component;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * WavePacketXWidthTool is the tool used for measuring the width
 * of a Gaussian wave packet in x space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketXWidthTool extends AbstractWavePacketMeasurementTool {

    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketXWidthTool( Component component, GaussianWavePacket wavePacket, Chart chart ) {
        super( component, wavePacket, chart );
    }

    //----------------------------------------------------------------------------
    // AbstractWavePacketMeasurementTool implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the tool to match the current settings of the domain,
     * wave packet, and chart.
     */
    public void updateTool() {
        
        // The current value that we're measuring.
        double dx = getWavePacket().getDeltaX();
        
        // Set the tool's bar width.
        float width = (float) ( 2 * ( getChart().transformXDouble( dx ) - getChart().transformXDouble( 0 ) ) );
        setToolWidth( width );
        
        // Set the tool's label.
        int domain = getDomain();
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            // 2 sigma sub-x
            setLabel( "<html>2\u03C3<sub>x</sub></html>" );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            // 2 sigma sub-t
            setLabel( "<html>2\u03C3<sub>t</sub></html>" );     
        }
    }

}
