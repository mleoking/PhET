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
import edu.colorado.phet.fourier.util.TrigCache;


/**
 * WavePacketXWidthPlot plots the x-space width of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketXWidthPlot extends AbstractWavePacketWidthPlot {
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketXWidthPlot( Component component, Chart chart, GaussianWavePacket wavePacket ) {
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
            // 2 sigma sub-x
            setLabel( "<html>2\u03C3<sub>x</sub></html>" );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            // 2 sigma sub-t
            setLabel( "<html>2\u03C3<sub>t</sub></html>" );     
        }
    }

    /**
     * Gets the x-space width of the wave packet, in model coordinates.
     * 
     * @param the width
     */
    protected double getModelWidth() {
        return 2 * getWavePacket().getDeltaX();
    }
    
    /**
     * Gets the location of the width indicator, in model coordinates.
     * 
     * @return Point2D
     */
    protected Point2D getModelLocation() {
        double x = 0;
        double y = 1 / Math.sqrt( Math.E );
        return new Point2D.Double( x, y );
    }
}
