
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

/**
 * DISCLAIMER: This class and subclasses are under development and not ready for general use.
 * This class creates renderers for use in DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public interface SeriesViewFactory {
    SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData );
}
