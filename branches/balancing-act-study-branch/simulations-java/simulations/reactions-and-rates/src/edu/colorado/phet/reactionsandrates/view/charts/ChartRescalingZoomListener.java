// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import org.jfree.data.Range;

/**
 * A zoom listener that rescales a chart.
 */
class ChartRescalingZoomListener implements ZoomControlNode.ZoomListener {
    private static final int MIN_NUM = 2;
    private static final double ZOOM_SCALE_FACTOR = 2.0;

    private final ResizableChart chart;
    private final Repaintable chartGraphic;

    public ChartRescalingZoomListener( ResizableChart chart, Repaintable chartGraphic ) {
        this.chart = chart;
        this.chartGraphic = chartGraphic;
    }

    public void zoomedOut() {
        handleZoom( true );
    }

    public void zoomedIn() {
        handleZoom( false );
    }

    private void handleZoom( boolean zoomOut ) {
        Range plotRange = chart.getYRange();

        double newMax = plotRange.getLength();

        if( zoomOut ) {
            newMax *= ZOOM_SCALE_FACTOR;
        }
        else {
            newMax /= ZOOM_SCALE_FACTOR;

            if( newMax < MIN_NUM ) {
                newMax = MIN_NUM;
            }
        }

        chart.setYRange( 0, (int)newMax );
        chartGraphic.repaint();
    }
}
