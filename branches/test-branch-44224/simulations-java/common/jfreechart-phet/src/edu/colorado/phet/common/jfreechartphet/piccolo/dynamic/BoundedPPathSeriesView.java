
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Renderer for DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class BoundedPPathSeriesView extends PPathSeriesView {
    public BoundedPPathSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    protected PPath createPPath() {
        return new IncrementalPPath( getDynamicJFreeChartNode().getPhetPCanvas() ) {
            protected void globalBoundsChanged( Rectangle2D bounds ) {
                Rectangle2D dataArea = getDataArea();
                getDynamicJFreeChartNode().localToGlobal( dataArea );
                Rectangle2D b = bounds.createIntersection( dataArea );
                super.globalBoundsChanged( b );
            }
        };
    }
}
