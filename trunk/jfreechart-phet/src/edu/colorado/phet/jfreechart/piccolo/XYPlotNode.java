/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.jfreechart.piccolo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * XYPlotNode is a node that draws an XYPlot's datasets in a specified data area.
 * This node is used to render an XYPlot outside of the JFreeChart framework.
 * Note that only the plot's datasets are drawn -- the plot's other parts 
 * (axes, labels, ticks, gridlines, etc.) are not drawn.
 * <p>
 * One place where you might use this node is to improve the performance
 * of a dynamic chart. JFreeChart really isn't up to the task of drawing
 * lots of dyamic data. But you can use a JFreeChart to draw the static 
 * chart elements, and one or more XYPlotNodes to draw the dynamic data.
 * Here's an example:
 * <code>
 * // Create your own plot subclass
 * public class MyXYPlot extends XYPlot {
 *     private XYSeries mySeries;
 *     public MyXYPlot() {
 *         // Set up datasets, renderers, axes, labels, ticks, gridlines, etc.
 *         // Do NOT set up any data.
 *     }
 *     public XYSeries getMySeries() {
 *          return mySeries;
 *     }
 * }
 * 
 * // Create 2 identical plots, one of which will never contain data.
 * // MYXYPlot sets up datasets, renders, axes, labels, ticks, gridlines, etc.
 * MyXYPlot plotNoData = new MyXYPlot(...);
 * MyXYPlot plotWithData = new MyXYPlot(...);
 * 
 * // Put the dataless plot in a buffered chart node.
 * // The chart will be drawn using a buffered image, and the image
 * // will change only when the chart changes.  Since we're not 
 * // going to add data to plotNoData, the data won't affect the chart.
 * JFreeChart chart = new JFreeChart(..., plotNoData,...);
 * JFreeChartNode chartNode = new JFreeChartNode( chart );
 * chartNode.setBuffered( true );
 * 
 * // Put the plot with data in an XYPlotNode.
 * XYPlotNode plotNode = new XYPlotNode( plotWithData );
 * 
 * // Make the XYPlotNode look like the chart
 * plotNode.setRenderingHints( chart.getRenderingHints() );
 * 
 * // Set the XYPlotNode's data area using the chart's rendering info.
 * chartNode.updateChartRenderingInfo();
 * PlotRenderingInfo plotInfo = chart.getChartRenderingInfo().getPlotInfo();
 * Rectangle2D chartDataAreaReference = plotInfo.getDataArea();
 * // Copy the reference so we don't whack the plotInfo's data area!
 * Rectangle2D chartDataArea = new Rectangle2D.Double( dataAreaReference );
 * Rectangle2D globalDataArea = chartNode.localToGlobal( localDataArea );
 * Rectangle2D plotDataArea = plotNode.globalToLocal( globalDataArea );
 * plotNode.setDataArea( plotDataArea );
 * 
 * // Update the XYPlotNode's data area if the chart changes...
 * chart.addChangeListener( new ChartChangeListener() {
 *     public void chartChanged( ChartChangeEvent e ) {
 *         // set the plotNode's data area, as above...
 *     }
 * } );
 * chartNode.addPropertyChangeListener( new PropertyChangeListener() {
 *     public void propertyChanged( PropertyChangeEvent e ) {
 *         // set the plotNode's data area, as above...
 *     }
 * } );
 *
 * // Add the nodes to a PCanvas.
 * PCanvas canvas = new PCanvas();
 * canvas.getLayer().addChild( chartNode );
 * chartNode.setOffset(...);
 * canvas.getLayer().addChild( plotNode );
 * plotNode.setOffset(...);
 * 
 * // Add data to the XYPlotNode's plot...
 * XYSeries series = plotWithData.getMySeries();
 * for (...) {
 *     series.add(...);
 * }
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class XYPlotNode extends PPath implements PlotChangeListener  {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private XYPlot _plot; // the plot whose data we'll be rendering
    private Rectangle2D _dataArea; // data area of the plot, in node's local coordinates
    private boolean _dataAreaStroked; // should we stroke the data area?
    private String _name; // name, for debugging
    private RenderingHints _renderingHints;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param plot
     */
    public XYPlotNode( XYPlot plot ) {
        super();
        _plot = plot;
        _plot.addChangeListener( this );
        _dataArea = new Rectangle2D.Double( 0, 0, 1, 1 );
        _dataAreaStroked = true; // see setDataAreaStroked
        _name = null;
        _renderingHints = null;
    }

    /**
     * Call this method before releasing all references to this object.
     * This cleans up any listener associations made with the plot.
     */
    public void cleanup() {
        if ( _plot != null ) {
            _plot.removeChangeListener( this );
            _plot = null;
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the name of this node.
     * This is useful mainly for debugging.
     * 
     * @param name
     */
    public void setName( String name ) {
        _name = name;
    }
    
    /**
     * Gets the name of this node.
     * 
     * @return the name
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Gets the plot.
     * 
     * @return the plot
     */
    public XYPlot getPlot() {
        return _plot;
    }
    
    /**
     * Sets rendering hints used to draw the plot's data.
     * If the plot is to be drawn on top of a JFreeChart image,
     * you probably want to use the chart's rendering hints so
     * that you have a consistent "look".
     * <p>
     * For example:
     * <code>
     * JFreeChart chart = new JFreeChart(...);
     * XYPlot plot = new XYPlot(...);
     * XYPlotNode node = new XYPlotNode( plot );
     * node.setRenderingHints( chart.getRenderingHints() );
     * </code>
     * 
     * @param renderingHints
     */
    public void setRenderingHints( RenderingHints renderingHints ) {
        _renderingHints = renderingHints;
    }
    
    /**
     * Gets the rendering hints.
     * 
     * @return rendering hints
     */
    public RenderingHints getRenderingHints() {
        return _renderingHints;
    }
    
    /**
     * Sets the data area. 
     * Drawing of the plot's datasets will be clipped to this rectangle.
     * The data area is in the node's local coordinate system.
     * 
     * @param dataArea
     */
    public void setDataArea( Rectangle2D dataArea ) {
        setBounds( dataArea );
        _dataArea.setRect( dataArea );
        repaint();
    }
    
    /**
     * Gets the data area, in the node's local coordinate system.
     * 
     * @return data area
     */
    public Rectangle2D getDataArea() {
        return new Rectangle2D.Double( _dataArea.getX(), _dataArea.getY(), _dataArea.getWidth(), _dataArea.getHeight() );
    }
    
    /**
     * Enables or disables stroking of the data area (enabled by default).
     * The plot's outline stroke and paint are used.
     * The border is usually stroked by the plot's draw method, 
     * but since we're only rendering datasets, the border won't be drawn.
     * If you're drawing on top of a JFreeChart, you probably want to
     * leave this enabled, since plotting the data may overwrite some
     * of the border.
     * 
     * @param dataAreaStroke
     */
    public void setDataAreaStroked( boolean dataAreaStroked ) {
        _dataAreaStroked = dataAreaStroked;
    }

    /**
     * Will the data area be stroked?
     * 
     * @return true or false
     */
    public boolean isDataAreaStroked() {
        return _dataAreaStroked;
    }
    
    //----------------------------------------------------------------------------
    // PlotChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Repaints the node whenever the plot changes.
     * 
     * @param event
     */
    public void plotChanged( PlotChangeEvent event ) {
        repaint();
    }
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------
    
    /*
     * Renders the plot's data in the data area.
     * Drawing is clipped to the data area.
     * The data area is in the node's local coordinates.
     * After rendering the data, the data area is optionally stroked.
     */
    protected void paint( PPaintContext paintContext ) {
        
        Graphics2D g2 = paintContext.getGraphics();
        
        if ( _renderingHints != null ) {
            g2.setRenderingHints( _renderingHints );
        }
        
        // Clip to the data area.
        // Do NOT call g2.setClip, or really bad things happen
        // with drawing order on Macintosh (and possibly other platforms).
        paintContext.pushClip( _dataArea );
        
        // Render each of the plot's datasets, in the proper order...
        int numberOfDatasets = _plot.getDatasetCount();
        DatasetRenderingOrder renderingOrder = _plot.getDatasetRenderingOrder();
        if ( renderingOrder == DatasetRenderingOrder.FORWARD ) {
            for ( int i = 0; i < numberOfDatasets; i++ ) {
                _plot.render( g2, _dataArea, i, null, null );
            }
        }
        else { /* DatasetRenderingOrder.REVERSE */
            for ( int i = numberOfDatasets - 1; i >= 0; i-- ) {
                _plot.render( g2, _dataArea, i, null, null );
            }
        }
        
        // restore the clip
        paintContext.popClip( null );
        
        // optionally stroke the data area -- do this after restoring the clip
        if ( _dataAreaStroked ) {
            g2.setStroke( _plot.getOutlineStroke() );
            g2.setPaint( _plot.getOutlinePaint() );
            g2.draw( _dataArea );
        }
    }
}
