package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class extends the functionality of JFreeChartNode by providing different strategies for rendering the data.
 * It is assumed that the chart's plot is XYPlot, and some functionality is lost in rendering, since we
 * have our own rendering strategies here.
 */
public class DynamicJFreeChartNode extends JFreeChartNode {
    private ArrayList seriesDataList = new ArrayList();
    private ArrayList seriesViewList = new ArrayList();
    private SeriesViewFactory jfreeChartSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new JFreeChartSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory piccoloSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new PiccoloSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory bufferedSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BufferedSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory viewFactory = jfreeChartSeriesFactory;

    public DynamicJFreeChartNode( JFreeChart chart ) {
        super( chart );
    }

    public void addValue( double x, double y ) {
        addValue( 0, x, y );
    }

    private void addValue( int series, double x, double y ) {
        getSeries( series ).addValue( x, y );
    }

    private SeriesData getSeries( int series ) {
        return (SeriesData)seriesDataList.get( series );
    }

    public void addSeries( String title, Color color ) {
        SeriesData seriesData = new SeriesData( title, color );
        seriesDataList.add( seriesData );
        updateSeriesViews();
    }

    static abstract class SeriesView {
        DynamicJFreeChartNode dynamicJFreeChartNode;
        SeriesData seriesData;

        public SeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            this.dynamicJFreeChartNode = dynamicJFreeChartNode;
            this.seriesData = seriesData;
            seriesData.addListener( new SeriesData.Listener() {
                public void dataAdded() {
                    SeriesView.this.dataAdded();
                }
            } );
        }

        public abstract void dataAdded();

        public abstract void uninstall();

        public abstract void install();
    }

    static class JFreeChartSeriesView extends SeriesView {

        public JFreeChartSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
            //painting happens automatically due to changes in the JFreeChart
        }

        public void uninstall() {
            XYSeriesCollection xySeriesCollection = (XYSeriesCollection)dynamicJFreeChartNode.getChart().getXYPlot().getDataset();
            xySeriesCollection.removeSeries( seriesData.getSeries() );
        }

        public void install() {
            XYSeriesCollection xySeriesCollection = (XYSeriesCollection)dynamicJFreeChartNode.getChart().getXYPlot().getDataset();
            xySeriesCollection.addSeries( seriesData.getSeries() );
        }
    }

    static class PiccoloSeriesView extends SeriesView {

        public PiccoloSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );

        }

        public void dataAdded() {
        }

        public void uninstall() {
        }

        public void install() {
        }
    }

    static class BufferedSeriesView extends SeriesView {

        public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
        }

        public void uninstall() {
        }

        public void install() {
        }
    }

    static interface SeriesViewFactory {
        SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData );
    }

    public void setJFreeChartSeries() {
        viewFactory = jfreeChartSeriesFactory;
        updateSeriesViews();
    }

    public void setPiccoloSeries() {
        viewFactory = piccoloSeriesFactory;
        updateSeriesViews();
    }

    public void setBufferedSeries() {
        viewFactory = bufferedSeriesFactory;
        updateSeriesViews();
    }

    private void updateSeriesViews() {
        removeAllSeriesViews();
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            SeriesView seriesDataView = viewFactory.createSeriesView( this, seriesData );
            seriesDataView.install();
            seriesViewList.add( seriesDataView );
        }
    }

    private void removeAllSeriesViews() {
        while( seriesViewList.size() > 0 ) {
            SeriesView seriesView = (SeriesView)seriesViewList.get( 0 );
            seriesView.uninstall();
            seriesViewList.remove( seriesView );
        }
    }

    public static class SeriesData {
        String title;
        Color color;
        XYSeries series;

        static int index = 0;

        public SeriesData( String title, Color color ) {
            this( title, color, new XYSeries( title + " " + ( index++ ) ) );
        }

        public SeriesData( String title, Color color, XYSeries series ) {
            this.title = title;
            this.color = color;
            this.series = series;
        }

        public String getTitle() {
            return title;
        }

        public Color getColor() {
            return color;
        }

        public XYSeries getSeries() {
            return series;
        }

        public void addValue( double time, double value ) {
            series.add( time, value );
            notifyDataAdded();
        }

        private ArrayList listeners = new ArrayList();

        public void removeListener( Listener listener ) {
            listeners.remove( listener );
        }

        public static interface Listener {
            void dataAdded();
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyDataAdded() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.dataAdded();
            }
        }
    }

}
