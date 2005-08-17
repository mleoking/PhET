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
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * WavePacketSpacingTool is the tool used for measuring the spacing (k1)
 * of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketSpacingTool extends AbstractWavePacketMeasurementTool {

    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketSpacingTool( Component component, GaussianWavePacket wavePacket, Chart chart ) {
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
        
        // Set the tool's bar width.
        float width = (float) getChart().transformXDouble( k1 );
        setToolWidth( width );
        
        // Set the tool's label.
        int domain = getDomain();
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            if ( k1 == 0 ) {
                setLabel( "<html>k<sub>1</sub>=0</html>" );
            }
            else {
                setLabel( "<html>k<sub>1</sub></html>" );
            }
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            if ( k1 == 0 ) {
                setLabel( "<html>\u03C9<sub>1</sub>=0</html>" );
            }
            else {
                setLabel( "<html>\u03C9<sub>1</sub></html>" );
            }     
        }
    }

}
