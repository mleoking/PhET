/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.Component;
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * WavePacketKWidthPlot plots the k-space width of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketKWidthPlot extends AbstractWavePacketWidthPlot {
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketKWidthPlot( Component component, Chart chart, GaussianWavePacket wavePacket ) {
        super( component, chart, wavePacket );
    }
    
    //----------------------------------------------------------------------------
    // AbstractWaveWidthPlot implementation
    //----------------------------------------------------------------------------

    /**
     * Changes the label to match the domain.
     * 
     * @param domain
     */
    public void setDomain( int domain ) {
        assert( FourierConstants.isValidDomain( domain ) );
        
        // Set the graphic's label.
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            // 2 sigma sub-k
            setLabel( "<html>2\u03C3<sub>k</sub></html>" );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            // 2 sigma sub-omega
            setLabel( "<html>2\u03C3<sub>\u03C9</sub></html>" );
        }
    }
    
    /**
     * Gets the k-space width of the wave packet, in model coordinates.
     * 
     * @param the width
     */
    protected double getModelWidth() {
        return 2 * getWavePacket().getDeltaK();
    }
    
    /**
     * Gets the location of the width indicator, in model coordinates.
     * 
     * @return Point2D
     */
    protected Point2D getModelLocation() {
        double dk = getWavePacket().getDeltaK();
        double k0 = getWavePacket().getK0();
        double k1 = getWavePacket().getK1();
        double x = k0;
        double y = GaussianWavePacket.getAmplitude( k0 + dk, k0, dk );
        if ( k1 != 0 ) {
            y = k1 * y;
        }
        return new Point2D.Double( x, y );
    }
}
