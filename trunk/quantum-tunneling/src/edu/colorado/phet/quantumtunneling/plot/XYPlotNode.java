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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * XYPlotNode is a node that draws an JFreeChart XYPlot in a specified data area.
 * This is used to render an XYPlot outside of the JFreeChart framework.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class XYPlotNode extends PNode implements PlotChangeListener {

    private XYPlot _plot;
    private Rectangle2D _dataArea;
    private String _name;
    
    public XYPlotNode( XYPlot plot ) {
        super();
        _plot = plot;
        _plot.addChangeListener( this );
        _dataArea = new Rectangle2D.Double( 0, 0, 1, 1 );
        _name = null;
    }

    public void cleanup() {
        if ( _plot != null ) {
            _plot.removeChangeListener( this );
            _plot = null;
        }
    }
    
    public void setName( String name ) {
        _name = name;
    }
    
    public String getName() {
        return _name;
    }
    
    public XYPlot getPlot() {
        return _plot;
    }
    
    public void setDataArea( Rectangle2D dataArea ) {
        System.out.println( "setDataArea: " + getName() + " " + dataArea );//XXX
        _dataArea.setRect( dataArea );
        repaint();
    }
    
    public Rectangle2D getDataArea() {
        return new Rectangle2D.Double( _dataArea.getX(), _dataArea.getY(), _dataArea.getWidth(), _dataArea.getHeight() );
    }
    
    public void plotChanged( PlotChangeEvent event ) {
        repaint();
    }
    
    protected void paint( PPaintContext paintContext ) {
        System.out.println( "paint " + getName() );//XXX
        Graphics2D g2 = paintContext.getGraphics();
        int numberOfDatasets = _plot.getDatasetCount();
        for ( int i = 0; i < numberOfDatasets; i++ ) {
            _plot.render( g2, _dataArea, i, null, null );
        }
    }
}
