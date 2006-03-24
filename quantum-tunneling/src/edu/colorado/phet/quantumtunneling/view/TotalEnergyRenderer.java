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
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.WavePacket;


/**
 * TotalEnergyRenderer renders the total energy of a wave packet as a "band" of probabilites.
 * <p>
 * For total energy E, the band will be brightest at E=E0, and decrease linearly in brightness
 * to E=minE below and E=maxE above.  minE and maxE are given by:
 * <p>
 * <code>
 * minE = E0 - ((2*hbar/w) * sqrt(2*(E0-V0 )/m )) + ((2*hbar*hbar)/(m*w*w))
 * maxE = E0 + ((2*hbar/w) * sqrt(2*(E0-V0 )/m )) + ((2*hbar*hbar)/(m*w*w))
 * </code>
 * where:
 * <code>
 * E0 = average total energy
 * V0 = potential energy at the wave packet's initial center position
 * w = wave packet's initial width
 * m = mass
 * hbar = Planck's constant
 * </code>
 * <p>
 * Exceptions to the above:
 * <ul>
 * <li>If E0 <= V0, then the "band" is replaced with a line.</li>
 * <li>If k0*w <= 2, then E- = V0, where k0=sqrt(2*m*(E0-V0)/(hbar*hbar))
 * </ul>
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private WavePacket _wavePacket;
    private AbstractPotential _potentialEnergy;
    
    private Color _centerColor;
    private Color _edgeColor;
    
    private Line2D _line; // reusable line

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public TotalEnergyRenderer() {
        super();
        _line = new Line2D.Double();
        setColor( QTConstants.COLOR_SCHEME.getTotalEnergyColor() );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color used for total energy.
     * 
     * @param color
     */
    public void setColor( Color color ) {
        _centerColor = color;
        _edgeColor = new Color( _centerColor.getRed(), _centerColor.getGreen(), _centerColor.getBlue(), 5 );
    }
    
    /**
     * Sets the wave packet.
     * The wave packet's initial width and center affect the total energy
     * probability distribution.
     * 
     * @param wavePacket
     */
    public void setWavePacket( WavePacket wavePacket ) {
        _wavePacket = wavePacket;
    }
    
    /**
     * Sets the potential energy.  
     * <p>
     * The wave packet (set via setWavePacket) provides access to its potential
     * energy.  But there are situations (like in the "Configure Energy" dialog)
     * where we want to use a "hypothetical" potential that hasn't been applied
     * to the wave packet model yet.  So we specify the potential energy separately
     * via this method.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        _potentialEnergy = potentialEnergy;
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
    
        // Do all rendering based on the first data point.
        if ( item != 0 ) {
            return;
        }
        
        // Initialized?
        if ( _wavePacket == null || _potentialEnergy == null ) {
            return;
        }
       
        // Visible?
        if ( !getItemVisible( series, item ) ) {
            return;
        }
        
        // Axis (model) coordinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double packetCenter = _wavePacket.getCenter();
        final double E0 = dataset.getYValue( series, item ); // the average total energy
        final double V0 = _potentialEnergy.getEnergyAt( packetCenter );
        
        // Java2D (screen) coorinates
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, domainAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, domainAxisLocation );
        final double averageY = rangeAxis.valueToJava2D( E0, dataArea, rangeAxisLocation );
        
        if ( E0 <= V0 ) {
            // Draw a line.
            // Use a Line2D so that this renderer matches the StandardXYItemRenderer used for plane waves.
            g2.setPaint( getSeriesPaint( series ) );
            g2.setStroke( getSeriesStroke( series ) );
            _line.setLine( minX, averageY, maxX, averageY );
            g2.draw( _line );
        }
        else {
            // Axis (model) coordinates
            final double packetWidth = _wavePacket.getWidth();
            final double k0 = Math.sqrt( ( 2 * MASS * ( E0 - V0 ) ) / ( HBAR * HBAR ) );
            final double term1 = ( 2 * HBAR / packetWidth ) * Math.sqrt( 2 * ( E0 - V0 ) / MASS );
            final double term2 = ( 2 * HBAR * HBAR ) / ( MASS * packetWidth * packetWidth );
            final double maxE = E0 + term1 + term2; // min total energy
            double minE = E0 - term1 + term2; // max total energy
            if ( k0 * packetWidth <= 2 ) {
                minE = V0;
            }

            // Java2D (screen) coorinates
            final double minY = rangeAxis.valueToJava2D( maxE, dataArea, rangeAxisLocation ); // +y is down!
            final double maxY = rangeAxis.valueToJava2D( minE, dataArea, rangeAxisLocation ); // +y is down!
            final double width = Math.max( maxX - minX, 1 );
            final double topHeight = Math.max( averageY - minY, 1 );
            final double bottomHeight = Math.max( maxY - averageY, 1 );
            
            // Draw a band...
            {
                Shape topShape = new Rectangle2D.Double( minX, minY, width, topHeight + 1 );
                Shape bottomShape = new Rectangle2D.Double( minX, averageY, width, bottomHeight );

                // Take care that the gradients aren't zero pixels high! That will crash the JVM.
                Paint topPaint = new GradientPaint( (float) minX, (float) minY, _edgeColor, (float) minX, (float) ( minY + topHeight ), _centerColor );
                Paint bottomPaint = new GradientPaint( (float) minX, (float) averageY, _centerColor, (float) minX, (float) ( averageY + bottomHeight ), _edgeColor );

                g2.setPaint( topPaint );
                g2.fill( topShape );
                g2.setPaint( bottomPaint );
                g2.fill( bottomShape );
            }
        }
    }
}
