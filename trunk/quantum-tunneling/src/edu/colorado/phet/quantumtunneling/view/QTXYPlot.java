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
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;


/**
 * QTXYPlot makes it possible to directly draw an XYPlot's markers.
 * The methods required to do this are protected in XYPlot. 
 * This class simply makes those methods public.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTXYPlot extends XYPlot {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public QTXYPlot() {
        super();
    }

    public QTXYPlot( XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer ) {
        super( dataset, domainAxis, rangeAxis, renderer );
    }
    
    //----------------------------------------------------------------------------
    // XYPlot overrides
    //----------------------------------------------------------------------------
    
    /*
     * Makes the interface for drawing domain (vertical) markers public.
     */
    public void drawDomainMarkers( Graphics2D g2, Rectangle2D dataArea, int index, Layer layer ) {
        super.drawDomainMarkers( g2, dataArea, index, layer );
    }
    
    /*
     * Makes the interface for drawing range (horizontal) markers public.
     */
    public void drawRangeMarkers( Graphics2D g2, Rectangle2D dataArea, int index, Layer layer ) {
        super.drawRangeMarkers( g2, dataArea, index, layer );
    }
}
