// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


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
    public void setDomain( Domain domain ) {
        
        // Set the graphic's label.
        if ( domain == Domain.SPACE ) {
            // 2 sigma sub-x
            setLabel( "<html>2\u03C3<sub>x</sub></html>" );
        }
        else if ( domain == Domain.TIME ) {
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
