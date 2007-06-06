package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import org.jfree.data.xy.XYSeriesCollection;

/**
 * Author: Sam Reid
* Jun 5, 2007, 6:03:46 PM
*/
public class JFreeChartSeriesView extends SeriesView {

    public JFreeChartSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    public void dataAdded() {
        //painting happens automatically due to changes in the JFreeChart
    }

    public void uninstall() {
        super.uninstall();
        XYSeriesCollection xySeriesCollection = (XYSeriesCollection)getDynamicJFreeChartNode().getChart().getXYPlot().getDataset();
        xySeriesCollection.removeSeries( getSeriesData().getSeries() );
    }

    public void install() {
        super.install();
        XYSeriesCollection xySeriesCollection = (XYSeriesCollection)getDynamicJFreeChartNode().getChart().getXYPlot().getDataset();
        xySeriesCollection.addSeries( getSeriesData().getSeries() );
    }
}
