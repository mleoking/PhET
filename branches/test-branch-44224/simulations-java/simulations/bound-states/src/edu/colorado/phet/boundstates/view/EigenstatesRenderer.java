/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

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
 * EigenstatesRenderer renders an eigenstate.
 * Each point in the dataset corresponds to an eigenstate energy.
 * The position (x-axis) coordinate in the dataset is ignored,
 * and the eigenstate is rendered as a horizontal line that 
 * extends to the extremes of the position range.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EigenstatesRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GeneralPath _path; // reusable path
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public EigenstatesRenderer() {
        super();
        _path = new GeneralPath();
    }

    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws a horizontal line at each energy value.
     */
    public void drawItem( Graphics2D g2, 
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
 
        if ( !getItemVisible( series, item ) ) {
            return;
        }
        
        // Model coordinates to draw a horizontal line at the specified energy...
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double energy = dataset.getYValue( series, item );
        
        // Translate the points to screen coordinates...
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, xAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, xAxisLocation );
        final double y = rangeAxis.valueToJava2D( energy, dataArea, yAxisLocation );
        
        // Draw the line...
        g2.setPaint( getSeriesPaint( series ) );
        g2.setStroke( getSeriesStroke( series ) );
        _path.reset();
        _path.moveTo( (float)minX, (float)y );
        _path.lineTo( (float)maxX, (float)y );
        g2.draw( _path );
    }
}
