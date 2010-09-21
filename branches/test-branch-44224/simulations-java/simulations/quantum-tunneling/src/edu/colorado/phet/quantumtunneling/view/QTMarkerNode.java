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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * QTMarkerNode is a node that draws an XYPlot's markers in a specified data area.
 * This node is used to render an XYPlot outside of the JFreeChart framework.
 * Note that only the plot's markers are drawn -- the plot's other parts 
 * (datasets, axes, labels, ticks, gridlines, etc.) are not drawn.
 * <p>
 * Usage is similar to XYPlotNode in jfreechart-phet.
 * <p>
 * NOTE: Markers for all layers (foreground and background) are drawn at the same time.
 * If this becomes an issue in the future, then we will need to specify which
 * layer's markers should be drawn in the constructor. You would then create separate
 * nodes for foreground and background markers, and place them in the desired 
 * piccolo layer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTMarkerNode extends PPath implements PlotChangeListener  {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTXYPlot _plot; // the plot whose data we'll be rendering
    private Rectangle2D _dataArea; // data area of the plot, in node's local coordinates
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
    public QTMarkerNode( QTXYPlot plot ) {
        super();
        _plot = plot;
        _plot.addChangeListener( this );
        _dataArea = new Rectangle2D.Double( 0, 0, 1, 1 );
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
     * Sets rendering hints used to draw the plot's markers.
     * If drawing on top of a JFreeChart image, you'll probably want to use
     * the chart's rendering hints so that you have a consistent "look".
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
     * Drawing will be clipped to this rectangle.
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
     * Renders the plot's markers in the data area.
     * Drawing is clipped to the data area.
     * The data area is in the node's local coordinates.
     */
    protected void paint( PPaintContext paintContext ) {
        
        Graphics2D g2 = paintContext.getGraphics();
        
        if ( _renderingHints != null ) {
            g2.setRenderingHints( _renderingHints );
        }
        
        // Clip to the data area
        Shape restoreClip = g2.getClip();
        g2.setClip( _dataArea );
        
        // Draw the markers...
        int numberOfRenderers = _plot.getDatasetCount();
        for ( int i = 0; i < numberOfRenderers; i++ ) {
            _plot.drawDomainMarkers( g2, _dataArea, i, Layer.BACKGROUND );
            _plot.drawDomainMarkers( g2, _dataArea, i, Layer.FOREGROUND );
        }
        for ( int i = 0; i < numberOfRenderers; i++ ) {
            _plot.drawRangeMarkers( g2, _dataArea, i, Layer.BACKGROUND );
            _plot.drawRangeMarkers( g2, _dataArea, i, Layer.FOREGROUND );
        }
        
        // restore the clip
        g2.setClip( restoreClip );
    }
}

