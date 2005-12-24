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

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * JFreeChartNode is a Piccolo node for displaying a JFreeChart.
 * The bounds of the node determine the size of the chart.
 * The node registers with the chart to receive notification
 * of changes to any component of the chart.  The chart is
 * redrawn automatically whenever this notification is received.
 * <p/>
 * Note: the chart has a size of 0,0 until the user manually calls setBounds(...)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JFreeChartNode extends PNode implements ChartChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private JFreeChart _chart; // chart associated with the node
    private ChartRenderingInfo _info; // the chart's rendering info

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructs a node that displays the specified chart.
     * Note: the chart has a size of 0,0 until the user manually calls setBounds(...)
     *
     * @param chart
     */
    public JFreeChartNode( JFreeChart chart ) {
        super();
        _info = new ChartRenderingInfo();
        _chart = chart;
        _chart.addChangeListener( this );
        updateChartRenderingInfo();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the chart that is associated with this node.
     *
     * @return JFreeChart
     */
    public JFreeChart getChart() {
        return _chart;
    }

    /**
     * Gets the chart's rendering info.
     * Changes to the chart are not reflected in the rendering info
     * until after the chart has been painted.
     * You can force the rendering info to be updated by calling
     * updateChartRenderingInfo.
     *
     * @return ChartRenderingInfo
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return _info;
    }

    /**
     * Forces an update of the chart rendering info, which is normally
     * not updated until the next call to paint.
     */
    public void updateChartRenderingInfo() {
        BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        PPaintContext paintContext = new PPaintContext( g2 );
        paint( paintContext );
    }

    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------

    /*
     * Paints the node.
     * The node's bounds (in the node's local coordinate system)
     * are used to determine the size and location of the chart.
     * Painting the node updates the chart's rendering info.
     */
    protected void paint( PPaintContext paintContext ) {
        Graphics2D g2 = paintContext.getGraphics();
        _chart.draw( g2, getBoundsReference(), _info );
    }

    //----------------------------------------------------------------------------
    // ChartChangeListener implementation
    //----------------------------------------------------------------------------

    /**
     * Receives notification of changes to the chart (or any of its components),
     * and redraws the chart.
     *
     * @param event
     */
    public void chartChanged( ChartChangeEvent event ) {
        /*
         * Do not look at event.getSource(), since the source of the event is
         * likely to be one of the chart's components rather than the chart itself.
         */
        repaint();
    }
}
