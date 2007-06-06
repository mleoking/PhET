package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.geom.Rectangle2D;
import java.awt.*;

/**
 * Author: Sam Reid
* Jun 5, 2007, 6:04:16 PM
*/
public class BufferedImmediateSeriesView extends BufferedSeriesView {

    public BufferedImmediateSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        /*Paint immediately requires a parent component to be opaque.  Perhaps this code should be replaced with a subclass of RepaintManager?*/
        dynamicJFreeChartNode.getPhetPCanvas().paintImmediately( new Rectangle( (int)bounds.getX(), (int)bounds.getY(), (int)( bounds.getWidth() + 1 ), (int)( bounds.getHeight() + 1 ) ) );
    }
}
