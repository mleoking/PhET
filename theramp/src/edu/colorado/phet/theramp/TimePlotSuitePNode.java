/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 2:17:06 PM
 * Copyright (c) Aug 2, 2005 by Sam Reid
 */

public class TimePlotSuitePNode extends PNode {
    private PCanvas pCanvas;
    private Range2D range;
    private TimeSeriesModel timeSeriesModel;
    private XYDataset dataset;
    private XYPlot plot;
    private BufferedImage bufferedImage;
    private PImage child;
    private JFreeChart chart;
    private int chartHeight;
    private PPath cursor;

    public TimePlotSuitePNode( PCanvas pCanvas, Range2D range, String name, final TimeSeriesModel timeSeriesModel, int height ) {
        this.pCanvas = pCanvas;
        this.range = range;
        this.chartHeight = height;
        this.timeSeriesModel = timeSeriesModel;
        dataset = createDataset();
        chart = createChart( range, dataset, name );
        this.plot = (XYPlot)chart.getPlot();
        child = new PImage();
        updateImage();
        addChild( child );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                cursor.setVisible( true );
                double time = timeSeriesModel.getPlaybackTime();
                Point2D imageLoc = toImageLocation( time, 0 );
                cursor.setOffset( imageLoc );
            }
        } );
        timeSeriesModel.addListener( new TimeSeriesModelListenerAdapter() {
            public void recordingStarted() {
                hideCursor();
            }

            public void recordingPaused() {
                showCursor();
            }

            public void recordingFinished() {
                showCursor();
            }

            public void playbackStarted() {
                showCursor();
            }

            public void playbackPaused() {
                showCursor();
            }

            public void playbackFinished() {
                showCursor();
            }

            public void reset() {
                hideCursor();
            }

            public void rewind() {
                showCursor();
            }
        } );
        Rectangle2D d = getDataArea();
        int cursorWidth = 6;
        cursor = new PPath( new Rectangle2D.Double( -cursorWidth / 2, -d.getHeight() / 2, cursorWidth, d.getHeight() ) );
        cursor.setVisible( false );
        cursor.setStroke( new BasicStroke( 1 ) );
        cursor.setPaint( new Color( 0, 0, 0, 0 ) );

        cursor.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                double viewTime = event.getPosition().getX();
                Point2D out = toLinearFunction().getInverseTransform().transform( new Point2D.Double( viewTime, 0 ), null );

                double t = out.getX();
                if( t < 0 ) {
                    t = 0;
                }
                else if( t > timeSeriesModel.getRecordTime() ) {
                    t = timeSeriesModel.getRecordTime();
                }
                timeSeriesModel.setReplayTime( t );
                System.out.println( "out = " + out );
//                double dx = event.getDelta().getWidth();

            }
        } );
//        cursor.setStroke( new BasicStroke( 1,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,1.0f,new float[]{8,4},0) );
        cursor.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        addChild( cursor );
    }

    private void showCursor() {
        cursor.setVisible( true );
    }

    private void hideCursor() {
        cursor.setVisible( false );
    }

    private void updateImage() {
        bufferedImage = chart.createBufferedImage( 800, chartHeight );
        child.setImage( bufferedImage );
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createXYLineChart( title,
                                                           "", // x-axis label
                                                           "", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, false, false, false );

        chart.setBackgroundPaint( Color.lightGray );

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );
        plot.setAxisOffset( new RectangleInsets( 5.0, 5.0, 5.0, 5.0 ) );

        NumberAxis xAxis = new NumberAxis( "Time (seconds)" );
        xAxis.setAutoRange( false );
        xAxis.setRange( range.getMinX(), range.getMaxX() );
        plot.setDomainAxis( xAxis );

        NumberAxis yAxis = new NumberAxis( "Energy (Joules)" );
        yAxis.setAutoRange( false );
        yAxis.setRange( range.getMinY(), range.getMaxY() );
        plot.setRangeAxis( yAxis );

        plot.setDomainCrosshairVisible( true );
        plot.setRangeCrosshairVisible( true );

        return chart;
    }

    private static XYDataset createDataset() {
        XYSeries xySeries = new XYSeries( new Integer( 0 ) );
        XYDataset xyDataset = new XYSeriesCollection( xySeries );
        return xyDataset;
    }

    public void addTimeSeries( TimeSeriesPNode timeSeriesPNode ) {
    }

    public void reset() {
        updateImage();
//        System.out.println( "reset image" );
    }

    public BufferedImage getChartImage() {
        return bufferedImage;
    }

    public Rectangle2D getDataArea() {
        return plot.getDataArea();
    }

    public LinearTransform2D toLinearFunction() {
        Point2D modelPoint0 = new Point2D.Double( 0, 0 );
        Point2D modelPoint1 = new Point2D.Double( 1, 1 );
        Point2D viewPt1 = toImageLocation( modelPoint0.getX(), modelPoint0.getY() );
        Point2D viewPt2 = toImageLocation( modelPoint1.getX(), modelPoint1.getY() );
        return new LinearTransform2D( modelPoint0, modelPoint1, viewPt1, viewPt2 );
    }

    public Point2D toImageLocation( double x, double y ) {
        Rectangle2D dataArea = plot.getDataArea();
        if( dataArea == null ) {
            throw new RuntimeException( "Null data area" );
        }

        double transX1 = plot.getDomainAxisForDataset( 0 ).valueToJava2D( x, dataArea, plot.getDomainAxisEdge() );
        double transY1 = plot.getRangeAxisForDataset( 0 ).valueToJava2D( y, dataArea, plot.getRangeAxisEdge() );
        Point2D.Double pt = new Point2D.Double( transX1, transY1 );
        return pt;
    }

    public void repaintImage( Rectangle2D bounds ) {
        if( bounds.intersects( getDataArea() ) ) {
            child.repaintFrom( new PBounds( bounds ), child );
        }
    }
}
