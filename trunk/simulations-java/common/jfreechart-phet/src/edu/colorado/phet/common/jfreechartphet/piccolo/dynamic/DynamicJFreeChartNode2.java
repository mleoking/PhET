package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;

import org.jfree.chart.JFreeChart;

/**
 * This class extends the functionality of JFreeChartNode by providing different strategies for rendering the data.
 * It is assumed that the chart's plot is XYPlot, and some functionality is lost in rendering, since we
 * have our own rendering strategies here.  Also, the supplied chart's data is not rendered; only the data
 * set explicitly with addValue() to this DynamicJFreeChartNode.
 * <p/>
 * Data is added to the chart through addValue() methods, not through the underlying XYSeriesCollection dataset.
 * <p/>
 * The 3 rendering styles are:
 * 1. JFreeChart renderer: This uses the JFreeChart subsystem to do all the data rendering.
 * This looks beautiful and comes built-in to jfreechart but has performance problems during dynamic data display, since the entire region
 * is repainted whenever a single point changes.
 * <p/>
 * 2. Piccolo renderer: This uses a PPath to render the path as a PNode child of the JFreeChartNode
 * This looks fine and reduces the clip region necessary for painting.  When combined with a buffered jfreechartnode, this can improve computation substantially.
 * This renderer is combined with a PClip to ensure no data is drawn outside the chart's data area (which could otherwise change the fullbounds of the jfreechartnode..
 * <p/>
 * 3. Buffered renderer: This draws directly to the buffer in the JFreeChartNode, and only repaints the changed region of the screen.
 * <p/>
 * 4. Buffered Immediate: This draws directly to the buffer,
 * and immediately repaints the dirty region so that multiple regions don't accumulate in the RepaintManager.
 *
 * @author Sam Reid
 * @version $Revision: 15634 $
 */
public class DynamicJFreeChartNode2 extends JFreeChartNode {
    private ArrayList seriesDataList = new ArrayList();//of type SeriesData
    private ArrayList seriesViewList = new ArrayList();//of type SeriesView
    private PhetPCanvas phetPCanvas;
    private PhetPPath debugBufferRegion;//internal debugging tool for deciphering screen output regions

    //The default SeriesView is JFreeChart rendering.
//    private DynamicJFreeChartNode2.SeriesViewFactory viewFactory = new DynamicJFreeChartNode2.SeriesViewFactory() {
//        public SeriesView createSeriesView( DynamicJFreeChartNode2 dynamicJFreeChartNode, SeriesData seriesData ) {
//            return new JFreeChartSeriesView( dynamicJFreeChartNode, seriesData );
//        }
//    };


    public DynamicJFreeChartNode2( PhetPCanvas phetPCanvas, JFreeChart chart ) {
        super( chart );
        this.phetPCanvas = phetPCanvas;
        debugBufferRegion = new PhetPPath( new BasicStroke( 1.0f ), Color.green );
//        addChild( debugBufferRegion );//this can destroy the bounds of the graph, use with care
    }

    /**
     * Adds the specified (x,y) pair to the 0th series in this plot.
     *
     * @param x the x-value to add
     * @param y the y-value to add
     */
    public void addValue( double x, double y ) {
        addValue( 0, x, y );
    }

    /**
     * Adds the specified (x,y) pair to the specified series.
     *
     * @param series the series to which data should be added.  This series should have already been added to this dynamic jfreechartnode with addSeries().
     * @param x      the x-value to add
     * @param y      the y-value to add
     */
    public void addValue( int series, double x, double y ) {
        getSeries( series ).addValue( x, y );
    }

    /**
     * Adds a new series to this chart for plotting, with the given name and color.
     *
     * @param title the title for the series
     * @param color the series' color
     */
    public void addSeries( String title, Color color ) {
        SeriesData seriesData = new SeriesData( title, color );
        seriesDataList.add( seriesData );
        updateSeriesViews();
    }

    /**
     * Empties each series associated with this chart.
     */
    public void clear() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            seriesData.clear();
        }
        clearBufferAndRepaint();
    }

    private SeriesData getSeries( int series ) {
        return (SeriesData)seriesDataList.get( series );
    }

    /**
     * Sets an arbitrary (possibly user-defined) view style.
     *
     * @param factory
     */
    public void setViewFactory( DynamicJFreeChartNode2.SeriesViewFactory factory ) {
//        viewFactory = factory;
        updateSeriesViews();
    }

    private void updateSeriesViews() {
        removeAllSeriesViews();
        clearBufferAndRepaint();
//        addAllSeriesViews();
        updateChartRenderingInfo();
    }

    private void removeAllSeriesViews() {
        while( seriesViewList.size() > 0 ) {
            SeriesView seriesView = (SeriesView)seriesViewList.get( 0 );
            seriesView.uninstall();
            seriesViewList.remove( seriesView );
        }
    }

//    public void setBuffered( boolean buffered ) {
//        super.setBuffered( buffered );
//        updateSeriesViews();
//    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public void setDebugBufferRegionPath( Rectangle2D bounds ) {
        debugBufferRegion.setPathTo( bounds );
    }

    public static interface SeriesViewFactory {
        SeriesView createSeriesView( DynamicJFreeChartNode2 dynamicJFreeChartNode, SeriesData seriesData );
    }

}
