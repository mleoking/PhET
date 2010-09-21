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

import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * BSRendererFactory encapsulates the creation of JFreeChart renderers
 * for various purposes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSRendererFactory {

    /* Not intended for instantiation */
    private BSRendererFactory() {}
    
    /**
     * Creates a renderer that will approximate a curve by 
     * connecting a series of points with line segments.
     * 
     * @return XYItemRenderer
     */
    public static XYItemRenderer createCurveRenderer() {
        StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setDrawSeriesLineAsPath( true );
        return renderer;
    }
    
    /**
     * Creates a renderer that will treat a series of points 
     * as eigenstates, to be rendered as a set of horizontal lines.
     * 
     * @return XYItemRenderer
     */
    public static XYItemRenderer createEigenstatesRenderer() {
        return new EigenstatesRenderer();
    }
    
    /**
     * Creates a renderer that will treat a series of points
     * as wave function phase data.
     * 
     * @return XYItemRenderer
     */
    public static XYItemRenderer createPhaseRenderer() {
        return new PhaseRenderer();
    }
}
