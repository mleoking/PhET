
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.IncrementalPPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Renderer for DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class IncrementalPPathSeriesView extends PPathSeriesView {
    public IncrementalPPathSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    protected PPath createPPath() {
        return new IncrementalPPath( getDynamicJFreeChartNode().getPhetPCanvas() );
    }
}
