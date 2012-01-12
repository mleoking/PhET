// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Renderer for DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class BufferedImmediateSeriesView extends BufferedSeriesView {

    public BufferedImmediateSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    protected void repaintPanel( Rectangle2D bounds ) {

        Rectangle2D dataArea = getDataArea();
        getDynamicJFreeChartNode().localToGlobal( dataArea );
        bounds = bounds.createIntersection( dataArea );
//        bounds=b;
        /*Paint immediately requires a parent component to be opaque.  Perhaps this code should be replaced with a subclass of RepaintManager?*/
        getDynamicJFreeChartNode().getPhetPCanvas().paintImmediately( new Rectangle( (int) bounds.getX(), (int) bounds.getY(), (int) ( bounds.getWidth() + 1 ), (int) ( bounds.getHeight() + 1 ) ) );
    }
}
