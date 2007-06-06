package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 6:03:59 PM
 */
public class IncrementalPPathSeriesView extends PPathSeriesView {
    public IncrementalPPathSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    protected PPath createPPath() {
        return new IncrementalPPath( getDynamicJFreeChartNode().getPhetPCanvas() );
    }
}
