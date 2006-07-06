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
     * Converts a Y coordinate in the node's local coordinate system
     * to an energy value in the energy chart's coordinate system.
     * 
     * @param yCoordinate
     * @return
     */
    public double nodeToEnergy( double yCoordinate ) {
        Point2D p = nodeToEnergy( new Point2D.Double( 0, yCoordinate ) );
        return p.getY();
    }
    
    /**
     * Converts an X coordinate in the node's local coordinate system
     * to a position value in the chart's coordinate system.
     * 
     * @param yCoordinate
     * @return
     */
    public double nodeToPosition( double xCoordinate ) {
        Point2D p = nodeToEnergy( new Point2D.Double( xCoordinate, 0 ) );
        return p.getX();
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
     * Converts an energy to a y coordinate in the node's local coordinate system.
     * 
     * @param energy
     * @return
     */
    public double energyToNode( double energy ) {
        Point2D modelPoint = new Point2D.Double( 0, energy );
        Point2D viewPoint = energyToNode( modelPoint );
        return viewPoint.getY();
    }
    
    /**
     * Converts a position to an x coordinate in the node's local coordinate system.
     * 
     * @param position, in nm
     * @return
     */
    public double positionToNode( double position ) {
        Point2D modelPoint = new Point2D.Double( position, 0 );
        Point2D viewPoint = energyToNode( modelPoint );
        return viewPoint.getX();
    }
}
