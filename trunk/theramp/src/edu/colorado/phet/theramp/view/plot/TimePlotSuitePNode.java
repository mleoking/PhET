/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.plot;

import edu.colorado.phet.common.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.common.LucidaSansFont;
import edu.colorado.phet.theramp.common.Range2D;
import edu.colorado.phet.theramp.view.RampFontSet;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 2:17:06 PM
 * Copyright (c) Aug 2, 2005 by Sam Reid
 */

public class TimePlotSuitePNode extends PNode {
    private RampModule module;
    private PSwingCanvas pCanvas;
    private Range2D range;
    private TimeSeriesModel timeSeriesModel;
    private XYDataset dataset;
    private XYPlot plot;
    private BufferedImage bufferedImage;
    private PImage chartGraphic;
    private JFreeChart chart;
    private int chartHeight;
    private PPath cursor;
    private PNode minButNode;
    private PNode maxButNode;
    private ArrayList series = new ArrayList();
    private boolean minimized = false;
    private ArrayList listeners = new ArrayList();

//    public static final int DEFAULT_CHART_WIDTH = 500;
    public static final int DEFAULT_CHART_WIDTH = 535;
    private int chartWidth = DEFAULT_CHART_WIDTH;
    private PSwing zoomInGraphic;
    private PSwing zoomOutGraphic;
    private int zoomButtonHeight = 17;

    public TimePlotSuitePNode( RampModule module, PSwingCanvas pCanvas, Range2D range, String name, final TimeSeriesModel timeSeriesModel, int height ) {
        this.module = module;
        this.pCanvas = pCanvas;
        this.range = range;
        this.chartHeight = height;
        this.timeSeriesModel = timeSeriesModel;
        dataset = createDataset();
        chart = createChart( range, dataset, name );
        this.plot = (XYPlot)chart.getPlot();
        chartGraphic = new PImage();
        chartGraphic.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        updateChartBuffer();

        addChild( chartGraphic );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                cursor.setVisible( true );
                updateCursorLocation();
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

//        JButton minBut = new JButton( "Minimize" );
        JButton minBut = null;
        try {
            minBut = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/min15.jpg" ) ) );
            minBut.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setMinimized( true );
                }

            } );
            minBut.setMargin( new Insets( 2, 2, 2, 2 ) );
            minButNode = new PSwing( pCanvas, minBut );
            minButNode.setOffset( 1, 1 );
            addChild( minButNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        JButton maximize = new JButton( name + " Graph" );
        maximize.setFont( RampFontSet.getFontSet().getNormalButtonFont() );
        minBut.setMargin( new Insets( 2, 2, 2, 2 ) );
        maximize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
        maxButNode = new PSwing( pCanvas, maximize );
        addChild( maxButNode );

        double maxVisibleRange = getMaxRangeValue();

        try {
            final ZoomButton zoomIn = new ZoomButton( new ImageIcon( loadZoomInImage() ),
                                                      -5000, -500, 100, maxVisibleRange * 2, maxVisibleRange, "Zoom In" );

            zoomInGraphic = new PSwing( pCanvas, zoomIn );
            addChild( zoomInGraphic );

            final ZoomButton zoomOut = new ZoomButton( new ImageIcon( loadZoomOutImage() ),
                                                       5000, 500, 100, maxVisibleRange * 2, maxVisibleRange, "Zoom Out" );
            zoomOut.addListener( new ZoomButton.Listener() {
                public void zoomChanged() {
                    double rangeY = zoomOut.getValue();
                    setChartRange( 0, -rangeY, 20, rangeY );
                    zoomIn.setValue( rangeY );
                }
            } );
            zoomOutGraphic = new PSwing( pCanvas, zoomOut );
            addChild( zoomOutGraphic );

            zoomIn.addListener( new ZoomButton.Listener() {
                public void zoomChanged() {
                    double rangeY = zoomIn.getValue();
                    setChartRange( 0, -rangeY, 20, rangeY );
                    zoomOut.setValue( rangeY );
                }
            } );
//            this.zoomInButton.setOffset( getDataArea().getX(), getDataArea().getY() );
//            this.zoomOutGraphic.setOffset( zoomInButton.getOffset().getX(), zoomInButton.getOffset().getY() + zoomInButton.getFullBounds().getHeight() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        invalidateLayout();

//        maxButNode.setVisible( false );
        forceMinimized( false );
    }

    private BufferedImage loadZoomInImage() throws IOException {
        return BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" ), zoomButtonHeight );
    }

    private BufferedImage loadZoomOutImage() throws IOException {
        return BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" ), zoomButtonHeight );
    }

    private double getMaxRangeValue() {
        return plot.getRangeAxis().getUpperBound();
    }

    private void setChartRange( int tMin, double yMin, int tMax, double yMax ) {
        Range2D range = new Range2D( tMin, yMin, tMax, yMax );
        this.range = range;
        plot.getRangeAxis().setRange( yMin, yMax );
        plot.getDomainAxis().setRange( tMin, tMax );
        updateChartBuffer();
        repaintAll();
    }

    private void updateCursorLocation() {
        double time = timeSeriesModel.getPlaybackTime();
        Point2D imageLoc = toImageLocation( time, 0 );
        cursor.setOffset( imageLoc );
    }

    public void setMinimized( boolean minimized ) {
        if( this.minimized != minimized ) {
            this.minimized = minimized;
            forceMinimized( minimized );
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.minimizeStateChanged();
            }
        }
    }

    private void setHasChild( boolean hasChild, PNode child ) {
        if( hasChild && !this.isAncestorOf( child ) ) {
            addChild( child );
        }
        else if( !hasChild && this.isAncestorOf( child ) ) {
            removeChild( child );
        }

    }

    private void forceMinimized( boolean minimized ) {

        setHasChild( !minimized, chartGraphic );
        setHasChild( !minimized, cursor );
        setHasChild( minimized, maxButNode );
        setHasChild( !minimized, minButNode );
        setHasChild( !minimized, zoomInGraphic );
        setHasChild( !minimized, zoomOutGraphic );

        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode node = (TimeSeriesPNode)series.get( i );
            setHasChild( !minimized, node.getReadoutGraphic() );
        }
        updateCursor();
        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode node = (TimeSeriesPNode)series.get( i );
            node.getReadoutGraphic().setVisible( !minimized );
        }
    }

    public boolean isMinimized() {
        return minimized;
    }

    private void showCursor() {
        cursor.setVisible( true );
    }

    protected void paint( PPaintContext paintContext ) {
//        System.out.println( "paintContext.getScale() = " + paintContext.getScale() );
//        paintContext.getGraphics().scale( 1.0/paintContext.getScale(),1.0/paintContext.getScale());
        super.paint( paintContext );
    }

    private void hideCursor() {
        cursor.setVisible( false );
    }

    public void setChartSize( int chartWidth, int chartHeight ) {
        if( this.chartWidth != chartWidth || this.chartHeight != chartHeight ) {
            this.chartWidth = chartWidth;
            this.chartHeight = chartHeight;
            repaintAll();
            updateCursor();
            invalidateLayout();
        }
    }

    protected void layoutChildren() {
        super.layoutChildren();
        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
            readoutGraphic.setOffset( getDataArea().getX() + 5, getDataArea().getY() + getDataArea().getHeight() / 2.0 + ( readoutGraphic.getFullBounds().getHeight() + 1 ) * i );
        }

        zoomInGraphic.setOffset( getDataArea().getX(), getDataArea().getY() );
        zoomOutGraphic.setOffset( zoomInGraphic.getOffset().getX(), zoomInGraphic.getOffset().getY() + zoomInGraphic.getFullBounds().getHeight() );

        System.out.println( System.currentTimeMillis() + ", Layout Children" );
    }

    private void updateCursor() {
        updateCursorSize();
        updateCursorLocation();
    }

    private void updateCursorSize() {
        Rectangle2D d = getDataArea();
        int cursorWidth = 6;
        cursor.setPathTo( new Rectangle2D.Double( -cursorWidth / 2, -d.getHeight() / 2, cursorWidth, d.getHeight() ) );
    }

    private void updateChartBuffer() {
        bufferedImage = chart.createBufferedImage( chartWidth, chartHeight );
        decorateBuffer();

        chartGraphic.setImage( bufferedImage );
    }

    private void decorateBuffer() {
        drawInPlotAxis();
        drawBorder( bufferedImage );
    }

    private void drawInPlotAxis() {
        Graphics2D g2 = bufferedImage.createGraphics();
        for( int t = 6; t <= 20; t += 2 ) {
            Point2D imagLoc = toImageLocation( t, 0 );
            PText text = new PText( "" + t );
            text.setOffset( imagLoc.getX() - text.getWidth() / 2, imagLoc.getY() );
            text.setFont( new LucidaSansFont( 10 ) );
            text.fullPaint( new PPaintContext( g2 ) );
        }
    }

    private void drawBorder( BufferedImage bufferedImage ) {
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setStroke( new BasicStroke() );
        graphics2D.setColor( Color.black );
        graphics2D.drawRect( 0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1 );
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createXYLineChart( "",
                                                           "", // x-axis label
                                                           "", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, false, false, false );

        chart.setBackgroundPaint( Color.lightGray );

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );
//        plot.setAxisOffset( new RectangleInsets( 5.0, 5.0, 5.0, 5.0 ) );

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRange( false );
        xAxis.setRange( range.getMinX(), range.getMaxX() );
        xAxis.setTickLabelsVisible( false );
        plot.setDomainAxis( xAxis );

//        NumberAxis yAxis = new NumberAxis( title + " (Joules)" );
        NumberAxis yAxis = new NumberAxis( title );
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
        PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
//        readoutGraphic.setOffset( getDataArea().getX() + 5, getDataArea().getY() + getDataArea().getHeight() / 2.0 + ( readoutGraphic.getHeight() + 1 ) * series.size() + 5 );
        series.add( timeSeriesPNode );
        addChild( readoutGraphic );
        invalidateLayout();
    }

    public void reset() {
        updateChartBuffer();
//        System.out.println( "reset image" );
    }

    public BufferedImage getChartImage() {
        return bufferedImage;
    }

    public Rectangle2D getDataArea() {
        Rectangle2D r = plot.getDataArea();
        if( r == null ) {
            throw new RuntimeException( "Null data area." );
        }
        else {
            return r;
        }
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
            chartGraphic.repaintFrom( new PBounds( bounds ), chartGraphic );
        }
    }

    public void repaintAll() {
        reset();
        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            timeSeriesPNode.repaintAll();
        }
    }

    public RampModule getRampModule() {
        return module;
    }

    public double getButtonHeight() {
        return minButNode.getHeight();
    }

    public double getVisibleHeight() {
        if( isMinimized() ) {
            return getButtonHeight();
        }
        else {
            return getFullBounds().getHeight();
        }

    }

    public static interface Listener {
        void minimizeStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
