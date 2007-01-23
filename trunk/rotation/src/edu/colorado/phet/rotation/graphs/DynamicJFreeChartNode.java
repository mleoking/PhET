package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

        public DynamicJFreeChartNode getDynamicJFreeChartNode() {
            return dynamicJFreeChartNode;
        }

        public XYSeries getSeries() {
            return seriesData.getSeries();
        }
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

        private PNode root = new PNode();
        private PhetPPath pathNode;
        private PClip pathClip;

        public PiccoloSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );

            pathClip = new PClip();
            pathClip.setStrokePaint( null );//set to non-null for debugging clip area
//            pathClip.setStrokePaint( Color.blue );//set to non-null for debugging clip area
            root.addChild( pathClip );
            pathClip.setPathTo( new Rectangle( -10000, -10000, 20000, 20000 ) );

            pathNode = new PhetPPath( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f ), seriesData.getColor() );
            pathClip.addChild( pathNode );
        }

        public void updateSeriesGraphic() {
            GeneralPath path = new GeneralPath();
            if( super.getSeries().getItemCount() > 0 ) {
                Point2D d = getNodePoint( 0 );
                path.moveTo( (float)d.getX(), (float)d.getY() );
                for( int i = 1; i < getSeries().getItemCount(); i++ ) {
                    Point2D nodePoint = getNodePoint( i );
                    path.lineTo( (float)nodePoint.getX(), (float)nodePoint.getY() );
                }
            }
            pathNode.setPathTo( path );
        }

        public Point2D.Double getPoint( int i ) {
            return new Point2D.Double( getSeries().getX( i ).doubleValue(), getSeries().getY( i ).doubleValue() );
        }

        public Point2D getNodePoint( int i ) {
            return getDynamicJFreeChartNode().plotToNode( getPoint( i ) );
        }

        public void setClip( Rectangle2D clip ) {
            pathClip.setPathTo( clip );
        }

        public void uninstall() {
            super.getDynamicJFreeChartNode().removeChild( root );
        }

        public void install() {
            getDynamicJFreeChartNode().addChild( root );
        }

        public void dataAdded() {
            updateSeriesGraphic();
        }

    }

    static class BufferedSeriesView extends SeriesView {

        public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
            dynamicJFreeChartNode
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
        addAllSeriesViews();
    }

    private void addAllSeriesViews() {
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
