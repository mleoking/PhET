/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;


public class PositionHistogramChartNode extends JFreeChartNode {

    private PositionHistogramChart _chart;
    
    public PositionHistogramChartNode( PositionHistogramChart chart ) {
        super( chart );
        setPickable( false );
        setChildrenPickable( false );
        
        _chart = chart;
    }
    
    public PositionHistogramChart getPositionHistogramChart() {
        return _chart;
    }
    
    /**
     * Gets the bounds of the associated chart's plot.
     * The bounds are in the node's local coordinates.
     * 
     * @return Rectangle2D
     */
    public Rectangle2D getPlotBounds() {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }
}
