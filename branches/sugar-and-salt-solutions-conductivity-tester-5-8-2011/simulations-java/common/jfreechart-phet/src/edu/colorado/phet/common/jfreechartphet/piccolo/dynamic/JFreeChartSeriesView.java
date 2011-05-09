
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import org.jfree.data.xy.XYSeriesCollection;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Renderer for DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class JFreeChartSeriesView extends SeriesView {

    //todo: provide support for invisible jfreechart series view  
    public void visibilityChanged() {

    }

    public JFreeChartSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
    }

    public void dataAdded() {
        //painting happens automatically due to changes in the JFreeChart
    }

    public void dataCleared() {
        //handled automatically in the JFreeChart
    }

    public void uninstall() {
        super.uninstall();
        XYSeriesCollection xySeriesCollection = (XYSeriesCollection) getDynamicJFreeChartNode().getChart().getXYPlot().getDataset();
        xySeriesCollection.removeSeries( getSeriesData().getSeries() );
    }

    public void install() {
        super.install();
        XYSeriesCollection xySeriesCollection = (XYSeriesCollection) getDynamicJFreeChartNode().getChart().getXYPlot().getDataset();
        xySeriesCollection.addSeries( getSeriesData().getSeries() );
    }

    protected void forceRepaintAll() {
    }
}
