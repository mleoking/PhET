/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.plot;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * XYPlotNode is a node that draws an XYPlot's data in a specified data area.
 * This node is used to render an XYPlot outside of the JFreeChart framework.
 * Note that only the plot's data is drawn -- the plot's other parts (axes, 
 * labels, ticks) are not drawn.
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
    
    public XYPlotNode( XYPlot plot ) {
        super();
        _plot = plot;
        _plot.addChangeListener( this );
        _dataArea = new Rectangle2D.Double( 0, 0, 1, 1 );
        _dataAreaStroked = true;
        _name = null;
        _renderingHints = null;
    }

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
     * @return
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
     * @return
     */
    public RenderingHints getRenderingHints() {
        return _renderingHints;
    }
    
    /**
     * Sets the data area. 
     * Drawing of the plot's data will be clipped to this rectangle.
     * The coordinates of the data area are expressed in the node's
     * local coordinate system.
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
     * @return
     */
    public Rectangle2D getDataArea() {
        return new Rectangle2D.Double( _dataArea.getX(), _dataArea.getY(), _dataArea.getWidth(), _dataArea.getHeight() );
    }
    
    /**
     * Enables or disables stroking of the data area.
     * The plot's outline stroke and paint are used.
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
        
        // Clip to the data area
        Shape restoreClip = g2.getClip();
        g2.setClip( _dataArea );
        
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
        g2.setClip( restoreClip );
        
        // optionally stroke the data area -- do this after restoring the clip
        if ( _dataAreaStroked ) {
            g2.setStroke( _plot.getOutlineStroke() );
            g2.setPaint( _plot.getOutlinePaint() );
            g2.draw( _dataArea );
        }
    }
}
