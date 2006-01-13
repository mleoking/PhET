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
 * PathRenderer renders an XYDataset as a path.
 * The data points are used to create a GeneralPath, which is 
 * then drawn using the renderer's stroke and paint.
 * The path may be open or closed, as specified in the constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PathRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _closed; // is the path closed?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor, with an open path.
     */
    public PathRenderer() {
        this( false );
    }
    
    /**
     * Constructor, allows you to specify whether the path is closed.
     * 
     * @param closed true or false
     */
    public PathRenderer( boolean closed ) {
        super();
        _closed = closed;
    }
    
    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws the path.
     * The entire path is drawn when a request is made to draw the 0th item.
     * All other item requests are ignored.
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
        
        if ( item == 0 ) {

            if ( !isVisible( series, item ) ) {
                return;
            }
            
            // Create the path...
            GeneralPath path = new GeneralPath();
            double x = dataset.getXValue( series, item );
            double y = dataset.getYValue( series, item );
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            double tx = domainAxis.valueToJava2D( x, dataArea, xAxisLocation );
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double ty = rangeAxis.valueToJava2D( y, dataArea, yAxisLocation );
            path.moveTo( (float)tx, (float)ty );
            final int numberOfItems = dataset.getItemCount( series );
            for ( int i = 1; i < numberOfItems; i++ ) {
                x = dataset.getXValue( series, i );
                tx = domainAxis.valueToJava2D( x, dataArea, xAxisLocation );
                y = dataset.getYValue( series, i );
                ty = rangeAxis.valueToJava2D( y, dataArea, yAxisLocation );
                path.lineTo( (float)tx, (float)ty );
            }
            if ( _closed ) {
                path.closePath();
            }
            
            // Stroke the path...
            g2.setStroke( getSeriesStroke( series ) );
            g2.setPaint( getSeriesPaint( series ) );
            g2.draw( path );
        }
    }
    
    //----------------------------------------------------------------------------
    // Misc.
    //----------------------------------------------------------------------------
    
    /*
     * Determines if an item is visible.
     * Note that there are many levels of visibility, and JFreeChart's 
     * renderers are very inconsistent in their support of visibility.
     * 
     * @param series
     * @param item
     */
    private boolean isVisible( int series, int item ) {
        return ( getSeriesVisible().booleanValue() && isSeriesVisible( series ) && getItemVisible( series, item ) );
    }
}
