/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.opticaltweezers.control.CloseButtonNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class PotentialEnergyChartNode extends PhetPNode {
    
    private PotentialEnergyChart _chart;
    private JFreeChartNode _chartWrapper;
    private CloseButtonNode _closeButtonNode;
    
    public PotentialEnergyChartNode( PSwingCanvas canvas, PotentialEnergyChart chart ) {
        super();
        
        _chart = chart;
        _chartWrapper = new JFreeChartNode( chart );
        addChild( _chartWrapper );
        
        _closeButtonNode = new CloseButtonNode( canvas );
        addChild( _closeButtonNode );
        
        setPickable( false );
        _chartWrapper.setPickable( false );
        _chartWrapper.setChildrenPickable( false );
        
        updateLayout();
    }
    
    public PotentialEnergyChart getPotentialEnergyChart() {
        return _chart;
    }
    
    public void setChartSize( double w, double h ) {
        _chartWrapper.setBounds( 0, 0, w, h );
        updateLayout();
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
    
    /*
     * Updates the layout when the bounds change.
     */
    private void updateLayout() {
        
        final double horizMargin = 15;
        final double vertMargin = 15;
        
        final double maxWidth = _chartWrapper.getFullBounds().getWidth();
        double maxHeight = _closeButtonNode.getFullBounds().getHeight() + vertMargin;
        
        double x = 0;
        double y = 0;
        
        // chart: close button sits on chart
        x = 0;
        y = 0;
        _chartWrapper.setOffset( x, y );
        
        // close button: right edge, vertically centered
        x = maxWidth - horizMargin - _closeButtonNode.getFullBounds().getWidth();
        y = ( maxHeight - _closeButtonNode.getFullBounds().getHeight() ) / 2;
        _closeButtonNode.setOffset( x, y );
    }
    
    public void addCloseListener( ActionListener listener ) {
        _closeButtonNode.addActionListener( listener );
    }
    
    public void removeCloseListener( ActionListener listener ) {
        _closeButtonNode.removeActionListener( listener );
    }
}
