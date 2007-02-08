/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.plots;

import edu.colorado.phet.common.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.common.LucidaSansFont;
import edu.colorado.phet.piccolo.event.CursorHandler;
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
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
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

public class EnergySkaterTimePlotNode extends PNode {
    private PSwingCanvas pCanvas;
    private Range2D range;
    private TimeSeriesModel timeSeriesModel;
    private XYDataset dataset;
    private XYPlot plot;
    private BufferedImage bufferedImage;
    private PImage chartGraphic;
    private JFreeChart chart;
    private int chartHeight;
    private PPath cursorPNode;
    private PNode minButNode;
    private PNode maxButNode;
    private ArrayList series = new ArrayList();
    private boolean minimized = false;
    private ArrayList listeners = new ArrayList();
    public static final int DEFAULT_CHART_WIDTH = 700;
    private int chartWidth = DEFAULT_CHART_WIDTH;
    private PSwing zoomInGraphic;
    private PSwing zoomOutGraphic;
    //    private int zoomButtonHeight = 17;
    private int zoomButtonHeight = -1;
    private int layoutCount = 0;
    private double defaultMaxY;
    private String units;
    private static final int MAX_TIME = 40;
    private ChartRenderingInfo info = new ChartRenderingInfo();
    private boolean useMinButton = false;
    private double fractionUpForZero;

    public EnergySkaterTimePlotNode( PSwingCanvas pCanvas, Range2D range, String name,
                                     String units, final TimeSeriesModel timeSeriesModel, int height, boolean useSlider ) {
        this.fractionUpForZero = Math.abs( range.getMinY() / range.getHeight() );
        this.units = units;
        this.defaultMaxY = range.getMaxY();
        this.pCanvas = pCanvas;
        this.range = range;
        this.chartHeight = height;
        this.timeSeriesModel = timeSeriesModel;
        dataset = createDataset();
        chart = createChart( range, dataset, name + " (" + units + ")" );
        this.plot = (XYPlot)chart.getPlot();
        chartGraphic = new PImage();
//        chartGraphic.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        updateChartBuffer();

        addChild( chartGraphic );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                cursorPNode.setVisible( true );
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
        cursorPNode = new PPath( new Rectangle2D.Double( -cursorWidth / 2, -d.getHeight() / 2, cursorWidth, d.getHeight() ) );
        cursorPNode.setVisible( false );
        cursorPNode.setStroke( new BasicStroke( 1 ) );
        cursorPNode.setPaint( new Color( 0, 0, 0, 0 ) );

        cursorPNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
//                double viewTime = event.getPosition().getX();
                double viewTime = event.getPositionRelativeTo( EnergySkaterTimePlotNode.this ).getX();
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
        cursorPNode.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        addChild( cursorPNode );

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
            if( useMinButton ) {
                addChild( minButNode );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        JButton maximize = new JButton( name + " Graph" );
//        maximize.setFont( RampFontSet.getFontSet().getNormalButtonFont() );
        minBut.setMargin( new Insets( 2, 2, 2, 2 ) );
        maximize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
//        maximize.setBackground( EarthGraphic.earthGreen );
        maxButNode = new PSwing( pCanvas, maximize );
        addChild( maxButNode );

        double maxVisibleRange = getMaxRangeValue();
        double dzPress = maxVisibleRange / 10;
        double dzHold = maxVisibleRange / 20;
        try {
            final ZoomButton zoomIn = new ZoomButton( new ImageIcon( loadZoomInImage() ),
                                                      -dzPress, -dzHold, 100, maxVisibleRange * 4, maxVisibleRange, "Zoom In" );

            zoomInGraphic = new PSwing( pCanvas, zoomIn );
            addChild( zoomInGraphic );

            final ZoomButton zoomOut = new ZoomButton( new ImageIcon( loadZoomOutImage() ),
                                                       dzPress, dzHold, 100, maxVisibleRange * 4, maxVisibleRange, "Zoom Out" );
            zoomOut.addListener( new ZoomButton.Listener() {
                public void zoomChanged() {
                    double rangeY = zoomOut.getValue();
                    double fullRangeY = rangeY * 2;
                    double y0 = fractionUpForZero * fullRangeY;
                    double y1 = fullRangeY - y0;
                    setChartRange( 0, -y0, MAX_TIME, y1 );
                    zoomIn.setValue( rangeY );
                }
            } );
            zoomOutGraphic = new PSwing( pCanvas, zoomOut );
            addChild( zoomOutGraphic );

            zoomIn.addListener( new ZoomButton.Listener() {
                public void zoomChanged() {
//                    double rangeY = zoomIn.getValue();
//                    setChartRange( 0, -rangeY, MAX_TIME, rangeY );
//                    zoomOut.setValue( rangeY );

                    double rangeY = zoomIn.getValue();
                    double fullRangeY = rangeY * 2;
                    double y0 = fractionUpForZero * fullRangeY;
                    double y1 = fullRangeY - y0;
                    setChartRange( 0, -y0, MAX_TIME, y1 );
                    zoomOut.setValue( rangeY );

                }
            } );
//            this.zoomInButton.setOffset( getDataArea().getX(), getDataArea().getY() );
//            this.zoomOutGraphic.setOffset( zoomInButton.getOffset().getX(), zoomInButton.getOffset().getY() + zoomInButton.getFullBounds().getHeight() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

//        if( useSlider ) {
//            slider = createSlider();
//        }
        invalidateLayout();

//        maxButNode.setVisible( false );
        setMinimizedState( false );
//        info = new ChartRenderingInfo();

    }

    public double getTopY() {
        Point2D loc = isMinimized() ? maxButNode.getGlobalFullBounds().getOrigin() :
                      chartGraphic.getGlobalFullBounds().getOrigin();
        pCanvas.getCamera().globalToLocal( loc );
//        pCanvas.getCamera().localToParent( loc );

        return loc.getY();
    }

    public void setSeriesFont( Font font ) {
        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            timeSeriesPNode.setFont( font );
        }
    }

    public void setSeriesPlotShadow( int dx, int dy ) {
        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            timeSeriesPNode.setShadowOffset( dx, dy );
        }
    }

    public void updateReadouts() {
        for( int i = 0; i < series.size(); i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            timeSeriesPNode.updateReadout();
        }
    }

    private BufferedImage loadZoomInImage() throws IOException {
        BufferedImage image = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
        if( zoomButtonHeight > 0 ) {
            return BufferedImageUtils.rescaleYMaintainAspectRatio( image, zoomButtonHeight );
        }
        else {
            return image;
        }
    }

    private BufferedImage loadZoomOutImage() throws IOException {
        BufferedImage image = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
        if( zoomButtonHeight > 0 ) {
            return BufferedImageUtils.rescaleYMaintainAspectRatio( image, zoomButtonHeight );
        }
        else {
            return image;
        }
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

    private void updateGridlines() {
        plot.clearDomainMarkers();

        for( int i = 5; i <= MAX_TIME; i += 5 ) {
            plot.addDomainMarker( new ValueMarker( i, Color.lightGray, new BasicStroke( 1 ) ) );
        }

        for( double y = defaultMaxY / 4; y < plot.getRangeAxis().getRange().getUpperBound(); y += defaultMaxY / 4 ) {
            plot.addRangeMarker( new ValueMarker( y, Color.lightGray, new BasicStroke( 1 ) ) );
        }
        for( double y = -defaultMaxY / 4; y > plot.getRangeAxis().getRange().getLowerBound(); y -= defaultMaxY / 4 ) {
            plot.addRangeMarker( new ValueMarker( y, Color.lightGray, new BasicStroke( 1 ) ) );
        }
    }

    private void updateCursorLocation() {
        double time = timeSeriesModel.getPlaybackTime();
        Point2D imageLoc = toImageLocation( time, getMaxRangeValue() );
        cursorPNode.setOffset( imageLoc );
    }

    public void setMinimized( boolean minimized ) {
        if( this.minimized != minimized ) {
            this.minimized = minimized;
            setMinimizedState( minimized );
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

    private void setMinimizedState( boolean minimized ) {
        setHasChild( !minimized, chartGraphic );
        setHasChild( !minimized, cursorPNode );
        setHasChild( minimized, maxButNode );
        setHasChild( !minimized && useMinButton, minButNode );
        setHasChild( !minimized, zoomInGraphic );
        setHasChild( !minimized, zoomOutGraphic );
//        if( slider != null ) {
//            setHasChild( !minimized, slider );
//        }

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
        cursorPNode.setVisible( true );
    }

    private void hideCursor() {
        cursorPNode.setVisible( false );
    }

    public void setChartSize( int chartWidth, int chartHeight ) {
        if( !( chartWidth > 0 && chartHeight > 0 ) ) {
            throw new RuntimeException( "Illegal chart dimensions: " + chartWidth + ", " + chartHeight );
        }
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

        for( int i = 0; i < series.size() / 2; i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
//            readoutGraphic.setOffset( getDataArea().getX() + 5, getDataArea().getY() + getDataArea().getHeight() / 2.0 + ( readoutGraphic.getFullBounds().getHeight() + 1 ) * i );
            double readoutDY = -2;
            readoutGraphic.setOffset( getDataArea().getX() + 5,
                                      getDataArea().getY() + 4 + ( readoutGraphic.getFullBounds().getHeight() + readoutDY ) * i );
        }

        for( int i = series.size() / 2; i < series.size(); i++ ) {
            TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
            PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
//            readoutGraphic.setOffset( getDataArea().getX() + 5, getDataArea().getY() + getDataArea().getHeight() / 2.0 + ( readoutGraphic.getFullBounds().getHeight() + 1 ) * i );
            double readoutDY = -2;
            int index = i - series.size() / 2;
            readoutGraphic.setOffset( getDataArea().getX() + getDataArea().getWidth() / 2 + 5,
                                      getDataArea().getY() + 4 + ( readoutGraphic.getFullBounds().getHeight() + readoutDY ) * index );
        }

        zoomInGraphic.setOffset( 10 + getDataArea().getMaxX(), getDataArea().getCenterY() - zoomInGraphic.getFullBounds().getHeight() - 2 );
        zoomOutGraphic.setOffset( 10 + getDataArea().getMaxX(), getDataArea().getCenterY() + 2 );

        layoutCount++;
        if( layoutCount > 100 && layoutCount % 25 == 0 ) {
            System.out.println( "layoutCount = " + layoutCount );
        }
        minButNode.setOffset( getDataArea().getMaxX() - minButNode.getFullBounds().getWidth() - 2, 0 + 2 );
    }

    private void updateCursor() {
        updateCursorShape();
        updateCursorLocation();
    }

    private void updateCursorShape() {
        Rectangle2D d = getDataArea();
        int cursorWidth = 6;
        cursorPNode.setPathTo( new Rectangle2D.Double( -cursorWidth / 2, 0, cursorWidth, d.getHeight() ) );
    }

    private void updateChartBuffer() {
        updateGridlines();

        if( chartWidth < 2000 && chartHeight < 2000 ) {
            bufferedImage = chart.createBufferedImage( chartWidth, chartHeight, info );
//            System.out.println( "TimePlotSuitePNode.updateChartBuffer@" + System.currentTimeMillis() );
            decorateBuffer();
            chartGraphic.setImage( bufferedImage );
        }
        else {
            System.out.println( "chartWidth = " + chartWidth );
            System.out.println( "chartHeight = " + chartHeight );
        }
    }

    private void decorateBuffer() {
        drawInPlotAxis();
    }

    private void drawInPlotAxis() {
        Graphics2D g2 = bufferedImage.createGraphics();
        for( int t = 2; t < MAX_TIME; t += 2 ) {
            Point2D imagLoc = toImageLocation( t, 0 );
            PText text = new PText( "" + t );
            text.setOffset( imagLoc.getX() - text.getWidth() / 2, imagLoc.getY() );
            text.setFont( new LucidaSansFont( 10 ) );
            text.fullPaint( new PPaintContext( g2 ) );
        }
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createXYLineChart( "",
                                                           "", // x-axis label
                                                           "", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, false, false, false );

        chart.setBackgroundPaint( new Color( 240, 220, 210 ) );

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinesVisible( false );
        plot.setRangeGridlinesVisible( false );

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRange( false );
        xAxis.setRange( range.getMinX(), range.getMaxX() );
        xAxis.setTickLabelsVisible( false );
        xAxis.setTickMarksVisible( true );
        plot.setDomainAxis( xAxis );

        NumberAxis yAxis = new NumberAxis( title );
        yAxis.setAutoRange( false );
        yAxis.setRange( range.getMinY(), range.getMaxY() );
        yAxis.setLabelFont( new Font( "Lucida Sans", Font.PLAIN, 11 ) );
        plot.setRangeAxis( yAxis );

        plot.setDomainCrosshairVisible( true );
        plot.setRangeCrosshairVisible( true );

        return chart;
    }

    private static XYDataset createDataset() {
        XYSeries xySeries = new XYSeries( new Integer( 0 ) );
        return new XYSeriesCollection( xySeries );
    }

    public void addTimeSeries( TimeSeriesPNode timeSeriesPNode ) {
        PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
        series.add( timeSeriesPNode );
        addChild( readoutGraphic );
        invalidateLayout();
    }

    public void reset() {
        updateChartBuffer();
    }

    public BufferedImage getChartImage() {
        return bufferedImage;
    }

    public Rectangle2D getDataArea() {
        Rectangle2D r = info.getPlotInfo().getDataArea();
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
        Rectangle2D dataArea = getDataArea();
        if( dataArea == null ) {
            throw new RuntimeException( "Null data area" );
        }

        double transX1 = plot.getDomainAxisForDataset( 0 ).valueToJava2D( x, dataArea, plot.getDomainAxisEdge() );
        double transY1 = plot.getRangeAxisForDataset( 0 ).valueToJava2D( y, dataArea, plot.getRangeAxisEdge() );
        return new Point2D.Double( transX1, transY1 );
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
