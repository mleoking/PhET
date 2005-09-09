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
 * WavePacketDeltaKTool is the tool used for measuring the delta k (or omega)
 * of a Gaussian wave packet in k space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketDeltaKTool extends AbstractWavePacketMeasurementTool {

    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketDeltaKTool( Component component, GaussianWavePacket wavePacket, Chart chart ) {
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
        double dk = getWavePacket().getDeltaK();
        
        // Set the tool's bar width.
        float width = (float) ( 2 * getChart().transformXDouble( dk ) );
        setToolWidth( width );
        
        // Set the tool's label.
        int domain = getDomain();
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            // 2dk
            setLabel( "<html>2\u03C3<sub>k</sub></html>" );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            //2dw
            setLabel( "<html>2\u03C3<sub>\u03C9</sub></html>" );     
        }
    }

}
