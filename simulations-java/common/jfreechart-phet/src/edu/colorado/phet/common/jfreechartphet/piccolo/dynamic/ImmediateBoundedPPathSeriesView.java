package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;

import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
 * Jun 6, 2007, 4:13:36 AM
 */
public class ImmediateBoundedPPathSeriesView extends PPathSeriesView {
    public ImmediateBoundedPPathSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    protected PPath createPPath() {
        return new IncrementalPPath( getDynamicJFreeChartNode().getPhetPCanvas() ) {
            protected void globalBoundsChanged( Rectangle2D bounds ) {
                Rectangle2D dataArea=getDataArea();
                getDynamicJFreeChartNode().localToGlobal( dataArea );
                Rectangle2D b = bounds.createIntersection( dataArea );
                super.repaintGlobalBoundsImmediately( b );
            }
        };
    }
}
