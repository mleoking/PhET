/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.HorizontalCursor;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.movingman.MMTimer;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.ValueGraphic;
import edu.colorado.phet.movingman.common.ObservingGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MMPlot implements ObservingGraphic {
    private String title;
    private MovingManModule module;
    private DataSeries dataSeries;
    private MMTimer timer;
    private Color color;
    private Stroke stroke;
    private BufferedGraphicForComponent buffer;
    private double xShift;

    private boolean visible = true;
    private Chart chart;
    private DataSet dataSet;
    private float lastTime;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 14 );
    private Font titleFont = new Font( "Lucida Sans", Font.BOLD, 18 );
    private VerticalChartSlider verticalChartSlider;
    private HorizontalCursor horizontalCursor;
    private GeneralPath path = new GeneralPath();
    private CloseButton closeButton;
    private ValueGraphic valueGraphic;

    public MMPlot( String title, final MovingManModule module, DataSeries dataSeries, MMTimer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, BufferedGraphicForComponent buffer, double xShift ) throws IOException {
        this.title = title;
        this.module = module;
        this.dataSeries = dataSeries;
        this.timer = timer;
        this.color = color;
        this.stroke = stroke;
        this.buffer = buffer;
        this.xShift = xShift;
        chart = new Chart( module.getApparatusPanel(), new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
        horizontalCursor = new HorizontalCursor( chart, new Color( 15, 0, 255, 50 ), new Color( 50, 0, 255, 150 ), 8 );
        module.getApparatusPanel().addGraphic( horizontalCursor, 1000 );

        chart.setBackground( createBackground() );
        dataSet = new DataSet();
        setInputRange( inputBox );
        timer.addObserver( this );
        chart.getHorizontalTicks().setVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getXAxis().setMajorTickFont( font );
        chart.getYAxis().setMajorTicksVisible( false );
        chart.getYAxis().setMajorTickFont( font );
        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );

        verticalChartSlider = new VerticalChartSlider( chart );
        chart.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
        horizontalCursor.addListener( new HorizontalCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                module.cursorMovedToTime( modelX );
            }
        } );
        closeButton = new CloseButton();
        module.getApparatusPanel().add( closeButton );
        chart.setVerticalTitle( title, color, titleFont );
    }

    private Paint createBackground() {
        return Color.yellow;
//        Rectangle rect = chart.getViewBounds();
//        Color topleft = Color.yellow;
//        System.out.println( "topleft = " + topleft );
////        Color bottomRight=new Color( 1.0f,.8f,.6f);
////        Color bottomRight=new Color( 255,255,250);
////        Color bottomRight=new Color( 60,90,250);
//        Paint g = new GradientPaint( rect.x, rect.y, topleft, rect.x + rect.width, rect.y + rect.height, bottomRight );
//
//        return g;
    }

    public void setCloseHandler( ActionListener actionListener ) {
        closeButton.addActionListener( actionListener );
    }

    public MovingManModule getModule() {
        return module;
    }

    public void setValueGraphic( ValueGraphic text ) {
        this.valueGraphic = text;
    }

    private static class CloseButton extends JButton {
        private static Icon icon;

        public CloseButton() throws IOException {
            super( loadIcon() );
        }

        public static Icon loadIcon() throws IOException {
            if( icon == null ) {
//                Image image = ImageIO.read( CloseButton.class.getClassLoader().getResource( "images/x-25.png" ) );
                Image image = ImageLoader.loadBufferedImage( "images/x-25.png" );
                icon = new ImageIcon( image );
            }
            return icon;
        }

        public void setPosition( int x, int y ) {
            reshape( x, y, getPreferredSize().width, getPreferredSize().height );
        }
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            GraphicsState state = new GraphicsState( g );
            chart.paint( g );
            Point pt = chart.getTransform().modelToView( 15, 0 );
            pt.y -= 3;
            PhetTextGraphic ptt = new PhetTextGraphic( module.getApparatusPanel(), new Font( "Lucida Sans", Font.BOLD, 16 ), "Time", Color.red, pt.x, pt.y );
            ptt.paint( g );
            Rectangle bounds = ptt.getBounds();
            Point2D tail = RectangleUtils.getRightCenter( bounds );
            tail = new Point2D.Double( tail.getX() + 5, tail.getY() );
            Point2D tip = new Point2D.Double( tail.getX() + 30, tail.getY() );
            Arrow arrow = new Arrow( tail, tip, 9, 9, 5 );
            PhetShapeGraphic psg = new PhetShapeGraphic( module.getApparatusPanel(), arrow.getShape(), Color.red, new BasicStroke( 1 ), Color.black );
            psg.paint( g );

            g.setClip( chart.getViewBounds() );
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( path );

            state.restoreGraphics();
        }
    }

    public double getxShift() {
        return xShift;
    }

    public ModelViewTransform2D getTransform() {
        return chart.getTransform();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        setSliderVisible( visible );
        if( !visible ) {
            setCursorVisible( visible );
        }
        closeButton.setVisible( visible );
    }

    public void setShift( double xShift ) {
        this.xShift = xShift;
    }

    public void setInputRange( Rectangle2D.Double inputBox ) {
        Range2D range = new Range2D( inputBox );
        chart.setRange( range );
    }

    public void setPaintYLines( double[] lines ) {
        double[] full = new double[lines.length * 2 + 1];
        for( int i = 0; i < lines.length; i++ ) {
            full[i] = lines[i];
            full[full.length - 1 - i] = -lines[i];
        }
        full[lines.length] = 0;

        double[] half = new double[lines.length * 2];
        for( int i = 0; i < lines.length; i++ ) {
            half[i] = lines[i];
            half[half.length - 1 - i] = -lines[i];
        }
        chart.getHorizonalGridlines().setMajorGridlines( half );
        chart.getVerticalTicks().setMajorGridlines( full );
        chart.getYAxis().setMajorGridlines( full );
    }

    public void setViewBounds( Rectangle rectangle ) {
        chart.setViewBounds( rectangle );
        chart.setBackground( createBackground() );
        verticalChartSlider.update();
        chart.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
        Rectangle vb = chart.getViewBounds();
        int x = vb.x + vb.width - closeButton.getPreferredSize().width;
        int y = vb.y;
//        System.out.println( "x = " + x + ", y=" + y );
        closeButton.setPosition( x - 2, y + 2 );
    }

    public void update( Observable o, Object arg ) {
        float time = (float)timer.getTime();
        if( time == lastTime ) {
            return;
        }
        lastTime = time;
        if( dataSeries.size() <= 1 ) {
            dataSet.clear();
        }
        else {
            float position = (float)dataSeries.getLastPoint();// * scale + yoffset;
            if( Float.isInfinite( position ) ) {
                return;
            }
            Point2D.Double pt = new Point2D.Double( time - xShift, position );
            dataSet.addPoint( pt );
            if( visible && buffer.getImage() != null && dataSet.size() >= 2 ) {
                int size = dataSet.size();
                Point2D a = chart.getTransform().modelToView( dataSet.pointAt( size - 2 ) );
                Point2D b = chart.getTransform().modelToView( dataSet.pointAt( size - 1 ) );
                Line2D.Double line = new Line2D.Double( a, b );
                if( dataSet.size() == 2 ) {
                    path.reset();
                    path.moveTo( (int)a.getX(), (float)a.getY() );
                }
                path.lineTo( (float)b.getX(), (float)b.getY() );
                Graphics2D g2 = module.getBackground().getImage().createGraphics();
                g2.setStroke( new BasicStroke( 2 ) );
                g2.setColor( color );
                g2.setClip( chart.getViewBounds() );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                g2.draw( line );
                Shape shape = stroke.createStrokedShape( line );
                module.getApparatusPanel().repaint( shape.getBounds() );
            }
        }
    }

    public void setSliderVisible( boolean b ) {
        verticalChartSlider.setVisible( b );
    }

    public void addSliderListener( VerticalChartSlider.Listener listener ) {
        verticalChartSlider.addListener( listener );
    }

    public void updateSlider() {
        JSlider js = verticalChartSlider.getSlider();
        if( !js.hasFocus() && dataSet.size() > 0 ) {
            double lastY = dataSet.getLastPoint().getY();
            verticalChartSlider.setValue( lastY );
        }
    }

    public void cursorMovedToTime( double time ) {
        horizontalCursor.setX( time );
        verticalChartSlider.setValue( valueGraphic.getValue() );
    }

    public void setCursorVisible( boolean visible ) {
        horizontalCursor.setVisible( visible );
    }
}
