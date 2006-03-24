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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;


/**
 * QTPathRenderer draws a series as a GeneralPath.
 * We assume that the domain axis is horizontal, and the range axis is vertical.
 * For performance optimization, the entire path is constructed and drawn in one shot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTPathRenderer extends AbstractXYItemRenderer {

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
    public QTPathRenderer() {
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

        // D nothing if item is not visible.
        if ( !getItemVisible( series, item ) ) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        // Build the complete path...
        _path.reset();
        final int numberOfItems = dataset.getItemCount( series );
        for ( int i = 0; i < numberOfItems; i++ ) {
            // Get the data point's x & y values...
            final double x = dataset.getXValue( series, i );
            final double y = dataset.getYValue( series, i );
            // Convert to screen coordinates...
            final double tx = domainAxis.valueToJava2D( x, dataArea, xAxisLocation );
            final double ty = rangeAxis.valueToJava2D( y, dataArea, yAxisLocation );
            // Add to the path...
            if ( i == 0 ) {
                _path.moveTo( (float) tx, (float) ty );
            }
            else {
                _path.lineTo( (float) tx, (float) ty );
            }
        }

        // Draw the path...
        g2.setStroke( getSeriesStroke( series ) );
        g2.setPaint( getSeriesPaint( series ) );
        g2.draw( _path );
    }
}
