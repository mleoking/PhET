package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.ZoomControlNode;
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

        zoomControl.addZoomListener( new ChartRescalingZoomListener(chart, this) );
    }
}
