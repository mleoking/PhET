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

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * WavePacketKWidthTool is the tool used for measuring the width
 * of a Gaussian wave packet in k space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketKWidthTool extends AbstractWavePacketMeasurementTool {

    private Point2D _origin;
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     */
    public WavePacketKWidthTool( Component component, GaussianWavePacket wavePacket, Chart chart, Point2D origin ) {
        super( component, wavePacket, chart );
        _origin = new Point2D.Double( origin.getX(), origin.getY() );
        setFillColor( Color.RED );
        setLook( MeasurementTool.LOOK_ARROWS );
        setDragEnabled( false );
        updateTool();
    }

    //----------------------------------------------------------------------------
    // AbstractWavePacketMeasurementTool implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the tool to match the current settings of the domain,
     * wave packet, and chart.
     */
    public void updateTool() {
        
        // The value that we're measuring.
        double dk = getWavePacket().getDeltaK();
        
        // Set the tool's bar width.
        float width = (float) ( 2 * getChart().transformXDouble( dk ) );
        setToolWidth( width );
        
        // Set the tool's label.
        int domain = getDomain();
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            // 2 sigma sub-k
            setLabel( "<html>2\u03C3<sub>k</sub></html>" );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            // 2 sigma sub-omega
            setLabel( "<html>2\u03C3<sub>\u03C9</sub></html>" );     
        }
        
        // Set the tool's location, relative to the origin.
        double k0 = getWavePacket().getK0();
        double k1 = getWavePacket().getK1();
        double modelX = k0 - ( 12 * Math.PI );
        double modelY = GaussianWavePacket.getAmplitude( k0 + dk, k0, dk );
        if ( k1 != 0 ) {
            modelY = k1 * modelY;
        }
        double viewX = getChart().transformXDouble( modelX );
        double viewY = getChart().transformYDouble( modelY ) - getChart().transformYDouble( 0 );
        setLocation( (int)( _origin.getX() + viewX ), (int) ( _origin.getY() + viewY ) );
    }

}
