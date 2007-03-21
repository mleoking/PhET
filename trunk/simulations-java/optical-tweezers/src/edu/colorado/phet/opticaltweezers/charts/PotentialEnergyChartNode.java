/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPNode;


public class PotentialEnergyChartNode extends PhetPNode {
    
    private PotentialEnergyChart _chart;
    private JFreeChartNode _chartWrapper;
    
    public PotentialEnergyChartNode( PotentialEnergyChart chart ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        _chart = chart;
        _chartWrapper = new JFreeChartNode( chart );
        addChild( _chartWrapper );
    }
    
    public PotentialEnergyChart getPotentialEnergyChart() {
        return _chart;
    }
    
    public void setChartSize( double w, double h ) {
        _chartWrapper.setBounds( 0, 0, w, h );
    }
    
    public void updateChartRenderingInfo() {
        _chartWrapper.updateChartRenderingInfo();
    }
    
    /**
     * Gets the bounds of the associated chart's plot.
     * The bounds are in the node's local coordinates.
     * 
     * @return Rectangle2D
     */
    public Rectangle2D getPlotBounds() {
        ChartRenderingInfo chartInfo = _chartWrapper.getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }
}
