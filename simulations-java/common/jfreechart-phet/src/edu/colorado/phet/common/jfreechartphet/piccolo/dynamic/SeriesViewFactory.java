package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

/**
 * Author: Sam Reid
* Jun 8, 2007, 2:30:32 AM
*/
public interface SeriesViewFactory {
    SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData );
}
