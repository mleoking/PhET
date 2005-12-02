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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;

import edu.colorado.phet.quantumtunneling.piccolo.JFreeChartNode;


/**
 * QTCombinedChartNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTCombinedChartNode extends JFreeChartNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param chart
     */
    public QTCombinedChartNode( QTCombinedChart chart ) {
        super( chart );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the bounds of the energy plot's drawing area.
     * The bounds are in the node's local coordinates.
     * 
     * @return
     */
    public Rectangle2D getEnergyPlotBounds() {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        PlotRenderingInfo energyPlotInfo = plotInfo.getSubplotInfo( QTCombinedChart.ENERGY_PLOT_INDEX );
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = energyPlotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }
    
    //----------------------------------------------------------------------------
    // Transforms
    //----------------------------------------------------------------------------
    
    /**
     * Converts a point in the node's local coordinate system
     * to a point in the energy chart's coordinate system.
     * 
     * @param screenPoint
     * @return
     */
    public Point2D nodeToEnergy( Point2D screenPoint ) {
        
        QTCombinedChart chart = (QTCombinedChart) getChart();

        // Convert the local node coordinates to axis coordinates... 
        EnergyPlot plot = chart.getEnergyPlot();
        Rectangle2D dataArea = getEnergyPlotBounds();
        double x = plot.getDomainAxis().java2DToValue( screenPoint.getX(), dataArea, plot.getDomainAxisEdge() );
        double y = plot.getRangeAxis().java2DToValue( screenPoint.getY(), dataArea, plot.getRangeAxisEdge() );
        Point2D chartPoint = new Point2D.Double( x, y );
        
        return chartPoint;
    }
    
    /**
     * Converts a point in the energy chart's coordinate system
     * to a point in the node's local coordinate system.
     * 
     * @param screenPoint
     * @return
     */
    public Point2D energyToNode( Point2D chartPoint ) {
        
        QTCombinedChart chart = (QTCombinedChart) getChart();
        
        // Convert the axis coordinates to local node coordinates...
        EnergyPlot plot = chart.getEnergyPlot();
        Rectangle2D dataArea = getEnergyPlotBounds();
        double x = plot.getDomainAxis().valueToJava2D( chartPoint.getX(), dataArea, plot.getDomainAxisEdge() );
        double y = plot.getRangeAxis().valueToJava2D( chartPoint.getY(), dataArea, plot.getRangeAxisEdge() );
        Point2D nodePoint = new Point2D.Double( x, y );
        
        return nodePoint;
    }
}
