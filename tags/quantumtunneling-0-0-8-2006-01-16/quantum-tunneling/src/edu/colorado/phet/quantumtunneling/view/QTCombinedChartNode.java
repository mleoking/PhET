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
import org.jfree.chart.plot.PlotRenderingInfo;

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
        setPickable( false );
        setChildrenPickable( false );
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
        return nodeToSubplot( screenPoint, QTCombinedChart.ENERGY_PLOT_INDEX );
    }
    
    /**
     * Converts a point in the energy chart's coordinate system
     * to a point in the node's local coordinate system.
     * 
     * @param screenPoint
     * @return
     */
    public Point2D energyToNode( Point2D energyPoint ) {
        return subplotToNode( energyPoint, QTCombinedChart.ENERGY_PLOT_INDEX );
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
