/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.CloseButtonNode;


public class PotentialEnergyChartNode extends PhetPNode {
    
    public static final double DEFAULT_HEIGHT = 200;
    
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color CHART_BORDER_COLOR = Color.BLACK;
    private static final Stroke CHART_BORDER_STROKE = new BasicStroke( 6f );
    private static final RectangleInsets CHART_INSETS = new RectangleInsets( 10, 10, 10, 10 ); // top,left,bottom,right

    private PotentialEnergyPlot _plot;
    private JFreeChartNode _chartWrapper;
    private CloseButtonNode _closeButtonNode;
    
    public PotentialEnergyChartNode() {
        super();
        
        _plot = new PotentialEnergyPlot();
        
        JFreeChart chart = new JFreeChart( OTResources.getString( "title.potentialEnergyChart" ), null /* titleFont */, _plot, false /* createLegend */ );
        chart.setAntiAlias( true );
        chart.setBorderVisible( true );
        chart.setBackgroundPaint( BACKGROUND_COLOR );
        chart.setBorderPaint( CHART_BORDER_COLOR );
        chart.setBorderStroke( CHART_BORDER_STROKE );
        chart.setPadding( CHART_INSETS );
        _chartWrapper = new JFreeChartNode( chart );

        _closeButtonNode = new CloseButtonNode();
        _closeButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
            }
        });
        
        addChild( _chartWrapper );
        addChild( _closeButtonNode );
        
        setPickable( false );
        _chartWrapper.setPickable( false );
        _chartWrapper.setChildrenPickable( false );
        
        updateLayout();
    }
    
    public void setChartSize( double w, double h ) {
        _chartWrapper.setBounds( 0, 0, w, h );
        _chartWrapper.updateChartRenderingInfo();
        updateLayout();
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
}
