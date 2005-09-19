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

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current settings of the domain & wave packet.
     */
    public void update() {
  
        // Set the graphic's width in model coordinates.
        double dx = getWavePacket().getDeltaX();
        setGraphicWidth( 2 * dx );
        
        // Set the graphic's location in model coordinates.
        double x = 0;
        double y = 1 / Math.sqrt( Math.E );
        getDataSet().clear();
        getDataSet().addPoint( x, y );
    }

}
