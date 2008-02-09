/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 11:33:35 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class BetaChart {
    private XYSeries meanSeries;
    private XYSeriesCollection xySeriesCollection;
    private NumberAxis domainAxis;
    private NumberAxis rangeAxis;
    private XYLineAndShapeRenderer renderer;
    private XYPlot xyPlot;
    private JFreeChart chart;
    private int maxItemCount;
    private XYSeries lineSeries;
    private XYSeries bestFitSeries;

    public BetaChart( int maxItemCount ) {
        this.maxItemCount = maxItemCount;

        meanSeries = new XYSeries( "Raw Data" );
        lineSeries = new XYSeries( "Mean Values" );
        bestFitSeries = new XYSeries( "Best Fit" );
        xySeriesCollection = new XYSeriesCollection( meanSeries );
        xySeriesCollection.addSeries( lineSeries );
        xySeriesCollection.addSeries( bestFitSeries );

        domainAxis = new NumberAxis( "ln[(c-r)/c]" );
        rangeAxis = new NumberAxis( "ln(Order Parameter)" );
        renderer = new XYLineAndShapeRenderer();

        int RAW_DATA_SERIES = 0;
        int LINE_SERIES = 1;
        int BEST_FIT_SERIES = 2;
//        renderer.setSeriesStroke( SERIES, new BasicStroke( 2 ) );
        renderer.setSeriesShape( RAW_DATA_SERIES, new Ellipse2D.Double( -3, -3, 6, 6 ) );
        renderer.setSeriesShape( LINE_SERIES, new Rectangle( -4, -4, 8, 8 ) );
//        int RAW_SERIES = 0;
//        renderer.setSeriesLinesVisible( RAW_SERIES, false );
        renderer.setSeriesLinesVisible( RAW_DATA_SERIES, false );
        renderer.setSeriesShapesVisible( RAW_DATA_SERIES, true );
        Color blue = new Color( 0, 0, 255, 14 );
//        renderer.setSeriesFillPaint( SERIES, paint );
//        renderer.setBaseFillPaint( paint );

//        renderer.setBasePaint( blue );
//        renderer.setBaseItemLabelPaint( blue );
//        renderer.setBaseOutlinePaint( blue );
//        renderer.setOutlinePaint( blue );
        renderer.setSeriesPaint( RAW_DATA_SERIES, blue );
        renderer.setSeriesItemLabelPaint( RAW_DATA_SERIES, blue );
        renderer.setSeriesOutlinePaint( RAW_DATA_SERIES, blue );


        renderer.setSeriesLinesVisible( LINE_SERIES, true );


        renderer.setSeriesLinesVisible( BEST_FIT_SERIES, true );
        renderer.setSeriesShapesVisible( BEST_FIT_SERIES, false );
        renderer.setSeriesStroke( BEST_FIT_SERIES, new BasicStroke( 2 ) );
        renderer.setSeriesPaint( BEST_FIT_SERIES, Color.green );
//        renderer.setBaseStroke( );
//        renderer.setSeriesStroke(0, null);

//        renderer.set
//        renderer.setSeriesShapesVisible( SERIES, false );

        xyPlot = new XYPlot( xySeriesCollection,
                             domainAxis,
                             rangeAxis, renderer );
        chart = new JFreeChart( "Critical Exponent Log Plot", xyPlot );
        rangeAxis.setAutoRange( true );

//        rangeAxis.setRange( -0.01, 1.01 );
        rangeAxis.setAutoRangeIncludesZero( false );

        domainAxis.setAutoRange( true );
//        domainAxis.setRange( 0 - 0.01, Math.PI * 2 + 0.01 );
        domainAxis.setAutoRangeIncludesZero( false );

//        domainAxis.setRange( -0.01, Math.PI * 2 + 0.01 );


    }

    public JFreeChart getChart() {
        return chart;
    }


    public void addDataPoint( double eta, double orderParameter ) {
        meanSeries.add( eta, orderParameter );
        while( meanSeries.getItemCount() > maxItemCount ) {
//            changeToScrolling();
            meanSeries.remove( 0 );
        }
        domainAxis.configure();
        updateLines();
    }

    private void updateLines() {
        MyMultiMap multiMap = new MyMultiMap();
        for( int i = 0; i < meanSeries.getItemCount(); i++ ) {
            XYDataItem item = meanSeries.getDataItem( i );
            multiMap.add( item.getX(), item.getY() );
        }
        Set keySet = multiMap.keySet();
        ArrayList lits = new ArrayList( keySet );
        Collections.sort( lits, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                Number a = (Number)o1;
                Number b = (Number)o2;
                return Double.compare( a.doubleValue(), b.doubleValue() );
            }
        } );
        lineSeries.clear();
        for( int i = 0; i < lits.size(); i++ ) {
            Number key = (Number)lits.get( i );
            int numValues = multiMap.numValues( key );
            if( numValues > 14 ) {
                lineSeries.add( key.doubleValue(), average( multiMap.getList( key ) ) );
            }
        }
    }

    private double average( List list ) {
        double sum = 0;
        for( int i = 0; i < list.size(); i++ ) {
            Number number = (Number)list.get( i );
            sum += number.doubleValue();
        }
        return sum / list.size();
    }

//    private void changeToScrolling() {
//        if( !changedToScrolling ) {
//            changedToScrolling = true;
//            domainAxis.setAutoRange( true );
//            domainAxis.setAutoRangeIncludesZero( false );
//        }
//    }

    public void reset() {
        lineSeries.clear();
        meanSeries.clear();
        bestFitSeries.clear();
    }

    public XYSeries getMeanDataSet() {
        return meanSeries;
    }

    public void showLine( Point2D lhs, Point2D rhs ) {
        bestFitSeries.clear();
        bestFitSeries.add( lhs.getX(), lhs.getY() );
        bestFitSeries.add( rhs.getX(), rhs.getY() );
    }
}
