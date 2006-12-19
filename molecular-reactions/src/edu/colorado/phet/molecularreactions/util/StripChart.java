package edu.colorado.phet.molecularreactions.util;
/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.util.EventChannel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;

import java.awt.*;
import java.util.EventListener;

/**
 * StripChart
 * <p/>
 * A scrolling strip chart implemented with JFreeChart objects.
 * <p/>
 * The StripChart buffers data so that it can retain more data than is shows at
 * any one time. This allows it to be scrolled back and forth through the
 * collected data. A circular buffer implemented in arrays is used.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChart implements Resetable {

    private XYSeries[] series;
    private JFreeChart chart;
    private double xAxisRange;
    private XYLineAndShapeRenderer renderer;
    private XYPlot plot;

    private int buffSize = 2;
    private double[][] buffer;
    private int clockBufferIdx;
    private int buffHead = 0;
    private int buffTail = buffSize - 1;
    private boolean recording;
    private double t0;

    /**
     * @param title
     * @param seriesNames
     * @param xAxisLabel
     * @param yAxisLabel
     * @param orienation
     * @param xAxisRange
     * @param minY
     * @param maxY
     */
    public StripChart( String title,
                       String[] seriesNames,
                       String xAxisLabel,
                       String yAxisLabel,
                       PlotOrientation orienation,
                       double xAxisRange,
                       double minY,
                       double maxY ) {
        this( title, seriesNames, xAxisLabel, yAxisLabel, orienation, xAxisRange, minY, maxY, 1 );
    }


    /**
     * @param title
     * @param seriesNames
     * @param xAxisLabel
     * @param yAxisLabel
     * @param orienation
     * @param xAxisRange
     * @param minY
     * @param maxY
     * @param buffSize
     */
    public StripChart( String title,
                       String[] seriesNames,
                       String xAxisLabel,
                       String yAxisLabel,
                       PlotOrientation orienation,
                       double xAxisRange,
                       double minY,
                       double maxY,
                       int buffSize ) {
        this.xAxisRange = xAxisRange;
        this.buffSize = buffSize;
        buffTail = buffSize - 1;

        series = new XYSeries[seriesNames.length];
        buffer = new double[seriesNames.length + 1][buffSize];
        clockBufferIdx = seriesNames.length;
        XYSeriesCollection dataset = new XYSeriesCollection();
        for( int i = 0; i < series.length; i++ ) {
            series[i] = new XYSeries( seriesNames[i] );
            dataset.addSeries( series[i] );
        }

        chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                orienation,
                true,
                true,
                false
        );
//        chart.getXYPlot().setDomainGridlinesVisible( false );
//        chart.getXYPlot().setRangeGridlinesVisible( false );
        chart.getLegend().setHorizontalAlignment( HorizontalAlignment.RIGHT );
        plot = (XYPlot)chart.getPlot();
        plot.getRangeAxis().setRange( minY, maxY );

        renderer = new XYLineAndShapeRenderer();
        for( int i = 0; i < series.length; i++ ) {
            renderer.setSeriesLinesVisible( i, true );
            renderer.setSeriesShapesVisible( i, false );
        }
        renderer.setSeriesLinesVisible( series.length, false );
        renderer.setSeriesShapesVisible( series.length, false );
        renderer.setToolTipGenerator( new StandardXYToolTipGenerator() );
        renderer.setDefaultEntityRadius( 6 );
        plot.setRenderer( renderer );

        // Set the minimum X value. This also sets the x range
        setMinX( 0 );
    }

    public void setSeriesPaint( int seriesNum, Paint paint ) {
        renderer.setSeriesPaint( seriesNum, paint );
    }

    public void setStroke( Stroke stroke ) {
        renderer.setStroke( stroke );
    }

    public void addData( double t, double[] y ) {
        if( recording ) {
            double tRec = t - t0;
            for( int seriesNum = 0; seriesNum < series.length; seriesNum++ ) {
                series[seriesNum].add( tRec, y[seriesNum] );
                buffer[seriesNum][buffHead] = y[seriesNum];
            }
            buffer[clockBufferIdx][buffHead] = tRec;

            XYPlot plot = (XYPlot)chart.getPlot();
            double minX = Math.max( buffer[clockBufferIdx][buffHead] - xAxisRange, 0 );
            double maxX = Math.max( buffer[clockBufferIdx][buffHead], xAxisRange );
            plot.getDomainAxis().setRange( minX, maxX );

            buffHead = ( buffHead + 1 ) % buffSize;
            if( buffHead == buffTail ) {
                buffTail = ( buffTail + 1 ) % buffSize;
            }
            listenerProxy.dataChanged();
        }
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void setMinX( double x ) {
        XYPlot plot = (XYPlot)chart.getPlot();
        double minX = Math.max( 0, Math.min( x, getMaxTime() - xAxisRange ));
        double maxX = Math.max( xAxisRange, Math.min( x + xAxisRange, getMaxTime() ));
        plot.getDomainAxis().setRange( minX, maxX );
    }

    private double getMaxTime() {
        int newestIdx = ( buffHead + buffSize - 1 ) % buffSize;
        return buffer[clockBufferIdx][newestIdx];
    }

    private double getMinTime() {
        return buffer[clockBufferIdx][buffTail];
    }

    /**
     * Set the vertical range of the chart
     *
     * @param minY
     * @param maxY
     */
    public void setYRange( int minY, int maxY ) {
        plot.getRangeAxis().setRange( minY, maxY );
    }

    /**
     * Get the maximum x value in the strip chart buffer
     *
     * @return the maximum x value in the data buffer
     */
    public double getMaxX() {
        return getMaxTime();
    }

    /**
     * Get the minimum x value in the strip chart buffer
     *
     * @return the minimum x value in the data buffer
     */
    public double getMinX() {
        return getMinTime();
    }

    public double getViewableRangeX() {
        Range range = getChart().getXYPlot().getDomainAxis().getRange();
        return range.getLength();
    }

    /**
     * Causes the chart to start recording
     */
    public void startRecording( double t0 ) {        
        recording = true;
        this.t0 = t0;
    }

    public void stopRecording() {
        recording = false;
    }

    public void reset() {
        recording = false;
        buffHead = 0;
        buffTail = buffSize - 1;
        for( int seriesNum = 0; seriesNum < series.length; seriesNum++ ) {
            series[seriesNum].clear();
        }
        setMinX( 0 );
    }

    public boolean isRecording() {
        return recording;
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    public interface Listener extends EventListener {
        void dataChanged();
    }

    private EventChannel eventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        eventChannel.removeListener( listener );
    }


}
