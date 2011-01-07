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

import java.awt.Component;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.enums.Domain;
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
        Domain domain = getDomain();
        if ( domain == Domain.SPACE ) {
            if ( k1 == 0 ) {
                // k1=0
                setLabel( "<html>k<sub>1</sub>=0</html>" );
            }
            else {
                // k1
                setLabel( "<html>k<sub>1</sub></html>" );
            }
        }
        else if ( domain == Domain.TIME ) {
            if ( k1 == 0 ) {
                // w1=0
                setLabel( "<html>" + MathStrings.C_OMEGA + "<sub>1</sub>=0</html>" );
            }
            else {
                // w1
                setLabel( "<html>" + MathStrings.C_OMEGA + "<sub>1</sub></html>" );
            }     
        }
    }

}
