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

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.view.tools.MeasurementTool;


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
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current settings of the wave packet.
     */
    public void update() {
        
        // Set the graphic's width, in model coordinates.
        double dk = getWavePacket().getDeltaK();
        setGraphicWidth( 2 * dk );
        
        // Set the graphic's location in model coordinates.
        double k0 = getWavePacket().getK0();
        double k1 = getWavePacket().getK1();
        double x = k0;
        double y = GaussianWavePacket.getAmplitude( k0 + dk, k0, dk );
        if ( k1 != 0 ) {
            y = k1 * y;
        }
        getDataSet().clear();
        getDataSet().addPoint( x, y );
    }
}
