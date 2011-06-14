// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * Superclass for rescalable chart nodes.
 */
public abstract class AbstractRescaleableChartNode extends PNode implements Repaintable {
    private static final int PADDING = 3;

    protected void addZoomControl( Dimension size, PhetPCanvas chartCanvas, ResizableChart chart ) {
        ZoomControlNode zoomControl = new ZoomControlNode( ZoomControlNode.VERTICAL );

        zoomControl.setOffset( PADDING, size.getHeight() - zoomControl.getFullBounds().getHeight() - PADDING );

        chartCanvas.addScreenChild( zoomControl );

        zoomControl.addZoomListener( new ChartRescalingZoomListener( chart, this ) );
    }
}
