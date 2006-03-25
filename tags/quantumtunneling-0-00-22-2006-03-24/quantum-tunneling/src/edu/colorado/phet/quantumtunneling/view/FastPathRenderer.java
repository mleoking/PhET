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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;


/**
 * FastPathRenderer draws a series as a GeneralPath.
 * For performance optimization, the entire path is constructed and drawn in one shot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FastPathRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GeneralPath _path;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public FastPathRenderer() {
        super();
        _path = new GeneralPath();
    }

    //----------------------------------------------------------------------------
    // XYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws the entire dataset when item == 0.
     * Calls with item != 0 do nothing.
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

        // We're drawing the entire path when item==0.
        if ( item != 0 ) {
            return;
        }

        // Do nothing if item is not visible.
        if ( !getItemVisible( series, item ) ) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        // Build the complete path...
        _path.reset();
        final int numberOfItems = dataset.getItemCount( series );
        for ( int i = 0; i < numberOfItems; i++ ) {
            // Get the data item's x & y values...
            final double x = dataset.getXValue( series, i );
            final double y = dataset.getYValue( series, i );
            // Convert to screen coordinates...
            final double tx = domainAxis.valueToJava2D( x, dataArea, xAxisLocation );
            final double ty = rangeAxis.valueToJava2D( y, dataArea, yAxisLocation );
            // Adjust for plot orientation...
            float fx = (float) tx;
            float fy = (float) ty;
            if ( plot.getOrientation() == PlotOrientation.HORIZONTAL ) {
                // The range axis is horizontal...
                fx = (float) ty;
                fy = (float) tx;
            }
            // Add to the path...
            if ( i == 0 ) {
                _path.moveTo( fx, fy );
            }
            else {
                _path.lineTo( fx, fy );
            }
        }

        // Draw the path...
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( getSeriesStroke( series ) );
        g2.setPaint( getSeriesPaint( series ) );
        g2.draw( _path );
    }
}
