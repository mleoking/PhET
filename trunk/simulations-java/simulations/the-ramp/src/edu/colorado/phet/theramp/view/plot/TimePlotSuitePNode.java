/*  */
package edu.colorado.phet.theramp.view.plot;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.LinearTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.common.BorderPNode;
import edu.colorado.phet.theramp.common.Range2D;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModel;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModelListenerAdapter;
import edu.colorado.phet.theramp.view.EarthGraphic;
import edu.colorado.phet.theramp.view.RampFontSet;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 2:17:06 PM
 */

public class TimePlotSuitePNode extends PhetPNode {
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
    private int zoomButtonHeight = 17;
    private SliderGraphic slider;
    private int layoutCount = 0;
    private double defaultMaxY;
    private String units;
    private Rectangle2D savedDataArea;
    private boolean dataAreaDirty = true;
    private boolean layoutDirty = true;

    public TimePlotSuitePNode( RampModule module, PSwingCanvas pCanvas, Range2D range, String name,
                               String units, final TimeSeriesModel timeSeriesModel, int height, boolean useSlider ) {
        this.units = units;
        this.defaultMaxY = range.getMaxY();
        this.module = module;
        this.pCanvas = pCanvas;
        this.range = range;
        this.chartHeight = height;
        this.timeSeriesModel = timeSeriesModel;
        dataset = createDataset();
        chart = createChart( range, dataset, name + " (" + units + ")" );
        this.plot = (XYPlot)chart.getPlot();
        chartGraphic = new PImage();
//        chartGraphic.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );//DEC_05
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
                double viewTime = event.getPositionRelativeTo( TimePlotSuitePNode.this ).getX();
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
            }
        } );
        cursorPNode.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        addChild( cursorPNode );
        JButton minBut = null;
        try {
            minBut = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "the-ramp/images/min15.jpg" ) ) );
            minBut.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setMinimized( true );
                }

            } );
            minBut.setMargin( new Insets( 2, 2, 2, 2 ) );
            minButNode = new PSwing( minBut );
            minButNode.setOffset( 1, 1 );
            addChild( minButNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        JButton maximize = new JButton( MessageFormat.format( TheRampStrings.getString( "readout.graph-name" ), new Object[]{name} ) );
        maximize.setFont( RampFontSet.getFontSet().getNormalButtonFont() );
        minBut.setMargin( new Insets( 2, 2, 2, 2 ) );
        maximize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
        maximize.setBackground( EarthGraphic.earthGreen );
        maxButNode = new PSwing( maximize );
        addChild( maxButNode );

        double maxVisibleRange = getMaxRangeValue();
        double dzPress = maxVisibleRange / 10;
        double dzHold = maxVisibleRange / 100;
        try {
            final ZoomButton zoomIn = new ZoomButton( new ImageIcon( loadZoomInImage() ),
                                                      -dzPress, -dzHold, 100, maxVisibleRange * 4, maxVisibleRange, TheRampStrings.getString( "camera.zoom-in" ) );

            zoomInGraphic = new PSwing( zoomIn );
            addChild( zoomInGraphic );

            final ZoomButton zoomOut = new ZoomButton( new ImageIcon( loadZoomOutImage() ),
                                                       dzPress, dzHold, 100, maxVisibleRange * 4, maxVisibleRange, TheRampStrings.getString( "camera.zoom-out" ) );
            zoomOut.addListener( new ZoomButton.Listener() {
                public void zoomChanged() {
                    double rangeY = zoomOut.getValue();
                    setChartRange( 0, -rangeY, RampModule.MAX_TIME, rangeY );
                    zoomIn.setValue( rangeY );
                }
            } );
            zoomOutGraphic = new PSwing( zoomOut );
            addChild( zoomOutGraphic );

            zoomIn.addListener( new ZoomButton.Listener() {
                public void zoomChanged() {
                    double rangeY = zoomIn.getValue();
                    setChartRange( 0, -rangeY, RampModule.MAX_TIME, rangeY );
                    zoomOut.setValue( rangeY );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        if( useSlider ) {
            slider = createSlider();
        }
        invalidateLayout();
        setMinimizedState( false );
    }

    public double getTopY() {
        Point2D loc = isMinimized() ? maxButNode.getGlobalFullBounds().getOrigin() :
                      chartGraphic.getGlobalFullBounds().getOrigin();
        pCanvas.getCamera().globalToLocal( loc );
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

    static class SliderGraphic extends PNode {
        private int width = 25;
        private int insetX = 10;
        private PPath thumb;
        private TimePlotSuitePNode timePlotSuitePNode;
        private Rectangle2D rect;
        private BorderPNode background;
        private BorderPNode track;

        public SliderGraphic( Rectangle2D.Double dataArea, TimePlotSuitePNode timePlotSuitePNode ) {
            background = new BorderPNode( timePlotSuitePNode.pCanvas,
                                          BorderFactory.createLoweredBevelBorder(),
                                          RectangleUtils.toRectangle( dataArea ) );
            Rectangle center = createCenter( dataArea );

            track = new BorderPNode( timePlotSuitePNode.pCanvas,
                                     BorderFactory.createRaisedBevelBorder(),
                                     center );
            addChild( track );
            this.rect = dataArea;
            timePlotSuitePNode.getRampModule().getRampPhysicalModel().addListener( new RampPhysicalModel.Adapter() {
                public void appliedForceChanged() {
                    update();
                }

            } );
            this.timePlotSuitePNode = timePlotSuitePNode;
            thumb = new PPath();
            Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( 23, 0 ), 10, 15, 7 );
            thumb.setPathTo( arrow.getShape() );
            thumb.setPaint( new RampLookAndFeel().getAppliedForceColor() );
            thumb.setStroke( new BasicStroke() );
            thumb.setStrokePaint( Color.black );

            addChild( thumb );
            thumb.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            thumb.addInputEventListener( new ThumbDrag( this ) );
            setPickable( false );
            //but leave children pickable
            setChildrenPickable( true );

            background.setPickable( false );
            background.setChildrenPickable( false );
        }

        private Rectangle createCenter( Rectangle2D dataArea ) {
            Rectangle2D center = new Rectangle2D.Double( dataArea.getX() + dataArea.getWidth() / 2, dataArea.getY(),
                                                         1, dataArea.getHeight() );
            return RectangleUtils.toRectangle( center );
        }

        static class ThumbDrag extends PBasicInputEventHandler {
            private SliderGraphic sliderGraphic;

            public ThumbDrag( SliderGraphic sliderGraphic ) {
                this.sliderGraphic = sliderGraphic;
            }

            public void mouseReleased( PInputEvent event ) {
                super.mouseReleased( event );
            }

            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                Point2D pt = event.getPositionRelativeTo( sliderGraphic );
                LinearTransform2D linearFunction = sliderGraphic.timePlotSuitePNode.toLinearFunction();
                AffineTransform inverse = linearFunction.getInverseTransform();
                Point2D result = inverse.transform( pt, null );

                double y = -result.getY();
                if( y > sliderGraphic.timePlotSuitePNode.getMaxRangeValue() ) {
                    y = sliderGraphic.timePlotSuitePNode.getMaxRangeValue();
                }
                else if( y < sliderGraphic.timePlotSuitePNode.getMinRangeValue() ) {
                    y = sliderGraphic.timePlotSuitePNode.getMinRangeValue();
                }
//                System.out.println( "y = " + y );
                sliderGraphic.timePlotSuitePNode.getRampModule().getRampPhysicalModel().setAppliedForce( y );
            }
        }

        private void update() {
            Point2D loc = timePlotSuitePNode.toImageLocation( 0,
                                                              timePlotSuitePNode.getRampModule().getRampPhysicalModel().getAppliedForceScalar() );
            double y = loc.getY();// - thumb.getFullBounds().getHeight() / 2;
//            System.out.println( "y = " + y );
            if( y < rect.getY() ) {
                y = rect.getY();
            }
            else if( y > rect.getMaxY() ) {
                y = rect.getMaxY();
            }
            thumb.setOffset( rect.getX() + 2, y );
        }

        public void dataAreaChanged( Rectangle2D area ) {
            if( !area.equals( rect ) ) {
                int sliderOffsetDX = 30;
                rect = new Rectangle2D.Double( insetX - sliderOffsetDX, area.getY(), width, area.getHeight() );
                background.setBorderRectangle( RectangleUtils.toRectangle( rect ) );
                track.setBorderRectangle( createCenter( rect ) );
                update();
            }
        }
    }

    private SliderGraphic createSlider() {
        Rectangle2D dataArea = getDataArea( plot );
        Rectangle2D.Double d = new Rectangle2D.Double( dataArea.getX(), dataArea.getY(), dataArea.getWidth(), dataArea.getHeight() );
        SliderGraphic pPath = new SliderGraphic( d, this );

        return pPath;
    }

    private BufferedImage loadZoomInImage() throws IOException {
        return BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "the-ramp/images/icons/glass-20-plus.gif" ), zoomButtonHeight );
    }

    private BufferedImage loadZoomOutImage() throws IOException {
        return BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "the-ramp/images/icons/glass-20-minus.gif" ), zoomButtonHeight );
    }


    private double getMinRangeValue() {
        return plot.getRangeAxis().getLowerBound();
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
        plot.addDomainMarker( new ValueMarker( 5, Color.lightGray, new BasicStroke( 1 ) ) );
        plot.addDomainMarker( new ValueMarker( 10, Color.lightGray, new BasicStroke( 1 ) ) );
        plot.addDomainMarker( new ValueMarker( 15, Color.lightGray, new BasicStroke( 1 ) ) );
        plot.addDomainMarker( new ValueMarker( 20, Color.lightGray, new BasicStroke( 1 ) ) );
        plot.addDomainMarker( new ValueMarker( 25, Color.lightGray, new BasicStroke( 1 ) ) );
        for( double y = defaultMaxY / 4; y < plot.getRangeAxis().getRange().getUpperBound(); y += defaultMaxY / 4 ) {
            plot.addRangeMarker( new ValueMarker( y, Color.lightGray, new BasicStroke( 1 ) ) );
        }
        for( double y = -defaultMaxY / 4; y > plot.getRangeAxis().getRange().getLowerBound(); y -= defaultMaxY / 4 ) {
            plot.addRangeMarker( new ValueMarker( y, Color.lightGray, new BasicStroke( 1 ) ) );
        }
    }

    private void updateCursorLocation() {
        double time = timeSeriesModel.getPlaybackTime();
        Point2D imageLoc = toImageLocation( time, 0 );
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
        setHasChild( !minimized, minButNode );
        setHasChild( !minimized, zoomInGraphic );
        setHasChild( !minimized, zoomOutGraphic );
        if( slider != null ) {
            setHasChild( !minimized, slider );
        }

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

    protected void paint( PPaintContext paintContext ) {
//        System.out.println( "paintContext.getScale() = " + paintContext.getScale() );
//        paintContext.getGraphics().scale( 1.0/paintContext.getScale(),1.0/paintContext.getScale());
        super.paint( paintContext );
    }

    private void hideCursor() {
        cursorPNode.setVisible( false );
    }

    public void setChartSize( int chartWidth, int chartHeight ) {

        if( !( chartWidth > 0 && chartHeight > 0 ) ) {
            throw new RuntimeException( "Illegal chart dimensions: " + chartWidth + ", " + chartHeight );
        }
        if( this.chartWidth != chartWidth || this.chartHeight != chartHeight ) {
            this.dataAreaDirty = true;
            this.layoutDirty = true;
            this.chartWidth = chartWidth;
            this.chartHeight = chartHeight;
            repaintAll();
            updateCursor();
            invalidateLayout();
        }
    }

    protected void layoutChildren() {
        if( layoutDirty ) {
            super.layoutChildren();
            dataAreaDirty = true;
            for( int i = 0; i < series.size() / 2; i++ ) {
                TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
                PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
                double readoutDY = -2;
                readoutGraphic.setOffset( getDataArea().getX() + 5,
                                          getDataArea().getY() + 4 + ( readoutGraphic.getFullBounds().getHeight() + readoutDY ) * i );
            }

            for( int i = series.size() / 2; i < series.size(); i++ ) {
                TimeSeriesPNode timeSeriesPNode = (TimeSeriesPNode)series.get( i );
                PNode readoutGraphic = timeSeriesPNode.getReadoutGraphic();
                double readoutDY = -2;
                int index = i - series.size() / 2;
                readoutGraphic.setOffset( getDataArea().getX() + getDataArea().getWidth() / 2 + 5,
                                          getDataArea().getY() + 4 + ( readoutGraphic.getFullBounds().getHeight() + readoutDY ) * index );
            }

            zoomInGraphic.setOffset( 5 + getDataArea().getX(), getDataArea().getMaxY() - zoomOutGraphic.getFullBounds().getHeight() - zoomInGraphic.getFullBounds().getHeight() - 2 );
            zoomOutGraphic.setOffset( zoomInGraphic.getOffset().getX(), zoomInGraphic.getOffset().getY() + zoomInGraphic.getFullBounds().getHeight() );

//        System.out.println( System.currentTimeMillis() + ", Layout Children" );
            layoutCount++;
            if( layoutCount > 100 ) {
                System.out.println( "layoutCount = " + layoutCount );
            }
            if( slider != null ) {
                slider.dataAreaChanged( getDataArea() );
            }
            minButNode.setOffset( getDataArea().getMaxX() - minButNode.getFullBounds().getWidth() - 2, 0 + 2 );
            layoutDirty = false;
        }
    }

    private void updateCursor() {
        updateCursorSize();
        updateCursorLocation();
    }

    private void updateCursorSize() {
        Rectangle2D d = getDataArea();
        int cursorWidth = 6;
        cursorPNode.setPathTo( new Rectangle2D.Double( -cursorWidth / 2, -d.getHeight() / 2, cursorWidth, d.getHeight() ) );
    }

    private void updateChartBuffer() {
        this.layoutDirty = true;
        updateGridlines();
        if( chartWidth < 2000 && chartHeight < 2000 ) {
            bufferedImage = chart.createBufferedImage( chartWidth, chartHeight );
//            System.out.println( "TimePlotSuitePNode.updateChartBuffer@" + System.currentTimeMillis() );
            decorateBuffer();
            chartGraphic.setImage( bufferedImage );
        }
        else {
//            System.out.println( "chartWidth = " + chartWidth );
//            System.out.println( "chartHeight = " + chartHeight );
        }
    }

    private void decorateBuffer() {
        drawInPlotAxis();
    }

    private void drawInPlotAxis() {
        Graphics2D g2 = bufferedImage.createGraphics();
        for( int t = 2; t < RampModule.MAX_TIME; t += 2 ) {
            Point2D imagLoc = toImageLocation( t, 0 );
            PText text = new PText( "" + t );
            text.setOffset( imagLoc.getX() - text.getWidth() / 2, imagLoc.getY() );
            text.setFont( new PhetFont( 10 ) );
            text.fullPaint( new PPaintContext( g2 ) );
        }
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createXYLineChart( "",
                                                           "", // x-axis label
                                                           "", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, false, false, false );

        chart.setBackgroundPaint( EarthGraphic.earthGreen );

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
        yAxis.setLabelFont( new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 11 ) );
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
        return getDataArea( plot );
    }

    private Rectangle2D getDataArea( XYPlot plot ) {
        if( savedDataArea == null || dataAreaDirty ) {
            BufferedImage image = new BufferedImage( 2, 2, BufferedImage.TYPE_INT_RGB );
            ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();
            chart.draw( image.createGraphics(), new Rectangle2D.Double( 0, 0, chartWidth, chartHeight ), chartRenderingInfo );
            Rectangle2D r = chartRenderingInfo.getPlotInfo().getDataArea();
            savedDataArea = new Rectangle2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight() );
            dataAreaDirty = false;
        }
        return savedDataArea;
    }

    public LinearTransform2D toLinearFunction() {
        Point2D modelPoint0 = new Point2D.Double( 0, 0 );
        Point2D modelPoint1 = new Point2D.Double( 1, 1 );
        Point2D viewPt1 = toImageLocation( modelPoint0.getX(), modelPoint0.getY() );
        Point2D viewPt2 = toImageLocation( modelPoint1.getX(), modelPoint1.getY() );
        return new LinearTransform2D( modelPoint0, modelPoint1, viewPt1, viewPt2 );
    }

    public Point2D toImageLocation( double x, double y ) {
        Rectangle2D dataArea = getDataArea( plot );
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
