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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;


/**
 * BSCombinedChartNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCombinedChartNode extends JFreeChartNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param chart
     * @param buffered
     */
    public BSCombinedChartNode( BSCombinedChart chart, boolean buffered ) {
        super( chart, buffered );
        setPickable( false );
        setChildrenPickable( false );
    }
    
    /**
     * Constructs and unbuffered chart node.
     * 
     * @param chart
     */
    public BSCombinedChartNode( BSCombinedChart chart ) {
        this( chart, false /* buffered */ );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the combined chart that this node manages.
     * 
     * @return
     */
    public BSCombinedChart getCombinedChart() {
        return (BSCombinedChart) getChart();
    }
    
    /**
     * Gets the bounds of the "Energy" plot's drawing area.
     * The bounds are in the node's local coordinates.
     * 
     * @return
     */
    public Rectangle2D getEnergyPlotBounds() {
        return getPlotBounds( BSCombinedChart.ENERGY_PLOT_INDEX );
    }
    
    /**
     * Gets the bounds of the bottom plot's drawing area.
     * The bounds are in the node's local coordinates.
     * 
     * @return
     */
    public Rectangle2D getBottomPlotBounds() {
        return getPlotBounds( BSCombinedChart.BOTTOM_PLOT_INDEX );
    }
    
    /**
     * Gets the bounds of a subplot's drawing area.
     * The bounds are in the node's local coordinates.
     * 
     * @param subplotIndex
     * @return
     */
    private Rectangle2D getPlotBounds( int subplotIndex ) {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        PlotRenderingInfo subplotInfo = plotInfo.getSubplotInfo( subplotIndex );
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = subplotInfo.getDataArea();
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
        return nodeToSubplot( screenPoint, BSCombinedChart.ENERGY_PLOT_INDEX );
    }
    
    /**
     * Converts a point in the energy chart's coordinate system
     * to a point in the node's local coordinate system.
     * 
     * @param screenPoint
     * @return
     */
    public Point2D energyToNode( Point2D energyPoint ) {
        return subplotToNode( energyPoint, BSCombinedChart.ENERGY_PLOT_INDEX );
    }
    
    /**
     * Converts a distance on the energy plot's position axis to 
     * a distance in the node's local coordinate system.
     * 
     * @param distance
     * @return
     */
    public double energyToNode( double distance ) {
        Point2D origin = energyToNode( new Point2D.Double( 0, 0 ) );
        Point2D position = energyToNode( new Point2D.Double( distance, 0 ) );
        return position.getX() - origin.getX();
    }
}
