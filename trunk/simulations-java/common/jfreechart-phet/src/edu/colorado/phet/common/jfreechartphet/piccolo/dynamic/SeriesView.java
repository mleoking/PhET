package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import org.jfree.data.xy.XYSeries;

/**
     * Base class strategy for painting a data series.
 */
public abstract class SeriesView {
    DynamicJFreeChartNode dynamicJFreeChartNode;
    SeriesData seriesData;
    private SeriesData.Listener listener = new SeriesData.Listener() {
        public void dataAdded() {
            SeriesView.this.dataAdded();
        }
    };

    public SeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        this.dynamicJFreeChartNode = dynamicJFreeChartNode;
        this.seriesData = seriesData;
    }

    public abstract void dataAdded();

    public void uninstall() {
        seriesData.removeListener( listener );
    }

    public void install() {
        seriesData.addListener( listener );
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    public XYSeries getSeries() {
        return seriesData.getSeries();
    }

    public SeriesData getSeriesData() {
        return seriesData;
    }
}
