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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;


/**
 * ChartNode is a Piccolo node that draws a JFreeChart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ChartNode extends DrawableNode implements ChartChangeListener {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param chart
     */
    public ChartNode( JFreeChart chart ) {
        super( chart );
        chart.addChangeListener( this );
    }

    //----------------------------------------------------------------------------
    // ChartChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Repaints the node when the chart (or any of its components) changes.
     * 
     * @param event
     */
    public void chartChanged( ChartChangeEvent event ) {
        /* 
         * Do not look at event.getSource(), since we 
         * can't identify all of the chart's components.
         */
        repaint();
    }
}
