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
 * WavePacketPeriodTool is the tool used for measuring the period (lamda or T)
 * of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketPeriodTool extends AbstractWavePacketMeasurementTool {

    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketPeriodTool( Component component, GaussianWavePacket wavePacket, Chart chart ) {
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
        double k1 = getWavePacket().getK1();
        double period = 0;
        if ( k1 > 0 ) {
            period = 2 * Math.PI / k1;
        }
        
        // Set the tool's bar width.
        float width = (float) ( getChart().transformXDouble( period ) - getChart().transformXDouble( 0 ) );
        setToolWidth( width );
        
        // Set the tool's label.
        int domain = getDomain();
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            if ( k1 == 0 ) {
                // lamda1=infinity
                setLabel( "<html>" + MathStrings.C_LAMDA + "<sub>1</sub>=" + MathStrings.C_INFINITY + "</html>" );
            }
            else {
                // lamda1
                setLabel( "<html>" + MathStrings.C_LAMDA + "<sub>1</sub></html>" );
            }
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            if ( k1 == 0 ) {
                // T1=infinity
                setLabel( "<html>" + MathStrings.C_PERIOD + "<sub>1</sub>=" + MathStrings.C_INFINITY + "</html>" );
            }
            else {
                // T1
                setLabel( "<html>" + MathStrings.C_PERIOD + "<sub>1</sub></html>" );
            }     
        }
    }

}
