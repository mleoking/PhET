/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.WavePacket;


/**
 * TotalEnergyRenderer render the total energy of a wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergyRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double HBAR = QTConstants.HBAR;
    private static final double MASS = QTConstants.MASS;
    private static final Color CENTER_COLOR = QTConstants.TOTAL_ENERGY_COLOR;
    private static final Color EDGE_COLOR = new Color( CENTER_COLOR.getRed(), CENTER_COLOR.getGreen(), CENTER_COLOR.getBlue(), 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private WavePacket _wavePacket;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public TotalEnergyRenderer() {
        super();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setWavePacket( WavePacket wavePacket ) {
        _wavePacket = wavePacket;
    }
    
    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws the band that represents the range of possible energy.
     * <p>
     * This band is implemented as 2 rectangles, each with its own
     * GradientPaint.  The rectangles and gradients are arranged such
     * that the darkest color at the average total energy point, and the
     * color fades out above and below.
     */
    public void drawItem( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item, 
            CrosshairState crosshairState, 
            int pass ) {
        
        // Initialized?
        if ( _wavePacket == null  ) {
            return;
        }
        
        // Visible?
        if ( !getItemVisible( series, item ) ) {
            return;
        }
        
        // Do all rendering based on the first data point.
        if ( item != 0 ) {
            return;
        }
        
        // Axis (model) coordinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double packetCenter = _wavePacket.getCenter();
        final double E0 = dataset.getYValue( series, item ); // the average total energy
        final double V0 = _wavePacket.getPotentialEnergy().getEnergyAt( packetCenter );
        
        // Java2D (screen) coorinates
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, domainAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, domainAxisLocation );
        final double averageY = rangeAxis.valueToJava2D( E0, dataArea, rangeAxisLocation );

        if ( E0 <= V0 ) {
            // Draw a line...
            g2.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
            g2.setStroke( QTConstants.TOTAL_ENERGY_STROKE );
            g2.drawLine( (int)minX, (int)averageY, (int)maxX, (int)averageY );
        }
        else {
            // Axis (model) coordinates
            final double packetWidth = _wavePacket.getWidth();
            final double term1 = ( 2 * HBAR / packetWidth ) * Math.sqrt( 2 * ( E0 - V0 ) / MASS );
            final double term2 = ( 2 * HBAR * HBAR ) / ( MASS * packetWidth * packetWidth );
            final double minE = E0 - term1 + term2; // max total energy
            final double maxE = E0 + term1 + term2; // min total energy

            // Java2D (screen) coorinates
            final double minY = rangeAxis.valueToJava2D( maxE, dataArea, rangeAxisLocation ); // +y is down!
            final double maxY = rangeAxis.valueToJava2D( minE, dataArea, rangeAxisLocation ); // +y is down!
            final double width = Math.max( maxX - minX, 1 );
            final double topHeight = Math.max( averageY - minY, 1 );
            final double bottomHeight = Math.max( maxY - averageY, 1 );
            
            // Draw a band...
            {
                Shape topShape = new Rectangle2D.Double( minX, minY, width, topHeight );
                Shape bottomShape = new Rectangle2D.Double( minX, averageY, width, bottomHeight );

                GradientPaint topGradient = new GradientPaint( (float) minX, (float) minY, EDGE_COLOR, (float) minX, (float) averageY, CENTER_COLOR );
                GradientPaint bottomGradient = new GradientPaint( (float) minX, (float) averageY, CENTER_COLOR, (float) minX, (float) maxY, EDGE_COLOR );

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setPaint( topGradient );
//                g2.setPaint( Color.RED );//XXX
                g2.fill( topShape );
                g2.setPaint( bottomGradient );
//                g2.setPaint( Color.GREEN );//XXX
                g2.fill( bottomShape );
            }
        }
    }
}
